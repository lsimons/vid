package commands;

public class TestRunner extends BuildChangeListener {
    public TestRunner(String command) {
        super(command);
    }

    @Override
    protected boolean include(String root, String path) {
        return path.startsWith("src") ||
                path.startsWith("lib") ||
                path.startsWith("test");
    }

    @Override
    protected String getAction() {
        return "running tests";
    }
}
