package utils;

import utils.StorageUtils;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class UpdateService {

    // 🔴 REPLACE THIS with your GitHub Raw URL
    private static final String BASE_URL = "https://raw.githubusercontent.com/Kas-sim/DevShelf-Data/refs/heads/main/book.json";

    private static final String[] FILES = {"book.json", "index_data.json", "version.txt"};

    public void checkForUpdates() {
        System.out.println("☁️ Checking for book updates...");

        String appData = StorageUtils.getAppDataDir();
        HttpClient client = HttpClient.newHttpClient();

        try {
            // 1. Check Remote Version
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "version.txt"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String remoteVersion = response.body().trim();

            // 2. Check Local Version
            File localVersionFile = new File(appData, "version.txt");
            String localVersion = "0.0";
            if (localVersionFile.exists()) {
                localVersion = Files.readString(localVersionFile.toPath()).trim();
            }

            // 3. Compare
            if (!remoteVersion.equals(localVersion)) {
                System.out.println("🔄 New content found! Updating to v" + remoteVersion);
                downloadFiles(client, appData);
            } else {
                System.out.println("✅ Books are up to date.");
            }

        } catch (Exception e) {
            System.err.println("⚠️ Update check failed (User might be offline): " + e.getMessage());
            // It's okay, just use local files
        }
    }

    private void downloadFiles(HttpClient client, String appDataPath) {
        for (String fileName : FILES) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + fileName))
                        .build();

                Path target = Path.of(appDataPath, fileName);

                client.send(req, HttpResponse.BodyHandlers.ofFile(target));
                System.out.println("📥 Downloaded: " + fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}