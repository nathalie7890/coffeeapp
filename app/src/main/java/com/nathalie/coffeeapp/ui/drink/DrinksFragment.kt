package com.nathalie.coffeeapp.ui.drink


import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.nathalie.coffeeapp.MyApplication
import com.nathalie.coffeeapp.adapters.DrinkAdapter
import com.nathalie.coffeeapp.databinding.FragmentDrinksBinding
import com.nathalie.coffeeapp.ui.MainFragmentDirections
import com.nathalie.coffeeapp.utils.Utils
import com.nathalie.coffeeapp.utils.Utils.hideKeyboard
import com.nathalie.coffeeapp.viewmodels.MainViewModel
import com.nathalie.coffeeapp.viewmodels.drink.DrinkViewModel



class DrinksFragment : Fragment() {
    private lateinit var adapter: DrinkAdapter
    private lateinit var binding: FragmentDrinksBinding
    private val viewModel: DrinkViewModel by viewModels {
        DrinkViewModel.Provider((requireActivity().applicationContext as MyApplication).drinkRepo)
    }
    private val parentViewModel: MainViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrinksBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        viewModel.drinks.observe(viewLifecycleOwner) {
            adapter.setDrinks(it)
        }

        parentViewModel.refreshDrinks.observe(viewLifecycleOwner) {
            refresh("", 0)
        }

//        binding.rvDrinks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0) {
//                    binding.searchContainer.isVisible = false
//                    binding.btnFilter.isVisible = false
//                } else if (dy < 0) {
//                    binding.btnFilter.isVisible = true
//                    binding.searchContainer.isVisible = true
//                }
//            }
//        })

        binding.srlRefresh.setOnRefreshListener {
            viewModel.onRefresh()
            binding.search.etSearch.setText("")
        }

        viewModel.swipeRefreshLayoutFinished.asLiveData()
            .observe(viewLifecycleOwner) {
                binding.srlRefresh.isRefreshing = false
            }

        binding.run {
            search.etSearch.setOnKeyListener OnKeyListener@{ _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    val search = search.etSearch.text.toString()
                    refresh(search, 0)
                    hideKeyboard()
                    return@OnKeyListener true
                }
                false
            }

            btnAdd.setOnClickListener {
                val action = MainFragmentDirections.actionMainToAddDrink()
                NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            }

            btnAll.setOnClickListener {
                refresh("", 0)
                Utils.updateColors(requireContext(), btnAll, btnHot, btnCold)
            }
            btnHot.setOnClickListener {
                refresh("", 1)
                Utils.updateColors(requireContext(), btnHot, btnAll, btnCold)
            }
            btnCold.setOnClickListener {
                refresh("", 2)
                Utils.updateColors(requireContext(), btnCold, btnAll, btnHot)
            }
        }
    }

    //fetch words
    fun refresh(str: String, cat: Int) {
        viewModel.getDrinks(str, cat)
    }


    //adapter for words
    fun setupAdapter() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = DrinkAdapter(emptyList()) {
            val action = MainFragmentDirections.actionMainToDetail(it.id!!)
            NavHostFragment.findNavController(this).navigate(action)
        }

        binding.rvDrinks.adapter = adapter
        binding.rvDrinks.layoutManager = layoutManager
    }


    companion object {
        private var drinksFragmentInstance: DrinksFragment? = null
        fun getInstance(): DrinksFragment {
            if (drinksFragmentInstance == null) {
                drinksFragmentInstance = DrinksFragment()
            }

            return drinksFragmentInstance!!
        }
    }
}