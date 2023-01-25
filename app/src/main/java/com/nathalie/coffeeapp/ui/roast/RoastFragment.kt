package com.nathalie.coffeeapp.ui.roast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nathalie.coffeeapp.MyApplication
import com.nathalie.coffeeapp.adapters.RoastAdapter
import com.nathalie.coffeeapp.databinding.FragmentRoastBinding
import com.nathalie.coffeeapp.ui.MainFragmentDirections
import com.nathalie.coffeeapp.viewmodels.MainViewModel
import com.nathalie.coffeeapp.viewmodels.roast.RoastViewModel

class RoastFragment : Fragment() {
    private lateinit var binding: FragmentRoastBinding
    private lateinit var adapter: RoastAdapter
    private val viewModel: RoastViewModel by viewModels {
        RoastViewModel.Provider((requireActivity().applicationContext as MyApplication).roastRepo)
    }
    private val parentViewModel: MainViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoastBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        viewModel.roasts.observe(viewLifecycleOwner) {
            adapter.setRoasts(it)
        }

        parentViewModel.refreshRoast.observe(viewLifecycleOwner) {
            refresh()
        }

        binding.btnAddRoast.setOnClickListener {
            val action = MainFragmentDirections.actionMainToAddRoast()
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    fun refresh() {
        viewModel.getRoasts()
    }

    fun setupAdapter() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = RoastAdapter(emptyList()) {
            val action = MainFragmentDirections.actionMainToEditRoast(it)
            NavHostFragment.findNavController(this).navigate(action)
        }
        binding.rvRoast.adapter = adapter
        binding.rvRoast.layoutManager = layoutManager
    }

    companion object {
        private var roastFragmentInstance: RoastFragment? = null
        fun getInstance(): RoastFragment {
            if (roastFragmentInstance == null) {
                roastFragmentInstance = RoastFragment()
            }

            return roastFragmentInstance!!
        }
    }
}