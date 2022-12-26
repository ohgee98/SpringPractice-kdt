package org.prgms.kdt.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.MemoryVoucherRepository;
import org.prgms.kdt.voucher.VoucherRepository;
import org.prgms.kdt.voucher.VoucherService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Mockito 실습
class OrderServiceTest {

    class OrderRepositoryStub implements OrderRepository {
        @Override
        public Order insert(Order order) {
            return null;
        }
    }

    @Test
    @DisplayName("오더가 생성되야한다. (stub)")
    void createOrder() {
        // Given
        var voucherRepository = new MemoryVoucherRepository();
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);
        var sut = new OrderService(new VoucherService(voucherRepository), new MemoryOrderRepository());

        // When
        var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)), fixedAmountVoucher.getVoucherId());

        // Then (행위에 대한 assert를 함)
        assertThat(order.totalAmount(), is(100L)); // 뒤에 L을 붙이는 것은 long이라고 명시해주는 것
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));

    }

    @Test
    @DisplayName("오더가 생성되야한다. (mock)")
    void createOrderByMock() {
        // Given (setup)
        var voucherServiceMock = mock(VoucherService.class);
        var orderRepositoryMock = mock(OrderRepository.class);
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherId())).thenReturn(fixedAmountVoucher); // mock으로 기술한 부분만 동작 (정의한 부분만 리턴)
        var sut = new OrderService(voucherServiceMock,orderRepositoryMock);

        // When
        var order = sut.createOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), 200, 1)),
                fixedAmountVoucher.getVoucherId());

        // Then
        assertThat(order.totalAmount(), is(100L)); // 뒤에 L을 붙이는 것은 long이라고 명시해주는 것
        assertThat(order.getVoucher().isEmpty(), is(false));
        var inOrder = inOrder(voucherServiceMock, orderRepositoryMock); // 실제 실행 순서를 판단해야 하는 경우
        inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId()); // 해당 객체가 호출이 되어졌다를 verify 하는 것 (행위 관점)
        inOrder.verify(orderRepositoryMock).insert(order);
        inOrder.verify(voucherServiceMock).useVoucher(fixedAmountVoucher); // 메소드 호출 여부 검증


    }
}