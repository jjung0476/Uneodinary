package org.bin.demo.uneodinary.view.fragment

import android.os.Bundle
import android.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.Fragment
import org.bin.demo.uneodinary.R
import org.bin.demo.uneodinary.databinding.FragmentTagMainBinding

class TagMainFragment : Fragment() {
    lateinit var binding: FragmentTagMainBinding

    private val tagData = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tagPlusSet.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, TagPlusFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        val tagRVAdapter = TagMainRVAdapter(tagData)
        binding.tagRecyclerView.adapter = tagRVAdapter

        binding.tagRecyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)

        tagRVAdapter.setMyItemClickListener(object: TagMainRVAdapter.MyItemClickListener{
            override fun onRemoveTag(position: Int) {
                tagRVAdapter.removeItem(position)
            }
        })
    }
}