package bob.colbaskin.it_tech2025.verification_log.data.encrypted_db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import bob.colbaskin.it_tech2025.common.biometric.KeyStoreManager
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.inject.Inject

private const val TAG = "DatabaseEncryptionHelper"
private const val DATABASE_NAME = "verification_log_unencrypted.db"

interface EncryptedDatabase {
    fun verificationLogDao(): VerificationLogDao
    fun close()
}

class EncryptedDatabaseImpl @Inject constructor(
    private val context: Context,
    private val keyStoreManager: KeyStoreManager
) : EncryptedDatabase {

    private var database: RoomDatabase? = null

    override fun verificationLogDao(): VerificationLogDao {
        return getDatabase().verificationLogDao()
    }

    override fun close() {
        database?.close()
        database = null
    }

    private fun getDatabase(): AppDatabase {
        if (database == null) {
            synchronized(this) {
                if (database == null) {
                    database = createEncryptedDatabase()
                }
            }
        }
        return database as AppDatabase
    }

    private fun createEncryptedDatabase(): AppDatabase {
        try {
            val password = generateAndStorePassword()
            val factory = SupportFactory(SQLiteDatabase.getBytes(password))

            password.fill('0')

            Log.d(TAG, "Encrypted database created successfully")
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d(TAG, "Encrypted database created")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        db.execSQL("PRAGMA foreign_keys = ON;")
                    }
                })
                .build()

        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "SQLCipher native library failed to load", e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create encrypted database: ${e.message}")
            throw RuntimeException("Failed to initialize encrypted database", e)
        }
    }

    private fun generateAndStorePassword(): CharArray {
        val password = CharArray(32)
        val random = SecureRandom()

        for (i in password.indices) {
            val charType = random.nextInt(3)
            password[i] = when (charType) {
                0 -> (random.nextInt(26) + 65).toChar() // A-Z
                1 -> (random.nextInt(26) + 97).toChar() // a-z
                else -> (random.nextInt(10) + 48).toChar() // 0-9
            }
        }

        return password
    }

    @Database(
        entities = [VerificationLogEntity::class],
        version = 1,
        exportSchema = false
    )
    @TypeConverters(DateConverters::class)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun verificationLogDao(): VerificationLogDao
    }
}
