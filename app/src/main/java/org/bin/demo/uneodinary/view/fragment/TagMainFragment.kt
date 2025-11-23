package org.bin.demo.uneodinary.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bin.demo.adapter.TagMainRVAdapter
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.uneodinary.databinding.FragmentTagMainBinding
import org.bin.demo.uneodinary.view.MainActivity
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel
import kotlin.getValue


@AndroidEntryPoint
class TagMainFragment : Fragment() {

    // lateinit var binding: FragmentTagMainBinding 대신 Null Safety를 위해 처리
    private var _binding: FragmentTagMainBinding? = null
    private val binding get() = _binding!!

    // SharedViewModel 인스턴스 가져오기
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var tagAdapter: TagMainRVAdapter // 어댑터를 lateinit으로 선언

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // View? 대신 View
        _binding = FragmentTagMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. RecyclerView 및 Adapter 설정
        setupRecyclerView()

        // 2. ViewModel LiveData 관찰
        observeViewModel()

        // 3. 기존 버튼 리스너 유지
        binding.tagPlusSet.setOnClickListener {
            // debug("navigateToTagPlusFragment !")
            (activity as? MainActivity)?.navigateToTagPlusFragment() // MainActivity 경로는 가정
        }

        binding.submitButton.setOnClickListener {
            // debug("navigateToTagPlusFragment !")
            (activity as? MainActivity)?.navigateToComposeHomeFragment() // MainActivity 경로는 가정
        }
    }

    private fun setupRecyclerView() {
        // ⭐️ 1. 어댑터 초기화 (빈 리스트는 ViewModel이 updateTagList를 통해 채워줄 것임)
        tagAdapter = TagMainRVAdapter()

        binding.tagRecyclerView.apply {
            adapter = tagAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        // ⭐️ 2. 어댑터 리스너 설정: 삭제 로직을 ViewModel로 위임
        tagAdapter.setMyItemClickListener(object: TagMainRVAdapter.MyItemClickListener{

            override fun onRemoveTag(tag: TagSummary) {
                sharedViewModel.removeTag(tag)
            }
        })
    }

    private fun observeViewModel() {
        // ⭐️ ViewModel의 tagList LiveData를 관찰
        sharedViewModel.tagList.observe(viewLifecycleOwner) { newTagList ->
            // LiveData가 변경될 때마다 어댑터의 데이터 셋을 업데이트
            tagAdapter.updateTagList(newTagList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 바인딩 해제
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagMainFragment()
    }
}