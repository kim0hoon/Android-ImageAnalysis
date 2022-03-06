package com.example.imageanalyzer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import com.example.imageanalyzer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val albumAdapter = AlbumAdapter(this)
    private val dataList = MutableLiveData(mutableListOf<PictureItem>()).apply {
        observe(this@MainActivity) {
            albumAdapter.dataList.submitList(it)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Permission", "권한 허용")
                initView()
                initData()
            } else {
                Log.d("Permission", "권한 거부")
                Toast.makeText(this, "저장소 접근 권한이 필요합니다", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
    }

    private fun initView() {
        binding.rvAlbumList.adapter = albumAdapter
    }

    private fun initData() {
        val resolver = contentResolver
        val query = arrayOf(MediaStore.Images.Media._ID)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, query, null, null, null)?.run {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val contentUri = "$uri/$id"
                (dataList.value ?: mutableListOf()).add(analysisPicture(Picture(id, contentUri)))
            }
            close()
        }
    }

    private fun checkPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun analysisPicture(picture: Picture): PictureItem {
        return PictureItem(picture,"",0.0)
    }
}