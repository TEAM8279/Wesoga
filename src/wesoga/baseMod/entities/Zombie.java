package wesoga.baseMod.entities;

import wesoga.World;
import wesoga.baseMod.BaseMod;
import wesoga.entities.Entity;
import wesoga.entities.LivingEntity;

public class Zombie extends LivingEntity {
	private int attackTimer = 100;

	public Zombie(double x, double y, double z) {
		super(BaseMod.ZOMBIE_MODEL, 0.7812, 1.7188, x, y, z, 10);
	}

	@Override
	public void onTick() {
		super.onTick();

		Player p = World.getNearestPlayer(x, y, z, 100);

		if (p != null) {
			final double distX = x - p.getX();
			final double distZ = z - p.getZ();

			rotation = Math.atan2(distX, distZ);

			speedX -= Math.sin(rotation) * 0.003;
			speedZ -= Math.cos(rotation) * 0.003;

			boolean blockAround = touchEast() || touchWest() || touchNorth() || touchSouth();

			if (blockAround && this.touchDown()) {
				speedY += 0.09;
			}
		}

		if (attackTimer > 0) {
			attackTimer--;
		}
	}

	@Override
	public void onCollision(Entity collider) {
		if (collider instanceof Player) {
			if (attackTimer == 0) {
				attackTimer = 100;
				((Player) collider).damage(1);
			}
		}
	}
}
