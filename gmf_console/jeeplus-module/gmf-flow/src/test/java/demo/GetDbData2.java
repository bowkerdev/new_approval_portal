package demo;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GetDbData2 {
	public static JSONArray getData(String sql) {
		 //声明Connection对象
        Connection con;
        //驱动程序名
        //String driver = "com.mysql.jdbc.Driver";
        String driver = "com.mysql.cj.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://bowkerdb-uat.mysql.database.azure.com:3306/einspection_uat?verifyServerCertificate=true&useSSL=true&requireSSL=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&autoReconnect=true&serverTimezone=GMT";
        //MySQL配置时的用户名
        String user = "zhimi@bowkerdb-uat";
        //MySQL配置时的密码
        String password = "ymXty^7J";        
        
        //遍历查询结果集
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            //2.创建statement类对象，用来执行SQL语句！！
            Statement statement = con.createStatement();
            //要执行的SQL语句 
            //3.ResultSet类，用来存放获取的结果集！！
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData rsmd = resultSet.getMetaData();
	        int columCount = rsmd.getColumnCount();
	        JSONArray jsonArray=new JSONArray();
	        while (resultSet.next()) {
	            JSONObject jsonObject=new JSONObject();
	            for(int i=1;i<=columCount;i++){
	            	if("file_name".equals(rsmd.getColumnName(i))){
	            		jsonObject.put(rsmd.getColumnName(i),new String(resultSet.getBlob(i).getBytes(1, (int) resultSet.getBlob(i).length()),"UTF-8"));
	            	}
	            	else{
	            		jsonObject.put(rsmd.getColumnName(i),resultSet.getObject(i));
	            	}
	                
	            }
	            jsonArray.add(jsonObject);
	        }
	        resultSet.close();
            con.close();
            return jsonArray;
        } catch(Exception e) {   
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");   
            e.printStackTrace();   
        }finally{
            System.out.println("数据库数据成功获取！！");
        }
		
		return null;
	}
	
	public static JSONArray getData2(String sql) {
		 //声明Connection对象
       Connection con;
       //驱动程序名
       //String driver = "com.mysql.jdbc.Driver";
       String driver = "com.mysql.cj.jdbc.Driver";
       //URL指向要访问的数据库名mydata
       String url = "jdbc:mysql://eins-prod.mysql.database.azure.com:3306/einspection?verifyServerCertificate=true&useSSL=true&requireSSL=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&autoReconnect=true&serverTimezone=GMT";
       //MySQL配置时的用户名
       String user = "webapp@eins-prod";
       //MySQL配置时的密码
       String password = "Eb#prDu7&*nE";        
       
       //遍历查询结果集
       try {
           //加载驱动程序
           Class.forName(driver);
           //1.getConnection()方法，连接MySQL数据库！！
           con = DriverManager.getConnection(url,user,password);
           if(!con.isClosed())
               System.out.println("Succeeded connecting to the Database!");
           //2.创建statement类对象，用来执行SQL语句！！
           Statement statement = con.createStatement();
           //要执行的SQL语句 
           //3.ResultSet类，用来存放获取的结果集！！
           ResultSet resultSet = statement.executeQuery(sql);
           ResultSetMetaData rsmd = resultSet.getMetaData();
	        int columCount = rsmd.getColumnCount();
	        JSONArray jsonArray=new JSONArray();
	        while (resultSet.next()) {
	            JSONObject jsonObject=new JSONObject();
	            for(int i=1;i<=columCount;i++){
	            	if("file_name".equals(rsmd.getColumnName(i))){
	            		jsonObject.put(rsmd.getColumnName(i),new String(resultSet.getBlob(i).getBytes(1, (int) resultSet.getBlob(i).length()),"UTF-8"));
	            	}
	            	else{
	            		jsonObject.put(rsmd.getColumnName(i),resultSet.getObject(i));
	            	}
	                
	            }
	            jsonArray.add(jsonObject);
	        }
	        resultSet.close();
           con.close();
           return jsonArray;
       } catch(Exception e) {   
           //数据库驱动类异常处理
           System.out.println("Sorry,can`t find the Driver!");   
           e.printStackTrace();   
       }finally{
           System.out.println("数据库数据成功获取！！");
       }
		
		return null;
	}
}