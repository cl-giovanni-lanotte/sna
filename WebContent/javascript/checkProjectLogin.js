function validate() {
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var titolo = document.getElementById("titolo").value;
    var filePath = document.getElementById("file").value;
    //var allowedExtensions = /(\.jpg)$/i;
    if (!/(txt)$/ig.test(filePath)) {
        alert("il file deve essere in formato txt.");
        return false;
    }

    if (email == ""){
        alert("il campo username e' vuoto");
        return false;
    }
    if (password == ""){
        alert("il campo password e' vuoto");
        return false;
    }

    if(!validateEmail(email)){
        alert("l'email non e' corretta riprovare");
        return false;
    }

    if(!validateTitolo(titolo)){
        alert("Il titolo deve avere solo lettere minuscole e maglioscole e la lunghezza della descrizione della proposta deve essere compresa tra tra 2 a 50 caratteri");
        return false;
    }

    if(!validatePassword(password)){
        alert("a password non è corretta, la password deve essere lunga almeno di 8 caratteri e contenente lettere magliuscole, lettere minuscole, numeri e caratteri speciali")
        return false;
    }
    document.invio.action = "/sna/InsertProject";
    document.invio.submit();
    return true;


}

