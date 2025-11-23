package org.bin.demo.uneodinary.view.compose.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.uneodinary.R
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@Composable
fun HomeScreen(sharedViewModel: SharedViewModel) {
    val totalItems by sharedViewModel.totalList.observeAsState(initial = emptyList())

    if (totalItems.isEmpty()) {
        // 데이터가 없을 때 (로딩 중이거나 빈 상태)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("지출 내역을 불러오는 중...")
        }
    } else {

        Scaffold(
            containerColor = Color.White,
            topBar = {
                Text(
                    text = "쓱- 정산",
                    color = Color(0xFF2196F3), // 파란색
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp)
                )
            },
            bottomBar = {
                HomeBottomNavigationBar(
                    onHomeClick = { /* 홈 클릭 액션 */ },
                    onReportClick = { /* 보고서 클릭 액션 */ }
                )
            }
        ) { paddingValues ->
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
                        onClick = {
                            sharedViewModel.navigateCamera()
                        }
                    )
                }

                // 2. 섹션 타이틀 및 태그 관리 버튼 (단일 아이템)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "태그별 지출 기록",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "태그 관리하기",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .clickable { /* TODO: 태그 관리 화면 이동 */ }
                                .padding(4.dp)
                        )
                    }
                }

                items(totalItems) { group ->
                    TagGroupCard(group, onClick = { tagId ->
                        sharedViewModel.onRecordButtonClicked(tagId!!, isHome = true)
                    })
                }

                // 하단 푸터 영역에 겹치지 않도록 여백 확보
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

enum class BottomNavItem {
    HOME, REPORT
}

@Composable
fun HomeBottomNavigationBar(onHomeClick: () -> Unit, onReportClick: () -> Unit) {
    // ⭐️ 현재 선택된 탭의 상태를 관리합니다. 기본값은 HOME입니다.
    var selectedTab by remember { mutableStateOf(BottomNavItem.HOME) }

    // 파란색 정의 (0xFF2196F3)
    val selectedColor = Color(0xFF2196F3)
    val unselectedColor = Color.Gray

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(56.dp),
        tonalElevation = 8.dp
    ) {
        // --- 1. 홈 아이템 ---
        val isHomeSelected = selectedTab == BottomNavItem.HOME
        NavigationBarItem(
            selected = false,
            onClick = {
                selectedTab = BottomNavItem.HOME // ⭐️ 상태 변경
                onHomeClick()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "홈",
                    modifier = Modifier.size(24.dp),
                    tint = if (isHomeSelected) selectedColor else unselectedColor
                )
            },
            label = {
                Text("홈", fontSize = 14.sp)
            },
            // ⭐️ 색상 설정은 selectedIconColor, selectedTextColor만 사용
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )

        // --- 2. 보고서 아이템 ---
        val isReportSelected = selectedTab == BottomNavItem.REPORT
        NavigationBarItem(
            selected = false,
            onClick = {
                selectedTab = BottomNavItem.REPORT
                onReportClick()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_report),
                    contentDescription = "보고서",
                    modifier = Modifier.size(24.dp),
                    tint = if (isReportSelected) selectedColor else unselectedColor
                )
            },
            label = {
                Text("보고서", fontSize = 14.sp)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )
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

        // R.drawable.ic_document는 가정
        Image(
            painter = painterResource(id = R.drawable.ic_document),
            contentDescription = "문서",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "사진 한장으로 쓱- 기록하기", // 이미지에 맞춰 텍스트 수정
            color = Color.Gray, fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        BaseActionButton(
            text = "지출 기록하기",
            textColor = Color.White,
            borderColor = Color.Transparent,
            backgroundColor = Color(0xFF2196F3), // 파란색으로 수정
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
fun TagGroupCard(
    group: TotalItems,
    onClick: (tagId: TotalItems?) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8)), // 연한 회색 배경
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "# ${group.tag.tagName}", fontSize = 18.sp, fontWeight = FontWeight.Bold
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
            } else {
                // 지출 기록이 없을 때 추가적인 여백
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 카드 내부 하단 버튼 (지출 기록하기 +)
            OutlinedButton(
                onClick = {
                    onClick(group)
                },
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
        modifier = Modifier.size(width = 140.dp, height = 100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = expense.title, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = expense.amount, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BaseActionButton(
    text: String,
    textColor: Color,
    borderColor: Color,
    backgroundColor: Color,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 12.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = Color.LightGray
        ),
        contentPadding = PaddingValues(contentPadding)
    ) {
        Text(text = text, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}