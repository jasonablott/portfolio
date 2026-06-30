import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

// Class to help accept HTTP requests in HTTP server. This class contains a getRequestMap method to parse http
// requests into a map for use in parsing them.
public class HTTPRequest {

    // method to get HTTP request header information into a hash map for parsing
    public static HashMap<String, String> getRequestMap(InputStream input) throws IOException {

        // create hashmap to store request parameters
        HashMap<String, String> requestParams = new HashMap<>();

        // scanner can handle data coming from the client via the socket InputStream
        Scanner scanner = new Scanner(input);

        // get lines from scanner

        // handle first line
        String requestHeader = scanner.nextLine();
        String [] requestHeaderSplit = requestHeader.split(" ");
        // get method, path, and protocol from first line
        String key1 = "method";
        String value1 = requestHeaderSplit[0];
        String key2 = "path";
        String value2 = requestHeaderSplit[1];
        String key3 = "Protocol";
        String value3 = requestHeaderSplit[2];
        // add those to hash map
        requestParams.put(key1, value1);
        requestParams.put(key2, value2);
        requestParams.put(key3, value3);

        // handle the rest of the request beyond first line
        while (scanner.hasNextLine()) {
            // need to get the key and value from each line of the request
            String requestLine = scanner.nextLine();
            // handle if line is empty, end this loop as the header is ending
            if (requestLine.isEmpty()){ break; }
            // split line on ": "
            String [] requestLineSplit = requestLine.split(": ");
            // get key
            String key = requestLineSplit[0];
            // get value
            String value = requestLineSplit[1];
            // add the key:value pair to the map
            requestParams.put(key, value);
        }
        // return the populated hash map
        return requestParams;
    }
}

