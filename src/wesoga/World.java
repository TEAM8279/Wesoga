package wesoga;

import java.util.ArrayList;

import wesoga.baseMod.entities.Player;
import wesoga.baseMod.entities.Zombie;
import wesoga.entities.Entity;
import wesoga.util.OpenSimplexNoise;

public class World {
	public static final int SIZE = 256;
	public static final int HEIGHT = 64;

	private static final byte[][][] blocks = new byte[SIZE][HEIGHT][SIZE];

	private static final ArrayList<Entity> entities = new ArrayList<>();

	private static final OpenSimplexNoise noise = new OpenSimplexNoise(42);

	static {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				int height = (int) ((noise.eval(x / 40.0, z / 40.0) + 1) / 2 * HEIGHT);
				for (int y = 0; y < height; y++) {
					blocks[x][y][z] = 1;
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

		/*
		 * for (Entity e : entities) { e.tickMoves(); }
		 */
	}

	public static byte getBlock(int x, int y, int z) {
		return blocks[x][y][z];
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
