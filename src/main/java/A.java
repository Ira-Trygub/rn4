import java.io.IOException;
import java.net.*;
import java.util.*;

public class A {

    public static InetAddress multicastAddress;
    public final static int zielport = 547;
    public final static int sourceport = 546;

    public static DatagramSocket clientSocket;

    private static byte[] hexStringToByteArray(String hex) {
        /* Konvertiere den String mit Hex-Ziffern in ein Byte-Array */
        byte[] val = new byte[hex.length() / 2];
        for (int i = 0; i < val.length; i++) {
            int index = i * 2;
            int num = Integer.parseInt(hex.substring(index, index + 2), 16);
            val[i] = (byte) num;
        }
        return val;
    }

    private static String byteArrayToHexString(byte[] byteArray) {
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
            System.out.println(" Hardware (LAN) Address = " + byteArrayToHexString(ni.getHardwareAddress()));


            List<InterfaceAddress> list = ni.getInterfaceAddresses();
            Iterator<InterfaceAddress> it = list.iterator();

            while (it.hasNext()) {
                InterfaceAddress ia = it.next();
                System.out.println(" Adress = " + ia.getAddress() + " with Prefix-Length " + ia.getNetworkPrefixLength());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        showNetwork();
        multicastAddress = Inet6Address.getByName("ff02::1:2%en0");
        clientSocket = new DatagramSocket(sourceport);

        String msgId = "01";
        String transactionId = "AAAAAA";
        String clientIdOption = "0001";
        String optionLen = "000A";
        String duidType = "0003"; // 2 Bytes
        String hardwareType = "0001"; // 2 Bytes
        String linkLayerAddress = "88665a0f1fe8"; // 6 Bytes Mac-Addr

        String datagram = msgId + transactionId + clientIdOption + optionLen + duidType + hardwareType + linkLayerAddress;

        byte[] datagramArr = hexStringToByteArray(datagram);

        DatagramPacket datagramPacketSend = new DatagramPacket(datagramArr, datagramArr.length, multicastAddress, zielport);

        clientSocket.send(datagramPacketSend);

        byte[] data = new byte[1024];

        DatagramPacket datagramPaketReceive = new DatagramPacket(data, data.length);

        clientSocket.receive(datagramPaketReceive);

        String received = byteArrayToHexString(datagramPaketReceive.getData());
        System.out.println("Nachricht empfangen: " + received);

    }



}