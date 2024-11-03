package com.miempresa.acua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage


class MainActivity : ComponentActivity() {
    private lateinit var mqttHandler: MqttHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mqttHandler = MqttHandler()
        mqttHandler.connect("tcp://broker.hivemq.com:1883","android_app_client")
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                AquaponicsMonitor(mqttHandler)
            }
        }
    }

    override fun onDestroy() {
        mqttHandler.disconnect()
        super.onDestroy()
    }
}
@Composable
fun AquaponicsMonitor(mqttHandler: MqttHandler) {
    var temperatura = remember { mutableStateOf("--.-") }
    var humedad = remember { mutableStateOf("--.-") }

    LaunchedEffect(Unit) {
        mqttHandler.subscribe("lectura/temperatura")
        mqttHandler.subscribe("lectura/humedad")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Título principal centrado
            Text(
                text = "Sistema Acuapónico",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 24.dp)  // Added spacing after title
            )

            // Tarjeta de Temperatura
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),  // Reduced height
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D2D2D)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,  // Center all content
                    verticalArrangement = Arrangement.Center  // Center vertically
                ) {
                    // Título
                    Text(
                        text = "Temperatura",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFFF6B00)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)  // Added spacing
                    )

                    // Valor centrado
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center  // Center horizontally
                    ) {
                        Text(
                            text = temperatura.value,
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Light
                            )
                        )
                        Text(
                            text = "°C",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = Color.White,
                            ),
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                    }
                }
            }

            // Tarjeta de Humedad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),  // Reduced height
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D2D2D)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,  // Center all content
                    verticalArrangement = Arrangement.Center  // Center vertically
                ) {
                    // Título
                    Text(
                        text = "Humedad",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF0088FF)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)  // Added spacing
                    )

                    // Valor centrado
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center  // Center horizontally
                    ) {
                        Text(
                            text = humedad.value,
                            style = MaterialTheme.typography.displayLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Light
                            )
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = Color.White,
                            ),
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        mqttHandler.setMessageListener { topic, message ->
            when (topic) {
                "lectura/temperatura" -> temperatura.value = String(message.payload)
                "lectura/humedad" -> humedad.value = String(message.payload)
            }
        }
        onDispose { }
    }
}