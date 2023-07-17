package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{ // repository 이름 + Impl
    private final EntityManager em;

    // RequiredArgsConstructor 써주면 아래 생략 가능.
//    public MemberRepositoryImpl(EntityManager em) { // 생성자가 하나만 있으면 Spring이 알아서 injection 해줌.
//        this.em = em;
//    }
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
