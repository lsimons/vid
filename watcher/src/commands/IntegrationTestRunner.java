package commands;

public class IntegrationTestRunner extends BuildChangeListener {
    public IntegrationTestRunner(String command) {
        super(command);
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
        return "running integration tests";
    }
}
