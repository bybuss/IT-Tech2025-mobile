package bob.colbaskin.it_tech2025.scanner.data.local


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scanner_results")
data class ScannerResultEntity(
    @PrimaryKey
    @ColumnInfo(name = "document_id") val documentId: Long,
    @ColumnInfo(name = "hash") val hash: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "expiration_date") val expirationDate: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "checked_at") val checkedAt: String,
    @ColumnInfo(name = "scanned_at") val scannedAt: Date
)
