/**
 *
 */
function validita(){
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;

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

    if(!validatePassword(password)){
    	alert("La password non e' corretta, la password deve essere lunga almeno di 6 caratteri fino a un massimo di 64 caratteri. Deve contenente lettere magliuscole, lettere minuscole, numeri e caratteri speciali");
        return false;
    }
    redirect("/sna/index.html");
    return true;
}


