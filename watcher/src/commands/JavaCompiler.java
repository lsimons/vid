package commands;

public class JavaCompiler extends BuildChangeListener {
    public JavaCompiler(String command) {
        super(command);
    }

    @Override
    protected boolean include(String root, String path) {
        return path.startsWith("src");
    }

    @Override
    protected String getAction() {
        return "compiling java";
    }
}
