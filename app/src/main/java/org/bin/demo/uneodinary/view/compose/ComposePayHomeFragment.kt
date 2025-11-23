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
import org.bin.demo.repository.model.TotalItems
import org.bin.demo.uneodinary.view.compose.screen.HomeScreen
import org.bin.demo.uneodinary.view.viewmodel.ApiServiceViewModel
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@AndroidEntryPoint
class ComposePayHomeFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val apiServiceViewModel: ApiServiceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreen(sharedViewModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val items: List<TotalItems>? = apiServiceViewModel.loadTotalItems()
                sharedViewModel.totalList.value = items
                debug("items : $items")
            }

        }
    }

    override fun onStart() {
        super.onStart()

    }

    companion object {
        fun newInstance() = ComposePayHomeFragment()
    }
}