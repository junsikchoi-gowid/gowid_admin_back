package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.helper.EmailValidator;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.core.domain.repository.querydsl.UserCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
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
    public Page<UserCustomRepository.UserListDto> getUserList(UserCustomRepository.UserListDto dto, Pageable pageable) {
        return userRepository.userList(dto, pageable);
    }

    @Transactional(readOnly = true)
    public UserCustomRepository.UserInfoDto getUserInfo(Long idxUser) {
        return userRepository.userInfo(idxUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateUserInfo(Long idxUser, AdminDto.UpdateUserDto dto) {
        User user = userRepository.findById(idxUser).orElseThrow(
            () -> UserNotFoundException.builder().build()
        );

        if(!EmailValidator.isValid(dto.getEmail())) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED);
        }

        if(userRepository.findByAuthentication_EnabledAndEmailAndIdxNot(true, dto.getEmail(), idxUser).isPresent()) {
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
