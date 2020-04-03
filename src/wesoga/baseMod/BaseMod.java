package wesoga.baseMod;

import wesoga.baseMod.blocks.Air;
import wesoga.baseMod.blocks.Grass;
import wesoga.entities.EntityModel;
import wesoga.textures.Textures;
import wesoga.util.Util;

public class BaseMod {
	private static final String TEXTURES_FOLDER = "/resources/textures/";

	public static final byte[] GRASS_TEXTURE = readData("grass.png");

	public static final Air AIR = new Air();
	public static final Grass GRASS = new Grass();

	public static final EntityModel PLAYER_MODEL = new EntityModel(1.8, 0.5);

	public static void load() {
		Textures.registerTexture(GRASS_TEXTURE);
	}

	private static byte[] readData(String resource) {
		return Util.readData(TEXTURES_FOLDER + resource);
	}
}
