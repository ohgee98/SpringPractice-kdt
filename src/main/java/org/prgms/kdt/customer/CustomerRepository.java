package org.prgms.kdt.customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer insert(Customer customer);

    Customer update(Customer customer);

    // Customer save(Customer customer); // 이건 심화과정에서 다룸

    int count();

    List<Customer> findAll();

    Optional<Customer> findById(UUID cutomerId); // null처리 고민하지 않도록
    Optional<Customer> findByName(String name);
    Optional<Customer> findByEmail(String email);

    void deleteAll();
}
