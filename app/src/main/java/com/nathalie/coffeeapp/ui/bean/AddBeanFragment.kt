package com.nathalie.coffeeapp.ui.bean

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.nathalie.coffeeapp.MyApplication
import com.nathalie.coffeeapp.data.model.Bean
import com.nathalie.coffeeapp.databinding.FragmentAddBeanBinding
import com.nathalie.coffeeapp.viewmodels.bean.AddBeanViewModel

class AddBeanFragment : Fragment() {
    private lateinit var binding: FragmentAddBeanBinding
    private lateinit var filePickerLauncher: ActivityResultLauncher<String>
    private var bytes: ByteArray? = null
    private val viewModel: AddBeanViewModel by viewModels {
        AddBeanViewModel.Provider((requireActivity().applicationContext as MyApplication).beanRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBeanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //when an image file is selected, convert it to byteArray and store it in variable bytes
        //decode the bytes to bitmap and display the image on ivBeanImage
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                bytes = inputStream?.readBytes()
                inputStream?.close()

                val bitmap = bytes?.let { it1 -> BitmapFactory.decodeByteArray(bytes, 0, it1.size) }
                binding.ivBeanImage.setImageBitmap(bitmap)
            }
        }

        binding.run {
            //when the this image image is clicked, opens gallery
            ivBeanImage.setOnClickListener {
                filePickerLauncher.launch("image/*")
            }

            //when add btn is clicked, add bean to room db and go back to the previous fragment
            btnAdd.setOnClickListener {
                val title = binding.etTitle.text.toString()
                val subtitle = binding.etSubtitle.text.toString()
                val taste = binding.etTaste.text.toString()
                val details = binding.etDetails.text.toString()
                val body = binding.sliderBody.value.toInt()
                val aroma = binding.sliderAroma.value.toInt()
                val caffeine = binding.sliderCaffeine.value.toInt()

                val bean = Bean(null, title, subtitle, taste, details, body, aroma, caffeine, bytes)
                viewModel.addBean(bean)
                val bundle = Bundle()
                bundle.putBoolean("refresh", true)
                setFragmentResult("from_add_bean", bundle)
                NavHostFragment.findNavController(this@AddBeanFragment).popBackStack()
            }
        }
    }
}