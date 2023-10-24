package com.khawi.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.bumptech.glide.Glide
import com.khawi.R
import java.text.SimpleDateFormat
import java.util.Date

fun ImageView.loadImage(context: Context, imageUrl: String?) {
    Glide.with(context).load(imageUrl).placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder).into(this)
}

fun TextView.TimerCode(
    content: String,
    endContent: String,
    timeSeconds: Long,
    countDownTimer: Long = 1000,
    format: String = "mm:ss"
) {
    object : CountDownTimer(timeSeconds * 1000, countDownTimer) {
        @SuppressLint("SimpleDateFormat")
        override fun onTick(millisUntilFinished: Long) {
            val date = Date(millisUntilFinished)
            val time = SimpleDateFormat(format).format(date) ?: ""
            val timerText = "$content $time"
            this@TimerCode.text = timerText
        }

        override fun onFinish() {
            this@TimerCode.text = endContent
        }
    }.start()
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun NavController.safeNavigate(
    @IdRes currentDestinationId: Int,
    @IdRes id: Int,
    args: Bundle? = null
) {
    if (currentDestinationId == currentDestination?.id) {
        navigate(id, args)
    }
}