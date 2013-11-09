<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="nomvc.*,com.google.gson.*"%>
<%
String path = application.getRealPath("/");
String function = request.getParameter("function");
String print = "";
Gson gson = new Gson();

install.path = path;
if(function.equals("step1")){
	print = gson.toJson( install.step1() );
}
else if(function.equals("step2")){
	install.username = request.getParameter("username");
	install.host = request.getParameter("host");
	install.password = request.getParameter("password");
	install.port = request.getParameter("port");
	install.name = request.getParameter("name");
	install.il8n = request.getParameter("il8n");
	install.type = request.getParameter("type");
	install.mode = request.getParameter("mode");
	print = gson.toJson( install.step2() );
}
else if(function.equals("step3")){
	print = gson.toJson( install.step3() );
}
else if(function.equals("step3_2")){
	install.sqls = request.getParameter("sqls");
	print = gson.toJson( install.step3_2() );
}
else if(function.equals("step4")){
	install.sql = request.getParameter("sql");
	print = gson.toJson( install.step4() );
}
out.print(print);
%>