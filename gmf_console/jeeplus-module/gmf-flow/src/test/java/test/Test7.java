package test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.jeeplus.common.utils.TimeUtils;

public class Test7 {
	
	public static String toTimeString(long time) {
		TimeUtils t = new TimeUtils(DateFormatUtils.formatUTC(time, "dd HH:mm:ss"));
		int day = t.get(TimeUtils.DAY);
		int hour = t.get(TimeUtils.HOUR);
		int minute = t.get(TimeUtils.MINUTE);
		int second = t.get(TimeUtils.SECOND);
		StringBuilder sb = new StringBuilder();
		if (day > 0){
			sb.append(day).append("天");
		}
		if (hour > 0){
			sb.append(hour).append("时");
		}
		if (minute > 0){
			sb.append(minute).append("分");
		}
		if (second > 0){
			sb.append(second).append("秒");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {//    1662453516043
		System.out.println(toTimeString(new Date().getTime()));
		System.out.println(toTimeString(1041308000L));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date(1041308000)));
		int times=1041308000/1000;
		int day = times/3600/24;
		int hour =times%(3600*24)/3600;
		int minute = times%(3600)/60;
		int second = times%60;
		
		System.out.println(day + " " + hour + " " + minute + " "+ second);
		
		// 1041308000
	}
	
}
