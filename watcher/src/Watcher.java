import java.io.IOException;

public interface Watcher {
    String[] ignored = new String[]{".git", ".idea", "out", "watcher", "jetty-runner"};

    void loop() throws InterruptedException, IOException;

    void start() throws InterruptedException, IOException;
}
