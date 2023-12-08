import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Scanner;

public class test {

    static BigDecimal[] x;
    static BigDecimal[] y;

    public static void main(String[] args) {
        
        loadData("re_arrenge.txt");

        BigDecimal totalDistance = BigDecimal.ZERO;

        for (int i = 0; i < 29; i++) {
            int currentCityIndex = i;
            int nextCityIndex = i + 1;
            BigDecimal distance = euclideanDistance(x[currentCityIndex], x[nextCityIndex], y[currentCityIndex], y[nextCityIndex]);
            totalDistance = totalDistance.add(distance);
            System.out.println("Distance from city " + currentCityIndex + " to city " + nextCityIndex + ": " + distance);
        }

        // Closing the loop by connecting the last city to the first city
       
       
        System.out.println("Distance from last city to first city: " + totalDistance);

        System.out.println("Total distance: " + totalDistance);
    }

    public static void loadData(String filePath) {
        try {
            Scanner scanner = new Scanner(new File(filePath));

            int dataSize = 30; // Assuming there are 30 data points based on the provided sample

            x = new BigDecimal[dataSize];
            y = new BigDecimal[dataSize];

            for (int i = 0; i < dataSize; i++) {
                x[i] = scanner.nextBigDecimal();
                y[i] = scanner.nextBigDecimal();
            }

            scanner.close();

            // Displaying loaded data for verification
            for (int i = 0; i < dataSize; i++) {
                System.out.println("x[" + i + "] = " + x[i] + ", y[" + i + "] = " + y[i]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal euclideanDistance(BigDecimal x1, BigDecimal x2, BigDecimal y1, BigDecimal y2) {
        BigDecimal xDiff = x1.subtract(x2);
        BigDecimal yDiff = y1.subtract(y2);
        BigDecimal xSquared = xDiff.pow(2);
        BigDecimal ySquared = yDiff.pow(2);
        return BigDecimal.valueOf(Math.sqrt(xSquared.add(ySquared).doubleValue()));
    }
}
