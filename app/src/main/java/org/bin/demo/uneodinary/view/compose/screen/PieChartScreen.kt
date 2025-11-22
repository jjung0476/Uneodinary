package org.bin.demo.uneodinary.view.compose.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. 데이터 모델 정의
data class PieChartData(
    val label: String, // 예: 식비
    val value: Float,  // 예: 10
    val color: Color   // 그래프 색상
)

@Composable
fun PieChartScreen() {
    // 2. 입력 데이터 (ArrayList or List)
    val chartDataList = listOf(
        PieChartData("식비", 10f, Color(0xFFFFA726)),   // 주황색
        PieChartData("생활비", 50f, Color(0xFF66BB6A)), // 초록색
        PieChartData("기타", 40f, Color(0xFFEF5350))    // 빨간색
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "지출 분석",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 그래프 그리기 함수 호출
        DrawPieChart(data = chartDataList, radius = 150.dp)
    }
}

@Composable
fun DrawPieChart(
    data: List<PieChartData>,
    radius: Dp
) {
    // 전체 합계 계산 (10 + 50 + 40 = 100)
    val totalSum = data.sumOf { it.value.toDouble() }.toFloat()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        // 1. 원형 그래프 (Canvas)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(radius * 2) // 지름 크기
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 시작 각도 (12시 방향부터 시작하려면 -90도)
                var startAngle = -90f

                data.forEach { slice ->
                    // 차지하는 각도 계산
                    val sweepAngle = (slice.value / totalSum) * 360f

                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true, // 중심점까지 채워서 부채꼴 모양 만듦
                        size = Size(size.width, size.height)
                    )

                    // 다음 조각을 위해 시작 각도 업데이트
                    startAngle += sweepAngle
                }
            }
        }

        // 2. 범례 (Legend) 표시 (옆에 텍스트로 설명)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEach { slice ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 1. 색상 박스 (Box 또는 Spacer 사용)
                    Box(
                        modifier = Modifier
                            .size(12.dp) // 크기 지정
                            .background(
                                color = slice.color,
                                shape = CircleShape // 원형 모양
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 2. 텍스트
                    Text(
                        text = "${slice.label} (${slice.value.toInt()}%)",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPieChart() {
    PieChartScreen()
}