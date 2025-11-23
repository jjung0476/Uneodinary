package com.example.uneodinary

import android.os.Bundle
import android.widget.*
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uneodinary.databinding.FragmentReportBinding
import retrofit2.Retrofit
import com.example.uneodinary.ui.theme.UneodinaryTheme

class ReportFragment : Fragment() {
    lateinit var binding: FragmentReportBinding

    private val tabData = ArrayList<TabItem>()

    private val reportData = ArrayList<Report>()

    private val authViewModel: AuthViewModel by viewModels {
        val authService = ApiClient.authService
        AuthViewModelFactory(AuthRepository(authService))
    }

    private lateinit var tabRVAdapter: ReportTabRVAdapter
    private lateinit var reportRVAdapter: ReportRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialTagName = "전체"
        val initialReportId = 0

        tabRVAdapter = ReportTabRVAdapter(tabData)
        binding.tagFilterRv.adapter = tabRVAdapter
        binding.tagFilterRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // 탭은 보통 수평입니다.

        reportRVAdapter = ReportRVAdapter(reportData)
        binding.reportSectionsRv.adapter = reportRVAdapter
        binding.reportSectionsRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { newReportList ->

                for (report in newReportList) {
                    reportRVAdapter.addItem(report)
                }

            }.onFailure { throwable ->
                Log.e("REPORT_LOAD_ERROR", "보고서 로드 실패: ${throwable.message}")
            }
        }

        tabRVAdapter.setMyItemClickListener(object : ReportTabRVAdapter.MyItemClickListener {
            override fun onItemClick(tab: TabItem) {
                val targetReportId = tab.reportId
                val targetTagName = tab.tagName

                authViewModel.loadReportsByTag(targetReportId, targetTagName)

                // (선택) 탭이 클릭되었을 때 해당 탭이 활성화되었음을 시각적으로 표시하는 로직 추가
                // tabRVAdapter.selectItem(tab)
            }
        })


    }
}