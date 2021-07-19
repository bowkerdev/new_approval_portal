package demo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Files;
import com.jeeplus.common.utils.net.HttpUtil;

public class TestImg3 {	
	private static byte[] getImg(String url) {
		try {
			ByteArrayOutputStream outStream=new ByteArrayOutputStream();
			HttpUtil.get(url,null,null,outStream);
			return outStream.toByteArray();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static void saveXml(JSONObject tmp,Document document,BufferedImage sourceImg) throws IOException {
		//3.获取根节点
        Element rootElement = document.getRootElement();
		Element filename =rootElement.element("filename");
        filename.setText(tmp.getString("file_no")+".jpg");
        
        Element size=rootElement.element("size");
        Element imgWidth=size.element("width");
        imgWidth.setText(String.valueOf(sourceImg.getWidth()));
        Element imgHeight=size.element("height");
        imgHeight.setText(String.valueOf(sourceImg.getHeight()));
        
        Element object=rootElement.element("object");
        Element name=object.element("name");
        name.setText("test2");
        
        int x=tmp.getIntValue("x");
        int y=tmp.getIntValue("y");
        
        Element bndbox=object.element("bndbox");
        Element xmin=bndbox.element("xmin");
        xmin.setText(String.valueOf(x-100));
        Element ymin=bndbox.element("ymin");
        ymin.setText(String.valueOf(y-100));
        Element xmax=bndbox.element("xmax");
        xmax.setText(String.valueOf(x+100));
        Element ymax=bndbox.element("ymax");
        ymax.setText(String.valueOf(y+100));
        
        Writer osWrite=new OutputStreamWriter(new FileOutputStream("C:\\Users\\glpll\\Desktop\\eins  ai\\train\\"+tmp.getString("file_no")+".xml"));//创建输出流  
		OutputFormat format = OutputFormat.createPrettyPrint();  //获取输出的指定格式    
		format.setEncoding("UTF-8");//设置编码 ，确保解析的xml为UTF-8格式  
		XMLWriter writer = new XMLWriter(osWrite,format);//XMLWriter 指定输出文件以及格式    
		writer.write(document);//把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)    
		writer.flush();  
		writer.close();
	}
	public static void main(String[] args) throws Exception {
		JSONArray tmps=GetDbData2.getData("SELECT t3.file_name,t3.file_no,t4.x,t4.y  ,t4.defect_no "+
			"from taskend4  t4 "+
			"join taskend3  t3 on t4.taskend3_id=t3.id "+
			"where  t4.create_date >'2021-03-15 00:00:00' "+
			"order by t4.create_date desc "+
			"limit 20");
		
/*		JSONArray tmps=GetDbData2.getData2("SELECT DISTINCT t3.file_name , t3.file_no "+
				"from taskend4  t4 "+
				"join taskend3  t3 on t4.taskend3_id=t3.id "+
				"order by t4.file_no desc "+
				"limit 200");*/

		//1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new File("C:\\Users\\glpll\\Desktop\\eins  ai\\IMG_20180919_115810.xml"));

		for(int i=0;i<tmps.size();i++){
			JSONObject tmp=tmps.getJSONObject(i);
			//byte[] bytes=getImg(tmp.getString("file_name"));// 
			byte[] bytes=getImg(tmp.getString("file_name").replace(".jpg", "_org.jpg"));
			BufferedImage sourceImg=ImageIO.read(new ByteArrayInputStream(bytes));	
		    saveXml(tmp, document,sourceImg);
		    Files.write(bytes, new File("C:\\Users\\glpll\\Desktop\\eins  ai\\train\\"+tmp.getString("file_no")+".jpg"));
		}
		
	}
}
