package wesoga.blocks;

import java.util.ArrayList;
import java.util.HashMap;

import wesoga.textures.Textures;

public class Blocks {
	private static ArrayList<BlockModel> blocks = new ArrayList<>();

	private static HashMap<BlockModel, Integer> blockTexture = new HashMap<>();

	public static void registerBlock(BlockModel block) {
		if (blocks.contains(block)) {
			throw new RuntimeException("This block is already registered");
		}

		blocks.add(block);
	}

	public static void assignTexture(BlockModel block, byte[] texture) {
		if (!blocks.contains(block)) {
			throw new RuntimeException("Block must be registered before assigning it a texture.");
		}

		blockTexture.put(block, Textures.textureID(texture));
	}

	public static int textureID(BlockModel block) {
		Integer id = blockTexture.get(block);

		if (id == null) {
			throw new RuntimeException("There is no texture for this block");
		}

		return id;
	}
}
