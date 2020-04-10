package dev.esophose.guiframework.framework.gui.screen;

import dev.esophose.guiframework.gui.screen.GuiPageContentsResult;
import dev.esophose.guiframework.gui.screen.Slotable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class FrameworkPageContentsResult implements GuiPageContentsResult {

    private List<Slotable> pageContents;

    public FrameworkPageContentsResult() {
        this(new ArrayList<>());
    }

    public FrameworkPageContentsResult(List<Slotable> pageContents) {
        this.pageContents = pageContents;
    }

    @Override
    public void setPageContents(List<Slotable> pageContents) {
        this.pageContents = pageContents;
    }

    @Override
    public void addPageContent(Slotable content) {
        this.pageContents.add(content);
    }

    @Override
    public void addPageContent(ItemStack itemStack) {
        this.addPageContent(new FrameworkItem(itemStack));
    }

    @Override
    public List<Slotable> getPageContents() {
        return Collections.unmodifiableList(this.pageContents);
    }

}
