package org.koiware.ocr.demo.app.koi_camera.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.bin.demo.debug
import org.bin.demo.uneodinary.view.event.NavigationEvent
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bin.demo.repository.model.SettlementSummaryData
import org.bin.demo.uneodinary.UApplication
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SharedViewModel @Inject constructor() : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap = _capturedBitmap.asStateFlow()

    fun onRecordButtonClicked() {
        debug("onRecordButtonClicked !")
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToCameraFragment)
        }
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap

        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateTagSelect(bitmap))
        }
    }

    fun onTagSelected(tagName: String) {
    }

    fun createShareContent(summary: SettlementSummaryData) {
        val shareText = """
        [${summary.tagName} 정산 내역]
        
        총 금액: ${summary.totalAmount}
        구성원 수: ${summary.memberCount}
        1인당 정산 금액: ${summary.perPersonAmount}
        
        [${summary.bankAccount}]로 송금해주세요!
    """.trimIndent()

        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.ShareContent(shareText))
        }
    }

    private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
        // 1. 임시 파일 생성 (캐시 디렉토리)
        val imagesFolder = File(context.cacheDir, "shared_images")
        imagesFolder.mkdirs() // 폴더 생성

        val file = File(imagesFolder, "settlement_share_${System.currentTimeMillis()}.png")

        try {
            // 2. 비트맵을 파일에 압축
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()

            // 3. FileProvider를 통해 URI 얻기
            // Manifest에 <provider> 설정이 되어 있어야 합니다!
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider", // Manifest에 정의된 authority
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
