import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Kagan Can, Student ID: 2022400240
 * @since Date: 05.05.2024
 */

public class MigrosDelivery {
    public static void main(String[] args) throws FileNotFoundException {

        int N = 100; // Number of iterations
        int M = 50; // Ant count per iteration
        double degradationFactor = 0.9;
        double alpha = 0.97;
        double beta = 2.8;
        double initialPheromoneIntensity = 0.1;
        double Q = 0.0001;
        double circleRadius = 0.02;

        int chosenMethod = 2; // 1 for brute force algorithm and 2 for ant colony optimization algorithm
        boolean pathOrMap = true; // true if we want to display the shortest path; false for displaying the intensity map

        int caseNumber = 5; // Number between 1 and 5, which corresponds to selected input file.
        String path = "input0" + caseNumber + ".txt";


        // Start
        File fh = new File(path);
        Scanner scanner = new Scanner(fh);
        ArrayList<House> houses = new ArrayList<>();


        // Take inputs and construct houses
        while (scanner.hasNextLine()) {

            String[] coords = scanner.nextLine().split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            houses.add(new House(x, y));

        }


        // Calculate the minimum distance and draw the path (or intensity map) using chosen method
        if (chosenMethod == 1){
            BruteForce(houses);
        }

        else if (chosenMethod == 2) {
            AntColonyOptimization(N, M, houses, alpha, beta, Q, degradationFactor, initialPheromoneIntensity, pathOrMap);
        }


        // Draw the migros and houses
        boolean migros = true;
        for (House house : houses){

            if (migros && pathOrMap) {
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                migros = false;
            }
            else{
                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            }

            StdDraw.filledCircle(house.x, house.y, circleRadius);

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(house.x, house.y, String.format("%d", house.houseNum));

        }

        StdDraw.show();
    }

    /**
     * This method finds the shortest path and minimum distance to travel through all the nodes and return back to starting node
     * It uses a helper method for recursion.
     *
     * @param houses: ArrayList that holds house objects
     */

    public static void BruteForce(ArrayList<House> houses){

        double startTime = System.currentTimeMillis();

        int houseCount = House.houseCount;
        double[][] distances = calculateDistances(houses);
        ArrayList<Integer> unvisited = new ArrayList<Integer>();
        ArrayList<Integer> shortestPath = new ArrayList<Integer>();
        ArrayList<Integer> currentPath = new ArrayList<Integer>();
        ArrayList<Double> minDistance = new ArrayList<Double>();

        minDistance.add(Double.MAX_VALUE);
        currentPath.add(0);

        for (int i = 1; i < houseCount; i++) {
            unvisited.add(i);
        }

        BruteForceHelper(distances, unvisited, shortestPath, minDistance, 0, currentPath);


        // Re-arrange the path to make it start from migros
        for(int i = 0; i < shortestPath.size(); i++) {

            int elem = shortestPath.get(i);
            elem++;
            shortestPath.remove(i);
            shortestPath.add(i, elem);

        }

        double elapsedTime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("Method: Brute-Force Method");
        System.out.println("Shortest Distance: " + minDistance.get(0));
        System.out.println("Shortest Path: " + shortestPath);
        System.out.println("Time it takes to find the shortest path: " + elapsedTime + " seconds.");


        // Draw the shortest path
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.004);
        for (int i = 0; i < shortestPath.size() - 1; i++){

            int firstIndex = shortestPath.get(i) - 1;
            int secondIndex = shortestPath.get(i + 1) - 1;

            StdDraw.line(houses.get(firstIndex).x, houses.get(firstIndex).y, houses.get(secondIndex).x, houses.get(secondIndex).y);

        }

    }

    /**
     *
     * A method to calculate distances between each house object.
     *
     * @param houses: ArrayList that holds house objects
     * @return Returns a 2D array of distances between each house object. [i][j] is the distance between 'i+1'th house object and 'j+1'th house object
     */

    public static double[][] calculateDistances(ArrayList<House> houses) {

        int houseCount = House.houseCount;
        double[][] distances = new double[houseCount][houseCount];

        for (int i = 0; i < houseCount; i++){
            for(int j = i; j < houseCount; j++){

                if (j == i){
                    distances[i][j] = Double.MAX_VALUE;
                }

                else {
                    double xdifference = Math.pow(houses.get(i).x - houses.get(j).x, 2);
                    double ydifference = Math.pow(houses.get(i).y - houses.get(j).y, 2);
                    double distance = Math.sqrt(xdifference + ydifference);

                    distances[i][j] = distance;
                    distances[j][i] = distance;
                }

            }
        }

        return distances;
    }

    /**
     *
     * A recursive helper method that uses BruteForce approach to calculate minDistance and shortestPath.
     *
     * @param distances: 2D array of distances between each house object
     * @param unvisited: ArrayList that holds the unvisited nodes
     * @param shortestPath: ArrayList that holds the shortest path to travel through all the nodes.
     * @param minDistance: ArrayList of only one element which is the total distance of the shortest path
     * @param currentDistance: ArrayList of only one element, which is the total distance of the current path
     * @param currentPath: ArrayList that holds the visited nodes respectively
     */

    private static void BruteForceHelper(double[][] distances, ArrayList<Integer> unvisited, ArrayList<Integer> shortestPath, ArrayList<Double> minDistance, double currentDistance, ArrayList<Integer> currentPath){

        // If current distance is already bigger than minimum distance, no longer continue.
        if (currentDistance >= minDistance.get(0)){
            return;
        }

        // If all the nodes are visited, finalize the path and update minDistance if currentDistance is smaller.
        if (unvisited.isEmpty()) {

            currentDistance += distances[currentPath.get(currentPath.size() - 1)][0];
            currentPath.add(0);

            if (currentDistance <= minDistance.get(0)){
                minDistance.remove(0);
                minDistance.add(currentDistance);
                shortestPath.clear();
                shortestPath.addAll(currentPath);
            }

            currentPath.remove(currentPath.size() - 1); // Backtrack
            return;
        }


        for (int i = 0; i < unvisited.size(); i++){

            int from = currentPath.get(currentPath.size() - 1);
            int to = unvisited.get(i);

            // Visit the node
            unvisited.remove(i);
            currentDistance += distances[from][to];
            currentPath.add(to);

            BruteForceHelper(distances, unvisited, shortestPath, minDistance, currentDistance, currentPath);

            // Backtrack
            currentDistance -= distances[from][to];
            unvisited.add(i, to);
            currentPath.remove(currentPath.size() - 1);

        }
    }

    /**
     *
     * A heuristic algorithm that finds and displays the minimum distance and shortest possible path it can find.
     * It can also display the pheromone intensities after finding the shortest path.
     *
     * @param N: Number of iteration (Learning Rate)
     * @param M: Ant per iteration
     * @param alpha: Hyper parameter which affects the weight of pheromone intensities during edgeValue calculation
     * @param beta: Hyper parameter which affects the weight of distance between each node during edgeValue calculation
     * @param Q: Hyper parameter for increasing pheromone intensity
     * @param degradationFactor: Multiplication variable that will be used to simulate remaining pheromone intensities after evaporation
     * @param initialPheromoneIntensity: Initial pheromone intensity
     * @param houses: ArrayList that holds the house objects.
     * @param pathOrMap: boolean to choose what you want to be displayed
     */

    public static void AntColonyOptimization(int N, int M, ArrayList<House> houses, double alpha, double beta, double Q, double degradationFactor, double initialPheromoneIntensity, boolean pathOrMap){

        double startTime = System.currentTimeMillis();

        int houseCount = House.houseCount;
        double[][] distances = calculateDistances(houses);


        double[][] pheromoneIntensities = new double[houseCount][houseCount];
        double[][] edgeValues = new double[houseCount][houseCount];

        for (int i = 0; i < houseCount; i++){
            for (int j = 0; j < houseCount; j++){
                pheromoneIntensities[i][j] = initialPheromoneIntensity;
                edgeValues[i][j] = (Math.pow(pheromoneIntensities[i][j], alpha)) / (Math.pow(distances[i][j], beta));
            }
        }


        double minDistance = Double.MAX_VALUE;
        ArrayList<Integer> bestAntTraversal = new ArrayList<Integer>();

        for (int i = 0; i < N; i++){
            for (int j = 0; j < M; j++){

                // Create an ant instance
                Ant ant = new Ant(houseCount);
                int startingNode = ant.randomStartingNode;

                double currentDistance = 0;
                ArrayList<Integer> currentPath = new ArrayList<Integer>();
                currentPath.add(startingNode);

                // Create an arraylist that holds the unvisited nodes
                ArrayList<Integer> unvisited = new ArrayList<Integer>();
                for (int k = 0; k < houseCount; k++){
                    if (k != startingNode){
                        unvisited.add(k);
                    }
                }


                // Calculate the probability for the ant to go to each node
                ArrayList<Double> probabilities = new ArrayList<Double>();
                while (!unvisited.isEmpty()){

                    int from = currentPath.get(currentPath.size() - 1); // Variable that holds where the ant currently is

                    // Calculate the total edge value of nodes that our ant can go next.
                    double totalEdgeValue = 0;
                    for(int k = 0; k < houseCount; k++){

                        if (unvisited.contains(k)){
                            totalEdgeValue += edgeValues[from][k];
                        }

                    }

                    // Calculate the probabilities of each node that our ant can go next.
                    for(int k = 0; k < unvisited.size(); k++){

                        int node = unvisited.get(k);
                        double probability = edgeValues[from][node] / totalEdgeValue;
                        probabilities.add(probability);

                    }


                    // Go to another node randomly.
                    Random rand = new Random();
                    double randomDouble = rand.nextDouble();
                    double tracer = 0;
                    int k = 0;

                    while(true){

                        if(tracer+probabilities.get(k) >= randomDouble){

                            int to = unvisited.get(k);
                            unvisited.remove(k);

                            currentDistance += distances[from][to];
                            currentPath.add(to);
                            probabilities.clear();

                            break;
                        }

                        tracer += probabilities.get(k);
                        k++;
                    }

                }


                // Finalize the cycle. Update pheromone intensities and edge values
                currentDistance += distances[currentPath.get(currentPath.size() - 1)][startingNode];
                currentPath.add(startingNode);

                double delta = Q/currentDistance;
                for(int k = 0; k < currentPath.size() - 1; k++){

                    int firstNode = currentPath.get(k);
                    int secondNode = currentPath.get(k+1);

                    pheromoneIntensities[firstNode][secondNode] += delta;
                    pheromoneIntensities[secondNode][firstNode] += delta;
                    edgeValues[firstNode][secondNode] = (Math.pow(pheromoneIntensities[firstNode][secondNode], alpha)) / (Math.pow(distances[firstNode][secondNode], beta));
                    edgeValues[secondNode][firstNode] = (Math.pow(pheromoneIntensities[secondNode][firstNode], alpha)) / (Math.pow(distances[secondNode][firstNode], beta));

                }

                // If this ant found a faster way to travel through all the nodes, update the minDistance and bestAntTraversal.
                if (currentDistance < minDistance){
                    minDistance = currentDistance;
                    bestAntTraversal.clear();
                    bestAntTraversal.addAll(currentPath);
                }

            }

            // Evaporate the pheromones after each iteration
            for (int j = 0; j < houseCount; j++) {
                for (int k = 0; k < houseCount; k++){
                    pheromoneIntensities[j][k] *= degradationFactor;
                }
            }

        }


        // re-arrange the path to make it start from migros.
        ArrayList<Integer> shortestPath = new ArrayList<Integer>();
        int migrosIndex = bestAntTraversal.indexOf(0);

        int j = migrosIndex;
        for (int i = 0; i < bestAntTraversal.size(); i++) {

            if (j == bestAntTraversal.size()) {
                j = 1;
            }

            shortestPath.add(bestAntTraversal.get(j) + 1);
            j++;
        }


        double elapsedTime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("Method: Ant Colony Method");
        System.out.println("Shortest Distance: " + minDistance);
        System.out.println("Shortest Path: " + shortestPath);
        System.out.println("Time it takes to find the shortest path: " + elapsedTime + " seconds.");


        // Draw either the shortest path or the pheromone intensities map
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        if (pathOrMap) {
            // draw the path
            StdDraw.setPenRadius(0.004);
            StdDraw.setPenColor(StdDraw.BLACK);
            for (int i = 0; i < shortestPath.size() - 1; i++) {

                int firstIndex = shortestPath.get(i) - 1;
                int secondIndex = shortestPath.get(i + 1) - 1;

                StdDraw.line(houses.get(firstIndex).x, houses.get(firstIndex).y, houses.get(secondIndex).x, houses.get(secondIndex).y);

            }
        }

        else{
            // draw the intensity map
            StdDraw.setPenColor(StdDraw.BLACK);
            for(int i = 0; i < houseCount; i++) {
                for (int k = i+1; k < houseCount; k++) {

                    int converted = (int) (1000.0 * pheromoneIntensities[i][k]);
                    double penRadius = ((double) converted) * 0.001;
                    StdDraw.setPenRadius(penRadius);

                    StdDraw.line(houses.get(i).x, houses.get(i).y, houses.get(k).x, houses.get(k).y);
                }
            }

        }

    }
}
