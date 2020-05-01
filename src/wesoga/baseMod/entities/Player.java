package wesoga.baseMod.entities;

import wesoga.baseMod.BaseMod;
import wesoga.entities.LivingEntity;

public class Player extends LivingEntity {
	public Player(double x, double y, double z) {
		super(BaseMod.PLAYER_MODEL, 10, x, y, z);
	}
}
