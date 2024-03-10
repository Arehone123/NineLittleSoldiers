import java.io.File ;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class NineLittleSoldiers
{
    static final int SIZE = 13;
    static  final int[] GOAL_STATE =
            {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0
            };


    public static void main(String[] args)
    {
        String inputFileA = "inputC.txt";
        String inputFileB = "inputD.txt";

        runBFS(inputFileA, "OutputA.txt");
        runBFS(inputFileB, "OutputB.txt");
    }


    static void runBFS(String inputFile, String outputFile)
    {
        int[] state = readState(inputFile);

        State initialState = new State(state);
        initialState.sethValue(initialState.calculateHeuristic());

        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(initialState);

        Map<State, State> parentMap = new HashMap<>();
        parentMap.put(initialState, null);

        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(outputFile);

            int moves = 0;
            State goalStateReached = null;

            while (!queue.isEmpty())
            {
                State current = queue.poll();
                moves++;

                writer.println("Soldiers to move: ");
                current.printState();

                if (current.isGoal())
                {
                    break;
                }

                if (Arrays.equals(current.getBoard(), GOAL_STATE))
                {
                    goalStateReached = current;
                    writer.println("Goal reached in " + moves + " moves.");
                    break;
                }

                if (moves >= 700)
                {
                    System.out.println("Halted: Maximum moves reached");
                    break;
                }

                List<State> neighbors = current.generateRestrictedChildren(); // Use generateRestrictedChildren
                for (State neighbor : neighbors)
                {
                    if (!parentMap.containsKey(neighbor))
                    {
                        queue.add(neighbor);
                        parentMap.put(neighbor, current);
                    }
                }
            }


            if (goalStateReached != null)
            {
                printPath(goalStateReached, parentMap);
            }

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }

        System.out.println("Output written to " + outputFile);
    }

    private static void printPath(State goalState, Map<State, State> parentMap)
    {
        List<State> path = new ArrayList<>();
        State current = goalState;

        while (current != null)
        {
            path.add(current);
            current = parentMap.get(current);
        }

        System.out.println("\nPath to the goal state:");
        for (int i = path.size() - 1; i >= 0; i--)
        {
            State state = path.get(i);
            System.out.print("step= " + (path.size() - i) + ":");
            state.printState();
        }
    }


    private static int[] readState(String filename)
    {
        int[] state = new int[SIZE];
        try (Scanner reader = new Scanner(new File(filename)))
        {
            for (int i = 0; i < SIZE; i++)
            {
                state[i] = reader.nextInt();
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return state;
    }


    public static class State implements Comparable<State>
    {
        private int hValue;
        private int[] board = new int[14];

        public State(int[] board)
        {
            this.board = Arrays.copyOf(board, SIZE);
        }

        public int gethValue()
        {
            return hValue;
        }

        public void sethValue(int hValue)
        {
            this.hValue = hValue;
        }

        public int[] getBoard()
        {
            return board;
        }

        public void setBoard(int[] board)
        {
            this.board = Arrays.copyOf(board, SIZE);
        }

        public boolean isGoal()
        {
            for (int i = 0; i < SIZE; i++)
            {
                if (board[i] != (i + 1))
                {
                    return false;
                }
            }
            return true;
        }

        public void performMove(int from, int to)
        {
            if (isValidMove(from, to)) {
                board[to] = board[from];
                board[from] = 0;
                hValue = calculateHeuristic();
            }
        }

        private boolean isValidMove(int from, int to)
        {
            return board[from] != 0 && board[to] == 0;
        }

        public int calculateHeuristic()
        {
           this.hValue = 0;
           for (int i = 1 ; i < 10 ; i++){
               if (this.board[i] != i){
                   this.hValue++;
               }
           }
           return hValue;
        }

        public List<State> generateRestrictedChildren()
        {
            List<State> children = new ArrayList<>();

            for (int i = 0; i < SIZE; i++)
            {
                if (board[i] == 1 || board[i] == 2 ||board[i] == 3 || board[i] == 4 ||board[i] == 5 || board[i] == 6 ||board[i] == 7 || board[i] == 8 ||board[i] == 9 )
                {
                    for (int j = 0; j < i; j++)
                    {
                        if (isValidMove(i, j))
                        {
                            State child = new State(board);
                            child.performMove(i, j);
                            children.add(child);
                        }
                    }
                }
            }
            return children;
        }

        private int findSoldierPosition(int soldierNumber)
        {
            for (int i = 0; i < SIZE; i++)
            {
                if (board[i] == soldierNumber)
                {
                    return i;
                }
            }
            return -1;
        }

        private int findGoalPosition(int soldierNumber)
        {
            for (int i = 0; i < SIZE; i++)
            {
                if (board[i] == soldierNumber)
                {
                    return i;
                }
            }
            return -1;
        }

        public List<State> generateChildren()
        {
            List<State> children = new ArrayList<>();

            for (int i = 0; i < SIZE; i++)
            {
                if (board[i] != 0)
                {
                    for (int j = 0; j < SIZE; j++)
                    {
                        if (isValidMove(i, j))
                        {
                            State child = new State(board);
                            child.performMove(i, j);
                            children.add(child);
                        }
                    }
                }
            }
            return children;
        }


        public void printState()
        {
            System.out.println(Arrays.toString(board) + " H=" + hValue);
        }


        @Override
        public int compareTo(State o)
        {
            return Integer.compare(this.hValue, o.hValue);
        }
    }
}
