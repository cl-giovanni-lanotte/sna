function validateTitolo(titolo){
    var re = /^[a-z-A-Z-0-9 ]{2,50}$/;
    return re.test(titolo);
}

function validateDescrizione(descrizione){
    var re = /^[a-z-A-Z- (),.']{10,250}$/;
    return re.test(descrizione);
}

function validateEmail(email) {
    var re = /^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    return re.test(String(email).toLowerCase());
}


function validatePassword(password) {
    var re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&]).{6,64}$/;
    return re.test(String(password));
}

function redirect(url, metodo){
    if (metodo == "post") {
        document.invio.action = url;
        document.invio.submit();
    }
}
