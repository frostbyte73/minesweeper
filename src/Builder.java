import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by David on 11/20/2015.
 * This class builds our own minesweeper field.
 * Will eventually be replaced by Reader
 */
public class Builder implements Minefield {

    private int[][] field;
    private boolean[][] known;
    private boolean initialized = false;

    @Override
    public int read(Point pt) {
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
        print();
        return field[x][y];
    }

    public void initialize(Point pt) {
        int x, y, val;
        boolean placed = false;
        field = new int[16][30]; // Expert sized
        known = new boolean[16][30];
        // Place 99 bombs
        for(int i=0; i<99; i++) {
            while(!placed) {
                x = ThreadLocalRandom.current().nextInt(1, 16);
                y = ThreadLocalRandom.current().nextInt(1, 30);
                // Starting click should always be 0
                if(Math.abs(pt.x-x) < 2 && Math.abs(pt.y-y) < 2) continue;
                if (field[x][y] != -1) { // No duplicates
                    field[x][y] = -1;
                    placed = true;
                }
            }
        }
        // Fill in the values
        for(int i=0; i<16; i++) {
            for(int j=0; j<30; j++) {
                if(field[i][j] == -1) break; // Don't override bombs
                val = 0;
                if(i > 0) {
                    if(j > 0 && field[i-1][j-1] == -1) val++;
                    if(field[i-1][j] == -1) val++;
                    if(j < 29 && field[i-1][j+1] == -1) val++;
                }
                if(j > 0 && field[i][j-1] == -1) val++;
                if(j < 29 && field[i][j+1] == -1) val++;
                if(i < 15) {
                    if(j > 0 && field[i+1][j-1] == -1) val++;
                    if(field[i+1][j] == -1) val++;
                    if(j < 29 && field[i+1][j+1] == -1) val++;
                }
                field[i][j] = val;
            }
        }
    }

    public void print() {
        for(int i=0; i<16; i++) {
            for(int j=0; j<30; j++) {
                if(!known[i][j]) {
                    System.out.print("?");
                } else {
                    System.out.print(field[i][j]);
                }
            }
            System.out.println();
        }
    }
}
