package org.clc.justgoup.export

import androidx.compose.runtime.Composable

@Composable
expect fun rememberBackupExporter(onResult: (Boolean) -> Unit): (fileName: String, content: String) -> Unit

@Composable
expect fun rememberBackupImporter(onResult: (String?) -> Unit): () -> Unit
