package commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BuildChangeListener implements ChangeListener {
    protected final static int sleep = 200;
    protected final static int window = 1000 * 1000 * 500; // .5 second
    protected final static ReentrantLock lock = new ReentrantLock();
    protected final PrintStream os;
    protected boolean verbose = false;
    protected long lastCompile;
    protected long nextCompile;
    protected String RESET = "";
    protected String RED = "";
    protected String GREEN = "";
    protected String BLUE = "";
    protected String command;

    public BuildChangeListener(PrintStream os, String command) {
        this.command = command;
        this.os = os;
        lastCompile = System.nanoTime();
        nextCompile = lastCompile;
        initColors();
        initVerbose();
        start();
    }

    public BuildChangeListener(String command) {
        this(System.err, command);
    }

    protected String getAction() {
        return "triggered " + command;
    }

    protected ProcessBuilder getCommand() {
        if (verbose) {
            return new ProcessBuilder("./build.sh", "-v", command);
        } else {
            return new ProcessBuilder("./build.sh", command);
        }
    }

    protected boolean include(String root, String path) {
        return true;
    }

    @Override
    public void handle(String root, String path) {
        if (!include(root, path) || globalExclude(path)) {
            return;
        }
        // System.out.println("path = " + path);
        long now = System.nanoTime();
        if (now > nextCompile) {
            nextCompile = now + window;
            // System.out.println("nextCompile = " + (nextCompile - start)/1000/1000 + "ms");
        }
    }

    protected boolean globalExclude(String path) {
        return path.startsWith(".git") || path.endsWith(".tmp");
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        runOnce();
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace(os);
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    protected void initColors() {
        boolean printColors = Boolean.parseBoolean(System.getProperty("clicolor", "false"));
        if (printColors) {
            RESET = "\u001B[0m";
            RED = "\u001B[31m";
            GREEN = "\u001B[1;32m";
            BLUE = "\u001B[1;34m";
        }
    }

    protected void initVerbose() {
        verbose = Boolean.parseBoolean(System.getProperty("verbose", "false"));
    }

    protected void copy(InputStream is, OutputStream os) throws IOException {
        while (is.available() > 0) {
            byte[] buf = new byte[1024];
            int read = is.read(buf);
            if (read > 0) {
                os.write(buf, 0, read);
            }
        }
        os.flush();
    }

    protected void runOnce() throws InterruptedException, IOException {
        long now = System.nanoTime();
        if (now < nextCompile) {
            return;
        }
        if (nextCompile <= lastCompile) {
            return;
        }

        if (lock.isLocked()) {
            return;
        }
        lock.lock();
        try {
            // System.out.println(
            //         "COMPILING since now = " + (now-start)/1000/1000 +
            //                 "ms, nextCompile = " + (nextCompile-start)/1000/1000 +
            //                 "ms, lastCompile = " + (lastCompile-start)/1000/1000 + "ms");
            os.print(BLUE + " " + getAction() + "..." + RESET);
            if (verbose) {
                os.println();
            }
            os.flush();
            lastCompile = nextCompile;
            Process process = startProcess();
            int exitValue = process.waitFor();
            if (verbose) {
                os.print(BLUE + " " + getAction() + ": " + RESET);
            }
            os.flush();
            if (exitValue == 0) {
                os.println(GREEN + "OK" + RESET);
                os.flush();
            } else {
                os.println(RED + "FAIL" + RESET);
                os.flush();
            }
            if (exitValue != 0 || verbose) {
                InputStream is = process.getInputStream();
                copy(is, os);
            }
        } finally {
            lock.unlock();
        }
    }

    protected Process startProcess() throws IOException {
        Process process;
        if (verbose) {
            process = getCommand().redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(
                    ProcessBuilder.Redirect.INHERIT).start();
        } else {
            process = getCommand().redirectErrorStream(true).start();
        }
        return process;
    }
}
