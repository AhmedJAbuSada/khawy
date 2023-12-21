package com.khawi.base

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.khawi.model.Order
import com.khawi.model.TrackingLocation
import com.khawi.model.db.user.UserRepository
import javax.inject.Inject

class LocationService : Service() {

    companion object {
        const val CHANNEL_ID = "location_service_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_LOCATION_UPDATE = "com.khawi.android.ACTION_LOCATION_UPDATE"
        const val EXTRA_LOCATION_DATA = "extra_location_data"
        const val ACTION_STOP_SERVICE = "com.khawi.android.ACTION_STOP_SERVICE"
        const val orderKey = "order"
        const val userIdKey = "user_id"
    }

    private val binder = LocationServiceBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var order: Order? = null
    private var userId: String? = null


    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(com.khawi.R.string.app_name))
            .setContentText(getString(com.khawi.R.string.tracking_location))
            .setSmallIcon(com.khawi.R.mipmap.ic_launcher)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        requestLocationUpdates()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        order = intent?.getParcelableExtra(orderKey) as? Order
        userId = intent?.getStringExtra(userIdKey)
        val action = intent?.action
        when (action) {
            ACTION_STOP_SERVICE -> {
                stopSelf() // Stop the service
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class LocationServiceBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    handleNewLocation(it)
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000 // 5 seconds
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun handleNewLocation(location: Location) {
        val database = FirebaseDatabase.getInstance().getReference(trackingTable)
        database.child(order?.id ?: "").setValue(
            TrackingLocation(
                lastUpdate = System.currentTimeMillis()/1000,
                lat = location.latitude,
                lng = location.longitude,
                orderId = order?.id ?: "",
                status = order?.status ?: "",
                userId = userId
            )
        )

        val intent = Intent(ACTION_LOCATION_UPDATE)
        intent.putExtra(EXTRA_LOCATION_DATA, location)
        sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}