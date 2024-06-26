import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

import org.sat4j.specs.TimeoutException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ContradictionException, TimeoutException {
        if (args.length < 2) {
            System.out.println("Too few arguments to run. Please provide 'data.in' and 'data.out'.");
            
        } else if (args.length == 2) {
            System.out.println("Solving a schedule class problem.");

            String input = args[0];
            String output = args[1];

            System.out.format("Here we got two files..(%s)!\n", input);

            solveSchedule(input, output);

        } else {
            System.out.println("Too many arguments to run. Please provide at most an input and an output file.");
            return;
        }
        System.out.println("\nFinished execution.");
        //return;
    }

    private static void solveSchedule(String input, String output) throws TimeoutException, FileNotFoundException, UnsupportedEncodingException, ContradictionException {

        //Create the solver with a 1h timeout
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600);

        //Add the data from file to the Solver
        addingFileInfo(input, solver);


        IProblem problem = solver;

        if (problem.isSatisfiable())
        {
            System.out.println("\n\tSatisfiable Schedule.");
            printSchedule(problem.model());
            printSchedule(problem.model(), output);

        } else {
            System.out.println("Unsatisfiable Schedule.");
        }
    }

    private static void printSchedule(int[] model, String file) {
        try {
            PrintWriter outFile = new PrintWriter(file, "UTF-8");
            for (int i = 0; i < model.length; i++) {
                if (model[i]>0){

                    outFile.print(String.valueOf(model[i]).
                            replace("", " ").trim());

                outFile.print("\n");}
            }
            outFile.close();
        } catch (Exception e) {
            return;
        }

    }

    private static void printSchedule(int[] model) {
        System.out.println("-------------------------------------");
        for (int i = 0; i < model.length; i++) {
            if (model[i]>0)
                System.out.print(" " + model[i]);

        }
    }

    private static void addingFileInfo(String input, ISolver solver) throws ContradictionException {

        int[][] a = readFiles(input);

        //Add the clauses
        makeClauses(solver, a);

    }

    private static void makeClauses(ISolver solver, int[][] a) throws ContradictionException {

        int nbCourses, nbTeachers, nbClRooms, nbTimeSlots;

        nbCourses = a[0][0]; nbTeachers = a[0][1];
        nbClRooms = a[0][2]; nbTimeSlots = a[0][3];
        System.out.format("nbs %d, %d, %d, %d \n", nbCourses, nbTeachers, nbClRooms, nbTimeSlots);

        int[] crsTeachers = new int[nbCourses];
        for (int i = 1; i <= nbCourses; i++) {
            crsTeachers[i-1] = 10 * i + a[1][i - 1];
            System.out.format("crsTeachers: %d - %d = %d \n",i, a[1][i - 1], 100*(10 * i + a[1][i - 1]));
        }

        constraint1(crsTeachers, nbClRooms, nbTimeSlots, solver);
        constraint2(crsTeachers, nbClRooms, nbTimeSlots, solver);
//        constraint3(crsTeachers, nbClRooms, nbTimeSlots, solver); //UnSatisfiable Schedule.
        constraint4(crsTeachers, nbClRooms, nbTimeSlots, solver);


    }

    private static void constraint4(int[] both, int nbClRooms, int nbTimeSlots, ISolver solver) throws ContradictionException {


        for (int i = 0; i < both.length; i++) {
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    for (int l = 0; l < both.length; l++) {
                        for (int m = 1; m <= nbClRooms; m++) {
                            for (int n = 1; n <= nbTimeSlots ; n++) {

                                int[] clause = new int[2];
                                if((10*j + k) == (10*m + n) && test1 != test2) //  1123 2222 3121 4213 5312 6211
                                {
                                    clause[0] = -test1;
                                    clause[1] = -test2;

                                    solver.addClause(new VecInt(clause));
                                }

                            }
                        }
                    }
                }
        }
    }

    private static void constraint3(int[] both, int nbClRooms, int nbTimeSlots, ISolver solver) throws ContradictionException {

        for (int i = 0; i < both.length; i++) {
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    for (int x = 0; x < both.length; x++) {
                        for (int l = j + 1; l <= nbClRooms; l++)
                            for (int m = k; m <= nbTimeSlots; m++) {

                                if (m == k)
                                {
                                    int[] clause = new int[2];
                                    clause[0] = -(100 * both[i] + 10 * j + k);
                                    clause[1] = -(100 * both[x] + 10 * l + m);

                                    solver.addClause(new VecInt(clause));
                                }
                            }
                    }
                }
        }


    }

    private static void constraint2(int[] both, int nbClRooms, int nbTimeSlots, ISolver solver) throws ContradictionException {
        for (int i = 0; i < both.length; i++) {

            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    int test1 = 100 * both[i] + 10 * j + k;
                    for (int l = 1; l <= nbClRooms ; l++)
                        for (int m = k+1; m <= nbTimeSlots; m++) {
                            int test2 = 100 * both[i] + 10 * l + m;
                            int[] clause = new int[2];
                            clause[0] = -(100 * both[i] + 10 * j + k);
                            clause[1] = -(100 * both[i] + 10 * l + m);
                            solver.addClause(new VecInt(clause));
                        }

                }
        }

    }

    private static void constraint1(int[] both, int nbClRooms, int nbTimeSlots, ISolver solver) throws ContradictionException {


        for (int i = 0; i < both.length; i++) {
            int[] clause = new int[nbClRooms * nbTimeSlots]; int idx = 0;
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    clause[idx++] = 100 * both[i] + 10 * j + k;
                }
            solver.addClause(new VecInt(clause));
        }

    }

    private static int[][] readFiles(String file) {

//        the number of classes C,
//        the number of teachers T,
//        the number of rooms R,
//        the number of time-slots S.
//
//        The second line will contain C integers (T_1,..., T_C), between 1 and T,
//        indicating that Ti teaches course i.
//        The output file will contain C rows, each containing four columns with the course, the teacher,
//        the room and the timeslot.

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
