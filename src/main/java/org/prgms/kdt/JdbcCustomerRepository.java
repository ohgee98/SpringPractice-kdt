package org.prgms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";
    private final String SELECT_ALL_SQL = "select * from customers";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email) values (UUID_TO_BIN(?),?,?)";
    private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";

    private final String DELETE_ALL_SQL = "DELETE FROM customers";


    public List<String> findNames(String name) {
        //       Java 10 부터 tryResource라는게 있어서 자동으로 close 할 수 있음 (더 간단)
//        var SELECT_SQL = "select * from customers WHERE name = '"+name+"'";
        List<String> names = new ArrayList<>();

        try (
                // Connection이 autoClose를 구현해서 {}block이 끝나면 알아서 닫아줌
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
//                var statement = connection.createStatement();
                var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
//                var resultSet = statement.executeQuery(SELECT_SQL);
        ) {
            statement.setString(1, name); // 파라미터 세팅 : 파라미터 인자가 n개 일 수 있으니 순서를 명시 (1부터 시작)
            logger.info("statement -> {}", statement);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) { // 결과값을 next로 넘기며 하나하나 살필 수 있음
                    var customerName = resultSet.getString("name");
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime(); // date쪽은 not null이라 null이 들어오면 error 날 수 있으니 유의
                    logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }

        return names;
    }

    public List<String> findAllName() {
        List<String> names = new ArrayList<>();

        try (
                // Connection이 autoClose를 구현해서 {}block이 끝나면 알아서 닫아줌
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) { // 결과값을 next로 넘기며 하나하나 살필 수 있음
                var customerName = resultSet.getString("name");
                var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime(); // date쪽은 not null이라 null이 들어오면 error 날 수 있으니 유의
//                    logger.info("customer id -> {}, name -> {}, createdAt -> {}", customerId, customerName, createdAt);
                names.add(customerName);
            }
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }

        return names;
    }

    public List<UUID> findAllIds() {
        List<UUID> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                var customerName = resultSet.getString("name");
                var customerId = toUUID(resultSet.getBytes("customer_id"));
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                names.add(customerId);
            }
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }

        return names;
    }

    public int insertCustomer(UUID customerId, String name, String email) {
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
                var statement = connection.prepareStatement(INSERT_SQL);
        ) {
            statement.setBytes(1, customerId.toString().getBytes());
            statement.setString(2, name);
            statement.setString(3, email);
            return statement.executeUpdate();

        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return 0;
    }

    public int updateCustomerName(UUID customerId, String name) {
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
                var statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
        ) {
            statement.setString(1, name);
            statement.setBytes(2, customerId.toString().getBytes());
            return statement.executeUpdate();

        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return 0;
    }

    public int deleteAllCustomers() {
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "root1234!");
                var statement = connection.prepareStatement(DELETE_ALL_SQL);
        ) {
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            logger.error("Got error while closing connection", throwables);
        }
        return 0;
    }

    static UUID toUUID(byte[] bytes){ // 버전 4를 맞추기 위해서 유틸성을 밖으로 빼 생성하기
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
    public static void main(String[] args) throws SQLException {

        var customerRepository = new JdbcCustomerRepository();

        var count = customerRepository.deleteAllCustomers(); // 삭제
        logger.info("deleted count -> {}", count);

        var customerId = UUID.randomUUID();
        logger.info("created customerId -> {}", customerId);
        logger.info("created UUID Version -> {}", customerId.version());

        customerRepository.insertCustomer(customerId, "new-user", "new-user@gmail.com");
        customerRepository.findAllIds().forEach(v -> logger.info("Found customerId : {} and version : {}", v, v.version()));
        // 만들 때 UUID 버전은 4이고 검색해서 가져올 때 UUID는 버전 3이라서 검색해서 가져올 때 둘의 id가 달라짐

//        customerRepository.insertCustomer(UUID.randomUUID(), "new-user", "new-user@gmail.com");
//        var customer2 = UUID.randomUUID();
//        customerRepository.insertCustomer(customer2, "new-user2", "new-user2@gmail.com");
//        customerRepository.findAllName().forEach(v -> logger.info("Found name : {}", v));
//        customerRepository.updateCustomerName(customer2, "updated-user2");
//        customerRepository.findAllName().forEach(v -> logger.info("Found name : {}", v));

        // 이런 SQLInjection 공격을 막기 위해 prepare statement를 사용하는게 좋다
        // 넣었던게 문자열 조합을 한게 아니라 그냥 name 변수 자체에 들어가서 찾지 못하게 되는 것임
        // creatStatment를 사용 했을 때는 들어간 문자열이 쿼리문으로 동작했었음


    }
}
