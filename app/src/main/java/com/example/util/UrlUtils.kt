package com.example.util

import android.net.Uri
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UrlUtils {
    enum class SearchEngine(val displayName: String, val searchUrl: String, val iconLetter: String) {
        GOOGLE("Google", "https://www.google.com/search?q=", "G"),
        BING("Bing", "https://www.bing.com/search?q=", "B"),
        DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "D"),
        YAHOO("Yahoo", "https://search.yahoo.com/search?p=", "Y"),
        WIKIPEDIA("Wikipedia", "https://en.wikipedia.org/wiki/Special:Search?search=", "W"),
        YOUTUBE("YouTube", "https://m.youtube.com/results?search_query=", "Y")
    }

    fun formatUrl(input: String, searchEngine: SearchEngine = SearchEngine.GOOGLE): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "opera://speeddial"
        if (trimmed.equals("opera://speeddial", ignoreCase = true)) return "opera://speeddial"

        // If it looks like a URL (contains dot, or starts with http/https/www/ftp)
        val isUrl = (trimmed.contains(".") && !trimmed.contains(" ")) ||
                trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) ||
                trimmed.startsWith("file://", ignoreCase = true) ||
                trimmed.startsWith("localhost", ignoreCase = true)

        return if (isUrl) {
            if (!trimmed.startsWith("http://", ignoreCase = true) &&
                !trimmed.startsWith("https://", ignoreCase = true) &&
                !trimmed.startsWith("file://", ignoreCase = true)
            ) {
                "https://$trimmed"
            } else {
                trimmed
            }
        } else {
            val encoded = try {
                URLEncoder.encode(trimmed, "UTF-8")
            } catch (e: Exception) {
                trimmed
            }
            "${searchEngine.searchUrl}$encoded"
        }
    }

    fun getDomain(url: String): String {
        return try {
            val uri = Uri.parse(url)
            uri.host?.replace("www.", "")?.replace("m.", "") ?: url
        } catch (e: Exception) {
            url
        }
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.getDefault(), "%.2f GB", gb)
            mb >= 1.0 -> String.format(Locale.getDefault(), "%.1f MB", mb)
            kb >= 1.0 -> String.format(Locale.getDefault(), "%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            else -> SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
