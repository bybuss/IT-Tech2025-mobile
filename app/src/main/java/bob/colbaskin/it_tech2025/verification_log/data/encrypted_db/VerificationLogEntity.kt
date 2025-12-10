package bob.colbaskin.it_tech2025.verification_log.data.encrypted_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "verification_logs")
data class VerificationLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "document_id")
    val documentId: Long,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "expiration_date")
    val expirationDate: Date,

    @ColumnInfo(name = "created_at")
    val createdAt: Date,

    @ColumnInfo(name = "checked_at")
    val checkedAt: Date
)
