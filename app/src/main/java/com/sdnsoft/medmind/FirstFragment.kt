package com.sdnsoft.medmind

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sdnsoft.medmind.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity

        val scanModel = mainActivity.model
        Log.d("Test", scanModel.toString())
        var scanAdapter = ScanAdapter(scanModel)
        var rv = _binding?.recyclerViewInfo!!

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = scanAdapter
    }

    override fun onStart() {
        super.onStart()

        val mainActivity = requireActivity() as MainActivity
        mainActivity.enableDisableFabButtons("fragment_main")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}