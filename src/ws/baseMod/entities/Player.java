package ws.baseMod.entities;

import ws.baseMod.BaseMod;
import ws.entities.LivingEntity;

public class Player extends LivingEntity {
	public Player(double x, double y) {
		super(BaseMod.PLAYER_MODEL, 10, x, y);
	}
}
