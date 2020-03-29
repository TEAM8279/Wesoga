package wesoga.baseMod;

import wesoga.baseMod.blocks.Air;
import wesoga.baseMod.blocks.Grass;
import wesoga.entities.EntityModel;

public class BaseMod {
	public static final Air AIR = new Air();
	public static final Grass GRASS = new Grass();

	public static final EntityModel PLAYER_MODEL = new EntityModel(1.8, 0.5);

	public static void load() {

	}
}
