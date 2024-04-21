package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.UnexpectedException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService transaction off
     * memberRepository transaction on
     * logRepository transaction on
     */
    @Test
    void outerTxOff_success() {
        //given
        String username ="outerTxOff_success";

        //when
        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService transaction off
     * memberRepository transaction on
     * logRepository transaction on Exception
     */
    @Test
    void outerTxOff_fail() {
        //given
        String username ="로그예외_outerTxOff_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService transaction on
     * memberRepository transaction off
     * logRepository transaction off
     */
    @Test
    void singleTx() {
        //given
        String username ="singleTx";

        //when, service 에서만 transaction 시작, 단일 트랜젝션
        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService transaction on
     * memberRepository transaction on
     * logRepository transaction on
     */
    @Test
    void outerTxOn_success() {
        //given
        String username ="outerTxOn_success";

        //when
        memberService.joinV1(username);

        //when: 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService transaction on
     * memberRepository transaction on
     * logRepository transaction on Exception
     */
    @Test
    void outerTxOn_fail() {
        //given
        String username ="로그예외_outerTxOn_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //when
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService transaction on
     * memberRepository transaction on
     * logRepository transaction on Exception
     */
    @Test
    void recoverException_fail() {
        //given
        String username ="로그예외_recoverException_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //when
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService transaction on
     * memberRepository transaction on
     * logRepository transaction on(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String username ="로그예외_recoverException_success";

        //when
        memberService.joinV2(username);

        //when
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }
}