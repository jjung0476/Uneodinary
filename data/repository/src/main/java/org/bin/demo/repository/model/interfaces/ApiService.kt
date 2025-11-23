package org.bin.demo.repository.model.interfaces

import okhttp3.RequestBody
import org.bin.demo.repository.model.dto.BaseResponse
import org.bin.demo.repository.model.dto.CreateReportReqDto
import org.bin.demo.repository.model.dto.ReceiptDetailResultDto
import org.bin.demo.repository.model.dto.ReportResponse
import org.bin.demo.repository.model.dto.TagAllReceiptsResponse
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.dto.TagUsersResultDto
import org.bin.demo.repository.model.dto.UploadReceiptResultDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // =================================================================
    // 1. Report Controller (보고서)
    // =================================================================

    @GET("/api/reports")
    // Response<List<ReportResponse>>는 목록이므로 BaseResponse 사용 불가
    suspend fun getAllReports(): Response<List<ReportResponse>>

    @POST("/api/reports")
    suspend fun createReport(@Body request: CreateReportReqDto): Response<BaseResponse<Int>>

    @GET("/api/reports/{reportId}")
    suspend fun getReportDetail(@Path("reportId") reportId: Int): Response<BaseResponse<ReportResponse>>


    // =================================================================
    // 2. Receipt Controller (영수증)
    // =================================================================

    @POST("/receipts/upload")
    suspend fun uploadReceipt(
        @Query("tagId") tagId: Long,
        @Body request: RequestBody
    ): Response<BaseResponse<UploadReceiptResultDto>>

    @GET("/receipts/{receiptId}")
    suspend fun getReceiptDetail(@Path("receiptId") receiptId: Int): Response<BaseResponse<ReceiptDetailResultDto>>

    @DELETE("/receipts/{receiptId}")
    suspend fun deleteReceipt(@Path("receiptId") receiptId: Int): Response<BaseResponse<Int>>

    @GET("/receipts/all")
    suspend fun getAllReceipts(): Response<BaseResponse<List<ReceiptDetailResultDto>>>

    // =================================================================
    // 3. Tag Controller (태그)
    // =================================================================

    @GET("/api/tag/{tagId}/user")
    suspend fun getTagUsers(@Path("tagId") tagId: Int): Response<BaseResponse<TagUsersResultDto>>

    @POST("/api/tag/{tagId}/user")
    suspend fun addUserToTag(
        @Path("tagId") tagId: Int,
        @Query("userName") userName: String
    ): Response<BaseResponse<Unit>>

    @POST("/api/tag/upload")
    suspend fun createTag(@Query("tagName") tagName: String): Response<BaseResponse<Unit>>

    @GET("/api/tag/{tagId}/detail")
    suspend fun getTagDetail(@Path("tagId") tagId: Int): Response<BaseResponse<TagDetailResultDto>>

    @GET("/api/tag/all")
    suspend fun getAllTags(): Response<List<Any>>


    @GET("/api/tag/receipt/all")
    suspend fun getAllTagReceipts(): Response<TagAllReceiptsResponse>

}