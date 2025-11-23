package org.bin.demo.uneodinary.view.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.launch
import org.bin.demo.debug
import org.bin.demo.uneodinary.view.event.NavigationEvent
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.SettlementSummaryData
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.repository.model.dto.ReportResponse
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.mapper.toTagSummary
import org.bin.demo.uneodinary.UApplication.Companion.extractNumber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SharedViewModel @Inject constructor() : ViewModel() {


    private val _tagList = MutableLiveData<List<TagSummary>>()
    val tagList: LiveData<List<TagSummary>> = _tagList

    val totalList = MutableLiveData<List<TotalItems>>()
    val selectedTotalItem = MutableLiveData<TotalItems?>()
    val selectedExpenseItem = MutableLiveData<Int>()
    val selectedTagDetailResultDto = MutableLiveData<TagDetailResultDto?>()
    val selectedDetailReport = MutableLiveData<ReportResponse>()

    val ocrData = MutableLiveData<String>()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap = _capturedBitmap.asStateFlow()

    var mIsHome = false
    var mIsReportCreate = false

    fun onRecordButtonClicked(totalItems: TotalItems, isHome: Boolean = false) {
        debug("onRecordButtonClicked !")
        selectedTotalItem.value = totalItems
        mIsHome = isHome
        viewModelScope.launch {
            if (isHome) {
                _navigationEvent.emit(NavigationEvent.NavigateToCameraFragment)
            } else {
                _navigationEvent.emit(NavigationEvent.NavigateTagExpenseDetail(totalItems))
            }
        }
    }

    fun navigateCamera() {
        debug("naviGateCamera !")
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToCameraFragment)
        }
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _capturedBitmap.value = bitmap

        viewModelScope.launch {
            selectedTotalItem.value?.let {
                debug("mIsHome : $mIsHome")
                if (mIsHome) {
                    _navigationEvent.emit(NavigationEvent.NavigateTagExpenseDetail(it))
                } else {
                    _navigationEvent.emit(NavigationEvent.NavigateTagSelect(bitmap))
                }
            } ?: run {
                _navigationEvent.emit(NavigationEvent.NavigateTagSelect(bitmap))
            }
        }
    }

    fun onTagSelected(tagName: String) {

    }

    fun createShareContent(summary: TagDetailResultDto) {

        val summary: TagSummary = summary.toTagSummary()

        val numberValue = extractNumber(summary.totalCost)
        val totalCostAsDouble = numberValue.toDouble()

// 4. 변환된 값을 사용하여 계산합니다.
        val perPersonCost = if (summary.totalUsers > 0) {
            // ⭐️ 이제 totalCostAsDouble은 숫자(709846.0)이므로 에러가 나지 않습니다.
            totalCostAsDouble / summary.totalUsers
        } else {
            0.0
        }

        val formattedTotalCost = String.format("%,d원", summary.totalCost)
        val formattedPerPersonAmount = String.format("%,d원", perPersonCost)

        val shareText = """
    [${summary.tagName} 정산 내역]
    
    총 금액: $formattedTotalCost
    구성원 수: ${summary.totalUsers}명
    1인당 정산 금액: $formattedPerPersonAmount
    
    [${summary.userAccount}]로 송금해주세요!
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
                context, "${context.packageName}.fileprovider", // Manifest에 정의된 authority
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun removeTag(tag: TagSummary) {
        val currentList = _tagList.value.orEmpty().toMutableList()

        if (currentList.remove(tag)) {
            _tagList.value = currentList.toList()
        }
    }

    fun addTag(newTag: TagSummary) {
        // 1. 현재 LiveData 값을 가져와 MutableList로 변환
        val currentList = _tagList.value.orEmpty().toMutableList()

        // 2. 새 항목을 리스트에 추가
        currentList.add(newTag)

        // 3. 변경된 리스트를 다시 LiveData에 할당하여 Fragment/Activity에 변경을 알림
        _tagList.value = currentList.toList()
    }
}
