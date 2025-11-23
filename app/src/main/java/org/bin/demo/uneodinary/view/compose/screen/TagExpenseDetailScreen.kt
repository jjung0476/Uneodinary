package org.bin.demo.uneodinary.view.compose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.LiveData
import org.bin.demo.debug
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.repository.model.dto.Receipt
import org.bin.demo.repository.model.dto.ReportResponse
import org.bin.demo.repository.model.dto.TagDetailResultDto
import org.bin.demo.repository.model.mapper.toExpenseItem
import org.bin.demo.repository.model.mapper.toTagSummary
import org.bin.demo.uneodinary.R

@Composable
fun TagExpenseDetailScreen(
    detailResult: LiveData<TagDetailResultDto?>,

    onBackClick: () -> Unit,
    onSettleClick: () -> Unit,
    onReportClick: () -> Unit
) {
    //  저장 완료 토스트/스낵바 상태
    val showSnackbar = remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    val detail by detailResult.observeAsState(initial = null)

    val summary: TagSummary? = detail?.toTagSummary()

    val expenses: List<ExpenseItem> = remember(detail) {
        detail?.receipts?.map { it.toExpenseItem() } ?: emptyList()
    }

    // LaunchedEffect는 summary가 준비되었을 때만 실행되도록 처리
    LaunchedEffect(key1 = summary) {
        if (summary != null && showSnackbar.value) {
            snackbarHostState.showSnackbar(
                message = "#${summary.tagName}에 저장되었습니다.",
                duration = SnackbarDuration.Short
            )
            showSnackbar.value = false
        }
    }

    Scaffold(
        topBar = {
            CommonAppTopBar(
                title = "태그별 지출 내역",
                onLeftIconClick = onBackClick,
                onRightIconClick = {},
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                // 사용자 정의 스타일 (이미지처럼 어두운 배경)
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF212121),
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            // 1. 총 비용 요약 섹션 (summary가 null이 아닐 때만 표시)
            summary?.let {
                item {
                    SummarySection(
                        summary = it,
                        onSettleClick = onSettleClick,
                        onReportClick = onReportClick
                    )
                }
            }

            // 2. 지출 내역 타이틀
            if (expenses.isNotEmpty()) {
                item {
                    Text(
                        text = "지출 내역",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                }

                // 3. ⭐️ 지출 내역 목록 (리스트 items() 사용)
                items(expenses) { expense ->
                    ExpenseListItem(expense = expense)
                }
            } else if (summary != null) {
                // 데이터가 로드되었으나 지출 내역이 없는 경우
                item {
                    Box(Modifier
                        .fillMaxWidth()
                        .padding(32.dp), Alignment.Center) {
                        Text("아직 기록된 지출 내역이 없습니다.")
                    }
                }
            }

            // 스낵바가 하단에 뜨므로, 목록이 잘 보이도록 하단에 여백 추가
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SummarySection(
    summary: TagSummary,
    onSettleClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD)) // 밝은 파란색 배경
            .padding(16.dp)
    ) {
        Text(
            text = "#${summary.tagName} 의 총 비용",
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = summary.totalCost,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "참여 인원 ${summary.totalUsers}명",
            fontSize = 14.sp,
            color = Color(0xFF42A5F5) // 파란색
        )
        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // 정산하기 버튼
            SummaryActionButton(
                text = "정산하기",
                icon = painterResource(id = R.drawable.icon1),
                onClick = onSettleClick,
                modifier = Modifier.weight(1f)
            )
            // 보고서 만들기 버튼
            SummaryActionButton(
                text = "보고서 만들기",
                icon = painterResource(id = R.drawable.icon2),
                onClick = onReportClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// 요약 영역의 액션 버튼 (정산하기 / 보고서 만들기)
@Composable
fun SummaryActionButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {

        Image(
            painter = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun ExpenseListItem(expense: Receipt) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5)) // 연한 회색 배경
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = expense.storeName, // 예: KG이니시스
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = expense.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "${expense.amount.toString()}원",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun CombinedSettlementAndReportScreen(
    detailResult: LiveData<ReportResponse>,
    onBackClick: () -> Unit,
    ) {
    val scrollState = rememberScrollState()

    val detail by detailResult.observeAsState(initial = null)

    Scaffold(
        topBar = {
            CommonAppTopBar(
                title = "회계 보고서",
                onLeftIconClick = {
                    onBackClick()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(Color(0xFFF0F2F5))
        ) {
            ReportSection(detail)
        }
    }
}

// =================================================================
// 2. 정산/홈 섹션 (이미지 1) - SettlementSection
// =================================================================

// =================================================================
// 3. 회계 보고서 섹션 (이미지 2) - ReportSection
// =================================================================

@Composable
fun ReportSection( detail: ReportResponse?) {
    debug("ReportSection : $detail")
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(horizontal = 16.dp)) {

        // 총 지출 금액 및 태그 정보
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)) {
            Text("총 지출 금액", color = Color.Gray, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = detail?.totalAmount?.toString() ?: "",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = Color(0xFF00BFFF)
                )
                // P 아이콘 Mockup
                Image(
                    painter = painterResource(id = R.drawable.ic_point),
                    contentDescription = "문서",
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("#${detail?.tagName}", color = Color.DarkGray)
        }

        Image(
            painter = painterResource(id = R.drawable.ic_line),
            contentDescription = "문서",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentScale = ContentScale.FillWidth
        )

        // 관리 정보
        ReportInfoRow(label = "관리자", value = "${detail?.managerName}")
        ReportInfoRow(label = "참여 인원", value = "${detail?.memberCount}")
        ReportInfoRow(label = "관리 계좌", value = "${detail?.managerAccount}")

        Image(
            painter = painterResource(id = R.drawable.ic_line),
            contentDescription = "문서",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentScale = ContentScale.FillWidth
        )

        // 지출 내역 목록
        Text("지출 내역", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))


        detail?.receipts?.forEachIndexed { index, expense ->
            ExpenseListItem(expense = expense)
        }

        Spacer(modifier = Modifier.height(20.dp)) // 섹션 하단 여백
    }
}

@Composable
fun ReportInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ExpenseDetailItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("KG이니시스", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Text("2025.11.22 오후 3:23", color = Color.Gray, fontSize = 12.sp)
        }
        Text("NN,NNN원", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun ExpenseListItem(expense: ExpenseItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5)) // 연한 회색 배경
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = expense.amount.toString(), // 예: KG이니시스
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = expense.title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = expense.amount, // 예: NN,NNN원
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
