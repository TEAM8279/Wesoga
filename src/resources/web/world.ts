class World {
	static HEIGHT = 64;
	static SIZE = 256;
	static cubes = new Uint8Array(World.SIZE * World.SIZE * World.HEIGHT);
	
	static get(x: number, y: number, z: number) {
		if (x >= World.SIZE || x < 0 || y >= World.HEIGHT || y < 0 || z >= World.SIZE || z < 0) {
			return 0;
		}

		return World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE];
	}

	static set(x: number, y: number, z: number, value: number) {
		if (x >= World.SIZE || x < 0 || y >= World.HEIGHT || y < 0 || z >= World.SIZE || z < 0) {
			throw "Index out of bounds";
		}

		World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE] = value;
	}
}

