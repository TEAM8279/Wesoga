package wesoga;

import wesoga.textures.Textures;
import wesoga.util.Util;

public class WebResources {
	private static final String WEB = "/resources/web/";

	private static final byte[] GRASS = readData("grass.png");

	private static final byte[] INDEX = readData("index.html");
	private static final byte[] MAT4 = readData("mat4.js");
	private static final byte[] SCRIPT = readData("script.js");
	private static final byte[] STYLE = readData("style.css");
	private static final byte[] WORLD = readData("world.js");
	private static final byte[] RENDER = readData("render.js");
	private static final byte[] PLAYER = readData("player.js");

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
		case "/mat4.js":
			return MAT4;
		case "/script.js":
			return SCRIPT;
		case "/style.css":
			return STYLE;
		case "/world.js":
			return WORLD;
		case "/render.js":
			return RENDER;
		case "/player.js":
			return PLAYER;
		}

		return null;
	}

	private static byte[] readData(String resource) {
		return Util.readData(WEB + resource);
	}
}
