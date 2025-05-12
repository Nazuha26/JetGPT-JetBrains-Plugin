# JetGPT v1.0.0

JetGPT is a **simple AI helper** inside your JetBrains IDE.  
It opens ChatGPT in a side Tool Window, so you can talk to it without leaving the code editor.

> ⚠️ This project is not official. It is **not** made by OpenAI and is **not** approved by them.

---

## ✨ Features

| Feature                   | Description                                                                      |
|---------------------------|----------------------------------------------------------------------------------|
| **Chat panel**            | Shows the regular ChatGPT web page inside the IDE.                               |
| **Navigation buttons**    | Go Back / Forward / Reload / Home - like in a browser.                           |
| **Login with cookie**     | Paste your `__Secure-next-auth.session-token` once and stay signed in.           |
| **Send code to ChatGPT**  | Right-click selected code → "Send to JetGPT" (or use `Ctrl + Shift + G`).        |
| **Multi-IDE support**     | Works in IntelliJ IDEA, PyCharm, CLion, Rider, and more (from 2023.2 to 2024.3). |

---

## 📦 Installation

1. Download the **latest version** from the [Releases](https://github.com/Nazuha26/JetGPT-JetBrains-Plugin/releases) page.
2. In your IDE:  
   go to **Settings → Plugins → Install Plugin from Disk...**
3. Select the downloaded `.zip` file.
4. Click OK and restart the IDE.

---

## How to Use

1. Open the **JetGPT** Tool Window from the right sidebar.
2. Click **Login / Update cookie**.
3. Paste your ChatGPT session token (`__Secure-next-auth.session-token`).
4. Start chatting directly in the panel!
5. To quickly send code into the prompt:
    - Select the code in the editor.
    - Right-click → **Send to JetGPT**.
    - Or press **Ctrl + Shift + G**.

---

## Privacy

JetGPT **does not store** or send any data anywhere except to the official ChatGPT website.  
It uses an embedded browser (JCEF) inside your IDE.

---

## License

This project is licensed under the MIT License.

“ChatGPT” is a trademark of OpenAI.  
JetGPT is not affiliated with or endorsed by OpenAI.