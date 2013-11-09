<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="nomvc.*"%>
<%
request.setCharacterEncoding("UTF-8");
String s_out = "";
String className = request.getParameter("class");
if(tools.configXML==null)tools.initMemory();


if(className.equals("basic_user"))				s_out = basic_user.function(request);
if(className.equals("basic_group"))				s_out = basic_group.function(request);
if(className.equals("basic_parameter"))			s_out = basic_parameter.function(request);

out.print(s_out);	
%>
