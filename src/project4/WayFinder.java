package project4;

import java.util.ArrayList;

public class WayFinder {

    public static void main(String[] args) {
        int[] data = {6, 9, 3, 10, 2, 1, 5, 8, 9, 1, 2, 5, 4, 8, 10, 7, 6, 0};
        Puzzle p = new Puzzle(data);
        ArrayList<String> solutions = p.getSolutions();
    }
}
