package demo.ocr.camera.utils

import android.graphics.Rect
import android.util.Size

data class ImageMetaData(
    val lensFocusDistance: Float?,
    val focalLengthMm: Float?,
    val sensorSizeW: Float?,
    val sensorSizeH: Float?,
    val imageProxySize: Size,
    val roiInProxy: Rect?,
    val zoomRatio: Float? = null,      // CameraX ZoomState
    val activeArray: Rect? = null,// SENSOR_INFO_ACTIVE_ARRAY_SIZE
    val cropRegion: Rect? = null  // SCALER_CROP_REGION
)