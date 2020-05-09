package wesoga.entities;

import wesoga.textures.Textures;

public final class EntityModel {
	public final int northTexture;
	public final int southTexture;
	public final int eastTexture;
	public final int westTexture;
	public final int topTexture;
	public final int botTexture;

	public final double size;
	public final double height;

	public EntityModel(byte[] north, byte[] south, byte[] east, byte[] west, byte[] top, byte[] bot, double size,
			double height) {
		northTexture = Textures.textureID(north);
		southTexture = Textures.textureID(south);
		eastTexture = Textures.textureID(east);
		westTexture = Textures.textureID(west);
		topTexture = Textures.textureID(top);
		botTexture = Textures.textureID(bot);

		this.size = size;
		this.height = height;
	}

	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}
}
