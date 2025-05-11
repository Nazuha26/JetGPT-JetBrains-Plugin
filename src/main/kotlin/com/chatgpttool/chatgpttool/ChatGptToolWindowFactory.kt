package com.chatgpttool.chatgpttool

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import javax.swing.JPanel
import javax.swing.BorderFactory
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton

class ChatGptToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val browser = JBCefBrowser("https://chat.openai.com/")
        ChatGptPanelManager.instance.setBrowser(browser)
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
                browser.loadURL("https://chat.openai.com/")
            }
        }

        val actionGroup = DefaultActionGroup().apply {
            add(backAction)
            add(forwardAction)
            add(reloadAction)
            add(homeAction)
        }

        val toolbar = ActionManager.getInstance()
            .createActionToolbar("ChatGptToolbar", actionGroup, true) // true = horizontal
        toolbar.targetComponent = null

        val toolbarPanel = NonOpaquePanel().apply {
            layout = BorderLayout()
            add(toolbar.component, BorderLayout.WEST)
        }

        val panel = JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder()
            add(toolbarPanel, BorderLayout.NORTH)
            add(browser.component, BorderLayout.CENTER)
        }

        val content = ContentFactory.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true   // show in all projects
}