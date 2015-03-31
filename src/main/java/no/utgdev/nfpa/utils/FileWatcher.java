package no.utgdev.nfpa.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcher {
    private final Path path;
    private final FileSystem fileSystem;
    private final WatchService watcher;

    public FileWatcher(Path path, Function task) throws IOException, InterruptedException {
        this.path = path;
        this.fileSystem = FileSystems.getDefault();
        this.watcher = fileSystem.newWatchService();
        this.path.register(this.watcher, ENTRY_MODIFY);

        new Timer("filewatcher", true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                WatchKey watchKey = watcher.poll();
                if (watchKey == null) {
                    return;
                }
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

                for (WatchEvent watchEvent : watchEvents) {
                    WatchEvent<Path> event = (WatchEvent<Path>) watchEvent;
                    String filepath = event.context().toString();

                    if (!filepath.contains("___jb_")) {
                        if (task != null) {
                            task.apply(filepath);
                        }
                    }
                }
                watchKey.reset();
            }
        }, 1000, 1000);
    }
}
