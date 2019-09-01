package ws;

public enum DataID {
	MOVE("move"), ROTATION("rot"), ENTITY_MODELS("entity_models"), ENTITIES("entities"), POSITION("position"),
	VIEW_DIST("view_dist"), WORLD("world"), READY("ready"), TEXTURES("textures"), ZOOM("zoom"), UNZOOM("unzoom");

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
