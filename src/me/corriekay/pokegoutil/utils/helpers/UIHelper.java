package me.corriekay.pokegoutil.utils.helpers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.UIManager;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;

public final class UIHelper {
    /** Prevent initializing this class. */
    private UIHelper() {
    }

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
    
    /**
     * Get the layout configuration from config file and applies to the window.
     * Also add listeners to get future changes in this configs.
     * @param frame that config layout will be applied
     */
    public static void handleLayoutFromConfig(final JFrame frame) {
        final ConfigNew config = ConfigNew.getConfig();
        frame.setBounds(0, 0, config.getInt(ConfigKey.WINDOW_WIDTH), config.getInt(ConfigKey.WINDOW_HEIGHT));
        if (config.getBool(ConfigKey.WINDOW_MAXIMIZE)) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        // add EventHandler to save new window size and position to
        // config for the app to remember over restarts
        frame.addWindowStateListener(new WindowStateListener() {

            @Override
            public void windowStateChanged(final WindowEvent e) {
                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    config.setBool(ConfigKey.WINDOW_MAXIMIZE, true);
                } else {
                    config.setBool(ConfigKey.WINDOW_MAXIMIZE, false);
                }
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                final JFrame w = (JFrame) e.getComponent();
                config.setInt(ConfigKey.WINDOW_WIDTH, w.getWidth());
                config.setInt(ConfigKey.WINDOW_HEIGHT, w.getHeight());
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
                final JFrame w = (JFrame) e.getComponent();
                config.setInt(ConfigKey.WINDOW_POS_X, w.getX());
                config.setInt(ConfigKey.WINDOW_POS_Y, w.getY());
            }
        });

        final Point pt = UIHelper.getLocationMidScreen(frame);
        final int posx = config.getInt(ConfigKey.WINDOW_POS_X, pt.x);
        final int posy = config.getInt(ConfigKey.WINDOW_POS_Y, pt.y);
        frame.setLocation(posx, posy);
    }
}
