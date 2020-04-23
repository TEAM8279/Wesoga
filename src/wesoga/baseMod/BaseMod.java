package wesoga.baseMod;

import wesoga.blocks.Block;
import wesoga.blocks.BlockModel;
import wesoga.blocks.BlockModels;
import wesoga.blocks.Blocks;
import wesoga.entities.EntityModel;
import wesoga.textures.Textures;
import wesoga.util.Util;

public class BaseMod {
	private static final String TEXTURES_FOLDER = "/resources/textures/";

	public static byte[] GRASS_TEXTURE;

	public static BlockModel AIR_MODEL;
	public static BlockModel GRASS_MODEL;

	public static Block AIR;
	public static Block GRASS;

	public static final EntityModel PLAYER_MODEL = new EntityModel(1.8, 0.5);

	public static void loadTextures() {
		GRASS_TEXTURE = readData("grass.png");
		Textures.registerTexture(GRASS_TEXTURE);
	}

	public static void loadModels() {
		AIR_MODEL = new BlockModel();
		BlockModels.register(AIR_MODEL);

		GRASS_MODEL = new BlockModel(GRASS_TEXTURE);
		BlockModels.register(GRASS_MODEL);
	}

	public static void loadBlocks() {
		AIR = new Block(AIR_MODEL, false);
		Blocks.register(AIR);

		GRASS = new Block(GRASS_MODEL, true);
		Blocks.register(GRASS);
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
