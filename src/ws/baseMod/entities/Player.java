package ws.baseMod.entities;

import ws.baseMod.BaseMod;
import ws.entities.Entity;

public class Player extends Entity {
	public Player(double x, double y) {
		super(BaseMod.PLAYER_MODEL);
		this.x = x;
		this.y = y;
	}
}
