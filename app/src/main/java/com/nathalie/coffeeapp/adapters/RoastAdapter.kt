package com.nathalie.coffeeapp.adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.nathalie.coffeeapp.data.model.Bean
import com.nathalie.coffeeapp.data.model.Roast
import com.nathalie.coffeeapp.databinding.ItemLayoutRoastBinding
import com.nathalie.coffeeapp.ui.MainFragmentDirections

class RoastAdapter(
    private var items: List<Roast>,
    val onClick: (id: Long) -> Unit
) : RecyclerView.Adapter<RoastAdapter.ItemRoastHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRoastHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLayoutRoastBinding.inflate(layoutInflater, parent, false)
        return ItemRoastHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemRoastHolder, position: Int) {
        val item = items[position]

        holder.binding.run {
            tvTitle.text = item.title
            tvTitle2.text = item.title
            tvDetails.text = item.details

            item.image?.let { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(item.image, 0, bytes.size)
                ivRoastImage.setImageBitmap(bitmap)
            }
            val components =
                arrayOf(ivDarkOverlay, tvTitle, tvTitle2, tvDetails, btnEdit)
            cvRoast.setOnClickListener {
                components.forEach {
                    if (it.visibility == View.GONE) it.visibility = View.VISIBLE
                    else it.visibility = View.GONE
                }
            }

            btnEdit.setOnClickListener {
                item.id?.let { it1 -> onClick(it1) }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setRoasts(items: List<Roast>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ItemRoastHolder(val binding: ItemLayoutRoastBinding) :
        RecyclerView.ViewHolder(binding.root)

}