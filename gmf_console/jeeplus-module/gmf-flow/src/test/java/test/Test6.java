package test;

import java.io.FileOutputStream;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Test6 {
	public static void main(String[] args) throws Exception {
	    // 截第20帧
		JSONArray jsons= GetDbData.getData("SELECT * from sys_async_queue where result ='1' order by create_date desc limit 1000");

		for(int i=0;i<jsons.size();i++){
			JSONObject  json=jsons.getJSONObject(i);
			try {
				String param = json.getString("param");
				JSONObject tmp = JSON.parseObject(param);
				String tmpString = null;
				try {
					tmpString = tmp.getString("panorama");

				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("F:/test/"+tmp.getString("id")+".jpg");
			//	IOUtils.write(Base64.getDecoder().decode(tmpString),new  FileOutputStream("F:/test/"+json.getString("id")+".jpg"));
				JSONArray tmps2=tmp.getJSONArray("facePicList");
				for(int j=0;j<tmps2.size();j++){
					IOUtils.write(Base64.getDecoder().decode(tmps2.getString(j)),new  FileOutputStream("F:/test/"+json.getString("id")+"_"+j+".jpg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(json);
				return ;
			}
			
		}
	   
       
	
	}
	
}
