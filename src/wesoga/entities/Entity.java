package wesoga.entities;

import wesoga.World;

public abstract class Entity {
	private static final double N = 0.001;

	public final int model;

	public final double size;
	public final double height;

	protected double speedX = 0;
	protected double speedY = 0;
	protected double speedZ = 0;

	protected double x;
	protected double y;
	protected double z;

	protected double rotation;

	private boolean alive = true;

	private boolean touchDown = false;
	private boolean touchUp = false;
	private boolean touchNorth = false;
	private boolean touchSouth = false;
	private boolean touchEast = false;
	private boolean touchWest = false;

	protected Entity(EntityModel model, double size, double height, double x, double y, double z) {
		this.model = EntityModels.getID(model);

		this.size = size;
		this.height = height;

		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final int getUUID() {
		return super.hashCode();
	}

	public final void setRotation(double value) {
		rotation = value;
	}

	public final double getRotation() {
		return rotation;
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final double getZ() {
		return z;
	}

	public final double getSpeedX() {
		return speedX;
	}

	public final double getSpeedY() {
		return speedY;
	}

	public final double getSpeedZ() {
		return speedZ;
	}

	public final void kill() {
		alive = false;
	}

	public final boolean isAlive() {
		return alive;
	}

	public final boolean touchDown() {
		return touchDown;
	}

	public final boolean touchUp() {
		return touchUp;
	}

	public final boolean touchEast() {
		return touchEast;
	}

	public final boolean touchWest() {
		return touchWest;
	}

	public final boolean touchNorth() {
		return touchNorth;
	}

	public final boolean touchSouth() {
		return touchSouth;
	}

	public final void accel(double x, double y, double z) {
		speedX += x;
		speedY += y;
		speedZ += z;
	}

	public double getFriction() {
		return 0.9;
	}

	private final void moveX() {
		touchWest = false;
		touchEast = false;

		x += speedX;
		for (int blockY = (int) y; blockY <= (int) (y + height); blockY++) {
			for (int blockZ = (int) z; blockZ <= (int) (z + size); blockZ++) {
				int blockX = (int) x;

				if (World.getBlock(blockX, blockY, blockZ).solid) {
					x = blockX + 1.0;
					speedX = 0;
					touchWest = true;
				}

				blockX = (int) (x + size);

				if (World.getBlock(blockX, blockY, blockZ).solid) {
					x = blockX - size - N;
					speedX = 0;
					touchEast = true;
				}
			}
		}
	}

	private final void moveY() {
		touchUp = false;
		touchDown = false;

		y += speedY;
		for (int blockX = (int) x; blockX <= (int) (x + size); blockX++) {
			for (int blockZ = (int) z; blockZ <= (int) (z + size); blockZ++) {
				int blockY = (int) y;

				if (World.getBlock(blockX, blockY, blockZ).solid) {
					y = blockY + 1.0;
					speedY = 0;
					touchDown = true;
				}

				blockY = (int) (y + height);

				if (World.getBlock(blockX, blockY, blockZ).solid) {
					y = blockY - height - N;
					speedY = 0;
					touchUp = true;
				}
			}
		}
	}

	private final void moveZ() {
		touchNorth = false;
		touchSouth = false;

		z += speedZ;
		for (int blockX = (int) x; blockX <= (int) (x + size); blockX++) {
			for (int blockY = (int) y; blockY <= (int) (y + height); blockY++) {
				int blockZ = (int) z;

				if (World.getBlock(blockX, blockY, blockZ).solid) {
					z = blockZ + 1.0;
					speedZ = 0;
					touchNorth = true;
				}

				blockZ = (int) (z + size);

				if (World.getBlock(blockX, blockY, blockZ).solid) {
					z = blockZ - size - N;
					speedZ = 0;
					touchSouth = true;
				}
			}
		}
	}

	/**
	 * Event fired every tick
	 */
	public void onTick() {

	}

	/**
	 * Event fired when colliding with an other entity
	 * 
	 * @param collider The collided entity
	 */
	public void onCollision(Entity collider) {

	}

	/**
	 * Called every tick to apply moves
	 */
	public void tickMoves() {
		speedY -= 0.003;

		if (Math.abs(speedX) > Math.abs(speedZ)) {
			moveX();
			moveZ();
		} else {
			moveZ();
			moveX();
		}

		moveY();

		speedX *= getFriction();
		speedY *= 0.998;
		speedZ *= getFriction();
	}

	public static void collide(final Entity e1, final Entity e2) {
		final double e1x = e1.x + e1.size / 2;
		final double e1z = e1.z + e1.size / 2;

		final double e2x = e2.x + e2.size / 2;
		final double e2z = e2.z + e2.size / 2;

		final double distX = e1x - e2x;
		final double distY = e1z - e2z;

		final double avgSize = (e1.size + e2.size) / 2;

		final double sqDist = distX * distX + distY * distY;

		if (sqDist > avgSize * avgSize) {
			return;
		}

		double moveSize = avgSize - Math.sqrt(sqDist);
		moveSize /= 32;

		double angle = Math.atan2(distX, distY);

		double impulseX = Math.sin(angle) * moveSize;
		double impulseZ = Math.cos(angle) * moveSize;

		e1.accel(impulseX, 0, impulseZ);

		e2.accel(-impulseX, 0, -impulseZ);

		e1.onCollision(e2);
		e2.onCollision(e1);
	}
}
