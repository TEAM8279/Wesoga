package ws.baseMod.entities;

import ws.baseMod.BaseMod;
import ws.entities.Entity;
import ws.entities.LivingEntity;

public class Arrow extends Entity {
	public Arrow(double x, double y, double direction) {
		super(BaseMod.ARROW_MODEL, x, y);

		this.rotation = direction;

		double rot = rotation - Math.PI / 2;

		this.accel(Math.cos(rot) * 0.2, Math.sin(rot) * 0.2);
	}

	@Override
	public void onTick() {
		rotation = Math.PI - Math.atan2(speedX, speedY);

		if (speedX * speedX + speedY * speedY < 0.001) {
			alive = false;
		}
	}

	@Override
	public void onCollision(Entity collidedEntity) {
		if (collidedEntity instanceof LivingEntity) {
			((LivingEntity) collidedEntity).damage(2);
		}

		alive = false;
	}

	@Override
	public boolean canWalk() {
		return false;
	}

	@Override
	public boolean canFly() {
		return true;
	}

	@Override
	public double getFriction() {
		return 0.99;
	}
}
