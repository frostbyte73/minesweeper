import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.sun.jna.*;
import com.sun.jna.win32.*;
import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * Created by David on 11/20/2015.
 * This class interacts with Windows minesweeper.
 */
public class Reader implements Minefield {

    private boolean initialized = false;
    private int LEFT_BUFFER = 15;
    private int TOP_BUFFER = 100;
    private int CELL_SIZE = 16;
    private int fieldX;
    private int fieldY;
    private int width;
    private int height;
    private Robot robot;

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
                W32APIOptions.DEFAULT_OPTIONS);

        HWND FindWindow(String lpClassName, String lpWindowName);

        int GetWindowRect(HWND handle, int[] rect);
    }

    @Override
    public int read(Point pt) {
        int x = (pt.y*CELL_SIZE)+8;
        int y = (pt.x*CELL_SIZE)+4;
        return read(x, y);
    }

    private int read(int x, int y) {
        BufferedImage img = robot.createScreenCapture(new Rectangle(fieldX,fieldY,width,height));
        Color color = new Color(img.getRGB(x, y));
        System.out.println(color.getRed()+" "+color.getGreen()+" "+color.getBlue());
//        if(color.getBlue() > 200) return 1;
//        if(color.getRed() > 200) return 3;

        return MINE;
    }

    @Override
    public int click(Point pt) throws IOException, AWTException, InterruptedException {
        if(!initialized) {
            initialize();
            initialized = true;
            System.out.println("INITIALIZED");
        }
        int x = fieldX+(pt.y*CELL_SIZE)+8;
        int y = fieldY+(pt.x*CELL_SIZE)+4;
        robot.mouseMove(x,y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        return MINE;//read(x, y);
    }

    @Override
    public void flag(Point pt) {
        int x = fieldX+(pt.y*CELL_SIZE)+8;
        int y = fieldY+(pt.x*CELL_SIZE)+4;
        robot.mouseMove(x,y);
        robot.mousePress(InputEvent.BUTTON2_MASK);
        robot.mouseRelease(InputEvent.BUTTON2_MASK);
    }

    // Don't need to print anything - ignore this
    @Override
    public void print() {

    }

    private void initialize() throws IOException, AWTException, InterruptedException {
        File f = new File("Minesweeper.exe");
        Runtime.getRuntime().exec(f.getAbsolutePath());
        Thread.sleep(1000);
        robot = new Robot();
        HWND hwnd = User32.INSTANCE.FindWindow(null, "Minesweeper");
        if(hwnd == null) return;
        int[] rect = {0, 0, 0, 0};
        if(User32.INSTANCE.GetWindowRect(hwnd, rect) == 0) return;
        fieldX = rect[0]+LEFT_BUFFER;
        fieldY = rect[1]+TOP_BUFFER;
        width = rect[2]-rect[0]-LEFT_BUFFER;
        height = rect[3]-rect[1]-TOP_BUFFER;
//        System.out.println("fieldX: "+fieldX);
//        System.out.println("fieldY: "+fieldY);
//        System.out.println("width: "+width);
//        System.out.println("height: "+height);
    }
}
