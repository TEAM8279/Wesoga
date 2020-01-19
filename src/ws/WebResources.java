package ws;

import java.io.IOException;

import ws.textures.Textures;

public class WebResources {
	private static final String WEB = "/resources/web/";

	private static final byte[] GRASS = readData("grass.png");
	
	private static final byte[] INDEX = readData("index.html");
	private static final byte[] MAT4 = readData("mat4.js");
	private static final byte[] NOISE = readData("noise.js");
	private static final byte[] SCRIPT = readData("script.js");
	private static final byte[] STYLE = readData("style.css");
	private static final byte[] WORLD = readData("world.js");

	public static byte[] getResource(String name) {
		if (name.startsWith("/textures/")) {
			int index = Integer.parseInt(name.substring("/textures/".length()));

			return Textures.getTexture(index);
		}

		switch (name) {
		case "/grass.png":
			return GRASS;
		case "/":
			return INDEX;
		case "/mat4.js":
			return MAT4;
		case "/noise.js":
			return NOISE;
		case "/script.js":
			return SCRIPT;
		case "/style.css":
			return STYLE;
		case "/world.js":
			return WORLD;
		}
		
		return null;
	}

	private static byte[] readData(String resource) {
		try {
			return WebResources.class.getResourceAsStream(WEB + resource).readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
