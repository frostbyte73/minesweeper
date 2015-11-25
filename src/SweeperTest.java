import java.awt.*;

/**
 * Created by David on 11/24/2015.
 */
public class SweeperTest implements Minefield {
    private int[][] field;
    private boolean[][] known;
    private boolean initialized = false;

    @Override
    public int read(Point pt) {
        if(!known[pt.x][pt.y]) {
            return UNKNOWN;
        }
        return field[pt.x][pt.y];
    }

    @Override
    public int click(Point pt) {
        int x = pt.x;
        int y = pt.y;
        if(x < 0 || y < 0 || x > 15 || y > 29) return 0;
        // Initialize on first click
        if(!initialized) {
            initialize(pt);
            initialized = true;
        }
        // Do nothing if already clicked
        if(known[x][y]) return field[x][y];
        known[x][y] = true;
        // If 0, click all surrounding points
        if(field[x][y] == 0) {
            click(new Point(x-1, y-1));
            click(new Point(x-1, y));
            click(new Point(x-1, y+1));
            click(new Point(x, y-1));
            click(new Point(x, y+1));
            click(new Point(x+1, y-1));
            click(new Point(x+1, y));
            click(new Point(x+1, y+1));
        }
        return field[x][y];
    }

    private void initialize(Point pt) {

    }

    @Override
    public void flag(Point pt) {

    }

    @Override
    public void print() {

    }
}
