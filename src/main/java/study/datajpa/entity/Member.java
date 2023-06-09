package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) // "team" 넣으면 무한루프 돌 가능성 있음. 가급적이면 연관관계 필드는 ToString 하지 말 것.
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) // ToOne 관계는 항상 LAZY
    @JoinColumn(name = "team_id")
    private Team team;

//    protected Member() { // @NoArgsConstructor 로 대체
        // JPA 에서 엔티티는 기본생성자가 하나 있어야함.
        // 아무데서나 호출되지 않게 하기 위하여 protected로 선언.
        // private으로 막아버리면 proxy에서 쓰지 못함.
//    }
    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null)
            changeTeam(team);
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
