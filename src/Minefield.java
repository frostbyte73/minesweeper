import java.awt.*;

/**
 * Created by David on 11/20/2015.
 * This interface will make it an easy switch from using Builder to Reader
 */
public interface Minefield {
    int read(Point pt);  // Read the value of a square
    int click(Point pt); // Click a square, and return the value
    void initialize();   // Do whatever setup needs to be done
}
