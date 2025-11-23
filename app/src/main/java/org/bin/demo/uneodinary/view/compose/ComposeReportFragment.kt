package org.bin.demo.uneodinary.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView // ComposeView 임포트
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bin.demo.debug
import org.bin.demo.uneodinary.view.compose.screen.CombinedSettlementAndReportScreen
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposeReportFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            debug("sharedViewModel.selectedDetailReport : ${sharedViewModel.selectedDetailReport.value}")
            setContent {
                CombinedSettlementAndReportScreen(
                    detailResult = sharedViewModel.selectedDetailReport,
                    onBackClick = {
                        requireActivity().onBackPressed()
                    })
            }
        }
    }

    companion object {
        fun newInstance() = ComposeReportFragment()
    }
}