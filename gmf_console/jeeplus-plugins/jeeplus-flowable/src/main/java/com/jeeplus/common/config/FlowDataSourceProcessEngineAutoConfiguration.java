package com.jeeplus.common.config;

import javax.sql.DataSource;

import org.activiti.compatibility.spring.SpringFlowable5CompatibilityHandlerFactory;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.configurator.IdmEngineConfigurator;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.FlowableProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jeeplus.modules.flowable.common.factory.MyActivityBehaviorFactory;
import com.jeeplus.modules.flowable.service.ext.FlowIdentityServiceImpl;

@Configuration
@EnableConfigurationProperties(FlowableProperties.class)
public class FlowDataSourceProcessEngineAutoConfiguration  implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    /**
     * @author : liugafeng
     * @date : 2019-07-11 08:50
     * <p>
     * 设置流程图识别中文
     */
    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
    	
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");
        //engineConfiguration.setDatabaseSchemaUpdate("true");
        engineConfiguration.setFlowable5CompatibilityEnabled(true);
        engineConfiguration.setFlowable5CompatibilityHandlerFactory(new SpringFlowable5CompatibilityHandlerFactory());
        engineConfiguration.setActivityBehaviorFactory(new MyActivityBehaviorFactory ());
    }




    @Bean
    public IdmEngineConfigurator idmEngineConfigurator(DataSource dataSource) {
        IdmEngineConfiguration idmEngineConfiguration = new IdmEngineConfiguration();
        idmEngineConfiguration.setDataSource(dataSource);
        idmEngineConfiguration.setIdmIdentityService(new FlowIdentityServiceImpl());

        IdmEngineConfigurator idmEngineConfigurator = new IdmEngineConfigurator();
        idmEngineConfigurator.setIdmEngineConfiguration(idmEngineConfiguration);
        return idmEngineConfigurator;
    }

}
