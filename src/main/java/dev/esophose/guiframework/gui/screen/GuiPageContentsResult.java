package dev.esophose.guiframework.gui.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class GuiPageContentsResult {

    private List<Slotable> pageContents;

    public GuiPageContentsResult() {
        this(new ArrayList<>());
    }

    public GuiPageContentsResult(List<Slotable> pageContents) {
        this.pageContents = pageContents;
    }

    /**
     * Sets the exact content of the result
     *
     * @param pageContents The indices and contents of the page
     */
    public void setPageContents(List<Slotable> pageContents) {
        this.pageContents = pageContents;
    }

    // TODO: JAVADOC

    public void addPageContent(Slotable content) {
        this.pageContents.add(content);
    }

    public void addPageContent(ItemStack itemStack) {
        this.addPageContent(new GuiItemStack(itemStack));
    }

    public List<Slotable> getPageContents() {
        return Collections.unmodifiableList(this.pageContents);
    }

}
