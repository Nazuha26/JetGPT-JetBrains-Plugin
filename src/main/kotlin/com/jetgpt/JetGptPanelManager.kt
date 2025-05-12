package com.jetgpt

import com.intellij.ui.jcef.JBCefBrowser

class JetGptPanelManager private constructor() {
    private var browser: JBCefBrowser? = null

    fun setBrowser(browser: JBCefBrowser) {
        this.browser = browser
    }

    fun getBrowser(): JBCefBrowser? = browser

    companion object {
        val instance = JetGptPanelManager()
    }
}