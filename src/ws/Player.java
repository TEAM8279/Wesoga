package ws;

public class Player extends Entity {
	private double speedX = 0;
	private double speedY = 0;

	private double x;
	private double y;

	public Player(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void tick() {
		speedX *= 0.9;
		speedY *= 0.9;

		this.x += speedX;
		this.y += speedY;
	}

	public void accelX(double value) {
		speedX += value;
	}

	public void accelY(double value) {
		speedY += value;
	}

	@Override
	public double getSize() {
		return 1;
	}
}
