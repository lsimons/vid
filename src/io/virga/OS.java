package io.virga;

@SuppressWarnings("UnusedDeclaration")
public class OS {
    public static String OS_NAME = System.getProperty("os.name", "unknown");
    public static boolean IS_MAC = OS_NAME.startsWith("Mac");
    public static boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
    public static boolean IS_LINUX = OS_NAME.startsWith("Linux") || OS_NAME.startsWith("LINUX");

    static {
        try {
            Class.forName("com.apple.eawt.Application");
        } catch (ClassNotFoundException e) {
            IS_MAC = false;
        }
    }
}
