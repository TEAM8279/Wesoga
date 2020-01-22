package wesoga.entities;

import java.util.ArrayList;
import java.util.HashMap;

import wesoga.textures.Textures;

public class Entities {
	private static ArrayList<EntityModel> entities = new ArrayList<>();

	private static HashMap<EntityModel, Integer> entityTexture = new HashMap<>();

	public static void registerModel(EntityModel model) {
		if (entities.contains(model)) {
			throw new RuntimeException("This tile is already registered");
		}

		entities.add(model);
	}

	public static void assignTexture(EntityModel model, byte[] texture) {
		if (!entities.contains(model)) {
			throw new RuntimeException("Tile must be registered before assigning it a texture.");
		}

		int index = Textures.textureID(texture);

		entityTexture.put(model, index);
	}

	public static int textureID(EntityModel model) {
		return entityTexture.get(model);
	}

	public static int modelID(EntityModel model) {
		int index = entities.indexOf(model);

		if (index == -1) {
			throw new RuntimeException("This entity isn't registered");
		}

		return index;
	}

	public static int count() {
		return entities.size();
	}

	public static EntityModel getModel(int index) {
		return entities.get(index);
	}
}
