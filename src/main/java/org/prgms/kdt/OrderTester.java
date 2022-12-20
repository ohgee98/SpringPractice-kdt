package org.prgms.kdt;
// 여기는 그냥 Spring Framework
import org.prgms.kdt.order.OrderItem;
import org.prgms.kdt.order.OrderProperties;
import org.prgms.kdt.order.OrderService;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.JdbcVoucherRepository;
import org.prgms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTester {
    public static void main(String[] args) throws IOException {

        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
//        var applicationContext = new AnnotationConfigApplicationContext();
//        applicationContext.register(AppConfiguration.class);
//        var environment = applicationContext.getEnvironment();
//        environment.setActiveProfiles("local");
//        applicationContext.refresh();


        // Environment와 Property 실습
//        var version = environment.getProperty("kdt.version");
//        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class); // 반환하는 값에 대한 형변환을 위해서
//        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
//        var description = environment.getProperty("kdt.description", List.class);
//        System.out.println(MessageFormat.format("version -> {0}", version));
//        System.out.println(MessageFormat.format("minimumOrderAmount -> {0}", minimumOrderAmount));
//        System.out.println(MessageFormat.format("description -> {0}", description));

        var orderProperties = applicationContext.getBean(OrderProperties.class);
//        System.out.println(MessageFormat.format("version -> {0}", orderProperties.getVersion()));
//        System.out.println(MessageFormat.format("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
//        System.out.println(MessageFormat.format("supportVendors -> {0}", orderProperties.getSupportVendors()));
//        System.out.println(MessageFormat.format("description -> {0}", orderProperties.getDescription()));

        // Resource 실습
        var resource = applicationContext.getResource("classpath:application.yaml"); // class Path 상에서 (구현체가 ClassPathResource)
        var resource2 = applicationContext.getResource("file:test/sample.txt"); //경로 지정해서 가져오기 (구현체가 FileUrlResource)
        var resource3 = applicationContext.getResource("https://stackoverflow.com/"); // 외부 사이트 가져오기 (file이 아님. UrlResource)

        System.out.println(MessageFormat.format("Resource ->{0}", resource3.getClass().getCanonicalName()));
//        var strings = Files.readAllLines(resource3.getFile().toPath()); // 개행이 될 때마다 각 원소가 되어 배열 형태로 저장
//        System.out.println(strings.stream().reduce("", (a,b) -> a + "\n" + b)); // 개행 추가해서 쓴 내용 그대로 보기 위함

        var readableByteChannel = Channels.newChannel(resource3.getURL().openStream()); // 외부 Url은 file이 아니기 때문에 Channel을 사용해서 확인할 수 있음
        var bufferedReader = new BufferedReader(Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
        var contents = bufferedReader.lines().collect(Collectors.joining("\n"));
        System.out.println(contents);

        var customerID = UUID.randomUUID();

        // Qualfier로 지정한 것을 가져오기 위해서
//        var voucherRepository = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);

        // 싱글톤 패턴과 프로토타입 패턴 비교 실습
//        var voucherRepository2 = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
//        System.out.println(MessageFormat.format("voucherRepository{0}", voucherRepository)); //객체가 가리키는 메모리 주소 출력하게 됨
//        System.out.println(MessageFormat.format("voucherRepository{0}", voucherRepository2));
//        System.out.println(MessageFormat.format("voucherRepository == voucherRepository2 => {0}",voucherRepository==voucherRepository2));
//        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customerID, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L,1));
        }}, voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

        applicationContext.close(); // 컨테이너에 등록된 모든 bean이 소멸됨. 이후 callback 실행

    }
}
