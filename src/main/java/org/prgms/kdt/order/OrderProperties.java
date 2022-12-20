package org.prgms.kdt.order;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

//@Component
@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean {

//    @Value("${kdt.version:v0.0.0}")
    private String version;

//    @Value("${kdt.minimum-order-amount}")
    private Integer minimumOrderAmount;

//    @Value("${kdt.support-vendors}")
    private List<String> supportVendors;
    private String description;

    @Value("${JAVA_HOME}")
    private String JavaHome;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(Integer minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public List<String> getSupportVendors() {
        return supportVendors;
    }

    public void setSupportVendors(List<String> supportVendors) {
        this.supportVendors = supportVendors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(MessageFormat.format("[OrderProperties] version -> {0}", version));
        System.out.println(MessageFormat.format("[OrderProperties] minimumOrderAmount -> {0}", minimumOrderAmount));
        System.out.println(MessageFormat.format("[OrderProperties] supportVendors -> {0}", supportVendors));
        System.out.println(MessageFormat.format("[OrderProperties] JavaHome -> {0}", JavaHome));
    }
}
