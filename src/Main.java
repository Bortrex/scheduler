import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.SingleSolutionDetector;

import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ContradictionException, TimeoutException {
        if (args.length < 2) {
            System.out.println("Too few arguments to run. Please provide 'data.in' and 'data.out'.");
            //return;
        } else if (args.length == 2) {
            System.out.println("Solving a schedule class problem.");

            String input = args[0];
            String output = args[1];

//            solveSudoku(input, output);
            solveSchedule(input, output);
            System.out.format("Here we got two files..(%s)!\n", input);
        } else {
            System.out.println("Too many arguments to run. Please provide at most an input and an output file.");
            return;
        }
        System.out.println("\nFinished execution.");
        //return;
    }

    private static void solveSchedule(String input, String output) throws TimeoutException, FileNotFoundException, UnsupportedEncodingException {

        //Create the solver with a 1h timeout
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600);

        //Add the clauses

        addingClauses(input, solver);
    }

    private static void addingClauses(String input, ISolver solver) {

        int[][] a = readFiles(input);


        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();

        }

    }

    private static int[][] readFiles(String file) {
        int rows = 2, columns = 4;

        int[][] arr = new int[rows][];
        int[] second, first;
        try {
            Scanner input = new Scanner(new File(file));
            first = new int[columns];
            for (int i = 0; i < first.length; ++i)
                first[i] = input.nextInt();

            second = new int[first[0]];
            for (int i = 0; i < second.length; i++)
                second[i] = input.nextInt();

        } catch (Exception e) {
            return null;
        }
        arr[0] = first;
        arr[1] = second;

        return arr;
    }

}
