package wesoga;

import java.util.ArrayList;

import wesoga.baseMod.BaseMod;
import wesoga.baseMod.entities.Player;
import wesoga.baseMod.entities.Zombie;
import wesoga.blocks.Block;
import wesoga.blocks.Blocks;
import wesoga.entities.Entity;
import wesoga.util.OpenSimplexNoise;

public class World {
	public static final int SIZE = 256;
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
				int height = (int) (Math.abs(noise.eval(x / 50.0, z / 50.0)) * HEIGHT / 3) + 1;
				for (int y = 0; y < height - 1; y++) {
					setBlock(x, y, z, BaseMod.SOIL);
				}

				setBlock(x, height - 1, z, BaseMod.GRASS);
			}
		}

		setBlock(50, 60, 50, BaseMod.GRASS);

		entities.add(new Zombie(50, 50, 50));
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
			e.onTick();
		}

		for (int a = 0; a < entities.size(); a++) {
			for (int b = a + 1; b < entities.size(); b++) {
				Entity.collide(entities.get(a), entities.get(b));
			}
		}

		for (int i = entities.size() - 1; i >= 0; i--) {
			if (!entities.get(i).isAlive()) {
				entities.remove(i);
			}
		}

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

	public static synchronized Player getNearestPlayer(double x, double y, double z, double maxDistance) {
		double distance = maxDistance * maxDistance;
		Player nearest = null;

		for (Entity entity : entities) {
			if (entity instanceof Player) {
				final double distX = x - entity.getX();
				final double distY = y - entity.getY();
				final double distZ = z - entity.getZ();

				final double dist = distX * distX + distY * distY + distZ * distZ;

				if (dist < distance) {
					distance = dist;
					nearest = (Player) entity;
				}
			}
		}

		return nearest;
	}
}
