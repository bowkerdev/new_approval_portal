package test;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.net.HttpUtil;

public class DdTest {
	public static String getToken(){
		String url="https://oapi.dingtalk.com/gettoken?appkey=#{appKey}&appsecret=#{appSecret}";
		String appKey ="dingmdokzvobaxgfujbw";
		String appSecret ="pYoryANdGgwaW94-JeJ8H_QYR02mmU5hO4HKJKdjRvQV9PgzVm3jfJk79fdfHm5N";
		url=url.replace("#{appKey}", appKey);
		url=url.replace("#{appSecret}", appSecret);
		try {
			String res = HttpUtil.get(url);
			JSONObject resObj= JSON.parseObject(res);
			if(resObj.getInteger("errcode")==0){
				return resObj.getString("access_token");
			}
			else{
				// TODO
				System.err.println(res);
				throw new RuntimeException("获取阿里token 失败："+res);
			}
		} catch (Exception e) {
			// TODO 
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public static JSONArray getDdUserOffice(String accessToken,
			String userid) {
		String url="https://oapi.dingtalk.com/topapi/v2/department/listparentbyuser?access_token=#{accessToken}";
		url=url.replace("#{accessToken}", accessToken);
		String param="{\"userid\":\"#{userid}\"}";
		param=param.replace("#{userid}", userid);
		JSONArray returnRes=new JSONArray();
		try {
			Map<String, String> headers=new HashMap<String, String>();
			headers.put("Content-Type", "application/json;charset=UTF-8");
			String res=HttpUtil.post(url, param, headers);
			JSONObject resObj=JSON.parseObject(res);
			if(resObj.getIntValue("errcode")==0){ 
				JSONArray parentList = resObj.getJSONObject("result").getJSONArray("parent_list");
				for(int i=0;parentList.size()>i;i++ ){
					String officeId=parentList.getJSONObject(i).getJSONArray("parent_dept_id_list").getString(0);
					url="https://oapi.dingtalk.com/topapi/v2/department/get?access_token=#{accessToken}";
					url=url.replace("#{accessToken}", accessToken);
					param="{\"language\":\"zh_CN\",\"dept_id\":\"#{deptId}\"}";
					param=param.replace("#{deptId}", officeId);
					res=HttpUtil.post(url, param, headers);
					resObj=JSON.parseObject(res);
					if(resObj.getIntValue("errcode")==0){ 
						returnRes.add(resObj.getJSONObject("result"));
					}
					else{//TODO
						System.err.println(res);
					}
				}
			}
			else{//TODO
				System.err.println(res);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return returnRes;
	}
	public static JSONObject getDdUserInfoDetails(String accessToken,
			String userid) {
		String url="https://oapi.dingtalk.com/topapi/v2/user/get?access_token=#{accessToken}";
		url=url.replace("#{accessToken}", accessToken);
		String param="{\"language\":\"zh_CN\",\"userid\":\"#{userid}\"}";
		param=param.replace("#{userid}", userid);
		try {
			Map<String, String> headers=new HashMap<String, String>();
			headers.put("Content-Type", "application/json;charset=UTF-8");
			String res=HttpUtil.post(url, param, headers);
			JSONObject resObj=JSON.parseObject(res);
			if(resObj.getIntValue("errcode")==0){ 
				JSONArray offices = getDdUserOffice(accessToken, userid);
				resObj.getJSONObject("result").put("dept_detail", offices);
				return resObj.getJSONObject("result");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) throws Exception {
		String accessToken = getToken();
		System.out.println(getDdUserInfoDetails(accessToken,"0909040846672569"));
	}
}
