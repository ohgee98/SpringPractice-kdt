package org.prgms.kdt.configuration;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

public class YamlPropertiesFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        var yamlPropertiestFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiestFactoryBean.setResources(encodedResource.getResource());

        var properties = yamlPropertiestFactoryBean.getObject();
        return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
    }
}
