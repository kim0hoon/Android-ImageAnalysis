package com.example.imageanalyzer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.imageanalyzer.databinding.ItemPictureBinding

class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.PictureViewHolder>() {

    private val diffUtil = object: DiffUtil.ItemCallback<Picture>() {
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture) = oldItem.id==newItem.id
        override fun areContentsTheSame(oldItem: Picture, newItem: Picture) = oldItem==newItem
    }

    val dataList = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):PictureViewHolder {
        val binding = ItemPictureBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder:PictureViewHolder, position: Int) {
        holder.bind(dataList.currentList[position])
    }

    override fun getItemCount(): Int = dataList.currentList.size

    class PictureViewHolder(private val binding: ItemPictureBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(data:Picture){
            binding.ivImage.setImageBitmap(data.bitMap)
        }
    }
}