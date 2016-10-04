package kh.sudokusolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class SudokuSolverAppTest {

	private SudokuSolverApp app = new SudokuSolverApp();
	
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
	
	public SudokuSolverAppTest() {
		app.setSudokuGrid(this.sudokuGrid);
	}
	
	@Test
	public void testGetValuesInSquare00() {
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(6);
		expectedValues.add(7);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInSquare(0, 0);
		assertTrue(values.containsAll(expectedValues));
	}

	@Test
	public void testGetValuesInSquare01() {
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(1);
		expectedValues.add(4);
		expectedValues.add(6);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInSquare(1, 0);
		assertTrue(values.containsAll(expectedValues));
	}

	@Test
	public void testGetValuesInSquare10() {
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(1);
		expectedValues.add(4);
		expectedValues.add(5);
		expectedValues.add(8);
		expectedValues.add(9);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInSquare(0, 1);
		assertTrue(values.containsAll(expectedValues));
	}
	
	@Test
	public void testGetValuesInCol0(){
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(1);
		expectedValues.add(4);
		expectedValues.add(6);
		expectedValues.add(9);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInColumn(0);
		
		assertTrue(values.containsAll(expectedValues));
		assertFalse(values.contains(2));
		assertFalse(values.contains(3));
		assertFalse(values.contains(5));
		assertFalse(values.contains(7));
		assertFalse(values.contains(8));

	}

	@Test
	public void testGetValuesInCol8(){
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(3);
		expectedValues.add(4);
		expectedValues.add(7);
		expectedValues.add(8);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInColumn(8);
		
		assertTrue(values.containsAll(expectedValues));
		assertFalse(values.contains(1));
		assertFalse(values.contains(2));
		assertFalse(values.contains(5));
		assertFalse(values.contains(6));
		assertFalse(values.contains(9));

	}

	@Test
	public void testGetValuesInRow0(){
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(1);
		expectedValues.add(6);
		expectedValues.add(7);
		expectedValues.add(8);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInRow(0);
		
		assertTrue(values.containsAll(expectedValues));
		assertFalse(values.contains(2));
		assertFalse(values.contains(3));
		assertFalse(values.contains(4));
		assertFalse(values.contains(5));
		assertFalse(values.contains(9));

	}

	@Test
	public void testGetValuesInRow8(){
		Set<Integer> expectedValues = new HashSet<>();
		expectedValues.add(3);
		expectedValues.add(4);
		expectedValues.add(6);
		expectedValues.add(9);
		this.app.populateSolutionGridWithStartingPosition();
		Set<Integer> values = this.app.getValuesInRow(8);
		
		assertTrue(values.containsAll(expectedValues));
		assertFalse(values.contains(1));
		assertFalse(values.contains(2));
		assertFalse(values.contains(5));
		assertFalse(values.contains(7));
		assertFalse(values.contains(8));

	}

	
}
