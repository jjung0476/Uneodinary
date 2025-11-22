package org.bin.demo.uneodinary.view.compose.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.uneodinary.R
import org.koiware.ocr.demo.app.koi_camera.viewmodel.SharedViewModel

data class TagGroup(val tagName: String, val expenses: List<ExpenseItem>)

@Composable
fun SsukSettlementScreen(sharedViewModel: SharedViewModel) {
    val tagGroups = listOf(
        TagGroup(
            tagName = "태그 1", expenses = listOf(
                ExpenseItem("지출 기록", "NN,NNN원"),
                ExpenseItem("지출 기록", "NN,NNN원"),
                ExpenseItem("지출 기록", "NN,NNN원")
            )
        ), TagGroup(
            tagName = "태그 2", expenses = emptyList()
        )
    )

    Scaffold(
        containerColor = Color.White, topBar = {
            Text(
                text = "쓱- 정산",
                color = Color(0xFF2196F3), // 파란색
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp)
            )
        }) { paddingValues ->
        // 메인 세로 스크롤 (LazyColumn)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. 상단 배너 영역 (단일 아이템)
            item {
                TopBannerSection(
                    {
                        sharedViewModel.onRecordButtonClicked()
                    })
            }

            // 2. 섹션 타이틀 (단일 아이템)
            item {
                Text(
                    text = "태그별 지출 기록",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // 3. 태그별 리스트 (반복 아이템)
            items(tagGroups) { group ->
                TagGroupCard(group)
            }

            // 하단 여백 확보용
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun TopBannerSection(
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_document),
            contentDescription = "문서",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "사진 한장으로 쓱- 정산하기", color = Color.Gray, fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        BaseActionButton(
            text = "지출 기록하기 ",
            textColor = Color.White,
            borderColor = Color.Transparent,
            backgroundColor = Color.Blue,
            isEnabled = true,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 12.dp
        ) {
            onClick()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TagGroupCard(group: TagGroup) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8)), // 연한 회색 배경
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 태그 헤더 (#태그 1 >)
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "# ${group.tagName}", fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "더보기",
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 내부 가로 스크롤 (LazyRow)
            if (group.expenses.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(group.expenses) { expense ->
                        ExpenseItemCard(expense)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 카드 내부 하단 버튼 (지출 기록하기 +)
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("지출 기록하기")
            }
        }
    }
}

@Composable
fun ExpenseItemCard(expense: ExpenseItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.size(width = 140.dp, height = 100.dp) // 고정 크기 혹은 wrapContent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = expense.title, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = expense.amount, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

