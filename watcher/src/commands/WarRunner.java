package commands;

import java.io.IOException;

public class WarRunner extends BuildChangeListener {
    private Process jetty;

    public WarRunner(String command) throws IOException {
        super(command);
        startJetty();
        final WarRunner ref = this;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ref.jetty.destroy();
                try {
                    ref.jetty.waitFor();
                } catch (InterruptedException e) {
                    // ok
                }
            }
        });
    }

    @Override
    protected boolean include(String root, String path) {
        return path.startsWith("src") ||
                path.startsWith("lib") ||
                path.startsWith("test") ||
                (path.startsWith("web") && !path.equals("web") // ./web changes if ./web/min.js changes
                        && !path.endsWith("min.js") && !path.endsWith(".js.map"));
    }

    @Override
    protected String getAction() {
        return "kick off rebuilding and reloading war";
    }

    protected Process startProcess() throws IOException {
        Process dummy = new ProcessBuilder("echo", "OK").start();
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
                try {
                    restartJetty();
                } catch (IOException e) {
                    e.printStackTrace(os);
                }
            }
        }).start();
        return dummy;
    }

    private void startJetty() throws IOException {
        jetty = getCommand().redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(
                ProcessBuilder.Redirect.INHERIT).start();
    }

    private void restartJetty() throws IOException {
        jetty.destroy();
        try {
            jetty.waitFor();
            startJetty();
        } catch (InterruptedException e) {
            // ok
        }
    }
}
