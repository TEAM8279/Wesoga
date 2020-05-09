package wesoga.entities;

import java.util.ArrayList;

public class EntityModels {
	private static ArrayList<EntityModel> entityModels = new ArrayList<>();

	public static void register(EntityModel model) {
		if (entityModels.contains(model)) {
			throw new RuntimeException("This block model is already registered");
		}

		entityModels.add(model);
	}

	public static int getID(EntityModel model) {
		return entityModels.indexOf(model);
	}

	public static EntityModel get(int id) {
		return entityModels.get(id);
	}

	public static int count() {
		return entityModels.size();
	}
}
