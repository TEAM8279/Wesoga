namespace Screens {
    export const loadingScreen = document.getElementById("loadingScreen");
    export const titleScreen = document.getElementById("titleScreen");
    export const deathScreen = document.getElementById("deathScreen");
    export const gameScreen = document.getElementById("gameScreen");

    export function showGame() {
        loadingScreen.style.display = "none";
        titleScreen.style.display = "none";
        deathScreen.style.display = "none";
        gameScreen.style.display = "block";
    }

    export function showDeath() {
        loadingScreen.style.display = "none";
        titleScreen.style.display = "none";
        deathScreen.style.display = "block";
        gameScreen.style.display = "block"; // Show game behind death screen
    }

    export function showTitle() {
        loadingScreen.style.display = "none";
        titleScreen.style.display = "block";
        deathScreen.style.display = "none";
        gameScreen.style.display = "none";
    }
}
