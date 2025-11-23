package org.bin.demo.uneodinary.view.compose.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.SelectableExpense
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.mapper.toExpenseItem


@Composable
fun SettleSelectScreen(
    detailResult: LiveData<TagDetailResultDto?>,
    onBackClick: () -> Unit,
    onNextClick: (List<ExpenseItem>) -> Unit,
    isReport: Boolean,
) {
    // 1. LiveData 관찰 및 초기 데이터 로드
    val detail by detailResult.observeAsState(initial = null)

    // LiveData에서 지출 항목 리스트 추출 및 매핑
    // detail 값이 바뀔 때만 다시 계산됩니다.
    val initialExpenses: List<ExpenseItem> = remember(detail) {
        detail?.receipts
            ?.map { it.toExpenseItem() }
            ?: emptyList()
    }

    val selectableExpenses: SnapshotStateList<SelectableExpense> = remember {
        mutableStateListOf()
    }

    LaunchedEffect(initialExpenses) {
        selectableExpenses.clear()

        if (initialExpenses.isNotEmpty()) {
            val newItems = initialExpenses.map { SelectableExpense(it, isSelected = true) }
            selectableExpenses.addAll(newItems)
        }
    }


    // 4. 파생 상태 (선택된 항목 및 전체 선택 상태)
    val selectedItems = selectableExpenses.filter { it.isSelected }.map { it.item }
    val isNextButtonEnabled = selectedItems.isNotEmpty()

    // 5. isAllSelected 상태 관리 (계산된 값으로 초기화)
    var isAllSelected by remember {
        mutableStateOf(false)
    }

    // selectableExpenses가 변경될 때마다 isAllSelected 상태 업데이트
    LaunchedEffect(selectableExpenses.toList()) {
        isAllSelected = selectableExpenses.all { it.isSelected } && selectableExpenses.isNotEmpty()
    }


    Scaffold(
        topBar = {
            CommonAppTopBar(
                title = "정산하기",
                onLeftIconClick = onBackClick
            )
        },
        bottomBar = {
            CommonBottomButton(
                text = "다음",
                isEnabled = isNextButtonEnabled,
                onClick = { onNextClick(selectedItems) }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            val title = if (isReport) "보고서를 제작할\n" +
                    "지출 내역을 선택해주세요" else "정산할 지출 내역을\n 선택해주세요"
            // 타이틀
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(24.dp))

            SelectAllButton(
                isAllSelected = isAllSelected,
                onToggle = {
                    val newState = !isAllSelected
                    // 전체 선택/해제 토글
                    selectableExpenses.forEachIndexed { index, item ->
                        selectableExpenses[index] = item.copy(isSelected = newState)
                    }
                    isAllSelected = newState
                }
            )

            Spacer(Modifier.height(16.dp))

            // 지출 내역 목록
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // ⭐️ 실제 API 데이터를 사용하는 selectableExpenses 리스트를 사용
                itemsIndexed(selectableExpenses) { index, item ->
                    SettleSelectListItem(
                        expense = item.item,
                        isSelected = item.isSelected,
                        onClick = {
                            // 항목 선택 상태 토글
                            selectableExpenses[index] = item.copy(isSelected = !item.isSelected)
                            // 전체 선택 상태는 다음 LaunchedEffect에서 업데이트됩니다.
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectAllButton(isAllSelected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.  dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "전체 선택",
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(8.dp))

        Icon(
            painter = rememberVectorPainter(if (isAllSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle),
            contentDescription = "전체 선택",
            tint = if (isAllSelected) Color(0xFF42A5F5) else Color(0xFFD1D5DB),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SettleSelectListItem(expense: ExpenseItem, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) Color(0xFF42A5F5) else Color(0xFFE0E0E0)
    val checkIcon = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ⭐️ 좌측 체크 아이콘
        Icon(
            imageVector = checkIcon,
            contentDescription = "선택",
            tint = if (isSelected) Color(0xFF42A5F5) else Color(0xFFD1D5DB),
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(16.dp))

        // 내역 정보
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = expense.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 금액
        Text(
            text = expense.amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}