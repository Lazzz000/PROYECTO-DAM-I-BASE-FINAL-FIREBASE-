package com.nexushardware.app.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nexushardware.app.R
import com.nexushardware.app.domain.model.Slide

class SliderAdapter(private val slides: List<Slide>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById(R.id.imageSlide)
        val title: TextView = itemView.findViewById(R.id.titleSlide)
        val desc: TextView = itemView.findViewById(R.id.descSlide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slide, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val slide = slides[position]
        holder.imageView.setImageResource(slide.image)
        holder.title.text = slide.title
        holder.desc.text = slide.desc
    }

    override fun getItemCount(): Int = slides.size
}