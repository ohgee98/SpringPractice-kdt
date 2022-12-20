package org.prgms.kdt;

import org.prgms.kdt.configuration.YamlPropertiesFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"org.prgms.kdt.order","org.prgms.kdt.voucher", "org.prgms.kdt.configuration"})
//@PropertySource("application.properties")
@PropertySource(value = "application.yaml", factory = YamlPropertiesFactory.class )
@EnableConfigurationProperties
public class AppConfiguration {

//    @Bean(initMethod = "init")
//    public BeanOne beanOne() {
//        return new BeanOne();
//    }
}
//
//class BeanOne implements InitializingBean {
//    public void init(){
//        System.out.println("[BeanOne] init called!");
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("[BeanOne] afterPropertiesSet called!");
//    }
//}
