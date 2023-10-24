package com.khawi.base

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.khawi.R

class AndroidDownloader(
    private val context: Context
) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String, name: String): Long {
        val request = DownloadManager.Request(url.toUri())
            //.setMimeType("image/jpeg")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(context.getString(R.string.downloading))
            //.addRequestHeader("Authorization", "Bearer <token>")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                context.getString(R.string.app_name) + "/$name"
            )
        return downloadManager.enqueue(request)
    }
}