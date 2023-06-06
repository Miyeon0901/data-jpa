package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;

    protected Member() {
        // JPA 에서 엔티티는 기본생성자가 하나 있어야함.
        // 아무데서나 호출되지 않게 하기 위하여 protected로 선언.
        // private으로 막아버리면 proxy에서 쓰지 못함.
    }
    public Member(String username) {
        this.username = username;
    }
}
