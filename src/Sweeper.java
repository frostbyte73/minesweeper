import java.util.*;

/**
 * Created by David on 11/20/2015.
 */
public class Sweeper {
    private static Minefield game = new Builder();

    private static Cell[][] field;

    private static ArrayDeque<Cell> simple = new ArrayDeque<>();
    private static ArrayDeque<Cell> recursive = new ArrayDeque<>();
    private static ArrayDeque<Cell> impossible = new ArrayDeque<>();

    private static int BOMB = -1;
    private static int UNKNOWN = -2;

    private static status Status = status.Running;

    private enum status {
        Running, Lost, Won
    }

    public static void main(String[] args) {
        initialize();
        game.print();
        while(Status == status.Running) {
            // Check for obvious solutions first to save time
            runSimple();
            // Run recursive search once finished
            if (!recursive.isEmpty()) {
                runRecursive();
            } else if(!impossible.isEmpty()){
                // Both queues are empty - a random click must be made
                // TODO: check probabilities
                System.out.println(impossible.size()+" Cells in impossible.");
                Status = status.Lost;
            } else {
                // All stacks empty; game won
                Status = status.Won;
            }
        }
        game.print();
        System.out.println("Game "+(Status==status.Won ? "Won!" : "Lost."));
     }

    /**
     * Initializes our minefield and starts the game
     */
    private static void initialize() {
        field = new Cell[16][30];
        for(int i=0; i<16; i++) {
            for(int j=0; j<30; j++) {
                field[i][j] = new Cell(i, j, UNKNOWN);
            }
        }
        Cell start = field[8][15];
        click(start);
    }

    /**
     * Tries to find a solution looking only at immediate neighbors
     */
    private static void runSimple() {
        while(!simple.isEmpty()) {
            // Check for obvious solutions
            Cell c = simple.removeFirst();
            if(c.val == c.bombs) {
                // All bombs have been marked
                while (!c.unknowns.isEmpty()) {
                    click(c.unknowns.removeFirst());
                    if(Status == status.Lost) return;
                }
            } else if (c.val - c.bombs == c.unknowns.size()) {
                // All unknowns must be bombs
                while (!c.unknowns.isEmpty()) {
                    flag(c.unknowns.removeFirst());
                }
            } else {
                // Need to run recursive search
                recursive.addFirst(c);
            }
        }
    }

    /**
     * Tries to find a solution recursively
     */
    private static void runRecursive() {
        impossible.addFirst(recursive.removeFirst()); //
//        ArrayDeque<Cell> queue = new ArrayDeque<>();
//        Cell c = recursive.removeFirst();
//        // TODO: run recursive search
//        impossible.addFirst(c);
    }

    /**
     * Clicks an UNKNOWN cell on the field
     * @param c - cell to click
     */
    private static void click(Cell c) {
        if (game.click(c) == BOMB) {
            System.out.println("Bomb hit on ("+c.x+", "+c.y+")");
            Status = status.Lost;
            return;
        }
//        game.print();
        // Update field with the new value
        initializeCell(c);

    }

    /**
     * Updates the cell's value and adds numbered cells to simple stack
     * @param c - clicked cell
     */
    private static void initializeCell(Cell c) {
        int i = c.x;
        int j = c.y;
        // Don't loop on 0's
        if(c.val == 0) return;
        // Read the value from the Minefield
        c.val = game.read(c);
        // Load data about its neighbors
        loadAdjacentData(c);
        if(c.val == 0) {
            // If 0, initialize surrounding cells
            if(i > 0) {
                if(j > 0) initializeCell(field[i-1][j-1]);
                initializeCell(field[i-1][j]);
                if(j < 29) initializeCell(field[i-1][j+1]);
            }
            if(j > 0) initializeCell(field[i][j-1]);
            if(j < 29) initializeCell(field[i][j+1]);
            if(i < 15) {
                if (j > 0) initializeCell(field[i+1][j-1]);
                initializeCell(field[i+1][j]);
                if (j < 29) initializeCell(field[i+1][j+1]);
            }
        } else {
            // Cell is a number - move to top of simple stack
            moveCellToTop(c);
        }
    }

    /**
     * Loads bomb and unknown data for newly clicked cell
     * called by initializeCell
     * @param c - clicked cell
     */
    private static void loadAdjacentData(Cell c) {
        // Only do this for numbers
        if(c.val <= 0) return;
        int i = c.x;
        int j = c.y;
        // Count bombs and fill unknowns
        c.bombs = 0;
        if(i > 0) {
            if(j > 0) updateAdjacentCells(c, field[i-1][j-1]);
            updateAdjacentCells(c, field[i-1][j]);
            if(j < 29) updateAdjacentCells(c, field[i-1][j+1]);
        }
        if(j > 0) updateAdjacentCells(c, field[i][j-1]);
        if(j < 29) updateAdjacentCells(c, field[i][j+1]);
        if(i < 15) {
            if (j > 0) updateAdjacentCells(c, field[i+1][j-1]);
            updateAdjacentCells(c, field[i+1][j]);
            if (j < 29) updateAdjacentCells(c, field[i+1][j+1]);
        }
    }

    /**
     * Adjusts unknown list and bomb count according to neighbor's status
     * @param c   - current cell
     * @param adj - adjacent cell
     */
    private static void updateAdjacentCells(Cell c, Cell adj) {
        if(adj.val == UNKNOWN) {
            if(!c.unknowns.contains(adj)) c.unknowns.addFirst(adj);
        } else {
            c.unknowns.remove(adj);
        }
        if(adj.val == BOMB) c.bombs++;
        if(c.val != UNKNOWN && adj.unknowns.contains(c)) {
            adj.unknowns.remove(c);
            moveCellToTop(adj);
        }
    }

    /**
     * Flags a mine on the field
     * @param c - bomb cell
     */
    private static void flag(Cell c) {
        game.flag(c);
        c.val = BOMB;
        int i = c.x;
        int j = c.y;
        if(i > 0) {
            if(j > 0) raiseBombCount(c, field[i-1][j-1]);
            raiseBombCount(c, field[i-1][j]);
            if(j < 29) raiseBombCount(c, field[i-1][j+1]);
        }
        if(j > 0) raiseBombCount(c, field[i][j-1]);
        if(j < 29) raiseBombCount(c, field[i][j+1]);
        if(i < 15) {
            if (j > 0) raiseBombCount(c, field[i+1][j-1]);
            raiseBombCount(c, field[i+1][j]);
            if (j < 29) raiseBombCount(c, field[i+1][j+1]);
        }
    }

    /**
     * Raises a neighbor's bomb count, and removes the bomb from unknowns
     * @param c   - bomb cell
     * @param adj - neighbor cell
     */
    private static void raiseBombCount(Cell c, Cell adj) {
        if(adj.val == UNKNOWN) return;
        adj.unknowns.remove(c);
        adj.bombs++;
        moveCellToTop(adj);
    }

    /**
     * Moves target cell to the top of the simple stack
     * @param c - cell to move
     */
    private static void moveCellToTop(Cell c) {
        impossible.remove(c);
        recursive.remove(c);
        simple.remove(c);
        simple.addFirst(c);
    }
}
