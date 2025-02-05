package com.example.ordersystem.member.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.dtos.LoginDto;
import com.example.ordersystem.member.dtos.MemberResDto;
import com.example.ordersystem.member.dtos.MemberSaveReqDto;
import com.example.ordersystem.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long create(MemberSaveReqDto dto) throws IllegalArgumentException {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("중복 이메일입니다.");
        }

        Member member = memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        return member.getId();
    }

    public List<MemberResDto> findMemberList() {
        return memberRepository.findAll().stream().map(m->m.memberListResDtoFromEntity()).toList();
    }

    public MemberResDto myinfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(()->new EntityNotFoundException("없는 아이디입니다."));
        return member.memberListResDtoFromEntity();
    }

    public Member login(LoginDto dto){
        boolean check = true;
//        email존재여부
        Optional<Member> optionalMember = memberRepository.findByEmail(dto.getEmail());
        if(!optionalMember.isPresent()){
            check = false;
        }
//        password일치 여부
        if(!passwordEncoder.matches(dto.getPassword(), optionalMember.get().getPassword())){
            check =false;
        }
        if(!check){
            throw new IllegalArgumentException("email 또는 비밀번호가 일치하지 않습니다.");
        }
        return optionalMember.get();
    }


}
