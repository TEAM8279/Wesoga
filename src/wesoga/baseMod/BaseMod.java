package wesoga.baseMod;

import wesoga.baseMod.entities.ArrowModel;
import wesoga.baseMod.entities.PlayerModel;
import wesoga.baseMod.entities.ZombieModel;
import wesoga.baseMod.tiles.Grass;
import wesoga.baseMod.tiles.Water;
import wesoga.entities.Entities;
import wesoga.textures.Textures;
import wesoga.tiles.Tiles;
import wesoga.util.Util;

public class BaseMod {
	public static final byte[] GRASS_TEXTURE = Util.readData("grass.png");
	public static final byte[] WATER_TEXTURE = Util.readData("water.png");
	public static final byte[] PLAYER_TEXTURE = Util.readData("player.png");
	public static final byte[] ZOMBIE_TEXTURE = Util.readData("zombie.png");
	public static final byte[] ARROW_TEXTURE = Util.readData("arrow.png");

	public static final Grass GRASS = new Grass();
	public static final Water WATER = new Water();

	public static final PlayerModel PLAYER_MODEL = new PlayerModel();
	public static final ZombieModel ZOMBIE_MODEL = new ZombieModel();
	public static final ArrowModel ARROW_MODEL = new ArrowModel();

	public static void load() {
		Textures.registerTexture(GRASS_TEXTURE);
		Textures.registerTexture(WATER_TEXTURE);
		Textures.registerTexture(PLAYER_TEXTURE);
		Textures.registerTexture(ZOMBIE_TEXTURE);
		Textures.registerTexture(ARROW_TEXTURE);

		Tiles.registerTile(GRASS);
		Tiles.registerTile(WATER);

		Tiles.assignTexture(GRASS, GRASS_TEXTURE);
		Tiles.assignTexture(WATER, WATER_TEXTURE);

		Entities.registerModel(PLAYER_MODEL);
		Entities.registerModel(ZOMBIE_MODEL);
		Entities.registerModel(ARROW_MODEL);

		Entities.assignTexture(PLAYER_MODEL, PLAYER_TEXTURE);
		Entities.assignTexture(ZOMBIE_MODEL, ZOMBIE_TEXTURE);
		Entities.assignTexture(ARROW_MODEL, ARROW_TEXTURE);
	}
}
