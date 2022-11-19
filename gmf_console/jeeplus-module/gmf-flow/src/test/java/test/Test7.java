package test;

import java.util.ArrayList;
import java.util.List;

import com.jeeplus.common.utils.StringUtils;

public class Test7 {
	public static void main(String[] args) throws Exception {//    1662453516043
		List<String> test=new ArrayList<>();
		test.add("1");
		test.add("b32fc3d6aae34e9590575ea00f22972d");
		System.out.println(StringUtils.join(test,","));
	}
	
}
