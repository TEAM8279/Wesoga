class EntityModel {
	constructor(northTexture: number, southTexture: number, eastTexture: number, westTexture: number, topTexture: number, botTexture: number, size: number, height: number) {
		this.northTexture = northTexture;
		this.southTexture = southTexture;
		this.eastTexture = eastTexture;
		this.westTexture = westTexture;
		this.topTexture = topTexture;
		this.botTexture = botTexture;

		this.size = size;
		this.height = height;
	}

	public northTexture: number;
	public southTexture: number;
	public eastTexture: number;
	public westTexture: number;
	public topTexture: number;
	public botTexture: number;

	public size: number;
	public height: number;
}
