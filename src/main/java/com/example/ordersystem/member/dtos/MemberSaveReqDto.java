package com.example.ordersystem.member.dtos;

import com.example.ordersystem.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberSaveReqDto {
    private String name;
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public Member toEntity(String encodedPassword) {
        return Member.builder().name(this.name).email(this.email).password(encodedPassword).build();
    }
}
