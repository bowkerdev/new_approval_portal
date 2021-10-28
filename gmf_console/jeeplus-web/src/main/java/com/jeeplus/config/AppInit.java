/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.config;

import org.springframework.beans.factory.annotation.Value;
import com.jeeplus.common.utils.net.HttpUtil;
import com.jeeplus.modules.sys.utils.DictUtils;

import java.net.InetAddress;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by jeeplus on 2017/11/30.
 */
@Component
public class AppInit implements CommandLineRunner {
    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path}")
    private String path;
    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>系统启动成功<<<<<<<<<<<<<");
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Jeeplus Application running at:\n\t" +
                "- Local: http://localhost:" + port + path + "/\n\t" +
                "- Network: http://" + ip + ":" + port + path + "/\n\t" +
                "- swagger: http://" + ip + ":" + port + path + "/doc.html\n" +
                "----------------------------------------------------------");
        
        String hosts=DictUtils.getDictValue("IgnoreHttpsHost", "sys_config", "baseportaldev.bowkerasia.com,wecare.bowkerasia.com");
    	if(!HttpUtil.getIgnoreHttpsHost().equals(hosts)){ //t统一设置
			HttpUtil.setIgnoreHttpsHost(hosts);
		}
    }


}

