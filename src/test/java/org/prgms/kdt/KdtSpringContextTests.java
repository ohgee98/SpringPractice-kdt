package org.prgms.kdt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.prgms.kdt.order.MemoryOrderRepository;
import org.prgms.kdt.order.OrderItem;
import org.prgms.kdt.order.OrderService;
import org.prgms.kdt.order.OrderStatus;
import org.prgms.kdt.voucher.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

// 통합 테스트 실습 (Mock이 아닌 실제 Bean을 사용함)

//@ExtendWith(SpringExtension.class) // juint과 spring testContext를 사용할 수 있게 해줌
//@ContextConfiguration // 별도로 작성하지 않아도 안에 있는 configuration을 찾게 됨
// 이 둘을 합친 것이 아래의 애노테이션임
@SpringJUnitConfig // testContext framework가 만들어짐
@ActiveProfiles("test") // profile로 인해 나눠지면 적용할 것을 명시
public class KdtSpringContextTests {
    @Configuration // configuration을 통해 applicationContext가 만들어짐
    @ComponentScan(basePackages = {"org.prgms.kdt.voucher","org.prgms.kdt.order"})
    static class Config {}

//    static class Config {
//        @Bean
//        VoucherRepository voucherRepository(){
//            return new VoucherRepository() {
//                @Override
//                public Optional<Voucher> findById(UUID voucherId) {
//                    return Optional.empty();
//                }
//
//                @Override
//                public Voucher insert(Voucher voucher) {
//                    return null;
//                }
//            };
//        }
//    }
    @Autowired
    ApplicationContext context;

    @Autowired
    OrderService orderService;

    @Autowired
    VoucherRepository voucherRepository;

    @Test
    @DisplayName("applicationContext가 생성 되야한다.")
    public void testApplicationContext() {
        assertThat(context, notNullValue());
    }

    @Test
    @DisplayName("VoucherRepository가 빈으로 등록되어 있어야 한다.")
    public void testVoucherRepositoryCreation() {
        var bean = context.getBean(VoucherRepository.class);
        assertThat(bean, notNullValue());
    }

    @Test
    @DisplayName("orderService를 사용해서 주문을 생성할 수 있다.")
    public void testOrderService() {
        // Given
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);

        // When
        var order = orderService.createOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), 200, 1)),
                fixedAmountVoucher.getVoucherId());

        // Then (행위에 대한 assert를 함)
        assertThat(order.totalAmount(), is(100L)); // 뒤에 L을 붙이는 것은 long이라고 명시해주는 것
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));
    }
}

