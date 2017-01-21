<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="javax.servlet.http.HttpSession" %>



<html>
<head>
		<style type="text/css">
		label {
		width: 6em;
	}
	</style>
	
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.5/css/bootstrap.min.css" integrity="sha384-AysaV+vQoT3kOAXZkl02PThvDr8HYKPZhNT5h/CXfBThSRXQ6jW5DO2ekP5ViFdi" crossorigin="anonymous">
						

</head>
<body>
	<%
	String failureMessage = (String) request.getSession().getAttribute("failureMessage");
	
	if (failureMessage != null) {
		%>
		<div class=\"alert alert-success\">" + <%=failureMessage%> + "</div>
		<%
		session.setAttribute("failureMessage", null);
	}
	%>
	
	<h1>Create a new account:</h1>

	<form class="form-inline" action="signup" method="POST">
		<div class="form-group">
			<label for="username">Username:</label><input id="username"
				name="username" type="text">
		</div>
		<br>
		<div class="form-group">
			<label for="firstname">First Name:</label><input id="firstname"
				name="firstname" type="text">
		</div>
		<br>
		<div class="form-group">
			<label for="lastname">Last Name:</label><input id="lastname"
				name="lastname" type="text">
		</div>
		<br>
		<div class="form-group">
			<label for="password">Password:</label><input id="password"
				name="password" type="password">
		</div>
		<button type="submit" class="btn btn-primary">Sign Up</button>
	</form>
</body>
</html>

<!-- 
<html>
<h1>Hello</h1>
<h2><%=new String("test")%></h2>
</html>

 -->