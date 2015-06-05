package commands;

public class JavascriptCompiler extends BuildChangeListener {
    public JavascriptCompiler(String command) {
        super(command);
    }

    @Override
    protected boolean include(String root, String path) {
        return path.endsWith(".js") && !path.endsWith("min.js");
    }

    @Override
    protected String getAction() {
        return "compiling javascript";
    }
}
