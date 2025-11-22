package org.bin.demo.uneodinary.view.compose.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.SelectableExpense
import org.bin.demo.repository.model.SettlementSummaryData

@Composable
fun SettlementProcessScreen(
    summaryData: SettlementSummaryData,
    onCloseClick: () -> Unit,
    onShareClick: () -> Unit
) {

    Scaffold(
        topBar = {
            CommonAppTopBar(
                title = "정산하기",
                onRightIconClick = { onCloseClick() }
            )
        },
        bottomBar = {
            CommonBottomButton(
                text = "참여 인원에게 공유하기",
                isEnabled = true, // 항상 활성화라고 가정
                onClick = onShareClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // 1. 타이틀 영역
            Text(
                text = "#${summaryData.tagName}의",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF42A5F5) // 파란색
            )
            Text(
                text = "정산을 진행합니다!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(32.dp))

            // 2. 요약 정보 카드
            SummaryCard(summaryData = summaryData)

            Spacer(Modifier.height(32.dp))

            Spacer(Modifier.weight(1f))

            // 3. 안내 문구
            Text(
                text = "공유만 쓱-해서\n간단하게 정산해보세요!",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementProcessTopBar(onCloseClick: () -> Unit) {
    SmallTopAppBar(
        title = { Text("정산하기") },
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                // 뒤로가기 대신 닫기(X) 아이콘 사용
                Icon(Icons.Default.Close, contentDescription = "닫기")
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun SummaryCard(summaryData: SettlementSummaryData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE3F2FD)) // 연한 파란색 배경
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 총 금액 및 구성원 수
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 총 금액
            SummaryItem(label = "총 금액", value = summaryData.totalAmount)
            // 구성원 수
            SummaryItem(label = "구성원 수", value = summaryData.memberCount)
        }

        Spacer(Modifier.height(20.dp))

        // 2. 1인당 정산 금액
        RoundedContentBox(
            label = "1인당 정산 금액",
            value = summaryData.perPersonAmount,
            valueColor = Color(0xFF42A5F5)
        )

        Spacer(Modifier.height(16.dp))

        // 3. 입금할 은행 계좌
        RoundedContentBox(
            label = "입금할 은행 계좌",
            value = summaryData.bankAccount,
            valueColor = Color(0xFF42A5F5)
        )
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

// 둥근 모서리 박스 (1인당 정산 금액, 계좌 번호)
@Composable
fun RoundedContentBox(label: String, value: String, valueColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}