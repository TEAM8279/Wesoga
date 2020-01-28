"use strict";
var World = /** @class */ (function () {
    function World() {
    }
    World.init = function () {
        for (var x = World.SIZE - 1; x >= 0; x--) {
            for (var z = World.SIZE - 1; z >= 0; z--) {
                var height = Math.floor((World.noise.noise2D(x / 40, z / 40) + 1) / 2 * World.HEIGHT);
                for (var y = height - 1; y >= 0; y--) {
                    World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE] = 1;
                }
            }
        }
    };
    World.get = function (x, y, z) {
        if (x >= World.SIZE || x < 0 || y >= World.HEIGHT || y < 0 || z >= World.SIZE || z < 0) {
            return 0;
        }
        return World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE];
    };
    World.set = function (x, y, z, value) {
        if (x >= World.SIZE || x < 0 || y >= World.HEIGHT || y < 0 || z >= World.SIZE || z < 0) {
            throw "Index out of bounds";
        }
        World.cubes[x + y * World.SIZE + z * World.HEIGHT * World.SIZE] = value;
    };
    World.HEIGHT = 256;
    World.SIZE = 256;
    World.cubes = new Uint8Array(World.SIZE * World.SIZE * World.HEIGHT);
    World.noise = openSimplexNoise(42);
    return World;
}());
World.init();
