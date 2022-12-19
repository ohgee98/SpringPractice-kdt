package org.prgms.kdt;

import org.prgms.kdt.order.OrderItem;
import org.prgms.kdt.order.OrderService;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

public class OrderTester {
    public static void main(String[] args) {

        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

        var customerID = UUID.randomUUID();

        // Qualfier로 지정한 것을 가져오기 위해서
        var voucherRepository = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
        var voucherRepository2 = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
        System.out.println(MessageFormat.format("voucherRepository{0}", voucherRepository)); //객체가 가리키는 메모리 주소 출력하게 됨
        System.out.println(MessageFormat.format("voucherRepository{0}", voucherRepository2));
        System.out.println(MessageFormat.format("voucherRepository == voucherRepository2 => {0}",voucherRepository==voucherRepository2));
//        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customerID, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L,1));
        }}, voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

        applicationContext.close(); // 컨테이너에 등록된 모든 bean이 소멸됨. 이후 callback 실행

    }
}
