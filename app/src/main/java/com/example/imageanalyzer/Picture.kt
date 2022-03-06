package com.example.imageanalyzer

import android.graphics.Bitmap

data class Picture(val id:Long, val uri:String)

data class PictureItem(val picture:Picture, val type:String, val time:Double)