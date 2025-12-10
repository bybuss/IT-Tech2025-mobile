package bob.colbaskin.it_tech2025.scanner.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.scanbot.sdk.barcode.BarcodeFormat
import io.scanbot.sdk.common.AspectRatio
import io.scanbot.sdk.ui_v2.common.OrientationLockMode
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerView
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.common.ScanbotColor
import io.scanbot.sdk.ui_v2.common.Vibration
import io.scanbot.sdk.ui_v2.common.ViewFinderConfiguration

@Composable
fun CameraScannerView(
    modifier: Modifier = Modifier,
    isScanning: Boolean,
    onHashScanned: (String) -> Unit,
    onStopScan: () -> Unit
) {
    val screenConfiguration = remember {
        BarcodeScannerScreenConfiguration().apply {
            this.cameraConfiguration.apply {
                flashEnabled = false
                orientationLockMode = OrientationLockMode.PORTRAIT
            }

            this.scannerConfiguration.apply {
                barcodeFormats = listOf(
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.CODABAR,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.CODE_39,
                    BarcodeFormat.EAN_13
                )
            }

            this.actionBar.flipCameraButton.visible = false
            this.actionBar.zoomButton.visible = false
            this.actionBar.flashButton.visible = false

            this.topBar.title.visible = false
            this.topBar.title.visible = false
            this.topBar.backgroundColor = ScanbotColor(Color.Transparent)
            this.topBar.cancelButton.visible = false

            this.vibration.enabled = true

            this.userGuidance.visible = false
        }
    }

    BarcodeScannerView(
        modifier = modifier.fillMaxSize(),
        configuration = screenConfiguration,
        onBarcodeScanned = { result ->
            if (isScanning) {
                if (result.items.isNotEmpty()) {
                    val barcodeItem = result.items.first()
                    val hash = barcodeItem.barcode.text
                    onStopScan()
                    onHashScanned(hash)
                }
            } else {
                null
            }
        },
        onBarcodeScannerClosed = {
            onStopScan()
        }
    )
}
