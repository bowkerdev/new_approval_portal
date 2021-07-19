package demo;

public class TestString {
	public static void main(String[] args) throws Exception {
		String url="https://gxhy-data.com:15002/sms/#{deviceId}/flv/hls/#{nvrId}_#{deviceId}.flv";
		url=url.replaceAll("\\#\\{nvrId\\}","2223");
		url=url.replaceAll("\\#\\{deviceId\\}","33");
		System.out.println(url);
	}
	
}
