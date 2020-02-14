package dev.esophose.guiframework.gui.screen;

import java.util.Collections;
import java.util.List;

public class GuiPageContentsResult {

    private List<ISlotable> pageContents;
    private boolean finished;

    public GuiPageContentsResult(List<ISlotable> pageContents, boolean finished) {
        this.pageContents = pageContents;
        this.finished = finished;
    }

    /**
     * Sets the exact content of the result
     *
     * @param pageContents The indices and contents of the page
     */
    public void setPageContents(List<ISlotable> pageContents) {
        this.pageContents = pageContents;
    }

    // TODO: JAVADOC

    public void addPageContent(ISlotable content) {
        this.pageContents.add(content);
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<ISlotable> getPageContents() {
        return Collections.unmodifiableList(this.pageContents);
    }

    public boolean isFinished() {
        return this.finished;
    }

}
