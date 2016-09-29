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

	enum Grouping{
		row,
		column,
		square
	}
	
	private static final Set<Integer> allowedValues = 
			new HashSet<>(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9}));
	
	//example grid to solve
	private int[][] sudokuGrid = {
			{0,0,0,8,1,0,6,7,0},
			{0,0,7,4,9,0,2,0,8},
			{0,6,0,0,5,0,1,0,4},
			{1,0,0,0,0,3,9,0,0},
			{4,0,0,0,8,0,0,0,7},
			{0,0,6,9,0,0,0,0,3},
			{9,0,2,0,3,0,0,6,0},
			{6,0,1,0,7,4,3,0,0},
			{0,3,4,0,6,9,0,0,0}
	};
	
	//list (rows) of list of list of integers
	//eg { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, ... },
	//   { {1, 2, 3}, {1, 2, 3}, {1, 2, 3}, ... }, 
	//   ... }
	private List<List<List<Integer>>> solutionGrid = new ArrayList<>();
	
	public SudokuSolverApp(){};
	
	public static void main(String[] args) {


		SudokuSolverApp app = new SudokuSolverApp();
		app.print();
		app.populateSolutionGridWithStartingPosition();
		app.printSolutionGrid();
				
		app.solve();
	}

	private void printValuesSet(Set<Integer> squareValues) {
		for(Integer i : squareValues){
			System.out.print(i.toString() + ", ");
		}
		System.out.println();
		
	}

	private void printSolutionGrid() {
		for(List<List<Integer>> row : this.solutionGrid){
			for(List<Integer> currentCell : row){
				System.out.print("{ ");
				for(Integer i : currentCell){
					System.out.print(i.toString() + ", ");
				}
				System.out.print(" },");
			}
			System.out.println();
		}
		
	}

	private void populateSolutionGridWithStartingPosition() {
		for(int row = 0; row < 9; row++){
			List<List<Integer>> currentRow = new ArrayList<>();
			
			for(int col = 0; col < 9; col++){
				List<Integer> currentCellPossibleNumberList = new ArrayList<>();
				int value = sudokuGrid[row][col];
				//if we have starting number for cell, add it to solution grid,
				//otherwise add an empty list for now - we'll come back and
				//populate each empty list with possible numbers when we start
				//solving
				if(value > 0){
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
		for(int row = 0; row < 9; row++){
			for(int col = 0; col < 9; col++){
				int value = sudokuGrid[row][col];
				System.out.print((value == 0 ? " " : value) + " ");
				if(col == 2 || col == 5){
					System.out.print("| ");	
				}
			}
			if(row == 2 || row == 5){
				System.out.println("\n- - - + - - - + - - -");
			} else {
				System.out.println();
			}
			
		}
		
	}

	void solve(){
		//TODO not sure if I'll use these, but could use to optimize approach
		int row = findRowWithMostNumbers();
		System.out.println("Row with most numbers: " + row);
		
		int col = findColumnWithMostNumbers();
		System.out.println("Col with most numbers: " + col);

		int square = findSquareWithMostNumbers();
		
		Grouping next = findNextBestCandidateGroupingForMatches(row, col, square);
		
		//iterate squares
		for(int rowSquare=0; rowSquare<3; rowSquare++){
			for(int colSquare=0; colSquare<3; colSquare++){
				System.out.print("Square " + rowSquare + ", "
						+ colSquare + ": ");
				Set<Integer> squareValues = this.getValuesInSquare(rowSquare, colSquare);
				this.printValuesSet(squareValues);
				Set<Integer> missingValues = this.getMissingPotentialValues(squareValues);
				System.out.print("Missing values: ");
				this.printValuesSet(missingValues);
				
				//TODO insert missing values into every blank cell in this square
				//TODO for each cell, remove any duplicates from same column
				//TODO for each row, remove any duplicates from the same row
			}
		}
		
	}

	/**
	 * Retrieves set of current values in a square, by iterating 3 rows from the starting row, and 
	 * 3 columns from the starting column.
	 * 
	 * Squares are referenced:
	 * first row:  {0,0}, {0,1}, {0,2}
	 * second row: {1,0}, {1,1}, {1,2}
	 * etc
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	Set<Integer> getValuesInSquare(int row, int col){
		Set<Integer> values = new HashSet<>();
		
		//iterate 3 rows for square
		for(int rowOffset=row*3; rowOffset < (row*3) + 3; rowOffset++){
			List<List<Integer>> currentRow = this.solutionGrid.get(rowOffset);
			
			//iterate 3 cells for current row
			for(int cellOffset=col*3; cellOffset < (col*3) + 3; cellOffset++){
				List<Integer> cellContent = currentRow.get(cellOffset);
				//only collect cells where we have a single (final) value,
				//not a list of possible values
				if(cellContent.size() == 1){
					values.add(cellContent.get(0));
				}
			}
		}
				
		return values;
	}
	
	Set<Integer> getValuesInRow(int row){
		Set<Integer> values = new HashSet<>();
		
		//TODO
		return values;
	}
	
	Set<Integer> getValuesInCol(int col){
		Set<Integer> values = new HashSet<>();
		
		//TODO
		return values;
	}
	
	Set<Integer> getMissingPotentialValues(Set<Integer> currentValues){
		
		Set<Integer> missingValues = new HashSet<>(SudokuSolverApp.allowedValues);
		missingValues.removeAll(currentValues);
		return missingValues;
	}
	
	private Grouping findNextBestCandidateGroupingForMatches(int row, int col, int square) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Find column with most numbers as best candidate to start looking 
	 * for potential numbers for blanks.
	 * @return
	 */
	private int findColumnWithMostNumbers() {
		int result = 0;
		int currentCount = 0;
		int currentLargest = 0;
		
		for(int col = 0; col < 9; col++){
			
			for(int row = 0; row < 9; row++){
				int currentValue = this.sudokuGrid[row][col];
				if(currentValue > 0){
					currentCount++;
				}
			}
			
			if(currentCount > currentLargest){
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
	 * Find row with most numbers as best candidate to start looking 
	 * for potential numbers for blanks.
	 * @return
	 */
	private int findRowWithMostNumbers() {
		int result = 0;
		int currentCount = 0;
		int currentLargest = 0;
		
		for(int row=0; row<9; row++){
			int values[] = this.sudokuGrid[row];
			
			for(int col= 0; col < 9; col++){
				if(values[col] > 0){
					currentCount++;
				}
			}
			
			if(currentCount > currentLargest){
				result = row;
				currentLargest = currentCount;
				
			}
			currentCount = 0;
		}
		return result;
	}
	
}
