"use strict";
(function() {
  const canvas = document.getElementById("canvas") as HTMLCanvasElement;

  const gc = canvas.getContext("2d");

  const smoothMoves:boolean = true;

  let sPosX = 1.0;
  let sPosY = 1.0;

  let rPosX = 1.0;
  let rPosY = 1.0;

  let rotation = 0;

  let rViewDistance = 5;
  let sViewDistance = 5;

  class Entity {
    public id: number;

    public x: number;
    public y: number;

    public rot:number;

    constructor(id:number, x:number, y:number, rot:number) {
      this.id = id;

      this.x = x;
      this.y = y;

      this.rot = rot;
    }
  }

  let entities:Entity[] = [];

  function create2DArray(width:number, height:number):number[][] {
    let array:number[][] = new Array(width)

    for(let i = 0; i < array.length; i++) {
      array[i] = new Array(height);
      for(let y = 0; y < array[i].length; y++) {
        array[i][y] = 0;
      }
    }

    return array;
  }

  let worldSize = 1;
  let world = create2DArray(1, 1);

  let tilesTextures:HTMLImageElement[] = new Array();

  canvas.onmousemove = function(event) {
    rotation = Math.atan2(canvas.width / 2 - event.x, event.y - canvas.height / 2) + Math.PI;

    socket.send("rot;" + rotation);
  }

  function drawRotatedImage(image:HTMLImageElement, x:number, y:number, width:number, height:number, angle:number) {
    gc.translate(x + width/2, y + height/2);
    gc.rotate(angle);
    gc.drawImage(image, -width/2, -height/2, width, height);
    gc.rotate(-angle);
    gc.translate(-x - width/2, -y - height/2);
  }

  let socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port);
  socket.onopen = function (event) {
    console.log("Web socket connected");
  }

  socket.onerror = function (event) {
    console.log(event);
  }

  socket.onmessage = function (event) {
    let datas = event.data.split(";");

    if(datas[0] === 'position') {
      sPosX = parseFloat(datas[1]);
      sPosY = parseFloat(datas[2]);
    } else if(datas[0] === 'entities') {
      let count = parseInt(datas[1], 10);

      let newEntities:Entity[] = [];
      for(let i = 0; i < count; i++) {
        newEntities.push(new Entity(parseInt(datas[i * 4 + 2], 10), parseFloat(datas[i * 4 + 3]), parseFloat(datas[i * 4 + 4]), parseFloat(datas[i * 4 + 5])));
      }

      entities = newEntities;
    } else if(datas[0] === 'world') {
      worldSize = parseInt(datas[1], 10);

      world = create2DArray(worldSize, worldSize);

      for(let y = 0; y < worldSize; y++) {
        for(let x = 0; x < worldSize; x++) {
            world[x][y] = parseInt(datas[2 + x + y * worldSize], 10);
        }
      }
    } else if(datas[0] === 'tiles_textures') {
      let size = parseInt(datas[1], 10);

      tilesTextures.length = size;

      for(let i = 0; i < size; i++) {
        tilesTextures[i] = new Image();
        tilesTextures[i].src = "tiles_textures/"+i;
      }
    } else if(datas[0] === 'ready') {
      ready();
    } else if(datas[0] === 'view_dist') {
      sViewDistance = parseInt(datas[1], 10);
    } else {
      console.error('Unknown data id : ' + datas[0]);
    }
  }

  socket.onclose = function (event) {
    console.log("Connection closed");
  }

  document.addEventListener("wheel", function(event) {
    if(event.deltaY < 0) {
      socket.send("unzoom");
    } else if(event.deltaY > 0) {
      socket.send("zoom");
    }
  });

  class Key {
    static upDown = false;
    static downDown = false;
    static leftDown = false;
    static rightDown = false;

    static update() {
      let accelY = 1;

      if(Key.upDown === Key.downDown) {
        accelY = 0;
      } else if(Key.upDown) {
        accelY = -1;
      }

      let accelX = 1;

      if(Key.leftDown === Key.rightDown) {
        accelX = 0;
      } else if(Key.leftDown) {
        accelX = -1;
      }

      socket.send("move;"+accelX+";"+accelY);
    }

    static startListening() {
      document.addEventListener('keydown', (event) => {
        const key = event.code

        let updated = false;

        if(key === 'KeyW') {
          if(!Key.upDown) {
            Key.upDown = true;
            updated = true;
          }
        } else if(key === 'KeyS') {
          if(!Key.downDown) {
            Key.downDown = true;
            updated = true;
          }
        } else if(key === 'KeyA') {
          if(!Key.leftDown) {
            Key.leftDown = true;
            updated = true;
          }
        } else if(key === 'KeyD') {
          if(!Key.rightDown) {
            Key.rightDown = true;
            updated = true;
          }
        }

        if(updated) {
          Key.update();
        }
      }, false);

      document.addEventListener('keyup', (event) => {
        const key = event.code;

        let updated = true;

        if(key === 'KeyW') {
          Key.upDown = false;
        } else if(key === 'KeyS') {
          Key.downDown = false;
        } else if(key === 'KeyA') {
          Key.leftDown = false;
        } else if(key === 'KeyD') {
          Key.rightDown = false;
        } else {
          updated = false;
        }

        if(updated) {
          Key.update();
        }
      }, false);
    }
  }

  function ready() {
    const player = new Image();
    player.src = "player.png";

    Key.startListening();

    function draw() {
        canvas.height = window.innerHeight;
        canvas.width = window.innerWidth;

        if(smoothMoves) {
          rViewDistance += (sViewDistance - rViewDistance) * 0.1;

          rPosX += (sPosX - rPosX) * 0.2;
          rPosY += (sPosY - rPosY) * 0.2;
        } else {
          rViewDistance = sViewDistance;
          rPosX = sPosX;
          rPosY = sPosY;
        }

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

        for(let x = startX; x < endX; x++) {
          for(let y = startY; y < endY; y++) {
            let tile = world[x][y];

            let img = tilesTextures[tile];

            gc.drawImage(img, x * scale - camX, y * scale - camY, scale + 1, scale + 1);
          }
        }

        for(let i = 0; i < entities.length; i++) {
          let e = entities[i];
          drawRotatedImage(player, e.x * scale - camX, e.y * scale - camY, scale, scale, e.rot);
        }

        drawRotatedImage(player, halfWidth - scale / 2, halfHeight - scale / 2, scale, scale, rotation);

        window.requestAnimationFrame(draw);
    }

    window.requestAnimationFrame(draw);
  }
})();
