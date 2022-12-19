package org.prgms.kdt.order;

import java.util.UUID;

public class OrderItem { // value object 클래스

    public final UUID productID;
    public final long productPrice;
    public final long quantity;

    public UUID getProductID() {
        return productID;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public long getQuantity() {
        return quantity;
    }


    public OrderItem(UUID productID, long productPrice, long quantity) {
        this.productID = productID;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }
}
