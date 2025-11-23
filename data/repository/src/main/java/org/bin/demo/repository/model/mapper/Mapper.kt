package org.bin.demo.repository.model.mapper

import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.ExpenseTag
import org.bin.demo.repository.model.SelectableExpense
import org.bin.demo.repository.model.SettlementSummaryData
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.repository.model.dto.ReceiptDetailResultDto
import org.bin.demo.repository.model.dto.ReceiptDto
import org.bin.demo.repository.model.dto.ReportResponse
import org.bin.demo.repository.model.dto.ReportTagItemDto
import org.bin.demo.repository.model.dto.TagAllReceiptsResponse
import org.bin.demo.repository.model.dto.TagDetailReceiptDto
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.dto.TagReceiptsDto
import org.bin.demo.repository.model.dto.UploadReceiptResultDto

// Mapper.kt

// 1. TagDetailReceiptDto (태그 상세 조회 내 영수증) -> ExpenseItem
fun TagDetailReceiptDto.toDomain(): ExpenseItem {
    return ExpenseItem(
        title = this.storeName,
        // String -> String 변환 (totalAmount가 서버에서 Int였으나 DTO는 Int, 도메인은 String으로 가정)
        amount = this.totalAmount.toString(),
        date = this.purchaseDate,
        receiptId = this.receiptId.toLong()
    )
}

// 2. ReceiptDetailResultDto (영수증 상세 조회) -> ExpenseItem
fun ReceiptDetailResultDto.toDomain(): ExpenseItem {
    return ExpenseItem(
        title = this.storeName,
        amount = this.totalAmount.toString(),
        date = this.purchaseDate,
        receiptId = this.receiptId.toLong()
    )
}

// 1. TagDetailResultDto (태그 상세 조회) -> TagSummary
fun TagDetailResultDto.toDomain(): TagSummary {
    return TagSummary(
        tagName = this.tagName,
        // Int -> String 변환
        totalCost = this.totalAmount.toString(),
        participantsCount = this.totalUsers
    )
}

// 2. ReportResponse (보고서 응답) -> SettlementSummaryData
fun ReportResponse.toSettlementSummaryData(): SettlementSummaryData {
    // 1인당 정산 금액 계산 (로직이 명확하지 않으므로 임시로 '0'으로 처리)
    val perPersonAmountCalculated: String =
        if (memberCount > 0)
            (totalAmount / memberCount).toString()
        else
            "0"

    return SettlementSummaryData(
        tagName = this.tagName,
        totalAmount = this.totalAmount.toString(),
        memberCount = this.memberCount.toString(),
        perPersonAmount = perPersonAmountCalculated,
        bankAccount = this.managerAccount // 관리자 계좌 정보 사용
    )
}

fun List<TagDetailReceiptDto>.toExpenseItemList(): List<ExpenseItem> {
    return this.map { it.toDomain() }
}

fun List<TagDetailReceiptDto>.toSelectableExpenseList(): List<SelectableExpense> {
    return this.map {
        SelectableExpense(
            item = it.toDomain(),
            isSelected = false
        )
    }
}

fun TagAllReceiptsResponse.toTotalItemsList(): List<TotalItems> {

    // 응답의 result가 null이거나 비어있으면 빈 리스트 반환
    val tagReceiptsList = this.result

    return tagReceiptsList.map { tagReceiptsDto ->
        // 1. TagSummary 객체 생성
        val tagSummary = tagReceiptsDto.toTagSummary()

        // 2. ExpenseItem 리스트 생성
        val expenseItems = tagReceiptsDto.receipts?.map { receiptDto ->
            receiptDto.toExpenseItem()
        } ?: emptyList()

        // 3. TotalItems 객체 생성 및 반환
        TotalItems(
            tag = tagSummary,
            expenses = expenseItems
        )
    }
}

private fun TagReceiptsDto.toTagSummary(): TagSummary {
    val calculatedTotalCost = this.receipts?.sumOf { it.totalAmount } ?: 0L
    val totalCostString = formatAmountToString(calculatedTotalCost)

    return TagSummary(
        tagName = this.tagName ?: "태그 이름 없음",
        totalCost = totalCostString,
        participantsCount = -1, // 서버 응답에 없으므로 임시로 -1 유지
        tagId = this.tagId
    )
}

private fun ReceiptDto.toExpenseItem(): ExpenseItem {
    return ExpenseItem(
        title = this.storeName ?: "상점 이름 없음",
        amount = formatAmountToString(this.totalAmount),
        date = this.purchaseDate ?: "", // 구매 날짜/시간
        receiptId = this.receiptId.toLong()
    )
}

// 금액 포맷팅 함수 (Mapper 파일 내에 보조 함수로 포함 가능)
private fun formatAmountToString(amount: Long?): String {
    if (amount == null || amount < 0) return "0원"
    return "${String.format("%,d", amount)}원"
}

private fun formatIntAmountToString(amount: Int?): String {
    if (amount == null || amount < 0) return "0원"
    return "${String.format("%,d", amount)}원"
}

// TagDetailResultDto 내의 영수증 상세 DTO (가정)
data class TagDetailReceiptDto(
    val receiptId: Int,
    val storeName: String,
    val totalAmount: Int,
    val purchaseDate: String
)



fun TagDetailReceiptDto.toExpenseItem(): ExpenseItem {
    return ExpenseItem(
        title = this.storeName,
        amount = formatIntAmountToString(this.totalAmount),
        date = this.purchaseDate,
        receiptId = this.receiptId.toLong()
    )
}

fun TagDetailResultDto.toTagSummary(): TagSummary {
    return TagSummary(
        tagName = this.tagName,
        totalCost = formatIntAmountToString(this.totalAmount),
        participantsCount = this.totalUsers,
        tagId = this.tagId,
        totalUsers = totalUsers,
    )
}

fun ReportTagItemDto.toExpenseTag(): ExpenseTag {
    return ExpenseTag(
        name = this.tagName,
        amount = String.format("%,d", this.totalAmount), // 금액 포맷팅
        count = 0, // 임시값 (API에 없음)
        reportId = this.reportId
    )
}
