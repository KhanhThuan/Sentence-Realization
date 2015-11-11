<%@page import="jp.ac.jaist.srealizer.utils.FontUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sentence Realizer</title>
</head>
<body>
<p> Th&#7847;y nh&#7853;p c&#225;c t&#7915; c&#225;ch nhau b&#7903;i d&#7845;u ph&#7849;y.
<form action="BagWordServlet" method="post">
	<textarea rows="10" cols="100" name="words">${words }</textarea>
	<br/>
	<input type="submit"/>
</form>
<p> ${ message }</p>
</body>
</html>