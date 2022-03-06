package com.example.imageanalyzer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imageanalyzer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val albumAdapter = AlbumAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
    }

    private fun initView(){
        binding.rvAlbumList.adapter=albumAdapter
    }

    private fun initData(){

    }
}