package bob.colbaskin.it_tech2025.verification_log.data.encrypted_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: VerificationLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<VerificationLogEntity>)

    @Query("SELECT * FROM verification_logs ORDER BY checked_at DESC")
    fun getAll(): Flow<List<VerificationLogEntity>>

    @Query("SELECT * FROM verification_logs WHERE status = :status ORDER BY checked_at DESC")
    fun getByStatus(status: String): Flow<List<VerificationLogEntity>>

    @Query("SELECT * FROM verification_logs WHERE checked_at BETWEEN :startDate AND :endDate ORDER BY checked_at DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<VerificationLogEntity>>

    @Query("SELECT * FROM verification_logs WHERE expiration_date < :threshold")
    suspend fun getExpiringSoon(threshold: Long): List<VerificationLogEntity>

    @Query("SELECT COUNT(*) FROM verification_logs")
    suspend fun getCount(): Int

    @Query("DELETE FROM verification_logs WHERE document_id = :documentId")
    suspend fun delete(documentId: Long)

    @Query("DELETE FROM verification_logs")
    suspend fun deleteAll()
}
