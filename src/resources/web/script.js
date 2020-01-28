"use strict";
(function () {
    var canvas = document.getElementById("canvas");
    var gl = canvas.getContext("webgl2");
    // Only continue if WebGL is available and working
    if (gl === null) {
        alert("Unable to initialize WebGL. Your browser or machine may not support it.");
        return;
    }
    // Vertex shader program
    var vsSource = "\n    attribute vec4 aVertexPosition;\n    attribute float aVertexColor;\n    attribute vec2 aVertexTexture;\n\n    uniform mat4 uViewMatrix;\n\n    varying lowp float vColor;\n    varying lowp vec2 vTexture;\n\n    void main() {\n      gl_Position = uViewMatrix * aVertexPosition;  \n\n      vColor = aVertexColor;\n      vTexture = aVertexTexture;\n    }\n  ";
    var fsSource = "\n    varying lowp float vColor;\n    varying lowp vec2 vTexture;\n\n    uniform sampler2D uSampler;\n\n    void main() {\n      lowp vec4 color = texture2D(uSampler, vTexture);\n      color.rgb *= vColor;\n\n      gl_FragColor = color;\n    }\n  ";
    function loadTexture(url) {
        var texture = gl.createTexture();
        gl.bindTexture(gl.TEXTURE_2D, texture);
        // Du fait que les images doivent être téléchargées depuis l'internet,
        // il peut s'écouler un certain temps avant qu'elles ne soient prêtes.
        // Jusque là, mettre un seul pixel dans la texture, de sorte que nous puissions
        // l'utiliser immédiatement. Quand le téléchargement de la page sera terminé,
        // nous mettrons à jour la texture avec le contenu de l'image.
        var level = 0;
        var internalFormat = gl.RGBA;
        var width = 1;
        var height = 1;
        var border = 0;
        var srcFormat = gl.RGBA;
        var srcType = gl.UNSIGNED_BYTE;
        var pixel = new Uint8Array([255, 0, 255, 255]); // bleu opaque
        gl.texImage2D(gl.TEXTURE_2D, level, internalFormat, width, height, border, srcFormat, srcType, pixel);
        var image = new Image();
        image.onload = function () {
            gl.bindTexture(gl.TEXTURE_2D, texture);
            var texSize = 128;
            var canvas = document.createElement("canvas");
            canvas.height = texSize;
            canvas.width = texSize;
            var gc = canvas.getContext("2d");
            gc.imageSmoothingEnabled = false;
            gc.drawImage(image, 0, 0, texSize, texSize);
            gl.texImage2D(gl.TEXTURE_2D, level, internalFormat, srcFormat, srcType, canvas);
            gl.generateMipmap(gl.TEXTURE_2D);
        };
        image.src = url;
        return texture;
    }
    function initShaderProgram(vsSource, fsSource) {
        var vertexShader = loadShader(gl.VERTEX_SHADER, vsSource);
        var fragmentShader = loadShader(gl.FRAGMENT_SHADER, fsSource);
        // Create the shader program
        var shaderProgram = gl.createProgram();
        gl.attachShader(shaderProgram, vertexShader);
        gl.attachShader(shaderProgram, fragmentShader);
        gl.linkProgram(shaderProgram);
        // If creating the shader program failed, alert
        if (!gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)) {
            alert('Unable to initialize the shader program: ' + gl.getProgramInfoLog(shaderProgram));
            return null;
        }
        return shaderProgram;
    }
    var shaderProgram = initShaderProgram(vsSource, fsSource);
    //
    // creates a shader of the given type, uploads the source and
    // compiles it.
    //
    function loadShader(type, source) {
        var shader = gl.createShader(type);
        // Send the source to the shader object
        gl.shaderSource(shader, source);
        // Compile the shader program
        gl.compileShader(shader);
        // See if it compiled successfully
        if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
            alert('An error occurred compiling the shaders: ' + gl.getShaderInfoLog(shader));
            gl.deleteShader(shader);
            return null;
        }
        return shader;
    }
    var programInfo = {
        program: shaderProgram,
        attribLocations: {
            vertexPosition: gl.getAttribLocation(shaderProgram, 'aVertexPosition'),
            vertexColor: gl.getAttribLocation(shaderProgram, 'aVertexColor'),
            vertexTexture: gl.getAttribLocation(shaderProgram, 'aVertexTexture')
        },
        uniformLocations: {
            uViewMatrix: gl.getUniformLocation(shaderProgram, 'uViewMatrix'),
            uSampler: gl.getUniformLocation(shaderProgram, 'uSampler')
        },
    };
    console.log(programInfo.attribLocations.vertexPosition);
    function createFrontFace(x, y, z) {
        return [
            x, y, z + 1,
            x + 1, y, z + 1,
            x + 1, y + 1, z + 1,
            x, y + 1, z + 1
        ];
    }
    function createBackFace(x, y, z) {
        return [
            x, y, z,
            x, y + 1, z,
            x + 1, y + 1, z,
            x + 1, y, z
        ];
    }
    function createTopFace(x, y, z) {
        return [
            x, y + 1, z,
            x + 1, y + 1, z,
            x + 1, y + 1, z + 1,
            x, y + 1, z + 1
        ];
    }
    function createBottomFace(x, y, z) {
        return [
            x, y, z,
            x + 1, y, z,
            x + 1, y, z + 1,
            x, y, z + 1
        ];
    }
    function createRightFace(x, y, z) {
        return [
            x + 1, y, z,
            x + 1, y + 1, z,
            x + 1, y + 1, z + 1,
            x + 1, y, z + 1
        ];
    }
    function createLeftFace(x, y, z) {
        return [
            x, y, z,
            x, y, z + 1,
            x, y + 1, z + 1,
            x, y + 1, z
        ];
    }
    function createFrontColor() {
        var c = 0.9;
        return [c, c, c, c];
    }
    function createBackColor() {
        var c = 0.9;
        return [c, c, c, c];
    }
    function createTopColor() {
        var c = 1;
        return [c, c, c, c];
    }
    function createBottomColor() {
        var c = 1;
        return [c, c, c, c];
    }
    function createRightColor() {
        var c = 0.8;
        return [c, c, c, c];
    }
    function createLeftColor() {
        var c = 0.8;
        return [c, c, c, c];
    }
    function createFrontTexture() {
        return [
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0,
        ];
    }
    function createBackTexture() {
        return [
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0,
        ];
    }
    function createTopTexture() {
        return [
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0,
        ];
    }
    function createBottomTexture() {
        return [
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0,
        ];
    }
    function createRightTexture() {
        return [
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0,
        ];
    }
    function createLeftTexture() {
        return [
            0.0, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0,
        ];
    }
    function createIndices(i) {
        return [
            i + 0, i + 1, i + 2, i + 0, i + 2, i + 3
        ];
    }
    var pointsCount = 0;
    function initBuffers() {
        var positions = [];
        var colors = [];
        var textures = [];
        var indices = [];
        var index = 0;
        pointsCount = 0;
        for (var z_1 = World.SIZE - 1; z_1 >= 0; z_1--) {
            for (var y_1 = World.HEIGHT - 1; y_1 >= 0; y_1--) {
                for (var x_1 = World.SIZE - 1; x_1 >= 0; x_1--) {
                    if (World.get(x_1, y_1, z_1) !== 0) {
                        if (World.get(x_1 + 1, y_1, z_1) === 0) {
                            positions.push.apply(positions, createRightFace(x_1, y_1, z_1));
                            colors.push.apply(colors, createRightColor());
                            textures.push.apply(textures, createRightTexture());
                            indices.push.apply(indices, createIndices(index));
                            index += 4;
                            pointsCount += 6;
                        }
                        if (World.get(x_1 - 1, y_1, z_1) === 0) {
                            positions.push.apply(positions, createLeftFace(x_1, y_1, z_1));
                            colors.push.apply(colors, createLeftColor());
                            textures.push.apply(textures, createLeftTexture());
                            indices.push.apply(indices, createIndices(index));
                            index += 4;
                            pointsCount += 6;
                        }
                        if (World.get(x_1, y_1 + 1, z_1) === 0) {
                            positions.push.apply(positions, createTopFace(x_1, y_1, z_1));
                            colors.push.apply(colors, createTopColor());
                            textures.push.apply(textures, createTopTexture());
                            indices.push.apply(indices, createIndices(index));
                            index += 4;
                            pointsCount += 6;
                        }
                        if (World.get(x_1, y_1 - 1, z_1) === 0) {
                            positions.push.apply(positions, createBottomFace(x_1, y_1, z_1));
                            colors.push.apply(colors, createBottomColor());
                            textures.push.apply(textures, createBottomTexture());
                            indices.push.apply(indices, createIndices(index));
                            index += 4;
                            pointsCount += 6;
                        }
                        if (World.get(x_1, y_1, z_1 + 1) === 0) {
                            positions.push.apply(positions, createFrontFace(x_1, y_1, z_1));
                            colors.push.apply(colors, createFrontColor());
                            textures.push.apply(textures, createFrontTexture());
                            indices.push.apply(indices, createIndices(index));
                            index += 4;
                            pointsCount += 6;
                        }
                        if (World.get(x_1, y_1, z_1 - 1) === 0) {
                            positions.push.apply(positions, createBackFace(x_1, y_1, z_1));
                            colors.push.apply(colors, createBackColor());
                            textures.push.apply(textures, createBackTexture());
                            indices.push.apply(indices, createIndices(index));
                            index += 4;
                            pointsCount += 6;
                        }
                    }
                }
            }
        }
        console.log(positions.length);
        console.log(colors.length);
        console.log(textures.length);
        console.log(indices.length);
        var positionBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(positions), gl.STATIC_DRAW);
        var colorBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, colorBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(colors), gl.STATIC_DRAW);
        var textureBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ARRAY_BUFFER, textureBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(textures), gl.STATIC_DRAW);
        var indexBuffer = gl.createBuffer();
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer);
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint32Array(indices), gl.STATIC_DRAW);
        return {
            position: positionBuffer,
            color: colorBuffer,
            texture: textureBuffer,
            indices: indexBuffer,
        };
    }
    var buffers = initBuffers();
    var rotY = 0.0;
    var rotX = 0.0;
    var x = -4;
    var y = 2;
    var z = -9;
    var spaceDown = false;
    var shiftDown = false;
    var wDown = false;
    var sDown = false;
    var aDown = false;
    var dDown = false;
    window.onkeydown = function (e) {
        var code = e.which;
        if (code === 32) {
            spaceDown = true;
        }
        else if (code === 16) {
            shiftDown = true;
        }
        else if (code === 87) {
            wDown = true;
        }
        else if (code === 83) {
            sDown = true;
        }
        else if (code === 65) {
            aDown = true;
        }
        else if (code === 68) {
            dDown = true;
        }
    };
    window.onkeyup = function (e) {
        var code = e.which;
        if (code === 32) {
            spaceDown = false;
        }
        else if (code === 16) {
            shiftDown = false;
        }
        else if (code === 87) {
            wDown = false;
        }
        else if (code === 83) {
            sDown = false;
        }
        else if (code === 65) {
            aDown = false;
        }
        else if (code === 68) {
            dDown = false;
        }
    };
    window.onmousedown = function () {
        canvas.requestPointerLock();
    };
    window.onmousemove = function (e) {
        if (document.pointerLockElement !== null) {
            rotY += e.movementX * 0.001;
            rotX += e.movementY * 0.001;
            if (rotX < -Math.PI / 2) {
                rotX = -Math.PI / 2;
            }
            else if (rotX > Math.PI / 2) {
                rotX = Math.PI / 2;
            }
        }
    };
    {
        var numComponents = 1;
        var type = gl.FLOAT;
        var normalize = false;
        var stride = 0;
        var offset = 0;
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.color);
        gl.vertexAttribPointer(programInfo.attribLocations.vertexColor, numComponents, type, normalize, stride, offset);
        gl.enableVertexAttribArray(programInfo.attribLocations.vertexColor);
    }
    // Tell WebGL how to pull out the positions from the position
    // buffer into the vertexPosition attribute.
    {
        var numComponents = 3; // pull out 3 values per iteration
        var type = gl.FLOAT; // the data in the buffer is 32bit floats
        var normalize = false; // don't normalize
        var stride = 0; // how many bytes to get from one set of values to the next
        // 0 = use type and numComponents above
        var offset = 0; // how many bytes inside the buffer to start from
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.position);
        gl.vertexAttribPointer(programInfo.attribLocations.vertexPosition, numComponents, type, normalize, stride, offset);
        gl.enableVertexAttribArray(programInfo.attribLocations.vertexPosition);
    }
    {
        var num = 2; // chaque coordonnée est composée de 2 valeurs
        var type = gl.FLOAT; // les données dans le tampon sont des flottants 32 bits
        var normalize = false; // ne pas normaliser
        var stride = 0; // combien d'octets à récupérer entre un jeu et le suivant
        var offset = 0; // à combien d'octets du début faut-il commencer
        gl.bindBuffer(gl.ARRAY_BUFFER, buffers.texture);
        gl.vertexAttribPointer(programInfo.attribLocations.vertexTexture, num, type, normalize, stride, offset);
        gl.enableVertexAttribArray(programInfo.attribLocations.vertexTexture);
    }
    var texture = loadTexture("grass.png");
    // Tell WebGL to use our program when drawing
    gl.useProgram(programInfo.program);
    gl.clearColor(0.0, 0.0, 0.0, 1.0); // Clear to black, fully opaque
    gl.clearDepth(1.0); // Clear everything
    gl.enable(gl.DEPTH_TEST); // Enable depth testing
    gl.depthFunc(gl.LEQUAL); // Near things obscure far things
    function drawScene(programInfo) {
        if (wDown) {
            z += Math.cos(rotY) * 0.1;
            x += Math.sin(rotY) * 0.1;
        }
        if (sDown) {
            z -= Math.cos(rotY) * 0.1;
            x -= Math.sin(rotY) * 0.1;
        }
        if (aDown) {
            z += Math.sin(rotY) * 0.1;
            x -= Math.cos(rotY) * 0.1;
        }
        if (dDown) {
            z -= Math.sin(rotY) * 0.1;
            x += Math.cos(rotY) * 0.1;
        }
        if (spaceDown) {
            y += 0.1;
        }
        if (shiftDown) {
            y -= 0.1;
        }
        if (canvas.height !== window.innerHeight || canvas.width !== window.innerWidth) {
            canvas.height = window.innerHeight;
            canvas.width = window.innerWidth;
            gl.viewport(0, 0, canvas.width, canvas.height);
        }
        // Clear the canvas before we start drawing on it.
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
        // Indiquer à WebGL que nous voulons affecter l'unité de texture 0
        gl.activeTexture(gl.TEXTURE0);
        // Lier la texture à l'unité de texture 0
        gl.bindTexture(gl.TEXTURE_2D, texture);
        // Indiquer au shader que nous avons lié la texture à l'unité de texture 0
        gl.uniform1i(programInfo.uniformLocations.uSampler, 0);
        {
            // Create a perspective matrix, a special matrix that is
            // used to simulate the distortion of perspective in a camera.
            // Our field of view is 45 degrees, with a width/height
            // ratio that matches the display size of the canvas
            // and we only want to see objects between 0.1 units
            // and 100 units away from the camera.
            var fieldOfView = 45 * Math.PI / 180; // in radians
            var aspect = canvas.clientWidth / canvas.clientHeight;
            var zNear = 0.1;
            var zFar = 1000.0;
            var projectionMatrix = mat4.create();
            // note: glmatrix.js always has the first argument
            // as the destination to receive the result.
            mat4.perspective(projectionMatrix, fieldOfView, aspect, zNear, zFar);
            // Set the drawing position to the "identity" point, which is
            // the center of the scene.
            var modelViewMatrix = mat4.create();
            mat4.rotateX(modelViewMatrix, modelViewMatrix, rotX);
            mat4.rotateY(modelViewMatrix, modelViewMatrix, rotY);
            mat4.translate(modelViewMatrix, // destination matrix
            modelViewMatrix, // matrix to translate
            -x, -y, z); // amount to translate
            var viewMatrix = mat4.create();
            mat4.multiply(viewMatrix, projectionMatrix, modelViewMatrix);
            // Set the shader uniforms
            gl.uniformMatrix4fv(programInfo.uniformLocations.uViewMatrix, false, viewMatrix);
        }
        {
            var vertexCount = pointsCount;
            var type = gl.UNSIGNED_INT;
            var offset = 0;
            gl.drawElements(gl.TRIANGLES, vertexCount, type, offset);
        }
    }
    function draw() {
        drawScene(programInfo);
        window.requestAnimationFrame(draw);
    }
    window.requestAnimationFrame(draw);
})();
