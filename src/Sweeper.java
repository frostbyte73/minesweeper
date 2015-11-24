import java.util.*;

/**
 * Created by David on 11/20/2015.
 */
public class Sweeper {
    private static Minefield field = new Builder();

    private static Stack<Cell> simple = new Stack<>();
    private static Stack<Cell> recursive = new Stack<>();
    private static Stack<Cell> impossible = new Stack<>();
    private static Stack<Cell> zeroes = new Stack<>();
    private static Stack<Cell> marked = new Stack<>();

    private static int BOMB = -1;
    private static int UNKNOWN = -2;

    private static boolean lost = false;

    public static void main(String[] args) {
        Cell start = new Cell(8, 15);
        field.click(start);
        loadSimple(start, true);
        field.print();
        while(!lost) {
            if (!simple.isEmpty()) {
                // Check for obvious solutions
                Cell c = simple.pop();
                Stack<Cell> unknowns = new Stack<>();
                int bombs = checkAdjacentSquares(unknowns, c);
                if(c.val == bombs) {
                    // All bombs have been marked
                    while (!unknowns.isEmpty()) {
                        Cell u = unknowns.pop();
                        u.val = field.click(u);
                        if (u.val == BOMB) {
                            System.out.println("HIT A BOMB!!");
                            lost = true;
                            break;
                        }
                        loadSimple(u, true);
                    }
                } else if (c.val - bombs == unknowns.size()) {
                    // All unknowns must be bombs
                    while (!unknowns.isEmpty()) {
                        Cell b = unknowns.pop();
                        field.flag(b);
                        loadSimple(b, true);
                    }
                } else {
                    // Need to give it a more in-depth look
                    recursive.push(c);
                }
            } else if (!recursive.isEmpty()) {
                // TODO:Do a recursive search for solutions
                Cell c = recursive.pop();
                impossible.push(c);
            } else {
                // Logic has failed us - take a random guess
                Cell c = impossible.pop();
                Stack<Cell> unknowns = new Stack<>();
                checkAdjacentSquares(unknowns, c);
                Cell u = unknowns.pop();
                u.val = field.click(u);
                if (u.val == BOMB) {
                    System.out.println("HIT A BOMB!!");
                    lost = true;
                    break;
                }
                loadSimple(u, true);
            }
        }
        field.print();
        System.out.println("Simple stack contains "+simple.size());
        System.out.println("Recursive stack contains "+recursive.size());
        System.out.println("Impossible stack contains "+impossible.size());
    }

    private static void loadSimple(Cell c, boolean adj) {
        int i = c.x;
        int j = c.y;
        // Don't add zeroes
        if(zeroes.contains(c) || marked.contains(c)) return;
        c.val = field.read(c);
        if(c.val == UNKNOWN) {
            return;
        } else if(c.val == BOMB) {
            marked.push(c);
        } else if(c.val == 0) {
            zeroes.push(c);
            adj = true;
        } else {
            // Move this to the top of the simple stack
            impossible.remove(c);
            recursive.remove(c);
            simple.remove(c);
            simple.push(c);
        }
        // Read adjacent squares
        if(!adj) return;
        if(i > 0) {
            if(j > 0) loadSimple(new Cell(i-1,j-1), false);
            loadSimple(new Cell(i-1,j), false);
            if(j < 29) loadSimple(new Cell(i-1,j+1), false);
        }
        if(j > 0) loadSimple(new Cell(i,j-1), false);
        if(j < 29) loadSimple(new Cell(i,j+1), false);
        if(i < 15) {
            if (j > 0) loadSimple(new Cell(i+1,j-1), false);
            loadSimple(new Cell(i+1,j), false);
            if (j < 29) loadSimple(new Cell(i+1,j+1), false);
        }
    }

    private static int checkAdjacentSquares(Stack<Cell> unknowns, Cell c) {
        int i = c.x;
        int j = c.y;
        int bombs = 0;
        if(i > 0) {
            if(j > 0) bombs += checkSquare(unknowns, new Cell(i-1,j-1));
            bombs += checkSquare(unknowns, new Cell(i-1,j));
            if(j < 29) bombs += checkSquare(unknowns, new Cell(i-1,j+1));
        }
        if(j > 0) bombs += checkSquare(unknowns, new Cell(i,j-1));
        if(j < 29) bombs += checkSquare(unknowns, new Cell(i,j+1));
        if(i < 15) {
            if (j > 0) bombs += checkSquare(unknowns, new Cell(i+1,j-1));
            bombs += checkSquare(unknowns, new Cell(i+1,j));
            if (j < 29) bombs += checkSquare(unknowns, new Cell(i+1,j+1));
        }
        return bombs;
    }

    private static int checkSquare(Stack<Cell> unknowns, Cell c) {
        c.val = field.read(c);
        if(c.val == BOMB) return 1;
        if(c.val == UNKNOWN) unknowns.push(c);
        return 0;
    }
}
