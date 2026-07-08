package org.clc.justgoup.export

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfURL
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UniformTypeIdentifiers.UTTypeJSON
import platform.darwin.NSObject

// UIDocumentPickerViewController.delegate is a weak reference, so we hold a strong
// reference here for the duration of the picker's lifetime to prevent it being
// deallocated before the async callback fires.
private object BackupPickerHolder {
    var delegate: NSObject? = null
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun rememberBackupExporter(onResult: (Boolean) -> Unit): (fileName: String, content: String) -> Unit {
    return { fileName, content ->
        val filePath = NSTemporaryDirectory() + fileName
        val written = NSString.create(string = content).writeToFile(
            filePath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )

        if (!written) {
            onResult(false)
        } else {
            val fileUrl = NSURL.fileURLWithPath(filePath)

            val delegate = BackupExportDelegate { success ->
                BackupPickerHolder.delegate = null
                onResult(success)
            }
            BackupPickerHolder.delegate = delegate

            val pickerController = UIDocumentPickerViewController(forExportingURLs = listOf(fileUrl))
            pickerController.delegate = delegate

            val presented = topMostViewController()?.let {
                it.presentViewController(pickerController, animated = true, completion = null)
                true
            } ?: false

            if (!presented) {
                BackupPickerHolder.delegate = null
                onResult(false)
            }
        }
    }
}

@Composable
actual fun rememberBackupImporter(onResult: (String?) -> Unit): () -> Unit {
    return {
        val delegate = BackupImportDelegate { content ->
            BackupPickerHolder.delegate = null
            onResult(content)
        }
        BackupPickerHolder.delegate = delegate

        val pickerController = UIDocumentPickerViewController(forOpeningContentTypes = listOf(UTTypeJSON))
        pickerController.delegate = delegate

        val presented = topMostViewController()?.let {
            it.presentViewController(pickerController, animated = true, completion = null)
            true
        } ?: false

        if (!presented) {
            BackupPickerHolder.delegate = null
            onResult(null)
        }
    }
}

private class BackupExportDelegate(
    private val onResult: (Boolean) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentAtURL: NSURL) {
        onResult(true)
    }

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        onResult(didPickDocumentsAtURLs.isNotEmpty())
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onResult(false)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class BackupImportDelegate(
    private val onResult: (String?) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentAtURL: NSURL) {
        onResult(readFile(didPickDocumentAtURL))
    }

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        onResult(url?.let(::readFile))
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onResult(null)
    }

    private fun readFile(url: NSURL): String? {
        url.startAccessingSecurityScopedResource()
        val content = runCatching {
            NSString.stringWithContentsOfURL(url, encoding = NSUTF8StringEncoding, error = null)
        }.getOrNull()
        url.stopAccessingSecurityScopedResource()
        return content
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun topMostViewController(): UIViewController? {
    val keyWindow = UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
        ?.keyWindow

    var topController = keyWindow?.rootViewController
    while (topController?.presentedViewController != null) {
        topController = topController.presentedViewController
    }

    return topController
}
