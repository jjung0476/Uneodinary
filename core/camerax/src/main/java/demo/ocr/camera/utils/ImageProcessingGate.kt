package demo.ocr.camera.utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class ImageProcessingGate {
    private val isProcessing = AtomicBoolean(false)

    fun canPublishAndAcquire() = !isProcessing.get()

    fun signalProcessingStart() {
        isProcessing.set(true)
    }
    fun signalProcessingFinished() {
        CoroutineScope(Dispatchers.Default).launch {
             isProcessing.set(false)
        }
    }
}