package com.example.uneodinary

import android.os.Bundle
import android.widget.*
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.uneodinary.databinding.FragmentTagPlusBinding
import com.example.uneodinary.ui.theme.UneodinaryTheme

class TagPlusFragment : Fragment() {
    lateinit var binding: FragmentTagPlusBinding

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
}