package org.prgms.kdt;
// 여기가 Spring Boot임
import org.prgms.kdt.order.OrderProperties;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.JdbcVoucherRepository;
import org.prgms.kdt.voucher.VoucherRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.text.MessageFormat;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"org.prgms.kdt.order","org.prgms.kdt.voucher", "org.prgms.kdt.configuration"})
public class KdtApplication {

	public static void main(String[] args) {

		var springApplictaion = new SpringApplication(KdtApplication.class);
//		springApplictaion.setAdditionalProfiles("local");
		var applicationContext = springApplictaion.run(args);

		var orderProperties = applicationContext.getBean(OrderProperties.class);
		System.out.println(MessageFormat.format("version -> {0}", orderProperties.getVersion()));
		System.out.println(MessageFormat.format("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
		System.out.println(MessageFormat.format("supportVendors -> {0}", orderProperties.getSupportVendors()));
		System.out.println(MessageFormat.format("description -> {0}", orderProperties.getDescription()));

		var voucherRepository = applicationContext.getBean(VoucherRepository.class);

		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));
	}

}
