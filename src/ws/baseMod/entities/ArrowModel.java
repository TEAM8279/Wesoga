package ws.baseMod.entities;

import ws.entities.EntityModel;

public class ArrowModel extends EntityModel {
	@Override
	public double getSize() {
		return 0.5;
	}

	@Override
	public double getFriction() {
		return 0.99;
	}
}
