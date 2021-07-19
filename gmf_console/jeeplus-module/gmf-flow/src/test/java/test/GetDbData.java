package test;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GetDbData {
	
	public static String convertBlobToBase64String(Blob blob) {
        String result = "";
        if(null != blob) {
            try {
                InputStream msgContent = blob.getBinaryStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[100];
                int n = 0;
                while (-1 != (n = msgContent.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                result =new String(output.toByteArray(),"UTF-8");
                output.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }
	
	public static JSONArray getData(String sql) {
		 //声明Connection对象
        Connection con;
        //驱动程序名
        //String driver = "com.mysql.jdbc.Driver";
        String driver = "com.mysql.cj.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://192.168.2.136:3306/security?verifyServerCertificate=false&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&autoReconnect=true&serverTimezone=UTC";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "123456";        
        
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
	            	if("param".equals(rsmd.getColumnName(i))){
	            		jsonObject.put(rsmd.getColumnName(i),convertBlobToBase64String(resultSet.getBlob(i)));
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
      String driver = "com.mysql.jdbc.Driver";
      //URL指向要访问的数据库名mydata
      String url = "jdbc:mysql://gmfzhimi.oicp.io:29823/epin?useUnicode=true&characterEncoding=utf-8&useSSL=false&nullCatalogMeansCurrent=true&serverTimezone=GMT";
      //MySQL配置时的用户名
      String user = "root";
      //MySQL配置时的密码
      String password = "123456";
      
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
	                jsonObject.put(rsmd.getColumnName(i),resultSet.getObject(i));
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