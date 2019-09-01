package ws.textures;

import java.util.ArrayList;

public class Textures {
	private static ArrayList<byte[]> textures = new ArrayList<>();

	public static void registerTexture(byte[] texture) {
		if (textures.contains(texture)) {
			throw new RuntimeException("This texture is already registered");
		}

		textures.add(texture);
	}

	public static int textureID(byte[] texture) {
		int index = textures.indexOf(texture);

		if (index == -1) {
			throw new RuntimeException("This texture isn't registered");
		}

		return index;
	}

	public static int count() {
		return textures.size();
	}

	public static byte[] getTexture(int index) {
		return textures.get(index);
	}
}
