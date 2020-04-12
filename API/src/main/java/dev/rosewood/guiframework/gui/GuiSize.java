package dev.rosewood.guiframework.gui;

public enum GuiSize {

    DYNAMIC(-1),
    ROWS_ONE(9),
    ROWS_TWO(18),
    ROWS_THREE(27),
    ROWS_FOUR(36),
    ROWS_FIVE(45),
    ROWS_SIX(54),
    HOPPER(5),
    DISPENSER(9);

    private int numSlots;

    GuiSize(int numSlots) {
        this.numSlots = numSlots;
    }

    /**
     * @return the number of slots for the gui size
     */
    public int getNumSlots() {
        return this.numSlots;
    }

}
