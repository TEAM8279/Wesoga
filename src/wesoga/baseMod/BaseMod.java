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
	public static byte[] PLAYER_BACK;
	public static byte[] PLAYER_RIGHT;
	public static byte[] PLAYER_LEFT;
	public static byte[] PLAYER_TOP;
	public static byte[] PLAYER_BOT;

	public static byte[] ZOMBIE_FRONT;
	public static byte[] ZOMBIE_BACK;
	public static byte[] ZOMBIE_RIGHT;
	public static byte[] ZOMBIE_LEFT;
	public static byte[] ZOMBIE_TOP;
	public static byte[] ZOMBIE_BOT;

	public static BlockModel AIR_MODEL;
	public static BlockModel GRASS_MODEL;
	public static BlockModel SOIL_MODEL;

	public static Block AIR;
	public static Block GRASS;
	public static Block SOIL;

	public static EntityModel PLAYER_MODEL;
	public static EntityModel ZOMBIE_MODEL;

	public static void loadTextures() {
		GRASS_TEXTURE_TOP = readData("grass_top.png");
		Textures.registerTexture(GRASS_TEXTURE_TOP);

		GRASS_TEXTURE_SIDE = readData("grass_side.png");
		Textures.registerTexture(GRASS_TEXTURE_SIDE);

		SOIL_TEXTURE = readData("soil.png");
		Textures.registerTexture(SOIL_TEXTURE);

		PLAYER_FRONT = readData("player_front.png");
		Textures.registerTexture(PLAYER_FRONT);
		PLAYER_BACK = readData("player_back.png");
		Textures.registerTexture(PLAYER_BACK);
		PLAYER_RIGHT = readData("player_right.png");
		Textures.registerTexture(PLAYER_RIGHT);
		PLAYER_LEFT = readData("player_left.png");
		Textures.registerTexture(PLAYER_LEFT);
		PLAYER_TOP = readData("player_top.png");
		Textures.registerTexture(PLAYER_TOP);
		PLAYER_BOT = readData("player_bot.png");
		Textures.registerTexture(PLAYER_BOT);

		ZOMBIE_FRONT = readData("zombie_front.png");
		Textures.registerTexture(ZOMBIE_FRONT);
		ZOMBIE_BACK = readData("zombie_back.png");
		Textures.registerTexture(ZOMBIE_BACK);
		ZOMBIE_RIGHT = readData("zombie_right.png");
		Textures.registerTexture(ZOMBIE_RIGHT);
		ZOMBIE_LEFT = readData("zombie_left.png");
		Textures.registerTexture(ZOMBIE_LEFT);
		ZOMBIE_TOP = readData("zombie_top.png");
		Textures.registerTexture(ZOMBIE_TOP);
		ZOMBIE_BOT = readData("zombie_bot.png");
		Textures.registerTexture(ZOMBIE_BOT);
	}

	public static void loadModels() {
		AIR_MODEL = new BlockModel();
		BlockModels.register(AIR_MODEL);

		GRASS_MODEL = new BlockModel(GRASS_TEXTURE_SIDE, GRASS_TEXTURE_SIDE, GRASS_TEXTURE_SIDE, GRASS_TEXTURE_SIDE,
				GRASS_TEXTURE_TOP, SOIL_TEXTURE);
		BlockModels.register(GRASS_MODEL);

		SOIL_MODEL = new BlockModel(SOIL_TEXTURE);
		BlockModels.register(SOIL_MODEL);

		PLAYER_MODEL = new EntityModel(PLAYER_FRONT, PLAYER_BACK, PLAYER_RIGHT, PLAYER_LEFT, PLAYER_TOP, PLAYER_BOT,
				0.7812, 1.7188);
		EntityModels.register(PLAYER_MODEL);

		ZOMBIE_MODEL = new EntityModel(ZOMBIE_FRONT, ZOMBIE_BACK, ZOMBIE_RIGHT, ZOMBIE_LEFT, ZOMBIE_TOP, ZOMBIE_BOT,
				0.7812, 1.7188);
		EntityModels.register(ZOMBIE_MODEL);
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
