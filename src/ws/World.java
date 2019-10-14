package ws;

import java.util.ArrayList;
import java.util.Random;

import ws.baseMod.BaseMod;
import ws.baseMod.entities.Player;
import ws.baseMod.entities.Zombie;
import ws.entities.Entity;
import ws.tiles.TileModel;
import ws.util.OpenSimplexNoise;

public class World {
	private static final Random rand = new Random();

	public static final int SIZE = 100;

	private static final TileModel[][] world = new TileModel[SIZE][SIZE];

	private static final ArrayList<Entity> entities = new ArrayList<>();

	private static final OpenSimplexNoise noise = new OpenSimplexNoise(rand.nextLong());

	static {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				double n = noise.eval(x / 10D, y / 10D);

				if (n < -0.6) {
					world[x][y] = BaseMod.WATER;
				} else {
					world[x][y] = BaseMod.GRASS;
				}
			}
		}
	}

	public static synchronized void addEntity(Entity e) {
		entities.add(e);
	}

	public static synchronized void removeEntity(Entity e) {
		entities.remove(e);
	}

	public static synchronized int entitiesCount() {
		return entities.size();
	}

	public static synchronized void tick() {
		int zombiesCount = 0;

		for (Entity e : entities) {
			if (e instanceof Zombie) {
				zombiesCount++;
			}
		}

		if (zombiesCount < 5) {
			entities.add(new Zombie(16, 16));
		}

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

	public static synchronized ArrayList<Entity> getVisibleEntities(Player p) {
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
