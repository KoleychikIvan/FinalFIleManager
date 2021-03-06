package com.koleychik.core_files

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.koleychik.core_files.api.FilesRepository
import com.koleychik.core_files.extensions.*
import com.koleychik.models.fileCarcass.FileCarcass
import com.koleychik.models.fileCarcass.MusicModel
import com.koleychik.models.fileCarcass.document.DocumentModel
import com.koleychik.models.fileCarcass.document.getTypeOfDocument
import com.koleychik.models.fileCarcass.media.ImageModel
import com.koleychik.models.fileCarcass.media.VideoModel
import com.koleychik.module_injector.AppConstants.TAG
import javax.inject.Inject


@SuppressLint("Recycle")
internal class FilesRepositoryImpl @Inject constructor(private val context: Context) :
    FilesRepository {

    private val contentUri = "external"

    override fun getImages(): List<ImageModel> {
        val uriExternal: Uri = ImagesStorage.EXTERNAL_CONTENT_URI
        val listRes = mutableListOf<ImageModel>()
        val sorterOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = queryContentResolver(
            uriExternal,
            imagesProjections,
            sortedOrder = sorterOrder
        ) ?: return emptyList()

        var id: Long
        while (cursor.moveToNext()) {
            id = cursor.getLong(0)
            listRes.add(
                ImageModel(
                    id = id,
                    name = cursor.getString(1),
                    uri = Uri.withAppendedPath(uriExternal, id.toString()),
                    sizeAbbreviation = context.getSizeAbbreviation(cursor.getLong(2)),
                    dateAdded = cursor.getLong(3)
                )
            )
        }
        cursor.close()
        return listRes
    }

    override fun getDocuments(): List<DocumentModel> {
        val listRes = mutableListOf<DocumentModel>()
        val uriExternal: Uri = MediaStore.Files.getContentUri(contentUri)
        val cursor = queryContentResolver(uriExternal, documentsProjections) ?: return emptyList()

        while (cursor.moveToNext()) {
            val name = cursor.getString(1)
            listRes.add(
                DocumentModel(
                    name = name,
                    uri = Uri.withAppendedPath(uriExternal, cursor.getString(0)),
                    sizeAbbreviation = context.getSizeAbbreviation(cursor.getLong(2)),
                    dateAdded = cursor.getLong(3),
                    getTypeOfDocument(name)
                )
            )
        }
        cursor.close()
        return listRes
    }

    override fun getMusic(): List<MusicModel> {
        val uriExternal = AudioStorage.EXTERNAL_CONTENT_URI
        val listRes = mutableListOf<MusicModel>()
        val cursor = context.contentResolver.query(
            uriExternal,
            audioProjections,
            null,
            null,
            null
        ) ?: return emptyList()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            listRes.add(
                MusicModel(
                    id,
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getLong(5),
                    Uri.withAppendedPath(uriExternal, id.toString()),
                    context.getSizeAbbreviation(cursor.getLong(6)),
                    cursor.getLong(7)
                )
            )
        }

        return listRes
    }

    override fun getVideo(): List<VideoModel> {
        val uriExternal = VideoStorage.EXTERNAL_CONTENT_URI
        val listRes = mutableListOf<VideoModel>()

        val cursor = queryContentResolver(uriExternal, videoProjections) ?: return emptyList()

        var id: Long

        while (cursor.moveToNext()) {
            id = cursor.getLong(0)
            listRes.add(
                VideoModel(
                    id = id,
                    name = cursor.getString(1),
                    uri = Uri.withAppendedPath(uriExternal, id.toString()),
                    duration = cursor.getLong(2),
                    sizeAbbreviation = context.getSizeAbbreviation(cursor.getLong(3)),
                    dateAdded = cursor.getLong(4)
                )
            )
        }
        cursor.close()
        return listRes
    }

    override fun gelFilesFromFolder(path: String): List<FileCarcass> {
        val selection = "relative_path = '$path'"
//        val selection = "_data LIKE '%.jpg'"

        val cursor = queryContentResolver(
            MediaStore.Files.getContentUri(contentUri),
            allFilesFromFolderProjections,
            selection = selection
        ) ?: return emptyList()

        val listRes = mutableListOf<FileCarcass>()
//        while (cursor.moveToNext()) listRes.add(
//            FileCarcass(
//                cursor.getLong(0),
//                cursor.getString(1),
//                relativePath = cursor.getString(2)
//            )
//        )
        cursor.close()
        return listRes
    }

    override fun delete(model: FileCarcass) {
        val file = model.uri.toFile()
        if (file.exists()) {
            Log.d(TAG, "file exists")
            if (file.delete()) Log.d(TAG, "file successful deleted")
            else Log.d(TAG, "fail to delete file")
        } else Log.d(TAG, "file doesn't exist")
    }

    override fun openFile(model: FileCarcass) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = model.uri
        val intentOpen = Intent.createChooser(intent, "Choose an application to open with:")
        ContextCompat.startActivity(context, intentOpen, Bundle())
    }

    private fun queryContentResolver(
        uri: Uri,
        projections: Array<out String>,
        selection: String? = null,
        selectionArgs: Array<out String>? = null,
        sortedOrder: String? = null
    ) = context.contentResolver.query(
        uri,
        projections,
        selection,
        selectionArgs,
        sortedOrder
    )


    fun test() {

    }

}