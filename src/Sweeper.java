import java.util.*;

/**
 * Created by David on 11/20/2015.
 */
public class Sweeper {
    private static Minefield game = new Builder();

    private static Cell[][] field;

    /**
     * This queue holds the cells whose information (neighboring mines and remaining unknowns)
     * has been updated. We will do a quick check for single-cell solutions
     */
    private static ArrayDeque<Cell> simple = new ArrayDeque<>();
    /**
     * This queue holds the cells with no obvious unmarked mines. We need to do a advanced search,
     * checking which potential mines work with the neighboring cells as well
     */
    private static ArrayDeque<Cell> advanced = new ArrayDeque<>();
    /**
     * This queue is for cells on which a guess must be made. An example would be two ones next to
     * each other, sharing two potential mines
     */
    private static ArrayDeque<Cell> impossible = new ArrayDeque<>();

    private static int MINE = -1;
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
            while(!simple.isEmpty()) {
                runSimple(simple.removeFirst());
            }
            // Run advanced search once finished
            if(!advanced.isEmpty()) {
                runAdvanced(advanced.removeFirst());
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
     * Looks for obvious solutions
     */
    protected static void runSimple(Cell c) {
        // Check for obvious solutions
        if(c.val == c.mines) {
            // All mines have been marked
            while(!c.unknowns.isEmpty()) {
                click(c.unknowns.removeFirst());
                if(Status == status.Lost) return;
            }
            while(!c.neighbors.isEmpty()) {
                // Break the neighbor link
                c.neighbors.removeFirst().neighbors.remove(c);
            }
        } else if(c.val - c.mines == c.unknowns.size()) {
            // All unknowns must be mines
            while(!c.unknowns.isEmpty()) {
                flag(c.unknowns.removeFirst());
            }
        } else {
            // Need to run advanced search
            advanced.addFirst(c);
        }
    }

    /**
     * Tries a more comprehensive search for solutions
     */
    protected static void runAdvanced(Cell c) {
        boolean found = false;
        // Loop over each neighbor
        for(Iterator<Cell> i = c.neighbors.iterator(); i.hasNext();) {
            Cell n = i.next();
            // Split c's neighbors into shared or unshared with n
            ArrayDeque<Cell> shared = new ArrayDeque<>();
            ArrayDeque<Cell> unshared = new ArrayDeque<>();
            for(Iterator<Cell> j = c.neighbors.iterator(); j.hasNext();) {
                Cell s = j.next();
                if(n.neighbors.contains(s)) {
                    shared.addFirst(s);
                } else {
                    unshared.addFirst(s);
                }
            }
            // If the neighbor shares all
            if(unshared.isEmpty()) continue;
            if(shared.size() == n.neighbors.size() && n.val-n.mines == c.val-c.mines) {
                // All remaining bombs lie in the shared spaces - click all unshared
                while(!unshared.isEmpty()) {
                    click(unshared.removeFirst());
                }
                found = true;
            }
            if(n.val-n.mines < shared.size() && c.val-c.mines == unshared.size()+n.val-n.mines) {
                // Sharing as many as possible, there are just enough unshared cells for the remaining mines
                while(!unshared.isEmpty()) {
                    flag(unshared.removeFirst());
                }
                found = true;
            }
        }
        if(found) {
            simple.addFirst(c);
        } else {
            impossible.addFirst(c);
        }
    }

    /**
     * Clicks an UNKNOWN cell on the field
     * @param c - cell to click
     */
    private static void click(Cell c) {
        if(game.click(c) == MINE) {
            System.out.println("Mine hit on ("+c.x+", "+c.y+")");
            Status = status.Lost;
            return;
        }
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
     * Loads mine and unknown data for newly clicked cell
     * called by initializeCell
     * @param c - clicked cell
     */
    private static void loadAdjacentData(Cell c) {
        // Only do this for numbers
        if(c.val <= 0) return;
        int i = c.x;
        int j = c.y;
        // Count mines and fill unknowns
        c.mines = 0;
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
     * Adjusts unknown list and mine count according to neighbor's status
     * @param c   - clicked cell
     * @param adj - adjacent cell
     */
    private static void updateAdjacentCells(Cell c, Cell adj) {
        if(adj.val == UNKNOWN) {
            if(!c.unknowns.contains(adj)) c.unknowns.addFirst(adj);
        } else {
            c.unknowns.remove(adj);
        }
        if(adj.val == MINE) c.mines++;
        if(adj.unknowns.contains(c)) {
            adj.unknowns.remove(c);
            if(adj.val > adj.mines) {
                adj.neighbors.addFirst(c);
                c.neighbors.addFirst(adj);
            }
            moveCellToTop(adj);
        }
    }

    /**
     * Flags a mine on the field
     * @param c - mine cell
     */
    private static void flag(Cell c) {
        game.flag(c);
        c.val = MINE;
        int i = c.x;
        int j = c.y;
        if(i > 0) {
            if(j > 0) raiseMineCount(c, field[i-1][j-1]);
            raiseMineCount(c, field[i-1][j]);
            if(j < 29) raiseMineCount(c, field[i-1][j+1]);
        }
        if(j > 0) raiseMineCount(c, field[i][j-1]);
        if(j < 29) raiseMineCount(c, field[i][j+1]);
        if(i < 15) {
            if (j > 0) raiseMineCount(c, field[i+1][j-1]);
            raiseMineCount(c, field[i+1][j]);
            if (j < 29) raiseMineCount(c, field[i+1][j+1]);
        }
    }

    /**
     * Raises a neighbor's mine count, and removes the mine from unknowns
     * @param c   - mine cell
     * @param adj - neighbor cell
     */
    private static void raiseMineCount(Cell c, Cell adj) {
        if(adj.val == UNKNOWN) return;
        adj.unknowns.remove(c);
        adj.mines++;
        moveCellToTop(adj);
    }

    /**
     * Moves target cell to the top of the simple stack
     * @param c - cell to move
     */
    private static void moveCellToTop(Cell c) {
        impossible.remove(c);
        advanced.remove(c);
        simple.remove(c);
        simple.addFirst(c);
    }
}
