package wesoga;

import wesoga.textures.Textures;
import wesoga.util.Util;

public class WebResources {
	private static final String WEB = "/resources/web/";

	private static final byte[] GRASS = readData("grass.png");

	private static final byte[] INDEX = readData("index.html");

	private static final byte[] STYLE = readData("style.css");

	private static final byte[] MAT4JS = readData("mat4.js");
	private static final byte[] DATAIDJS = readData("dataid.js");
	private static final byte[] MAINJS = readData("main.js");
	private static final byte[] WORLDJS = readData("world.js");
	private static final byte[] RENDERJS = readData("render.js");
	private static final byte[] PLAYERJS = readData("player.js");

	public static byte[] getResource(String name) {
		if (name.startsWith("/textures/")) {
			int index = Integer.parseInt(name.substring("/textures/".length()));

			return Textures.getTexture(index);
		}

		switch (name) {
		case "/":
			return INDEX;
		case "/grass.png":
			return GRASS;
		case "/style.css":
			return STYLE;
		case "/mat4.js":
			return MAT4JS;
		case "/dataid.js":
			return DATAIDJS;
		case "/main.js":
			return MAINJS;
		case "/world.js":
			return WORLDJS;
		case "/render.js":
			return RENDERJS;
		case "/player.js":
			return PLAYERJS;
		}

		return null;
	}

	private static byte[] readData(String resource) {
		return Util.readData(WEB + resource);
	}
}
