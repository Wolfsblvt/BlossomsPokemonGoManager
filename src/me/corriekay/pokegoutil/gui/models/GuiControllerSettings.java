package me.corriekay.pokegoutil.gui.models;

import javafx.stage.StageStyle;

/**
 * The settings used for initializing the controller.
 */
public class GuiControllerSettings {
    private String title;
    private boolean resizeable;
    private boolean maximized;
    private StageStyle stageStyle;

    /**
     * Instantiate a GuiControllerSettings with the default value.
     */
    public GuiControllerSettings() {
        title = "";
        resizeable = true;
        maximized = false;
        stageStyle = StageStyle.DECORATED;
    }

    /**
     * Get the stage style of the controller
     *
     * @return stage style of the controller
     */
    public StageStyle getStageStyle() {
        return stageStyle;
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
     * Get if the controller is maximized
     *
     * @return is the controller maximized
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Get if the controller is resizeable.
     *
     * @return is the controller resizeable
     */
    public boolean isResizeable() {
        return resizeable;
    }

    /**
     * Set if the controller is maximized
     *
     * @param maximized is the controller maximized
     */
    public void setMaximized(final boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * Set if the controller is resizeable.
     *
     * @param resizeable is the controller resizeable
     */
    public void setResizeable(final boolean resizeable) {
        this.resizeable = resizeable;
    }

    /**
     * Set the stage style of the controller
     * 
     * @param stageStyle stage style of the controller
     */
    public void setStageStyle(final StageStyle stageStyle) {
        this.stageStyle = stageStyle;
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
