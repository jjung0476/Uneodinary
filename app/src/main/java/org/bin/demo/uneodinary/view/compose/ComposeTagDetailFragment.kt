package org.bin.demo.uneodinary.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView // ComposeView 임포트
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.uneodinary.view.compose.screen.TagDetailScreen
import org.bin.demo.uneodinary.view.compose.screen.TagSelectScreen
import org.koiware.ocr.demo.app.koi_camera.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposeTagDetailFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val summaryData = TagSummary(
                    tagName = "식비",
                    totalCost = "45,000원",
                    participantsCount = 4
                )
                val expenseList = listOf(
                    ExpenseItem("KG이니시스", "20,000원", "2025.11.22 오후 3:23"),
                    ExpenseItem("스타벅스", "5,000원", "2025.11.22 오후 4:00"),
                    ExpenseItem("배달의민족", "20,000원", "2025.11.23 오전 10:00")
                )

                TagDetailScreen(
                    summary = summaryData,
                    expenses = expenseList,
                    onBackClick = {},
                    onSettleClick = {},
                    onReportClick = {}
                )
            }
        }
    }

    companion object {
        fun newInstance() = ComposeTagDetailFragment()
    }
}