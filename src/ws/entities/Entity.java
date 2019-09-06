package ws.entities;

import ws.World;

public abstract class Entity {
	protected final EntityModel model;

	protected double speedX = 0;
	protected double speedY = 0;

	protected double x;
	protected double y;

	protected double rotation;

	protected boolean alive = true;

	protected Entity(EntityModel model) {
		this.model = model;
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

	public final EntityModel getModel() {
		return model;
	}

	public final int getModelID() {
		return Entities.modelID(model);
	}

	public final void tickMoves() {
		speedX *= 0.9;
		speedY *= 0.9;

		x += speedX;

		if (x < 0) {
			speedX = 0;
			x = 0;
		} else if (x + model.getSize() > World.SIZE) {
			speedX = 0;
			x = Math.nextDown(World.SIZE - model.getSize());
		}

		// Left collision check
		for (int blockY = (int) y; blockY <= (int) (y + model.getSize()); blockY++) {
			int blockX = (int) x;

			if (!World.getTile(blockX, blockY).isWalkable()) {
				x = blockX + 1;
				speedX = 0;
			}
		}

		// Right collision check
		for (int blockY = (int) y; blockY <= (int) (y + model.getSize()); blockY++) {
			int blockX = (int) (x + model.getSize());

			if (!World.getTile(blockX, blockY).isWalkable()) {
				x = Math.nextDown(blockX - model.getSize());
				speedX = 0;
			}
		}

		y += speedY;

		if (y < 0) {
			speedY = 0;
			y = 0;
		} else if (y + model.getSize() > World.SIZE) {
			speedY = 0;
			y = Math.nextDown(World.SIZE - model.getSize());
		}

		// Up collision check
		for (int blockX = (int) x; blockX <= (int) (x + model.getSize()); blockX++) {
			int blockY = (int) y;

			if (!World.getTile(blockX, blockY).isWalkable()) {
				y = blockY + 1;
				speedY = 0;
			}
		}

		// Down collision check
		for (int blockX = (int) x; blockX <= (int) (x + model.getSize()); blockX++) {
			int blockY = (int) (y + model.getSize());

			if (!World.getTile(blockX, blockY).isWalkable()) {
				y = Math.nextDown(blockY - model.getSize());
				speedY = 0;
			}
		}
	}

	public final void accel(double x, double y) {
		this.speedX += x;
		this.speedY += y;
	}

	public final boolean isAlive() {
		return alive;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	public void onTick() {

	}

	public void onCollision(Entity collidedEntity) {

	}

	public static void collide(final Entity e1, final Entity e2) {
		final double e1x = e1.x + e1.model.getSize() / 2;
		final double e1y = e1.y + e1.model.getSize() / 2;

		final double e2x = e2.x + e2.model.getSize() / 2;
		final double e2y = e2.y + e2.model.getSize() / 2;

		final double distX = e1x - e2x;
		final double distY = e1y - e2y;

		final double avgSize = (e1.model.getSize() + e2.model.getSize()) / 2;

		if (distX * distX + distY * distY > avgSize * avgSize) {
			return;
		}

		double moveSize = avgSize - Math.sqrt(distX * distX + distY * distY);
		moveSize /= 32;

		double angle = Math.atan2(distX, distY);
		e1.accel(Math.sin(angle) * moveSize, Math.cos(angle) * moveSize);

		angle += Math.PI;
		e2.accel(Math.sin(angle) * moveSize, Math.cos(angle) * moveSize);

		e1.onCollision(e2);
		e2.onCollision(e1);
	}
}
