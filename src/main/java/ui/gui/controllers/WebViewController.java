package ui.gui.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebViewController {

    @FXML private WebView webView;

    // Store the original URL in case we need to open it externally
    private String currentUrl;

    public void loadUrl(String url) {
        this.currentUrl = url;
        WebEngine engine = webView.getEngine();
        engine.setJavaScriptEnabled(true); // Viewer needs JS

        System.out.println("🌐 Original URL: " + url);

        if (isPdf(url)) {
            // 🛠️ THE FIX: Wrap the PDF in Google Docs Viewer
            // This converts the PDF to HTML so WebView can show it.
            try {
                String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
                String viewerUrl = "https://docs.google.com/gview?embedded=true&url=" + encodedUrl;

                System.out.println("📄 PDF detected. Loading via Viewer: " + viewerUrl);
                engine.load(viewerUrl);
            } catch (Exception e) {
                e.printStackTrace();
                engine.load(url); // Fallback
            }
        } else {
            // It's a normal website, load it directly
            engine.load(url);
        }
    }

    private boolean isPdf(String url) {
        return url.toLowerCase().endsWith(".pdf");
    }
    @FXML
    private void handleOpenExternal() {
        if (currentUrl != null) {
            try {
                // This opens the user's default system browser (Chrome/Edge)
                java.awt.Desktop.getDesktop().browse(new java.net.URI(currentUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleClose() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}