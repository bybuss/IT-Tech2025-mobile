package bob.colbaskin.it_tech2025.scanner.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import bob.colbaskin.it_tech2025.scanner.domain.ScannerRepository
import bob.colbaskin.it_tech2025.scanner.domain.models.CameraPermissionState
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var state by mutableStateOf(ScannerState())
        private set

    private var checkDocumentJob: Job? = null

    fun onAction(action: ScannerAction) {
        when (action) {
            is ScannerAction.RequestCameraPermission -> requestCameraPermission()
            is ScannerAction.OnCameraPermissionResult -> onCameraPermissionResult(action.granted)
            is ScannerAction.StartCameraScan -> startCameraScan()
            is ScannerAction.StopCameraScan -> stopCameraScan()
            is ScannerAction.OnHashScanned -> onHashScanned(action.hash)
            is ScannerAction.OnFileSelected -> onFileSelected(action.uri)
            is ScannerAction.ClearSelectedFile -> clearSelectedFile()
            is ScannerAction.CheckDocument -> checkDocument()
            is ScannerAction.ClearDocumentResult -> clearDocumentResult()
            else -> Unit
        }
    }

    fun resetAuthState() {
        state = state.copy(
            documentCheckState = UiState.Loading,
            currentStatus = DocumentStatus.NOT_SCANNED
        )
    }

    private fun requestCameraPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )

        state = if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            state.copy(
                cameraPermissionState = CameraPermissionState.GRANTED,
                isCameraScanning = true
            )
        } else {
            state.copy(cameraPermissionState = CameraPermissionState.NOT_GRANTED)
        }
    }

    private fun onCameraPermissionResult(granted: Boolean) {
        state = if (granted) {
            state.copy(
                cameraPermissionState = CameraPermissionState.GRANTED,
                isCameraScanning = true
            )
        } else {
            state.copy(
                cameraPermissionState = CameraPermissionState.DENIED,
                isCameraScanning = false
            )
        }
    }

    private fun startCameraScan() {
        if (state.cameraPermissionState == CameraPermissionState.GRANTED) {
            state = state.copy(
                isCameraScanning = true,
                scannedHash = null,
                documentCheckState = UiState.Loading,
                currentStatus = DocumentStatus.NOT_SCANNED
            )
        }
    }

    private fun stopCameraScan() {
        state = state.copy(isCameraScanning = false)
    }

    private fun onHashScanned(hash: String) {
        state = state.copy(
            isCameraScanning = false,
            scannedHash = hash,
            currentStatus = DocumentStatus.NOT_SCANNED
        )
        checkDocument()
    }

    private fun onFileSelected(uri: Uri) {
        state = state.copy(
            selectedFileUri = uri.toString(),
            documentCheckState = UiState.Loading,
            currentStatus = DocumentStatus.NOT_SCANNED
        )
        //TODO: обработка файла
    }

    private fun clearSelectedFile() {
        state = state.copy(selectedFileUri = null)
    }

    private fun clearDocumentResult() {
        state = state.copy(
            scannedHash = null,
            documentCheckState = UiState.Loading,
            currentStatus = DocumentStatus.NOT_SCANNED
        )

        if (state.cameraPermissionState == CameraPermissionState.GRANTED) {
            startCameraScan()
        }
    }

    private fun checkDocument() {
        val hash = state.scannedHash ?: return

        checkDocumentJob?.cancel()

        checkDocumentJob = viewModelScope.launch {
            state = state.copy(
                documentCheckState = UiState.Loading,
                currentStatus = DocumentStatus.NOT_SCANNED
            )

            val result = scannerRepository.checkDocument(hash)

            state = when (result) {
                is ApiResult.Success -> {
                    val status = when (result.data.status.lowercase()) {
                        "green" -> DocumentStatus.GREEN
                        "yellow" -> DocumentStatus.YELLOW
                        "red" -> DocumentStatus.RED
                        else -> DocumentStatus.GRAY
                    }
                    state.copy(
                        documentCheckState = UiState.Success(result.data),
                        currentStatus = status
                    )
                }
                is ApiResult.Error -> {
                    state.copy(
                        documentCheckState = UiState.Error(
                            title = result.title,
                            text = result.text
                        ),
                        currentStatus = DocumentStatus.NOT_SCANNED
                    )
                }
            }
        }
    }
}
