package org.bin.demo.uneodinary.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.bin.demo.debug
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.repository.model.dto.BaseResponse
import org.bin.demo.repository.model.dto.CreateReportReqDto
import org.bin.demo.repository.model.dto.ReportResponse
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.interfaces.AppRepository
import org.bin.demo.repository.model.mapper.toExpenseItem
import org.bin.demo.repository.model.mapper.toTotalItemsList
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ApiServiceViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    suspend fun loadTotalItems(): List<TotalItems>? {
        try {
            val response = appRepository.getAllTagReceipts()

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.isSuccess) {
                    val totalItems = responseBody.toTotalItemsList()
                    return totalItems
                }
            }

            return null

        } catch (e: java.net.ConnectException) {
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun loadDetailTag(tagId: Int): TagDetailResultDto? {
        debug("loadDetailTag : $tagId")
        try {
            // 1. API 호출 (suspend 함수 내부에서 동기적으로 실행)
            val response: Response<BaseResponse<TagDetailResultDto>> = appRepository.getTagDetail(tagId)
            debug("loadDetailTag response : $response")

            if (response.isSuccessful) {
                val baseResponse = response.body()

                // 2. HTTP 성공 (2xx) 및 API 로직 성공 (isSuccess = true) 확인
                if (baseResponse != null && baseResponse.isSuccess) {
                    debug("baseResponse : $baseResponse")

                    val resultDto: TagDetailResultDto? = baseResponse.result

                    if (resultDto != null) {
                        // 3. 성공적인 DTO 반환
                        return resultDto
                    }
                } else {
                    // API 로직 실패 (isSuccess = false) 또는 result가 null인 경우
                    println("API 로직 실패: ${baseResponse?.message ?: "Result DTO 없음"}")
                }
            } else {
                // 4. HTTP 실패 (4xx, 5xx)
                println("HTTP 실패: Code=${response.code()}")
            }

        } catch (e: Exception) {
            // 5. 네트워크 연결 오류, JSON 파싱 오류 등 예외 발생 시
            println("예외 발생: ${e.message}")
        }

        return null
    }

    suspend fun uploadReceipt(tagId: Int, ocrData: String): Int {
        debug("uploadReceipt: tagId: $tagId,c ocrData: $ocrData")

        try {
            // 1. API 호출 (suspend 함수 내부에서 동기적으로 실행)
            val response = appRepository.uploadReceipt(ocrData, tagId.toLong())

            debug("uploadReceipt response: $response")

            if (response.isSuccessful) {
                val baseResponse = response.body()

                // 2. HTTP 성공 (2xx) 및 API 로직 성공 (isSuccess = true) 확인
                if (baseResponse != null && baseResponse.isSuccess) {
                    val resultDto = baseResponse.result
                    debug("uploadReceipt result DTO: $resultDto")

                    if (resultDto != null) {
                        return resultDto.receiptId.toInt()
                    }
                } else {
                    debug("uploadReceipt API 실패: Code=${response.code()}, Message=${baseResponse?.message ?: "응답 본문 없음"}")
                }
            } else {
                // 5. HTTP 실패 (4xx, 5xx)
                debug(
                    "uploadReceipt HTTP 실패: Code=${response.code()}, ErrorBody=${
                        response.errorBody()?.string()
                    }"
                )
            }

        } catch (e: Exception) {
            // 6. 네트워크 연결 오류, JSON 파싱 오류 등 예외 발생 시
            debug("uploadReceipt 예외 발생: ${e.message}")
        }

        // 7. 모든 실패 경로 (HTTP 오류, API 오류, 예외 발생)에 대해 null 반환
        return -1
    }

    suspend fun createReports(request: CreateReportReqDto): Int {
        debug("createReports : $request") // debug 함수 사용

        try {
            val response = appRepository.createReport(request)

            if (response.isSuccessful) {
                val responseBody = response.body()
                debug("createReports responseBody : $responseBody")
                debug("responseBody.result : ${responseBody?.result}")
                return responseBody?.result ?: -1
            } else {
                return -1
            }
        } catch (e: Exception) {
            // 네트워크 예외 발생 (ConnectException, TimeOut 등)
            // Log.e("TAG", "createReports 예외 발생: ${e.message}")
            return -1
        }
    }

    suspend fun loadDetailReport(reportId: Int): ReportResponse? {
        try {
            val response = appRepository.getReportDetail(reportId)
            if (response.isSuccessful) {
                // 데이터 처리
                val reports = response.body()
                debug("loadDetailReport : $reports")
                return reports?.result
            } else {
                // 오류 처리
            }
        } catch (e: Exception) {
            // 네트워크 오류 처리
        }
        return null
    }



    fun loadAllReports() {
        viewModelScope.launch {
            try {
                val response = appRepository.getAllReports()
                if (response.isSuccessful) {
                    // 데이터 처리
                    val reports = response.body()
                    debug("reports")
                } else {
                    // 오류 처리
                }
            } catch (e: Exception) {
                // 네트워크 오류 처리
            }
        }
    }

    fun requestAddTag(tagSummary: TagSummary) {
        viewModelScope.launch {
            try {
                val response = appRepository.createTag(tagSummary.tagName)
                if (response.isSuccessful) {
                    val result = response.body()
                    debug("result : $result")
                } else {
                    // 오류 처리
                }
            } catch (e: Exception) {
                // 네트워크 오류 처리
            }
        }
    }


}