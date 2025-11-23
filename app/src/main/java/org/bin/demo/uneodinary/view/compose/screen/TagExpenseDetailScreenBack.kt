//package org.bin.demo.uneodinary.view.compose.screen
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.res.painterResource
//import org.bin.demo.repository.model.ExpenseItem
//import org.bin.demo.repository.model.TagSummary
//import org.bin.demo.uneodinary.R
//
//@Composable
//fun TagExpenseDetailScreen(
//    summary: TagSummary,
//    expenses: List<ExpenseItem>,
//    onBackClick: () -> Unit,
//    onSettleClick: () -> Unit,
//    onReportClick: () -> Unit
//) {
//    //  저장 완료 토스트/스낵바 상태
//    val showSnackbar = remember { mutableStateOf(true) }
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    //  화면 진입 시 한 번만 저장 완료 메시지 표시
//    LaunchedEffect(key1 = Unit) {
//        if (showSnackbar.value) {
//            snackbarHostState.showSnackbar(
//                message = "#${summary.tagName}에 저장되었습니다.",
//                duration = SnackbarDuration.Short
//            )
//            showSnackbar.value = false
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            CommonAppTopBar(
//                title = "태그별 지출 내역",
//                onLeftIconClick = { onBackClick },
//                onRightIconClick = {},
//            )
//        },
//        snackbarHost = {
//            SnackbarHost(snackbarHostState) { data ->
//                // 사용자 정의 스타일 (이미지처럼 어두운 배경)
//                Snackbar(
//                    snackbarData = data,
//                    containerColor = Color(0xFF212121),
//                    contentColor = Color.White,
//                    modifier = Modifier
//                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                )
//            }
//        },
//        containerColor = Color.White
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = paddingValues
//        ) {
//            item {
//                // 1. 총 비용 요약 섹션
//                SummarySection(
//                    summary = summary,
//                    onSettleClick = onSettleClick,
//                    onReportClick = onReportClick
//                )
//            }
//
//            // 2. 지출 내역 타이틀
//            item {
//                Text(
//                    text = "지출 내역",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
//                )
//            }
//
//            // 3. 지출 내역 목록
//            items(expenses) { expense ->
//                ExpenseListItem(expense = expense)
//            }
//
//            // 스낵바가 하단에 뜨므로, 목록이 잘 보이도록 하단에 여백 추가
//            item { Spacer(Modifier.height(80.dp)) }
//        }
//    }
//}
//
//@Composable
//fun SummarySection(
//    summary: TagSummary,
//    onSettleClick: () -> Unit,
//    onReportClick: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFE3F2FD)) // 밝은 파란색 배경
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "#${summary.tagName} 의 총 비용",
//            fontSize = 16.sp,
//            color = Color.Black.copy(alpha = 0.6f)
//        )
//        Spacer(Modifier.height(4.dp))
//        Text(
//            text = summary.totalCost, // 예: NN,NNN원
//            fontSize = 32.sp,
//            fontWeight = FontWeight.ExtraBold,
//            color = Color.Black
//        )
//        Spacer(Modifier.height(8.dp))
//        Text(
//            text = "참여 인원 ${summary.participantsCount}명",
//            fontSize = 14.sp,
//            color = Color(0xFF42A5F5) // 파란색
//        )
//        Spacer(Modifier.height(20.dp))
//
//        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//            // 정산하기 버튼
//            SummaryActionButton(
//                text = "정산하기",
//                icon = painterResource(id = R.drawable.icon1),
//                onClick = onSettleClick,
//                modifier = Modifier.weight(1f)
//            )
//            // 보고서 만들기 버튼
//            SummaryActionButton(
//                text = "보고서 만들기",
//                icon = painterResource(id = R.drawable.icon2),
//                onClick = onReportClick,
//                modifier = Modifier.weight(1f)
//            )
//        }
//    }
//}
//
//// 요약 영역의 액션 버튼 (정산하기 / 보고서 만들기)
//@Composable
//fun SummaryActionButton(
//    text: String,
//    icon: Painter,
//    onClick: () -> Unit,
//    modifier: Modifier
//) {
//    OutlinedButton(
//        onClick = onClick,
//        modifier = modifier.height(56.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = ButtonDefaults.outlinedButtonColors(
//            containerColor = Color.White,
//            contentColor = Color.Black
//        )
//    ) {
//
//        Image(
//            painter = icon,
//            contentDescription = text,
//            modifier = Modifier.size(20.dp)
//        )
//
//        Spacer(Modifier.width(8.dp))
//        Text(text)
//    }
//}
//
//@Composable
//fun ExpenseListItem(expense: ExpenseItem) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color(0xFFF5F5F5)) // 연한 회색 배경
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column {
//            Text(
//                text = expense.amount, // 예: KG이니시스
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(Modifier.height(4.dp))
//            Text(
//                text = expense.dateTime, // 예: 2025.11.22 오후 3:23
//                fontSize = 12.sp,
//                color = Color.Gray
//            )
//        }
//        Text(
//            text = expense.amount, // 예: NN,NNN원
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}