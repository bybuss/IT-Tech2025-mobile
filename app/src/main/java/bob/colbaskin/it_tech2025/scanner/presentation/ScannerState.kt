package bob.colbaskin.it_tech2025.scanner.presentation

import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.scanner.domain.models.CameraPermissionState
import bob.colbaskin.it_tech2025.scanner.domain.models.ScannerResult

data class ScannerState(
    val cameraPermissionState: CameraPermissionState = CameraPermissionState.NOT_REQUESTED,
    val scannedHash: String? = null,
    val documentCheckState: UiState<ScannerResult> = UiState.Loading,
    val isCameraScanning: Boolean = false,
    val selectedFileUri: String? = null,
    val currentStatus: DocumentStatus = DocumentStatus.NOT_SCANNED
) {
    val isScanInProgress: Boolean
        get() = isCameraScanning

    val currentDocumentResult: ScannerResult?
        get() = when (documentCheckState) {
            is UiState.Success -> documentCheckState.data
            else -> null
        }
}

enum class DocumentStatus {
    NOT_SCANNED,
    GREEN,
    YELLOW,
    RED,
    GRAY
}