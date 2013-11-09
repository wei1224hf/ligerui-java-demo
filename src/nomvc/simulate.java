package nomvc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class simulate {
	
	public static Hashtable basic_user(){
		Hashtable t_return = new Hashtable();
		String sql = "";
		Connection conn = tools.getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			for(int i=0;i<100;i++){
				sql = "insert into basic_user(id,username,password,money,credits,group_code,type,status) values ('"+(1000+i)+"','user"+i+"','"+tools.MD5("user"+i)+"','100','100','330281-8432-04-13-01','2','1')";
				stmt.execute(sql);
				sql = "insert into basic_group_2_user(user_code,group_code) values ('user"+i+"','330281-8432-04-13-01')";
				stmt.execute(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }	

		return t_return;
	}
	
	public static void main(String args[]){
		simulate.basic_user();
	}
}
