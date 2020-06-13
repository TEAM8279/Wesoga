package wesoga.entities;

import wesoga.World;

public abstract class LivingEntity extends Entity {
	protected int maxHP;
	protected int hp;

	protected LivingEntity(EntityModel model, double size, double height, double x, double y, double z, int maxHP) {
		super(model, size, height, x, y, z);

		this.maxHP = maxHP;
		hp = maxHP;
	}

	public void damage(int hp) {
		this.hp -= hp;

		if (this.hp <= 0) {
			this.hp = 0;
			kill();
		}
	}

	public void heal(int hp) {
		this.hp += hp;

		if (this.hp > maxHP) {
			this.hp = maxHP;
		}
	}

	public int getMaxHP() {
		return maxHP;
	}

	public int getHP() {
		return hp;
	}

	@Override
	public void onTick() {
		if (x < 2 || x >= World.SIZE - 2 || z < 2 || z >= World.SIZE - 2) {
			damage(1);
		}
	}
}
