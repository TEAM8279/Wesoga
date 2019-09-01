package ws.baseMod.tiles;

import ws.tiles.TileModel;

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
