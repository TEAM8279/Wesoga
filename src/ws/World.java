package ws;

import java.util.ArrayList;

import ws.baseMod.BaseMod;
import ws.baseMod.entities.Player;
import ws.baseMod.entities.Zombie;
import ws.entities.Entity;
import ws.tiles.TileModel;

public class World {
	public static final int SIZE = 100;

	private static final TileModel[][] world = new TileModel[SIZE][SIZE];

	private static final ArrayList<Entity> entities = new ArrayList<>();

	static {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				world[x][y] = BaseMod.GRASS;
			}
		}

		for (int x = 4; x < 14; x++) {
			for (int y = 4; y < 14; y++) {
				world[x][y] = BaseMod.WATER;
			}
		}

		entities.add(new Zombie(16, 16));
	}

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
		for (int i = entities.size() - 1; i >= 0; i--) {
			if (!entities.get(i).isAlive()) {
				entities.remove(i);
			}
		}

		for (int a = 0; a < entities.size(); a++) {
			for (int b = a + 1; b < entities.size(); b++) {
				Entity.collide(entities.get(a), entities.get(b));
			}
		}

		for (Entity e : entities) {
			e.onTick();
		}

		for (Entity e : entities) {
			e.tickMoves();
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

	public static synchronized Player getNearestPlayer(final double x, final double y, final double maxDistance) {
		double distance = maxDistance * maxDistance;
		Player nearest = null;

		for (final Entity entity : entities) {
			if (entity instanceof Player) {
				final double distX = x - entity.getX();
				final double distY = y - entity.getY();

				final double dist = distX * distX + distY * distY;

				if (dist < distance) {
					distance = dist;
					nearest = (Player) entity;
				}
			}
		}

		return nearest;
	}
}
