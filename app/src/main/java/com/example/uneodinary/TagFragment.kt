package com.example.uneodinary

import android.os.Bundle
import android.widget.*
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.uneodinary.databinding.FragmentTagBinding
import com.example.uneodinary.ui.theme.TagMainFragment
import com.example.uneodinary.ui.theme.UneodinaryTheme

class TagFragment : Fragment() {
    lateinit var binding: FragmentTagBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, TagMainFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}