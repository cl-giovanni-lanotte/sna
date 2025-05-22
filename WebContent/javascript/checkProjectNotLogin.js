function validate() {
    var titolo = document.getElementById("titolo").value;
    var filePath = document.getElementById("file").value;
    if (!/(txt)$/ig.test(filePath)) {
        alert("il file deve essere in formato txt.");
        return false;
    }

    if(!validateTitolo(titolo)){
        alert("Il titolo deve avere solo lettere minuscole e maglioscole e la lunghezza della descrizione della proposta deve essere compresa tra tra 2 a 50 caratteri");
        return false;
    }

    document.invio.action = "/sna/InsertProject";
    document.invio.submit();
    return true;


}

function validateTitolo(titolo){
    var re = /^[a-z-A-Z-0-9 ]{2,50}$/;
    return re.test(titolo);
}

function validateDescrizione(descrizione){
    var re = /^[a-z-A-Z- ()]{10,250}$/;
    return re.test(descrizione);
}
