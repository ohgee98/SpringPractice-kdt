package org.prgms.kdt.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {

    // 어떤 값이 바뀌고 바뀌지 않는지 유의해서 정하기 (바뀌지 않으면 final)
    private final UUID customer_id;
    private String name;
    private final String email;
    private LocalDateTime lastLonginAt;
    private final LocalDateTime createdAt;

    public Customer(UUID customer_id, String name, String email, LocalDateTime createdAt) {
        validateName(name);
        this.customer_id = customer_id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Customer(UUID customer_id, String name, String email, LocalDateTime lastLonginAt, LocalDateTime createdAt) {
        validateName(name);
        this.customer_id = customer_id;
        this.name = name;
        this.email = email;
        this.lastLonginAt = lastLonginAt;
        this.createdAt = createdAt;
    }

    // getter는 다 만들어도 setter는 무분별하게 사용할 수 없음.
    // setter는 entity를 바꾸는 것이라서 가급적 비즈니스 로직에서 처리하도록 함.
    public void changeName(String name){
        validateName(name);
        this.name = name;
    }

    private static void validateName(String name) {
        if (name.isBlank()){
            throw new RuntimeException("Name should not be blank");
        }
    }

    public void login(){
        this.lastLonginAt = LocalDateTime.now();
    }

    public UUID getCustomer_id() {
        return customer_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getLastLonginAt() {
        return lastLonginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
