package org.bin.demo.uneodinary.view.fragment

import android.os.Bundle
import android.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.bin.demo.uneodinary.R
import org.bin.demo.uneodinary.databinding.FragmentTagBinding

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