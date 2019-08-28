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

	private static List<Entity> entities = new ArrayList<>();

	public synchronized static void addEntity(Entity e) {
		entities.add(e);
	}

	public synchronized static void removeEntity(Entity e) {
		entities.remove(e);
	}

	public synchronized static int entitiesCount() {
		return entities.size();
	}

	public synchronized static void tick() {
		for(int a = 0; a < entities.size(); a++) {
			for(int b = a + 1; b < entities.size(); b++) {
				Entity.collide(entities.get(a), entities.get(b));
			}
		}
		
		for (Entity e : entities) {
			e.tick();
		}
	}

	public static TileModel getTile(int x, int y) {
		return world[x][y];
	}

	public synchronized static ArrayList<Entity> getVisibleEntities(Player p) {
		ArrayList<Entity> selected = new ArrayList<>();

		for (Entity e : entities) {
			if (e != p) {
				selected.add(e);
			}
		}

		return selected;
	}
}
