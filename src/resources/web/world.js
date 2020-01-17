class World {
    static init() {
        World.HEIGHT = 256;
        World.SIZE = 256;
        World.cubes = new Uint8Array(World.SIZE * World.SIZE * World.HEIGHT);
        World.noise = openSimplexNoise(42);

        for (let x = World.SIZE - 1; x >= 0; x--) {
            for (let z = World.SIZE - 1; z >= 0; z--) {
                let height = Math.floor((World.noise.noise2D(x / 40, z / 40) + 1) / 2 * World.HEIGHT);
                for (let y = height - 1; y >= 0; y--) {
                    World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE] = 1;
                }
            }
        }
    }

    static get(x, y, z) {
        if (x >= World.SIZE || x < 0 || y >= World.HEIGHT || y < 0 || z >= World.SIZE || z < 0) {
            return 0;
        }

        return World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE];
    }

    static set(x, y, z, value) {
        if (x >= World.SIZE || x < 0 || y >= World.HEIGHT || y < 0 || z >= World.SIZE || z < 0) {
            throw "Index out of bounds";
        }

        World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE] = value;
    }
}
World.init();
