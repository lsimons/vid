import commands.ChangeListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class JDKWatcher implements Watcher {
    protected String[] absoluteIgnored = new String[ignored.length];
    protected Map<WatchKey, Path> keys = new HashMap<>();
    protected Path root;
    protected String rootString;
    protected WatchService ws;
    private ChangeListener changeListener;

    public JDKWatcher(WatchService ws, Path root, ChangeListener changeListener) {
        this.ws = ws;
        this.root = root;
        this.rootString = root.toAbsolutePath().toString();
        for (int i = 0; i < ignored.length; i++) {
            absoluteIgnored[i] = rootString + File.separator + ignored[i];
        }
        this.changeListener = changeListener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loop() throws InterruptedException, IOException {
        while (true) {
            WatchKey key = ws.take();
            Path parent = keys.get(key);
            if (parent == null) {
                key.cancel();
                continue;
            }

            List<WatchEvent<?>> events = key.pollEvents();
            for (WatchEvent<?> event : events) {
                WatchEvent.Kind kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path childPath = pathEvent.context();
                Path filePath = parent.resolve(childPath);
                if (kind == ENTRY_CREATE && Files.isDirectory(filePath)) {
                    registerTree(ws, filePath);
                }

                String filePathString = filePath.toAbsolutePath().toString();
                if (filePathString.startsWith(rootString)) {
                    filePathString = filePathString.substring(rootString.length());
                }
                if (filePathString.startsWith(File.separator)) {
                    filePathString = filePathString.substring(1);
                }
                handle(filePathString);
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
            }

            if (keys.isEmpty()) {
                break;
            }
        }
    }

    @Override
    public void start() throws InterruptedException, IOException {
        registerTree(ws, root);
    }

    protected void handle(String path) {
        changeListener.handle(rootString, path);
    }

    protected void register(WatchService ws, Path path) throws IOException {
        WatchKey key = path.register(ws, ENTRY_CREATE, ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        keys.put(key, path);
    }

    protected void registerTree(final WatchService ws, final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String currentString = dir.toAbsolutePath().toString();
                for (String ignore : absoluteIgnored) {
                    if (currentString.startsWith(ignore)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }
                register(ws, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
