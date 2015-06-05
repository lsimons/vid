import commands.BuildChangeListener;
import commands.ChangeListener;
import commands.IntegrationTestRunner;
import commands.JavaCompiler;
import commands.JavascriptCompiler;
import commands.TestRunner;
import commands.WarBuilder;
import commands.WarRunner;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;

public class MainWatcher implements Watcher {
    private Watcher delegate;
    private ChangeListener changeListener;

    public MainWatcher(ChangeListener changeListener) throws IOException {
        this.changeListener = changeListener;
        try {
            init();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException 
                e) {
            throw new RuntimeException("Cannot set up watcher", e);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String cmd = "war-watch";
        if (args.length == 1) {
            cmd = args[0];
        } else if (args.length > 1) {
            usage();
        }

        BuildChangeListener changeListener;

        if ("js-watch".equals(cmd)) {
            changeListener = new JavascriptCompiler("js-compile");
        } else if ("compile-watch".equals(cmd)) {
            changeListener = new JavaCompiler("compile");
        } else if ("test-watch".equals(cmd)) {
            changeListener = new TestRunner("test");
        } else if ("integration-test-watch".equals(cmd)) {
            changeListener = new IntegrationTestRunner("integration-test");
        } else if ("war-watch".equals(cmd)) {
            changeListener = new WarBuilder("war");
        } else if ("run-watch".equals(cmd)) {
            changeListener = new WarRunner("run");
        } else {
            usage();
            throw new RuntimeException(); // never happens...
        }
        MainWatcher watcher = new MainWatcher(changeListener);
        changeListener.start();
        watcher.start();
        watcher.loop();
    }

    private static void usage() {
        System.err.println("Usage: java -cp ... [-Dclicolor=true] [-Dverbose=true]" +
                MainWatcher.class.getSimpleName() + " [command]");
        System.err.flush();
        System.exit(1);
    }

    @Override
    public void loop() throws InterruptedException, IOException {
        delegate.loop();
    }

    @Override
    public void start() throws InterruptedException, IOException {
        delegate.start();
    }

    private void init()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException,
            IOException {
        Path cwd = Paths.get("");
        try {
            Class<?> macOSWatcher = Class.forName("MacOSWatcher");
            Constructor constructor = macOSWatcher.getConstructor(com.barbarysoftware.watchservice.WatchService.class,
                    Path.class, ChangeListener.class);
            com.barbarysoftware.watchservice.WatchService watchService =
                    com.barbarysoftware.watchservice.WatchService.newWatchService();
            delegate = (Watcher) constructor.newInstance(watchService, cwd, changeListener);
        } catch (ClassNotFoundException e) {
            FileSystem fs = FileSystems.getDefault();
            WatchService ws = fs.newWatchService();
            delegate = new JDKWatcher(ws, cwd, changeListener);
        }
    }
}
