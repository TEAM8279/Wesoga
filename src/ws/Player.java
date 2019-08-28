package ws;

public class Player extends Entity {

	public Player(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public double getSize() {
		return 1;
	}
}
