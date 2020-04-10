package dev.esophose.guiframework.gui.screen;

@FunctionalInterface
public interface PageContentsRequester {

    /**
     * Requests contents to be displayed in a page
     *
     * @param pageNumber Number of the page being requested
     * @param startIndex Start index of the requested contents
     * @param endIndex End index of the requested contents
     * @return A GuiPageContentsResult with the requested contents
     */
    GuiPageContentsResult request(int pageNumber, int startIndex, int endIndex);

}
