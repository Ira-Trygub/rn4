import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class B {
    public static void main(String[] args) {
        try {
            showNetwork();
            String msgId = "01";
            String transactionId = "123456";
            String clientIdOption = "0001";
            String optionLen = "000A";
            String duidType = "0003";
            String hardwareType = "0001";
            String linkLayerAddress = "88665a0f1fe8";
            String scopeId = "en0";
            String send = msgId + transactionId + clientIdOption + optionLen + duidType + hardwareType + linkLayerAddress;
            var socket = new DatagramSocket();
            var addr = Inet6Address.getByName("ff02::1:2%" + scopeId);
            byte[] sendBuf = hexStringtoByteArray(send);
            var packet = new DatagramPacket(sendBuf, sendBuf.length, addr, 547);
            socket.send(packet);
            byte[] receiveBuf = new byte[1024];
            DatagramPacket resp = new DatagramPacket(receiveBuf, 1024);
            socket.receive(resp);
            System.out.println("GOT" + resp.getLength() + ":" + byteArraytoHexString(resp.getData()));
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        var s = StreamSupport.stream(Spliterators.spliteratorUnknownSize(en.asIterator(), Spliterator.ORDERED), false).collect(Collectors.toList());
        s.forEach(ni -> {
            System.out.println("\nDisplay Name = " + ni.getDisplayName());
            System.out.println(" Name = " + ni.getName());
            System.out.println(" Scope ID (Interface ID) = " + ni.getIndex());
            try {
                System.out.println(" Hardware (LAN) Address = " + byteArraytoHexString(ni.getHardwareAddress()));
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            List<InterfaceAddress> list = ni.getInterfaceAddresses();
            Iterator<InterfaceAddress> it = list.iterator();

            while (it.hasNext()) {
                InterfaceAddress ia = it.next();
                System.out.println(" Adress = " + ia.getAddress() + " with Prefix-Length " + ia.getNetworkPrefixLength());
            }
        });
    }
}
