package com.example.imageanalyzer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
        private val uriTextView = binding.tvId
        private val typeTextView = binding.tvType
        private val timeTextView = binding.tvTime
        fun bind(data: PictureItem) {
            val idText = "ID : ${data.picture.id}"
            val timeText = "소요시간 : ${data.time}"
            val typeText = "분석결과\n${data.type}"
            uriTextView.text = idText
            timeTextView.text = timeText
            typeTextView.text = typeText
            Glide.with(context).load(data.picture.uri).placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_error).fitCenter().into(binding.ivImage)
        }
    }
}