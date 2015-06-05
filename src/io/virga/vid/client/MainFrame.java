package io.virga.vid.client;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings({"AppEngineForbiddenCode", "FieldCanBeLocal"})
public class MainFrame extends JFrame {
    private Dimension maximumSize = new Dimension(600, 2048);
    private Dimension minimumSize = new Dimension(200, 400);
    private Dimension preferredSize = new Dimension(400, 800);
    private JFXPanel fxPanel = new JFXPanel();
    private Browser browser;
    private Scene scene;
    private String url;

    public MainFrame(String title, String url) {
        super(title);
        this.url = url;
        setSizeAndPosition();
        //setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initFX();
        add(fxPanel);
    }
    
    private void initFX() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        initFXComponents();
                    }
                });
            }
        });
    }
    
    private void initFXComponents() {
        browser = new Browser(url, getWidth(), getHeight());
        scene = new Scene(browser, getWidth(), getHeight(), javafx.scene.paint.Color.web("#FFFFFF"));
        fxPanel.setScene(scene);
    }

    private void setSizeAndPosition() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        setPreferredSize(preferredSize);
        setMinimumSize(minimumSize);
        setMaximumSize(maximumSize);

        int startWidth = Math.max((int) minimumSize.getWidth(), Math.min((int) preferredSize.getWidth(), screenWidth));
        int startHeight = Math.max((int) minimumSize.getHeight(), Math.min((int) preferredSize.getHeight(),
                screenHeight));
        setSize(startWidth, startHeight);

        int startLeft = Math.max(0, screenWidth - startWidth);
        int startTop = 0;
        /*switch (initialWindowPosition) {
            case TOP_LEFT:
                startLeft = 0;
                startTop = 0;
                break;
            case TOP_CENTER:
                startLeft = Math.max(0, (screenWidth - startWidth) / 2);
                startTop = 0;
                break;
            case TOP_RIGHT:
                startLeft = Math.max(0, screenWidth - startWidth);
                startTop = 0;
                break;
            case MIDDLE_LEFT:
                startLeft = 0;
                startTop = Math.max(0, (screenHeight - startHeight) / 2);
                break;
            case MIDDLE:
                startLeft = Math.max(0, (screenWidth - startWidth) / 2);
                startTop = Math.max(0, (screenHeight - startHeight) / 2);
                break;
            case MIDDLE_RIGHT:
                startLeft = Math.max(0, screenWidth - startWidth);
                startTop = Math.max(0, (screenHeight - startHeight) / 2);
                break;
            case BOTTOM_LEFT:
                startLeft = 0;
                startTop = Math.max(0, screenHeight - startHeight);
                break;
            case BOTTOM_CENTER:
                startLeft = Math.max(0, (screenWidth - startWidth) / 2);
                startTop = Math.max(0, screenHeight - startHeight);
                break;
            case BOTTOM_RIGHT:
                startLeft = Math.max(0, screenWidth - startWidth);
                startTop = Math.max(0, screenHeight - startHeight);
                break;
            case UNSET:
            default:
                break;
        }*/
        setLocation(startLeft, startTop);
    }
}
