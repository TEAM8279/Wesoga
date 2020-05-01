namespace BlockModels {
    let models: BlockModel[] = [];

    export function add(model: BlockModel) {
        models.push(model);
    }

    export function count() {
        return models.length;
    }

    export function get(id: number) {
        return models[id];
    }
}
