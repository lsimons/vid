package commands;

public class WarBuilder extends BuildChangeListener {
    public WarBuilder(String command) {
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
        return "building war";
    }
}
