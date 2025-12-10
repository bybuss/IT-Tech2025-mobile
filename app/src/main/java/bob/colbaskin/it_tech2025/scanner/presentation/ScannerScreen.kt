package bob.colbaskin.it_tech2025.scanner.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.scanner.domain.models.CameraPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bob.colbaskin.it_tech2025.common.design_system.theme.CustomTheme
import com.google.accompanist.permissions.PermissionState
import bob.colbaskin.it_tech2025.R
import bob.colbaskin.it_tech2025.navigation.Screens

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreenRoot(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val state = viewModel.state

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    LaunchedEffect(cameraPermissionState.status) {
        when (cameraPermissionState.status) {
            is PermissionStatus.Granted -> {
                viewModel.onAction(ScannerAction.OnCameraPermissionResult(true))
            }
            is PermissionStatus.Denied -> {
                viewModel.onAction(ScannerAction.OnCameraPermissionResult(false))
            }
            else -> {}
        }
    }

    LaunchedEffect(state.selectedFileUri) {
        state.selectedFileUri?.let { uri ->
            navController.navigate(Screens.ImageAreaSelectionScreen(uri))
            viewModel.onAction(ScannerAction.ClearSelectedFile)
        }
    }

    LaunchedEffect(state.documentCheckState) {
        when (val checkState = state.documentCheckState) {
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    checkState.title,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetAuthState()
            }
            else -> {}
        }
    }

    ScannerScreen(
        state = state,
        onAction = viewModel::onAction,
        cameraPermissionState = cameraPermissionState
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    state: ScannerState,
    onAction: (ScannerAction) -> Unit,
    cameraPermissionState: PermissionState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.cameraPermissionState == CameraPermissionState.GRANTED && state.isCameraScanning) {
            CameraScannerView(
                isScanning = state.isCameraScanning,
                onHashScanned = { hash ->
                    onAction(ScannerAction.OnHashScanned(hash))
                },
                onStopScan = { onAction(ScannerAction.StopCameraScan) }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        ScannerOverlay(
            state = state,
            onAction = onAction,
            cameraPermissionState = cameraPermissionState
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ScannerOverlay(
    state: ScannerState,
    onAction: (ScannerAction) -> Unit,
    cameraPermissionState: PermissionState
) {
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAction(ScannerAction.OnFileSelected(it)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            StatusStrip(status = state.currentStatus)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GalleryButton(
                onPickImage = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(modifier = Modifier.width(32.dp))

            CameraToggleButton(
                isScanning = state.isCameraScanning,
                onToggleCamera = {
                    if (state.cameraPermissionState == CameraPermissionState.GRANTED) {
                        if (state.isCameraScanning) {
                            onAction(ScannerAction.StopCameraScan)
                        } else {
                            onAction(ScannerAction.StartCameraScan)
                        }
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            )
        }

        if (state.cameraPermissionState != CameraPermissionState.GRANTED) {
            CameraPermissionOverlay(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
private fun CameraToggleButton(
    isScanning: Boolean,
    onToggleCamera: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .background(
                color = CustomTheme.colors.glass,
                shape = CircleShape
            )
            .clickable { onToggleCamera() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                R.drawable.ic_camera_start
            ),
            contentDescription = if (isScanning) "Остановить сканирование" else "Начать сканирование",
            tint = CustomTheme.colors.white,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun StatusStrip(status: DocumentStatus) {
    val statusInfo = remember(status) {
        when (status) {
            DocumentStatus.GREEN -> StatusInfo(
                color = Color.Green,
                text = "Документ подлинный"
            )
            DocumentStatus.YELLOW -> StatusInfo(
                color = Color.Yellow,
                text = "Документ скоро истечет"
            )
            DocumentStatus.RED -> StatusInfo(
                color = Color.Red,
                text = "Недействителен"
            )
            else -> StatusInfo(
                color = Color.Gray,
                text = "Не сканировано"
            )
        }
    }

    AnimatedContent(
        targetState = statusInfo,
        transitionSpec = {
            fadeIn().togetherWith(fadeOut())
        },
        label = "statusAnimation"
    ) { targetStatus ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                colors = CardDefaults.cardColors(
                    containerColor = CustomTheme.colors.glass
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = targetStatus.color,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = targetStatus.text,
                        style = CustomTheme.typography.h3Medium,
                        color = CustomTheme.colors.white
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryButton(onPickImage: () -> Unit) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .background(
                color = CustomTheme.colors.glass,
                shape = CircleShape
            )
            .clickable { onPickImage() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_gallery),
            contentDescription = "Выбрать из галереи",
            tint = CustomTheme.colors.white,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun CameraPermissionOverlay(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Требуется доступ к камере",
                style = CustomTheme.typography.h3Medium,
                color = CustomTheme.colors.white,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomTheme.colors.white,
                    contentColor = CustomTheme.colors.black
                )
            ) {
                Text("Разрешить доступ к камере")
            }
        }
    }
}

private data class StatusInfo(
    val color: Color,
    val text: String
)
