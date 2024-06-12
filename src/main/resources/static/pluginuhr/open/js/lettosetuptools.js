//Confirm Dialog mit einer Ja/Nein-Frage. Liefert true bei Ja und False bei Nein
function lettoconfirm(message) {
    return confirm(message);
}

//Schreibt eine Message auf den Bildschirm
function lettomessage(message) {
    alert(message);
}

// Ändert den Cursor in einen Warte-Cursor
function setCursorWaiting() {
    document.body.style.cursor = "progress";
    $(":input").each(function(){
        this.style.cursor = "progress";
    })
    //$("*").each(function(){ this.style.cursor = "progress"; })
}

// Ändert den Cursor wieder zurück in den Standard-Cursor
function setCursorNormal() {
    document.body.style.cursor = "default";
    $(":input").each(function(){
        this.style.cursor = "default";
    })
}