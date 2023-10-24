package com.khawi.base
interface Downloader {
    fun downloadFile(url: String, name: String): Long
}