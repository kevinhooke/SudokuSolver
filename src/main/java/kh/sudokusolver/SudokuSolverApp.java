package kh.sudokusolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sudoku solver.
 * 
 * @author kevinhooke
 *
 */
public class SudokuSolverApp {

	enum Grouping {
		row, column, square
	}

	private static final Set<Integer> allowedValues = new HashSet<>(
			Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }));

	// example grid to solve
	private int[][] sudokuGrid = { { 0, 0, 0, 8, 1, 0, 6, 7, 0 }, { 0, 0, 7, 4, 9, 0, 2, 0, 8 },
			{ 0, 6, 0, 0, 5, 0, 1, 0, 4 }, { 1, 0, 0, 0, 0, 3, 9, 0, 0 }, { 4, 0, 0, 0, 8, 0, 0, 0, 7 },
			{ 0, 0, 6, 9, 0, 0, 0, 0, 3 }, { 9, 0, 2, 0, 3, 0, 0, 6, 0 }, { 6, 0, 1, 0, 7, 4, 3, 0, 0 },
			{ 0, 3, 4, 0, 6, 9, 0, 0, 0 } };

	// list (rows) of list of list of integers
	// eg { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, ... },
	// { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, ... },
	// ... }
	private List<List<List<Integer>>> solutionGrid = new ArrayList<>();

	boolean foundValuesOnLastPass = true;

	public SudokuSolverApp() {
	};

	public static void main(String[] args) {

		SudokuSolverApp app = new SudokuSolverApp();
		app.print();
		app.populateSolutionGridWithStartingPosition();
		app.printSolutionGrid();

		app.solve();

		app.printSolutionGrid();
	}

	private void printValuesSet(Set<Integer> squareValues) {
		for (Integer i : squareValues) {
			System.out.print(i.toString() + ", ");
		}
		System.out.println();

	}

	private void printSolutionGrid() {
		for (List<List<Integer>> row : this.solutionGrid) {
			for (List<Integer> currentCell : row) {
				System.out.print("{ ");
				for (Integer i : currentCell) {
					System.out.print(i.toString() + ", ");
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
				int value = sudokuGrid[row][col];
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
	 * Prints the puzzle grid.
	 * 
	 */
	private void print() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				int value = sudokuGrid[row][col];
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

	void solve() {

		//---
		// TODO not sure if I'll use these, but could use to optimize approach
		int rowWithMostNumbers = findRowWithMostNumbers();
		System.out.println("Row with most numbers: " + rowWithMostNumbers);

		int colWithMostNumbers = findColumnWithMostNumbers();
		System.out.println("Col with most numbers: " + colWithMostNumbers);

		int square = findSquareWithMostNumbers();

		Grouping next = findNextBestCandidateGroupingForMatches(colWithMostNumbers, colWithMostNumbers, square);
		//---
		
		int passesThroughGridCount = 0;
		
		while (foundValuesOnLastPass) {
			// iterate squares
			for (int rowSquare = 0; rowSquare < 3; rowSquare++) {
				for (int colSquare = 0; colSquare < 3; colSquare++) {
					System.out.print("Square " + rowSquare + ", " + colSquare + ": ");
					Set<Integer> squareValues = this.getValuesInSquare(rowSquare, colSquare);
					this.printValuesSet(squareValues);
					Set<Integer> missingValues = this.getMissingPotentialValues(squareValues);
					System.out.print("Missing values: ");
					this.printValuesSet(missingValues);

					//insert missing values into every blank cell in this square
					foundValuesOnLastPass = this.updateValuesInSquare(rowSquare, colSquare, new ArrayList<Integer>(missingValues));
				}
			}
			
			//get set of all single (guessed) values for each column, and remove
			//from any sets of guesses
			Set<Integer> singleValues = new HashSet<Integer>();
			for(int col = 0; col < 8; col++){
				for(int row = 0; row < 8; row++){
					Set<Integer> valuesInSameColumn = this.getValuesInColumn(col);
					//if(values){
					//	valuesInColumn.addAll(valuesInColumn);
					}
				}
			}
			
			// TODO for each row, remove any duplicates from the same
			// row

			passesThroughGridCount++;
		}
		System.out.println("Passes through grid: " + passesThroughGridCount);
	}

	private void insertMissingValuesIntoBlankCells(Set<Integer> missingValues, int rowSquare, int colSquare) {
		// TODO Auto-generated method stub

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
	Set<Integer> getValuesInSquare(int row, int col) {
		Set<Integer> values = new HashSet<>();

		// iterate 3 rows for square
		for (int rowOffset = row * 3; rowOffset < (row * 3) + 3; rowOffset++) {
			List<List<Integer>> currentRow = this.solutionGrid.get(rowOffset);

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

	boolean updateValuesInSquare(int row, int col, List<Integer> newValues) {
		Set<Integer> values = new HashSet<>();
		boolean replacedValuesOnThisPass = false;
		
		// iterate 3 rows for square
		for (int rowOffset = row * 3; rowOffset < (row * 3) + 3; rowOffset++) {
			List<List<Integer>> currentRow = this.solutionGrid.get(rowOffset);

			// iterate 3 cells for current row
			for (int cellOffset = col * 3; cellOffset < (col * 3) + 3; cellOffset++) {
				List<Integer> cellContent = currentRow.get(cellOffset);
				// if the current cell is empty, replace it with the possible
				// list
				if (cellContent.size() == 0) {
					currentRow.set(cellOffset, newValues);
					replacedValuesOnThisPass = true;
				}
			}
		}
		
		return replacedValuesOnThisPass;
	}

	Set<Integer> getValuesInRow(int row) {
		Set<Integer> values = new HashSet<>();

		List<List<Integer>> currentRow = this.solutionGrid.get(row);
		
		for(int col = 0; col < 8; col ++){
			
			List<Integer> valuesForCell = currentRow.get(col);
			values.addAll(valuesForCell);
		}
		
		return values;
	}

	/**
	 * Retrieves a set of unique values for the specified column. Iterates through all rows to retrieve 
	 * each value for that column from each row.
	 * 
	 * @param col
	 * @return
	 */
	Set<Integer> getValuesInColumn(int col) {
		Set<Integer> values = new HashSet<>();

		for(int row = 0; row < 8; row ++){
			List<List<Integer>> currentRow = this.solutionGrid.get(row);
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

	private Grouping findNextBestCandidateGroupingForMatches(int row, int col, int square) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Find column with most numbers as best candidate to start looking for
	 * potential numbers for blanks.
	 * 
	 * @return
	 */
	private int findColumnWithMostNumbers() {
		int result = 0;
		int currentCount = 0;
		int currentLargest = 0;

		for (int col = 0; col < 9; col++) {

			for (int row = 0; row < 9; row++) {
				int currentValue = this.sudokuGrid[row][col];
				if (currentValue > 0) {
					currentCount++;
				}
			}

			if (currentCount > currentLargest) {
				result = col;
				currentLargest = currentCount;

			}
			currentCount = 0;
		}
		return result;
	}

	private int findSquareWithMostNumbers() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Find row with most numbers as best candidate to start looking for
	 * potential numbers for blanks.
	 * 
	 * @return
	 */
	private int findRowWithMostNumbers() {
		int result = 0;
		int currentCount = 0;
		int currentLargest = 0;

		for (int row = 0; row < 9; row++) {
			int values[] = this.sudokuGrid[row];

			for (int col = 0; col < 9; col++) {
				if (values[col] > 0) {
					currentCount++;
				}
			}

			if (currentCount > currentLargest) {
				result = row;
				currentLargest = currentCount;

			}
			currentCount = 0;
		}
		return result;
	}

	public int[][] getSudokuGrid() {
		return sudokuGrid;
	}

	public void setSudokuGrid(int[][] sudokuGrid) {
		this.sudokuGrid = sudokuGrid;
	}

}
