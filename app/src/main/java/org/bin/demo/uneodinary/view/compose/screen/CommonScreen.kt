package org.bin.demo.uneodinary.view.compose.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlin.apply

import kotlin.text.isEmpty
import androidx.core.graphics.createBitmap

//@Composable
//fun TopBar(title: String, backClick: () -> Unit) {
//    val drawLineColor = MaterialTheme.colorScheme.outlineVariant
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(63.dp)
//            .drawBehind {
//                val strokeWidthPx = Density.density.run { 1.dp.toPx() }
//                val y = DrawScope.size.height - strokeWidthPx / 2
//                DrawScope.drawLine(
//                    color = drawLineColor,
//                    start = Offset(0f, y),
//                    end = Offset(DrawScope.size.width, y),
//                    strokeWidth = strokeWidthPx,
//                    cap = StrokeCap.Square
//                )
//            },
//        contentAlignment = Alignment.Center, // Box의 기본 정렬은 중앙으로 유지
//    ) {
//        // 뒤로가기 아이콘 (왼쪽 정렬)
//        Row( // Icon을 Row로 감싸서 특정 정렬에 배치
//            modifier = Modifier
//                .fillMaxWidth() // Row가 전체 너비를 차지하게 하고
//                .align(Alignment.CenterStart)
//                .padding(start = 16.dp),
//            verticalAlignment = Alignment.CenterVertically // Row 내부에서 수직 중앙 정렬
//        ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
//                contentDescription = "뒤로가기",
//                modifier = Modifier
//                    .size(24.dp)
//                    .clickable(onClick = backClick),
//                tint = MaterialTheme.colorScheme.onTertiaryContainer,
//            )
//        }
//
//        // 텍스트가 아이콘 영역을 침범하지 않도록 왼쪽에 패딩 추가
//        val iconWidth = 24.dp
//        val horizontalTextPadding = iconWidth + 16.dp
//
//        Text(
//            text = title,
//            fontSize = 16.sp,
//            color = MaterialTheme.colorScheme.onTertiaryContainer,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = horizontalTextPadding),
//            textAlign = TextAlign.Center,
//            maxLines = 1
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonAppTopBar(
    title: String,
    onLeftIconClick: (() -> Unit)? = null,
    onRightIconClick: (() -> Unit)? = null,
    leftIcon: ImageVector = Icons.Default.ArrowBack,
    rightIcon: ImageVector = Icons.Default.MoreVert
) {
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },

        // 2. 왼쪽 아이콘 설정 (onLeftIconClick의 null 여부로 표시 결정)
        navigationIcon = {
            onLeftIconClick?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(leftIcon, contentDescription = "왼쪽 버튼")
                }
            }
        },

        // 3. 오른쪽 아이콘 설정 (onRightIconClick의 null 여부로 표시 결정)
        actions = {
            onRightIconClick?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(rightIcon, contentDescription = "오른쪽 버튼")
                }
            }
        },

        // 4. 공통 스타일
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun CommonBottomButton(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) Color(0xFF42A5F5) else Color(0xFFD1D5DB), // 파란색 또는 회색
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BaseActionButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color = Color.Transparent,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    contentPadding: Dp = 15.dp,
    onClick: () -> Unit,
) {
    val actualBorder = if (borderColor == Color.Transparent) {
        BorderStroke(0.dp, Color.Transparent)
    } else {
        BorderStroke(1.dp, borderColor)
    }

    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            disabledContentColor = textColor.copy(alpha = 0.5f)
        ),
        border = actualBorder,
        contentPadding = PaddingValues(vertical = contentPadding),
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = backgroundColor,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun rememberBitmapFromComposable(
    content: @Composable () -> Unit // 캡처할 Composable 컨텐츠
): Bitmap? {
    val view = LocalView.current // 현재 View를 가져옴
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    // 캡처는 LaunchedEffect에서 비동기적으로 수행
    LaunchedEffect(key1 = Unit) {
        bitmap = captureComposableToBitmap(view, content)
    }
    return bitmap
}

suspend fun captureComposableToBitmap(view: View, content: @Composable () -> Unit): Bitmap {
    return withContext(Dispatchers.Main) {
        val displayMetrics = view.context.resources.displayMetrics
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.AT_MOST)

        // 캡처할 Composable을 View로 변환
        val composeView = ComposeView(view.context).apply {
            setContent(content)
        }

        // View 측정 및 레이아웃
        composeView.measure(widthMeasureSpec, heightMeasureSpec)
        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

        val bitmap = createBitmap(composeView.measuredWidth, composeView.measuredHeight)
        val canvas = Canvas(bitmap)
        composeView.draw(canvas)
        bitmap
    }
}

@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imageVector: ImageVector? = null,
    isEnabled: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        cursorBrush = SolidColor(Color.Black),
        visualTransformation = visualTransformation,
        // decorationBox를 사용하여 내부 콘텐츠를 커스터마이징합니다.
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box() {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            fontSize = 16.sp,
                            color = Color.Gray,
                        )
                    }
                    innerTextField() // 실제 텍스트 입력 컴포저블
                }
                imageVector?.apply {
                    Icon(
                        imageVector = this,
                        contentDescription = "검색 아이콘",
                        tint = Color.Gray // 아이콘 색상
                    )
                }
            }
        }
    )
}

@Composable
fun BaseAlertDialogShape(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
    confirmButton: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }
        },
        text = content,
        confirmButton = {
            confirmButton?.apply {
                this()
            }
        },
        dismissButton = { onDismissRequest() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        modifier = Modifier.padding(16.dp)
    )
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}