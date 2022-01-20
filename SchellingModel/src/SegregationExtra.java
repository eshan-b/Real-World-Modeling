import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
/* std lib for Picture class is a requirement */

public class SegregationExtra {
    // initialize picture
    Picture p;
    private int width; // only need width for square

    // 0 - vacant, 1 - red, 2 - green
    private int[][] arr;
    int rows, cols;
    int totalPopulation;

    // parameters
    private double threshold;
    private final double red, empty;

    // amount satisfied
    private double satisfied;

    // used for mean similar neighbors
    private int similarNeighbors, totalNeighbors;
    private double similarity;

    public SegregationExtra(double threshold, double empty, double red, int width) {
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

    public double getSimilarity() { return similarity; }

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

        // update neighboring squares calculation
        this.similarNeighbors += similar;
        this.totalNeighbors += notEmpty;

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

    public double getSatisfied() { return satisfied; }

    public void setThreshold(double threshold) {
        this.threshold = threshold;

        // reset after each run
        this.totalNeighbors = 0;
        this.similarNeighbors = 0;
    }

    public void run() {
        // initialize the world
        initialize();

        // run simulation limited times
        for (int i = 0; i < 100; i++) {
            simulation();
            this.similarity = ((double) this.similarNeighbors / this.totalNeighbors) * 100;
        }

    }

    public static void main(String[] args) {
        // add all the working points to list
        ArrayList<Double> similarList = new ArrayList<>();

        // initialize object
        SegregationExtra s = new SegregationExtra(0.00, 0.2, 0.5, 100);

        // run simulation from 0 - 100% threshold
        int runs = 0;
        thresholdSimulation(runs, similarList, s);

        ArrayList<Integer> breakingPoints = new ArrayList<>();
        ArrayList<Double> percentChanges = new ArrayList<>();

        // find the percent changes between the similarities
        findChanges(similarList, breakingPoints, percentChanges);

        System.out.println("This phase transitions are:");
        breakingPoints.forEach(System.out::println);
    }

    private static void thresholdSimulation(int runs, ArrayList<Double> similarList, SegregationExtra s) {
        for (double i = 0; i < 1; i += 0.01) {
            // run simulation
            s.run();

            runs++;

            // amount similar
            double similar = s.getSimilarity();

            // display progress
            System.out.printf("Amount satisfied: %.2f%% | Runs: %d | Similarity: %.2f%%\r",
                            (s.getSatisfied() * 100), runs, similar);

            // add all points to list
            similarList.add(similar);

            // change threshold
            s.setThreshold(i);
        }
    }

    private static void findChanges(ArrayList<Double> similarList, ArrayList<Integer> breakingPoints, ArrayList<Double> percentChanges) {
        for (int i = 1; i < similarList.size() - 1; i++) {
            // get the percent change for between each value
            double percentChange = Math.abs(similarList.get(i) - similarList.get(i - 1)) /
                    similarList.get(i - 1);

            // if the percent change is abnormal, it is considered a breaking point
            if (percentChange > 0.05) {
                breakingPoints.add(i - 1);
                percentChanges.add(percentChange);
            }
        }
    }
}