<%@ page contentType="text/html; charset=gbk"
	import="java.util.*,com.jspsmart.upload.*,com.google.gson.Gson,myapp.*"%>
<%
	String executor = (String) request.getParameter("executor");
	Hashtable t__grup_type = basic_user.getSession(executor);
	String group_code = (String) t__grup_type.get("group_code");
	String user_type = (String) t__grup_type.get("user_type");
	
	SmartUpload su = new SmartUpload();
	su.initialize(pageContext);

	su.setDeniedFilesList("exe,bat,jsp,htm,html,,");
	su.upload();
	//int count = su.save("/file/upload");

	for (int i = 0; i < su.getFiles().getCount(); i++) {
		com.jspsmart.upload.File file = su.getFiles().getFile(i);
		Calendar calendar = Calendar.getInstance();
		String filename = String.valueOf(calendar.getTimeInMillis())+"."+file.getFileExt();;
		String saveurl = application.getRealPath("/")+"file/upload/" + filename ;

		System.out.println(saveurl);
		file.saveAs(saveurl,SmartUpload.SAVE_PHYSICAL);		
		
		String functionName = (String) request.getParameter("function");
		if (functionName.endsWith("uploadPhoto")) {
			Hashtable t = new Hashtable();
			t.put("status", "1");
			t.put("msg", "ok");
			t.put("path", "../file/upload/" + filename );

			out.println(new Gson().toJson(t));
		} else {
			String className = request.getParameter("class");
			String s_out = "";

			request.setAttribute("path", "../file/upload/" + filename);
			if(className.equals("oa_plan"))					s_out = oa_plan.function(request);
			if(className.equals("oa_work"))					s_out = oa_work.function(request);
			if(className.equals("government_building"))		s_out = government_building.function(request);
			if(className.equals("government_family"))		s_out = government_family.function(request);
			if(className.equals("government_resident"))		s_out = government_resident.function(request);

			out.print(s_out);
		}

		break;
	}	

%>