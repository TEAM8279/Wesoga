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

  class EntityModel {
    public textureID:number

    public size:number;

    constructor(textureID:number, size:number) {
      this.textureID = textureID;
      this.size = size;
    }
  }

  class Entity {
    public id:number;

    public modelID:number;

    public x:number;
    public y:number;

    public rX:number;
    public rY:number;

    public rot:number;

    constructor(id:number, modelID:number, x:number, y:number, rot:number) {
      this.id = id;

      this.modelID = modelID;

      this.x = x;
      this.y = y;

      this.rX = x;
      this.rY = y;

      this.rot = rot;
    }

    public updateAnimation() {
      if(smoothMoves) {
        this.rX += (this.x - this.rX) * 0.2;
        this.rY += (this.y - this.rY) * 0.2;
      } else {
        this.rX = this.x;
        this.rY = this.y;
      }
    }
  }

  const entityModels:EntityModel[] = [];

  let entities:Entity[] = [];

  function create2DArray(width:number, height:number):number[][] {
    let array:number[][] = new Array(width)

    for(let i = 0; i < array.length; i++) {
      array[i] = new Array(height);
    }

    return array;
  }

  let worldSize = 1;
  let world = create2DArray(1, 1);

  const textures:HTMLImageElement[] = [];

  canvas.onmousemove = function(event) {
    rotation = Math.atan2(canvas.width / 2 - event.x, event.y - canvas.height / 2) + Math.PI;

    socket.send("rot;" + rotation);
  }

  canvas.oncontextmenu = function(event) {
    event.preventDefault();
  }

  function drawRotatedImage(image:HTMLImageElement, x:number, y:number, width:number, height:number, angle:number) {
    gc.translate(x + width/2, y + height/2);
    gc.rotate(angle);
    gc.drawImage(image, -width/2, -height/2, width, height);
    gc.rotate(-angle);
    gc.translate(-x - width/2, -y - height/2);
  }

  let socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port);
  socket.onopen = function () {
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
        newEntities.push(new Entity(parseInt(datas[i * 5 + 2], 10), parseInt(datas[i * 5 + 3], 10), parseFloat(datas[i * 5 + 4]), parseFloat(datas[i * 5 + 5]), parseFloat(datas[i * 5 + 6])));
      }

      for(let i = 0; i < entities.length; i++) {
        let oldEntity = entities[i];

        for(let j = 0; j < newEntities.length; j++) {
          let newEntity = newEntities[j];

          if(newEntity.id === oldEntity.id && newEntity.modelID === newEntity.modelID) {
            newEntity.rX = oldEntity.rX;
            newEntity.rY = oldEntity.rY;
            break;
          }
        }
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
    } else if(datas[0] === 'textures') {
      let size = parseInt(datas[1], 10);

      textures.length = size;

      for(let i = 0; i < size; i++) {
        textures[i] = new Image();
        textures[i].src = "textures/"+i;
      }
    } else if(datas[0] === 'entity_models') {
      entityModels.length = 0;

      let count = parseInt(datas[1], 10);

      for(let i = 0; i < count; i++) {
        entityModels.push(new EntityModel(parseInt(datas[i * 2 + 2], 10), parseFloat(datas[i * 2 + 3])));
      }
    } else if(datas[0] === 'ready') {
      ready();
    } else if(datas[0] === 'view_dist') {
      sViewDistance = parseInt(datas[1], 10);
    } else {
      console.error('Unknown data id : ' + datas[0]);
    }
  }

  socket.onclose = function () {
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

            let img = textures[tile];

            gc.drawImage(img, x * scale - camX, y * scale - camY, scale + 1, scale + 1);
          }
        }

        for(let i = 0; i < entities.length; i++) {
          let entity = entities[i];

          entity.updateAnimation();

          let model = entityModels[entity.modelID];
          let texture = textures[model.textureID];

          drawRotatedImage(texture, entity.rX * scale - camX, entity.rY * scale - camY, model.size * scale, model.size * scale, entity.rot);
        }

        drawRotatedImage(textures[entityModels[0].textureID], halfWidth - scale / 2, halfHeight - scale / 2, scale, scale, rotation);

        window.requestAnimationFrame(draw);
    }

    window.requestAnimationFrame(draw);
  }
})();
