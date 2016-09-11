package me.corriekay.pokegoutil.gui.models;

public class GuiControllerSettings {
    private String title;
    private boolean resizeable;

    public GuiControllerSettings() {
        title = "";
        resizeable = true;
    }

    public String getTitle() {
        return title;
    }

    public boolean isResizeable() {
        return resizeable;
    }

    public void setResizeable(final boolean resizeable) {
        this.resizeable = resizeable;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
