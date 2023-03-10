package org.prgms.kdt.customer.service;

import org.prgms.kdt.customer.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    void createCustomers(List<Customer> customers);

    Customer createCustomer(String email, String name);

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomer(UUID customerId);
}
