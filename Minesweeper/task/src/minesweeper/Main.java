package minesweeper;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

	private static final char MINE = 'X';

	private static final char MARK = '*';

	private static final char BLANK = '.';

	private static final char DISCOVERED = '/';

	private static int gridW = 9;
	private static int gridH =9;

	private static char[][] field = new char[gridW][gridH];

	private static char[][] playersCopy = new char[gridW][gridH];
	private static int numberOfMines;

	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		System.out.println("How many mines do you want on the field?");
		numberOfMines = scanner.nextInt();
		play();
	}

	private static void prepareField(int x, int y) {

		for (char[] row : field) {
			Arrays.fill(row, BLANK);
		}
		setMines(x, y);
		calculateMinesAround();


	}

	private static void play() {

		boolean firstMove = true;

		boolean won = false;
		boolean lost = false;
		for (char[] row : playersCopy) {
			Arrays.fill(row, BLANK);
		}
		printArrayForPlayer();
		while (!won) {
			System.out.println("Set/unset mines marks or claim a cell as free (format: X Y free/mine):");
			int col = scanner.nextInt() - 1;
			int row = scanner.nextInt() - 1;
			String action = scanner.next().trim();

			if(!firstMove) {
				lost = hitTheMine(row, col);
				if (lost) {
					break;
				}
			}
			switch (action) {
				case "free":
					if(firstMove){
						prepareField(row, col);
						firstMove = false;
					}
					reveal(row, col);
					printArrayForPlayer();
					break;
				case "mine":
					makeAMove(row, col);
					break;
			}
			won = checkIfWin();
		}

		if (lost) {
			markMines();
			printArrayForPlayer();
			System.out.println("You stepped on a mine and failed!");
		} else {
			System.out.println("Congratulations! You found all mines!");
		}

	}

	private static int calculateNear(int x, int y) {

		if (outOfBounds(x, y)) {
			return  0;
		}
		int[] around = new int[]{-1, 0, 1};

		int minesAround = 0;

		for (int offsetX : around) {
			for (int offsetY : around) {
				if(outOfBounds(offsetX + x, offsetY + y )){
					continue;
				}
				char cell = field[offsetX + x][offsetY + y];

				if (cell == MINE) {
					minesAround++;
				}
			}
		}
		return  minesAround;
	}

	private static boolean outOfBounds(int x, int y) {

		return x < 0 || y < 0 || x >= gridW || y >= gridH;
	}

	private static void reveal(int x, int y) {

		if (outOfBounds(x, y)) {
			return;
		}

		char playerCell = playersCopy[x][y];
		if (playerCell == DISCOVERED) {
			return;
		}
		char cell = field[x][y];
		if (cell != BLANK) {
			playersCopy[x][y] = cell;
			return;
		}
		playersCopy[x][y] = DISCOVERED;

		reveal(x - 1, y - 1);
		reveal(x - 1, y + 1);
		reveal(x + 1, y - 1);
		reveal(x + 1, y + 1);
		reveal(x - 1, y);
		reveal(x, y - 1);
		reveal(x, y + 1);
		reveal(x + 1, y);



	}

	private static void makeAMove(int x, int y) {

		if (playersCopy[x][y] == MARK) {
			playersCopy[x][y] = BLANK;
		} else {
			playersCopy[x][y] = MARK;
		}
		printArrayForPlayer();
	}

	private static boolean hitTheMine(int x, int y) {

		return field[x][y] == MINE;
	}

	private static void markMines() {

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				if (field[i][j] == MINE) {
					playersCopy[i][j] = MINE;
				}
			}
		}
	}

	private static void setMines(int row , int col) {

		Random random = new Random();
		int count = 0;

		while (count < numberOfMines) {
			int x = random.nextInt(gridW);
			int y = random.nextInt(gridH);
			if(x == row & y== col) {
				continue;
			}
			if (field[x][y] == BLANK) {
				field[x][y] = MINE;
				count++;
			}
		}
	}

	private static void calculateMinesAround() {

		for (int row = 0; row < field.length; row++) {
			char[] line = field[row];
			for (int col = 0; col < line.length; col++) {
				char c = field[row][col];
				if (c == MINE) {
					continue;
				}
				int minesAround = calculateNear(row, col);

				if (minesAround != 0) {
					field[row][col] = (char) (minesAround + '0');
				}
			}
		}
	}

	private static boolean checkIfWin() {

		int countMines = 0;
		int countMarks = 0;
		int countBlanks = 0;

		for (int row = 0; row < field.length; row++) {
			char[] line = field[row];
			for (int col = 0; col < line.length; col++) {
				if(playersCopy[row][col] == MARK){
					countMarks++;
					if(field[row][col] == MINE){
						countMines++;
					}
				}
				else if(playersCopy[row][col] == BLANK){
					countBlanks++;
				}

			}
		}

		if (countMarks > numberOfMines) {
			return false;
		}
		if (countMarks + countBlanks > numberOfMines) {
			return false;
		}


		return countMines == numberOfMines || countBlanks == numberOfMines ;
	}

	private static int countNotDiscoveredCells() {
		int count = 0;

		for (char[] chars : playersCopy) {
			for (int j = 0; j < playersCopy[0].length; j++) {
				if (chars[j] == BLANK) {
					count++;
				}
			}
		}
		return count;
	}

	private static void printEmptyArray() {

		int row = 1;
		System.out.println(" │123456789│");
		System.out.println("—│—————————│");
		for (int i = 0; i < gridW; i++) {
			System.out.print(row + "│");
			row++;
			for (int j = 0; j < gridH; j++) {
				System.out.print(BLANK);
			}
			System.out.print("│");
			System.out.println();
		}
		System.out.println("—│—————————│");

	}

	private static void printArrayForPlayer() {

		int row = 1;
		System.out.println(" │123456789│");
		System.out.println("—│—————————│");
		for (char[] chars : playersCopy) {
			System.out.print(row + "│");
			row++;
			for (int j = 0; j < playersCopy[0].length; j++) {
				System.out.print(chars[j]);
			}
			System.out.print("│");
			System.out.println();
		}
		System.out.println("—│—————————│");
	}
}
