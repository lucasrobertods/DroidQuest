package com.lrds.androidroadapp

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lrds.androidroadapp.platform.AndroidPlatformHost

class MainActivity : ComponentActivity() {
    private var pendingExportJson: String? = null
    private var pendingExportCallback: ((Boolean, String) -> Unit)? = null
    private var pendingImportCallback: ((String?) -> Unit)? = null
    private var pendingPermissionCallback: ((Boolean) -> Unit)? = null

    private val createJsonDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val content = pendingExportJson
        val callback = pendingExportCallback
        pendingExportJson = null
        pendingExportCallback = null

        if (uri != null && content != null) {
            val ok = runCatching {
                contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(content.toByteArray(Charsets.UTF_8))
                }
            }.isSuccess
            callback?.invoke(ok, if (ok) "Backup JSON exported!" else "Could not write backup file.")
        } else {
            callback?.invoke(false, "Export cancelled.")
        }
    }

    private val openJsonDocument = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        val callback = pendingImportCallback
        pendingImportCallback = null
        val text = uri?.let {
            runCatching {
                contentResolver.openInputStream(it)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
                    reader.readText()
                }
            }.getOrNull()
        }
        callback?.invoke(text)
    }

    private val requestNotificationsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val callback = pendingPermissionCallback
        pendingPermissionCallback = null
        callback?.invoke(granted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidPlatformHost.init(applicationContext)
        AndroidPlatformHost.handleLaunchIntent(intent)
        AndroidPlatformHost.onExportDocumentRequested = { fileName, content, onResult ->
            pendingExportJson = content
            pendingExportCallback = onResult
            createJsonDocument.launch(fileName)
        }
        AndroidPlatformHost.onOpenDocumentRequested = { onPicked ->
            pendingImportCallback = onPicked
            openJsonDocument.launch(arrayOf("application/json", "text/plain", "*/*"))
        }
        AndroidPlatformHost.onRequestNotificationPermission = { onGranted ->
            pendingPermissionCallback = onGranted
            requestNotificationsPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            App(
                onThemeChange = { isDark ->
                    val style = if (isDark) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                    enableEdgeToEdge(
                        statusBarStyle = style,
                        navigationBarStyle = style,
                    )
                },
                platformBackHandler = { enabled, onBack ->
                    BackHandler(enabled = enabled, onBack = onBack)
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AndroidPlatformHost.handleLaunchIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            AndroidPlatformHost.onExportDocumentRequested = null
            AndroidPlatformHost.onOpenDocumentRequested = null
            AndroidPlatformHost.onRequestNotificationPermission = null
        }
    }
}

@Preview
@Composable
private fun AppAndroidPreview() {
    App()
}
