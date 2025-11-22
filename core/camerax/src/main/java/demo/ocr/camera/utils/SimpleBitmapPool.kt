package demo.ocr.camera.utils

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap

internal class SimpleBitmapPool {
    private var crop: Bitmap? = null
    private var rot:  Bitmap? = null

    fun obtainCrop(w: Int, h: Int): Bitmap {
        val b = crop
        return if (b != null && b.width == w && b.height == h && !b.isRecycled) {
            b
        } else {
            createBitmap(w, h)
                .also { crop = it }
        }
    }

    fun obtainRot(w: Int, h: Int): Bitmap {
        val b = rot
        return if (b != null && b.width == w && b.height == h && !b.isRecycled) {
            b
        } else {
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                .also { rot = it }
        }
    }

    fun clear() {
        crop?.recycle(); crop = null
        rot?.recycle();  rot  = null
    }
}