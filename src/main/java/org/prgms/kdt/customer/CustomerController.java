package org.prgms.kdt.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/api/v1/customers") //api는 version을 꼭 명시해줘야함
    @ResponseBody // 별다른 동작하지 않아도 JSON으로 응답이 옴
    public List<Customer> findCustomers(Model model){
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers")
    public String viewCustomersPage(Model model){
        var allCustomers = customerService.getAllCustomers();
        model.addAttribute("serverTime", LocalDateTime.now());
        model.addAttribute("customers", allCustomers);
        return "views/customers";
    }

    @GetMapping("/customers/{customerId}")
    public String findCustomer(@PathVariable("customerId") UUID customerId, Model model){
        var maybeCustomer = customerService.getCustomer(customerId);
        if (maybeCustomer.isPresent()){
            model.addAttribute("customer", maybeCustomer.get());
            return "views/customer-details";
        } else {
            return "views/404";
        }
    }

    @GetMapping("/customers/new")
    public String viewNewCustomerPage(){
        return "views/new-customers";
    }

    @PostMapping("/customers/new")
    public String addNewCustomer(CreateCustomerRequest createCustomerRequest){
        customerService.createCustomer(createCustomerRequest.email(), createCustomerRequest.name());
        return "redirect:/customers";
    }
}
