package org.bin.demo.uneodinary.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.bin.demo.debug
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.uneodinary.R
import org.bin.demo.uneodinary.view.compose.ComposePayHomeFragment
import org.bin.demo.uneodinary.view.compose.ComposeReportFragment
import org.bin.demo.uneodinary.view.compose.ComposeTagExpenseDetailFragment
import org.bin.demo.uneodinary.view.compose.ComposeTagSelectFragment
import org.bin.demo.uneodinary.view.compose.ComposeTagSettleProcessFragment
import org.bin.demo.uneodinary.view.compose.ComposeTagSettleSelectFragment
import org.bin.demo.uneodinary.view.event.NavigationEvent
import org.bin.demo.uneodinary.view.fragment.CameraFragment
import org.bin.demo.uneodinary.view.fragment.TagFragment
import org.bin.demo.uneodinary.view.fragment.TagMainFragment
import org.bin.demo.uneodinary.view.fragment.TagPlusFragment
import org.bin.demo.uneodinary.view.viewmodel.ApiServiceViewModel
import org.bin.demo.uneodinary.view.viewmodel.OcrTranslateViewModel
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel
import kotlin.getValue


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private val apiServiceViewModel: ApiServiceViewModel by viewModels()
    private val ocrTranslateViewModel: OcrTranslateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        if (savedInstanceState == null) {
            navigateToTagMainFragment()
//            navigateToComposeHomeFragment()
//            navigateToComposeReportFragment()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)

            val controller = window.insetsController
            if (controller != null) {
                // navigationBars -> 하단바 제거
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        observeNavigationEvents()


        onBackPressedDispatcher.addCallback(this) {
            navigateBack()
        }
    }

    private fun observeNavigationEvents() {
        debug("observeNavigationEvents")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                debug("observeNavigationEvents repeatOnLifecycle(Lifecycle.State.STARTED)")
                // ViewModel의 navigationEvent (Flow) 구독
                sharedViewModel.navigationEvent.collect { event ->
                    debug("event collect : $event")
                    handleNavigation(event)
                }
            }
        }
    }

    private fun handleNavigation(event: NavigationEvent) {
        when (event) {
            NavigationEvent.NavigateToCameraFragment -> {
                navigateToCameraFragment()
            }

            is NavigationEvent.NavigateTagSelect -> {
                navigateToTagSelectFragment()
            }

            is NavigationEvent.ShareContent -> {
                shareContent(event.text)
            }

            is NavigationEvent.NavigateTagExpenseDetail -> {
                val selectedItem = event.totalItems

                debug("NavigateTagExpenseDetail: ${selectedItem.tag.tagName} 태그 상세로 이동!")

                val tagId = selectedItem.tag.tagId
                val ocrTextData = ocrTranslateViewModel.ocrText.value
                debug("pre tagId : $tagId")

                if (tagId != -1) {
                    lifecycleScope.launch {
                        val result = apiServiceViewModel.uploadReceipt(
                            tagId,
                            ocrTextData
                        )

                        debug("result : $result")
                        debug("tagId : $tagId")
                        if (result != -1) {
                            val detailTag = apiServiceViewModel.loadDetailTag(tagId)
                            sharedViewModel.selectedTagDetailResultDto.value = detailTag
                            navigateToTagDetailFragment(result)
                        } else {
                            Toast.makeText(
                                baseContext,
                                "영수증 인식에 실패했습니다. 다시 촬영해주세요.",
                                Toast.LENGTH_SHORT
                            ).show() // 토스트 표시

                            navigateToCameraFragment()
                        }
                    }
                } else {
                    // 필수 데이터(tagId 또는 ocrText)가 부족할 경우 오류 처리
                    debug("오류: tagId 또는 OCR 텍스트 데이터가 부족합니다.")
                }
            }
        }
    }

    fun navigateToTagPlusFragment() {
        debug("navigateToTagPlusFragment !")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TagPlusFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToTagMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TagMainFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToTagFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, TagFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToComposeHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ComposePayHomeFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToSettleProcessFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ComposeTagSettleProcessFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToSettleSelectFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ComposeTagSettleSelectFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToTagSelectFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ComposeTagSelectFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToCameraFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CameraFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToTagDetailFragment(id: Int) {
        debug("navigateToTagDetailFragment !")
        sharedViewModel.selectedExpenseItem.value = id

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ComposeTagExpenseDetailFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToComposeReportFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ComposeReportFragment.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateBack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun shareContent(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "정산 내역 공유")
        startActivity(shareIntent)
    }
}

