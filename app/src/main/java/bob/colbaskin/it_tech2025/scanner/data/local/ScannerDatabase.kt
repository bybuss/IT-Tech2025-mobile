package bob.colbaskin.it_tech2025.scanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ScannerResultEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ScannerDatabase : RoomDatabase() {

    abstract fun scannerResultDao(): ScannerResultDao

    companion object {
        const val DATABASE_NAME = "scanner_db"
    }
}
