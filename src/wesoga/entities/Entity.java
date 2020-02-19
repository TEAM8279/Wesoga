package wesoga.entities;

public abstract class Entity {
	protected double speedX = 0;
	protected double speedY = 0;
	protected double speedZ = 0;

	protected double x;
	protected double y;
	protected double z;

	protected double rotation;

	protected boolean alive = true;

	protected Entity(double x, double y, double z) {
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

	public final void accel(double x, double y, double z) {
		speedX += x;
		speedY += y;
		speedZ += z;
	}

	public double getFriction() {
		return 0.9;
	}

	public void tickMoves() {
		x += speedX;
		y += speedY;
		z += speedZ;

		speedX *= getFriction();
		speedY *= getFriction();
		speedZ *= getFriction();
	}
}
