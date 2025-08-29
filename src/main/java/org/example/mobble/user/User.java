package org.example.mobble.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_tb")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 테이블 설정 후 추가

    // 아이디
    private String username;

    // 비밀번호
    private String password;

    // 이메일
    // 이메일 인증도 추가할 건지?
    private String email;

    // reCAPTCHA 추가? : 봇 생성 방지용
}

