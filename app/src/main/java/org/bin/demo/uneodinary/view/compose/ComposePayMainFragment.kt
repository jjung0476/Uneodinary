package org.bin.demo.uneodinary.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView // ComposeView 임포트
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bin.demo.uneodinary.view.compose.screen.SsukSettlementScreen
import org.koiware.ocr.demo.app.koi_camera.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposePayMainFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SsukSettlementScreen(sharedViewModel)
            }
        }
    }

    companion object {
        fun newInstance() = ComposePayMainFragment()
    }
}