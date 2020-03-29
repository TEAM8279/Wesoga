package wesoga.blocks;

public abstract class BlockModel {
	@Override
	public final boolean equals(Object obj) {
		return obj == this;
	}

	public abstract boolean isSolid();

	public abstract boolean isVisible();
}
