package ws.baseMod;

import ws.tiles.Grass;
import ws.tiles.Tiles;
import ws.tiles.Water;
import ws.util.Util;

public class BaseMod {
	public static final byte[] GRASS_TEXTURE = Util.readData("grass.png");
	public static final byte[] WATER_TEXTURE = Util.readData("water.png");

	public static final Grass GRASS = new Grass();
	public static final Water WATER = new Water();

	public static void load() {
		Tiles.registerTile(GRASS);
		Tiles.registerTile(WATER);

		Tiles.registerTexture(GRASS_TEXTURE);
		Tiles.registerTexture(WATER_TEXTURE);

		Tiles.assignTexture(GRASS, GRASS_TEXTURE);
		Tiles.assignTexture(WATER, WATER_TEXTURE);
	}
}
