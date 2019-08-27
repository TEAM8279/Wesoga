package ws;

import java.util.ArrayList;
import java.util.List;

import ws.baseMod.BaseMod;
import ws.tiles.TileModel;

public class World {
	public static final int SIZE = 100;

	private static final TileModel[][] world = new TileModel[SIZE][SIZE];

	static {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				world[x][y] = BaseMod.GRASS;
			}
		}

		for (int x = 4; x < 8; x++) {
			for (int y = 4; y < 9; y++) {
				world[x][y] = BaseMod.WATER;
			}
		}
	}

	public static List<Entity> entities = new ArrayList<>();

	public static void tick() {
		for (Entity e : entities) {
			e.tick();
		}
	}

	public static TileModel getTile(int x, int y) {
		return world[x][y];
	}
}
