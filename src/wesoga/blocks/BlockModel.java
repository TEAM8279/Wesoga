package wesoga.blocks;

import wesoga.textures.Textures;

public final class BlockModel {
	public final boolean visible;

	public final int northTexture;
	public final int southTexture;
	public final int eastTexture;
	public final int westTexture;
	public final int topTexture;
	public final int botTexture;

	public BlockModel() {
		visible = false;

		northTexture = -1;
		southTexture = -1;
		eastTexture = -1;
		westTexture = -1;
		topTexture = -1;
		botTexture = -1;
	}

	public BlockModel(byte[] texture) {
		this.visible = true;

		int id = Textures.textureID(texture);

		northTexture = id;
		southTexture = id;
		eastTexture = id;
		westTexture = id;
		topTexture = id;
		botTexture = id;
	}

	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}
}
