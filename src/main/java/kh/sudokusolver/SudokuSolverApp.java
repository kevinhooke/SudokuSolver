package kh.sudokusolver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Sudoku solver.
 * 
 * Uses the following approach to solve a grid: 
 * 1. Inserts sets of possible guesses into each blank cell.
 * 2. Iterates through each cell removing single (guessed) values from each square, 
 *  row and column in each set of possible guesses in each cell.
 * 3. Repeat step 2 until no remaining changes are made.
 * 
 * Limitations:
 * - this approach cannot solve a blank grid, as it assumes the grid contains at least 
 * a starting point with some cells populated
 * - the starting position probably has a minimum number of required populated values in order to 
 * reach a solution. Below this minimum, this approach will fail to find a solution.
 * - this is my first attempt at writing a solving algorithm from scratch with no prior
 * knowledge of typical approaches used to solve a constraint based problem such as Sudoku, 
 * using brute force or backtracking algorithms etc.
 * 
 * @author kevinhooke
 *
 */
public class SudokuSolverApp {

    private static Logger LOG = Logger.getLogger("SudokuSolverApp");

    private static final Set<Integer> allowedValues = new HashSet<>(
            Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }));

    // example grid to solve
    //1
    private int[][] startingSudokuGrid = { 
            { 0, 0, 0, 8, 1, 0, 6, 7, 0 }, 
            { 0, 0, 7, 4, 9, 0, 2, 0, 8 },
            { 0, 6, 0, 0, 5, 0, 1, 0, 4 }, 
            { 1, 0, 0, 0, 0, 3, 9, 0, 0 }, 
            { 4, 0, 0, 0, 8, 0, 0, 0, 7 },
            { 0, 0, 6, 9, 0, 0, 0, 0, 3 }, 
            { 9, 0, 2, 0, 3, 0, 0, 6, 0 }, 
            { 6, 0, 1, 0, 7, 4, 3, 0, 0 },
            { 0, 3, 4, 0, 6, 9, 0, 0, 0 }
    };

    //easy
//    private int[][] startingSudokuGrid = { 
//            { 5, 0, 8, 4, 0, 0, 7, 0, 0 }, 
//            { 0, 0, 0, 0, 0, 0, 8, 1, 9 },
//            { 1, 0, 3, 0, 0, 6, 4, 0, 0 }, 
//            { 8, 0, 0, 9, 1, 0, 0, 0, 3 }, 
//            { 0, 0, 9, 0, 6, 0, 2, 0, 0 },
//            { 6, 0, 0, 0, 8, 3, 0, 0, 4 }, 
//            { 0, 0, 5, 6, 0, 0, 1, 0, 7 }, 
//            { 9, 4, 6, 0, 0, 0, 0, 0, 0 },
//            { 0, 0, 1, 0, 0, 9, 6, 0, 2 }
//    };

    // list (rows) of list of list of integers
    // eg { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, ... },
    // { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, ... },
    // ... }
    private List<List<List<Integer>>> solutionGrid = new ArrayList<>();

    /**
     * Default constructor.
     */
    public SudokuSolverApp() {

        InputStream inputStream = SudokuSolverApp.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        SudokuSolverApp app = new SudokuSolverApp();
        app.printGridWithBorders();

        app.populateSolutionGridWithStartingPosition();
        app.printSolutionGrid();

        app.solve();
        System.out.println("Complete!");
    }

    private void printValuesSet(Set<Integer> squareValues) {
        for (Integer i : squareValues) {
            System.out.print(i.toString() + ", ");
        }
        System.out.println();
        System.out.println();
    }

    
    /**
     * Prints the puzzle grid with borders around each square.
     * 
     */
    private void printGridWithBorders() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = startingSudokuGrid[row][col];
                System.out.print((value == 0 ? " " : value) + " ");
                if (col == 2 || col == 5) {
                    System.out.print("| ");
                }
            }
            if (row == 2 || row == 5) {
                System.out.println("\n- - - + - - - + - - -");
            } else {
                System.out.println();
            }
        }
    }

    
    private void printSolutionGrid() {
        for (List<List<Integer>> row : this.solutionGrid) {
            for (List<Integer> currentCell : row) {
                // full cell pad to accomodate 0..9
                // int paddingSize = (9 - currentCell.size()) * 3;

                // temp - reduced pad
                int paddingSize = (5 - currentCell.size()) * 3;

                System.out.print("{ ");
                for (Integer i : currentCell) {
                    System.out.print(i.toString() + ", ");
                }
                for (int paddingCount = 0; paddingCount < paddingSize; paddingCount++) {
                    System.out.print(" ");
                }

                System.out.print(" },");
            }
            System.out.println();
        }

    }

    
    void populateSolutionGridWithStartingPosition() {
        for (int row = 0; row < 9; row++) {
            List<List<Integer>> currentRow = new ArrayList<>();

            for (int col = 0; col < 9; col++) {
                List<Integer> currentCellPossibleNumberList = new ArrayList<>();
                int value = startingSudokuGrid[row][col];
                // if we have starting number for cell, add it to solution grid,
                // otherwise add an empty list for now - we'll come back and
                // populate each empty list with possible numbers when we start
                // solving
                if (value > 0) {
                    currentCellPossibleNumberList.add(value);
                }
                currentRow.add(currentCellPossibleNumberList);
            }
            solutionGrid.add(currentRow);
        }
    }

    
    /**
     * Solves the grid. Loops through squares first, inserting possible values
     * into each empty cell. Then iterates row by row removing single values
     * from sets of guesses until unable to remove any values.
     */
    void solve() {

        int passesThroughGridCount = 0;

        // pass 1 - loop through squares and populate blank cells with lists of
        // possible values
        for (int rowSquare = 0; rowSquare < 3; rowSquare++) {
            for (int colSquare = 0; colSquare < 3; colSquare++) {
                System.out.print("Square " + rowSquare + ", " + colSquare + ": ");
                Set<Integer> singleValuesInSquare = this.getSingleValuesInSquare(rowSquare, colSquare);
                this.printValuesSet(singleValuesInSquare);

                Set<Integer> missingValues = this.getMissingPotentialValues(singleValuesInSquare);
                System.out.print("Missing values: ");
                this.printValuesSet(missingValues);
                // insert missing values into every blank cell in this square
                this.updateValuesInSquare(rowSquare, colSquare, new ArrayList<Integer>(missingValues));
            }
        }

        this.printSolutionGrid();

        // pass 2 - loop through individual cells and remove any invalid values
        boolean solvedValuesOnAtLeastOnePass = true;
        while (solvedValuesOnAtLeastOnePass) {
            boolean replacedOnLastIteration = false;
            for (int col = 0; col < 9; col++) {
                for (int row = 0; row < 9; row++) {
                    boolean solvedValuesThisPass = this.removeSingleValuesFromCurrentGuesses(row, col);
                    if (solvedValuesThisPass) {
                        replacedOnLastIteration = solvedValuesThisPass;
                    }
                }
                if (!replacedOnLastIteration) {
                    solvedValuesOnAtLeastOnePass = false;
                }
            }
            passesThroughGridCount++;
            this.printSolutionGrid();
            System.out.println("Passes through grid: " + passesThroughGridCount);
        }

    }

    
    private boolean removeSingleValuesFromCurrentGuesses(int row, int col) {
        boolean valuesReplaced = false;

        Set<Integer> singleValuesInRow = this.getSingleValuesInRow(row);
        Set<Integer> singleValuesInCol = this.getSingleValuesInColumn(col);
        Set<Integer> singleValuesInSquare = this.getSingleValuesInSquareByRowCol(row, col);
        List<Integer> valuesInCell = this.getValueInCell(row, col);
        // only replace if this cell currently has more than one guess
        if (valuesInCell.size() > 1) {
            boolean valuesReplacedInRow = valuesInCell.removeAll(singleValuesInRow);
            boolean valuesReplacedInCol = valuesInCell.removeAll(singleValuesInCol);
            boolean valuesReplacedInSquare = valuesInCell.removeAll(singleValuesInSquare);

            valuesReplaced = valuesReplacedInRow || valuesReplacedInCol || valuesReplacedInSquare;
            if (valuesReplaced) {
                List<List<Integer>> valuesInRow = this.getValuesInRow(row);
                valuesInRow.set(col, valuesInCell);
                this.solutionGrid.set(row, valuesInRow);
            }
        }
        return valuesReplaced;
    }


    /**
     * Retrieves set of single values in a column.
     * @param col column index to retrieve
     * @return Set of single values in the specified column.
     */
    Set<Integer> getSingleValuesInColumn(int col) {
        Set<Integer> singleValues = new HashSet<Integer>();
        for (int row = 0; row < 9; row++) {
            List<List<Integer>> valuesInRow = getValuesInRow(row);
            List<Integer> valuesInCell = valuesInRow.get(col);
            if (valuesInCell.size() == 1) {
                singleValues.addAll(valuesInCell);
            }
        }
        return singleValues;
    }

    
    /**
     * Retrieves set of single values in a row.
     * 
     * @param row
     * @return
     */
    Set<Integer> getSingleValuesInRow(int row) {
        Set<Integer> singleValues = new HashSet<Integer>();
        List<List<Integer>> valuesInRow = getValuesInRow(row);
        for (int col = 0; col < 9; col++) {
            List<Integer> valuesInCol = valuesInRow.get(col);
            if (valuesInCol.size() == 1) {
                singleValues.addAll(valuesInCol);
            }
        }
        return singleValues;
    }
    
    
    /**
     * Retrieves set of current values in a square, by iterating 3 rows from the
     * starting row, and 3 columns from the starting column.
     * 
     * Squares are referenced: first row: {0,0}, {0,1}, {0,2} second row: {1,0},
     * {1,1}, {1,2} etc
     * 
     * @param row
     * @param col
     * @return
     */
    Set<Integer> getSingleValuesInSquare(int row, int col) {
        Set<Integer> values = new HashSet<>();

        // iterate 3 rows for square
        for (int rowOffset = row * 3; rowOffset < (row * 3) + 3; rowOffset++) {
            List<List<Integer>> currentRow = getValuesInRow(rowOffset);

            // iterate 3 cells for current row
            for (int cellOffset = col * 3; cellOffset < (col * 3) + 3; cellOffset++) {
                List<Integer> cellContent = currentRow.get(cellOffset);
                // only collect cells where we have a single (final) value,
                // not a list of possible values
                if (cellContent.size() == 1) {
                    values.add(cellContent.get(0));
                }
            }
        }

        return values;
    }

    
    /**
     * Updates empty values in a square. Checks for single values in the same
     * column first, then the same row, and removes them from the guessed
     * values.
     * 
     * @param squareRow
     * @param squareCol
     * @param missingValuesInSquare
     * @return
     */
    boolean updateValuesInSquare(int squareRow, int squareCol, List<Integer> missingValuesInSquare) {

        boolean replacedValuesOnThisPass = false;

        // iterate 3 rows for square
        for (int row = squareRow * 3; row < (squareRow * 3) + 3; row++) {
            List<List<Integer>> currentRow = getValuesInRow(row);

            // get single values in same row

            Set<Integer> singleValuesInSameRow = this.getSingleValuesInRow(row);

            // iterate 3 columns for current row of this square
            for (int col = squareCol * 3; col < (squareCol * 3) + 3; col++) {

                List<Integer> cellContent = currentRow.get(col);

                // if the current cell is empty, replace it with the possible
                // list of guesses
                if (cellContent.size() == 0) {

                    Set<Integer> guessesForThisCell = new HashSet<>(missingValuesInSquare);

                    // remove single values for the same row
                    guessesForThisCell.removeAll(singleValuesInSameRow);

                    // remove single values in same column
                    Set<Integer> singleValuesInSameColumn = this.getSingleValuesInColumn(col);
                    guessesForThisCell.removeAll(singleValuesInSameColumn);

                    currentRow.set(col, new ArrayList<>(guessesForThisCell));

                    replacedValuesOnThisPass = true;
                }
            }
        }
        return replacedValuesOnThisPass;
    }

    
    private Set<Integer> getSingleValuesInSquareByRowCol(int row, int col) {
        int squareRow = this.getSquareRowFromRow(row);
        int squareCol = this.getSquareColFromCol(col);
        Set<Integer> singleValuesInSquare = this.getSingleValuesInSquare(squareRow, squareCol);
        return singleValuesInSquare;
    }

    
    int getSquareColFromCol(int col) {
        int squareCol = col / 3;
        return squareCol;
    }

    
    int getSquareRowFromRow(int row) {
        int rowSquare = row / 3;
        return rowSquare;
    }

    
    List<Integer> getValueInCell(int row, int col) {
        List<List<Integer>> valuesInRow = this.getValuesInRow(row);
        return valuesInRow.get(col);
    }

    
    List<List<Integer>> getValuesInRow(int row) {
        List<List<Integer>> currentRow = this.solutionGrid.get(row);
        return currentRow;
    }

    
    Set<Integer> getValuesInRowAsSet(int row) {
        Set<Integer> values = new HashSet<>();

        List<List<Integer>> currentRow = getValuesInRow(row);

        for (int col = 0; col < 8; col++) {

            List<Integer> valuesForCell = currentRow.get(col);
            values.addAll(valuesForCell);
        }

        return values;
    }

    /**
     * Retrieves a set of unique values for the specified column. Iterates
     * through all rows to retrieve each value for that column from each row.
     * 
     * @param col
     * @return
     */
    Set<Integer> getValuesInColumnAsSet(int col) {
        Set<Integer> values = new HashSet<>();

        for (int row = 0; row < 8; row++) {
            List<List<Integer>> currentRow = getValuesInRow(row);
            List<Integer> valuesForCell = currentRow.get(col);
            values.addAll(valuesForCell);
        }

        return values;
    }


    Set<Integer> getMissingPotentialValues(Set<Integer> currentValues) {

        Set<Integer> missingValues = new HashSet<>(SudokuSolverApp.allowedValues);
        missingValues.removeAll(currentValues);
        return missingValues;
    }

    
    public int[][] getSudokuGrid() {
        return startingSudokuGrid;
    }

    public void setSudokuGrid(int[][] sudokuGrid) {
        this.startingSudokuGrid = sudokuGrid;
    }

    boolean isValueInCellValid(int row, int col) {
        // TODO not used
        return true;
    }

    boolean isGuessForCellValid(int row, int col, int guess) {
        // TODO not used
        return true;
    }

}