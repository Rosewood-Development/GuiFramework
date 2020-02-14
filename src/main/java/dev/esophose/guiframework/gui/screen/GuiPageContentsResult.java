package dev.esophose.guiframework.gui.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiPageContentsResult {

    private List<ISlotable> pageContents;

    public GuiPageContentsResult() {
        this(new ArrayList<>());
    }

    public GuiPageContentsResult(List<ISlotable> pageContents) {
        this.pageContents = pageContents;
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

    public List<ISlotable> getPageContents() {
        return Collections.unmodifiableList(this.pageContents);
    }

}
