package com.nathalie.coffeeapp.ui.bean

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.nathalie.coffeeapp.MyApplication
import com.nathalie.coffeeapp.R
import com.nathalie.coffeeapp.databinding.FragmentBeanDetailBinding
import com.nathalie.coffeeapp.viewmodels.bean.BeanDetailViewModel
import com.nathalie.coffeeapp.viewmodels.MainViewModel

class BeanDetailFragment : Fragment() {
    private lateinit var binding: FragmentBeanDetailBinding
    val viewModel: BeanDetailViewModel by viewModels {
        BeanDetailViewModel.Provider((requireContext().applicationContext as MyApplication).beanRepo)
    }
    private val parentViewModel: MainViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeanDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navArgs: BeanDetailFragmentArgs by navArgs()
        viewModel.getBeanById(navArgs.id)


        viewModel.bean.observe(viewLifecycleOwner) {
            binding.run {
                //decode ByteArray from it.image to Bitmap and then display the image at ivBeanImage
                it.image?.let { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(it.image, 0, bytes.size)
                    ivBeanImage.setImageBitmap(bitmap)
                }

                //setting text values of title, subtitle, taste and details
                tvTitle.text = it.title
                tvSubtitle.text = it.subtitle
                tvTaste.text = it.taste
                tvDetails.text = it.details

                //setting 3 sliders value according to the value of it.body, it.aroma and it.caffeine
                sliderBody.value = it.body.toFloat()
                sliderAroma.value = it.aroma.toFloat()
                sliderCaffeine.value = it.caffeine.toFloat()

                //disable sliding of all 3 sliders
                sliderBody.isEnabled = false
                sliderAroma.isEnabled = false
                sliderCaffeine.isEnabled = false
            }
        }

        //when edit button is clicked, user will be taken to edit bean fragment along with its id passed through navArgs
        binding.btnEdit.setOnClickListener {
            val action = BeanDetailFragmentDirections.actionBeanDetailToEditBean(navArgs.id)
            NavHostFragment.findNavController(this).navigate(action)
        }


        //when delete btn is clicked, a dialog that requests confirmation will pop up
        //once confirmed, this coffee bean will be deleted
        binding.btnDelete.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("refresh", true)
            MaterialAlertDialogBuilder(requireContext(), R.style.CoffeeApp_AlertDialog)
                .setTitle("Delete ${binding.tvTitle.text}?")
                .setCancelable(true)
                .setPositiveButton("Delete") { _, it ->
                    viewModel.deleteBean(navArgs.id)
                    setFragmentResult("from_delete_bean", bundle)
                    NavHostFragment.findNavController(this).popBackStack()
                }.setNegativeButton("Cancel") { _, it ->
                }
                .show()
        }

        //this is coming from edit bean fragment, it will refresh the current fragment
        setFragmentResultListener("from_edit_bean") { _, result ->
            val refresh = result.getBoolean("refresh")
            parentViewModel.shouldRefreshDrinks(refresh)

            //Snackbar that tells user the coffee bean is updated, with an action button that once clicked, will take user back to BeanFragment.kt
            snackbar = Snackbar.make(requireView(), "Coffee bean updated!", Snackbar.LENGTH_LONG)
            val view2 = snackbar!!.view
            val params = view2.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view2.layoutParams = params
            snackbar!!
                .setAction("Back To Beans") {
                    NavHostFragment.findNavController(this).popBackStack()
                }
                .setBackgroundTint(resources.getColor(R.color.almond))
                .setActionTextColor(resources.getColor(R.color.chestnut))
                .setTextColor(resources.getColor(R.color.smoky))
                .setAnchorView(binding.btnEdit)
                .show()
        }
    }

}