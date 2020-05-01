class BlockModel {
    constructor(visible: boolean, northTexture: number, southTexture: number, eastTexture: number, westTexture: number, topTexture: number, botTexture: number) {
        this.visible = visible;
        this.northTexture = northTexture;
        this.southTexture = southTexture;
        this.eastTexture = eastTexture;
        this.westTexture = westTexture;
        this.topTexture = topTexture;
        this.botTexture = botTexture;
    }

    public visible: boolean;

    public northTexture: number;
    public southTexture: number;
    public eastTexture: number;
    public westTexture: number;
    public topTexture: number;
    public botTexture: number;
}
