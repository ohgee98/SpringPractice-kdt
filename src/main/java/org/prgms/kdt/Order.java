package org.prgms.kdt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Order { // entity 클래스
    private final UUID orderid;
    private final UUID customerID;
    private final List<OrderItem> orderItems;
    private Optional<Voucher> voucher;
    private OrderStatus orderStatus = OrderStatus.ACCEPTED;

    public Order(UUID orderid, UUID customerID, List<OrderItem> orderItems) {
        this.orderid = orderid;
        this.customerID = customerID;
        this.orderItems = orderItems;
        this.voucher = Optional.empty();
    }

    public Order(UUID orderid, UUID customerID, List<OrderItem> orderItems, Voucher voucher) {
        this.orderid = orderid;
        this.customerID = customerID;
        this.orderItems = orderItems;
        this.voucher = Optional.of(voucher);
    }

    public long totalAmount(){
        var beforeDiscount = orderItems.stream().map( v-> v.getProductPrice() * v.getQuantity())
                .reduce(0L, Long::sum);

        return voucher.map(value -> value.discount(beforeDiscount)).orElse(beforeDiscount);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
