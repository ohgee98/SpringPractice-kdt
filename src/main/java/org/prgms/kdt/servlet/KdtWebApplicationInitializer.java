package org.prgms.kdt.servlet;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zaxxer.hikari.HikariDataSource;
import org.prgms.kdt.customer.CustomerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.management.remote.JMXServerErrorException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// WebApplicationInitializer 상속받아 구현해 servlet 등록
public class KdtWebApplicationInitializer implements WebApplicationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(KdtWebApplicationInitializer.class);
    private ContextLoaderListener loaderListener;


    @EnableWebMvc
    @Configuration
    @ComponentScan(basePackages = "org.prgms.kdt.customer",
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class),
            useDefaultFilters = false
    ) // 다른 스테레오 타입 컴포넌트가 스캔되는 걸 방지하기 위해 false를 함
    static class ServletConfig implements WebMvcConfigurer, ApplicationContextAware {

        ApplicationContext applicationContext;

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {

            registry.jsp().viewNames("jsp/*");

            var springResourceTemplateResolver = new SpringResourceTemplateResolver();
            springResourceTemplateResolver.setApplicationContext(applicationContext);
            springResourceTemplateResolver.setPrefix("/WEB-INF/");
            springResourceTemplateResolver.setSuffix(".html");
            var springTemplateEngine = new SpringTemplateEngine();
            springTemplateEngine.setTemplateResolver(springResourceTemplateResolver);

            var thymeleafViewResolver = new ThymeleafViewResolver();
            thymeleafViewResolver.setTemplateEngine(springTemplateEngine);
            thymeleafViewResolver.setOrder(1); // 여러개의 viewResolver가 있을 때 순서 정하기
            thymeleafViewResolver.setViewNames(new String[]{"views/*"}); // jsp와 충돌 피하기 위함 (jsp와 thymeleaf를 같이 쓸 일 거의 없음)
            registry.viewResolver(thymeleafViewResolver);
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**").addResourceLocations("/resources/")
                    .setCachePeriod(60) // 캐쉬 적용
                    .resourceChain(true) // static resource에 접근할 때 접근 전략 제공, 기본은 pathResolve
                    .addResolver(new EncodedResourceResolver()); // gzip 찾음, 없으면 path
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) { // 기존 컨버터에 확장이 가능
            var messageConverter = new MarshallingHttpMessageConverter();
            var xStreamMarshaller = new XStreamMarshaller();
            messageConverter.setMarshaller(xStreamMarshaller);
            messageConverter.setUnmarshaller(xStreamMarshaller);
            converters.add(0, messageConverter);

            // JSON에서 date값을 받을 때 분해되어 오는 것을 2022-12-29 이런 형식으로 보기 위함
            var javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
            var modules = Jackson2ObjectMapperBuilder.json().modules(javaTimeModule);

            converters.add(1, new MappingJackson2HttpMessageConverter(modules.build()));

            // SpringBoot에서 하면 손쉽게 가능함
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                    .allowedMethods("GET","POST") // 메소드 단위로 허용하려는 경우
                    .allowedOrigins("*"); //전체 다 허용
        }
    }


    @Configuration
    @ComponentScan(basePackages = "org.prgms.kdt.customer",
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class)
    )
    @EnableTransactionManagement
    static class RootConfig {
        @Bean
        public DataSource dataSource(){

            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("root1234!")
                    .type(HikariDataSource.class)
                    .build();
            dataSource.setMaximumPoolSize(1000);
            dataSource.setMinimumIdle(100);
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource){
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate){
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("Starting Server...");
        var rootApplicationContext = new AnnotationConfigWebApplicationContext();
        rootApplicationContext.register(RootConfig.class);
        var loaderListener = new ContextLoaderListener(rootApplicationContext);
        servletContext.addListener(loaderListener);

        // dispatcherServlet 쪽에 등록
        var applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ServletConfig.class);
        var dispatcherServlet = new DispatcherServlet(applicationContext);
        var servletRegistration = servletContext.addServlet("test", dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(1); // default값이 -1로 정의

    }


}
