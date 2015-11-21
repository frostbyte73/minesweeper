import java.awt.*;

/**
 * Created by David on 11/20/2015.
 */
public class Sweeper {
    public static Minefield field = new Builder();
    public static void main(String[] args) {
        field.click(new Point(8, 15));
    }
}
