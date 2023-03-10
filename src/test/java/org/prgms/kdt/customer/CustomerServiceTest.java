package org.prgms.kdt.customer;

import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgms.kdt.customer.model.Customer;
import org.prgms.kdt.customer.repository.CustomerNamedJdbcRepository;
import org.prgms.kdt.customer.repository.CustomerRepository;
import org.prgms.kdt.customer.service.CustomerService;
import org.prgms.kdt.customer.service.CustomerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_6_23;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Configuration
    @EnableTransactionManagement
    static class Config {

        @Bean
        public DataSource dataSource(){

            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:2215/test-order_mgmt")
                    .username("test")
                    .password("test1234!")
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

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager){
            return new TransactionTemplate(platformTransactionManager);
        }

        @Bean
        public CustomerRepository customerRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate){
            return new CustomerNamedJdbcRepository(namedParameterJdbcTemplate);
        }

        @Bean
        public CustomerService customerService(CustomerRepository customerRepository){
            return new CustomerServiceImpl(customerRepository);
        }

    }

    static EmbeddedMysql embeddedMysql;

    @BeforeAll
    static void setup(){
        var mysqlConfig = aMysqldConfig(v5_6_23)
                .withCharset(UTF8)
                .withPort(2215)
                .withUser("test", "test1234!")
                .withTimeZone("Asia/Seoul")
                .build();
        embeddedMysql = anEmbeddedMysql(mysqlConfig)
                .addSchema("test-order_mgmt", classPathScript("schema.sql"))
                .start();
    }

    @AfterAll
    static void cleanup(){
        embeddedMysql.stop();
    }

    @AfterEach
    void dataCleanup(){
        customerRepository.deleteAll();
    }

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DisplayName("?????? ?????? ?????????")
    void multiInsertTest(){
        var customers = List.of(
                new Customer(UUID.randomUUID(),"a","a@gmail.com",LocalDateTime.now()),
                new Customer(UUID.randomUUID(),"b","b@gmail.com",LocalDateTime.now())
        );

        customerService.createCustomers(customers);
        var allCustomersRetrieved = customerRepository.findAll();
        assertThat(allCustomersRetrieved.size(), is(2));
        assertThat(allCustomersRetrieved, containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs(customers.get(1))));
    }

    @Test
    @DisplayName("?????? ?????? ????????? ?????? ??????????????? ??????????????????.")
    void multiInsertRollbackTest(){
        var customers = List.of(
                new Customer(UUID.randomUUID(),"c","c@gmail.com",LocalDateTime.now()),
                new Customer(UUID.randomUUID(),"d","c@gmail.com",LocalDateTime.now())
        );

        try{
            customerService.createCustomers(customers);
        } catch (DataAccessException e){

        }


        var allCustomersRetrieved = customerRepository.findAll();
        assertThat(allCustomersRetrieved.size(), is(0));
        assertThat(allCustomersRetrieved.isEmpty(), is(true));
        assertThat(allCustomersRetrieved, not(containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs(customers.get(1)))));
    }
}