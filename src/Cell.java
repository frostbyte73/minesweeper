import java.awt.*;
import java.util.ArrayDeque;

/**
 * Created by David on 11/23/2015.
 */
public class Cell extends Point {
    public int val;
    public int bombs;
    public ArrayDeque<Cell> unknowns;

    public Cell(int i, int j, int v) {
        x = i;
        y = j;
        val = v;
        unknowns = new ArrayDeque<>();
    }
}
