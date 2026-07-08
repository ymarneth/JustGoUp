package org.clc.justgoup.export

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberBackupExporter(onResult: (Boolean) -> Unit): (fileName: String, content: String) -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)
    var pendingContent by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        val content = pendingContent
        pendingContent = null

        val success = uri != null && content != null && runCatching {
            context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
        }.isSuccess

        currentOnResult(success)
    }

    return { fileName, content ->
        pendingContent = content
        launcher.launch(fileName)
    }
}

@Composable
actual fun rememberBackupImporter(onResult: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val content = uri?.let {
            runCatching {
                context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes().decodeToString() }
            }.getOrNull()
        }

        currentOnResult(content)
    }

    return { launcher.launch(arrayOf("application/json")) }
}
