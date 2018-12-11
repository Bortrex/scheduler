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
        if (args.length < 1){
            System.out.println("Too few arguments to run. Please provide at least an output file.");
            return;
        }else if (args.length == 1){
            System.out.println("Generating a new sudoku grid.");

            String output = args[0];

            generateSudoku(output);

        }else if (args.length == 2){
            System.out.println("Solving a sudoku grid.");

            String input = args[0];
            String output = args[1];
//            generateSudoku(input);//the sudoku was not generated before solving it
            solveSudoku(input, output);
        }else{
            System.out.println("Too many arguments to run. Please provide at most an input and an output file.");
            return;
        }
        System.out.println("\nFinished execution.");
        return;
    }
//        String file = "sample1.txt", out_file = "solved1.txt", test = "generate1.txt"; //change here to test different sudokus!
//        int[][]data = readFiles(file);
//
//        System.out.println("Sudoku to be solve..");
//        for(int i=0; i<9; i++) {
//            for (int j = 0; j < 9; j++) {
//                System.out.print(data[i][j] + "  ");
//                if (j % 3 == 2) {
//                    System.out.print("| ");
//                }
//            }
//            if (i % 3 == 2) {
//                System.out.print("\n=========+==========+==========+\n");
//            } else {
//                System.out.print("\n");
//            }
//        }

    //Create the solver
//        ISolver solver = SolverFactory.newDefault();
////        ISolver gen = SolverFactory.newDefault();
//        solver.setTimeout(3600); // 1 hour timeout
//        //Add the clauses
//        makeClauses(solver);
//        //Add the sudoku
//        addData(file, solver);
//
////        makeClauses(gen);
//        generate(test);//,gen);
//
//        //SingleSolutionDetector problem = new SingleSolutionDetector(SolverFactory.newMiniSAT());
//        // feed problem/solver as usual if (problem.isSatisfiable()) { if (problem.hasASingleSolution()) {
//        // great, the instance has a unique solution int [] uniquesolution = problem.getModel(); } else {
//        // too bad, got more than one } }
//        try {
//            IProblem problem = solver;
////            IProblem i_gen = gen;
//            if (problem.isSatisfiable()) {
//
//                System.out.println("Satisfiable !\n");
//                printSudoku(problem.model(), out_file);
//
//            }
////            if (i_gen.isSatisfiable())
////            {
////                printSudoku(i_gen.model(), test);
////            }
//            else {
//                System.out.println("Unsatisfiable !");
//            }
//
//        } catch (TimeoutException e) {
//            System.out.println ("Timeout, sorry !");
//        }
//
//        System.out.println("Finished!");
//    }

    //Generates a new sudoku and print it in the output
    private static void generateSudoku(String output) throws ContradictionException, TimeoutException, FileNotFoundException, UnsupportedEncodingException {
        //Create the solver with a 1h timeout
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600);

        //Add the clauses
        makeClauses(solver);

        //Sudoku grid to fill
        int[][] grid = new int[9][9];

        //Clauses for the sudoku data
        int[] clause = new int[81];
        for(int i = 0; i < 81; i++) clause[i] = 0;

        int nClauses = 0;

        //Forbidden clauses for the sudoku data
        int[] Fclause = new int[729];
        int FnClauses = 0;

        //Create the detector
        SingleSolutionDetector problem = new SingleSolutionDetector(solver);

        //While the problem has more than one solution, given the added data
        while (problem.isSatisfiable(new VecInt(clause)) && ! problem.hasASingleSolution(new VecInt(clause)))
        {
            //Add a random clause to the list, ie place a random number in the grid
            int min = 111, max = 999;
            int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

            while(isForbidden(Fclause, randomNum)){
                //System.out.println("Fordidden clause:" + randomNum);
                randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

                //If all clauses have been tried
                if (nClauses + FnClauses >= 729){
                    System.out.println("Backtracking failed.");
                    return;
                }
            }

            clause[nClauses++] = randomNum;
            //System.out.println("Clause added:" + clause[nClauses-1]);

            //Check that the model is still satisfiable
            if (! problem.isSatisfiable(new VecInt(clause))){
                //System.out.println("Clause removed:" + clause[nClauses-1]);

                //Remove the last clause
                clause[--nClauses] = 0;

                //Prevent from adding this clause again
                Fclause[FnClauses++] = randomNum;
            }

        }

        //Print the sudoku to the file
        printSudoku2(clause);
        printSudoku2(clause, output);
    }


    private static boolean isForbidden(int[] Fclause, int number){
        //Numbers [ij0] are forbidden
        if(number%10 == 0)
            return true;

        //Numbers [i0k] are forbidden
        if((number/10) %10 == 0)
            return true;

        int i = 0;
        while (Fclause[i] != 0){
            if (Fclause[i] == number) return true;
            i++;
        }

        return false;
    }

    //Solves the sudoku given as input, prints the solution in the output
    private static void solveSudoku(String input, String output) throws ContradictionException, TimeoutException, FileNotFoundException, UnsupportedEncodingException {
        //Create the solver with a 1h timeout
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600);

        //Add the clauses
        makeClauses(solver);

        //Add the sudoku data
        addData(input, solver);

        try{
            IProblem problem = solver;

            if (problem.isSatisfiable()){
                System.out.println("Satisfiable sudoku.");
                printSudoku(problem.model());
                printSudoku(problem.model(), output);
            }else{
                System.out.println("Unsatisfiable sudoku.");
            }

        }catch (TimeoutException e){
            System.out.println("Timeout of the solver.");
        }
    }
    public static void addData(String file, ISolver solver) throws ContradictionException
    {
        int rows =9, columns=9, counter=0;
        int a[][] = readFiles(file);

        for(int i = 0; i < rows; ++i)
        {
            for(int j = 0; j < columns; ++j)
            {
                if(a[i][j] != 0)
                {
                    int[] clause = new int[1];
                    clause[0] = (i+1)*100 + (j+1)*10 + a[i][j];
                    solver.addClause(new VecInt(clause));
                    //   output = output.concat((i+1)+""+(j+1)+""+a[i][j]+" 0\n");
                    counter++;
                }
            }
        }
    }
    private static void printSudoku2(int[] clause){
        int[][] grid = new int[9][9];

        for (int m = 0; m < clause.length; m++){
            int k = clause[m] % 10;
            int j = (clause[m]/10) % 10;
            int i = (clause[m]/100);

            if (clause[m] == 0) break;

            grid[i-1][j-1] = k;
        }

        System.out.println("-------------------------");
        for (int i = 0; i< 9; i++)
        {
            System.out.print("| ");
            for (int j = 0; j <  9; j++)
            {
                System.out.print(grid[i][j] + " " );
                if (j%3 == 2) System.out.print("| ");
            }
            System.out.println();
            if (i%3 == 2)
                System.out.println("-------------------------");
        }
    }


    private static void printSudoku2(int[] clause, String file) throws FileNotFoundException, UnsupportedEncodingException {
        int[][] grid = new int[9][9];

        for (int m = 0; m < clause.length; m++){
            int k = clause[m] % 10;
            int j = (clause[m]/10) % 10;
            int i = (clause[m]/100);

            if (clause[m] == 0) break;

            grid[i-1][j-1] = k;
        }

        PrintWriter outFile = new PrintWriter(file, "UTF-8");

        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
                outFile.print(grid[i][j] + " ");

            outFile.print("\n");
        }

        outFile.close();
    }

//    public static void generate(String out_file)throws ContradictionException {//, ISolver solver) {
//        int rows =9, columns=9;
////        int n_rand = ThreadLocalRandom.current().nextInt(1, (9+1));
//        int a[][] = new int[rows][columns];//readFiles(file);
//        ISolver gen = SolverFactory.newDefault();int count = 0;
//        makeClauses(gen);
////        ArrayList<Integer> numbers = new ArrayList<Integer>();
//        for(int i = 0; i < rows; ++i)
//        {
//            for(int j = 0; j < columns; ++j)
//            {
//                int random = ThreadLocalRandom.current().nextInt(1, (9+1));
////                System.out.print(random);
////                if(!numbers.contains(random))
////                {
////                    numbers.add(random);
////                }
//                a[i][j] = ThreadLocalRandom.current().nextInt(1, (9+1));
////                System.out.print("-"+a[i][j]+" ");
////
//////                int k = ThreadLocalRandom.current().nextInt(1, (9+1));
//////                int l = ThreadLocalRandom.current().nextInt(1, (9+1));
//////                System.out.print(a[i][j]);
////
//
//                if (!Arrays.asList(a[i][j]).contains(random))// && i < 4 && j < 4)
////                if(i < 2 && j < 2)
//                {  System.out.print(a[i][j]);
////                    count++;
////                    System.out.print(count);
//
//                    int[] clause = new int[1];
//                    clause[0] = (i+1)*100 + (j+1)*10 + a[i][j];
//                    gen.addClause(new VecInt(clause));
////
//                }
//            }
//            System.out.println();
//        }
//
//        SingleSolutionDetector single = new SingleSolutionDetector(gen);
//        try {
////            IProblem i_gen = gen;
//
//            if (single.isSatisfiable())
//            {
//                if (single.hasASingleSolution())
//                {
//                System.out.println("oooohhhh yeaaaah!!!");
//                }
//                printSudoku(single.model(), out_file);
//            }
//            else {
//                System.out.println("Unsatisfiable !");
//            }
//
//        } catch (TimeoutException e) {
//            System.out.println ("Timeout, sorry !");
//        }
//    }

    //Reads a sudoku from a file, returns its matrix
    private static int[][] readFiles(String file)
    {
        int rows = 9;
        int columns = 9;
        int[][] a = new int[rows][columns];
        try
        {
            Scanner input = new Scanner (new File(file));

            for(int i = 0; i < rows; ++i)
                for(int j = 0; j < columns; ++j)
                    if(input.hasNextInt())
                        a[i][j] = input.nextInt();

            input.close();
        }
        catch (Exception e)
        {
            return null;
        }

        return  a;
    }

    public static void makeClauses(ISolver solver) throws ContradictionException{

        //Number of clauses that will be added
        int nClauses = 11988;
        solver.setExpectedNumberOfClauses(nClauses);

        /* Clause 1 : exactly one value per case */

        constraint11(solver);
        constraint12(solver);

        /* Clause 2 : exactly one of each number per row */
        constraint21(solver);
        constraint22(solver);

        /* Clause 3 : exactly one of each number per column */
        constraint31(solver);
        constraint32(solver);

        /* Clause 4 : exactly one of each number per sub-grid */
        constraint41(solver);
        constraint42A(solver);
        constraint42B(solver);

        /* The predicate S_{ijk} is represented by "ijk"
         * The predicate not(S_{ijk}) is represented by "-ijk"
         * The clause A | B is represented by "A B 0"
         * The clause A & B is represented by "A \n B 0"
         * A clause is ended by the symbol "0"
         * Each clause starts on a new line
         */

    }

    //1.1 At least one value per case
    //81 clauses
    private static void constraint11(ISolver solver) throws ContradictionException{

        for(int i = 1; i <= 9; i++)
            for(int j = 1; j <= 9; j++){
                int[] clause = new int[9];
                for(int k = 1; k <= 9; k++)
                    clause[k-1] = i*100 + j*10 + k;

                solver.addClause(new VecInt(clause));
            }

    }

    //1.2 At most one value per case
    //2916 clauses
    private static void constraint12(ISolver solver) throws ContradictionException{
        for(int i = 1; i <= 9; i++)
            for(int j = 1; j <= 9; j++)
                for(int k = 1; k <= 9; k++)
                    for(int m = k + 1; m <= 9; m++) {
                        int[] clause = new int[2];
                        clause[0] = -(i*100 + j*10 + k);
                        clause[1] = -(i*100 + j*10 + m);
                        solver.addClause(new VecInt(clause));
                    }
    }

    //2.1 At least one of each number per row
    //81 clauses
    private static void constraint21(ISolver solver) throws ContradictionException{
        for(int k = 1; k <= 9; k++)
            for(int j = 1; j <= 9; j++){
                int[] clause = new int[9];
                for(int i = 1; i <= 9; i++)
                    clause[i-1] = i*100 + j*10 + k;

                solver.addClause(new VecInt(clause));

            }
    }

    //2.2 At most one of each number per row
    //2916 clauses
    private static void constraint22(ISolver solver) throws ContradictionException{
        for (int i = 1; i <= 9; i++)
            for (int j = 1; j <= 9; j++)
                for (int k = 1; k <= 9; k++)
                    for (int m = i + 1; m <= 9; m++) {
                        int[] clause = new int[2];
                        clause[0] = -(i*100 + j*10 + k);
                        clause[1] = -(m*100 + j*10 + k);
                        solver.addClause(new VecInt(clause));
                    }

    }

    //3.1 At least one of each number per column
    //81 clauses
    private static void constraint31(ISolver solver) throws ContradictionException{

        for(int k = 1; k <= 9; k++)
            for(int i = 1; i <= 9; i++){
                int[] clause = new int[9];
                for(int j = 1; j <= 9; j++)
                    clause[j-1] = i*100 + j*10 + k;

                solver.addClause(new VecInt(clause));
            }
    }

    //3.3 At most one of each number per column
    //2916 clauses
    private static void constraint32(ISolver solver) throws ContradictionException{
        for(int i = 1; i <= 9; i++)
            for(int j = 1; j <= 9; j++)
                for(int k = 1; k <= 9; k++)
                    for(int m = j + 1; m <= 9; m++) {
                        int[] clause = new int[2];
                        clause[0] = -(i*100 + j*10 + k);
                        clause[1] = -(i*100 + m*10 + k);
                        solver.addClause(new VecInt(clause));
                    }
    }

    //4.1 At least one of each number per sub-grid
    //243 clauses
    private static void constraint41(ISolver solver) throws ContradictionException{
        for(int k = 1; k <= 9; k++)
            for(int i = 1; i <= 9; i+=3)
                for(int j = 1; j <= 9; j+=3){
                    int[] clause = new int[9];
                    for(int x = 0; x <= 2; x++)
                        for(int y = 0; y <= 2; y++)
                            clause[(3*x) + y] = (i+x)*100 + (j+y)*10 + k;

                    solver.addClause(new VecInt(clause));
                }
    }

    //4.2A At most one of each number per sub-grid (1)
    private static void constraint42A(ISolver solver) throws ContradictionException{
        for(int k = 1; k <= 9; k++)
            for(int i = 1; i <= 3; i++)
                for(int j = 1; j <= 3; j++)
                    for(int x = 0; x <= 2; x++)
                        for(int y = 0; y <= 2; y++)
                            for(int m = j + 1; m <= 3; m++) {
                                int[] clause = new int[2];
                                clause[0] = -((3*x+i)*100 + (3*y+j)*10 + k);
                                clause[1] = -((3*x+i)*100 + (3*y+m)*10 + k);
                                solver.addClause(new VecInt(clause));
                            }
    }

    //4.2B At most one of each number per sub-grid (2)
    private static void constraint42B(ISolver solver) throws ContradictionException{
        for(int k = 1; k <= 9; k++)
            for(int i = 1; i <= 3; i++)
                for(int j = 1; j <= 3; j++)
                    for(int x = 0; x <= 2; x++)
                        for(int y = 0; y <= 2; y++)
                            for(int m = i + 1; m <= 3; m++)
                                for(int l = 1; l <= 3; l++){
                                    int[] clause = new int[2];
                                    clause[0] = -((3*x+i)*100 + (3*y+j)*10 + k);
                                    clause[1] = -((3*x+m)*100 + (3*y+l)*10 + k);
                                    solver.addClause(new VecInt(clause));
                                }
    }

    //Prints the sudoku from the model to the screen
    private static void printSudoku(int[] model)
    {
        int[] matrix = filtermodel(model);
        System.out.println("-------------------------");
        for (int i = 0; i< 9; i++)
        {
            System.out.print("| ");
            for (int j = 0; j <  9; j++)
            {
                System.out.print(matrix[i * 9 + j] % 10 + " " );
                if (j%3 == 2) System.out.print("| ");
            }
            System.out.println();
            if (i%3 == 2)
                System.out.println("-------------------------");
        }
    }

    //Prints the sudoku from the model to a file
    private static void printSudoku(int[] model, String file)
    {
        try{
            PrintWriter outFile = new PrintWriter(file, "UTF-8");
            int[] matrix = filtermodel(model);
            for (int i = 0; i< 9; i++)
            {
                for (int j = 0; j <  9; j++)
                    outFile.print(matrix[i * 9 + j] % 10 +" ");

                outFile.print("\n");

            }
            outFile.close();
        }
        catch (Exception e){return;}
    }


    private static int[] filtermodel(int[] model)
    {
        int[] ret = new int[81];
        int count = 0, lastField =0;
        for(int i = 0; i< model.length; i++)
        {
            if(count <= 81 && model[i] >0)
            {
                if (lastField == model[i] / 10)
                {
                    System.out.println("There double numbers in " + lastField);
                }
                else
                {
                    lastField = model[i] / 10;
                    ret[count] = model[i];
                    count++;
                }
            }
            if(count > 81 && model[i] >0)
            {
                System.out.println("ERROR: the numbers are repeated in one cell");
                break;
            }
        }

        return ret;
    }

}
