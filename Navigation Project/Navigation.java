import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Kagan Can, Student ID: 2022400240
 * @since Date: 25.03.2024
 */

public class Navigation {
    public static void main(String[] args) throws FileNotFoundException {

        File connectionsFH = new File("city_connections.txt");
        File coordinatesFH = new File("city_coordinates.txt");
        Scanner connectionsScanner = new Scanner(connectionsFH);
        Scanner coordinatesScanner = new Scanner(coordinatesFH);
        Scanner inputScanner = new Scanner(System.in);

        ArrayList<City> cities = new ArrayList<>(); // ArrayList that holds all the cities.

        String src = "";
        String dest = "";


        // Read the city coordinates file and add each city to cities ArrayList.
        while (coordinatesScanner.hasNextLine()){

            String[] cityArray = coordinatesScanner.nextLine().split(", ");

            String cityName = cityArray[0];
            int x = Integer.parseInt(cityArray[1]);
            int y = Integer.parseInt(cityArray[2]);

            City city = new City(cityName, x, y);
            cities.add(city);
        }
        coordinatesScanner.close();


        // Read the city connections file and update each cities' connections ArrayList.
        while (connectionsScanner.hasNextLine()){

            String[] connection = connectionsScanner.nextLine().split(",");
            int firstIndex = findCity(connection[0],cities);
            int secondIndex = findCity(connection[1],cities);

            cities.get(firstIndex).connections.add(connection[1]);
            cities.get(secondIndex).connections.add(connection[0]);
        }
        connectionsScanner.close();


        // Take the source and the destination from the user
        while (src.isEmpty() || dest.isEmpty()){

            if (src.isEmpty()){

                System.out.print("Enter starting city: ");
                src = inputScanner.nextLine();

                if (findCity(src, cities) == -1){
                    System.out.println("City named " + src + " not found. Please enter a valid city name.");
                    src = "";
                }
            }

            else if (dest.isEmpty()){

                System.out.print("Enter destination city: ");
                dest = inputScanner.nextLine();

                if (findCity(dest, cities) == -1){
                    System.out.println("City named " + dest + " not found. Please enter a valid city name.");
                    dest = "";
                }
            }
        }
        inputScanner.close();


        // Calculate minimum distance and find the shortest path from source to destination.
        ArrayList<String> shortestPath = new ArrayList<String>();
        shortestPath = findShortestPath(cities, src, dest);


        // If there is no path, inform the user by a prompt.
        if (shortestPath.isEmpty()) {
            System.out.println("No path could be found.");
        }

        else {

            // Print out the distance and path.
            for (int i = 0; i < shortestPath.size(); i++){

                if (i == 0){
                    System.out.print("Path: " + shortestPath.get(i));
                }

                else {
                    System.out.print(" -> " + shortestPath.get(i));
                }
            }


            // Draw the map and arrange the window.
            int width = 2377;
            int height = 1055;
            int fontSize = 10;
            StdDraw.setCanvasSize(width/2, height/2);
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.picture(width/2.0,height/2.0, "map.png", width, height);
            StdDraw.enableDoubleBuffering();


            // Draw all the cities and connections.
            StdDraw.setPenColor(StdDraw.GRAY);
            StdDraw.setFont(new Font("Serif", Font.PLAIN, fontSize));
            for (City city : cities){

                StdDraw.text(city.x, city.y + 15, city.cityName);
                StdDraw.filledCircle(city.x, city.y, 5);
                for (String connection : city.connections){

                    int connectionIndex = findCity(connection, cities);
                    StdDraw.line(city.x, city.y, cities.get(connectionIndex).x, cities.get(connectionIndex).y);
                }
            }


            // Draw the shortest path in blue.
            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
            StdDraw.setPenRadius(0.008);
            for (int i = 0; i < shortestPath.size(); i++){

                int city1Index = findCity(shortestPath.get(i), cities);

                if (i != shortestPath.size() - 1){
                    int city2Index = findCity(shortestPath.get(i+1), cities);
                    StdDraw.line(cities.get(city1Index).x, cities.get(city1Index).y, cities.get(city2Index).x, cities.get(city2Index).y);
                }

                StdDraw.text(cities.get(city1Index).x, cities.get(city1Index).y + 15, cities.get(city1Index).cityName);
                StdDraw.filledCircle(cities.get(city1Index).x, cities.get(city1Index).y, 5);
            }

            StdDraw.show();
        }
    }


    /**
     * A function that finds the index of given city in cities ArrayList
     * @param cityName: Name of a city
     * @param cities: ArrayList of all cities
     * @return index of the given city in cities ArrayList
     */
    public static int findCity(String cityName , ArrayList<City> cities){
        int i=0;
        for(City city:cities){

            if (city.cityName.equals(cityName)){
                return i;
            }
            i++;
        }

        return -1;
    }

    /**
     * A function which finds the distance between given two cities
     * @param city1: A city name
     * @param city2: A city name
     * @param cities: ArrayList of all cities
     * @return the distance between two given cities
     */
    public static double findDistance(String city1, String city2,ArrayList<City> cities){
        int x1=0;
        int x2=0;
        int y1=0;
        int y2=0;

        for (City city:cities){

            if (city.cityName.equals(city1)){
                x1=city.x;
                y1=city.y;
            }
            else if (city.cityName.equals(city2)) {
                x2=city.x;
                y2=city.y;
            }
        }

        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**
     * A function which finds the shortest path and minimum distance from source to destination
     * @param cities: ArrayList of all cities
     * @param src: Source name
     * @param dest: Destination name
     * @return String ArrayList that holds the shortest path from source to destination
     */
    public static ArrayList<String> findShortestPath(ArrayList<City> cities, String src, String dest){

        ArrayList<String> queue = new ArrayList<>(); // A queue-like ArrayList.
        ArrayList<Double> minDistance = new ArrayList<>(); // ArrayList that holds the minimum distances from source.
        ArrayList<ArrayList<String>> shortestPaths = new ArrayList<>(); // ArrayList that holds the shortest paths from source.

        queue.add(src); // Add source to start processing
        minDistance.add(0.0); // Distance from source to itself is 0.
        ArrayList<String> temp = new ArrayList<>();
        temp.add(src);
        shortestPaths.add(temp); // Path from source to source is the source itself.

        // If source and destination are the same, print and return the values at first index of the shortestPaths and minDistance arraylists.
        if (src.equals(dest)){
            System.out.printf("Total Distance: %.2f. ", minDistance.get(0));
            return shortestPaths.get(0);
        }


        int i = 0; // Index at which the process will happen.
        int destIndex = -1; // Index of destination in ArrayLists.
        double minDist = Double.MAX_VALUE; // variable that holds the minimum distance from source to destination.

        while (i<queue.size()){
            /*
                If minimum distance from source to current city is already bigger
                than that of from source to destination city, continue. Do not process.
             */
            if (minDistance.get(i) >= minDist){
                i++;
                continue;
            }


            int processFrom = i; // Control variable to decide where to start processing the queue in the next iteration
            int cityIndex = findCity(queue.get(i), cities);
            for (String connection: cities.get(cityIndex).connections){

                double totalDist = findDistance(connection, queue.get(i), cities) + minDistance.get(i);

                // Check if our connection is in queue (visited).
                if (queue.contains(connection)){

                    // Find the index of the connection in queue.
                    int j = -1;
                    for (int k = 0; k<queue.size(); k++){
                        if (queue.get(k).equals(connection)){
                            j = k;
                            break;
                        }
                    }

                    // If we find a shorter distance to the connection, update its minimum distance and shortest path
                    if (totalDist < minDistance.get(j)){

                        minDistance.set(j, totalDist);

                        ArrayList<String> newPath = new ArrayList<>();
                        newPath.addAll(shortestPaths.get(i));
                        newPath.add(connection);
                        shortestPaths.set(j, newPath);

                        // If this connection is our destination, update minDist
                        if (connection.equals(dest)){
                            minDist = totalDist;
                        }

                        /*
                            If the connection is already processed, but we find a shorter way to the connection
                            rewind the processing. Start again from that connection after updating the ArrayLists.
                         */
                        if (i > j && (j-1 < processFrom)){
                            processFrom = j-1; // Make this j-1 so that we start from jth index after incrementing i by 1.
                        }
                    }
                }

                //If connection is not yet visited, add it in queue. Add its minimum distance and shortest path from source.
                else{
                    queue.add(connection);
                    minDistance.add(totalDist);

                    ArrayList<String> tempArray = new ArrayList<>();
                    tempArray.addAll(shortestPaths.get(i));
                    tempArray.add(connection);
                    shortestPaths.add(tempArray);

                    // If connection of our current city is the destination, hold its index and update minDist
                    if (connection.equals(dest)){
                        destIndex = queue.size()-1;
                        minDist = totalDist;
                    }
                }
            }

            i = processFrom;
            i++; // Increment i by one to process the next city in queue.
        }


        // If there is no path from source to destination, return an empty arraylist.
        if (destIndex == -1){
            ArrayList<String> emptyArrayList = new ArrayList<>();
            return emptyArrayList;
        }

        // Print out the minimum distance and return the shortest path from source to destination.
        else{
            System.out.printf("Total Distance: %.2f. ", minDistance.get(destIndex));
            return shortestPaths.get(destIndex);
        }
    }
}
