package org.bin.demo.repository.model.dto

data class BaseResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: T?
)
data class CreateReportReqDto(
    val tagId: Int,
    val receiptIds: List<Int>
)

// =================================================================
// 핵심 데이터 모델 (재사용)
// =================================================================

data class Receipt(
    val receiptId: Int,
    val amount: Int,
    val date: String,
    val storeName: String
)

// ReceiptTagDto (GET /receipts/{receiptId} 내 중첩 태그)
data class ReceiptTagDto(
    val tagId: Int,
    val title: String,
    val description: String
)

// TagUserDto (GET /api/tag/{tagId}/user 내 사용자 목록)
data class TagUserDto(
    val userId: Int,
    val username: String
)

// =================================================================
// Result DTO (BaseResponse<T>의 T에 해당하는 타입)
// =================================================================

// ReportResponse (GET /api/reports, GET /api/reports/{reportId} 사용)
data class ReportResponse(
    val reportId: Int,
    val tagName: String,
    val reportDate: String,
    val memberCount: Int,
    val members: List<String>,
    val managerName: String,
    val managerAccount: String,
    val receipts: List<Receipt>,
    val totalAmount: Int
)

// UploadReceiptResultDto (POST /receipts/upload 응답)
data class UploadReceiptResultDto(
    val receiptId: Long
)

// ReceiptDetailResultDto (GET /receipts/{receiptId} 및 AllReceiptsRespDto 목록 항목)
data class ReceiptDetailResultDto(
    val createdAt: String,
    val updatedAt: String,
    val receiptId: Int,
    val storeName: String,
    val purchaseDate: String,
    val totalAmount: Int,
    val imageUrl: String,
    val tag: ReceiptTagDto
)

// TagUsersResultDto (GET /api/tag/{tagId}/user 응답)
data class TagUsersResultDto(
    val tagId: Int,
    val tagName: String,
    val tagUsers: List<TagUserDto>
)

// TagDetailResultDto (GET /api/tag/{tagId}/detail 응답)
data class TagDetailReceiptDto(
    val receiptId: Int,
    val storeName: String,
    val purchaseDate: String,
    val totalAmount: Int
)

data class TagDetailResultDto(
    val tagId: Int,
    val tagName: String,
    val totalAmount: Int,
    val totalUsers: Int,
    val receipts: List<TagDetailReceiptDto>
)

// 1. 최하위 항목: 개별 영수증 정보
data class ReceiptDto(
    val receiptId: Int,
    val storeName: String,
    val purchaseDate: String, // 날짜 문자열
    val totalAmount: Long // 금액은 계산의 안정성을 위해 Long 타입 권장
)

// 2. 중간 항목: 특정 태그와 그에 속한 영수증 목록
data class TagReceiptsDto(
    val tagId: Int,
    val tagName: String,
    // ReceiptDto 리스트를 포함합니다.
    val receipts: List<ReceiptDto>
)

// 3. 최상위 응답 래퍼: API 응답의 전체 구조
// result 필드가 List<TagReceiptsDto>를 포함합니다.
data class TagAllReceiptsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<TagReceiptsDto>
)


// 1. 개별 보고서 요약 항목 DTO
data class ReportTagItemDto(
    val totalAmount: Int,
    val tagName: String,
    val reportId: Long,
    val reportDate: String
)

// 2. 전체 API 응답 DTO
data class ReportResponseDto(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<ReportTagItemDto> // 리스트를 담고 있습니다.
)
