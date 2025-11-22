package demo.ocr.camera.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.os.Build
import androidx.camera.core.ImageProxy
import androidx.core.graphics.createBitmap
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

object CameraUtils {
    private var recycledBitmap: Bitmap? = null
    private var outPixels: IntArray? = null

    @JvmStatic
    fun isLowSpecDevice(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // 예시: 2GB(2 * 1024 * 1024 * 1024 바이트)보다 작은 메모리를 가진 기기를 저사양으로 간주
        val thresholdMemory = 2L * 1024 * 1024 * 1024 // 2GB
//        return memoryInfo.totalMem < thresholdMemory
        return false
    }

    private fun Image.toRecycledBitmapRotFromRect(
        rotationDeg: Int, cropRect: Rect, alignChromaEven: Boolean = false
    ): Bitmap {
        require(format == ImageFormat.YUV_420_888)

        val rotateCropRect = cropRect.let {
            if (rotationDeg == 90 || rotationDeg == 270) {
                it
            } else {
//                Rect(it.top, it.left, it.bottom, it.right)
                it
            }
        }

        val srcW = width
        val srcH = height
        val rot = ((rotationDeg % 360) + 360) % 360

        // 0) crop rect 클램프(+선택적 짝수 정렬)
        var l = rotateCropRect.left.coerceIn(0, srcW)
        var t = rotateCropRect.top.coerceIn(0, srcH)
        var r = rotateCropRect.right.coerceIn(0, srcW)
        var b = rotateCropRect.bottom.coerceIn(0, srcH)

        if (alignChromaEven) {
            // 4:2:0 크로마 블록 경계에 맞추면 UV 샘플링이 안정적
            l = l and -2
            t = t and -2
            if (((r - l) and 1) != 0) r -= 1
            if (((b - t) and 1) != 0) b -= 1
        }

        val cw = (r - l).coerceAtLeast(1)
        val ch = (b - t).coerceAtLeast(1)

        // 1) 회전 후 출력 크기
        val swap = rot == 90 || rot == 270

        val outW = if (swap) ch else cw
        val outH = if (swap) cw else ch


        // 2) 비트맵/버퍼 재사용
        if (recycledBitmap == null || recycledBitmap?.width != outW || recycledBitmap?.height != outH) {
            recycledBitmap = createBitmap(outW, outH)
            outPixels = IntArray(outW * outH)
        }
        val out = outPixels!!

        // 3) 평면/스트라이드
        val yP = planes[0]
        val uP = planes[1]
        val vP = planes[2]
        val yBuf = yP.buffer;
        val uBuf = uP.buffer;
        val vBuf = vP.buffer
        val yRS = yP.rowStride;
        val yPS = yP.pixelStride
        val uRS = uP.rowStride;
        val uPS = uP.pixelStride
        val vRS = vP.rowStride;
        val vPS = vP.pixelStride

        fun clamp8(x: Int) = if (x < 0) 0 else if (x > 255) 255 else x
        fun getU8(buf: ByteBuffer, idx: Int) = buf.get(idx).toInt() and 0xFF

        // 4) (dx,dy) [출력] -> (x',y') [크롭 내부 좌표] -> (sx,sy) [원본]
        for (dy in 0 until outH) {
            val rowBase = dy * outW
            when (rot) {
                0 -> {
                    val yPrime = dy
                    val sy = t + yPrime
                    val yRowBase = sy * yRS
                    val uvRow = (sy shr 1)
                    val uRowBase = uvRow * uRS
                    val vRowBase = uvRow * vRS
                    val xStart = l
                    for (dx in 0 until outW) {
                        val sx = xStart + dx
                        val yIndex = yRowBase + sx * yPS
                        var Y = getU8(yBuf, yIndex) - 16
                        if (Y < 0) Y = 0
                        val uvCol = (sx shr 1)
                        val U = getU8(uBuf, uRowBase + uvCol * uPS) - 128
                        val V = getU8(vBuf, vRowBase + uvCol * vPS) - 128
                        val c = 298 * Y
                        out[rowBase + dx] =
                            (0xFF shl 24) or (clamp8((c + 409 * V + 128) shr 8) shl 16) or (clamp8((c - 100 * U - 208 * V + 128) shr 8) shl 8) or (clamp8(
                                (c + 516 * U + 128) shr 8
                            ))
                    }
                }

                90 -> {
                    // (x',y') = (dy, ch-1-dx)
                    val xPrimeConst = dy
                    val sxConst = l + xPrimeConst
                    val uvColConst = (sxConst shr 1)
                    for (dx in 0 until outW) {
                        val yPrime = ch - 1 - dx
                        val sy = t + yPrime
                        val yRowBase = sy * yRS
                        val yIndex = yRowBase + sxConst * yPS
                        var Y = getU8(yBuf, yIndex) - 16
                        if (Y < 0) Y = 0
                        val uvRow = (sy shr 1)
                        val U = getU8(uBuf, uvRow * uRS + uvColConst * uPS) - 128
                        val V = getU8(vBuf, uvRow * vRS + uvColConst * vPS) - 128
                        val c = 298 * Y
                        out[rowBase + dx] =
                            (0xFF shl 24) or (clamp8((c + 409 * V + 128) shr 8) shl 16) or (clamp8((c - 100 * U - 208 * V + 128) shr 8) shl 8) or (clamp8(
                                (c + 516 * U + 128) shr 8
                            ))
                    }
                }

                180 -> {
                    // (x',y') = (cw-1-dx, ch-1-dy)
                    val yPrimeConst = ch - 1 - dy
                    val sy = t + yPrimeConst
                    val yRowBase = sy * yRS
                    val uvRow = (sy shr 1)
                    val uRowBase = uvRow * uRS
                    val vRowBase = uvRow * vRS
                    for (dx in 0 until outW) {
                        val xPrime = cw - 1 - dx
                        val sx = l + xPrime
                        val yIndex = yRowBase + sx * yPS
                        var Y = getU8(yBuf, yIndex) - 16
                        if (Y < 0) Y = 0
                        val uvCol = (sx shr 1)
                        val U = getU8(uBuf, uRowBase + uvCol * uPS) - 128
                        val V = getU8(vBuf, vRowBase + uvCol * vPS) - 128
                        val c = 298 * Y
                        out[rowBase + dx] =
                            (0xFF shl 24) or (clamp8((c + 409 * V + 128) shr 8) shl 16) or (clamp8((c - 100 * U - 208 * V + 128) shr 8) shl 8) or (clamp8(
                                (c + 516 * U + 128) shr 8
                            ))
                    }
                }

                270 -> {
                    // (x',y') = (cw-1-dy, dx)
                    val xPrimeConst = cw - 1 - dy
                    val sxConst = l + xPrimeConst
                    val uvColConst = (sxConst shr 1)
                    for (dx in 0 until outW) {
                        val yPrime = dx
                        val sy = t + yPrime
                        val yRowBase = sy * yRS
                        val yIndex = yRowBase + sxConst * yPS
                        var Y = getU8(yBuf, yIndex) - 16
                        if (Y < 0) Y = 0
                        val uvRow = (sy shr 1)
                        val U = getU8(uBuf, uvRow * uRS + uvColConst * uPS) - 128
                        val V = getU8(vBuf, uvRow * vRS + uvColConst * vPS) - 128
                        val c = 298 * Y
                        out[rowBase + dx] =
                            (0xFF shl 24) or (clamp8((c + 409 * V + 128) shr 8) shl 16) or (clamp8((c - 100 * U - 208 * V + 128) shr 8) shl 8) or (clamp8(
                                (c + 516 * U + 128) shr 8
                            ))
                    }
                }
            }
        }

        return recycledBitmap!!.apply {
            setPixels(out, 0, outW, 0, 0, outW, outH)
        }
    }

    private fun clampRect(src: Rect, bounds: Rect): Rect {
        val l = max(bounds.left, min(bounds.right, src.left))
        val t = max(bounds.top, min(bounds.bottom, src.top))
        val r = max(l, min(bounds.right, src.right))
        val b = max(t, min(bounds.bottom, src.bottom))
        return Rect(l, t, r, b)
    }

    private fun rotatedSize(w: Int, h: Int, deg: Int): Pair<Int, Int> =
        if (((deg % 360) + 360) % 360 in listOf(90, 270)) Pair(h, w) else Pair(w, h)

    internal fun getCameraHardwareLevel(context: Context): Int? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        // 기기에 있는 모든 카메라의 ID
        val cameraIds = cameraManager.cameraIdList

        // 첫 번째 카메라(후면 카메라)의 특성
        if (cameraIds.isNotEmpty()) {
            val cameraId = cameraIds[0]
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            return characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        }

        return null
    }

    internal fun ImageProxy.toRoiBitmap(rotateDegree: Int, roi: Rect): Bitmap {
        val bitmap = toBitmap()

        val matrix = Matrix()
        matrix.postRotate(rotateDegree.toFloat())

        return Bitmap.createBitmap(
            bitmap,
            roi.left,
            roi.top,
            roi.width(),
            roi.height(),
            matrix,
            true
        )
    }

    fun Context.isSamsungFold(): Boolean {
        return Build.MANUFACTURER.equals("samsung", ignoreCase = true) &&
                (Build.MODEL?.startsWith("SM-F9", ignoreCase = true) == true)
    }

    fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun RectF.toAbsoluteRectInParent(offsetRect: RectF): RectF {
        return RectF(
            offsetRect.left + this.left,
            offsetRect.top + this.top,
            this.right + offsetRect.left,
            this.bottom + offsetRect.top
        )
    }

    fun Context.isPortrait(): Boolean =
        resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    fun Context.isLandscape(): Boolean =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}