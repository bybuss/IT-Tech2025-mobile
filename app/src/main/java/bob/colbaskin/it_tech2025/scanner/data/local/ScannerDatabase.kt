package bob.colbaskin.it_tech2025.scanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [ScannerResultEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ScannerDatabase : RoomDatabase() {

    abstract fun scannerResultDao(): ScannerResultDao

    companion object {
        const val DATABASE_NAME = "scanner_db"
    }
}
