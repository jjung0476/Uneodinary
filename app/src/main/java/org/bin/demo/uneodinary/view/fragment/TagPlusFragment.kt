package org.bin.demo.uneodinary.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.bin.demo.repository.model.TagSummary
import org.bin.demo.uneodinary.databinding.FragmentTagPlusBinding
import org.bin.demo.uneodinary.view.viewmodel.ApiServiceViewModel
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel
import kotlin.getValue

@AndroidEntryPoint
class TagPlusFragment : Fragment() {
    lateinit var binding: FragmentTagPlusBinding

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val apiServiceViewModel: ApiServiceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagPlusBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            val tagSummary = TagSummary(binding.editTextTagInput.text.toString())
            sharedViewModel.addTag(tagSummary)
            apiServiceViewModel.requestAddTag(tagSummary)
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.buttonClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.editTextTagInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 내용이 비어있지 않을 때만 View.VISIBLE로 설정
                binding.buttonClearInput.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        binding.buttonClearInput.setOnClickListener {
            binding.editTextTagInput.setText("")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TagPlusFragment()
    }
}