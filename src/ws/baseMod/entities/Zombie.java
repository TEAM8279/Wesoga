package ws.baseMod.entities;

import ws.World;
import ws.baseMod.BaseMod;
import ws.entities.Entity;
import ws.entities.LivingEntity;

public class Zombie extends LivingEntity {
	private int attackTimer = 100;

	public Zombie(int x, int y) {
		super(BaseMod.ZOMBIE_MODEL, 5, x, y);
	}

	@Override
	public void onTick() {
		final Player player = World.getNearestPlayer(x, y, 20);

		if (player == null) {
			return;
		}

		final double distX = x - player.getX();
		final double distY = y - player.getY();

		rotation = Math.atan2(distX, distY);

		speedX -= Math.sin(rotation) * 0.004;

		speedY -= Math.cos(rotation) * 0.004;

		if (attackTimer > 0) {
			attackTimer--;
		}
	}

	@Override
	public void onCollision(Entity e) {
		if (e instanceof Player) {
			if (attackTimer == 0) {
				attackTimer = 100;
				((Player) e).damage(1);
			}
		}
	}
}
