package demo.ocr.camera.camera

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import demo.ocr.camera.interfaces.CropRectangleProvider
import demo.ocr.camera.interfaces.RoiChangeListener

class CropRectangleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), CropRectangleProvider {

    private var baseBackground: Drawable? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (baseBackground == null) {
            baseBackground = background?.constantState?.newDrawable()?.mutate()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    private val rectangleRoi: RectF
        get() = if (width > 0 && height > 0) getRoiArea() else RectF()

    var mRoiChangeListener: RoiChangeListener? = null

    private fun getRoiArea(): RectF {
        val orientation = resources.configuration.orientation
        val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT

        val centerX = width / 2
        val centerY = height / 2

        val roiWidth = width.toFloat()
        val height = height.toFloat()

        val (rotateRoiWidth, rotateRoiHeight) = if (isPortrait) {
            (roiWidth) to height
        } else {
            (height) to roiWidth
        }

        return RectF(
            (centerX - rotateRoiWidth / 2),
            (centerY - rotateRoiHeight / 2),
            (centerX + rotateRoiWidth / 2),
            (centerY + rotateRoiHeight / 2)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w <= 0 || h <= 0) return

        mRoiChangeListener?.onEventOccurred()
    }

    override fun setSize(width: Int, height: Int) {
        (layoutParams as ConstraintLayout.LayoutParams).apply {
            matchConstraintMaxWidth = width
            matchConstraintMaxHeight = height
            constrainedWidth = true
            constrainedHeight = true
        }.also { layoutParams = it }
        requestLayout()
    }

    override fun setRoiChangedCallback(roiChangeListener: RoiChangeListener) {
        mRoiChangeListener = roiChangeListener
    }

    override fun getCropRect(rotateValue: Boolean): RectF {
        return if (rotateValue) {
            rectangleRoi
        } else {
            rectangleRoi
        }
    }

    override fun getViewRect(): RectF {
        val rectInParent = RectF(
            this.left.toFloat(),
            this.top.toFloat(),
            this.right.toFloat(),
            this.bottom.toFloat()
        )
        return rectInParent
    }
}