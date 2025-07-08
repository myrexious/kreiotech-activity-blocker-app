package com.example.kreiotech.activityblocker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kreiotech.activityblocker.ui.theme.DarkBlackColorScheme
import com.example.kreiotech.activityblocker.ui.theme.KreioTechActivityBlockerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KreioTechActivityBlockerTheme {
                MainScreen()
            }
        }
    }
}

fun isAccessibilityServiceEnabled(
    context: Context, service: Class<out AccessibilityService>
): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = Settings.Secure.getString(
        context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServices)
    val expectedComponentName = "${context.packageName}/${service.name}"
    while (colonSplitter.hasNext()) {
        val componentName = colonSplitter.next()
        if (componentName.equals(expectedComponentName, ignoreCase = true)) {
            return true
        }
    }
    return false
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("blocker_prefs", Context.MODE_PRIVATE)
    val isServiceEnabled = isAccessibilityServiceEnabled(context, ActivityBlockerAccessibilityService::class.java)

    // Use state variables for toggles
    var blockInstagramReelsEnabled by remember {
        mutableStateOf(prefs.getBoolean("block_instagram_reels_enabled", false))
    }
    var blockInstagramExploreEnabled by remember {
        mutableStateOf(prefs.getBoolean("block_instagram_explore_enabled", false))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlackColorScheme.primary)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isServiceEnabled) {
            Text(
                text = "Accessibility Service Not Enabled",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    Toast.makeText(
                        context, "Find 'KreioTech-ActivityBlocker' in the list and enable it.", Toast.LENGTH_LONG
                    ).show()
                }
            ) {
                Text("Enable Accessibility Service")
            }
        } else {
            Text(
                text = "Accessibility Service Access Granted",
                textAlign = TextAlign.Center,
                color = Color(0xFF4CAF50), // Green color
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Blocker toggle area
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Block Instagram Reels", color = DarkBlackColorScheme.secondary)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                enabled = isServiceEnabled, // Only enabled if service is enabled
                checked = isServiceEnabled && blockInstagramReelsEnabled,
                onCheckedChange = {
                    if (isServiceEnabled) {
                        blockInstagramReelsEnabled = it
                        prefs.edit().putBoolean("block_instagram_reels_enabled", it).apply()
                    } else {
                        Toast.makeText(
                            context, "Please enable Accessibility Service for blocking to work.", Toast.LENGTH_LONG
                        ).show()
                    }
                },
                colors = SwitchDefaults.colors()
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Block Instagram Explore", color = DarkBlackColorScheme.secondary)
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Switch(
                enabled = isServiceEnabled, // Only enabled if service is enabled
                checked = isServiceEnabled && blockInstagramExploreEnabled,
                onCheckedChange = {
                    if (isServiceEnabled) {
                        blockInstagramExploreEnabled = it
                        prefs.edit().putBoolean("block_instagram_explore_enabled", it).apply()
                    } else {
                        Toast.makeText(
                            context, "Please enable Accessibility Service for blocking to work.", Toast.LENGTH_LONG
                        ).show()
                    }
                },
                colors = SwitchDefaults.colors()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    KreioTechActivityBlockerTheme {
        MainScreen()
    }
}