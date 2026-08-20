package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val faviconUrl: String? = null,
    val isSpeedDial: Boolean = false,
    val speedDialIndex: Int = 0,
    val folder: String = "Bookmarks",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val visitCount: Int = 1
)

enum class DownloadStatus {
    PENDING, DOWNLOADING, COMPLETED, FAILED, PAUSED, CANCELLED
}

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val url: String,
    val filePath: String,
    val totalBytes: Long = 0,
    val downloadedBytes: Long = 0,
    val mimeType: String? = null,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_pages")
data class SavedPage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val filePath: String,
    val fileSize: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class TabItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "Speed Dial",
    val url: String = "opera://speeddial",
    val isPrivate: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isDesktopMode: Boolean = false,
    val isNightMode: Boolean = false,
    val isReaderMode: Boolean = false,
    val readerContent: String? = null,
    val lastVisitedAt: Long = System.currentTimeMillis()
)

enum class DataSavingMode {
    EXTREME, HIGH, OFF
}

data class DataSavingsStats(
    val totalOriginalBytes: Long = 124500000L, // ~124.5 MB
    val totalSavedBytes: Long = 95200000L,     // ~95.2 MB (76% savings)
    val adsBlockedCount: Int = 342,
    val trackersBlockedCount: Int = 189,
    val savedPagesCount: Int = 5
) {
    val savingPercentage: Int
        get() = if (totalOriginalBytes > 0) {
            ((totalSavedBytes.toDouble() / totalOriginalBytes.toDouble()) * 100).toInt()
        } else 0
}
