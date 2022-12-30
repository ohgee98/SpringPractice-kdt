package org.prgms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

//    @Around("org.prgms.kdt.aop.commonPointcut.repositoryInsertMethodPointcut()") // 포인트컷 PCD(지정자) : 시점(접근지정자 적용될반환값 패키지와클래스명 메소드명) 개수 등 뭐든 상관 없을 때 .. 사용
    @Around("@annotation(org.prgms.kdt.aop.TrackTime)") // 호출 후 반환되기까지 걸린 시간을 알려주려고
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Before method called. {}", joinPoint.getSignature().toString());
        var startTime = System.nanoTime(); // 1 -> 1,000,000,000
        var result = joinPoint.proceed();
        var endTime = System.nanoTime()-startTime;
        log.info("After method called with result => {} and time taken {} nanoseconds", result,endTime);
        return result;
    }
}
