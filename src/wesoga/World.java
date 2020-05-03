package wesoga;

import java.util.ArrayList;

import wesoga.baseMod.BaseMod;
import wesoga.baseMod.entities.Player;
import wesoga.blocks.Block;
import wesoga.blocks.Blocks;
import wesoga.entities.Entity;
import wesoga.util.OpenSimplexNoise;

public class World {
	public static final int SIZE = 128;
	public static final int HEIGHT = 64;

	private static final int[][][] blocks = new int[SIZE][HEIGHT][SIZE];

	private static final ArrayList<Entity> entities = new ArrayList<>();

	private static final OpenSimplexNoise noise = new OpenSimplexNoise(42);

	static {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				for (int z = 0; z < SIZE; z++) {
					setBlock(x, y, z, BaseMod.AIR);
				}
			}
		}

		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				int height = (int) ((noise.eval(x / 40.0, z / 40.0) + 1) / 2 * HEIGHT / 2);
				for (int y = 0; y < height - 1; y++) {
					setBlock(x, y, z, BaseMod.SOIL);
				}

				setBlock(x, height - 1, z, BaseMod.GRASS);
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
		for (Entity e : entities) {
			e.tickMoves();
		}
	}

	public static Block getBlock(int x, int y, int z) {
		return Blocks.get(blocks[x][y][z]);
	}

	public static void setBlock(int x, int y, int z, Block block) {
		blocks[x][y][z] = Blocks.getID(block);
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
