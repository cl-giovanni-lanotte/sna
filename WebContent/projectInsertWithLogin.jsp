<%--
  Created by IntelliJ IDEA.
  User: giovanni
  Date: 09/09/20
  Time: 18:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Inserisci Progetto</title>
    <script type="text/javascript" src="javascript/checkProjectLogin.js"></script>
    <script type="text/javascript" src="javascript/validateParameters.js"></script>
</head>
<body>
<h1>Inserisci il tuo progetto</h1>
<form method="post" name="invio" enctype="multipart/form-data">
    <input type="text" id="titolo" placeholder="Titolo progetto" name="titolo"><br>
    Inserisci il file txt:<input type = "file" name = "file" id = "file" size = "50"  ><br>
    <input type="text" id="email" placeholder="Email" name="email"><br>
    <input type="password" id="password" placeholder="Password" name="password"><br>
    <input type="button" name="login"  value="Inserisci" onclick="validate()">
</form>
</body>
</html>
