package wesoga.entities;

public final class EntityModel {
	private final double height;
	private final double width;

	public EntityModel(double height, double width) {
		this.height = height;
		this.width = width;
	}

	/**
	 * @return height of the entity with this model
	 */
	public final double getHeight() {
		return height;
	}

	/**
	 * @return width of the entity with this model
	 */
	public final double getWidth() {
		return width;
	}
}
