package org.prgms.kdt.customer;

import org.prgms.kdt.JdbcCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.*;

@Repository
@Primary
public class CustomerNamedJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<Customer> customerRowMapper = (resultSet,i) ->{
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var customerName = resultSet.getString("name");
        var customerEmail = resultSet.getString("email");
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
                resultSet.getTimestamp("last_login_at").toLocalDateTime() : null ;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

        return new Customer(customerId,customerName,customerEmail,lastLoginAt,createdAt);
    } ;

    public CustomerNamedJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Map<String ,Object> toParamMAp(Customer customer) {
        return new HashMap<String, Object>() {{
            put("customerId", customer.getCustomerId().toString().getBytes());
            put("name", customer.getName());
            put("email", customer.getEmail());
            put("createdAt", Timestamp.valueOf(customer.getCreatedAt()));
            put("lastLoginAt", customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
        }};
    }

    @Override
    public Customer insert(Customer customer) {

        var update = jdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UNHEX(REPLACE(:customerId, '-', '')), :name, :email, :createdAt)",
                toParamMAp(customer));

        if (update!=1){
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update("UPDATE customers SET name = :name, email = :email, last_login_at = :lastLoginAt  WHERE customer_id = UNHEX(REPLACE(:customerId, '-', ''))",
                toParamMAp(customer));
        if (update!=1){
            throw new RuntimeException("Nothing was updated");
        }
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Collections.emptyMap() ,Integer.class); // aueryForObject는 빈 Map을 인자로 전달해줘야함
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UNHEX(REPLACE(:customerId, '-', ''))",
                    Collections.singletonMap("customerId",customerId.toString().getBytes()),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result",e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE name = :name",
                    Collections.singletonMap("name",name),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result",e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers WHERE email = :email",
                    Collections.singletonMap("email",email),
                    customerRowMapper));
        } catch (EmptyResultDataAccessException e){
            logger.error("Got empty result",e);
            return Optional.empty();
        }
    }

//    public void testTransaction(Customer customer){
//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus status) {
//                jdbcTemplate.update("UPDATE customers SET name = :name  WHERE customer_id = UNHEX(REPLACE(:customer_id, '-', ''))",toParamMAp(customer));
//                jdbcTemplate.update("UPDATE customers SET email = :email  WHERE customer_id = UNHEX(REPLACE(:customer_id, '-', ''))",toParamMAp(customer));
//            }
//        });
//    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM customers", Collections.emptyMap());
    }

    static UUID toUUID(byte[] bytes){
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
