import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Illumio {

    private static final Logger logger = Logger.getLogger(Illumio.class.getName());
    private static final Map<String, String> protocols = new HashMap<>();

    // Constants for default values
    private static final String UNKNOWN_PROTOCOL = "unknown";
    private static final String UNTAGGED = "Untagged";

    static {
        protocols.put("1", "icmp");
        protocols.put("6", "tcp");
        protocols.put("17", "udp");
        protocols.put("41", "ipv6");
        protocols.put("50", "esp");
        protocols.put("58", "ipv6-icmp");
        protocols.put("81", "vmtp");
        protocols.put("89", "ospfigp");
        protocols.put("91", "larp");
    }

    /*
        This function processes data in multiple steps:

        Step 1: Firt lookup file is read and the tags are stored into a map. The dst port and protocol are combined with a delimiter '_' and used as key. 
        The tag is out Value.
        Step 2: Now the log file is read, assuming that the 6th and 7th indexes contain dstport and protocol respectively(starting from 0th index).
         We concatenate dstport and protocol with  delimiter '_'. And use this key to search our lookup table. 
         We create out result maps in step too. One for the tag counts and on for the dstport and protocol combinations.
        Step 3: Now we write our output maps to a file called 'output.txt'

        Some notes:
        - We have created a static map called 'Protocols' with most used protocols and their numbers.
        - In step 1, while reading lookup table we ignore the first line as its for the headers in csv fomrat file.
        - We assume log file is a text file, with no commas or headers.
        - We are writing both the outputs to same file, as understood form the assignment.
    */

    public static void main(String[] args) {
        String lookupPath = "/Users/amithaattapu/Desktop/lookup.txt";
        String logsPath = "/Users/amithaattapu/Desktop/logs.txt";
        String outputFilePath = "/Users/amithaattapu/Desktop/output.txt";

        Map<String, String> lookupTable = new HashMap<>();
        Map<String, Integer> tagCounts = new HashMap<>();
        Map<String, Integer> portProtocolComboCounts = new HashMap<>();

        // STEP 1 - Process Lookup File
        processLookupFile(lookupPath, lookupTable);

        // STEP 2 - Process Log File
        processLogFile(logsPath, lookupTable, tagCounts, portProtocolComboCounts);

        // STEP 3 - Write Output to File
        writeOutputToFile(outputFilePath, tagCounts, portProtocolComboCounts);
    }

    // Function to process the lookup file
    private static void processLookupFile(String lookupPath, Map<String, String> lookupTable) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(lookupPath))) {
            String line;
            reader.readLine();  // Skip the header line
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Split line by commas and handle any malformed lines
                String[] lineStrings = line.split(",");
                if (lineStrings.length < 3) {
                    logger.warning("Skipping malformed line in lookup file: " + line);
                    continue; // Skip lines with insufficient data
                }

                // Store the lookup table entry, combining dst port and protocol as the key
                lookupTable.put(lineStrings[0] + "_" + lineStrings[1], lineStrings[2]);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading lookup file at path: " + lookupPath, e);
            System.exit(1);
        }
    }

    // Function to process the log file
    private static void processLogFile(String logsPath, Map<String, String> lookupTable, 
                                       Map<String, Integer> tagCounts, Map<String, Integer> portProtocolComboCounts) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(logsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Split line by spaces, handle malformed lines
                String[] lineStrings = line.split(" ");
                if (lineStrings.length < 8) {  // Check if there are enough fields
                    logger.warning("Skipping malformed line in logs file: " + line);
                    continue;
                }

                // Extract dstPort and protocol from the log line (6th and 7th fields)
                String dstPort = lineStrings[6];
                String protocolCode = lineStrings[7];

                // Retrieve the protocol name, handle missing protocols (default to "unknown")
                String protocol = protocols.getOrDefault(protocolCode, UNKNOWN_PROTOCOL);

                // Create key for the lookup table (dstport_protocol)
                String lookupKey = dstPort + "_" + protocol;

                // Retrieve tag from lookup table, default to "Untagged" if not found
                String tag = lookupTable.getOrDefault(lookupKey, UNTAGGED);

                // Count occurrences of tags and port/protocol combinations
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                portProtocolComboCounts.put(lookupKey, portProtocolComboCounts.getOrDefault(lookupKey, 0) + 1);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading logs file at path: " + logsPath, e);
            System.exit(1);
        }
    }

    // Function to write the output to a file
    private static void writeOutputToFile(String outputFilePath, 
                                          Map<String, Integer> tagCounts, 
                                          Map<String, Integer> portProtocolComboCounts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("Tag Counts:\n");
            writer.write("Tag,Count\n");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }

            writer.write("\nPort/Protocol Combination Counts:\n");
            writer.write("Port,Protocol,Count\n");
            for (Map.Entry<String, Integer> entry : portProtocolComboCounts.entrySet()) {
                String[] entrySplit = entry.getKey().split("_");
                writer.write(entrySplit[0] + "," + entrySplit[1] + "," + entry.getValue() + "\n");
            }

            System.out.println("Outputs written to " + outputFilePath);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to file at path: " + outputFilePath, e);
            System.exit(1);
        }
    }
}
