package org.prgms.kdt.customer;

import com.wix.mysql.EmbeddedMysql;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgms.kdt.customer.model.Customer;
import org.prgms.kdt.customer.repository.CustomerNamedJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerNamedJdbcRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepositoryTest.class);
    @Configuration
    @ComponentScan(basePackages = {"org.prgms.kdt.customer"})
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
    }

    @Autowired
    CustomerNamedJdbcRepository customerJdbcRepository;

    @Autowired
    DataSource dataSource;

    Customer newCustomer;

    EmbeddedMysql embeddedMysql;

    @BeforeAll
    void setup(){
        newCustomer = new Customer(UUID.randomUUID(), "test-user", "test-user@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
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
    void cleanup(){
        embeddedMysql.stop();
    }

    @Test
    @Order(1)
    public void testHikariConnectionPool(){
        assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
    }

    @Test
    @Order(2)
    @DisplayName("????????? ????????? ??? ??????.")
    public void testInsert(){

        try{
            customerJdbcRepository.insert(newCustomer);
        } catch (BadSqlGrammarException e){
            logger.error("God BadSQLGrammarException error code -> {}",e.getSQLException().getErrorCode(), e);
        }

        var retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));

    }

    @Test
    @Order(3)
    @DisplayName("?????? ????????? ????????? ??? ??????.")
    public void testFindAll(){
        var customers = customerJdbcRepository.findAll();
        assertThat(customers.isEmpty(), is(false));
    }

    @Test
    @Order(4)
    @DisplayName("???????????? ????????? ????????? ??? ??????.")
    public void testFindByName(){
        var customer = customerJdbcRepository.findByName(newCustomer.getName());
        assertThat(customer.isEmpty(), is(false));

        var unknown = customerJdbcRepository.findByName("unknown-user");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(5)
    @DisplayName("???????????? ????????? ????????? ??? ??????.")
    public void testFindByEmail(){
        var customer = customerJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customer.isEmpty(), is(false));

        var unknown = customerJdbcRepository.findByEmail("unknown-user@gmail.com");
        assertThat(unknown.isEmpty(), is(true));
    }

    @Test
    @Order(6)
    @DisplayName("????????? ????????? ??? ??????.")
    public void testUpdate(){
        newCustomer.changeName("updated-user");
        customerJdbcRepository.update(newCustomer);

        var all = customerJdbcRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all, everyItem(samePropertyValuesAs(newCustomer)));

        var retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(), is(false));
        assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));
    }

    @Test
    @Order(7)
    @DisplayName("???????????? ?????????")
    public void testTransaction(){
//        var prevOne = customerJdbcRepository.findById(newCustomer.getCustomer_id());
//        assertThat(prevOne.isEmpty(), is(false));
//        var newOne = new Customer(UUID.randomUUID(),"a", "a@gamil.com", LocalDateTime.now());
//        var insertedNewOne = customerJdbcRepository.insert(newOne);
//        try{
//            customerJdbcRepository.testTransaction(
//                    new Customer(insertedNewOne.getCustomer_id(),
//                        "b",
//                        prevOne.get().getEmail(),
//                        newOne.getCreatedAt()));
//        } catch (DataAccessException e){
//            logger.error("Got error when testing transaction",e);
//        }
//
//        var maybeNewOne = customerJdbcRepository.findById(insertedNewOne.getCustomer_id());
//        assertThat(maybeNewOne.isEmpty(), is(false));
//        assertThat(maybeNewOne.get(), samePropertyValuesAs(newOne));
    }

}