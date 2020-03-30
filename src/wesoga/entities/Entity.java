package wesoga.entities;

import wesoga.World;

public abstract class Entity {
	private static final double N = 0.001;

	protected final EntityModel model;

	protected double speedX = 0;
	protected double speedY = 0;
	protected double speedZ = 0;

	protected double x;
	protected double y;
	protected double z;

	protected double rotation;

	protected boolean alive = true;

	protected boolean onFloor = false;

	protected Entity(EntityModel model, double x, double y, double z) {
		this.model = model;
		this.x = x;
		this.y = y;
		this.z = z;
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

	public final EntityModel getModel() {
		return model;
	}

	public final boolean isAlive() {
		return alive;
	}

	public final boolean isOnFloor() {
		return onFloor;
	}

	public final void accel(double x, double y, double z) {
		speedX += x;
		speedY += y;
		speedZ += z;
	}

	public double getFriction() {
		return 0.9;
	}

	public void tickMoves() {
		speedY -= 0.002;

		// X collision check
		x += speedX;
		for (int blockY = (int) y; blockY <= (int) (y + model.getHeight()); blockY++) {
			for (int blockZ = (int) z; blockZ <= (int) (z + model.getWidth()); blockZ++) {
				int blockX = (int) x;

				byte block = World.getBlock(blockX, blockY, blockZ);

				if (block > 0) {
					x = blockX + 1.0;
					speedX = 0;
				}
			}
		}
		for (int blockY = (int) y; blockY <= (int) (y + model.getHeight()); blockY++) {
			for (int blockZ = (int) z; blockZ <= (int) (z + model.getWidth()); blockZ++) {
				int blockX = (int) (x + model.getWidth());

				byte block = World.getBlock(blockX, blockY, blockZ);

				if (block > 0) {
					x = blockX - model.getWidth() - N;
					speedX = 0;
				}
			}
		}

		// Y collision check
		onFloor = false;
		y += speedY;
		for (int blockX = (int) x; blockX <= (int) (x + model.getWidth()); blockX++) {
			for (int blockZ = (int) z; blockZ <= (int) (z + model.getWidth()); blockZ++) {
				int blockY = (int) y;

				byte block = World.getBlock(blockX, blockY, blockZ);

				if (block > 0) {
					y = blockY + 1.0;
					speedY = 0;
					onFloor = true;
				}
			}
		}
		for (int blockX = (int) x; blockX <= (int) (x + model.getWidth()); blockX++) {
			for (int blockZ = (int) z; blockZ <= (int) (z + model.getWidth()); blockZ++) {
				int blockY = (int) (y + model.getHeight());

				byte block = World.getBlock(blockX, blockY, blockZ);

				if (block > 0) {
					y = blockY - model.getHeight() - N;
					speedY = 0;
				}
			}
		}

		// Z collision check
		z += speedZ;
		for (int blockX = (int) x; blockX <= (int) (x + model.getWidth()); blockX++) {
			for (int blockY = (int) y; blockY <= (int) (y + model.getHeight()); blockY++) {
				int blockZ = (int) z;

				byte block = World.getBlock(blockX, blockY, blockZ);

				if (block > 0) {
					z = blockZ + 1.0;
					speedZ = 0;
				}
			}
		}
		for (int blockX = (int) x; blockX <= (int) (x + model.getWidth()); blockX++) {
			for (int blockY = (int) y; blockY <= (int) (y + model.getHeight()); blockY++) {
				int blockZ = (int) (z + model.getWidth());

				byte block = World.getBlock(blockX, blockY, blockZ);

				if (block > 0) {
					z = blockZ - model.getWidth() - N;
					speedZ = 0;
				}
			}
		}

		speedX *= getFriction();
		speedY *= 0.999;
		speedZ *= getFriction();

	}
}
