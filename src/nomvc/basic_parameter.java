package nomvc;
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

import com.google.gson.Gson;

public class basic_parameter {
	
	public static String function(HttpServletRequest request) {
		String out = "";
		String functionName = (String) request.getParameter("function");
		String executor = (String)request.getParameter("executor");
		String session = (String)request.getParameter("session");
		Gson g = new Gson();
		Hashtable t = new Hashtable();
		t.put("state", "2");
		t.put("msg", "access denied");		
		try {	

			
			if (functionName.equals("grid")) {		
				if(basic_user.checkPermission(executor, "120301", session)){
					t = grid(
							(String) request.getParameter("search"),
							(String) request.getParameter("pagesize"),
							(String) request.getParameter("page"),
							"10"
							);	
				}
			}else if(functionName.equals("add")){
				if(basic_user.checkPermission(executor, "120321", session)){
					t = add(
							(String)request.getParameter("data"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("resetMemory")){
				if(basic_user.checkPermission(executor, "120342", session)){
					t = basic_parameter.resetMemory();		
				}
			}else if(functionName.equals("remove")){
				if(basic_user.checkPermission(executor, "120323", session)){
					t = remove(
							(String)request.getParameter("codes"),
							(String)request.getParameter("username")
							);
				}				
			}else if(functionName.equals("loadConfig")){
				t = loadConfig();				
			}else if(functionName.equals("lowerCodes")){
				out = g.toJson(lowerCodes((String)request.getParameter("code"), (String)request.getParameter("reference")));
				return out;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			out = e.toString();
		}		
		out = g.toJson(t);
		return out;
	}
	
	public static Hashtable loadConfig() {
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String sql = "select code,value from basic_parameter where reference = 'basic_group__type' and code not in ('1','9')  order by code";
			rset = stmt.executeQuery(sql);
			ArrayList a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("type", a);		
			
			sql = "select code,value from basic_parameter where reference = 'basic_group__status' ";
			rset = stmt.executeQuery(sql);
			a = new ArrayList();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				t.put("code", rset.getString("code"));
				t.put("value", rset.getString("value"));			
				a.add(t);
			}
			t_return.put("status", a);
		} catch (SQLException e) {
			e.printStackTrace();
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
			,String user_type){
		
		Hashtable t_return = new Hashtable();
		Connection conn = conn = tools.getConn();
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			String where = " where 1=1 ";
			String sql = "select * from basic_parameter";
			Hashtable search_t = new Gson().fromJson(search, Hashtable.class);
			for (Iterator it = search_t.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = search_t.get(key);
				if(key.equals("name")){
					where += " and reference like '%"+value+"%'";
				}
			}
			sql += where + " limit "+(Integer.valueOf(pagesize) * (Integer.valueOf(pagenum)-1) )+","+pagesize+";";
			System.out.println(sql);
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			ArrayList a = new ArrayList();
			ResultSetMetaData rsData = rset.getMetaData();
			while (rset.next()) {			
				Hashtable t = new Hashtable();	
				for(int i=1;i<=rsData.getColumnCount();i++){
					if(rset.getString(rsData.getColumnLabel(i)) != null){
						t.put(rsData.getColumnLabel(i), rset.getString(rsData.getColumnLabel(i)));
					}else{
						t.put(rsData.getColumnLabel(i), "-");
					}
				}
				a.add(t);
			}
			t_return.put("Rows", a);	
			String sql_total = "select count(*) as Total from basic_parameter "+where;

			rset = stmt.executeQuery(sql_total);
			rset.next();
			t_return.put("Total", rset.getString("Total"));
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
		
		try {
			stmt = conn.createStatement();
			Hashtable t_data = new Gson().fromJson(data, Hashtable.class);	
			
			Enumeration e = t_data.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = (String)t_data.get(key);
				t_data.put(key, "'"+value+"'");
			}

			e = t_data.keys();
			String keys = "insert into basic_parameter (";
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
			stmt.executeUpdate(sql);
			
			t_return.put("status", "1");
			t_return.put("msg", "ok");
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
	
	public static Hashtable remove(String codes,String executor) throws SQLException{
		Hashtable t_return = new Hashtable();
		Statement stmt = tools.getConn().createStatement();
		String[] code = codes.split(",");
		String sql = "";
		for(int i=0;i<code.length;i++){
			sql = "delete from basic_parameter where code = '"+code[i]+"' ;";
			System.out.println(sql);
			stmt.executeUpdate(sql);
		}		
		t_return.put("status", "1");
		t_return.put("msg", "ok");
		return t_return;
	}	
	
	public static ArrayList lowerCodes(String code,String reference) throws SQLException{		
		String sql = "select code,value from basic_parameter where code like '"+code+"__' and reference = '"+reference+"';";
		
		System.out.println(sql);
		Statement stmt = tools.getConn().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList a = new ArrayList();
		ResultSetMetaData rsData = rs.getMetaData();
		while (rs.next()) {			
			Hashtable t = new Hashtable();	
			for(int i=1;i<=rsData.getColumnCount();i++){
				if(rs.getString(rsData.getColumnLabel(i)) != null){
					t.put(rsData.getColumnLabel(i), rs.getString(rsData.getColumnLabel(i)));
				}else{
					t.put(rsData.getColumnLabel(i), "nul");
				}
			}
			a.add(t);
		}
		return a;
	}		
	
	public static Hashtable resetMemory(){
		Hashtable t_return = new Hashtable();
		tools.initMemory();
		
		t_return.put("status", "1");
		return t_return;
	}
	
	
	public static void main(String args[]) throws SQLException{
//		System.out.println(new Gson().toJson(basic_group.grid("{}", "20", "1", "1")));
//		System.out.println(new Gson().toJson(basic_group.loadConfig()));
//		System.out.println(new Gson().toJson(basic_group.view("1")));	
//		System.out.println(new Gson().toJson(basic_group.permission_get("10")));		
//		System.out.println(new Gson().toJson(basic_group.remove("10","admin")));	
	}	
}