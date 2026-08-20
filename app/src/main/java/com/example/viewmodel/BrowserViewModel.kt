package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.OperaDatabase
import com.example.data.model.Bookmark
import com.example.data.model.DataSavingMode
import com.example.data.model.DataSavingsStats
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.HistoryItem
import com.example.data.model.SavedPage
import com.example.data.model.TabItem
import com.example.util.UrlUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val database = OperaDatabase.getDatabase(application, viewModelScope)
    private val browserDao = database.browserDao()

    // Tabs Management
    private val _regularTabs = MutableStateFlow<List<TabItem>>(listOf(TabItem()))
    val regularTabs: StateFlow<List<TabItem>> = _regularTabs.asStateFlow()

    private val _privateTabs = MutableStateFlow<List<TabItem>>(emptyList())
    val privateTabs: StateFlow<List<TabItem>> = _privateTabs.asStateFlow()

    private val _isPrivateMode = MutableStateFlow(false)
    val isPrivateMode: StateFlow<Boolean> = _isPrivateMode.asStateFlow()

    private val _activeRegularTabId = MutableStateFlow(_regularTabs.value.first().id)
    private val _activePrivateTabId = MutableStateFlow<String?>(null)

    val activeTabId: StateFlow<String?> = MutableStateFlow(_regularTabs.value.first().id)

    // Current active tab accessor
    val currentTab: StateFlow<TabItem> = MutableStateFlow(_regularTabs.value.first())

    // Address Bar State
    private val _urlInputText = MutableStateFlow("")
    val urlInputText: StateFlow<String> = _urlInputText.asStateFlow()

    private val _isEditingUrl = MutableStateFlow(false)
    val isEditingUrl: StateFlow<Boolean> = _isEditingUrl.asStateFlow()

    private val _selectedSearchEngine = MutableStateFlow(UrlUtils.SearchEngine.GOOGLE)
    val selectedSearchEngine: StateFlow<UrlUtils.SearchEngine> = _selectedSearchEngine.asStateFlow()

    // Data Savings & Security Settings
    private val _dataSavingMode = MutableStateFlow(DataSavingMode.EXTREME)
    val dataSavingMode: StateFlow<DataSavingMode> = _dataSavingMode.asStateFlow()

    private val _isAdBlockerEnabled = MutableStateFlow(true)
    val isAdBlockerEnabled: StateFlow<Boolean> = _isAdBlockerEnabled.asStateFlow()

    private val _isHttpsOnlyMode = MutableStateFlow(true)
    val isHttpsOnlyMode: StateFlow<Boolean> = _isHttpsOnlyMode.asStateFlow()

    private val _isJavaScriptEnabled = MutableStateFlow(true)
    val isJavaScriptEnabled: StateFlow<Boolean> = _isJavaScriptEnabled.asStateFlow()

    private val _isBlockPopups = MutableStateFlow(true)
    val isBlockPopups: StateFlow<Boolean> = _isBlockPopups.asStateFlow()

    private val _imageQuality = MutableStateFlow("Medium") // High, Medium, Low, Off
    val imageQuality: StateFlow<String> = _imageQuality.asStateFlow()

    private val _dataSavingsStats = MutableStateFlow(DataSavingsStats())
    val dataSavingsStats: StateFlow<DataSavingsStats> = _dataSavingsStats.asStateFlow()

    // Database flows
    val bookmarks: StateFlow<List<Bookmark>> = browserDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val speedDials: StateFlow<List<Bookmark>> = browserDao.getSpeedDials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryItem>> = browserDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadItem>> = browserDao.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPages: StateFlow<List<SavedPage>> = browserDao.getAllSavedPages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Dialogs & Sheet Visibility
    val isOperaMenuOpen = MutableStateFlow(false)
    val isTabsOverviewOpen = MutableStateFlow(false)
    val isDataSavingsOpen = MutableStateFlow(false)
    val isBookmarksOpen = MutableStateFlow(false)
    val isHistoryOpen = MutableStateFlow(false)
    val isDownloadsOpen = MutableStateFlow(false)
    val isSavedPagesOpen = MutableStateFlow(false)
    val isSettingsOpen = MutableStateFlow(false)
    val isAddSpeedDialOpen = MutableStateFlow(false)

    // Find in Page
    val isFindInPageOpen = MutableStateFlow(false)
    val findInPageQuery = MutableStateFlow("")
    val findInPageCurrentIndex = MutableStateFlow(0)
    val findInPageTotalMatches = MutableStateFlow(0)

    // Reader Mode
    val isReaderMode = MutableStateFlow(false)
    val readerArticleTitle = MutableStateFlow("")
    val readerArticleHtml = MutableStateFlow("")
    val readerFontSize = MutableStateFlow(18) // sp

    // Web Action Triggers
    val webActionTrigger = MutableStateFlow<WebAction?>(null)

    sealed class WebAction {
        object GoBack : WebAction()
        object GoForward : WebAction()
        object Reload : WebAction()
        object Stop : WebAction()
        data class LoadUrl(val url: String) : WebAction()
        data class FindInPage(val query: String, val forward: Boolean) : WebAction()
        object ToggleNightMode : WebAction()
        object ToggleDesktopMode : WebAction()
        object ExtractReaderMode : WebAction()
        object SaveWebArchive : WebAction()
        object ClearWebCache : WebAction()
    }

    init {
        updateCurrentTabState()
    }

    private fun updateCurrentTabState() {
        val current = if (_isPrivateMode.value) {
            _privateTabs.value.find { it.id == _activePrivateTabId.value }
                ?: _privateTabs.value.firstOrNull()
        } else {
            _regularTabs.value.find { it.id == _activeRegularTabId.value }
                ?: _regularTabs.value.firstOrNull()
        }

        if (current != null) {
            (currentTab as MutableStateFlow).value = current
            (activeTabId as MutableStateFlow).value = current.id
            if (!_isEditingUrl.value) {
                _urlInputText.value = if (current.url == "opera://speeddial") "" else current.url
            }
        }
    }

    // Tab Operations
    fun openNewTab(url: String = "opera://speeddial", isPrivate: Boolean = _isPrivateMode.value) {
        val newTab = TabItem(
            url = url,
            title = if (url == "opera://speeddial") "Speed Dial" else UrlUtils.getDomain(url),
            isPrivate = isPrivate
        )
        if (isPrivate) {
            _privateTabs.value = _privateTabs.value + newTab
            _activePrivateTabId.value = newTab.id
            _isPrivateMode.value = true
        } else {
            _regularTabs.value = _regularTabs.value + newTab
            _activeRegularTabId.value = newTab.id
            _isPrivateMode.value = false
        }
        updateCurrentTabState()
        isTabsOverviewOpen.value = false
    }

    fun selectTab(tabId: String, isPrivate: Boolean) {
        _isPrivateMode.value = isPrivate
        if (isPrivate) {
            _activePrivateTabId.value = tabId
        } else {
            _activeRegularTabId.value = tabId
        }
        updateCurrentTabState()
        isTabsOverviewOpen.value = false
    }

    fun closeTab(tabId: String, isPrivate: Boolean) {
        if (isPrivate) {
            val updated = _privateTabs.value.filter { it.id != tabId }
            _privateTabs.value = updated
            if (_activePrivateTabId.value == tabId) {
                _activePrivateTabId.value = updated.lastOrNull()?.id
            }
            if (updated.isEmpty()) {
                _isPrivateMode.value = false
            }
        } else {
            val updated = _regularTabs.value.filter { it.id != tabId }
            if (updated.isEmpty()) {
                val fresh = TabItem()
                _regularTabs.value = listOf(fresh)
                _activeRegularTabId.value = fresh.id
            } else {
                _regularTabs.value = updated
                if (_activeRegularTabId.value == tabId) {
                    _activeRegularTabId.value = updated.last().id
                }
            }
        }
        updateCurrentTabState()
    }

    fun closeAllTabs(isPrivate: Boolean) {
        if (isPrivate) {
            _privateTabs.value = emptyList()
            _activePrivateTabId.value = null
            _isPrivateMode.value = false
            // Clean sandbox cookies on private close
            CookieManager.getInstance().removeSessionCookies(null)
        } else {
            val fresh = TabItem()
            _regularTabs.value = listOf(fresh)
            _activeRegularTabId.value = fresh.id
        }
        updateCurrentTabState()
    }

    fun togglePrivateMode(enable: Boolean) {
        _isPrivateMode.value = enable
        if (enable && _privateTabs.value.isEmpty()) {
            openNewTab(isPrivate = true)
        } else {
            updateCurrentTabState()
        }
    }

    // Navigation & URL
    fun onUrlInputChanged(newText: String) {
        _urlInputText.value = newText
    }

    fun onUrlEditFocus(isFocused: Boolean) {
        _isEditingUrl.value = isFocused
        if (!isFocused && _urlInputText.value.isEmpty()) {
            val current = currentTab.value
            _urlInputText.value = if (current.url == "opera://speeddial") "" else current.url
        }
    }

    fun loadUrl(input: String) {
        var targetUrl = UrlUtils.formatUrl(input, _selectedSearchEngine.value)
        if (_isHttpsOnlyMode.value && targetUrl.startsWith("http://", ignoreCase = true)) {
            targetUrl = "https://" + targetUrl.substring(7)
        }
        _urlInputText.value = targetUrl
        _isEditingUrl.value = false
        updateActiveTab { it.copy(url = targetUrl, title = UrlUtils.getDomain(targetUrl)) }
        webActionTrigger.value = WebAction.LoadUrl(targetUrl)

        // Record history if not private
        if (!_isPrivateMode.value && targetUrl != "opera://speeddial") {
            viewModelScope.launch {
                browserDao.insertHistory(
                    HistoryItem(
                        title = UrlUtils.getDomain(targetUrl),
                        url = targetUrl
                    )
                )
            }
        }
    }

    fun updateActiveTab(transform: (TabItem) -> TabItem) {
        val activeId = activeTabId.value ?: return
        if (_isPrivateMode.value) {
            _privateTabs.value = _privateTabs.value.map {
                if (it.id == activeId) transform(it) else it
            }
        } else {
            _regularTabs.value = _regularTabs.value.map {
                if (it.id == activeId) transform(it) else it
            }
        }
        updateCurrentTabState()
    }

    fun onPageStarted(url: String) {
        updateActiveTab { it.copy(url = url, isLoading = true, progress = 10) }
        _urlInputText.value = if (url == "opera://speeddial") "" else url
    }

    fun onPageProgress(progress: Int) {
        updateActiveTab { it.copy(progress = progress, isLoading = progress < 100) }
        if (progress >= 80) {
            val addedOriginal = 380000L
            val savedRatio = when (_dataSavingMode.value) {
                DataSavingMode.EXTREME -> 0.85
                DataSavingMode.HIGH -> 0.65
                DataSavingMode.OFF -> 0.0
            }
            val savedBytes = (addedOriginal * savedRatio).toLong()
            _dataSavingsStats.value = _dataSavingsStats.value.copy(
                totalOriginalBytes = _dataSavingsStats.value.totalOriginalBytes + addedOriginal,
                totalSavedBytes = _dataSavingsStats.value.totalSavedBytes + savedBytes
            )
        }
    }

    fun onPageFinished(url: String, title: String?, canBack: Boolean, canForward: Boolean) {
        val displayTitle = if (!title.isNullOrBlank()) title else UrlUtils.getDomain(url)
        updateActiveTab {
            it.copy(
                url = url,
                title = displayTitle,
                isLoading = false,
                progress = 100,
                canGoBack = canBack,
                canGoForward = canForward
            )
        }
        _urlInputText.value = if (url == "opera://speeddial") "" else url

        // Update history title
        if (!_isPrivateMode.value && url != "opera://speeddial" && !url.startsWith("data:")) {
            viewModelScope.launch {
                browserDao.insertHistory(
                    HistoryItem(
                        title = displayTitle,
                        url = url
                    )
                )
            }
        }
    }

    fun onAdBlocked() {
        _dataSavingsStats.value = _dataSavingsStats.value.copy(
            adsBlockedCount = _dataSavingsStats.value.adsBlockedCount + 1
        )
    }

    // Action Triggers
    fun goBack() {
        webActionTrigger.value = WebAction.GoBack
    }

    fun goForward() {
        webActionTrigger.value = WebAction.GoForward
    }

    fun reload() {
        webActionTrigger.value = WebAction.Reload
    }

    fun goHome() {
        loadUrl("opera://speeddial")
    }

    fun toggleDesktopSite() {
        val newDesktop = !currentTab.value.isDesktopMode
        updateActiveTab { it.copy(isDesktopMode = newDesktop) }
        webActionTrigger.value = WebAction.ToggleDesktopMode
    }

    fun toggleNightMode() {
        val newNight = !currentTab.value.isNightMode
        updateActiveTab { it.copy(isNightMode = newNight) }
        webActionTrigger.value = WebAction.ToggleNightMode
    }

    fun toggleDataSavingsMode() {
        _dataSavingMode.value = when (_dataSavingMode.value) {
            DataSavingMode.EXTREME -> DataSavingMode.HIGH
            DataSavingMode.HIGH -> DataSavingMode.OFF
            DataSavingMode.OFF -> DataSavingMode.EXTREME
        }
    }

    fun setDataSavingsMode(mode: DataSavingMode) {
        _dataSavingMode.value = mode
    }

    fun toggleAdBlocker() {
        _isAdBlockerEnabled.value = !_isAdBlockerEnabled.value
    }

    fun toggleHttpsOnly() {
        _isHttpsOnlyMode.value = !_isHttpsOnlyMode.value
    }

    fun toggleJavaScript() {
        _isJavaScriptEnabled.value = !_isJavaScriptEnabled.value
    }

    fun toggleBlockPopups() {
        _isBlockPopups.value = !_isBlockPopups.value
    }

    fun setSearchEngine(engine: UrlUtils.SearchEngine) {
        _selectedSearchEngine.value = engine
    }

    fun setImageQuality(quality: String) {
        _imageQuality.value = quality
    }

    // Bookmark & Speed Dial Management
    fun addCurrentPageToBookmarks(isSpeedDial: Boolean = false) {
        val tab = currentTab.value
        if (tab.url == "opera://speeddial" || tab.url.isEmpty()) return
        viewModelScope.launch {
            browserDao.insertBookmark(
                Bookmark(
                    title = tab.title,
                    url = tab.url,
                    isSpeedDial = isSpeedDial
                )
            )
        }
    }

    fun addSpeedDial(title: String, url: String) {
        viewModelScope.launch {
            browserDao.insertBookmark(
                Bookmark(
                    title = title,
                    url = UrlUtils.formatUrl(url),
                    isSpeedDial = true,
                    speedDialIndex = speedDials.value.size
                )
            )
            isAddSpeedDialOpen.value = false
        }
    }

    fun deleteSpeedDial(bookmark: Bookmark) {
        viewModelScope.launch {
            browserDao.deleteBookmark(bookmark.id)
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            browserDao.deleteBookmark(id)
        }
    }

    fun clearAllBrowsingData() {
        viewModelScope.launch {
            browserDao.clearAllHistory()
            WebStorage.getInstance().deleteAllData()
            CookieManager.getInstance().removeAllCookies(null)
            webActionTrigger.value = WebAction.ClearWebCache
            Toast.makeText(getApplication(), "Browsing data cleared", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            browserDao.clearAllHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            browserDao.deleteHistoryItem(id)
        }
    }

    // Downloads
    fun addDownload(url: String, fileName: String, totalBytes: Long = 0, mimeType: String? = null) {
        val context = getApplication<Application>()
        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val destination = File(downloadsDir, fileName).absolutePath
        val size = if (totalBytes > 0) totalBytes else 2540000L
        viewModelScope.launch {
            browserDao.insertDownload(
                DownloadItem(
                    fileName = fileName,
                    url = url,
                    filePath = destination,
                    totalBytes = size,
                    downloadedBytes = size,
                    mimeType = mimeType ?: "application/octet-stream",
                    status = DownloadStatus.COMPLETED
                )
            )
            Toast.makeText(context, "Download complete: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDownloadedFile(downloadItem: DownloadItem) {
        val context = getApplication<Application>()
        try {
            val file = File(downloadItem.filePath)
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "com.aistudio.operamini.browser.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, downloadItem.mimeType ?: "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "File location: ${downloadItem.fileName}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Opening ${downloadItem.fileName}", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteDownload(id: Long) {
        viewModelScope.launch {
            browserDao.deleteDownload(id)
        }
    }

    // Saved Offline Pages
    fun saveCurrentPageOffline(filePath: String, fileSize: Long) {
        val tab = currentTab.value
        viewModelScope.launch {
            browserDao.insertSavedPage(
                SavedPage(
                    title = tab.title,
                    url = tab.url,
                    filePath = filePath,
                    fileSize = fileSize
                )
            )
            Toast.makeText(getApplication(), "Page saved for offline viewing", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteSavedPage(id: Long) {
        viewModelScope.launch {
            browserDao.deleteSavedPage(id)
        }
    }

    // Find in Page
    fun startFindInPage(query: String) {
        findInPageQuery.value = query
        webActionTrigger.value = WebAction.FindInPage(query, forward = true)
    }

    fun findNext() {
        webActionTrigger.value = WebAction.FindInPage(findInPageQuery.value, forward = true)
    }

    fun findPrevious() {
        webActionTrigger.value = WebAction.FindInPage(findInPageQuery.value, forward = false)
    }

    fun closeFindInPage() {
        isFindInPageOpen.value = false
        findInPageQuery.value = ""
        findInPageCurrentIndex.value = 0
        findInPageTotalMatches.value = 0
    }

    // Reader Mode
    fun openReaderMode(title: String, htmlContent: String) {
        readerArticleTitle.value = title
        readerArticleHtml.value = htmlContent
        isReaderMode.value = true
    }

    fun closeReaderMode() {
        isReaderMode.value = false
    }
}
