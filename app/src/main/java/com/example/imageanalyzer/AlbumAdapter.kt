package com.example.imageanalyzer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imageanalyzer.databinding.ItemPictureBinding

class AlbumAdapter(val context: Context) : RecyclerView.Adapter<AlbumAdapter.PictureViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<PictureItem>() {
        override fun areItemsTheSame(oldItem: PictureItem, newItem: PictureItem) =
            oldItem.picture.id == newItem.picture.id

        override fun areContentsTheSame(oldItem: PictureItem, newItem: PictureItem) =
            oldItem == newItem
    }

    val dataList = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val binding = ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(dataList.currentList[position])
    }

    override fun getItemCount(): Int = dataList.currentList.size

    inner class PictureViewHolder(private val binding: ItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val uriTextView = binding.tvUri
        private val typeTextView = binding.tvType
        private val timeTextView = binding.tvTime
        fun bind(data: PictureItem) {
            uriTextView.text = "uri : uri"
            typeTextView.text = "종류 : 종류"
            timeTextView.text = "소요시간 : 0.000212312s"
            Glide.with(context).load(data.picture.uri).placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_error).fitCenter().into(binding.ivImage)
        }
    }
}