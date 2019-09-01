package ws.tiles;

import java.util.ArrayList;
import java.util.HashMap;

import ws.textures.Textures;

public class Tiles {
	private static ArrayList<TileModel> tiles = new ArrayList<>();

	private static HashMap<TileModel, Integer> tileTexture = new HashMap<>();

	public static void registerTile(TileModel tile) {
		if (tiles.contains(tile)) {
			throw new RuntimeException("This tile is already registered");
		}

		tiles.add(tile);
	}

	public static void assignTexture(TileModel tile, byte[] texture) {
		if (!tiles.contains(tile)) {
			throw new RuntimeException("Tile must be registered before assigning it a texture.");
		}

		int index = Textures.textureID(texture);

		tileTexture.put(tile, index);
	}

	public static int textureID(TileModel tile) {
		return tileTexture.get(tile);
	}
}
