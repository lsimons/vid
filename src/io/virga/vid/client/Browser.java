package io.virga.vid.client;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {
    final WebView browser = new WebView();
    final WebEngine engine = browser.getEngine();
    private int width;
    private int height;

    public Browser(String url, int width, int height) {
        this.width = width;
        this.height = height;
        engine.load(url);
        getChildren().add(browser);
        browser.setContextMenuEnabled(false);
        engine.setUserStyleSheetLocation(getClass().getResource("user-stylesheet.css").toExternalForm());
    }

    @Override
    protected double computePrefWidth(double height) {
        return width;
    }

    @Override
    protected double computePrefHeight(double width) {
        return height;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }
}
