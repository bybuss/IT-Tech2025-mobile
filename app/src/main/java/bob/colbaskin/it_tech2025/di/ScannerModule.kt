package bob.colbaskin.it_tech2025.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    @Provides
    @Singleton
    fun provideBarcodeScannerSDK(@ApplicationContext context: Context): ScanbotBarcodeScannerSDK {
        return ScanbotBarcodeScannerSDK(context)
    }
}