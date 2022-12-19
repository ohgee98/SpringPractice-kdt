package org.prgms.kdt.voucher;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//@Primary
@Repository
@Qualifier("memory")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 스쿱 정의
public class MemoryVoucherRepository implements VoucherRepository, InitializingBean, DisposableBean {
    // 메모리 관리하는 클래스
    private final Map<UUID, Voucher> storage = new ConcurrentHashMap<>(); // thread safety를 위해 ConcurrentHashMap 사용
    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(storage.get(voucherId)); // id가 null인 경우 Optional사용해 empty로 반환됨
    }

    @Override
    public Voucher insert(Voucher voucher) {
        storage.put(voucher.getVoucherId(),voucher);
        return voucher;
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("postConstruct called!");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet called!");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("preDestroy called!");

    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy called!");
    }
}
