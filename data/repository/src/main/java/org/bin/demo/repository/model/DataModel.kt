package org.bin.demo.repository.model


data class TotalItems(val tag: TagSummary, val expenses: List<ExpenseItem>)

data class TagSummary(
    val tagName: String,
    val totalCost: String = "",
    val userName: String = "",
    val userAccount: String = "",
    val participantsCount: Int = -1,
    val tagId: Int = -1,
    val totalUsers: Int = -1,
)


data class SettlementSummaryData(
    val tagName: String,
    val totalAmount: String,
    val memberCount: String,
    val perPersonAmount: String,
    val bankAccount: String
)

data class ExpenseTag(
    val name: String,
    val amount: String,
    val count: Int, // 이 정보는 API에 없으므로 임시 값 사용
    val reportId: Long // 보고서 ID 추가
)


// UI에서 사용할 데이터 모델
data class ExpenseItem(
    val title: String,
    val amount: String, // UI 표시용 (예: "20,000원")
    val date: String,
    val receiptId: Long
)

data class SelectableExpense(
    val item: ExpenseItem,
    val isSelected: Boolean = true
)