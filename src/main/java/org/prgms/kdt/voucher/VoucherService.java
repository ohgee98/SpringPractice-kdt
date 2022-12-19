package org.prgms.kdt.voucher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
public class VoucherService {

//    @Autowired //자동으로 생성자 생성 가능
    private VoucherRepository voucherRepository;

    @Autowired
    public VoucherService(@Qualifier("memory") VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public VoucherService(VoucherRepository voucherRepository, String dummy) {
        this.voucherRepository = voucherRepository;
    }

    public Voucher getVoucher(UUID voucherId) {
        return voucherRepository
                .findById(voucherId)
                .orElseThrow( () -> new RuntimeException(MessageFormat.format("Can not find a voucher for {0}", voucherId)));
    }

    public void useVoucher(Voucher voucher) {
    }

//    @Autowired
//    public void setVoucherRepository(VoucherRepository voucherRepository) {
//        this.voucherRepository = voucherRepository;
//    }
}
