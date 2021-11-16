package project4;

import java.util.ArrayList;
import java.util.Arrays;

/** Receives a puzzle as command-line input and presents its solutions
 *
 * @author Aramis Tanelus
 */
public class WayFinder {

    /** Converts the command line arguments to an int array for processing
     *
     * @param args The command line argument array
     * @throws IllegalArgumentException if any number is outside the range [0, 99], there are no numbers given, or non-integer inputs are provided
     * @return an int array representing the input puzzle
     */
    private static int[] parseArguments(String[] args) throws IllegalArgumentException {
        if (args.length == 0)
            throw new IllegalArgumentException("ERROR: You must provide the puzzle tiles as command line arguments.");

        int[] puzzleTiles = new int[args.length];
        try {
            for(int i = 0; i < args.length; i++)
                puzzleTiles[i] = Integer.parseInt(args[i]);
        } catch (Exception e) {
            throw new IllegalArgumentException("ERROR: All puzzle ties must be integers.");
        }
        return puzzleTiles;
    }


    public static void main(String[] args) {
        int[] puzzleTiles = null;
        try {
            puzzleTiles = parseArguments(args);
        } catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
            return;
        }

        try {
            Puzzle p = new Puzzle(puzzleTiles);
            ArrayList<String> solutions = p.getSolutions();
            int numSolutions = solutions.size();
            for (String solution : solutions) {
                System.out.println(solution);
                System.out.println();
            }
            if (numSolutions == 0) {
                System.out.println("No way through this puzzle.");
            } else if (numSolutions == 1) {
                System.out.println("There is 1 way through the puzzle.");
            } else {
                System.out.printf("There are %d ways through the puzzle.", numSolutions);
            }
        } catch (Exception e) {
            // The exact error message is provided by the thowing methods
            System.err.println(e.getMessage());
            return;
        }
    }
}

