package org.bin.demo.uneodinary.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView // ComposeView 임포트
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bin.demo.repository.model.SettlementSummaryData
import org.bin.demo.uneodinary.view.compose.screen.SettleSelectScreen
import org.bin.demo.uneodinary.view.compose.screen.SettlementProcessScreen
import org.bin.demo.uneodinary.view.compose.screen.TagSelectScreen
import org.koiware.ocr.demo.app.koi_camera.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposeTagSettleSelectFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val dummyData = SettlementSummaryData(
                    tagName = "여행비",
                    totalAmount = "80,000원",
                    memberCount = "4명",
                    perPersonAmount = "20,000원",
                    bankAccount = "110-234-567890"
                )

                SettlementProcessScreen(
                    summaryData = dummyData,
                    onCloseClick = {},
                    onShareClick = {
                        sharedViewModel.createShareContent(dummyData)
                    }
                )
            }
        }
    }

    companion object {
        fun newInstance() = ComposeTagSettleSelectFragment()
    }
}