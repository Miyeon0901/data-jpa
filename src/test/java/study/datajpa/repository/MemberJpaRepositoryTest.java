package study.datajpa.repository;

import org.junit.jupiter.api.Test; // junit5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringRunner.class) // junit5를 쓸때 필요 없음.
@SpringBootTest
@Transactional // 모든 데이터 변경은 transaction 안에서 이루어져야함.
@Rollback(false) // 롤백을 하면 영속성 컨텍스트 flush를 하지 않음. 쿼리가 안날라감.
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() throws Exception {
        Member member = new Member("miyeon");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        // 같은 트랜잭션 안에서는 영속성 컨텍스트의 동일성을 보장한다. 1차 캐시
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        
        assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장.
    }

}