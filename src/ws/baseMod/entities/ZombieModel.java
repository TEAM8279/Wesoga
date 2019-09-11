package ws.baseMod.entities;

import ws.entities.EntityModel;

public class ZombieModel extends EntityModel {
	@Override
	public double getSize() {
		return 1;
	}

	@Override
	public double getFriction() {
		return 0.9;
	}
}
