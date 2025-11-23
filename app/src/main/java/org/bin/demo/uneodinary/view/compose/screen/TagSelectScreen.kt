package org.bin.demo.uneodinary.view.compose.screen

// TagSelectScreen.kt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelectScreen(
    sharedViewModel: SharedViewModel,
    onBackClick: () -> Unit,
    onDoneClick: (TotalItems) -> Unit,
    addTag: () -> Unit
) {
    val capturedBitmap by sharedViewModel.capturedBitmap.collectAsState()
    var selectedTag by remember { mutableStateOf<TotalItems?>(null) }
    val isDoneButtonEnabled = selectedTag != null
    val totalItems by sharedViewModel.totalList.observeAsState(initial = emptyList())


    Scaffold(
        containerColor = Color.White,
        topBar = {
            SmallTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
            )
        },

        bottomBar = {
            CommonBottomButton(
                text = "완료",
                isEnabled = isDoneButtonEnabled,
                onClick = {
                    onDoneClick(selectedTag!!)
                },
            )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (capturedBitmap != null) {
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "Captured Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = "image", color = Color.Gray, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. 타이틀
            Text(
                text = "저장할 태그를 선택해주세요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 태그 리스트
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(totalItems) { item ->

                    TagSelectionButton(
                        tagName = item.tag.tagName,
                        isSelected = item == selectedTag,
                        onClick = {
                            selectedTag = if (item == selectedTag) null else item
                        }
                    )
                }

                // 태그 추가하기 버튼
                item {
                    OutlinedButton(
                        onClick = {
                            addTag()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("태그 추가하기", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TagSelectionButton(
    tagName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF42A5F5) else Color(0xFFEEEEEE) // 파란색 또는 연한 회색

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 태그 이름 (# + 이름)
            Row {
                Text(text = "#", color = Color(0xFF42A5F5), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = tagName, color = Color.Black, fontWeight = FontWeight.Normal)
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "선택됨",
                    tint = Color(0xFF42A5F5), // 파란색 체크
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}