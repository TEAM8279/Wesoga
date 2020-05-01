package wesoga.blocks;

public final class Block {
	public final int model;

	public final boolean solid;

	public Block(BlockModel model, boolean solid) {
		this.model = BlockModels.getID(model);

		this.solid = solid;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
