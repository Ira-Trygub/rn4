	private byte[] hexStringtoByteArray(String hex) {
		/* Konvertiere den String mit Hex-Ziffern in ein Byte-Array */
		byte[] val = new byte[hex.length() / 2];
		for (int i = 0; i < val.length; i++) {
			int index = i * 2;
			int num = Integer.parseInt(hex.substring(index, index + 2), 16);
			val[i] = (byte) num;
		}
		return val;
	}

	private String byteArraytoHexString(byte[] byteArray) {
		/* Konvertiere das Byte-Array in einen String mit Hex-Ziffern */
		String hex = "";
		if (byteArray != null) {
			for (int i = 0; i < byteArray.length; ++i) {
				hex = hex + String.format("%02X", byteArray[i]);
			}
		}
		return hex;
	}

	private void showNetwork() throws SocketException {
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
