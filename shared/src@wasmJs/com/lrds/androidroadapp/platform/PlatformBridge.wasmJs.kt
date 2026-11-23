package com.lrds.androidroadapp.platform

import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("UnusedParameter") // Referenced inside Kotlin/Wasm js() snippet
private fun jsOpenUrl(url: String) {
    js(
        """
        if (typeof globalThis.window !== 'undefined') {
          globalThis.window.open(url, '_blank', 'noopener,noreferrer');
        }
        """
    )
}

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("UnusedParameter") // Referenced inside Kotlin/Wasm js() snippet
private fun jsDownloadJsonFile(fileName: String, content: String): Boolean =
    js(
        """
        (() => {
          try {
            const blob = new Blob([content], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
            return true;
          } catch (e) {
            return false;
          }
        })()
        """
    )

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("UnusedParameter") // Referenced inside Kotlin/Wasm js() snippet
private fun jsPickJsonFile(onLoaded: (String) -> Unit) {
    js(
        """
        (() => {
          const input = document.createElement('input');
          input.type = 'file';
          input.accept = 'application/json,.json';
          input.onchange = (e) => {
            const file = e.target && e.target.files ? e.target.files[0] : null;
            if (!file) return;
            const reader = new FileReader();
            reader.onload = () => {
              if (typeof reader.result === 'string') {
                onLoaded(reader.result);
              }
            };
            reader.readAsText(file);
          };
          input.click();
        })()
        """
    )
}

private class WasmPlatformBridge : PlatformBridge {
    override val supportsDailyReminders: Boolean = false
    override val supportsNativeFilePicker: Boolean = true

    override fun openUrl(url: String) {
        runCatching { jsOpenUrl(url) }
    }

    override fun exportProgressJson(
        fileName: String,
        jsonContent: String,
        onResult: (success: Boolean, message: String) -> Unit,
    ) {
        val ok = runCatching { jsDownloadJsonFile(fileName, jsonContent) }.getOrDefault(false)
        if (ok) {
            onResult(true, "Downloaded $fileName!")
        } else {
            onResult(false, "Could not download backup JSON.")
        }
    }

    override fun pickProgressJson(onPicked: (rawJson: String?) -> Unit) {
        runCatching {
            jsPickJsonFile { content -> onPicked(content) }
        }.onFailure {
            onPicked(null)
        }
    }

    override fun syncDailyReviewReminder(
        enabled: Boolean,
        hour: Int,
        dueCount: Int,
        onResult: (granted: Boolean, message: String) -> Unit,
    ) {
        onResult(false, "Local push reminders are available on Android & iOS.")
    }
}

actual fun createPlatformBridge(): PlatformBridge = WasmPlatformBridge()
