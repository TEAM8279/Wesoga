package wesoga.blocks;

import java.util.ArrayList;

public class BlockModels {
	private static ArrayList<BlockModel> blockModels = new ArrayList<>();

	public static void register(BlockModel model) {
		if (blockModels.contains(model)) {
			throw new RuntimeException("This block model is already registered");
		}

		blockModels.add(model);
	}

	public static int getID(BlockModel model) {
		return blockModels.indexOf(model);
	}
	
	public static BlockModel get(int id) {
		return blockModels.get(id);
	}
	
	public static int count() {
		return blockModels.size();
	}
}
