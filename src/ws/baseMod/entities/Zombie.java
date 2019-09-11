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
		final Player player = World.getNearestPlayer(x, y, 10);

		if (player == null) {
			return;
		}

		final double distX = player.getX() - x;
		final double distY = player.getY() - y;

		final double angle = Math.atan2(distY, distX);

		this.rotation = angle + Math.PI / 2;

		this.speedX += Math.cos(angle) * 0.004;

		this.speedY += Math.sin(angle) * 0.004;

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
