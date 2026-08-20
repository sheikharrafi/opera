package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.OperaBottomBar
import com.example.ui.components.OperaMenuDialog
import com.example.ui.components.OperaTopBar
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DataSavingsScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SavedPagesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TabsOverviewScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BrowserViewModel = viewModel()
            OperaBrowserApp(viewModel = viewModel)
        }
    }
}

@Composable
fun OperaBrowserApp(viewModel: BrowserViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val regularTabs by viewModel.regularTabs.collectAsState()
    val privateTabs by viewModel.privateTabs.collectAsState()
    val isPrivateMode by viewModel.isPrivateMode.collectAsState()

    val urlInputText by viewModel.urlInputText.collectAsState()
    val isEditingUrl by viewModel.isEditingUrl.collectAsState()
    val selectedSearchEngine by viewModel.selectedSearchEngine.collectAsState()

    val dataSavingMode by viewModel.dataSavingMode.collectAsState()
    val dataSavingsStats by viewModel.dataSavingsStats.collectAsState()
    val isAdBlockerActive by viewModel.isAdBlockerEnabled.collectAsState()
    val isHttpsOnlyMode by viewModel.isHttpsOnlyMode.collectAsState()
    val isJavaScriptEnabled by viewModel.isJavaScriptEnabled.collectAsState()
    val isBlockPopups by viewModel.isBlockPopups.collectAsState()
    val imageQuality by viewModel.imageQuality.collectAsState()

    val bookmarks by viewModel.bookmarks.collectAsState()
    val speedDials by viewModel.speedDials.collectAsState()
    val history by viewModel.history.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val savedPages by viewModel.savedPages.collectAsState()

    val webAction by viewModel.webActionTrigger.collectAsState()

    // Overlay Screen States
    val isOperaMenuOpen by viewModel.isOperaMenuOpen.collectAsState()
    val isTabsOverviewOpen by viewModel.isTabsOverviewOpen.collectAsState()
    val isDataSavingsOpen by viewModel.isDataSavingsOpen.collectAsState()
    val isBookmarksOpen by viewModel.isBookmarksOpen.collectAsState()
    val isHistoryOpen by viewModel.isHistoryOpen.collectAsState()
    val isDownloadsOpen by viewModel.isDownloadsOpen.collectAsState()
    val isSavedPagesOpen by viewModel.isSavedPagesOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()

    // In-Page tools
    val isFindInPageOpen by viewModel.isFindInPageOpen.collectAsState()
    val findInPageQuery by viewModel.findInPageQuery.collectAsState()
    val findInPageCurrentIndex by viewModel.findInPageCurrentIndex.collectAsState()
    val findInPageTotalMatches by viewModel.findInPageTotalMatches.collectAsState()

    val isReaderMode by viewModel.isReaderMode.collectAsState()
    val readerArticleTitle by viewModel.readerArticleTitle.collectAsState()
    val readerArticleHtml by viewModel.readerArticleHtml.collectAsState()
    val readerFontSize by viewModel.readerFontSize.collectAsState()

    val totalTabCount = regularTabs.size + privateTabs.size

    // Handle Android System Back Button Gracefully
    BackHandler(
        enabled = isTabsOverviewOpen || isDataSavingsOpen || isBookmarksOpen ||
                isHistoryOpen || isDownloadsOpen || isSavedPagesOpen ||
                isSettingsOpen || isReaderMode || isFindInPageOpen || currentTab.canGoBack
    ) {
        when {
            isSettingsOpen -> viewModel.isSettingsOpen.value = false
            isSavedPagesOpen -> viewModel.isSavedPagesOpen.value = false
            isDownloadsOpen -> viewModel.isDownloadsOpen.value = false
            isHistoryOpen -> viewModel.isHistoryOpen.value = false
            isBookmarksOpen -> viewModel.isBookmarksOpen.value = false
            isDataSavingsOpen -> viewModel.isDataSavingsOpen.value = false
            isTabsOverviewOpen -> viewModel.isTabsOverviewOpen.value = false
            isReaderMode -> viewModel.closeReaderMode()
            isFindInPageOpen -> viewModel.closeFindInPage()
            currentTab.canGoBack -> viewModel.goBack()
        }
    }

    MyApplicationTheme(isPrivateMode = isPrivateMode) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Browser Window Layout
            Scaffold(
                topBar = {
                    OperaTopBar(
                        currentTab = currentTab,
                        urlInputText = urlInputText,
                        isEditingUrl = isEditingUrl,
                        isPrivateMode = isPrivateMode,
                        isAdBlockerActive = isAdBlockerActive,
                        selectedSearchEngine = selectedSearchEngine,
                        onUrlChanged = { viewModel.onUrlInputChanged(it) },
                        onUrlSubmit = { viewModel.loadUrl(it) },
                        onFocusChange = { viewModel.onUrlEditFocus(it) },
                        onReload = { viewModel.reload() },
                        onStop = { viewModel.webActionTrigger.value = BrowserViewModel.WebAction.Stop },
                        onSearchEngineSelect = { viewModel.setSearchEngine(it) },
                        onOpenDataSavingsDashboard = { viewModel.isDataSavingsOpen.value = true },
                        onOpenReaderMode = { viewModel.webActionTrigger.value = BrowserViewModel.WebAction.ExtractReaderMode },
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                    )
                },
                bottomBar = {
                    OperaBottomBar(
                        canGoBack = currentTab.canGoBack,
                        canGoForward = currentTab.canGoForward,
                        tabCount = if (totalTabCount > 0) totalTabCount else 1,
                        isPrivateMode = isPrivateMode,
                        dataSavingMode = dataSavingMode,
                        onBackClick = { viewModel.goBack() },
                        onForwardClick = { viewModel.goForward() },
                        onOperaMenuClick = { viewModel.isOperaMenuOpen.value = true },
                        onTabsClick = { viewModel.isTabsOverviewOpen.value = true },
                        onHomeClick = { viewModel.goHome() }
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                BrowserScreen(
                    currentTab = currentTab,
                    speedDials = speedDials,
                    dataSavingMode = dataSavingMode,
                    dataSavingsStats = dataSavingsStats,
                    isAdBlockerActive = isAdBlockerActive,
                    isPrivateMode = isPrivateMode,
                    imageQuality = imageQuality,
                    webAction = webAction,
                    isFindInPageOpen = isFindInPageOpen,
                    findInPageQuery = findInPageQuery,
                    findInPageCurrentIndex = findInPageCurrentIndex,
                    findInPageTotalMatches = findInPageTotalMatches,
                    isReaderMode = isReaderMode,
                    readerArticleTitle = readerArticleTitle,
                    readerArticleHtml = readerArticleHtml,
                    readerFontSize = readerFontSize,
                    isJavaScriptEnabled = isJavaScriptEnabled,
                    isBlockPopups = isBlockPopups,
                    onPageStarted = { viewModel.onPageStarted(it) },
                    onPageProgress = { viewModel.onPageProgress(it) },
                    onPageFinished = { url, title, canBack, canForward ->
                        viewModel.onPageFinished(url, title, canBack, canForward)
                    },
                    onAdBlocked = { viewModel.onAdBlocked() },
                    onDownloadRequested = { url, fileName, size, mime ->
                        viewModel.addDownload(url, fileName, size, mime)
                    },
                    onSpeedDialClick = { viewModel.loadUrl(it) },
                    onAddSpeedDial = { title, url -> viewModel.addSpeedDial(title, url) },
                    onDeleteSpeedDial = { viewModel.deleteSpeedDial(it) },
                    onOpenBookmarks = { viewModel.isBookmarksOpen.value = true },
                    onOpenHistory = { viewModel.isHistoryOpen.value = true },
                    onOpenDownloads = { viewModel.isDownloadsOpen.value = true },
                    onOpenSavedPages = { viewModel.isSavedPagesOpen.value = true },
                    onOpenDataSavingsDashboard = { viewModel.isDataSavingsOpen.value = true },
                    onFindQueryChanged = { viewModel.startFindInPage(it) },
                    onFindNext = { viewModel.findNext() },
                    onFindPrevious = { viewModel.findPrevious() },
                    onCloseFindInPage = { viewModel.closeFindInPage() },
                    onIncreaseReaderFontSize = { viewModel.readerFontSize.value = (readerFontSize + 2).coerceAtMost(30) },
                    onDecreaseReaderFontSize = { viewModel.readerFontSize.value = (readerFontSize - 2).coerceAtLeast(12) },
                    onCloseReaderMode = { viewModel.closeReaderMode() },
                    onReaderContentExtracted = { title, content -> viewModel.openReaderMode(title, content) },
                    onOfflinePageSaved = { path, size -> viewModel.saveCurrentPageOffline(path, size) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            // Opera Action Bottom Sheet Menu
            OperaMenuDialog(
                isOpen = isOperaMenuOpen,
                currentTab = currentTab,
                dataSavingMode = dataSavingMode,
                dataSavingsStats = dataSavingsStats,
                isAdBlockerActive = isAdBlockerActive,
                isPrivateMode = isPrivateMode,
                onDismiss = { viewModel.isOperaMenuOpen.value = false },
                onOpenBookmarks = { viewModel.isBookmarksOpen.value = true },
                onOpenHistory = { viewModel.isHistoryOpen.value = true },
                onOpenDownloads = { viewModel.isDownloadsOpen.value = true },
                onOpenSavedPages = { viewModel.isSavedPagesOpen.value = true },
                onOpenDataSavingsDashboard = { viewModel.isDataSavingsOpen.value = true },
                onToggleDataSavings = { viewModel.toggleDataSavingsMode() },
                onToggleAdBlocker = { viewModel.toggleAdBlocker() },
                onToggleNightMode = { viewModel.toggleNightMode() },
                onToggleDesktopSite = { viewModel.toggleDesktopSite() },
                onFindInPage = { viewModel.isFindInPageOpen.value = true },
                onAddToSpeedDial = { viewModel.addCurrentPageToBookmarks(isSpeedDial = true) },
                onSavePageOffline = {
                    viewModel.webActionTrigger.value = BrowserViewModel.WebAction.SaveWebArchive
                },
                onOpenSettings = { viewModel.isSettingsOpen.value = true }
            )

            // Overlays: Tabs Overview
            AnimatedVisibility(
                visible = isTabsOverviewOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                TabsOverviewScreen(
                    tabs = if (isPrivateMode) privateTabs else regularTabs,
                    currentTabId = currentTab.id,
                    onSelectTab = { tabId -> viewModel.selectTab(tabId, isPrivateMode) },
                    onCloseTab = { tabId -> viewModel.closeTab(tabId, isPrivateMode) },
                    onNewTab = { isPrivate -> viewModel.openNewTab(isPrivate = isPrivate) },
                    onCloseAllTabs = { viewModel.closeAllTabs(isPrivateMode) },
                    onDone = { viewModel.isTabsOverviewOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlays: Data Savings Dashboard
            AnimatedVisibility(
                visible = isDataSavingsOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                DataSavingsScreen(
                    dataSavingMode = dataSavingMode,
                    dataSavingsStats = dataSavingsStats,
                    isAdBlockerActive = isAdBlockerActive,
                    imageQuality = imageQuality,
                    onSetDataSavingMode = { viewModel.setDataSavingsMode(it) },
                    onToggleAdBlocker = { viewModel.toggleAdBlocker() },
                    onSetImageQuality = { viewModel.setImageQuality(it) },
                    onBack = { viewModel.isDataSavingsOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlays: Bookmarks & Speed Dial
            AnimatedVisibility(
                visible = isBookmarksOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BookmarksScreen(
                    bookmarks = bookmarks,
                    onSelectBookmark = { url ->
                        viewModel.loadUrl(url)
                        viewModel.isBookmarksOpen.value = false
                    },
                    onDeleteBookmark = { viewModel.deleteBookmark(it) },
                    onBack = { viewModel.isBookmarksOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlays: Browsing History
            AnimatedVisibility(
                visible = isHistoryOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                HistoryScreen(
                    history = history,
                    onSelectHistory = { url ->
                        viewModel.loadUrl(url)
                        viewModel.isHistoryOpen.value = false
                    },
                    onDeleteHistory = { viewModel.deleteHistoryItem(it) },
                    onClearAll = { viewModel.clearAllHistory() },
                    onBack = { viewModel.isHistoryOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlays: Downloads Manager
            AnimatedVisibility(
                visible = isDownloadsOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                DownloadsScreen(
                    downloads = downloads,
                    onOpenDownload = { viewModel.openDownloadedFile(it) },
                    onDeleteDownload = { viewModel.deleteDownload(it) },
                    onBack = { viewModel.isDownloadsOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlays: Saved Offline Pages
            AnimatedVisibility(
                visible = isSavedPagesOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                SavedPagesScreen(
                    savedPages = savedPages,
                    onSelectPage = { page ->
                        viewModel.loadUrl(if (page.filePath.isNotBlank()) "file://${page.filePath}" else page.url)
                        viewModel.isSavedPagesOpen.value = false
                    },
                    onDeletePage = { viewModel.deleteSavedPage(it) },
                    onBack = { viewModel.isSavedPagesOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlays: Settings
            AnimatedVisibility(
                visible = isSettingsOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                SettingsScreen(
                    selectedSearchEngine = selectedSearchEngine,
                    dataSavingMode = dataSavingMode,
                    isAdBlockerActive = isAdBlockerActive,
                    isHttpsOnlyMode = isHttpsOnlyMode,
                    isJavaScriptEnabled = isJavaScriptEnabled,
                    isBlockPopups = isBlockPopups,
                    onSearchEngineChange = { viewModel.setSearchEngine(it) },
                    onToggleAdBlocker = { viewModel.toggleAdBlocker() },
                    onToggleHttpsOnly = { viewModel.toggleHttpsOnly() },
                    onToggleJavaScript = { viewModel.toggleJavaScript() },
                    onToggleBlockPopups = { viewModel.toggleBlockPopups() },
                    onClearAllBrowsingData = { viewModel.clearAllBrowsingData() },
                    onBack = { viewModel.isSettingsOpen.value = false },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
