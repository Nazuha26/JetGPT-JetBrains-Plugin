package com.jetgpt

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefCookie
import javax.swing.JPanel
import javax.swing.BorderFactory
import java.awt.BorderLayout
import java.util.concurrent.TimeUnit

class JetGptToolWindowFactory : ToolWindowFactory {
    private fun setChatGptCookie(sessionToken: String, jbBrowser: JBCefBrowser) {
        val cookie = JBCefCookie(
            "__Secure-next-auth.session-token",
            sessionToken,
            "chatgpt.com",
            "/",
            true,
            true,
            null,
            null,
            false,
            null
        )

        val success = jbBrowser.jbCefCookieManager.setCookie("https://chatgpt.com/", cookie)
        println("setCookie return: $success")

        try {
            val cookies = jbBrowser.jbCefCookieManager
                .getCookies("https://chatgpt.com/", true)
                .get(2, TimeUnit.SECONDS)

            if (cookies.isEmpty()) println("Cookie list is EMPTY.")

            //cookies.forEach { println("Cookie: ${it.name} = ${it.value.take(20)}..., HttpOnly=${it.isHttpOnly}") }

            cookies.find { it.name == "__Secure-next-auth.session-token" }?.let {
                println("Cookie \"__Secure-next-auth.session-token\" SUCCESSFULLY SET")
            } ?: run {
                println("Cookie \"__Secure-next-auth.session-token\" NOT FOUND after set")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Messages.showErrorDialog("Error read cookies: ${e.message}", "JetGPT Plugin")
        }

    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val browser = JBCefBrowser("https://chatgpt.com/")
        JetGptPanelManager.instance.setBrowser(browser)
        val cefBrowser = browser.cefBrowser

        val backAction = object : AnAction("Back", "Go Back", IconLoader.getIcon("/icons/back_16x16.svg", javaClass)) {
            override fun actionPerformed(e: AnActionEvent) {
                if (cefBrowser.canGoBack()) cefBrowser.goBack()
            }

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = cefBrowser.canGoBack()
            }
        }

        val forwardAction = object : AnAction("Forward", "Go Forward", IconLoader.getIcon("/icons/forward_16x16.svg", javaClass)) {
            override fun actionPerformed(e: AnActionEvent) {
                if (cefBrowser.canGoForward()) cefBrowser.goForward()
            }

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = cefBrowser.canGoForward()
            }
        }

        val reloadAction = object : AnAction("Reload", "Reload Page", IconLoader.getIcon("/icons/reload_16x16.svg", javaClass)) {
            override fun actionPerformed(e: AnActionEvent) {
                cefBrowser.reload()
            }
        }

        val homeAction = object : AnAction("Home", "Go to Home Page", IconLoader.getIcon("/icons/home_16x16.svg", javaClass)) {
            override fun actionPerformed(e: AnActionEvent) {
                browser.loadURL("https://chatgpt.com/")
            }
        }

        val loginAction = object : AnAction(
            "Login / Update cookie",
            "Set ChatGPT session token",
            IconLoader.getIcon("/icons/login_16x16.svg", javaClass)
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                val dlg = TokenDialog(e.project, browser)
                if (dlg.showAndGet()) {     // pressed OK
                    val token = dlg.getToken()
                    if (token.isNotEmpty()) {
                        setChatGptCookie(token, browser)
                        browser.loadURL("https://chatgpt.com/")
                    }
                }
            }
        }



        val toolbarLeftGroup = DefaultActionGroup().apply {
            add(backAction)
            add(forwardAction)
            add(reloadAction)
            add(homeAction)
        }

        val toolbarRightGroup = DefaultActionGroup().apply {
            add(loginAction)
        }

        val toolbarLeft = ActionManager.getInstance()
            .createActionToolbar("JetGptToolbarLeft", toolbarLeftGroup, true)
        toolbarLeft.targetComponent = null

        val toolbarRight = ActionManager.getInstance()
            .createActionToolbar("JetGptToolbarRight", toolbarRightGroup, true)
        toolbarRight.targetComponent = null
        //toolbarRight.component.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT / i dk why this doesn't work

        val toolbarPanel = JPanel(BorderLayout()).apply {
            isOpaque = false
            add(toolbarLeft.component, BorderLayout.WEST)
            add(toolbarRight.component, BorderLayout.EAST)
        }

        val panel = JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder()
            add(toolbarPanel, BorderLayout.NORTH)
            add(browser.component, BorderLayout.CENTER)
        }

        val content = ContentFactory.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}