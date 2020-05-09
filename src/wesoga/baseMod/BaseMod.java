package wesoga.baseMod;

import wesoga.blocks.Block;
import wesoga.blocks.BlockModel;
import wesoga.blocks.BlockModels;
import wesoga.blocks.Blocks;
import wesoga.entities.EntityModel;
import wesoga.entities.EntityModels;
import wesoga.textures.Textures;
import wesoga.util.Util;

public class BaseMod {
	private static final String TEXTURES_FOLDER = "/resources/textures/";

	public static byte[] GRASS_TEXTURE_TOP;
	public static byte[] GRASS_TEXTURE_SIDE;
	public static byte[] SOIL_TEXTURE;

	public static byte[] PLAYER_FRONT;

	public static BlockModel AIR_MODEL;
	public static BlockModel GRASS_MODEL;
	public static BlockModel SOIL_MODEL;

	public static Block AIR;
	public static Block GRASS;
	public static Block SOIL;

	public static EntityModel PLAYER_MODEL;

	public static void loadTextures() {
		GRASS_TEXTURE_TOP = readData("grass_top.png");
		Textures.registerTexture(GRASS_TEXTURE_TOP);

		GRASS_TEXTURE_SIDE = readData("grass_side.png");
		Textures.registerTexture(GRASS_TEXTURE_SIDE);

		SOIL_TEXTURE = readData("soil.png");
		Textures.registerTexture(SOIL_TEXTURE);

		PLAYER_FRONT = readData("player_front.png");
		Textures.registerTexture(PLAYER_FRONT);
	}

	public static void loadModels() {
		AIR_MODEL = new BlockModel();
		BlockModels.register(AIR_MODEL);

		GRASS_MODEL = new BlockModel(GRASS_TEXTURE_SIDE, GRASS_TEXTURE_SIDE, GRASS_TEXTURE_SIDE, GRASS_TEXTURE_SIDE,
				GRASS_TEXTURE_TOP, SOIL_TEXTURE);
		BlockModels.register(GRASS_MODEL);

		SOIL_MODEL = new BlockModel(SOIL_TEXTURE);
		BlockModels.register(SOIL_MODEL);

		PLAYER_MODEL = new EntityModel(PLAYER_FRONT, PLAYER_FRONT, PLAYER_FRONT, PLAYER_FRONT, PLAYER_FRONT,
				PLAYER_FRONT, 0.7812, 1.7188);
		EntityModels.register(PLAYER_MODEL);
	}

	public static void loadBlocks() {
		AIR = new Block(AIR_MODEL, false);
		Blocks.register(AIR);

		GRASS = new Block(GRASS_MODEL, true);
		Blocks.register(GRASS);

		SOIL = new Block(SOIL_MODEL, true);
		Blocks.register(SOIL);
	}

	public static void load() {
		loadTextures();
		loadModels();
		loadBlocks();
	}

	private static byte[] readData(String resource) {
		return Util.readData(TEXTURES_FOLDER + resource);
	}
}
