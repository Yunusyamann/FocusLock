package com.example.focuslock

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(this)) }

        if (!hasOverlayPermission) {
            PermissionScreen { hasOverlayPermission = Settings.canDrawOverlays(this) }
        } else {
            AnimatedZenScreen()
        }
    }

    @Composable
    fun PermissionScreen(onPermissionGranted: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.SelfImprovement, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("İzin Gerekli", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Odaklanma modunun cihazı kilitleyebilmesi için 'Diğer uygulamaların üzerinde göster' iznine ihtiyacı var.", color = Color.LightGray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
            ) { Text("Ayarlara Git ve İzin Ver", color = Color.Black) }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { onPermissionGranted() }) { Text("İzni Verdim, Devam Et", color = Color(0xFF38BDF8)) }
        }
    }

    @Composable
    fun AnimatedZenScreen() {
        val timeOptions = listOf(20, 30, 45, 60, 90)
        var selectedMinutes by remember { mutableStateOf(20) }

        // --- ANİMASYONLAR ---
        val infiniteTransition = rememberInfiniteTransition(label = "zen_animations")

        // 1. Su Dalgası Animasyonu (Büyüme ve Kaybolma)
        val rippleRadius by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 400f,
            animationSpec = infiniteRepeatable(animation = tween(4000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart),
            label = "ripple_radius"
        )
        val rippleAlpha by infiniteTransition.animateFloat(
            initialValue = 0.5f, targetValue = 0f,
            animationSpec = infiniteRepeatable(animation = tween(4000, easing = LinearOutSlowInEasing), repeatMode = RepeatMode.Restart),
            label = "ripple_alpha"
        )

        // 2. Süzülen Ay Animasyonu (Yukarı aşağı hareket)
        val moonY by infiniteTransition.animateFloat(
            initialValue = -10f, targetValue = 10f,
            animationSpec = infiniteRepeatable(animation = tween(3000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
            label = "moon_y"
        )

        // Arka Plan Renk Geçişi (Gece)
        val bgBrush = Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF000000)))

        Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
            // Ay çizimi
            Box(
                modifier = Modifier.offset(x = 60.dp, y = (100 + moonY).dp).size(60.dp)
                    .clip(CircleShape).background(Color(0xFFFDE047).copy(alpha = 0.8f))
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(120.dp))

                // Animasyonlu Zen İkonu (Su dalgaları bu Box'ın etrafında çizilir)
                Box(
                    modifier = Modifier.size(150.dp).drawBehind {
                        drawCircle(color = Color(0xFF38BDF8).copy(alpha = rippleAlpha), radius = rippleRadius)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.SelfImprovement, contentDescription = "Zen", tint = Color(0xFF38BDF8), modifier = Modifier.size(120.dp))
                }

                Spacer(modifier = Modifier.height(60.dp))

                Text("Telefonunu bir kenara bırak", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Önemli bir şeye odaklan", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(40.dp))

                // Süre Seçim Butonları
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    timeOptions.forEach { minutes ->
                        val isSelected = selectedMinutes == minutes
                        Box(
                            modifier = Modifier.size(60.dp).clip(CircleShape)
                                .background(if (isSelected) Color(0xFF0EA5E9) else Color(0xFF1E293B))
                                .clickable { selectedMinutes = minutes },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$minutes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.LightGray)
                                Text("dk", fontSize = 12.sp, color = if (isSelected) Color.White else Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Test için 1 Dakika Butonu
                TextButton(onClick = {
                    startFocusService(1 * 60 * 1000L)
                    finish()
                }) {
                    Text("Dene (1 dk)", color = Color(0xFF38BDF8), fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Ana Başlat Butonu
                Button(
                    onClick = {
                        startFocusService(selectedMinutes * 60 * 1000L)
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Odaklanma Modunu Aç", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    private fun startFocusService(timeInMillis: Long) {
        val serviceIntent = Intent(this, FocusService::class.java).apply { putExtra("TIME_IN_MILLIS", timeInMillis) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}