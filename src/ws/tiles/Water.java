package ws.tiles;

public class Water extends TileModel {

	@Override
	public String getID() {
		return "water";
	}

	@Override
	public boolean isWalkable() {
		return false;
	}
}
