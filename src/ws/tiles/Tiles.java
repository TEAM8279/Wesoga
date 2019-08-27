package ws.tiles;

import java.util.ArrayList;
import java.util.HashMap;

public class Tiles {
	private static ArrayList<TileModel> tiles = new ArrayList<>();
	private static ArrayList<byte[]> textures = new ArrayList<>();

	private static HashMap<TileModel, Integer> tileTexture = new HashMap<>();

	public static void registerTile(TileModel tile) {
		if (tiles.contains(tile)) {
			throw new RuntimeException("This tile is already registered");
		}

		tiles.add(tile);
	}

	public static void registerTexture(byte[] texture) {
		if (textures.contains(texture)) {
			throw new RuntimeException("This texture is already registered");
		}

		textures.add(texture);
	}

	public static void assignTexture(TileModel tile, byte[] texture) {
		if (!tiles.contains(tile)) {
			throw new RuntimeException("Tile must be registered before assigning it a texture.");
		}

		int index = textures.indexOf(texture);

		if (index == -1) {
			throw new RuntimeException("Texture must be registered before being assigned to a tile");
		}

		tileTexture.put(tile, index);
	}

	public static int textureID(TileModel tile) {
		return tileTexture.get(tile);
	}

	public static int texturesCount() {
		return textures.size();
	}

	public static byte[] getTexutre(int index) {
		return textures.get(index);
	}
}
