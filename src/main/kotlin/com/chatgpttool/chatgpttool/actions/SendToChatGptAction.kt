package com.chatgpttool.chatgpttool.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.chatgpttool.chatgpttool.ChatGptPanelManager
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon
import javax.swing.JOptionPane

class SendToChatGptAction : AnAction("Send to ChatGPT", "Send selected code to ChatGPT", icon) {
    companion object {
        val icon: Icon = IconLoader.getIcon("/icons/chatgpt_16x16.svg", SendToChatGptAction::class.java)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val hasSelection = editor?.selectionModel?.hasSelection() == true
        e.presentation.isEnabledAndVisible = hasSelection
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText ?: return

        val safeText = selectedText
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")

        // Достаём браузер из панели
        val jbBrowser = ChatGptPanelManager.instance.getBrowser()
        if (jbBrowser == null) {
            JOptionPane.showMessageDialog(null, "ChatGPT ToolWindow not initialized.")
            return
        }

        jbBrowser.cefBrowser.executeJavaScript(
            """
            (function() {
                const editor = document.querySelector('div.ProseMirror[id="prompt-textarea"]');
                if (editor) {
                    editor.innerText = "$safeText";
                    editor.dispatchEvent(new InputEvent('input', { bubbles: true }));
                } else {
                    console.log("=== ERR - NOT FOUND editor .ProseMirror");
                }
            })();
            """.trimIndent(),
            jbBrowser.cefBrowser.url,
            0
        )
    }
}
