package com.example.imageanalyzer

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.imageanalyzer.databinding.ActivityMainBinding
import com.example.imageanalyzer.ml.LiteModelAiyVisionClassifierFoodV11
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val albumAdapter = AlbumAdapter(this)
    private val dataList = MutableLiveData(mutableListOf<PictureItem>()).apply {
        observe(this@MainActivity) {
            albumAdapter.dataList.submitList(it.toMutableList())
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

    private lateinit var model: LiteModelAiyVisionClassifierFoodV11
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
        model = LiteModelAiyVisionClassifierFoodV11.newInstance(this)
        val resolver = contentResolver
        val query = arrayOf(MediaStore.Images.Media._ID)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, query, null, null, null)?.run {
            CoroutineScope(IO).launch {
                while (moveToNext()) {
                    val id = getLong(getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val contentUri = "$uri/$id"
                    val item = analysisPicture(Picture(id, contentUri))
                    dataList.postValue((dataList.value ?: mutableListOf()).apply { add(item) })
                }
                close()
                model.close()
            }
        }
    }

    private fun checkPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    private fun analysisPicture(picture: Picture): PictureItem {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        contentResolver,
                        Uri.parse(picture.uri)
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(picture.uri))
            }.run { copy(Bitmap.Config.ARGB_8888, true) }
            val image = TensorImage.fromBitmap(bitmap)

            val outputs = model.process(image)
            val probability = outputs.probabilityAsCategoryList
            return PictureItem(picture,
                probability.filter { it.score > 0.1f }.toMutableList()
                    .apply { sortByDescending { it.score } }
                    .joinToString("\n") { "${it.displayName} ${it.label} ${it.score}" },
                0.0
            )
        } catch (e: Exception) {
            Log.e("analysisPicture", "${e.message}")
            return PictureItem(picture, "", 0.0)
        }
    }

}