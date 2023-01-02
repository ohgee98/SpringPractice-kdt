package org.prgms.kdt.voucher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class HamcrestAssertionTest {

    @Test
    @DisplayName("여러 hamcrset matcher 테스트")
    void hamcrestTest(){
        assertEquals(2,1+1);
        // hamcrest 메소드, 실제 값과  mathcer 순서로 들어감
        assertThat(1+1,equalTo(2));
        assertThat(1+1,is(2));
        assertThat(1+1,anyOf(is(1),is(2))); // 값이 변동이 될 수 있을 때

        assertNotEquals(1, 1+1);
        // hamcrest 메소드
        assertThat(1+1, not(equalTo(1)));
    }

    @Test
    @DisplayName("컬렉션에 대한 matcher 테스트")
    void hamcrestListMatcherTest(){
        var prices = List.of(2,3,4);
        assertThat(prices,hasSize(3)); // 사이즈 확인
        assertThat(prices, everyItem(greaterThan(1)));
        assertThat(prices, containsInAnyOrder(3,4,2)); // 순서가 중요하면 contains를 쓰고, 안 중요하면 이걸 씀
        assertThat(prices,hasItem(greaterThanOrEqualTo(2)));
    }
}
