package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import com.nomadconnection.dapp.core.domain.D1200;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @interfaceID : 1200
 * @description : 법인회원신규여부검증
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1200 extends CommonPart {

    private String d001;    // 사업자등록번호

    private String d002;      // 법인회원구분코드(01: 신용카드회원)

    private String d003;      // 신규대상여부(Y/N)

    private String d004;    // 총한도금액

    private String d005;  // 특화한도금액

    private String d006;    // 제휴약정한도금액

    private String d007;    // 신청접수일자

    private String d008;    // 신청접수순번

    public void assignDataTo(D1200 d1200) {
        if (!StringUtils.isEmpty(d001)) {d1200.d001(d001);}
        if (!StringUtils.isEmpty(d002)) {d1200.d002(d002);}
        if (!StringUtils.isEmpty(d003)) {d1200.d003(d003);}
        if (!StringUtils.isEmpty(d004)) {d1200.d004(d004);}
        if (!StringUtils.isEmpty(d005)) {d1200.d005(d005);}
        if (!StringUtils.isEmpty(d006)) {d1200.d006(d006);}
        if (!StringUtils.isEmpty(d007)) {d1200.d007(d007);}
        if (!StringUtils.isEmpty(d008)) {d1200.d008(d008);}
        if (!StringUtils.isEmpty(c007)) {d1200.c007(c007);}
    }

    public void assignDataFrom(D1200 d1200) {
        if (!StringUtils.isEmpty(d1200.d001())) {d001=d1200.d001();}
        if (!StringUtils.isEmpty(d1200.d002())) {d002=d1200.d002();}
        if (!StringUtils.isEmpty(d1200.d003())) {d003=d1200.d003();}
        if (!StringUtils.isEmpty(d1200.d004())) {d004=d1200.d004();}
        if (!StringUtils.isEmpty(d1200.d005())) {d005=d1200.d005();}
        if (!StringUtils.isEmpty(d1200.d006())) {d006=d1200.d006();}
        if (!StringUtils.isEmpty(d1200.d007())) {d007=d1200.d007();}
        if (!StringUtils.isEmpty(d1200.d008())) {d008=d1200.d008();}
    }

    public void assignDataFrom(CommonPart commonPart) {
        if (!StringUtils.isEmpty(commonPart.getC001())) {c001=commonPart.getC001();}
        if (!StringUtils.isEmpty(commonPart.getC002())) {c002=commonPart.getC002();}
        if (!StringUtils.isEmpty(commonPart.getC003())) {c003=commonPart.getC003();}
        if (!StringUtils.isEmpty(commonPart.getC004())) {c004=commonPart.getC004();}
        if (!StringUtils.isEmpty(commonPart.getC005())) {c005=commonPart.getC005();}
        if (!StringUtils.isEmpty(commonPart.getC006())) {c006=commonPart.getC006();}
        if (!StringUtils.isEmpty(commonPart.getC007())) {c007=commonPart.getC007();}
        if (!StringUtils.isEmpty(commonPart.getC008())) {c008=commonPart.getC008();}
        if (!StringUtils.isEmpty(commonPart.getC007())) {c007=commonPart.getC007();}
        if (!StringUtils.isEmpty(commonPart.getC010())) {c010=commonPart.getC010();}
        if (!StringUtils.isEmpty(commonPart.getC011())) {c011=commonPart.getC011();}
        if (!StringUtils.isEmpty(commonPart.getC012())) {c012=commonPart.getC012();}
        if (!StringUtils.isEmpty(commonPart.getC013())) {c013=commonPart.getC013();}
        if (!StringUtils.isEmpty(commonPart.getC014())) {c014=commonPart.getC014();}
    }
}
