package com.chatgpttool.chatgpttool

import com.intellij.ui.jcef.JBCefBrowser

class ChatGptPanelManager private constructor() {
    private var browser: JBCefBrowser? = null

    fun setBrowser(browser: JBCefBrowser) {
        this.browser = browser
    }

    fun getBrowser(): JBCefBrowser? = browser

    companion object {
        val instance = ChatGptPanelManager()
    }
}