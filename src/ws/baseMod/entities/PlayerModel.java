package ws.baseMod.entities;

import ws.entities.EntityModel;

public class PlayerModel extends EntityModel {
	@Override
	public double getSize() {
		return 1;
	}

	@Override
	public double getFriction() {
		return 0.9;
	}
}
