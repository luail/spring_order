package com.example.ordersystem.member.controller;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.dtos.LoginDto;
import com.example.ordersystem.member.dtos.MemberResDto;
import com.example.ordersystem.member.dtos.MemberSaveReqDto;
import com.example.ordersystem.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody MemberSaveReqDto dto) {
        Long memberId = memberService.create(dto);
        return new ResponseEntity<>(memberId, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<MemberResDto> memberListResDto = memberService.findMemberList();
        return new ResponseEntity<>(memberListResDto,HttpStatus.OK);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody LoginDto dto) {
//        email, password 검증
        Member member = memberService.login(dto);
//        토큰 생성 및 return
        String token = jwtTokenProvider.createToken();
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", token);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

}
