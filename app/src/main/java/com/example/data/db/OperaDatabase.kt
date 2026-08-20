package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Bookmark
import com.example.data.model.DownloadItem
import com.example.data.model.HistoryItem
import com.example.data.model.SavedPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Bookmark::class,
        HistoryItem::class,
        DownloadItem::class,
        SavedPage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OperaDatabase : RoomDatabase() {
    abstract fun browserDao(): BrowserDao

    companion object {
        @Volatile
        private var INSTANCE: OperaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): OperaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OperaDatabase::class.java,
                    "opera_mini_database"
                )
                .addCallback(OperaDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class OperaDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDefaultSpeedDials(database.browserDao())
                    }
                }
            }
        }

        private suspend fun populateDefaultSpeedDials(dao: BrowserDao) {
            val defaults = listOf(
                Bookmark(title = "Google", url = "https://www.google.com", isSpeedDial = true, speedDialIndex = 0),
                Bookmark(title = "YouTube", url = "https://m.youtube.com", isSpeedDial = true, speedDialIndex = 1),
                Bookmark(title = "Wikipedia", url = "https://en.m.wikipedia.org", isSpeedDial = true, speedDialIndex = 2),
                Bookmark(title = "Facebook", url = "https://m.facebook.com", isSpeedDial = true, speedDialIndex = 3),
                Bookmark(title = "Cricbuzz", url = "https://m.cricbuzz.com", isSpeedDial = true, speedDialIndex = 4),
                Bookmark(title = "BBC News", url = "https://www.bbc.com/news", isSpeedDial = true, speedDialIndex = 5),
                Bookmark(title = "Amazon", url = "https://www.amazon.com", isSpeedDial = true, speedDialIndex = 6),
                Bookmark(title = "Reddit", url = "https://m.reddit.com", isSpeedDial = true, speedDialIndex = 7)
            )
            defaults.forEach { dao.insertBookmark(it) }

            // Add sample history and downloads for instant realistic Opera Mini experience
            dao.insertHistory(HistoryItem(title = "Google Search", url = "https://www.google.com"))
            dao.insertHistory(HistoryItem(title = "BBC News - World Updates", url = "https://www.bbc.com/news"))
            dao.insertHistory(HistoryItem(title = "Wikipedia - Opera (web browser)", url = "https://en.m.wikipedia.org/wiki/Opera_(web_browser)"))
        }
    }
}
