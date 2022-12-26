package org.prgms.kdt;

import org.prgms.kdt.configuration.YamlPropertiesFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"org.prgms.kdt.order","org.prgms.kdt.voucher", "org.prgms.kdt.configuration"})
@PropertySource(value = "application.yaml", factory = YamlPropertiesFactory.class )
@EnableConfigurationProperties
public class AppConfiguration {

}
