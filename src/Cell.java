import java.awt.*;
import java.util.ArrayDeque;

/**
 * Created by David on 11/23/2015.
 */
public class Cell extends Point {
    // Number of adjacent mines
    public int val;
    // Number of adjacent mines flagged
    public int mines;
    // List of unknown neighbor cells
    public ArrayDeque<Cell> unknowns;
    // List of active neighbors
    public ArrayDeque<Cell> neighbors;

    public Cell(int i, int j, int v) {
        x = i;
        y = j;
        val = v;
        unknowns = new ArrayDeque<>();
        neighbors = new ArrayDeque<>();
    }
}
