package ws;

import java.io.IOException;

import ws.tiles.Tiles;

public class WebResources {
	private static final String WEB = "/resources/web/";

	private static final byte[] INDEX = readData("index.html");
	private static final byte[] SCRIPT = readData("script.js");
	private static final byte[] PLAYER = readData("player.png");
	private static final byte[] STYLE = readData("style.css");

	private static final byte[] ERROR = readData("error.png");
	private static final byte[] GRASS = readData("grass.png");

	public static byte[] getResource(String name) {
		if (name.startsWith("/tiles_textures/")) {
			int index = Integer.valueOf(name.substring("/tiles_textures/".length()));

			return Tiles.getTexutre(index);
		}

		switch (name) {
		case "/":
			return INDEX;
		case "/script.js":
			return SCRIPT;
		case "/style.css":
			return STYLE;
		case "/player.png":
			return PLAYER;
		case "/grass.png":
			return GRASS;
		case "/error":
			return ERROR;
		default:
			return null;
		}
	}

	private static byte[] readData(String resource) {
		try {
			return WebResources.class.getResourceAsStream(WEB + resource).readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
