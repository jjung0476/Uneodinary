package org.bin.demo.repository.model

data class TagSummary(
    val tagName: String,
    val totalCost: String,
    val participantsCount: Int
)

data class ExpenseItem(
    val title: String,
    val amount: String,
    val dateTime: String = "",
    val id: Int = -1,
)

data class SelectableExpense(
    val item: ExpenseItem,
    val isSelected: Boolean = false
)

data class SettlementSummaryData(
    val tagName: String,
    val totalAmount: String,
    val memberCount: String,
    val perPersonAmount: String,
    val bankAccount: String
)