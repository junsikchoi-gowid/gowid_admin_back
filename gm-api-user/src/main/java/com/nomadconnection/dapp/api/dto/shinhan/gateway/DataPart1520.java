package com.nomadconnection.dapp.api.dto.shinhan.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @interfaceID : 1520
 * @description : 재무제표스크래핑
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataPart1520 extends CommonPart {

    private String d001;    // 신청접수일자

    private String d002;    // 신청접수순번

    private String d003;    // 사업자등록번호 
            
    private String d004;    // 발급(승인)번호
            
    private String d005;    // 주민번호    
            
    private String d006;    // 상호(사업장명)
            
    private String d007;    // 발급가능여부  
            
    private String d008;    // 시작일자    
            
    private String d009;    // 종료일자    
            
    private String d010;    // 성명      
            
    private String d011;    // 주소      
            
    private String d012;    // 종목      
            
    private String d013;    // 업태      
            
    private String d014;    // 작성일자    
            
    private String d015;   // 귀속연도     
            
    private String d016;    // 총자산     
            
    private String d017;   // 매출       
            
    private String d018;   // 납입자본금    
            
    private String d019;   // 자기자본금    
            
    private String d020;   // 재무조사일    

}
