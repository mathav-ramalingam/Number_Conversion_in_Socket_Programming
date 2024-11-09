import java.io.*;
import java.net.*;

public class TCPServer {

    public static String convertNumber(String data) {
        try {
            String[] parts = data.split(" ");
            int number;

            // Determine the base from the input
            switch (parts[1].toUpperCase()) {
                case "BIN":
                    number = Integer.parseInt(parts[0], 2);
                    break;
                case "OCT":
                    number = Integer.parseInt(parts[0], 8);
                    break;
                case "DEC":
                    number = Integer.parseInt(parts[0]);
                    break;
                case "HEX":
                    number = Integer.parseInt(parts[0], 16);
                    break;
                default:
                    return "Unknown base";
            }

            String binary = Integer.toBinaryString(number);
            String octal = Integer.toOctalString(number);
            String hex = Integer.toHexString(number);

            return String.format("Binary: %s, Octal: %s, Decimal: %d, Hex: %s", binary, octal, number, hex);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("TCP Server is listening on port 5000");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String text;

                while ((text = reader.readLine()) != null) {
                    String result = convertNumber(text);
                    writer.println(result);
                }

                socket.close();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
