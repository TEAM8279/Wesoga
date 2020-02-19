package wesoga;

public enum DataID {
	MOVE("move"), ROTATION("rotation"), ENTITIES("entities"), POSITION("position"), VIEW_DIST("view_dist"),
	START("start"), ZOOM("zoom"), UNZOOM("unzoom"), HEALTH("health"), PRIMARY("primary"), LOAD("load"),
	USERNAME("username"), VALID_USERNAME("valid_username"), INVALID_USERNAME("invalid_username"), DEAD("dead");

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
