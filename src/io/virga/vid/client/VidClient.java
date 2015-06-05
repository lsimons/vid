package io.virga.vid.client;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.nio.file.Paths;

public class VidClient {
    private final int port;

    public VidClient(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 64646;
        
        VidClient client = new VidClient(port);
        client.startTomcat();
        client.startClient();
    }

    private void startTomcat() throws LifecycleException {
        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        
        /* Context ctx = */ tomcat.addWebapp(null, "",
                Paths.get("").toAbsolutePath().resolve("out/artifacts/web_war_exploded").toString());
        /*Wrapper defaultServlet = ctx.createWrapper();
        defaultServlet.setName("defaultServlet");
        defaultServlet.setServletClass("org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);
        ctx.addChild(defaultServlet);
        ctx.addServletMapping("/", "defaultServlet");
        ctx.addWelcomeFile("index.jsp");*/
        tomcat.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    tomcat.stop();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startClient() {
        String url = "http://localhost:" + port + "/";
        MainFrame mainFrame = new MainFrame("VID", url);
        mainFrame.setVisible(true);
    }
}
