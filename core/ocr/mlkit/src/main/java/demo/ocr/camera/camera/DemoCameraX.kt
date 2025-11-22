package demo.ocr.camera.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import android.util.AttributeSet
import android.util.Rational
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.FrameLayout
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import demo.ocr.camera.interfaces.CameraRepository
import demo.ocr.camera.utils.CameraUtils.isLowSpecDevice
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.core.view.doOnLayout
import androidx.lifecycle.DefaultLifecycleObserver
import demo.ocr.camera.R
import demo.ocr.camera.interfaces.CropRectangleProvider
import demo.ocr.camera.interfaces.ImageCaptureListener
import demo.ocr.camera.interfaces.ImageFrameListener
import demo.ocr.camera.utils.CameraUtils.isLandscape
import demo.ocr.camera.utils.CameraUtils.toAbsoluteRectInParent
import demo.ocr.camera.utils.ImageMetaData
import demo.ocr.camera.utils.ImageProcessingGate
import demo.ocr.camera.utils.SimpleBitmapPool
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class DemoCameraX @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    var cropRectangleProvider: CropRectangleProvider? = null
) : FrameLayout(context, attrs, defStyleAttr), CameraRepository {

    private var cameraLifecycleOwner: LifecycleOwner? = null
    private val mPreviewView: PreviewView = PreviewView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
    private var mPreview: Preview? = null

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private var mResolution: Size = Size(1920, 1080)

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var imageAnalysis: ImageAnalysis? = null
    private var imageCapture: ImageCapture? = null
    private var pendingRoiBitmapDeferred: CompletableDeferred<Pair<Bitmap?, ImageMetaData?>>? = null

    var cameraScope: CoroutineScope? = null
    private var observeJob: Job? = null

    var isAutoDetectMode = true

    private var mOverlayBitmap: Bitmap? = null
    private var mRegisteredListener: ImageFrameListener? = null

    private var isLensFocused = false
    var lastTotalCaptureResult: TotalCaptureResult? = null

    val mImageProcessingGate = ImageProcessingGate()

    private var mFocalLengthMm: Float? = null // 카메라 초점 거리
    private var sensorWidthMm: Float? = null // 카메라 센서의 물리적 크기 w
    private var sensorHeightMm: Float? = null // 카메라 센서의 물리적 크기 h
    private var mLastImageProxySize = Size(0, 0)
    private var reusableOriginBitmap: Bitmap? = null
    private val bmpPool = SimpleBitmapPool()

    private val orientationListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            val rot = when {
                orientation in 45..134 -> Surface.ROTATION_270
                orientation in 135..224 -> Surface.ROTATION_180
                orientation in 225..314 -> Surface.ROTATION_90
                else -> Surface.ROTATION_0
            }

            if (context.isLandscape()) {
                if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270) {
                    if (rot != imageAnalysis?.targetRotation || rot != imageAnalysis?.targetRotation) {
                        imageCapture?.targetRotation = rot
                        imageAnalysis?.targetRotation = rot
                        mCropRoiRect = null
                    }
                }
            }
        }
    }

    val overlayPaint = Paint().apply {
        color = "#88000000".toColorInt()
        style = Paint.Style.FILL
    }

    @Volatile
    private var mCropRoiRect: Rect? = null

    init {
        addView(mPreviewView)

        setWillNotDraw(false)

        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.DemoCameraX) {
                val cropViewResId = getResourceId(R.styleable.DemoCameraX_cropViewId, NO_ID)
                post {
                    val cropView = (parent as View).findViewById<View>(cropViewResId)
                    if (cropView is CropRectangleProvider) {
                        cropRectangleProvider = cropView
                        cropRectangleProvider?.setRoiChangedCallback {
                            mCropRoiRect = null
                        }
                    } else {
                    }
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        cropRectangleProvider?.let { cropRectangleProvider ->
            // 실제 좌표 계산
            val calculatedRect = cropRectangleProvider.getCropRect()
                .toAbsoluteRectInParent(cropRectangleProvider.getViewRect())
            // 상단 영역
            canvas.drawRect(0f, 0f, width.toFloat(), calculatedRect.top, overlayPaint)
            // 하단 영역
            canvas.drawRect(
                0f,
                calculatedRect.bottom,
                width.toFloat(),
                height.toFloat(),
                overlayPaint
            )
            // 왼쪽 영역 (중앙 크롭 영역의 높이만큼)
            canvas.drawRect(
                0f,
                calculatedRect.top,
                calculatedRect.left,
                calculatedRect.bottom,
                overlayPaint
            )
            // 오른쪽 영역 (중앙 크롭 영역의 높이만큼)
            canvas.drawRect(
                calculatedRect.right,
                calculatedRect.top,
                width.toFloat(),
                calculatedRect.bottom,
                overlayPaint
            )
            // image draw
//            mOverlayBitmap?.let {
//                val padding = (it.width * 0.025f).toInt()
//                val srcRect = Rect(padding, 0, it.width - padding, it.height)
//                canvas.drawBitmap(it, srcRect, calculatedRect, null)
//            }
        }
    }

    private fun setupCamera() {
        startCamera()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        mPreviewView.doOnLayout {
            val scaleX = 1.025f
            val scaleY = 1.0f
            mPreviewView.scaleX = scaleX
            mPreviewView.scaleY = scaleY

            mPreviewView.pivotX = mPreviewView.width / 2f
            mPreviewView.pivotY = mPreviewView.height / 2f

            mPreviewView.translationX = 0f
        }
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            if (mPreviewView.width == 0 || mPreviewView.height == 0) {
                mPreviewView.post { bindCameraUseCases(mPreviewView) }
            } else {
                bindCameraUseCases(mPreviewView)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases(previewView: PreviewView) {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        if (previewView.display == null) {
        }

        val previewRotation = previewView.display?.rotation ?: Surface.ROTATION_0

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val rotateResolution: Size = mResolution


        val resolutionSelector = ResolutionSelector.Builder()
            .setAllowedResolutionMode(ResolutionSelector.PREFER_HIGHER_RESOLUTION_OVER_CAPTURE_RATE)
            .setAspectRatioStrategy(
                AspectRatioStrategy(
                    AspectRatio.RATIO_16_9,
                    AspectRatioStrategy.FALLBACK_RULE_AUTO
                )
            )
            .setResolutionStrategy(
                ResolutionStrategy(
                    mResolution,
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
            .build()

        val preview = Preview.Builder().setResolutionSelector(resolutionSelector)
            .setTargetRotation(previewRotation).build()
        mPreview = preview

        previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE

        previewView.scaleType = PreviewView.ScaleType.FIT_CENTER

        cameraLifecycleOwner?.let {
            previewView.previewStreamState.observe(it) { state ->
                if (state == PreviewView.StreamState.STREAMING) {
                    val info = preview.resolutionInfo ?: return@observe
                    val rot = ((info.rotationDegrees % 360) + 360) % 360

                    val srcCropW = info.cropRect.width()
                    val srcCropH = info.cropRect.height()
                    // 회전 보정 (뷰 좌표계 방향과 맞추기)
                    val (srcW, srcH) = if (rot % 180 == 0) srcCropW to srcCropH else srcCropH to srcCropW

                    val dstW = previewView.width
                    val dstH = previewView.height

                    val content = visibleContentRectFitCenter(srcW, srcH, dstW, dstH)
                    cropRectangleProvider?.let { cropView ->
                        if (content.width() < cropView.getViewRect().width()) {
                            cropView.setSize(
                                content.width().toInt(),
                                (content.width() / 1.33f).toInt()
                            )
                        }
                        if (content.height() < cropView.getViewRect().height()) {
                            cropView.setSize(
                                (content.height() * 1.33f).toInt(),
                                content.height().toInt()
                            )
                        }
                        post { invalidate() }
                    }
                }
            }
        }


        // 양옆 빈 공간 방지
        clipChildren = true
        clipToPadding = true

        preview.surfaceProvider = previewView.surfaceProvider

        val imageAnalysisBuilder =
            ImageAnalysis.Builder().setResolutionSelector(resolutionSelector)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(previewRotation).setImageQueueDepth(1)
                .setOutputImageRotationEnabled(true)
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)

        if (!isLowSpecDevice(context)) {
            Camera2Interop.Extender(imageAnalysisBuilder).setCaptureRequestOption(
                CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON
            ).setCaptureRequestOption(
                CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO
            ).setSessionCaptureCallback(object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    lastTotalCaptureResult = result
                }
            })
        }

        imageAnalysis = imageAnalysisBuilder.build()

        if (isAutoDetectMode) {
            setImageAnalyzer()
        }

        cameraProvider.unbindAll()

        val lifecycleOwnerToUse = cameraLifecycleOwner ?: run {
            return
        }

        try {
            imageCapture =
                ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .setResolutionSelector(resolutionSelector)
                    .setTargetRotation(previewRotation).build()

            val vpScale = when (previewView.scaleType) {
                PreviewView.ScaleType.FIT_CENTER -> ViewPort.FIT
                PreviewView.ScaleType.FILL_CENTER -> ViewPort.FILL_CENTER
                else -> ViewPort.FILL_CENTER
            }

            val viewPort = ViewPort.Builder(
                Rational(previewView.width, previewView.height),
                previewView.display?.rotation ?: Surface.ROTATION_0
            ).setScaleType(vpScale).build()

            val useCaseGroup = UseCaseGroup.Builder().addUseCase(preview).addUseCase(imageCapture!!)
                .addUseCase(imageAnalysis!!).setViewPort(viewPort).build()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwnerToUse, cameraSelector, useCaseGroup
            )
            readStaticCameraParams(camera!!.cameraInfo)

            cameraScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            cameraScope?.launch {

                launch {
//                    focusLoop()
                }
            }

            preview.resolutionInfo?.let {
            }

        } catch (exc: Exception) {
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPreviewView.requestLayout()
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.cameraLifecycleOwner = lifecycleOwner
        cameraLifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                resume()
            }

            override fun onPause(owner: LifecycleOwner) {
                pause()
            }
        })
    }

    suspend fun focusLoop() {
        withContext(Dispatchers.Main.immediate) {
            val factory = mPreviewView.meteringPointFactory
            withContext(Dispatchers.IO) {
                while (isActive) {
                    val centerX = mPreviewView.width / 2.0f
                    val centerY = mPreviewView.height / 2.0f
                    val centerPoint = factory.createPoint(centerX, centerY)

                    val action = FocusMeteringAction.Builder(
                        centerPoint,
                        FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE or FocusMeteringAction.FLAG_AWB
                    ).setAutoCancelDuration(2, TimeUnit.SECONDS).build()

                    try {
                        isLensFocused = false
                        isLensFocused = suspendCancellableCoroutine<Boolean> { continuation ->
                            val future = camera?.cameraControl?.startFocusAndMetering(action)

                            future?.addListener({
                                try {
                                    val result = future.get()
                                    continuation.resume(true)
                                } catch (e: Exception) {
                                    continuation.resumeWithException(e)
                                }
                            }, cameraExecutor)
                        }
                        if (isLensFocused) {
                            delay(4000L)
                        }
                    } catch (e: Exception) {
                        delay(1000)
                    }
                }
            }
        }
    }

    suspend fun captureImageToBitmap() {
        withContext(Dispatchers.Main.immediate) {
            val factory = mPreviewView.meteringPointFactory
            val centerPoint = factory.createPoint(
                mPreviewView.width / 2f, mPreviewView.height / 2f
            )

            withContext(Dispatchers.IO) {
                val action =
                    FocusMeteringAction.Builder(centerPoint, FocusMeteringAction.FLAG_AF).build()

                val future = camera?.cameraControl?.startFocusAndMetering(action)
                val captureExcutor = Executors.newSingleThreadExecutor { r ->
                    Thread(r, "capture").apply {
                        priority = Thread.MIN_PRIORITY
                    }
                }

                future?.addListener({
                    try {
                        val result = future.get()
                        // 초점 맞추기 성공 여부 확인
                        if (result.isFocusSuccessful) {
                            // 초점이 성공적으로 맞춰졌을 때만 사진 촬영
                            imageCapture?.takePicture(
                                cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                                    @SuppressLint("UnsafeOptInUsageError")
                                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                        val rotationDegrees = (imageProxy.imageInfo.rotationDegrees) % 360
                                        val (effW, effH) = getEffectiveSensorSizeMm()

                                        val roi: Rect =
                                            (mCropRoiRect ?: getOrientationRoi(imageProxy).also { mCropRoiRect = it })

                                        val imageMetaData = ImageMetaData(
                                            lensFocusDistance = getCurrentFocusDistanceMeters(),
                                            focalLengthMm = mFocalLengthMm,
                                            sensorSizeW = effW,
                                            sensorSizeH = effH,
                                            imageProxySize = Size(imageProxy.width, imageProxy.height),
                                            roiInProxy = roi,
                                        )

                                        val croppedBitmap = getCroppedRoiBitmap(imageProxy)
                                        val finalBitmap = if (isAutoDetectMode) {
                                            croppedBitmap
                                        } else {
                                            croppedBitmap?.let {
                                                it.config?.let { config ->
                                                    it.copy(
                                                        config,
                                                        true
                                                    )
                                                }
                                            }
                                        }

                                        pendingRoiBitmapDeferred?.complete(Pair(finalBitmap, imageMetaData))
                                        imageProxy.close()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        val msg = "Image capture failed: ${exception.message}"
                                    }
                                })
                        } else {
                        }
                    } catch (e: Exception) {
                    }
                }, captureExcutor)
            }
        }
    }

    fun setImageAnalyzer() {
        val analysisExecutor =
            if (isLowSpecDevice(context)) Executors.newSingleThreadExecutor { r ->
                Thread(r, "analysis").apply {
                    priority = Thread.MIN_PRIORITY
                }
            }
            else cameraExecutor

        imageAnalysis?.setAnalyzer(analysisExecutor) { imageProxy ->
            if (mImageProcessingGate.canPublishAndAcquire() && isGoodFrame()) {
                mImageProcessingGate.signalProcessingStart()

                val croppedBitmap = getCroppedRoiBitmap(imageProxy)

                val imageMetaData = getImageMetaData(imageProxy)
                mRegisteredListener?.onImageAvailable(
                    croppedBitmap,
                    imageMetaData,
                    mImageProcessingGate
                )
            }

            imageProxy.close()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getImageMetaData(imageProxy: ImageProxy): ImageMetaData {
        mLastImageProxySize = Size(imageProxy.width, imageProxy.height)
        val (effW, effH) = getEffectiveSensorSizeMm()

        // onCaptureCompleted 또는 analysis 프레임에서
        val res = lastTotalCaptureResult

        val cropRegion =
            res?.get(CaptureResult.SCALER_CROP_REGION) // 센서 active array에서 중앙 크롭 영역(px)
        val zoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio // CameraX 제공 (있으면 사용)

        // 정적 특성
        val c2Info = Camera2CameraInfo.from(camera!!.cameraInfo)
        val activeArray =
            c2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE) // px

        return ImageMetaData(
            lensFocusDistance = getCurrentFocusDistanceMeters(),
            focalLengthMm = mFocalLengthMm,
            sensorSizeW = effW,
            sensorSizeH = effH,
            imageProxySize = Size(imageProxy.width, imageProxy.height),
            roiInProxy = mCropRoiRect,
            zoomRatio = zoomRatio,
            activeArray = activeArray,
            cropRegion = cropRegion
        )
    }

    fun isGoodFrame(): Boolean {
        return lastTotalCaptureResult?.let {
            val afState = it.get(CaptureResult.CONTROL_AF_STATE)
            val aeState = it.get(CaptureResult.CONTROL_AE_STATE)
            val awbState = it.get(CaptureResult.CONTROL_AWB_STATE)

            // 이미지 분석에 유효한 상태인지 판단
            (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || afState == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED || afState == null) && (aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED || aeState == null) && (awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED || awbState == null)
        } ?: true
    }

    private fun getOrientationRoi(imageProxy: ImageProxy): Rect {
        val rot = ((imageProxy.imageInfo.rotationDegrees % 360) + 360) % 360
        val srcW0 = imageProxy.width
        val srcH0 = imageProxy.height
        val dstW = mPreviewView.width
        val dstH = mPreviewView.height

        val wRot = if (rot % 180 == 0) srcW0 else srcH0
        val hRot = if (rot % 180 == 0) srcH0 else srcW0

        val cropView = cropRectangleProvider ?: return Rect()
        val cropViewRectInView = cropView.getCropRect()
            .toAbsoluteRectInParent(cropView.getViewRect())

        val x1v = cropViewRectInView.left
        val y1v = cropViewRectInView.top
        val x2v = cropViewRectInView.right
        val y2v = cropViewRectInView.bottom


        val toUprime: (Float) -> Float
        val toVprime: (Float) -> Float
        val sUsed: Float

        if (false) {
            // FILL_CENTER: s = max, 중앙 크롭 오프셋(u0, v0) 적용
            val sx = dstW.toFloat() / wRot
            val sy = dstH.toFloat() / hRot
            val s = maxOf(sx, sy)
            sUsed = s
            val cropW = dstW / s
            val cropH = dstH / s
            val u0 = (wRot - cropW) / 2f
            val v0 = (hRot - cropH) / 2f
            toUprime = { xv -> u0 + xv / s }
            toVprime = { yv -> v0 + yv / s }
        } else {
            // FIT_CENTER: s = min, 패딩(leftPad/topPad) 제거 후 /s
            val sx = dstW.toFloat() / wRot
            val sy = dstH.toFloat() / hRot
            val s = minOf(sx, sy)
            sUsed = s
            val leftPad = (dstW - wRot * s) / 2f
            val topPad = (dstH - hRot * s) / 2f
            toUprime = { xv -> (xv - leftPad) / s }
            toVprime = { yv -> (yv - topPad) / s }
        }

        var u1p = toUprime(x1v);
        var v1p = toVprime(y1v)
        var u2p = toUprime(x2v);
        var v2p = toVprime(y2v)

        // (옵션) 가로 방향 여유 패딩 – 반드시 sUsed로 계산
        val padU = (dstW * 0.025f) / sUsed
        u1p -= padU
        u2p += padU

        // 회전된 버퍼 경계로 클램프
        u1p = u1p.coerceIn(0f, wRot.toFloat()); u2p = u2p.coerceIn(0f, wRot.toFloat())
        v1p = v1p.coerceIn(0f, hRot.toFloat()); v2p = v2p.coerceIn(0f, hRot.toFloat())

        // 역회전
        val srcW = srcW0.toFloat()
        val srcH = srcH0.toFloat()
        fun backToSrc(uP: Float, vP: Float): Pair<Float, Float> = when (rot) {
            0 -> uP to vP
            90 -> vP to (srcH - uP)
            180 -> (srcW - uP) to (srcH - vP)
            270 -> (srcW - vP) to uP
            else -> uP to vP
        }
        val (x1i, y1i) = backToSrc(u1p, v1p)
        val (x2i, y2i) = backToSrc(u2p, v2p)

        val left = floor(min(x1i, x2i)).toInt().coerceIn(0, srcW0)
        val top = floor(min(y1i, y2i)).toInt().coerceIn(0, srcH0)
        val right = ceil(max(x1i, x2i)).toInt().coerceIn(left, srcW0)
        val bottom = ceil(max(y1i, y2i)).toInt().coerceIn(top, srcH0)
        return Rect(left, top, right, bottom)
    }

    override fun setResolution(width: Int, height: Int) {
        mResolution = Size(width, height)
        bindCameraUseCases(mPreviewView)
    }

    private fun releaseCamera() {
        mCropRoiRect = null
        mRegisteredListener = null
        imageAnalysis?.clearAnalyzer()
        cancelPendingCapture()
        if (cameraScope?.isActive == true) {
            cameraScope?.cancel()
        }

        observeJob?.cancel()
        observeJob = null
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }

    override fun startCamera(
        lifecycleOwner: LifecycleOwner,
        isAutoMode: Boolean,
    ) {
        orientationListener.enable()
        setLifecycleOwner(lifecycleOwner)
        isAutoDetectMode = isAutoMode && !isLowSpecDevice(context)
        setupCamera()
    }

    override fun stopCamera() {
        orientationListener.disable()
        releaseCamera()
    }

    override fun registerImageListener(imageFrameListener: ImageFrameListener) {
        // 이미 리스너가 등록되어 있다면 중복 등록 방지
        mRegisteredListener = imageFrameListener
    }

    override fun captureImage(imageCaptureListener: ImageCaptureListener) {
        if (pendingRoiBitmapDeferred?.isActive == true) {
            imageCaptureListener.onImageAvailable(null, null)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                getCaptureCroppedImage().run {
                    imageCaptureListener.onImageAvailable(this?.first, this?.second)
                }
            }
        }
    }

    fun showOverlayBitmap(overlayBitmap: Bitmap?) {
        mOverlayBitmap = overlayBitmap
        invalidate()
    }

    suspend fun getCaptureCroppedImage(): Pair<Bitmap?, ImageMetaData?>? {
        pendingRoiBitmapDeferred = CompletableDeferred()
        withContext(Dispatchers.Main.immediate) {
            captureImageToBitmap()
        }

        var bitmapImageSource: Pair<Bitmap?, ImageMetaData?>?
        try {
            withTimeout(7000L) {
                bitmapImageSource = pendingRoiBitmapDeferred?.await()
            }
        } catch (e: Exception) {
            if (pendingRoiBitmapDeferred?.isActive == true) {
                pendingRoiBitmapDeferred?.completeExceptionally(e)
            }
            return null
        } finally {
            pendingRoiBitmapDeferred = null
        }

        return bitmapImageSource
    }

    override fun resume() {
        cameraProvider?.let {
            bindCameraUseCases(mPreviewView)
        } ?: run {
        }
    }

    override fun pause() {
        mCropRoiRect = null
        imageAnalysis?.clearAnalyzer()
        cameraProvider?.unbindAll()
        if (cameraScope?.isActive == true) {
            cameraScope?.cancel()
        }
        pendingRoiBitmapDeferred?.takeIf { it.isActive }
            ?.complete(Pair<Bitmap?, ImageMetaData?>(null, null))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun readStaticCameraParams(cameraInfo: CameraInfo) {
        val c2Info = Camera2CameraInfo.from(cameraInfo)

        // 초점거리(mm)
        val focals =
            c2Info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
        mFocalLengthMm = focals?.firstOrNull()

        // 센서 물리 크기(mm)
        val sensorSize =
            c2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
        sensorWidthMm = sensorSize?.width
        sensorHeightMm = sensorSize?.height
    }

    fun getCurrentFocusDistanceMeters(): Float? {
        val diopter = lastTotalCaptureResult?.get(CaptureResult.LENS_FOCUS_DISTANCE)
        return diopter?.let { if (it > 0f) 1f / it else null }.apply {
//            debug("getCurrentFocusDistanceMeters : $this")
        }
    }

    fun getEffectiveSensorSizeMm(): Pair<Float?, Float?> {
        val zoom = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
        val w = sensorWidthMm?.let { it / zoom }
        val h = sensorHeightMm?.let { it / zoom }
        return w to h
    }

    fun visibleContentRectFitCenter(srcW: Int, srcH: Int, dstW: Int, dstH: Int): RectF {
        if (srcW <= 0 || srcH <= 0 || dstW <= 0 || dstH <= 0) return RectF(0f, 0f, 0f, 0f)

        val scale = minOf(dstW.toFloat() / srcW, dstH.toFloat() / srcH)
        val drawW = srcW * scale
        val drawH = srcH * scale
        val left = (dstW - drawW) * 0.5f
        val top = (dstH - drawH) * 0.5f
        return RectF(left, top, left + drawW, top + drawH)
    }

    private fun cancelPendingCapture() {
        pendingRoiBitmapDeferred?.let {
            if (it.isActive) {
                it.complete(Pair<Bitmap?, ImageMetaData?>(null, null))
            }
        }
        pendingRoiBitmapDeferred = null
    }

    private fun getCroppedRoiBitmap(imageProxy: ImageProxy): Bitmap? {
        return cropRectangleProvider?.let {

            reusableOriginBitmap = reusableOriginBitmap?.run {
                if (width == imageProxy.width && height == imageProxy.height) this else createBitmap(
                    imageProxy.width,
                    imageProxy.height
                )
            } ?: createBitmap(imageProxy.width, imageProxy.height)

            imageProxy.use { reusableOriginBitmap = imageProxy.toBitmap() }
            reusableOriginBitmap?.let {
                cropBitmap(it, imageProxy)
            }
        }
    }

    private fun cropBitmap(bitmap: Bitmap, imageProxy: ImageProxy): Bitmap {
        val roi: Rect = (mCropRoiRect ?: getOrientationRoi(imageProxy).also {
            mCropRoiRect = it
        })
        return cropAndRotateIntoPool(bitmap, roi)
    }

    // 2) 새로 할당하지 않는 crop+rotate 구현
    fun cropAndRotateIntoPool(
        src: Bitmap,
        roi: Rect,
    ): Bitmap {
        // ROI 정규화
        val x1 = minOf(roi.left, roi.right)
        val y1 = minOf(roi.top, roi.bottom)
        val x2 = maxOf(roi.left, roi.right).coerceAtMost(src.width)
        val y2 = maxOf(roi.top, roi.bottom).coerceAtMost(src.height)
        val cx = x1.coerceAtLeast(0)
        val cy = y1.coerceAtLeast(0)
        val cw = (x2 - cx).coerceAtLeast(1)
        val ch = (y2 - cy).coerceAtLeast(1)

        // 1) 잘라서 crop 버퍼에 그리기(새 Bitmap 생성 X)
        val crop = bmpPool.obtainCrop(cw, ch)
        Canvas(crop).drawBitmap(
            src,
            Rect(cx, cy, cx + cw, cy + ch),
            Rect(0, 0, cw, ch),
            null
        )

        return crop
    }
}