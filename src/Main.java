import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;

import java.io.File;
import java.io.FileNotFoundException;
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
            //return;
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

        } else {
            System.out.println("Unsatisfiable Schedule.");
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




        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();

        }

//        for (int i = 1; i <= 9; i++)
//            for (int j = 1; j <= 9; j++) {
//                int[] clause = new int[9];
//                for (int k = 1; k <= 9; k++) {
//                    clause[k - 1] = i * 100 + j * 10 + k;
//                    System.out.print((i * 100 + j * 10 + k) + " ");
//                }
//
//
//            }
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
//        //Number of clauses that will be added
//        int nbClauses = 0;
//        solver.setExpectedNumberOfClauses(nbClauses);

        constraint1(crsTeachers, nbClRooms, nbTimeSlots, solver);
//        constraint2(crsTeachers, nbClRooms, nbTimeSlots, solver);
//        constraint3(crsTeachers, nbClRooms, nbTimeSlots, solver);
        constraint4(crsTeachers, nbClRooms, nbTimeSlots, solver);


    }

    private static void constraint4(int[] both, int nbClRooms, int nbTimeSlots, ISolver solver) throws ContradictionException {

//        int[] clause2 = new int[2];
//        clause2[0] = -2211;
//        clause2[1] = -3111;
//        solver.addClause(new VecInt(clause2));

        for (int i = 0; i < both.length; i++) {
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
//                    int x = both.length - i -1;
                        int test1 = 100 * both[i] + 10 * j + k;
                    for (int l = 0; l < both.length; l++) {
                        for (int m = 1; m <= nbClRooms; m++) {
                            for (int n = 1; n <= nbTimeSlots ; n++) {

                                int test2 = 100 * both[l] + 10 * m + n;

                                int[] clause = new int[2];
//                                System.out.format("(%d <-> %d)\n",
//                                        test1, test2);
//                                if(j == m && test1 != test2)
                                if((10*j + k)== (10*m + n) && test1 != test2)
                                {
//                                    System.out.format("YES j(%d) - m(%d)\n",j,m);
//                                    System.out.format("(%d <-> %d)\n",
//                                        -test1, -test2);
                                    clause[0] = -test1;
                                    clause[1] = -test2;

                                    solver.addClause(new VecInt(clause));
                                }
                                else if ( k == n && test1 != test2 )
                                {
//                                    System.out.println("YES k - n");
//                                    System.out.format("(%d <-> %d)\n",
//                                            test1, test2);
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
        System.out.print(" \n\n");
//        for (int k = 1; k <= 9; k++) {
//            for (int j = 1; j <= 9; j++) {
//                int[] clause = new int[9];
//                for (int i = 1; i <= 9; i++)
//                    //clause[i - 1] = i * 100 + j * 10 + k;
//                    System.out.print(" " + (i * 100 + j * 10 + k));
//            } System.out.print(" ;\n");
//        }

        for (int i = 0; i < both.length; i++) {
//            int[] clause = new int[nbClRooms * nbTimeSlots];
            int idx = 0;
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    int idx2 = 0;


                    for (int x = 0; x < both.length; x++) {
                        for (int l = j + 1; l <= nbClRooms; l++)
                            for (int m = k; m <= nbTimeSlots; m++) {

                                if (m == k)
                                {
                                    int[] clause = new int[2];
//                                    System.out.format("(%d <-> %d)\n",
//                                            //(-(i * 100 + j * 10 + k)),
//                                            (-(100 * both[i] + 10 * j + k)),
//                                            (-(100 * both[x] + 10 * l + m)));
//                                    (-(i * 100 + j * 10 + m)));

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
//        for (int i = 1; i <= 9; i++)
//            for (int j = 1; j <= 9; j++)
//                for (int k = 1; k <= 9; k++)
//                    for (int m = k + 1; m <= 9; m++) {
//                        int[] clause = new int[2];
//                        clause[0] = -(i * 100 + j * 10 + k);
//                        clause[1] = -(i * 100 + j * 10 + m);
//                        System.out.format("(%d <-> %d)\n",(-(i * 100 + j * 10 + k)),(-(i * 100 + j * 10 + m)));
//                        //solver.addClause(new VecInt(clause));
//                    }

        for (int i = 0; i < both.length; i++) {
//            int[] clause = new int[nbClRooms * nbTimeSlots];
            int idx = 0;
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    int idx2 = 0;

//                    for (int x = 0; x < both.length; x++)
                    {
                        for (int l = j; l <= nbClRooms; l++)
                            for (int m = k + 1; m <= nbTimeSlots; m++) {

//                                if (j == l)
                                {
                                    int[] clause = new int[2];
                            System.out.format("(%d <-> %d)\n",
                                    //(-(i * 100 + j * 10 + k)),
                                    (-(100 * both[i] + 10 * j + k)),
                                    (-(100 * both[i] + 10 * l + m)));
//                                    (-(i * 100 + j * 10 + m)));

                                    clause[0] = -(100 * both[i] + 10 * j + k);
                                    clause[1] = -(100 * both[i] + 10 * l + m);

                                    solver.addClause(new VecInt(clause));

                                }
                            }
                    }
                }
        }
    }

    private static void constraint1(int[] both, int nbClRooms, int nbTimeSlots, ISolver solver) throws ContradictionException {

//        for (int i = 1; i <= 9; i++){
//            for (int j = 1; j <= 9; j++) {
//                int[] clause = new int[9];
//                for (int k = 1; k <= 9; k++)
////                    clause[k - 1] = i * 100 + j * 10 + k;
//                    System.out.print(" "+(i * 100 + j * 10 + k));
////                solver.addClause(new VecInt(clause));
//            }System.out.print(" ;\n");
//        }

        for (int i = 0; i < both.length; i++) {
            int[] clause = new int[nbClRooms * nbTimeSlots]; int idx = 0;
            for (int j = 1; j <= nbClRooms; j++)
                for (int k = 1; k <= nbTimeSlots; k++) {
                    System.out.format("-> %d  -- %d\n", (100 * both[i] + 10 * j + k), (idx));
                    clause[idx++] = 100 * both[i] + 10 * j + k;
                }
//            System.out.println(" "+clause.length);
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
