package com.khawi.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.khawi.base.DATABASE_NAME
import com.khawi.model.db.user.UserDao
import com.khawi.model.db.user.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [UserModel::class],
    version = 2, exportSchema = true
)
abstract class RoomDatabaseBase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: RoomDatabaseBase? = null
        fun getInstance(context: Context, scope: CoroutineScope): RoomDatabaseBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, scope).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, scope: CoroutineScope): RoomDatabaseBase {
            val builder =
                Room.databaseBuilder(context, RoomDatabaseBase::class.java, DATABASE_NAME)
                    .setJournalMode(JournalMode.TRUNCATE)
//            try {
//                val dbFile = File(getOfflinePath(), getOfflineFileName())
//                if (dbFile.exists()) {
//                    builder.createFromFile(dbFile)
//                } else {
//                    val sd =
//                        File("${Environment.getExternalStorageDirectory().path}/TankerTarpetX/DB/")
//                    if (sd.exists())
//                        builder.createFromFile(File(sd, DATABASE_NAME))
//                }
//                val databaseFile: File =
//                    MainApplication.applicationContext().getDatabasePath(DATABASE_NAME)
//                if (!databaseFile.exists() && dbFile.exists()) {
//                    copyDatabaseFile(dbFile, databaseFile)
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
            builder
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            instance?.let { _ ->
                                scope.launch {
                                }
                            }
                        }
                    }
                )
            return builder.build()
        }

//        private fun copyDatabaseFile(originalFile: File, destinationFile: File) {
//            try {
//                val input: ReadableByteChannel = FileInputStream(originalFile).channel
//
//                // An intermediate file is used so that we never end up with a half-copied database file
//                // in the internal directory.
//                val intermediateFile = File.createTempFile(
//                    "room-copy-helper", ".tmp", MainApplication.applicationContext().cacheDir
//                )
//                intermediateFile.deleteOnExit()
//                val output = FileOutputStream(intermediateFile).channel
//                copy(input, output)
//                val parent = destinationFile.parentFile
//                if (parent != null && !parent.exists() && !parent.mkdirs()) {
//                    Log.e("Failed to create directories for ", destinationFile.absolutePath)
//                }
//                if (!intermediateFile.renameTo(destinationFile)) {
//                    Log.e("","Failed to move intermediate file ("
//                            + intermediateFile.absolutePath + ") to destination ("
//                            + destinationFile.absolutePath + ").")
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

//        private fun copy(input: ReadableByteChannel, output: FileChannel) {
//            try {
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                    output.transferFrom(input, 0, Long.MAX_VALUE)
//                } else {
//                    val inputStream = Channels.newInputStream(input)
//                    val outputStream = Channels.newOutputStream(output)
//                    var length: Int
//                    val buffer = ByteArray(1024 * 4)
//                    while (inputStream.read(buffer).also { length = it } > 0) {
//                        outputStream.write(buffer, 0, length)
//                    }
//                }
//                output.force(false)
//                input.close()
//                output.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        private fun getOfflinePath(): String {
//            return MainApplication.applicationContext().cacheDir.path + File.separator + "offline"
//        }
//
//        private fun getOfflineFileName(): String {
//            return DATABASE_NAME
//        }
    }

}