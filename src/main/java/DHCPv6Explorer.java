import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class DHCPv6Explorer {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private String multicastAdressPrefix = "FF32:00FF:";

    /*
    public DHCPv6Explorer() throws SocketException {
        socket = new DatagramSocket(4445);
    }
    */

    public static void main(String[] args) {
        try {
            // showNetwork();
            // fe80:0:0:0:1c8e:5929:a87b:9111%en0 with Prefix-Length 64
            var socket = new DatagramSocket();
            var buf = hexStringtoByteArray(
                    "600f07f300083afffe80000000000000" +
                            "1c8e5929a87b9111ff02000000000000" +
                            "00000000000000028500cdf200000000"
            );
            InetAddress address = Inet6Address.getByName("ff02::2");
            var packet = new DatagramPacket(buf, buf.length, address, 0);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, packet.getLength());

            if (received.equals("end")) {
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        socket.close();
    }


    private static byte[] hexStringtoByteArray(String hex) {
        /* Konvertiere den String mit Hex-Ziffern in ein Byte-Array */
        byte[] val = new byte[hex.length() / 2];
        for (int i = 0; i < val.length; i++) {
            int index = i * 2;
            int num = Integer.parseInt(hex.substring(index, index + 2), 16);
            val[i] = (byte) num;
        }
        return val;
    }

    private static String byteArraytoHexString(byte[] byteArray) {
        /* Konvertiere das Byte-Array in einen String mit Hex-Ziffern */
        String hex = "";
        if (byteArray != null) {
            for (int i = 0; i < byteArray.length; ++i) {
                hex = hex + String.format("%02X", byteArray[i]);
            }
        }
        return hex;
    }

    private static void showNetwork() throws SocketException {
        /* Netzwerk-Infos fuer alle Interfaces ausgeben */
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface ni = en.nextElement();
            System.out.println("\nDisplay Name = " + ni.getDisplayName());
            System.out.println(" Name = " + ni.getName());
            System.out.println(" Scope ID (Interface ID) = " + ni.getIndex());
            System.out.println(" Hardware (LAN) Address = " + byteArraytoHexString(ni.getHardwareAddress()));

            List<InterfaceAddress> list = ni.getInterfaceAddresses();
            Iterator<InterfaceAddress> it = list.iterator();

            while (it.hasNext()) {
                InterfaceAddress ia = it.next();
                System.out
                        .println(" Adress = " + ia.getAddress() + " with Prefix-Length " + ia.getNetworkPrefixLength());
            }
        }
    }

    private Inet6Address getAdress(String host, byte[] addr, int scopeId) throws UnknownHostException {
        return Inet6Address.getByAddress(host, addr, scopeId);
    }
}
