package com.jetgpt.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.jetgpt.JetGptPanelManager
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon
import javax.swing.JOptionPane
import org.json.JSONObject


class SendToJetGptAction : AnAction("Send to JetGPT", "Send selected code to JetGPT", icon) {
    companion object {
        val icon: Icon = IconLoader.getIcon("/icons/chatgpt_16x16.svg", SendToJetGptAction::class.java)
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

        val jsSafeText = JSONObject.quote(selectedText)

        val jbBrowser = JetGptPanelManager.instance.getBrowser()
        if (jbBrowser == null) {
            JOptionPane.showMessageDialog(null, "JetGPT ToolWindow not initialized.")
            return
        }

        jbBrowser.cefBrowser.executeJavaScript(
            """
            (function() {
                const editor = document.querySelector('div.ProseMirror[id="prompt-textarea"]');
                if (editor) {
                    const existing = editor.innerText.trim();
                    const selected = $jsSafeText;
                    
                    const toInsert = existing
                        ? existing + "\n\n" + selected + "\n\n\n"
                        : selected + "\n\n\n";
        
                    editor.innerText = toInsert;
                    editor.dispatchEvent(new InputEvent('input', { bubbles: true }));
                    editor.focus();
                    
                    // set cursor to the end of the prompt area
                    const range = document.createRange();
                    range.selectNodeContents(editor);
                    range.collapse(false);

                    const selection = window.getSelection();
                    selection.removeAllRanges();
                    selection.addRange(range);
                } else {
                    console.log("=== ERR - NOT FOUND editor .ProseMirror");
                }
            })();
            """.trimIndent(),
            jbBrowser.cefBrowser.url,
            0
        )

        jbBrowser.component.requestFocusInWindow()
    }
}
