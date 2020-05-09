package wesoga.baseMod.entities;

import wesoga.baseMod.BaseMod;
import wesoga.entities.LivingEntity;

public class Player extends LivingEntity {
	public Player(double x, double y, double z) {
		super(BaseMod.PLAYER_MODEL, 0.7812, 1.7188, x, y, z, 10);
	}
}
