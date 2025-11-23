package org.bin.demo.repository.model.interfaces

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.bin.demo.debug
import org.bin.demo.repository.model.dto.BaseResponse
import org.bin.demo.repository.model.dto.CreateReportReqDto
import org.bin.demo.repository.model.dto.ReceiptDetailResultDto
import org.bin.demo.repository.model.dto.ReportResponse
import org.bin.demo.repository.model.dto.TagAllReceiptsResponse
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.dto.TagUsersResultDto
import org.bin.demo.repository.model.dto.UploadReceiptResultDto
import retrofit2.Response
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val apiService: ApiService
) {
    // =================================================================
    // 1. Report Controller (보고서)
    // =================================================================

    suspend fun getAllReports(): Response<List<ReportResponse>> {
        return apiService.getAllReports()
    }

    suspend fun createReport(request: CreateReportReqDto): Response<BaseResponse<Int>> {
        debug("createReport ! : $request")
        return apiService.createReport(request)
    }

    suspend fun getReportDetail(reportId: Int): Response<BaseResponse<ReportResponse>> {
        return apiService.getReportDetail(reportId)
    }

    // =================================================================
    // 2. Receipt Controller (영수증)
    // =================================================================

    suspend fun uploadReceipt(data: String, tagId: Long): Response<BaseResponse<UploadReceiptResultDto>> {
        val body = data.toRequestBody("text/plain".toMediaType())
        return apiService.uploadReceipt(tagId, body)
    }

    suspend fun getReceiptDetail(receiptId: Int): Response<BaseResponse<ReceiptDetailResultDto>> {
        return apiService.getReceiptDetail(receiptId)
    }

    suspend fun deleteReceipt(receiptId: Int): Response<BaseResponse<Int>> {
        return apiService.deleteReceipt(receiptId)
    }

    suspend fun getAllReceipts(): Response<BaseResponse<List<ReceiptDetailResultDto>>> {
        return apiService.getAllReceipts()
    }

    // =================================================================
    // 3. Tag Controller (태그)
    // =================================================================

    suspend fun getTagUsers(tagId: Int): Response<BaseResponse<TagUsersResultDto>> {
        return apiService.getTagUsers(tagId)
    }

    // Unit 대신 EmptyResultDto를 리턴한다고 가정합니다.
    suspend fun addUserToTag(tagId: Int, userName: String): Response<BaseResponse<Unit>> {
        // Response<BaseResponse<Unit>>을 반환하기 위해 apiService 호출 결과를 그대로 반환
        return apiService.addUserToTag(tagId, userName)
    }

    // Unit 대신 EmptyResultDto를 리턴한다고 가정합니다.
    suspend fun createTag(tagName: String): Response<BaseResponse<Unit>> {
        // Response<BaseResponse<Unit>>을 반환하기 위해 apiService 호출 결과를 그대로 반환
        return apiService.createTag(tagName)
    }

    suspend fun getTagDetail(tagId: Int): Response<BaseResponse<TagDetailResultDto>> {
        return apiService.getTagDetail(tagId)
    }

    suspend fun getAllTags(): Response<List<Any>> {
        return apiService.getAllTags()
    }

    suspend fun getAllTagReceipts(): Response<TagAllReceiptsResponse> {
        return apiService.getAllTagReceipts()
    }
}