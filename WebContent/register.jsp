<!DOCTYPE html>
<html>
<head>
    <title>Registrazione</title>
    <script type="text/javascript" src="javascript/checkRegistrationUser.js"></script>
    <script type="text/javascript" src="javascript/validateParameters.js"></script>
</head>

<body>
<form name="invio" method="post" enctype="multipart/form-data">
    <h1>Registrati al sito</h1>
    <input type = "file" name = "file" id = "file" size = "50"  >
    <input type="text" id="email" placeholder="Email" name="email" >
    <input type="password" id="password" placeholder="Password" name="password">
    <input id="password2" name="password2" placeholder="Ripeti password" type="password">

    <button type="submit" name="registrati" onclick="validate()">registrati</button>

</form>

</body>
</html>