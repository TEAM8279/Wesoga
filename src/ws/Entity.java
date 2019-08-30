package ws;

public abstract class Entity {
	protected double speedX = 0;
	protected double speedY = 0;

	protected double x;
	protected double y;

	protected double rotation;

	public void setRotation(double value) {
		rotation = value;
	}

	public double getRotation() {
		return rotation;
	}

	public abstract int getID();

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void tick() {
		speedX *= 0.9;
		speedY *= 0.9;

		x += speedX;

		if (x < 0) {
			speedX = 0;
			x = 0;
		} else if (x + getSize() > World.SIZE) {
			speedX = 0;
			x = Math.nextDown(World.SIZE - getSize());
		}

		// Left collision check
		for (int blockY = (int) y; blockY <= (int) (y + getSize()); blockY++) {
			int blockX = (int) x;

			if (!World.getTile(blockX, blockY).isWalkable()) {
				x = blockX + 1;
				speedX = 0;
			}
		}

		// Right collision check
		for (int blockY = (int) y; blockY <= (int) (y + getSize()); blockY++) {
			int blockX = (int) (x + getSize());

			if (!World.getTile(blockX, blockY).isWalkable()) {
				x = Math.nextDown(blockX - getSize());
				speedX = 0;
			}
		}

		y += speedY;

		if (y < 0) {
			speedY = 0;
			y = 0;
		} else if (y + getSize() > World.SIZE) {
			speedY = 0;
			y = Math.nextDown(World.SIZE - getSize());
		}

		// Up collision check
		for (int blockX = (int) x; blockX <= (int) (x + getSize()); blockX++) {
			int blockY = (int) y;

			if (!World.getTile(blockX, blockY).isWalkable()) {
				y = blockY + 1;
				speedY = 0;
			}
		}

		// Down collision check
		for (int blockX = (int) x; blockX <= (int) (x + getSize()); blockX++) {
			int blockY = (int) (y + getSize());

			if (!World.getTile(blockX, blockY).isWalkable()) {
				y = Math.nextDown(blockY - getSize());
				speedY = 0;
			}
		}
	}

	public void accel(double x, double y) {
		this.speedX += x;
		this.speedY += y;
	}

	public void accelX(double value) {
		speedX += value;
	}

	public void accelY(double value) {
		speedY += value;
	}

	public abstract double getSize();

	public static void collide(final Entity e1, final Entity e2) {
		final double e1x = e1.x + e1.getSize() / 2;
		final double e1y = e1.y + e1.getSize() / 2;

		final double e2x = e2.x + e2.getSize() / 2;
		final double e2y = e2.y + e2.getSize() / 2;

		final double distX = e1x - e2x;
		final double distY = e1y - e2y;

		final double avgSize = (e1.getSize() + e2.getSize()) / 2;

		if (distX * distX + distY * distY > avgSize * avgSize) {
			return;
		}

		double moveSize = avgSize - Math.sqrt(distX * distX + distY * distY);
		moveSize /= 32;

		double angle = Math.atan2(distX, distY);
		e1.accel(Math.sin(angle) * moveSize, Math.cos(angle) * moveSize);

		angle += Math.PI;
		e2.accel(Math.sin(angle) * moveSize, Math.cos(angle) * moveSize);
	}
}
