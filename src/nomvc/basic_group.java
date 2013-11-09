package nomvc;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.google.gson.Gson;

public class basic_group {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
		t.put("state", "2");
		t.put("msg", "access denied");	
		t.put("user", executor);
		t.put("session", session);
		try {	
			
			if (functionName.equals("grid")) {	
				if(basic_user.checkPermission(executor, "120101", session)){

					String sortname = "code";
					String sortorder = "asc";
					if( request.getParameter("sortname") != null ){
						sortname = (String) request.getParameter("sortname");
					}
					if( request.getParameter("sortorder") != null ){
						sortorder = (String) request.getParameter("sortorder");
					}				
					t = grid(
						 (String) request.getParameter("search")
						,(String) request.getParameter("pagesize")
						,(String) request.getParameter("page")
						,executor
						,sortname
						,sortorder
						);		
				}else{
					t.put("action", "120101");
				}
			}else if(functionName.equals("add")){
				if(basic_user.checkPermission(executor, "120121", session)){
					t = add(
							(String)request.getParameter("data"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("modify")){
				if(basic_user.checkPermission(executor, "120122", session)){
					t = modify(
							(String)request.getParameter("data"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("view")){
				t = view(
						(String)request.getParameter("code")						
						);							
			}else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "120123", session)){
					t = remove(
							(String)request.getParameter("codes"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("permission_set")){
				t.put("action", "120140");
				if(basic_user.checkPermission(executor, "120140", session)){
					t = permission_set(
							 (String)request.getParameter("code")
							,(String)request.getParameter("codes")
							,(String)request.getParameter("cost_")
							,(String)request.getParameter("credits_")
							);
				}				
			}else if(functionName.equals("permission_get")){
				t.put("action", "120140");
				if(basic_user.checkPermission(executor, "120140", session)){
					t = permission_get(
							(String)request.getParameter("code")
							);
				}				
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}
			

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}		
		out = g.toJson(t);
		return out;
	}
	
	public static Hashtable loadConfig() {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		ArrayList a = null;
		
		try {
			stmt = conn.createStatement();
			String sql = "select code,value from basic_parameter where reference = 'basic_group__type' and code not in ('1','9')  order by code";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("basic_group__type", a);		
			
			sql = "select code,value from basic_parameter where reference = 'basic_group__status' ";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("basic_group__status", a);	
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }		
		
		return t_return;
	}
	
	public static Hashtable grid(
			 String search
			,String pagesize
			,String pagenum
			,String executor
			,String sortname
			,String sortorder) {
		Hashtable t_return = new Hashtable();
		Connection conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		String where = " where 1=1 ";
		String sql = tools.getSQL("basic_group__grid");
		Hashtable search_t = (Hashtable) new Gson().fromJson(search, Hashtable.class);
		for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = search_t.get(key);
			if(key.equals("name")){
				where += " and name like '%"+value+"%'";
			}
			if(key.equals("type")){
				where += " and type = '"+value+"'";
			}		
			if(key.equals("code")){
				where += " and ( ( code like '"+value+"__' ) or (code = '"+value+"') )";
			}			
		}
		int i_pagesize = Integer.valueOf(pagesize);
		int i_pagenum = Integer.valueOf(pagenum);
		sql += where + " order by "+sortname+" "+sortorder+" limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+" ";

		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList a = new ArrayList();
			ResultSetMetaData rsData = rs.getMetaData();
			while (rs.next()) {			
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rs.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}
				a.add(t);
			}
			t_return.put("Rows", a);	
			String sql_total = "select count(*) as Total from basic_group "+where;

			rs = stmt.executeQuery(sql_total);
			rs.next();
			t_return.put("Total", rs.getString("Total"));			
		} catch (SQLException e) {
			e.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", e.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }

		return t_return;
	}		
	
	public static Hashtable add(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;

		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
	
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
		}

		e = t_data.keys();
		String keys = "insert into basic_group (";
		String values = ") values (";		
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			keys += key+",";
			values += (String)t_data.get(key)+",";
		}
		keys = keys.substring(0,keys.length()-1);
		values = values.substring(0,values.length()-1);
		
		String sql = keys + values + ");";
		System.out.println(sql);	
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			t_return.put("status", "1");
			t_return.put("msg", "ok");	
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		return t_return;
	}
	
	public static Hashtable remove(String codes,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String[] code = codes.split(",");
			String sql = "";
			for(int i=0;i<code.length;i++){
				sql = "delete from basic_group where code = '"+code[i]+"' ;";
				stmt.executeUpdate(sql);
			}		
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}	
	
	public static Hashtable modify(String data,String executor) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		Hashtable t_data = new Gson().fromJson(data, Hashtable.class);
		String code = (String) t_data.get("code");
		t_data.remove("code");
		
		String sql = "";
		Enumeration e = t_data.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String)t_data.get(key);
			t_data.put(key, "'"+value+"'");
		}
		
		e = t_data.keys();
		sql = "update basic_group set ";
	
		while (e.hasMoreElements()) {
		String key = (String) e.nextElement();
			sql += key + " = " + (String)t_data.get(key) + ",";
		}
		sql = sql.substring(0,sql.length()-1);
		sql += " where code = '"+code+"' ";
		
		System.out.println(sql);		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);				
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }		

		return t_return;
	}
	
	public static Hashtable view(String code) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String sql = "select * from basic_group where code = '"+code+"'";
			rset = tools.getConn().createStatement().executeQuery(sql);
			rset.next();
			Hashtable t_data = new Hashtable();
			ResultSetMetaData m = rset.getMetaData();
			for(int i=1;i<=m.getColumnCount();i++){
				if(rset.getString(m.getColumnLabel(i)) != null){
					t_data.put(m.getColumnLabel(i), rset.getString(m.getColumnLabel(i)));
				}else{
					t_data.put(m.getColumnLabel(i), "-");
				}
			}
			
			t_return.put("data",t_data);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }	

		return t_return;
	}	
	
	public static Hashtable permission_set(String group_code,String permission_codes,String cost_,String credits_) {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = tools.getConn().createStatement();
			String sql = "delete from basic_group_2_permission where group_code = '"+group_code+"' ";
			stmt.executeUpdate(sql);
			
			String[] codes = permission_codes.split(",");
			String[] cost = cost_.split(",");
			String[] credits = credits_.split(",");		
			for(int i=0;i<codes.length;i++){
				sql = "insert into basic_group_2_permission (group_code,permission_code,cost,credits) values ( '"+group_code+"','"+codes[i]+"','"+cost[i]+"','"+credits[i]+"' ); ";
				stmt.executeUpdate(sql);
			}	
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		
		return t_return;
	}	
	
	public static Hashtable permission_get(String code){
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String sql = tools.getSQL("basic_group__permission_get").replace("__group_code__", "'"+code+"'");
			rset = stmt.executeQuery(sql);	
			System.out.println(sql);
			ArrayList array = new ArrayList();
			while (rset.next()) {		
				ResultSetMetaData rsData = rset.getMetaData();	
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rset.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rset.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}
				if(rset.getString("cost") != null){
					t.put("ischecked", 1);
				}
				array.add(t);
			}	
			array = tools.list2Tree(array);	
			
			t_return.put("permissions", array);
			t_return.put("status", "1");
			t_return.put("msg", "ok");
		} catch (SQLException ex) {
			ex.printStackTrace();
			t_return.put("status", "2");	
			t_return.put("msg", ex.toString());	
		} finally {
            try { if (rset != null) rset.close(); } catch(Exception ex) { }
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }

		return t_return;
	}
	
	public static Hashtable upload(String path,String executor) {
		Hashtable t_return = new Hashtable();
		Statement stmt = null;
		try {
			stmt = tools.getConn().createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return t_return;
		}
		String filePath = path;
		InputStream fs = null;
		Workbook workBook = null;
		Sheet sheet = null;
		int columns,rows = 0;
		String[] sqls = null;
		
		try {
			fs = new FileInputStream(filePath);
			workBook = Workbook.getWorkbook(fs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	 		t_return.put("status", "2");
	 		t_return.put("msg", "Excel path wrong");
	 		return t_return;
        } catch (BiffException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }		
		
        try {
        	String[] sql_user = new String[4]; 
    		sql_user[0] = "insert into basic_user(username,password,group_code,group_all,id,type,status) values ('admin','"+tools.MD5("admin")+"','10','10',1,'10','10')";
    		sql_user[1] = "insert into basic_user(username,password,group_code,group_all,id,type,status) values ('guest','"+tools.MD5("guest")+"','99','99',2,'10','10')";
    		sql_user[2] = "insert into basic_group_2_user(user_code,group_code) values ('admin','10')";	
    		sql_user[3] = "insert into basic_group_2_user(user_code,group_code) values ('guest','99')";    		
    		
    		for(int i=0;i<4;i++){
    			System.out.println(sql_user[i]);
    			stmt.executeUpdate(sql_user[i]);
    		}			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

        int basic_group__id = tools.getTableId("basic_group");
        sheet = workBook.getSheet("data_basic_group");
        //System.out.println(sheet.getColumns());
        rows = sheet.getRows();
        if(rows>20000){
    		t_return.put("status", "2");
    		t_return.put("msg", "row count must be less than 20000 , your rows:"+rows);
    		return t_return;
        }
        Cell cell = null;
        sqls = new String[rows-1];
        
//        try {
//			stmt.executeUpdate("START TRANSACTION;");
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
        
        for(int i=1;i<rows;i++){
        	basic_group__id ++;
        	sqls[i-1] = "insert into basic_group(id,name,code,type,status) values ('" 
				+basic_group__id+"','"
				+sheet.getCell(0,i).getContents()+"','"
				+sheet.getCell(1,i).getContents()+"','"
				+sheet.getCell(2,i).getContents()+"','"
				+sheet.getCell(3,i).getContents()+"'"
			+");";
			//System.out.println(sqls[i-1]);
			
			try {
				System.out.println("line: "+(i+1));
				stmt.executeUpdate(sqls[i-1]);	
			} catch (SQLException e) {
				e.printStackTrace();
				
//		        try {
//					stmt.executeUpdate("ROLLBACK;");
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//	    		t_return.put("status", "2");
//	    		t_return.put("msg", "Wrong data , check line "+(i+1));
//	    		return t_return;
			}	
        }
        
        
        sheet = workBook.getSheet("data_basic_permission");
        System.out.println(sheet.getColumns());
        rows = sheet.getRows();
        sqls = new String[rows-1];    

        for(int i=1;i<rows;i++){
        	
        	sqls[i-1] = "insert into basic_permission (name,type,code,icon,path) values('" 
				+sheet.getCell(0,i).getContents().trim()+"','"
				+sheet.getCell(1,i).getContents()+"','"
				+sheet.getCell(2,i).getContents()+"','"
				+sheet.getCell(3,i).getContents()+"','"
				+sheet.getCell(4,i).getContents()+"'"
			+");";
			System.out.println(sqls[i-1]);
			
			try {
				System.out.println("line: "+(i+1));
				stmt.executeUpdate(sqls[i-1]);	
			} catch (SQLException e) {
				e.printStackTrace();
				
		        try {
					stmt.executeUpdate("ROLLBACK;");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	    		t_return.put("status", "2");
	    		t_return.put("msg", "Wrong data , check line "+(i+1));
	    		return t_return;
			}	
        }
        

        sheet = workBook.getSheet("data_basic_group_2_permission");//杩欓噷鍙彇寰楃涓�釜sheet鐨勫�锛岄粯璁や粠0寮�
        columns = sheet.getColumns();
        rows = sheet.getRows();

        sqls = new String[columns*rows];    
        
        for(int i=2;i<rows;i++){
        	String permission = sheet.getCell(1,i).getContents();
        	for(int i2 = 2;i2<columns;i2++){
        		String group = sheet.getCell(i2,1).getContents();
        		if( sheet.getCell(i2,i).getContents() != null && sheet.getCell(i2,i).getContents().equals("1") ){
        			sqls[(i2-1)*rows+i] = "insert into basic_group_2_permission (permission_code,group_code) values('"+permission+"','"+group+"');";
        			
        			try {
        				System.out.println("line: "+(i+1)+" column: "+(i2+1));
        				stmt.executeUpdate(sqls[(i2-1)*rows+i]);	
        			} catch (SQLException e) {
        				e.printStackTrace();
        				
        		        try {
        					stmt.executeUpdate("ROLLBACK;");
        				} catch (SQLException e1) {
        					e1.printStackTrace();
        				}
        	    		t_return.put("status", "2");
        	    		t_return.put("msg", "Wrong data , check line "+(i+1));
        	    		return t_return;
        			}	
        		}
        	}
        }
        
//        try {        
//	        stmt.executeUpdate("COMMIT;");
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}  

        /*
        try {
			stmt.executeUpdate("DELETE from basic_group_2_permission where basic_group_2_permission.group_code not in('10','99');");
			stmt.executeUpdate("insert into basic_group_2_permission (permission_code,group_code) SELECT basic_permission.`code` as permission_code ,basic_group.`code` as group_code FROM basic_permission , basic_group WHERE (basic_permission.`code` like '50%' or basic_permission.`code` like '11%' or basic_permission.`code` like '52%' )  AND basic_group.`code` >= '30' and basic_group.`code` <> '99' and basic_permission.`code` not like '%9_'; ");
			stmt.executeUpdate("insert into basic_group_2_permission (permission_code,group_code) SELECT basic_permission.`code` as permission_code ,'X1' as group_code FROM basic_permission  WHERE (basic_permission.`code` like '50%' or basic_permission.`code` like '11%' or basic_permission.`code` like '52%' )  AND basic_permission.`code` like '%9_'; ");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		*/	
        
        workBook.close();
        try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        t_return.put("status","1");
        t_return.put("msg","ok");
		return t_return;
	}	
	

	
	

	public static void main(String args[]) throws SQLException{
//		System.out.println(new Gson().toJson(basic_group.grid("{}", "20", "1", "1")));
//		System.out.println(new Gson().toJson(basic_group.loadConfig()));
//		System.out.println(new Gson().toJson(basic_group.view("1")));	
//		System.out.println(new Gson().toJson(basic_group.permission_get("10")));		
//		System.out.println(new Gson().toJson(basic_group.remove("10","admin")));	
		basic_group.upload( tools.getConfigItem("APPPATH")+"/file/data.xls", "admin");
//		basic_group.data4test();
//		System.out.println( 
//				new Gson().toJson( (basic_group.grid("{}", "10", "10", "admin", "id", "desc") ) )
//				);
//		basic_group.simulate();
//		basic_user.data4test();
//		oa_plan.data4test(1);
	}	
}