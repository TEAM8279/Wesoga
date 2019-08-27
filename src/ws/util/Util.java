package ws.util;

import java.io.IOException;

public class Util {
	public static final String WEB = "/resources/web/";

	public static byte[] readData(String resource) {
		try {
			return Util.class.getResourceAsStream(WEB + resource).readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
