package org.bin.demo.uneodinary.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView // ComposeView 임포트
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.bin.demo.debug
import org.bin.demo.repository.model.ExpenseItem
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.uneodinary.view.MainActivity
import org.bin.demo.uneodinary.view.compose.screen.TagExpenseDetailScreen
import org.bin.demo.uneodinary.view.viewmodel.ApiServiceViewModel
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposeTagExpenseDetailFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val apiServiceViewModel: ApiServiceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TagExpenseDetailScreen(
                    detailResult = sharedViewModel.selectedTagDetailResultDto,
                    onBackClick = {
                        requireActivity().onBackPressed()
                    },
                    onSettleClick = {
                        sharedViewModel.mIsReportCreate = false
                        (activity as? MainActivity)?.navigateToSettleProcessFragment()
                    },
                    onReportClick = {
                        sharedViewModel.mIsReportCreate = true
                        (activity as? MainActivity)?.navigateToSettleProcessFragment()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = ComposeTagExpenseDetailFragment()
    }
}