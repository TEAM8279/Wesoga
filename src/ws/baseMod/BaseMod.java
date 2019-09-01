package ws.baseMod;

import ws.baseMod.entities.PlayerModel;
import ws.baseMod.tiles.Grass;
import ws.baseMod.tiles.Water;
import ws.entities.Entities;
import ws.textures.Textures;
import ws.tiles.Tiles;
import ws.util.Util;

public class BaseMod {
	public static final byte[] GRASS_TEXTURE = Util.readData("grass.png");
	public static final byte[] WATER_TEXTURE = Util.readData("water.png");
	public static final byte[] PLAYER_TEXTURE = Util.readData("player.png");

	public static final Grass GRASS = new Grass();
	public static final Water WATER = new Water();

	public static final PlayerModel PLAYER_MODEL = new PlayerModel();

	public static void load() {
		Textures.registerTexture(GRASS_TEXTURE);
		Textures.registerTexture(WATER_TEXTURE);
		Textures.registerTexture(PLAYER_TEXTURE);

		Tiles.registerTile(GRASS);
		Tiles.registerTile(WATER);

		Tiles.assignTexture(GRASS, GRASS_TEXTURE);
		Tiles.assignTexture(WATER, WATER_TEXTURE);

		Entities.registerModel(PLAYER_MODEL);
		Entities.assignTexture(PLAYER_MODEL, PLAYER_TEXTURE);
	}
}
