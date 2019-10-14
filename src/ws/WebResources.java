package ws;

import java.io.IOException;

import ws.textures.Textures;

public class WebResources {
	private static final String WEB = "/resources/web/";

	private static final byte[] INDEX = readData("index.html");
	private static final byte[] SCRIPT = readData("script.js");
	private static final byte[] STYLE = readData("style.css");
	private static final byte[] FAVICON = readData("player.png");

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
		case "/style.css":
			return STYLE;
		case "/favicon.png":
			return FAVICON;
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
