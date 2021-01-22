package com.koleychik.models

import com.koleychik.models.fileCarcass.DocumentModel
import com.koleychik.models.fileCarcass.FileCarcass
import com.koleychik.models.fileCarcass.FolderModel
import com.koleychik.models.fileCarcass.MusicModel
import com.koleychik.models.fileCarcass.media.ImageModel
import com.koleychik.models.fileCarcass.media.VideoModel

fun FileCarcass.getWeight(firstLatter: Char): Int {

    var weight = 1

    when (this) {
        is MusicModel -> {
            println(100)
            weight *= 100
        }
        is ImageModel -> {
            println(150)
            weight *= 150
        }
        is VideoModel -> {
            println(200)
            weight *= 200
        }
        is DocumentModel -> {
            println(50)
            weight *= 50
        }
        is FolderModel -> {
            println(300)
            weight *= 300
        }
        else -> {
            println(1)
            weight *= 1
        }
    }

    println("firstLatter.toInt() = ${firstLatter.toInt()}")
    return weight * firstLatter.toInt()

} 