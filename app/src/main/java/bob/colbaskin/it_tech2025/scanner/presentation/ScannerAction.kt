package bob.colbaskin.it_tech2025.scanner.presentation

import android.net.Uri

sealed interface ScannerAction {
    data object RequestCameraPermission : ScannerAction
    data class OnCameraPermissionResult(val granted: Boolean) : ScannerAction
    data object StartCameraScan : ScannerAction
    data object StopCameraScan : ScannerAction
    data class OnHashScanned(val hash: String) : ScannerAction
    data class OnFileSelected(val uri: Uri) : ScannerAction
    data object ClearSelectedFile : ScannerAction
    data object CheckDocument : ScannerAction
    data object ClearDocumentResult : ScannerAction
}
