import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// Define a logger class
public class Logger {
    private static BufferedWriter writer;

    public static void initLogger(String fileName) {
        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));  // Set true for append mode
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        System.out.println(message); // Print to console
        try {
            if (writer != null) {
                writer.write(message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
