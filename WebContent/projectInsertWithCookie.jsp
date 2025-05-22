<%--
  Created by IntelliJ IDEA.
  User: giovanni
  Date: 09/09/20
  Time: 18:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Inserisci Progetto</title>
    <script type="text/javascript" src="javascript/checkProjectNotLogin.js"></script>
    <script type="text/javascript" src="javascript/validateParameters.js"></script>
</head>
<body>
<form method="post" name="invio" enctype="multipart/form-data">
    <input type="text" id="titolo" placeholder="Titolo progetto" name="titolo"><br>
    <input type = "file" name = "file" id = "file" size = "50"  ><br>
    <input type="button" name="login"  value="Inserisci" onclick="validate()">
</form>
</body>
</html>
