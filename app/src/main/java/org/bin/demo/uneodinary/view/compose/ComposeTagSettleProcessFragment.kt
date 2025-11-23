package org.bin.demo.uneodinary.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView // ComposeView 임포트
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.bin.demo.repository.model.dto.CreateReportReqDto
import org.bin.demo.uneodinary.view.MainActivity
import org.bin.demo.uneodinary.view.compose.screen.SettleSelectScreen
import org.bin.demo.uneodinary.view.viewmodel.ApiServiceViewModel
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposeTagSettleProcessFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val apiServiceViewModel: ApiServiceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettleSelectScreen(
                    detailResult = sharedViewModel.selectedTagDetailResultDto,
                    onBackClick = {
                        requireActivity().onBackPressed()
                    },
                    isReport = sharedViewModel.mIsReportCreate,
                    onNextClick = { data ->
                        if (sharedViewModel.mIsReportCreate) {
                            sharedViewModel.selectedTagDetailResultDto.let {
                                val receiptIds: List<Int> = data.map { expenseItem ->
                                    expenseItem.receiptId.toInt()
                                }

                                it.value?.let {
                                    val request = CreateReportReqDto(tagId = it.tagId, receiptIds)

                                    lifecycleScope.launch {
                                        val reportId = apiServiceViewModel.createReports(request)
                                        org.bin.demo.debug("reportId : $reportId")
                                        if (reportId != -1) {
                                            val detailReport = apiServiceViewModel. loadDetailReport(reportId)
                                            sharedViewModel.selectedDetailReport.value = detailReport
                                            (activity as? MainActivity)?.navigateToComposeReportFragment()
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "보고서 생성에 실패했습니다. 다시 시도해주세요.",
                                                Toast.LENGTH_SHORT
                                            ).show() // 토스트 표시
                                        }
                                    }
                                }
                            }
                        } else {
                            (activity as? MainActivity)?.navigateToSettleSelectFragment()
                        }
                    })
            }
        }
    }

    companion object {
        fun newInstance() = ComposeTagSettleProcessFragment()
    }
}