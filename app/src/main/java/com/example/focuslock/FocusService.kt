package com.example.focuslock

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.savedstate.*

class FocusService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: ComposeView
    private var timer: CountDownTimer? = null
    private lateinit var lifecycleOwner: FakeLifecycleOwner

    // UI Güncelleme
    private var timeLeftText = mutableStateOf("00:00")

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeInMillis = intent?.getLongExtra("TIME_IN_MILLIS", 60000L) ?: 60000L
        showOverlay()
        startTimer(timeInMillis)
        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification() {
        val channelId = "FocusLockChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Focus Lock", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Odaklanma Modu")
            .setContentText("Odaklanma devam ediyor, telefon kilitli.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
        }
    }

    private fun showOverlay() {
        lifecycleOwner = FakeLifecycleOwner()

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            setContent {
                // Kilit ekranında da aynı rahatlatıcı su animasyonu
                val infiniteTransition = rememberInfiniteTransition(label = "lock_ripple")
                val rippleRadius by infiniteTransition.animateFloat(
                    initialValue = 0f, targetValue = 500f,
                    animationSpec = infiniteRepeatable(animation = tween(5000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart),
                    label = "radius"
                )
                val rippleAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 0f,
                    animationSpec = infiniteRepeatable(animation = tween(5000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart),
                    label = "alpha"
                )

                val bgBrush = Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF000000)))

                Box(modifier = Modifier.fillMaxSize().background(bgBrush).drawBehind {
                    drawCircle(color = Color(0xFF38BDF8).copy(alpha = rippleAlpha), radius = rippleRadius)
                }) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = timeLeftText.value,
                            color = Color.White,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "İyi bir başlangıç, işin yarısıdır.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            Icons.Rounded.SelfImprovement,
                            contentDescription = "Zen",
                            tint = Color(0xFF38BDF8).copy(alpha = 0.8f),
                            modifier = Modifier.size(180.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Acil Durum Çağrısı",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 50.dp)
                        )
                    }
                }
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlayView, params)
    }

    private fun startTimer(timeInMillis: Long) {
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timeLeftText.value = String.format("%02d:%02d", minutes, seconds)
            }
            override fun onFinish() { stopSelf() }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        if (::overlayView.isInitialized) windowManager.removeView(overlayView)
        if (::lifecycleOwner.isInitialized) lifecycleOwner.stop()
    }
}

// Lifecycle sınıfı
class FakeLifecycleOwner : SavedStateRegistryOwner, ViewModelStoreOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val lifecycle: Lifecycle get() = lifecycleRegistry

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
    fun stop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }
}