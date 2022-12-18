package org.prgms.kdt;

import java.util.List;
import java.util.UUID;

public class OrderService {
    private final VoucherService voucherService;
    private final OrderRepository orderRepository;

    public OrderService(VoucherService voucherService, OrderRepository orderRepository) {
        this.voucherService = voucherService;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(UUID customrId, List<OrderItem> orderItems) {
        var order = new Order(UUID.randomUUID(), customrId, orderItems);
        orderRepository.insert(order);
        return order;
    }

    public Order createOrder(UUID customrId, List<OrderItem> orderItems, UUID voucherId) {
        var voucher = voucherService.getVoucher(voucherId);
        var order = new Order(UUID.randomUUID(), customrId, orderItems, voucher);
        orderRepository.insert(order);
        voucherService.useVoucher(voucher);
        return order;
    }


}
