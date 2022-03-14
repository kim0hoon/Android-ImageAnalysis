package com.example.imageanalyzer

data class Picture(val id:Long, val uri:String)

data class PictureItem(val picture:Picture, val type:String, val time:Double)