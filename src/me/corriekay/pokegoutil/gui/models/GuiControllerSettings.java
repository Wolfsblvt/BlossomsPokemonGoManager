package me.corriekay.pokegoutil.gui.models;

/**
 * The settings used for initializing the controller.
 */
public class GuiControllerSettings {
    private String title;
    private boolean resizeable;

    /**
     * Instantiate a GuiControllerSettings with the default value.
     */
    public GuiControllerSettings() {
        title = "";
        resizeable = true;
    }

    /**
     * Get the title of the controller.
     *
     * @return title of the controller
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get if the controller is resizeable.
     *
     * @return is controller resizeable
     */
    public boolean isResizeable() {
        return resizeable;
    }

    /**
     * Set if the controller is resizeable
     *
     * @param resizeable is controller resizeable
     */
    public void setResizeable(final boolean resizeable) {
        this.resizeable = resizeable;
    }

    /**
     * Set the title of the controller.
     *
     * @param title title of the controller
     */
    public void setTitle(final String title) {
        this.title = title;
    }
}
