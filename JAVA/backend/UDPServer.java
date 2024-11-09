import java.net.*;

public class UDPServer {

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
        try {
            DatagramSocket socket = new DatagramSocket(5001);
            byte[] buffer = new byte[1024];

            System.out.println("UDP Server is listening on port 5001");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);

                String result = convertNumber(received);
                byte[] resultBytes = result.getBytes();

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                DatagramPacket resultPacket = new DatagramPacket(resultBytes, resultBytes.length, clientAddress, clientPort);
                socket.send(resultPacket);
            }

        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
