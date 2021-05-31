package com.nomadconnection.dapp.api.v2.service.admin;

import com.nomadconnection.dapp.api.exception.AlreadyExistException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.helper.EmailValidator;
import com.nomadconnection.dapp.api.service.SaasTrackerService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.service.expense.rpc.ExpenseRpc;
import com.nomadconnection.dapp.api.v2.dto.AdminDto;
import com.nomadconnection.dapp.core.domain.embed.ExpenseStatus;
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

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final SaasTrackerService saasTrackerService;
    private final ExpenseRpc expenseRpc;

    @Transactional(readOnly = true)
    public Page<UserCustomRepository.UserListDto> getUserList(UserCustomRepository.SearchUserListDto dto, Pageable pageable) {
        return userRepository.userList(dto, pageable);
    }

    @Transactional(readOnly = true)
    public UserCustomRepository.UserInfoDto getUserInfo(Long idxUser) {
        User user = userService.getUser(idxUser);
        UserCustomRepository.UserInfoDto userInfo = userRepository.userInfo(idxUser);

        try {
            Integer status = saasTrackerService.findSaasTrackerProgress(user).status();
            userInfo.setSaasStatus(status);
        } catch (Exception e){
            log.info("[AdminUserService findSaasTrackerProgress] {}", e);
        }

        try {
            ExpenseStatus expenseStatus = expenseRpc.requestStatus(user.corp().resCompanyIdentityNo()).getSetupStatus();
            userInfo.setExpenseStatus(expenseStatus);
        } catch (Exception e){
            log.info("[AdminUserService expenseStatus] {}", e);
        }

        return userInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity updateUserInfo(Long idxUser, AdminDto.UpdateUserDto dto) {
        User user = userRepository.findById(idxUser).orElseThrow(
            () -> UserNotFoundException.builder().build()
        );

        if(!EmailValidator.isValid(dto.getEmail())) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED);
        }
        String orgEmail = user.email();

        if(userRepository.findByAuthentication_EnabledAndEmailAndIdxNot(true, dto.getEmail(), idxUser).isPresent()) {
            throw AlreadyExistException.builder()
                .category("email")
                .resource(dto.getEmail())
                .build();
        }

        Optional.ofNullable(dto.getUserName()).ifPresent(user::name);
        Optional.ofNullable(dto.getEmail()).ifPresent(user::email);
        Optional.ofNullable(dto.getPhone()).ifPresent(user::mdn);
        Optional.ofNullable(dto.getCorpName()).ifPresent(user::corpName);
        Optional.ofNullable(dto.getPosition()).ifPresent(user::position);

        userRepository.save(user);

        try {
            expenseRpc.requestUpdateUserProfile(orgEmail, dto.getUserName(), dto.getPhone(), dto.getEmail());
        } catch (Exception e) {
            log.warn("[updateUserInfo] Fail to update user info of expense service: {}", orgEmail);
        }

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
