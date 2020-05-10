package wesoga;

import wesoga.textures.Textures;
import wesoga.util.Util;

public class WebResources {
	private static final String WEB_FOLDER = "/resources/web/";

	private static final byte[] INDEX = readData("index.html");

	private static final byte[] SCRIPT = readData("script.js");

	public static byte[] getResource(String name) {
		if (name.startsWith("/textures/")) {
			int index = Integer.parseInt(name.substring("/textures/".length()));

			return Textures.getTexture(index);
		}

		switch (name) {
		case "/":
			return INDEX;
		case "/script.js":
			return SCRIPT;
		}

		return null;
	}

	private static byte[] readData(String resource) {
		return Util.readData(WEB_FOLDER + resource);
	}
}
