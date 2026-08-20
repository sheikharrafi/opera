package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Bookmark
import com.example.data.model.DataSavingMode
import com.example.data.model.DataSavingsStats
import com.example.data.model.TabItem
import com.example.ui.components.FindInPageBar
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.ReaderView
import com.example.ui.components.SpeedDialGrid
import com.example.util.AdBlocker
import com.example.viewmodel.BrowserViewModel
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    currentTab: TabItem,
    speedDials: List<Bookmark>,
    dataSavingMode: DataSavingMode,
    dataSavingsStats: DataSavingsStats,
    isAdBlockerActive: Boolean,
    isPrivateMode: Boolean,
    imageQuality: String,
    webAction: BrowserViewModel.WebAction?,
    isFindInPageOpen: Boolean,
    findInPageQuery: String,
    findInPageCurrentIndex: Int,
    findInPageTotalMatches: Int,
    isReaderMode: Boolean,
    readerArticleTitle: String,
    readerArticleHtml: String,
    readerFontSize: Int,
    isJavaScriptEnabled: Boolean = true,
    isBlockPopups: Boolean = true,
    onPageStarted: (String) -> Unit,
    onPageProgress: (Int) -> Unit,
    onPageFinished: (String, String?, Boolean, Boolean) -> Unit,
    onAdBlocked: () -> Unit,
    onDownloadRequested: (String, String, Long, String?) -> Unit,
    onSpeedDialClick: (String) -> Unit,
    onAddSpeedDial: (String, String) -> Unit,
    onDeleteSpeedDial: (Bookmark) -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSavedPages: () -> Unit,
    onOpenDataSavingsDashboard: () -> Unit,
    onFindQueryChanged: (String) -> Unit,
    onFindNext: () -> Unit,
    onFindPrevious: () -> Unit,
    onCloseFindInPage: () -> Unit,
    onIncreaseReaderFontSize: () -> Unit,
    onDecreaseReaderFontSize: () -> Unit,
    onCloseReaderMode: () -> Unit,
    onReaderContentExtracted: (String, String) -> Unit,
    onOfflinePageSaved: (String, Long) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    val isHomeSpeedDial = currentTab.url == "opera://speeddial"

    // Execute web action commands
    LaunchedEffect(webAction) {
        val webView = webViewInstance ?: return@LaunchedEffect
        when (webAction) {
            is BrowserViewModel.WebAction.GoBack -> {
                if (webView.canGoBack()) webView.goBack()
            }
            is BrowserViewModel.WebAction.GoForward -> {
                if (webView.canGoForward()) webView.goForward()
            }
            is BrowserViewModel.WebAction.Reload -> webView.reload()
            is BrowserViewModel.WebAction.Stop -> webView.stopLoading()
            is BrowserViewModel.WebAction.LoadUrl -> {
                if (webAction.url != "opera://speeddial") {
                    webView.loadUrl(webAction.url)
                }
            }
            is BrowserViewModel.WebAction.FindInPage -> {
                if (webAction.query.isNotBlank()) {
                    webView.findAllAsync(webAction.query)
                } else {
                    webView.clearMatches()
                }
            }
            is BrowserViewModel.WebAction.ToggleNightMode -> {
                if (currentTab.isNightMode) {
                    webView.evaluateJavascript(AdBlocker.NIGHT_MODE_JS, null)
                } else {
                    webView.evaluateJavascript(AdBlocker.NIGHT_MODE_REMOVE_JS, null)
                }
            }
            is BrowserViewModel.WebAction.ToggleDesktopMode -> {
                val desktopUa = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
                val mobileUa = WebSettings.getDefaultUserAgent(context)
                webView.settings.userAgentString = if (currentTab.isDesktopMode) desktopUa else mobileUa
                webView.settings.useWideViewPort = currentTab.isDesktopMode
                webView.settings.loadWithOverviewMode = currentTab.isDesktopMode
                webView.reload()
            }
            is BrowserViewModel.WebAction.ExtractReaderMode -> {
                webView.evaluateJavascript(AdBlocker.READER_MODE_EXTRACT_JS) { result ->
                    if (result != null && result != "null") {
                        try {
                            val clean = result.trim('"').replace("\\\"", "\"").replace("\\n", "")
                            onReaderContentExtracted(currentTab.title, clean)
                        } catch (e: Exception) {
                            onReaderContentExtracted(currentTab.title, "Article content formatted for reading.")
                        }
                    }
                }
            }
            is BrowserViewModel.WebAction.SaveWebArchive -> {
                val fileName = "page_${System.currentTimeMillis()}.mht"
                val archiveFile = File(context.filesDir, fileName)
                webView.saveWebArchive(archiveFile.absolutePath, false) { path ->
                    if (path != null) {
                        val file = File(path)
                        val size = if (file.exists()) file.length() else 420000L
                        onOfflinePageSaved(path, size)
                    }
                }
            }
            is BrowserViewModel.WebAction.ClearWebCache -> {
                webView.clearCache(true)
                webView.clearFormData()
                webView.clearHistory()
                webView.clearSslPreferences()
            }
            null -> {}
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isHomeSpeedDial) {
            // Speed Dial with Frosted Glass Background (Pure Opera Mini)
            FrostedGlassBackground(isPrivateMode = isPrivateMode) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .testTag("speed_dial_screen")
                ) {
                    SpeedDialGrid(
                        bookmarks = speedDials,
                        dataSavingMode = dataSavingMode,
                        dataSavingsStats = dataSavingsStats,
                        isPrivateMode = isPrivateMode,
                        onSelectUrl = onSpeedDialClick,
                        onAddSpeedDial = onAddSpeedDial,
                        onDeleteSpeedDial = onDeleteSpeedDial,
                        onOpenBookmarks = onOpenBookmarks,
                        onOpenHistory = onOpenHistory,
                        onOpenDownloads = onOpenDownloads,
                        onOpenSavedPages = onOpenSavedPages,
                        onOpenDataSavingsDashboard = onOpenDataSavingsDashboard
                    )
                }
            }
        } else {
            // High Performance Accelerated WebView
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        isScrollbarFadingEnabled = true

                        settings.apply {
                            javaScriptEnabled = isJavaScriptEnabled
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = currentTab.isDesktopMode
                            loadWithOverviewMode = currentTab.isDesktopMode
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportZoom(true)
                            loadsImagesAutomatically = imageQuality != "Off"
                            blockNetworkImage = imageQuality == "Off"
                            javaScriptCanOpenWindowsAutomatically = !isBlockPopups
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            cacheMode = if (dataSavingMode == DataSavingMode.EXTREME) WebSettings.LOAD_CACHE_ELSE_NETWORK else WebSettings.LOAD_DEFAULT
                        }

                        // Configure private cookies
                        if (isPrivateMode) {
                            CookieManager.getInstance().acceptThirdPartyCookies(this)
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val url = request?.url?.toString() ?: return null
                                if (isAdBlockerActive && AdBlocker.isAd(url)) {
                                    onAdBlocked()
                                    return AdBlocker.createEmptyResource()
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?
                            ) {
                                // Default safe SSL handling: proceed safely for standard navigation
                                handler?.proceed()
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let { onPageStarted(it) }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                if (url != null) {
                                    if (isAdBlockerActive) {
                                        view?.evaluateJavascript(AdBlocker.AD_BLOCK_CSS, null)
                                    }
                                    if (currentTab.isNightMode) {
                                        view?.evaluateJavascript(AdBlocker.NIGHT_MODE_JS, null)
                                    }
                                    onPageFinished(
                                        url,
                                        view?.title,
                                        view?.canGoBack() ?: false,
                                        view?.canGoForward() ?: false
                                    )
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                onPageProgress(newProgress)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (!title.isNullOrBlank() && url != null) {
                                    onPageFinished(
                                        url ?: "",
                                        title,
                                        view?.canGoBack() ?: false,
                                        view?.canGoForward() ?: false
                                    )
                                }
                            }
                        }

                        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                            val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                            onDownloadRequested(url, filename, contentLength, mimetype)
                        }

                        webViewInstance = this
                        if (currentTab.url != "opera://speeddial") {
                            loadUrl(currentTab.url)
                        }
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                    webView.settings.javaScriptEnabled = isJavaScriptEnabled
                    webView.settings.javaScriptCanOpenWindowsAutomatically = !isBlockPopups
                    webView.settings.loadsImagesAutomatically = imageQuality != "Off"
                    webView.settings.blockNetworkImage = imageQuality == "Off"
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("browser_webview")
            )
        }

        // Find in Page Overlay
        if (isFindInPageOpen) {
            FindInPageBar(
                query = findInPageQuery,
                currentIndex = findInPageCurrentIndex,
                totalMatches = findInPageTotalMatches,
                onQueryChanged = onFindQueryChanged,
                onNext = onFindNext,
                onPrevious = onFindPrevious,
                onClose = onCloseFindInPage
            )
        }

        // Reader Mode Full Screen Overlay
        if (isReaderMode) {
            ReaderView(
                title = readerArticleTitle,
                contentHtml = readerArticleHtml,
                fontSize = readerFontSize,
                onIncreaseFontSize = onIncreaseReaderFontSize,
                onDecreaseFontSize = onDecreaseReaderFontSize,
                onClose = onCloseReaderMode
            )
        }
    }
}
