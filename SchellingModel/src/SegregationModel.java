import java.awt.*;
import java.util.Arrays;
/* std lib for Picture class is a requirement */

public class SegregationModel {
    // initialize picture
    Picture p;
    private int width; // only need width for square

    // 0 - vacant, 1 - red, 2 - green
    private int[][] arr;
    int rows, cols, totalPopulation;

    // parameters
    private final double threshold, red, empty;

    // amount satisfied
    private double satisfied;

    public SegregationModel(double threshold, double empty, double red, int width) {
        this.threshold = threshold;
        this.empty = empty;
        this.red = red;
        this.width = width;

        this.arr = new int[width][width];
        rows = this.arr.length;
        cols = this.arr[0].length;
        totalPopulation = rows * cols;

        this.p = new Picture(this.width, this.width);
    }

    // set everything to vacant, red, and blue
    public void initialize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Math.random() < this.empty)
                    this.arr[i][j] = 0; // set vacant
                else if (Math.random() < red)
                    this.arr[i][j] = 1; // set red
                else
                    this.arr[i][j] = 2; // set blue
            }
        }
    }

    // simulate model per square
    public void simulation() {
        // temporary value for checking satisfaction
        int unsatisfied = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // temporary value for checking
                int value = this.arr[row][col];

                // move if not satisfied
                if (!isSatisfied(row, col)) {
                    move(row, col, value);
                    unsatisfied++;
                }
            }
        }

        // calculate amount satisfied
        satisfied = Math.min(1, (totalPopulation - unsatisfied) /
                    (totalPopulation - (this.empty * totalPopulation)));

        // update display
        draw();
    }

    public double getSatisfied() { return satisfied; }

    private void move(int row, int col, int value) {
        // make current square empty
        this.arr[row][col] = 0;

        // find random empty square
        do {
            row = (int) (Math.random() * rows);
            col = (int) (Math.random() * cols);
        } while (this.arr[row][col] != 0);

        // set empty square to current value
        this.arr[row][col] = value;
    }

    public boolean isSatisfied(int row, int col) {
        // temporary value for checking
        int value = this.arr[row][col];

        // array of neighboring points
        int[] pArray = {this.arr[(row + 1) % rows][(col) % cols],
                        this.arr[(row) % rows][(col + 1) % cols],
                        this.arr[(row - 1 + rows) % rows][(col) % cols],
                        this.arr[(row) % rows][(col - 1 + cols) % cols]};

        // count the similar non-empty squares
        int similar = (int) Arrays.stream(pArray).filter(p -> value == p && p != 0).count();

        // count amount of non-empty squares
        int notEmpty = (int) Arrays.stream(pArray).filter(p -> p != 0).count();

        // return if satisfied
        return (notEmpty != 0) && ((double) similar / notEmpty >= this.threshold);
    }

    // draw the values from 2D array
    public void draw() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // temporary value for checking
                int value = this.arr[i][j];

                // set empty, red, and blue values
                if (value == 0)
                    this.p.set(j, i, Color.WHITE);
                else if (value == 1)
                    this.p.set(j, i, Color.RED);
                else
                    this.p.set(j, i, Color.BLUE);
            }
        }

        // update screen
        this.p.show();
    }

    // perform and display full simulation
    public void display() throws InterruptedException {
        // initialize the world
        initialize();

        int runs = 0;

        // run until 100% satisfaction
        while (getSatisfied() < 1) {
            // run simulation for each square
            simulation();

            // print amount satisfied
            System.out.printf("Amount satisfied: %.2f%% | Runs: %d\r", (getSatisfied() * 100), runs);

            runs++;

            // delay
            Thread.sleep(100);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SegregationModel s = new SegregationModel(0.68, 0.2, 0.5, 500);
        s.display();
    }
}
