package org.prgms.kdt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class AppConfiguration {

    @Bean
    public VoucherRepository voucherRepository(){
        return new VoucherRepository() {
            @Override
            public Optional<Voucher> findById(UUID voucherId) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public OrderRepository orderRepository(){
        return new OrderRepository() {
            @Override
            public void insert(Order order) {

            }
        };
    }
    @Bean
    // 매개변수로 객체를 넣어주면 스프링이 알아서 찾아가지고 객체 생성해줌..?
    public VoucherService voucherService(VoucherRepository voucherRepository){
        return new VoucherService(voucherRepository);
    }

    @Bean
    public OrderService orderService(VoucherService voucherService, OrderRepository orderRepository){
        return new OrderService(voucherService, orderRepository);
    }
}
