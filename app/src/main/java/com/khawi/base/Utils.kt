package com.khawi.base

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.model.Order
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.MalformedURLException
import java.net.NetworkInterface
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale


fun Activity.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Fragment.hideKeyboard() {
    view?.let { requireActivity().hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Context.openKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Activity.savePreference(key: String, value: String) {
    val sharedPref = this.getSharedPreferences("MODE_PRIVATE", MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putString(key, value)
        apply()
    }
}

fun Activity.savePreference(key: String, value: Long) {
    val sharedPref = this.getSharedPreferences("MODE_PRIVATE", MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putLong(key, value)
        apply()
    }
}

fun Activity.savePreference(key: String, value: Boolean) {
    val sharedPref = this.getSharedPreferences("MODE_PRIVATE", MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
        putBoolean(key, value)
        apply()
    }
}

fun Context.getPreference(key: String): String {
    val sharedPref = this.getSharedPreferences("MODE_PRIVATE", MODE_PRIVATE)
    return sharedPref.getString(key, "") ?: ""
}

fun Context.getPreferenceBoolean(key: String): Boolean {
    val sharedPref = this.getSharedPreferences("MODE_PRIVATE", MODE_PRIVATE)
    return sharedPref.getBoolean(key, false)
}

fun Context.getPreferenceLong(key: String): Long {
    val sharedPref = this.getSharedPreferences("MODE_PRIVATE", MODE_PRIVATE)
    return sharedPref.getLong(key, 0L)
}

fun Context.setLocale(locale: Locale) {
    val resources = this.resources
    val configuration = resources.configuration
    val displayMetrics = resources.displayMetrics
    configuration.setLocale(locale)
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        this.createConfigurationContext(configuration)
    } else {
        resources.updateConfiguration(configuration, displayMetrics)
    }
}

fun Context.showKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Double.zeroPattern(): String {
    return String.format(Locale.ENGLISH, "%.2f", this)
}


fun String.showToastMessage(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun String.showAlertMessage(
    context: Context,
    title: String,
    confirmText: String,
    cancelText: String? = null,
    type: Int,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val alert = SweetAlertDialog(context, type)
    alert.titleText = title
    alert.contentText = this
    alert.confirmText = confirmText
    alert.setConfirmClickListener {
        onConfirmClick.invoke()
        it.dismissWithAnimation()
    }
    if (!cancelText.isNullOrEmpty()) {
        alert.setCancelButton(cancelText) {
            onCancelClick.invoke()
            it.dismissWithAnimation()
        }
    }
    if (!alert.isShowing)
        alert.show()
    alert.getButton(SweetAlertDialog.BUTTON_CONFIRM).backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main))
    if (!cancelText.isNullOrEmpty())
        alert.getButton(SweetAlertDialog.BUTTON_CANCEL).backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))

}

fun String.errorMessage(context: Context) {
    this.showAlertMessage(
        context = context,
        title = context.getString(R.string.error),
        confirmText = context.getString(R.string.Ok),
        type = SweetAlertDialog.ERROR_TYPE,
        onCancelClick = {

        },
        onConfirmClick = {

        }
    )
}

fun <T> String.fromJson(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)


fun <T> LiveData<T?>.observeOnce(observer: Observer<T?>) {
    observeForever(object : Observer<T?> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
        .isConnected
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return activeNetInfo != null && activeNetInfo.isConnectedOrConnecting

//    try {
//        val ipAddr: InetAddress = InetAddress.getByName("google.com")
//        //You can replace it with your name
//        (!ipAddr.equals("") /*&& isNetworkAvailable(context)*/)
//    } catch (e: Exception) {
//        false
//    }
}

fun Activity.activateLockScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@activateLockScreen, null)
        }
    }
}

fun Activity.changeLanguage(language: String) {
    savePreference(language_key, language)
    finishAffinity()
    startActivity(this.intent)

    overridePendingTransition(0, 0)
//    val intent = Intent(
//        this,
//        MainActivity::class.java
//    )
//    val mPendingIntent = PendingIntent.getActivity(
//        this,
//        0,
//        intent,
//        PendingIntent.FLAG_CANCEL_CURRENT
//    )
//    val mgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
//    exitProcess(0)
}

fun Context.getIPAddress(): String {
    val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
    return android.text.format.Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
}

fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr: String = addr.hostAddress ?: ""
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim)
                                .toUpperCase()
                        }
                    }
                }
            }
        }
    } catch (ignored: Exception) {
        ignored.printStackTrace()
    }
    return ""
}

fun String.validateEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

//inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
//    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
//}

fun Context.initLoading(): KProgressHUD {
    return KProgressHUD.create(this)
        .setCustomView(LayoutInflater.from(this).inflate(R.layout.loading, null as ViewGroup?))
        .setBackgroundColor(Color.TRANSPARENT)
    //.setLabel(this.getString(R.string.please_wait))
}

fun KProgressHUD?.showDialog() {
    this?.show()?.setCancellable(false)
}

fun KProgressHUD?.hideDialog() {
    if (this?.isShowing == true)
        this.dismiss()
}

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

fun Long.getTimeAgo(context: Context, date: String): String? {
    var time = this
    if (time < 1000000000000L) {
        time *= 1000
    }
    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }
    val diff = now - time
    return if (diff < MINUTE_MILLIS) {
        context.getString(R.string.just_now)
    } else if (diff < 2 * MINUTE_MILLIS) {
        context.getString(R.string.a_minute_ago)
    } else if (diff < 50 * MINUTE_MILLIS) {
        "${(diff / MINUTE_MILLIS)} ${context.getString(R.string.minute_ago)}"
    } else if (diff < 90 * MINUTE_MILLIS) {
        context.getString(R.string.an_hour_ago)
    } else if (diff < 24 * HOUR_MILLIS) {
        "${(diff / HOUR_MILLIS)} ${context.getString(R.string.hours_ago)}"
    } else if (diff < 48 * HOUR_MILLIS) {
        context.getString(R.string.yesterday)
    } else if (diff < 7 * DAY_MILLIS) {
        "${(diff / DAY_MILLIS)} ${context.getString(R.string.days_ago)}"
    } else {
        date
    }
}

fun String.isHttps(): Boolean {
    return try {
        val url = URL(this)
        val scheme = url.protocol
        "https".equals(scheme, ignoreCase = true) || "http".equals(scheme, ignoreCase = true)
    } catch (e: MalformedURLException) {
        e.printStackTrace()
        // Handle the MalformedURLException as needed.
        // It means the input URL is not a valid URL.
        false
    }
}

val requestedPermissions = arrayOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA
)

fun Context.checkSelfPermission(): Boolean {
    return !(ContextCompat.checkSelfPermission(
        this,
        requestedPermissions[0]
    ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                requestedPermissions[1]
            ) != PackageManager.PERMISSION_GRANTED)
}


fun Context.deliverBottomSheet(
    layoutInflater: LayoutInflater,
    container: ViewGroup,
    successText: String,
    buttonText: String,
    clickAction: () -> Unit
) {
    val bottomSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
    val rootView =
        layoutInflater.inflate(R.layout.bottomsheet_success_request, container, false)
    bottomSheet.setContentView(rootView)

    val successContent = rootView.findViewById<TextView>(R.id.successContent)

    val doneBtn = rootView.findViewById<TextView>(R.id.doneBtn)

    doneBtn.setOnClickListener {
        clickAction.invoke()
        bottomSheet.dismiss()
    }

    successContent.text = successText
    doneBtn.text = buttonText
    bottomSheet.show()
}

fun LatLng.getAddress(context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(
        this.latitude,
        this.longitude,
        1
    )
    var address = ""
    if (addresses?.isNotEmpty() == true)
        address = addresses[0].getAddressLine(0) ?: ""
    return address
}

fun LatLng.getAddressTitle(context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(
        this.latitude,
        this.longitude,
        1
    )
    val address = StringBuilder()
    if (addresses?.isNotEmpty() == true) {
        if ((addresses[0].locality ?: "").isNotEmpty())
            address.append(addresses[0].locality ?: "").append(" ")
        if ((addresses[0].adminArea ?: "").isNotEmpty())
            address.append(addresses[0].adminArea ?: "")
    }
    return address.toString()
}

fun String.formatDate(): String? {
    val formatZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    return formatZ.parse(this)?.let { date -> format.format(date) }
}

fun String.formatDateTime(): String? {
    val formatZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)

    return formatZ.parse(this)?.let { date -> format.format(date) }
}

fun Activity.startTrackingService(order: Order?, userId:String) {
    val serviceIntent = Intent(this, LocationService::class.java)
    serviceIntent.putExtra(LocationService.orderKey, order)
    serviceIntent.putExtra(LocationService.userIdKey, userId)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(serviceIntent)
    } else {
        startService(serviceIntent)
    }
}

fun Activity.stopTrackingService() {
    val serviceIntent = Intent(this, LocationService::class.java)
    stopService(serviceIntent)
}

fun String.checkTime(tripTimeZone: TextView): String {
    return if (this.contains("pm")) {
        tripTimeZone.text = "م"
        this.replace("pm", "")
    } else if (this.contains("am")) {
        tripTimeZone.text = "ص"
        this.replace("am", "")
    }else if (this.contains("م")) {
        tripTimeZone.text = "م"
        this.replace("م", "")
    } else if (this.contains("ص")) {
        tripTimeZone.text = "ص"
        this.replace("ص", "")
    } else {
        this
    }
}