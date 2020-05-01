package wesoga.blocks;

import java.util.ArrayList;

public class Blocks {
	private static ArrayList<Block> blocks = new ArrayList<>();

	public static void register(Block block) {
		if (blocks.contains(block)) {
			throw new RuntimeException("This block is already registered");
		}

		blocks.add(block);
	}

	public static int getID(Block block) {
		return blocks.indexOf(block);
	}

	public static Block get(int id) {
		return blocks.get(id);
	}
}
