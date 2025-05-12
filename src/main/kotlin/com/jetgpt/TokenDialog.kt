package com.jetgpt

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Font
import java.util.concurrent.TimeUnit
import javax.swing.*

class TokenDialog(project: com.intellij.openapi.project.Project?, private val browser: JBCefBrowser? = null) : DialogWrapper(project) {
    private val tokenArea = JTextArea(4, 40)

    init {
        title = "ChatGPT Login"
        init()
        setSize(780, 480)

        tokenArea.lineWrap = true
        tokenArea.wrapStyleWord = true
        tokenArea.margin = JBUI.insets(6)
        tokenArea.font = Font("Monospaced", Font.PLAIN, 12)

        SwingUtilities.invokeLater {
            loadExistingToken()
            tokenArea.requestFocusInWindow()
        }
    }

    override fun createCenterPanel(): JComponent = JPanel().apply {
        border = JBUI.Borders.empty(10)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(JLabel(
            """
            <html>
            1. In your browser, go to <a href='https://chatgpt.com' style='color:#3399FF; text-decoration: underline;'>https://chatgpt.com</a> and sign in.<br>
            2. Press <b>F12 → Application</b> tab → Cookies → https://chatgpt.com.<br>
            3. Copy the value of "<b>__Secure-next-auth.session-token</b>".<br>
            4. Paste it below and click OK.
            </html>
            """.trimIndent()
        ))

        add(Box.createVerticalStrut(10))
        add(JScrollPane(tokenArea))
    }

    fun getToken(): String = tokenArea.text.trim()

    private fun loadExistingToken() {
        try {
            val cookie = browser?.jbCefCookieManager
                ?.getCookies("https://chatgpt.com/", true)
                ?.get(2, TimeUnit.SECONDS)
                ?.find { it.name == "__Secure-next-auth.session-token" }

            if (cookie != null) {
                tokenArea.text = cookie.value
            } else {
                tokenArea.text = ""
                tokenArea.toolTipText = "Paste your ChatGPT session token here"
            }
        } catch (e: Exception) {
            tokenArea.text = ""
        }
    }
}
