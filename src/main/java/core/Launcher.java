package core;

public class Launcher {
    public static void main(String[] args) {
        // We delegate to the real JavaFX class.
        // This tricks the JVM into loading the classpath correctly first.
        GuiMain.main(args);
    }
}