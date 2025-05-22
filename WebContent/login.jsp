<!DOCTYPE html>
<html>
<head>
  <title>Benvenuto</title>
  <script type="text/javascript" src="javascript/checkLogin.js"></script>
  <script type="text/javascript" src="javascript/validateParameters.js"></script>
</head>

<body>
<form name = "invio" method = "post" >
  <h1>Login</h1>
  <input type="text" id="email" placeholder="Email" name="email" >
  <input type="password" id="password" placeholder="Password" name="password">
  <button type="submit" name="login" onclick="validita()">Accedi</button>
<br>
  <input type = "checkbox" id="cookies" name="cookies"> Login automatico
  <br>
  <a href="/sna/registerUser">Non sei registrato? Registrati</a>
</form>



</body>
</html>