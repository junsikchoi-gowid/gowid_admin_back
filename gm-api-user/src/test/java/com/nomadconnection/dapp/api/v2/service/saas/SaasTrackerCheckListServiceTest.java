package com.nomadconnection.dapp.api.v2.service.saas;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.v2.dto.saas.SaasTrackerCheckListDto;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasCheckCategoryRepository;
import com.nomadconnection.dapp.core.domain.repository.saas.SaasCheckInfoRepository;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckCategory;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckInfo;
import com.nomadconnection.dapp.core.domain.saas.SaasCheckType;
import com.nomadconnection.dapp.core.domain.saas.SaasPaymentInfo;
import com.nomadconnection.dapp.core.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SaasTrackerCheckListServiceTest extends AbstractSpringBootTest {

    @Autowired
    SaasTrackerCheckListService checkListService;

    @Autowired
    SaasCheckCategoryRepository saasCheckCategoryRepository;

    @Autowired
    SaasCheckInfoRepository checkInfoRepository;

    @Autowired
    UserService userService;

    private static User user;
    private static SaasCheckCategory checkCategory;
    private static SaasPaymentInfo paymentInfo;

    @BeforeEach
    public void init() {
        user = userService.getUser(66L);
    }

    @AfterEach
    public void after() {

    }

    @Test
    @DisplayName(value = "체크리스트 조회")
    @Order(1)
    public void test_get_check_list_1() {
        List<SaasCheckInfo> saasCheckInfoList = checkListService.findSaasCheckInfoListByUser(user);
        assertThat(saasCheckInfoList).isNotNull();
        assertThat(saasCheckInfoList.size()).isEqualTo(5);
    }

    @Test
    @DisplayName(value = "체크리스트 항목 별 건수 조회")
    @Order(2)
    public void test_get_check_list_2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // private method test
        SaasTrackerCheckListService service = new SaasTrackerCheckListService(checkInfoRepository, userService);
        Method m = service.getClass().getDeclaredMethod("getCheckListCountByType", List.class, SaasCheckType.class);
        m.setAccessible(true);

        List<SaasCheckInfo> saasCheckInfoList = checkListService.findSaasCheckInfoListByUser(user);

        // 해지필요 건수
        int needCancelListCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.NEED_CANCEL);
        assertThat(needCancelListCount).isEqualTo(1);

        // 신규등록 건수
        int newListCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.NEW);
        assertThat(newListCount).isEqualTo(1);

        // 재등록 건수
        int reRegistrationCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.RE_REGISTRATION);
        assertThat(reRegistrationCount).isEqualTo(1);

        // 이상결제 건수
        int strangeCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.STRANGE);
        assertThat(strangeCount).isEqualTo(1);

        // 무료인데 결제된 의심 건수
        int freeChangeListCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.FREE_CHANGE);
        assertThat(freeChangeListCount).isEqualTo(1);

        // 결제 급등 건수
        int increasedListCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.INCREASED);
        assertThat(increasedListCount).isEqualTo(1);

        // 무료만료알림 건수
        int freeExpirationListCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.FREE_EXPIRATION);
        assertThat(freeExpirationListCount).isEqualTo(1);

        // 중복결제의심 건수
        int duplicateListCount = (int) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.DUPLICATE);
        assertThat(duplicateListCount).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "항목에 따른 체크리스트 조회")
    @Order(3)
    @Transactional
    public void test_get_check_list_3() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // private method test
        SaasTrackerCheckListService service = new SaasTrackerCheckListService(checkInfoRepository, userService);
        Method m = service.getClass().getDeclaredMethod("getCheckListByType", List.class, SaasCheckType.class);
        m.setAccessible(true);

        List<SaasCheckInfo> saasCheckInfoList = checkListService.findSaasCheckInfoListByUser(user);

        // 해지필요
        List<SaasTrackerCheckListDto.CheckData> needCancelList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.NEED_CANCEL);
        needCancelList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 신규등록
        List<SaasTrackerCheckListDto.CheckData> newList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.NEW);
        newList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 재등록
        List<SaasTrackerCheckListDto.CheckData> reRegistrationList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.RE_REGISTRATION);
        reRegistrationList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 이상결제
        List<SaasTrackerCheckListDto.CheckData> strangeList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.STRANGE);
        strangeList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 무료인데 결제된 의심
        List<SaasTrackerCheckListDto.CheckData> freeChangeList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.FREE_CHANGE);
        freeChangeList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 결제 급등
        List<SaasTrackerCheckListDto.CheckData> increasedList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.INCREASED);
        increasedList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 무료만료알림
        List<SaasTrackerCheckListDto.CheckData> freeExpirationList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.FREE_EXPIRATION);
        freeExpirationList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());

        // 중복결제의심
        List<SaasTrackerCheckListDto.CheckData> duplicateList = (List<SaasTrackerCheckListDto.CheckData>) m.invoke(checkListService, saasCheckInfoList, SaasCheckType.DUPLICATE);
        duplicateList.stream().findFirst().ifPresent(v -> assertThat(v.getIdxSaasCheckInfo()).isNotNull());
    }

    @Test
    @DisplayName(value = "ID에 따른 체크리스트 항목 조회")
    @Order(4)
    @Transactional
    public void test_get_check_list_4() {
        assertThat(checkListService.findSaasCheckInfo(user, 1L)).isNotNull();
        assertThrows(EntityNotFoundException.class, () -> {
            checkListService.findSaasCheckInfo(user, 99L);
        });
    }

    @Test
    @DisplayName(value = "체크리스트 항목 수정 - checked")
    @Order(5)
    @Transactional
    public void test_get_check_list_5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // private method test
        SaasTrackerCheckListService service = new SaasTrackerCheckListService(checkInfoRepository, userService);
        Method m = service.getClass().getDeclaredMethod("updateSaasCheckInfo", User.class, Long.class, SaasTrackerCheckListDto.CheckInfoReq.class);
        m.setAccessible(true);

        boolean checked = false;
        SaasTrackerCheckListDto.CheckInfoReq req = SaasTrackerCheckListDto.CheckInfoReq.builder().checked(checked).build();
        m.invoke(checkListService, user, 1L, req);

        SaasCheckInfo newCheckInfo = checkListService.findSaasCheckInfo(user, 1L);
        assertThat(newCheckInfo.checked()).isNotEqualTo(!checked);
    }
}