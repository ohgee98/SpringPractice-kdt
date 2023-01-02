package org.prgms.kdt.voucher;

import org.prgms.kdt.voucher.Voucher;

import java.util.UUID;

public class FixedAmountVoucher implements Voucher {
    private static final long MAX_VOUCHER_AMOUNT = 10000;
    private final UUID voucherId;
    private final long amount;
    public FixedAmountVoucher(UUID voucherId,long amount) {
        if (amount<0) throw new IllegalArgumentException("Amount should be postive."); //할인이 마이너스 될 수 없게 예외 발생시키기
        if (amount==0) throw new IllegalArgumentException("Amount should not be zero.");
        if (amount>MAX_VOUCHER_AMOUNT) throw new IllegalArgumentException("Amount should be less than " + MAX_VOUCHER_AMOUNT); // 왜 formatted가 안뜰까?
        this.voucherId = voucherId;
        this.amount = amount;
    }

    @Override
    public UUID getVoucherId() {
        return voucherId;
    }

    public long discount(long beforeDiscount){
        var discountAmount = beforeDiscount - amount;
        return (discountAmount < 0) ? 0 : discountAmount;
    }

    @Override
    public String toString(){
        return "FixedAmountVoucher{" +
                "voucherId=" + voucherId +
                ", amount=" + amount +
                '}';
    }



}
