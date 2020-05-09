package wesoga;

public enum DataID {
	MOVE("move"), ROTATION("rotation"), ENTITIES("entities"), POSITION("position"), HEALTH("health"),
	PRIMARY("primary"), USERNAME("username"), VALID_USERNAME("valid_username"), INVALID_USERNAME("invalid_username"),
	DEAD("dead"), LOAD_TEXTURES("load_textures"), LOAD_BLOCK_MODELS("load_block_models"),
	LOAD_ENTITY_MODELS("load_entity_models"), LOAD_WORLD("load_world"), LOAD_FINISHED("load_finished");

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
