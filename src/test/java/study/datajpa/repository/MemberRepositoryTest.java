package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception {
        System.out.println("MemberRepository = " + memberRepository.getClass());
        // class com.sun.proxy.$Proxy103
        // spring data jpa가 interface가 구현 클래스를 만들어줌.
        Member member = new Member("younghoon");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장.
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();
        result.stream().forEach(System.out::println);
    }

    @Test
    public void findMemberDto() {
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);

        Member member1 = new Member("AAA", 10, team1);
        Member member2 = new Member("BBB", 20, team2);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> result = memberRepository.findMemberDto();
        result.stream().forEach(System.out::println);
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        result.stream().forEach(System.out::println);
    }

    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> list = memberRepository.findListByUsername("AAA");
        Member member = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");
        // 단건 조회인데 결과가 여러 건이면 exception 발생
        // javax.persistence.NonUniqueResultException -> org.springframework.dao.IncorrectResultSizeDataAccessException
        // repository 에서 사용된 기술과 상관없이 서비스 계층의 springframework exception으로 translation

        System.out.println("findListSize: " + list.size()); // 결과가 없어도 null이 아닌 빈 collection을 반환해 준다.
        System.out.println("findList: " + list);
        System.out.println("findMember: " + member); // 결과가 없으면 null이 반환됨.
        System.out.println("findOptional: " + optional.orElse(new Member("miyeon")));
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username"); // Pageable 이 조상 인터페이스.

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // page니까 알아서 total count 쿼리도 같이 나감.
//        Slice<Member> slice = memberRepository.findByAge(age, pageRequest); // count 쿼리 안나감. limit가 +1 되서 날아감.

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); // API로 반환하는건 DTO로!

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

//        List<Member> content = slice.getContent();
//
//        assertThat(content.size()).isEqualTo(3);
//        assertThat(slice.getNumber()).isEqualTo(0);
//        assertThat(slice.isFirst()).isTrue();
//        assertThat(slice.hasNext()).isTrue();

    }
}