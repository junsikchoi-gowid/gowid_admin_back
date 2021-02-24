package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.core.domain.repository.querydsl.UserCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public ResponseEntity getUserList(String keyWord, Pageable pageable) {
        Page<UserCustomRepository.UserListDto> userList = userRepository.userList(keyWord, pageable);
        return ResponseEntity.ok().body(
            BusinessResponse.builder().data(userList).build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseEntity getUserInfo(Long idxUser) {
        UserCustomRepository.UserInfoDto userInfo = userRepository.userInfo(idxUser);
        return ResponseEntity.ok().body(
            BusinessResponse.builder().data(userInfo).build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateUserInfo(Long idxUser, AdminDto.UpdateUserDto dto) {
        User user = userRepository.findById(idxUser).orElseThrow(
            () -> UserNotFoundException.builder().build()
        );

        if(userService.isPresentEmail(dto.getEmail())) {
            throw AlreadyExistException.builder()
                .category("email")
                .resource(dto.getEmail())
                .build();
        }

        userRepository.save(user.name(dto.getUserName())
            .mdn(dto.getPhone())
            .corpName(dto.getCorpName())
            .position(dto.getPosition())
            .email(dto.getEmail())
        );

        return ResponseEntity.ok().body(
            BusinessResponse.builder().build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity initUserInfo(Long idxUser) {
        userService.initUserInfo(idxUser);

        return ResponseEntity.ok().body(
            BusinessResponse.builder().build()
        );
    }
}
