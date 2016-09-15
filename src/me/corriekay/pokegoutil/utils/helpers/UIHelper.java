package me.corriekay.pokegoutil.utils.helpers;

import javax.swing.*;
import java.awt.*;

public final class UIHelper {
    private UIHelper() { /* Prevent initializing this class */ }

    public static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLocationMidScreen(Window frame, int screen) {
        frame.setLocation(getLocationMidScreen(frame, screen));
    }

    public static void setLocationMidScreen(Window frame) {
        setLocationMidScreen(frame, 0);
    }

    public static Point getLocationMidScreen(Window frame, int screen) {
        DisplayMode monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen]
                .getDisplayMode();
        Point p = new Point(monitor.getWidth() / 2 - (frame.getWidth() / 2),
                monitor.getHeight() / 2 - (frame.getHeight() / 2));

        return p;
    }

    public static Point getLocationMidScreen(Window frame) {
        return getLocationMidScreen(frame, 0);
    }

    public static void setBackgroundColorRecursively(Container container, Color color) {
        for (int i = 0; i < container.getComponentCount(); i++) {
            Component component = container.getComponent(i);
            if (component instanceof Container) {
                setBackgroundColorRecursively((Container) component, color);
            }
            component.setBackground(color);
        }
    }
}
