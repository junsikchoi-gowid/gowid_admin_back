package com.nomadconnection.dapp.codef.io.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScrapingMessageGroup {

    GP00000("GP-00000","정상처리 되었습니다."),
    GP00001("GP-00001","연결이 원활하지 않습니다. 잠시 후 다시 시도해주세요."),
    GP00002("GP-00002","일시적인 오류가 발생했습니다. 고객센터로 문의해주세요."),
    GP00003("GP-00003","일시적인 오류가 발생했습니다. 재시도 시 해결되지 않으면 고객센터로 문의해주세요."),
    GP00004("GP-00004","미등록 또는 만료된 인증서입니다. 인증서 확인 후 다시 시도해주세요."),
    GP00005("GP-00005","계좌정보 오류입니다. 고객센터로 문의해주세요."),
    GP00006("GP-00006","카드정보 오류입니다. 고객센터로 문의해주세요."),
    GP00007("GP-00007","계정정보 오류입니다."),
    GP00008("GP-00008","해당 기관에 등록된 법인 공인인증서인지 확인 후 시도해주세요. 계속해서 오류 발생할 경우 고객센터로 문의해주세요."),
    GP99999("GP-99999","알 수 없는 오류가 발생했습니다. 고객센터로 문의해주세요."),
    ;

    private final String group;
    private final String message;

}
