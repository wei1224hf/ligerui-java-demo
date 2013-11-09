package nomvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gson.Gson;

public class install {

	public static String path = "";
	public static String configXMLFileName = "config.xml";

	public static Hashtable step1() {
		Hashtable t_return = new Hashtable();

		File f_config = new File(install.path + install.configXMLFileName);
		if (!f_config.exists()) {
			t_return.put("status", "2");
			t_return.put("msg", "Cant find the config.xml " + install.path
					+ install.configXMLFileName);
			return t_return;
		}
		if (!f_config.canWrite()) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ "\\config.xml can't be written. modify the authority and try again");
			return t_return;
		}

		String xml = "";
		try {
			String path = install.path + install.configXMLFileName;

			File file = new File(path);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			int s;
			while ((s = br.read()) != -1) {
				buffer.append((char) s);
			}
			xml = buffer.toString();

		} catch (Exception e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		try {
			Document document = DocumentHelper.parseText(xml);
			document.elementByID("APPPATH").setText(install.path);
			String savexml = document.asXML();
			savexml = savexml.replace(
							"<!DOCTYPE root>",
							"<!DOCTYPE root [<!ELEMENT root ANY><!ELEMENT item ANY><!ATTLIST item ID ID #REQUIRED><!ATTLIST item Explanation CDATA #IMPLIED>]>");

			FileOutputStream fos = new FileOutputStream(install.path
					+ install.configXMLFileName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(savexml);
			osw.flush();
		} catch (DocumentException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (FileNotFoundException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (UnsupportedEncodingException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		String otherPath = "";
		otherPath = "\\file";
		f_config = new File(install.path +otherPath );
		if (!(f_config.isDirectory() && f_config.canWrite())) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ otherPath+ " can't be written. modify the authority and try again");
			return t_return;
		}

		otherPath = "\\file\\upload";
		f_config = new File(install.path +otherPath );
		if (!(f_config.isDirectory() && f_config.canWrite())) {
			t_return.put("status", "2");
			t_return.put(
					"msg",
					install.path
							+ otherPath+ " can't be written. modify the authority and try again");
			return t_return;
		}

		t_return.put("status", "1");
		t_return.put("msg", "Done, everything is right.");
		return t_return;
	}

	public static String username, password, host, port, name, il8n ,type,mode = "";
	public static Hashtable step2() {
		Hashtable t_return = new Hashtable();

		String xml = "";
		try {
			String path = install.path + install.configXMLFileName;

			File file = new File(path);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			int s;
			while ((s = br.read()) != -1) {
				buffer.append((char) s);
			}
			xml = buffer.toString();

		} catch (Exception e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		try {
			Document document = DocumentHelper.parseText(xml);			

			document.elementByID("DB_URL").setText(
					"CDATASTART__jdbc:"+install.type+"://" + install.host + ":" + install.port + "/"
							+ install.name + "?user="+install.username+"&password="+install.password+"__CDATAEND");
			document.elementByID("DB_USERNAME").setText("NULL");
			document.elementByID("DB_PASSWORD").setText("NULL");
			document.elementByID("DB_HOST").setText("NULL");
			document.elementByID("DB_NAME").setText("NULL");
			document.elementByID("DB_TYPE").setText(install.type);
			document.elementByID("MODE").setText(install.mode);
			document.elementByID("IL8N").setText(install.il8n);
			
			String savexml = document.asXML();
			savexml = savexml
					.replace(
							"<!DOCTYPE root>",
							"<!DOCTYPE root [<!ELEMENT root ANY><!ELEMENT item ANY><!ATTLIST item ID ID #REQUIRED><!ATTLIST item Explanation CDATA #IMPLIED>]>");
			savexml = savexml.replace("&amp;", "&");
			savexml = savexml.replace("CDATASTART__", "<![CDATA[");
			savexml = savexml.replace("__CDATAEND", "]]>");
			System.out.println(savexml);
			FileOutputStream fos = new FileOutputStream(install.path
					+ install.configXMLFileName);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(savexml);
			osw.flush();
			
		} catch (DocumentException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}catch (FileNotFoundException e) {		 
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (UnsupportedEncodingException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}		
		
		tools.getConfigItem("reLoad");
		Connection conn = tools.getConn();
		if(conn==null){
			t_return.put("status", "2");
			t_return.put(
					"msg",
					"Can not connect to the database. ");
		}else{
			t_return.put("status", "1");
			t_return.put(
					"msg",
					"Done, everything is right. You may check the Databse infomation from config.xml later. ");
		}

		return t_return;
	}

	public static String XLSSQL = null;
	public static Hashtable step3() {
		Hashtable t_return = new Hashtable();
		String filePath = install.path+"sql/sql_"+tools.getConfigItem("IL8N")+".xls";
		InputStream fs = null;
		Workbook workBook = null;

		try {
			fs = new FileInputStream(filePath);
			workBook = Workbook.getWorkbook(fs);
		} catch (FileNotFoundException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (BiffException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		} catch (IOException e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		String sql= "";
		int sheetcount = workBook.getNumberOfSheets();
		for (int i = 0; i < sheetcount; i++) {
			Sheet sheet = workBook.getSheet(i);
			int rows = sheet.getRows();
			
			for (int i2 = 0; i2 < rows; i2++) {
				String theSQL = sheet.getCell(4, i2).getContents();
				sql += theSQL  + "\r\t";				
			}
		}

		try {
			install.XLSSQL = sql ;
			FileOutputStream fos = new FileOutputStream(install.path
					+ "\\file\\sql.txt");
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(sql);
			osw.flush();
			
		} catch (Exception e) {
			t_return.put("status", "2");
			t_return.put("msg", e.toString());
			return t_return;
		}

		t_return.put("status", "1");
		t_return.put("sql", sql.split(";"));
		t_return.put("msg", "Total sql: "+sql.split(";").length );
		return t_return;
	}
	
	public static String sqls = "";
	public static Hashtable step3_2() {
		Hashtable t_return = new Hashtable();
		ArrayList a = new Gson().fromJson(install.sqls, ArrayList.class);
		Connection conn = tools.getConn();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();			
			for(int i=0;i<a.size();i++){
				String sql = (String) a.get(i);
				System.out.println(sql);
				stmt.execute(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            try { if (stmt != null) stmt.close(); } catch(Exception ex) { }
            try { if (conn != null) conn.close(); } catch(Exception ex) { }
        }
		t_return.put("status", "1");
		t_return.put("msg", "sql executed "+a.size());
		return t_return;
	}	

	public static String sql = "";
	public static Hashtable step4() {
		Hashtable t_return = new Hashtable();
		basic_group.upload(tools.getConfigItem("APPPATH")
				+ "/sql/data_"+tools.getConfigItem("IL8N")+".xls", "admin");
		tools.initMemory();
		t_return.put("status", "1");
		t_return.put(
				"msg",
				"Done, everything is right. You can visit the <a href='../html/desktop.html'>system</a> now . Username and password are both 'admin' ");
		return t_return;
	}

	
	
	public static void main(String args[]){
		install.path = "C:\\Users\\Administrator\\workspace\\nomvc\\WebContent\\";
		install.step3();
	}
}
