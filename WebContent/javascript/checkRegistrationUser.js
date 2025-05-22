/**
 *
 */

function validate(){
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var password2 = document.getElementById("password2").value;
    var filePath = document.getElementById("file").value;
    if (!/(jpg|jpeg)$/ig.test(filePath)) {
        alert("L'immagine deve essere di formato jpeg.");
        return false;
    }
    if (email == ""){
        alert("il campo username e' vuoto")
        return false;
    }
    if (password == ""){
        alert("il campo password e' vuoto")
        return false;
    }

    if(!validateEmail(email)){
        alert("l'email non e' corretta riprovare");
        return false;
    }
    
    if(password2 != password){
        alert("le password inserite non sono corrette");
        return false;
    }


    if(!validatePassword(password)){
        alert("La password non e' corretta, la password deve essere lunga almeno di 6 caratteri fino a un massimo di 64 caratteri. Deve contenente lettere magliuscole, lettere minuscole, numeri e caratteri speciali");
        return false;
    }
    document.invio.action = "/sna/registerUser";
    document.invio.submit();
    return true;
}