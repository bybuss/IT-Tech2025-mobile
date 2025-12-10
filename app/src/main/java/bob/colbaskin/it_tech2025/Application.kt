package bob.colbaskin.it_tech2025

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer

@HiltAndroidApp
class Application: Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            ScanbotBarcodeScannerSDKInitializer()
                .initialize(this)
        } catch (e: Exception) {
            Log.e("ScannerApplication", "Ошибка инициализации Scanbot SDK", e)
        }
    }
}
