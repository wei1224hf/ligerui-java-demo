<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="myapp.*,java.io.*,java.sql.Connection"%>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires",0);

String s_out = service_resource.eds_map(request.getParameter("path"));
File file = new File(s_out);
byte[] buffer = null;  
      try {  
         
          FileInputStream fis = new FileInputStream(file);  
          ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
          byte[] b = new byte[1000];  
          int n;  
          while ((n = fis.read(b)) != -1) {  
              bos.write(b, 0, n);  
          }  
          fis.close();  
          bos.close();  
          buffer = bos.toByteArray();  
      } catch (FileNotFoundException e) {  
          e.printStackTrace();  
      } catch (IOException e) {  
          e.printStackTrace();  
      }  
OutputStream out1 = response.getOutputStream();
BufferedOutputStream bos = null;
bos = new BufferedOutputStream(out1);
bos.write(buffer, 0, buffer.length);             
bos.close();             
out.clear();
out=pageContext.pushBody();	
%>