package ws.tiles;

public abstract class TileModel {
	public abstract String getID();

	@Override
	public final boolean equals(Object obj) {
		return obj == this;
	}

	public boolean isWalkable() {
		return true;
	}

	public boolean isFlyable() {
		return true;
	}
}
