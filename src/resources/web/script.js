"use strict";
(function () {
    const loadPanel = document.getElementById("load");
    const menuPanel = document.getElementById("menu");
    menuPanel.style.display = "none";
    const canvas = document.getElementById("canvas");
    canvas.style.display = "none";
    const username = document.getElementById("username");
    const socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port);
    let inGame = false;
    function loadingMessageReader(event) {
        let datas = event.data.split(";");
        const size = parseInt(datas[0], 10);
        textures.length = size;
        for (let i = 0; i < size; i++) {
            textures[i] = new Image();
            textures[i].src = "textures/" + i;
        }
        entityModels.length = 0;
        const count = parseInt(datas[1], 10);
        for (let i = 0; i < count; i++) {
            entityModels.push(new EntityModel(parseInt(datas[i * 2 + 2], 10), parseFloat(datas[i * 2 + 3])));
        }
        worldSize = parseInt(datas[count * 2 + 2], 10);
        world = create2DArray(worldSize, worldSize);
        for (let y = 0; y < worldSize; y++) {
            for (let x = 0; x < worldSize; x++) {
                world[x][y] = parseInt(datas[count * 2 + 3 + x + y * worldSize], 10);
            }
        }
        loadPanel.style.display = "none";
        menuPanel.style.display = "block";
        socket.onmessage = loginMessageReader;
    }
    function loginMessageReader(event) {
        if (event.data === 'valid_username') {
            menuPanel.style.display = "none";
            canvas.style.display = "block";
            socket.onmessage = gameMessageReader;
            gameLoop();
        }
        else if (event.data === 'invalid_username') {
            username.value = "";
        }
    }
    function gameMessageReader(event) {
        let datas = event.data.split(";");
        if (datas[0] === 'position') {
            sPosX = parseFloat(datas[1]);
            sPosY = parseFloat(datas[2]);
        }
        else if (datas[0] === 'entities') {
            let count = parseInt(datas[1], 10);
            let newEntities = [];
            for (let i = 0; i < count; i++) {
                newEntities.push(new Entity(parseInt(datas[i * 5 + 2], 10), parseInt(datas[i * 5 + 3], 10), parseFloat(datas[i * 5 + 4]), parseFloat(datas[i * 5 + 5]), parseFloat(datas[i * 5 + 6])));
            }
            for (let i = 0; i < entities.length; i++) {
                let oldEntity = entities[i];
                for (let j = 0; j < newEntities.length; j++) {
                    let newEntity = newEntities[j];
                    if (newEntity.id === oldEntity.id && newEntity.modelID === newEntity.modelID) {
                        newEntity.rX = oldEntity.rX;
                        newEntity.rY = oldEntity.rY;
                        break;
                    }
                }
            }
            entities = newEntities;
        }
        else if (datas[0] === 'view_dist') {
            sViewDistance = parseInt(datas[1], 10);
        }
        else if (datas[0] === 'health') {
            maxHP = parseInt(datas[1], 10);
            hp = parseInt(datas[2], 10);
        }
        else if (datas[0] === 'load') {
            load = parseFloat(datas[1]);
            if (load === 0) {
                rLoad = 0;
            }
        }
        else {
            console.error('Unknown data id : ' + datas[0]);
        }
    }
    socket.onerror = function (event) {
        console.log(event);
    };
    socket.onopen = function () {
        console.log("Web socket connected");
    };
    socket.onmessage = loadingMessageReader;
    socket.onclose = function () {
        console.log("Connection closed");
    };
    const gc = canvas.getContext("2d");
    const animSpeed = 0.2;
    let sPosX = 1.0;
    let sPosY = 1.0;
    let rPosX = 1.0;
    let rPosY = 1.0;
    let rotation = 0;
    let rViewDistance = 5;
    let sViewDistance = 5;
    let hp = 10;
    let maxHP = 10;
    let rHP = 10;
    let load = 0;
    let rLoad = 0;
    class EntityModel {
        constructor(textureID, size) {
            this.textureID = textureID;
            this.size = size;
        }
    }
    class Entity {
        constructor(id, modelID, x, y, rot) {
            this.id = id;
            this.modelID = modelID;
            this.x = x;
            this.y = y;
            this.rX = x;
            this.rY = y;
            this.rot = rot;
        }
        updateAnimation() {
            this.rX += (this.x - this.rX) * animSpeed;
            this.rY += (this.y - this.rY) * animSpeed;
        }
    }
    const entityModels = [];
    let entities = [];
    function create2DArray(width, height) {
        let array = new Array(width);
        for (let i = 0; i < array.length; i++) {
            array[i] = new Array(height);
        }
        return array;
    }
    let worldSize = 1;
    let world = create2DArray(1, 1);
    const textures = [];
    window.onmousemove = function (event) {
        rotation = Math.atan2(canvas.width / 2 - event.x, canvas.height / 2 - event.y);
    };
    window.onmousedown = function () {
        if (inGame) {
            socket.send("primary;1;" + rotation);
        }
    };
    window.onmouseup = function () {
        if (inGame) {
            socket.send("primary;0;" + rotation);
        }
    };
    window.oncontextmenu = function (event) {
        event.preventDefault();
    };
    function drawRotatedImage(image, x, y, width, height, angle) {
        gc.translate(x + width / 2, y + height / 2);
        gc.rotate(-angle);
        gc.drawImage(image, -width / 2, -height / 2, width, height);
        gc.rotate(angle);
        gc.translate(-x - width / 2, -y - height / 2);
    }
    window.onwheel = function (event) {
        if (event.deltaY < 0) {
            socket.send("unzoom");
        }
        else if (event.deltaY > 0) {
            socket.send("zoom");
        }
    };
    class Key {
        static update() {
            let accelY = 0;
            if (Key.downDown) {
                accelY++;
            }
            if (Key.upDown) {
                accelY--;
            }
            let accelX = 0;
            if (Key.rightDown) {
                accelX++;
            }
            if (Key.leftDown) {
                accelX--;
            }
            if (inGame) {
                socket.send("move;" + accelX + ";" + accelY);
            }
        }
        static startListening() {
            window.onkeydown = function (event) {
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
            };
            window.onkeyup = function (event) {
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
                else if (key === 'Enter') {
                    if (!inGame) {
                        socket.send("username;" + username.value);
                    }
                }
                else {
                    updated = false;
                }
                if (updated) {
                    Key.update();
                }
            };
        }
    }
    Key.upDown = false;
    Key.downDown = false;
    Key.leftDown = false;
    Key.rightDown = false;
    Key.startListening();
    function gameLoop() {
        inGame = true;
        function draw() {
            socket.send("rot;" + rotation);
            canvas.height = window.innerHeight;
            canvas.width = window.innerWidth;
            rViewDistance += (sViewDistance - rViewDistance) * animSpeed;
            rPosX += (sPosX - rPosX) * animSpeed;
            rPosY += (sPosY - rPosY) * animSpeed;
            rHP += (hp - rHP) * animSpeed;
            rLoad += (load - rLoad) * animSpeed;
            let startX = Math.max(0, Math.floor(rPosX - rViewDistance));
            let startY = Math.max(0, Math.floor(rPosY - rViewDistance));
            let endX = Math.min(worldSize, Math.ceil(rPosX + rViewDistance) + 1);
            let endY = Math.min(worldSize, Math.ceil(rPosY + rViewDistance) + 1);
            const scale = Math.max(canvas.width, canvas.height) / (rViewDistance * 2);
            const halfWidth = canvas.width / 2;
            const halfHeight = canvas.height / 2;
            let camX = scale * rPosX - halfWidth + scale / 2;
            let camY = scale * rPosY - halfHeight + scale / 2;
            gc.imageSmoothingEnabled = false;
            for (let x = startX; x < endX; x++) {
                for (let y = startY; y < endY; y++) {
                    let tile = world[x][y];
                    let img = textures[tile];
                    gc.drawImage(img, x * scale - camX, y * scale - camY, scale + 1, scale + 1);
                }
            }
            for (let i = 0; i < entities.length; i++) {
                let entity = entities[i];
                entity.updateAnimation();
                let model = entityModels[entity.modelID];
                let texture = textures[model.textureID];
                drawRotatedImage(texture, entity.rX * scale - camX, entity.rY * scale - camY, model.size * scale, model.size * scale, entity.rot);
            }
            drawRotatedImage(textures[entityModels[0].textureID], halfWidth - scale / 2, halfHeight - scale / 2, 0.9 * scale, 0.9 * scale, rotation);
            gc.fillStyle = "#882222";
            gc.fillRect(50, canvas.height - 100, 200, 50);
            gc.fillStyle = "#ff4444";
            gc.fillRect(50, canvas.height - 100, rHP / maxHP * 200, 50);
            if (load > 0) {
                gc.fillStyle = "#222288";
                gc.fillRect(300, canvas.height - 100, 200, 50);
                gc.fillStyle = "#4444ff";
                gc.fillRect(300, canvas.height - 100, rLoad * 200, 50);
            }
            window.requestAnimationFrame(draw);
        }
        window.requestAnimationFrame(draw);
    }
})();
