package wesoga;

public enum DataID {
	MOVE("move"), ROTATION("rotation"), ENTITIES("entities"), POSITION("position"), HEALTH("health"),
	PRIMARY("primary"), LOAD_WORLD("load_world"), LOGIN("login"), VALID_USERNAME("valid_username"),
	INVALID_USERNAME("invalid_username"), DEATH("death"), LOAD_TEXTURES("load_textures"),
	LOAD_BLOCK_MODELS("load_block_models"), LOAD_ENTITY_MODELS("load_entity_models");

	private final String value;

	private DataID(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public boolean same(String value) {
		return this.value.contentEquals(value);
	}
}
