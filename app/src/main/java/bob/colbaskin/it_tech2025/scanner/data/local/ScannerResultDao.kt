package bob.colbaskin.it_tech2025.scanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScannerResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ScannerResultEntity)

    @Query("SELECT * FROM scanner_results ORDER BY checked_at DESC")
    suspend fun getAllResults(): List<ScannerResultEntity>

    @Query("DELETE FROM scanner_results WHERE document_id = :documentId")
    suspend fun deleteResult(documentId: Long)

    @Query("SELECT COUNT(*) FROM scanner_results")
    suspend fun getResultsCount(): Int
}
