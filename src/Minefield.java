import java.awt.*;
import java.io.IOException;

/**
 * Created by David on 11/20/2015.
 * This interface will make it an easy switch from using Builder to Reader
 */
public interface Minefield {
    int MINE = -1;                          // Value of a bomb, returned by read or click
    int UNKNOWN = -2;                       // Value of an unknown square, returned by read
    int read(Point pt);                     // Read the value of a square
    int click(Point pt) throws IOException, AWTException, InterruptedException; // Click a square, and return the value
    void flag(Point pt);                    // Drop a flag on a square
}
