import com.barbarysoftware.watchservice.StandardWatchEventKind;
import com.barbarysoftware.watchservice.WatchEvent;
import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.barbarysoftware.watchservice.WatchableFile;
import commands.ChangeListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class MacOSWatcher implements Watcher {
    protected String[] absoluteIgnored = new String[ignored.length];
    protected Map<WatchKey, Path> keys = new HashMap<>();
    protected Path root;
    protected String rootString;
    protected WatchService ws;
    private ChangeListener changeListener;

    public MacOSWatcher(WatchService ws, Path root, ChangeListener changeListener) {
        this.ws = ws;
        this.root = root;
        this.rootString = root.toAbsolutePath().toString();
        for (int i = 0; i < ignored.length; i++) {
            absoluteIgnored[i] = rootString + File.separator + ignored[i];
        }
        this.changeListener = changeListener;
    }

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

                if (kind == StandardWatchEventKind.OVERFLOW) {
                    continue;
                }

                WatchEvent<File> pathEvent = (WatchEvent<File>) event;
                File childPath = pathEvent.context();
                Path filePath = parent.resolve(childPath.toPath());
                /*
                
                barbarywatchservice mac implementation auto-recurses
                
                if (kind == StandardWatchEventKind.ENTRY_CREATE && Files.isDirectory(filePath)) {
                    registerTree(ws, filePath);
                }
                */

                String filePathString = filePath.toAbsolutePath().toString();
                boolean shouldIgnore = false;
                for (String ignore : absoluteIgnored) {
                    if (filePathString.startsWith(ignore)) {
                        shouldIgnore = true;
                        break;
                    }
                }

                if (!shouldIgnore) {
                    if (filePathString.startsWith(rootString)) {
                        filePathString = filePathString.substring(rootString.length());
                    }
                    if (filePathString.startsWith(File.separator)) {
                        filePathString = filePathString.substring(1);
                    }
                    handle(filePathString);
                }
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

    public void start() throws InterruptedException, IOException {
        registerTree(ws, root);
    }

    protected void handle(String path) {
        changeListener.handle(rootString, path);
    }

    protected void register(WatchService ws, Path path) throws IOException {
        WatchableFile file = new WatchableFile(path.toFile());
        WatchKey key = file.register(ws, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_MODIFY,
                StandardWatchEventKind.ENTRY_DELETE);
        keys.put(key, path);
    }

    protected void registerTree(final WatchService ws, final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return;
        }
        register(ws, path);
    }
}
