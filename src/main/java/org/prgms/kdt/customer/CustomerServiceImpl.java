package org.prgms.kdt.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional // 오류 발생하면 전체 롤백시키도록
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }
}
