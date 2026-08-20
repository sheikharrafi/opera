package com.example.util

import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdBlocker {
    private val AD_DOMAINS = setOf(
        "doubleclick.net",
        "googlesyndication.com",
        "googleadservices.com",
        "pagead2.googlesyndication.com",
        "adservice.google.com",
        "adnxs.com",
        "outbrain.com",
        "taboola.com",
        "scorecardresearch.com",
        "criteo.com",
        "quantserve.com",
        "zedo.com",
        "moatads.com",
        "rubiconproject.com",
        "pubmatic.com",
        "advertising.com",
        "popads.net",
        "propellerads.com",
        "adcolony.com",
        "unityads.unity3d.com"
    )

    fun isAd(url: String): Boolean {
        val lower = url.lowercase()
        return AD_DOMAINS.any { lower.contains(it) }
    }

    fun createEmptyResource(): WebResourceResponse {
        return WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
    }

    const val AD_BLOCK_CSS = """
        javascript:(function() {
            var css = '.adsbygoogle, .ad-unit, [id^="google_ads_"], [class*="sponsored-post"], .trc_related_container, .taboola-ad, .outbrain-ad { display: none !important; height: 0 !important; }';
            var style = document.createElement('style');
            style.type = 'text/css';
            style.appendChild(document.createTextNode(css));
            document.head.appendChild(style);
        })()
    """

    const val NIGHT_MODE_JS = """
        javascript:(function() {
            var id = 'opera-mini-night-mode-style';
            var existing = document.getElementById(id);
            if (!existing) {
                var css = 'html { filter: invert(90%) hue-rotate(180deg) !important; background-color: #121212 !important; } img, video, iframe, canvas { filter: invert(100%) hue-rotate(180deg) !important; }';
                var style = document.createElement('style');
                style.id = id;
                style.type = 'text/css';
                style.appendChild(document.createTextNode(css));
                document.head.appendChild(style);
            }
        })()
    """

    const val NIGHT_MODE_REMOVE_JS = """
        javascript:(function() {
            var existing = document.getElementById('opera-mini-night-mode-style');
            if (existing) existing.remove();
        })()
    """

    const val READER_MODE_EXTRACT_JS = """
        (function() {
            var article = document.querySelector('article') || document.querySelector('main') || document.querySelector('.post-content') || document.querySelector('.article-body') || document.body;
            var title = document.querySelector('h1')?.innerText || document.title;
            var paragraphs = article.querySelectorAll('p, h2, h3, blockquote');
            var text = '';
            paragraphs.forEach(function(p) {
                if (p.innerText.trim().length > 20) {
                    text += '<p>' + p.innerHTML + '</p>';
                }
            });
            return JSON.stringify({ title: title, content: text });
        })()
    """
}
