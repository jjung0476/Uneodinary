package demo.ocr.camera.interfaces

import android.view.View
import demo.ocr.camera.camera.DemoCameraX

object CameraProvider {
    fun getCameraInstance(rootView: View, koiCameraViewId: Int): CameraRepository {
        return rootView.findViewById<DemoCameraX>(koiCameraViewId) as CameraRepository
    }
}
