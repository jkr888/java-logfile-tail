<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="meta.jsp"%>
<title>Home Page</title>
<%@ include file="header.jsp"%>
</head>
<body>
	<%@ include file="menu.jsp"%>

	<div class="container-fluid">
		<jsp:include page="body.jsp"></jsp:include>

	</div>

	<%@ include file="footer.jsp"%>
</body>
</html>