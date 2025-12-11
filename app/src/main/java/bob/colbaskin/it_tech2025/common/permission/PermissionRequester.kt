package bob.colbaskin.it_tech2025.common.permission

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        SideEffect {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionStatus != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onPermissionGranted()
            }
        }
    } else {
        onPermissionGranted()
    }
}
