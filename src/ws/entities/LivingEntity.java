package ws.entities;

public abstract class LivingEntity extends Entity {
	protected int maxHP;
	protected int hp;

	protected LivingEntity(EntityModel model, int maxHP) {
		super(model);

		this.maxHP = maxHP;
		hp = maxHP;
	}

	public void damage(int hp) {
		this.hp -= hp;

		if (this.hp < 0) {
			alive = false;
		}
	}

	public void heal(int hp) {
		this.hp += hp;

		if (this.hp > maxHP) {
			this.hp = maxHP;
		}
	}
}
