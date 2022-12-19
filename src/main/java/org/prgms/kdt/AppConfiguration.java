package org.prgms.kdt;

import org.prgms.kdt.order.Order;
import org.prgms.kdt.voucher.MemoryVoucherRepository;
import org.prgms.kdt.voucher.Voucher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"org.prgms.kdt.order","org.prgms.kdt.voucher", "org.prgms.kdt.configuration"})
public class AppConfiguration {

    @Bean(initMethod = "init")
    public BeanOne beanOne() {
        return new BeanOne();
    }
}

class BeanOne implements InitializingBean {
    public void init(){
        System.out.println("[BeanOne] init called!");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[BeanOne] afterPropertiesSet called!");
    }
}
