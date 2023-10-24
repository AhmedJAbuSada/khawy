package com.khawi.base

interface InterfaceTimer {
    fun endTime()
    fun onUpdate(timer: String, millis: Long)
}