"use strict";
(function () {
    const canvas = document.getElementById("canvas");
    const gc = canvas.getContext("2d");
    let posX = 100.0;
    let posY = 100.0;
    let viewDistance = 5;
    let requestedViewDistance = 5;
    class Entity {
        constructor(id, x, y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
    let entities = [];
    function create2DArray(width, height) {
        let array = new Array(width);
        for (let i = 0; i < array.length; i++) {
            array[i] = new Array(height);
            for (let y = 0; y < array[i].length; y++) {
                array[i][y] = 0;
            }
        }
        return array;
    }
    let worldSize = 1;
    let world = create2DArray(1, 1);
    let tilesTextures = new Array();
    let socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port);
    socket.onopen = function (event) {
        console.log("Web socket connected");
    };
    socket.onerror = function (event) {
        console.log(event);
    };
    socket.onmessage = function (event) {
        let datas = event.data.split(";");
        if (datas[0] === 'position') {
            posX = parseFloat(datas[1]);
            posY = parseFloat(datas[2]);
        }
        else if (datas[0] === 'entities') {
            let count = parseInt(datas[1], 10);
            entities = new Array(0);
            for (let i = 0; i < count; i++) {
                entities.push(new Entity(parseInt(datas[i * 3 + 2], 10), parseFloat(datas[i * 3 + 3]), parseFloat(datas[i * 3 + 4])));
            }
        }
        else if (datas[0] === 'world') {
            worldSize = parseInt(datas[1]);
            world = create2DArray(worldSize, worldSize);
            for (let y = 0; y < worldSize; y++) {
                for (let x = 0; x < worldSize; x++) {
                    world[x][y] = parseInt(datas[2 + x + y * worldSize]);
                }
            }
        }
        else if (datas[0] === 'tiles_textures') {
            let size = parseInt(datas[1]);
            tilesTextures.length = size;
            for (let i = 0; i < size; i++) {
                tilesTextures[i] = new Image();
                tilesTextures[i].src = "tiles_textures/" + i;
            }
        }
        else if (datas[0] === 'ready') {
            ready();
        }
        else if (datas[0] === 'view_dist') {
            requestedViewDistance = parseInt(datas[1]);
        }
        else {
            console.error('Unknown data id : ' + datas[0]);
        }
    };
    socket.onclose = function (event) {
        console.log("Connection closed");
    };
    document.addEventListener("wheel", function (event) {
        if (event.deltaY < 0) {
            socket.send("unzoom");
        }
        else if (event.deltaY > 0) {
            socket.send("zoom");
        }
    });
    class Key {
        static update() {
            let accelY = 1;
            if (Key.upDown === Key.downDown) {
                accelY = 0;
            }
            else if (Key.upDown) {
                accelY = -1;
            }
            let accelX = 1;
            if (Key.leftDown === Key.rightDown) {
                accelX = 0;
            }
            else if (Key.leftDown) {
                accelX = -1;
            }
            socket.send("accel;" + accelX + ";" + accelY);
        }
        static startListening() {
            document.addEventListener('keydown', (event) => {
                const key = event.code;
                let updated = false;
                if (key === 'KeyW') {
                    if (!Key.upDown) {
                        Key.upDown = true;
                        updated = true;
                    }
                }
                else if (key === 'KeyS') {
                    if (!Key.downDown) {
                        Key.downDown = true;
                        updated = true;
                    }
                }
                else if (key === 'KeyA') {
                    if (!Key.leftDown) {
                        Key.leftDown = true;
                        updated = true;
                    }
                }
                else if (key === 'KeyD') {
                    if (!Key.rightDown) {
                        Key.rightDown = true;
                        updated = true;
                    }
                }
                if (updated) {
                    Key.update();
                }
            }, false);
            document.addEventListener('keyup', (event) => {
                const key = event.code;
                let updated = true;
                if (key === 'KeyW') {
                    Key.upDown = false;
                }
                else if (key === 'KeyS') {
                    Key.downDown = false;
                }
                else if (key === 'KeyA') {
                    Key.leftDown = false;
                }
                else if (key === 'KeyD') {
                    Key.rightDown = false;
                }
                else {
                    updated = false;
                }
                if (updated) {
                    Key.update();
                }
            }, false);
        }
    }
    Key.upDown = false;
    Key.downDown = false;
    Key.leftDown = false;
    Key.rightDown = false;
    function ready() {
        const player = new Image();
        player.src = "player.png";
        Key.startListening();
        function draw() {
            canvas.height = window.innerHeight;
            canvas.width = window.innerWidth;
            const scale = Math.max(window.innerHeight, window.innerWidth) / (viewDistance * 2);
            const halfWidth = window.innerWidth / 2;
            const halfHeight = window.innerHeight / 2;
            let camX = scale * posX - halfWidth + scale / 2;
            let camY = scale * posY - halfHeight + scale / 2;
            gc.imageSmoothingEnabled = false;
            viewDistance += (requestedViewDistance - viewDistance) * 0.1;
            let startX = Math.max(0, Math.floor(posX - viewDistance));
            let startY = Math.max(0, Math.floor(posY - viewDistance));
            let endX = Math.min(worldSize, Math.ceil(posX + viewDistance) + 1);
            let endY = Math.min(worldSize, Math.ceil(posY + viewDistance) + 1);
            for (let x = startX; x < endX; x++) {
                for (let y = startY; y < endY; y++) {
                    let tile = world[x][y];
                    let img = tilesTextures[tile];
                    gc.drawImage(img, x * scale - camX, y * scale - camY, scale, scale);
                }
            }
            for (let i = 0; i < entities.length; i++) {
                let e = entities[i];
                gc.drawImage(player, e.x * scale - camX, e.y * scale - camY, scale, scale);
            }
            gc.drawImage(player, halfWidth - scale / 2, halfHeight - scale / 2, scale, scale);
            window.requestAnimationFrame(draw);
        }
        window.requestAnimationFrame(draw);
    }
})();
