import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by David on 11/20/2015.
 * This class builds our own minesweeper field.
 * Will eventually be replaced by Reader
 */
public class Builder implements Minefield {

    private int[][] field;

    @Override
    public int read(Point pt) {
        return field[pt.x][pt.y];
    }

    @Override
    public int click(Point pt) {
        return field[pt.x][pt.y];
    }

    public void initialize() {
        int x, y, val;
        boolean placed = false;
        field = new int[16][30]; // Expert sized
        // Place 99 bombs
        for(int i=0; i<99; i++) {
            while(!placed) {
                x = ThreadLocalRandom.current().nextInt(1, 16);
                y = ThreadLocalRandom.current().nextInt(1, 30);
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
}
