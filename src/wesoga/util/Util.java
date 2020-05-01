package wesoga.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {
	public static byte[] readData(String resource) {
		try {
			return readAllBytes(Util.class.getResourceAsStream(resource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] readAllBytes(InputStream stream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = stream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		return buffer.toByteArray();
	}
}
