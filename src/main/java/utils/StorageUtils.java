package utils;

import java.io.File;
import java.nio.file.Paths;

public class StorageUtils {

    private static final String APP_NAME = "DevShelf";

    /**
     * Returns the folder where we can safely WRITE data (logs, popularity).
     * On Windows: C:\Users\Name\AppData\Roaming\DevShelf
     * On Mac/Linux: UserHome/.DevShelf
     */
    public static String getAppDataDir() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        File appDataDir;
        if (os.contains("win")) {
            // Windows: Use AppData
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                appDataDir = new File(appData, APP_NAME);
            } else {
                appDataDir = new File(userHome, APP_NAME);
            }
        } else {
            // Mac/Linux: Use hidden folder in home
            appDataDir = new File(userHome, "." + APP_NAME);
        }

        // Create the directory if it doesn't exist
        if (!appDataDir.exists()) {
            appDataDir.mkdirs();
        }

        return appDataDir.getAbsolutePath();
    }
}
