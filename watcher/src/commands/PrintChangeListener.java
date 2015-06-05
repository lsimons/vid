package commands;

@SuppressWarnings("UnusedDeclaration")
public class PrintChangeListener implements ChangeListener {
    @Override
    public void handle(String root, String path) {
        if ("".equals(path)) {
            path = ".";
        }
        System.out.println(path);
    }
}
