package dev.rosewood.guiframework.gui;

public enum GuiSize {

    DYNAMIC(-1, -1),
    ROWS_ONE(1, 9),
    ROWS_TWO(2, 9),
    ROWS_THREE(3, 9),
    ROWS_FOUR(4, 9),
    ROWS_FIVE(5, 9),
    ROWS_SIX(6, 9),
    HOPPER(1, 5),
    DISPENSER(3, 3);

    private final int rows, cols;

    GuiSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * @return the number of rows for this size
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * @return the number of cols for this size
     */
    public int getCols() {
        return this.cols;
    }

    /**
     * @return the number of slots for this size
     */
    public int getNumSlots() {
        if (this == DYNAMIC)
            return -1;

        return this.rows * this.cols;
    }

    /**
     * Gets a chest GuiSize from a given number of rows
     *
     * @param rows the number of rows
     * @return a GuiSize with the given number of rows
     * @throws IllegalStateException if the row count is not between 1 and 6 inclusive
     */
    public static GuiSize fromRows(int rows) {
        if (rows <= 0 || rows > 6)
            throw new IllegalArgumentException("rows must be between 1 and 6 inclusive");

        for (GuiSize guiSize : GuiSize.values()) {
            if (!guiSize.name().startsWith("ROWS"))
                continue;

            if (guiSize.rows == rows)
                return guiSize;
        }

        // Impossible to reach
        throw new IllegalStateException();
    }

}
