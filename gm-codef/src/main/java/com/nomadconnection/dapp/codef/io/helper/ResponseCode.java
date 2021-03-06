package com.nomadconnection.dapp.codef.io.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nomadconnection.dapp.codef.io.helper.ScrapingMessageGroup.*;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    // 통신오류
    CF11021("CF-11021","해당 라이센스에 권한이 없습니다.", GP99999),
    CF11023("CF-11023","업데이트 서버를 연결할 수 없습니다.", GP99999),
    CF11042("CF-11042","모듈 실행 중 오류가 발생하였습니다.(R)", GP99999),
    CF12004("CF-12004","구문에서 예외가 발생하였습니다.", GP00001),
    CF12307("CF-12307","암/복호화 도중 오류가 발생하였습니다.", GP00001),
    CF12308("CF-12308","복호화 도중 오류가 발생하였습니다.", GP00001),
    CF12701("CF-12701","전문 혹은 페이지가 변경되었습니다.", GP00002),
    CF12702("CF-12702","전문 혹은 데이터가 변경되었습니다.", GP00002),
    CF12710("CF-12710","사이트 개편으로 현재 처리 중입니다.", GP00002),
    CF12040("CF-12040","현재 모듈에서 지원되지 않습니다.", GP00001),
    CF12050("CF-12050","[dummy module] 미제공 서비스", GP00001),
    CF12103("CF-12103","IP가 차단되었습니다.", GP00001),
    CF12200("CF-12200","통신 연결 실패.", GP00001),
    CF12201("CF-12201","동일 계정으로 다른 매체에서 로그인하였습니다.", GP00001),
    CF12203("CF-12203","방화벽 또는 방화벽 프로그램에 의해 통신 연결이 차단되었습니다.", GP00001),
    CF12003("CF-12003","해당 기관 서버에서 오류가 발생하였습니다.", GP00001),
    CF12041("CF-12041","이용 가능 시간이 아닙니다.", GP00012),
    CF12104("CF-12104","접속자 폭주로 접속이 지연되고 있습니다. 잠시 후 다시 시도해주시기 바랍니다.", GP00001),
    CF12202("CF-12202","통신 연결 시간이 초과하였습니다. 네트웍 상태가 안 좋거나 대상기관에서 응답이 없습니다. 잠시 후 다시 시도하시기 바랍니다.", GP00001),
    CF12100("CF-12100","(해당 기관 오류 메시지가 있습니다.)", GP00003),
    CF12043("CF-12043","연체 금액이 있어 선결제가 불가합니다.", GP00003),
    CF12044("CF-12044","사용자 설정으로 인해 뱅킹 거래가 제한된 상태입니다.", GP00003),
    CF12045("CF-12045","사용자 설정으로 인해 해외에서의 거래가 제한된 상태입니다.", GP00003),
    CF12046("CF-12046","가상계좌가 예약되어있어 선결제가 불가합니다.", GP00003),
    CF12047("CF-12047","결제 당일에는 가상계좌 지정 입금예약이 불가합니다.", GP00003),
    CF12048("CF-12048","현재 접속하신 단말기는 미지정 단말기입니다.", GP00003),
    CF12049("CF-12049","해당 메뉴에 조회 권한이 없습니다.", GP00003),
    CF12051("CF-12051","나이 제한으로 서비스를 이용하실 수 없습니다.", GP00003),
    CF12811("CF-12811","뱅킹 종류 오류입니다.", GP00003),
    CF12812("CF-12812","인터넷뱅킹 미가입 고객입니다.", GP00003),
    CF12814("CF-12814","스마트 인증서 가입자입니다.공인인증서로 로그인이 불가하오니 확인 후 거래하시기 바랍니다.", GP00003),
    CF12815("CF-12815","장기 미사용 고객입니다.", GP00003),
    CF12816("CF-12816","해지된 고객입니다.", GP00003),
    CF12830("CF-12830","1일 로그인 허용 횟수가 초과하였습니다.", GP00003),
    CF12831("CF-12831","본인인증 후 서비스 이용이 가능합니다.", GP00003),
    CF12832("CF-12832","홈페이지 회원가입이 필요합니다. 확인 후 거래하시기 바랍니다.", GP00003),
    CF12833("CF-12833","비밀번호 재설정이 필요합니다. 확인 후 거래하시기 바랍니다.", GP00003),
    CF12834("CF-12834","계정 잠김 해제가 필요합니다. 확인 후 거래하시기 바랍니다.", GP00003),
    CF12300("CF-12300","인증서 파일이 없습니다.", GP00003),
    CF12301("CF-12301","인증서 파일을 찾을 수 없습니다.", GP00003),
    CF12302("CF-12302","인증서 암호가 틀렸습니다.", GP00003),
    CF12805("CF-12805","미등록 인증서입니다.", GP00004),
    CF12806("CF-12806","만료된 인증서입니다.", GP00004),
    CF12807("CF-12807","폐기된 인증서입니다.", GP00004),
    CF12808("CF-12808","만료 또는 폐기된 인증서입니다.", GP00004),
    CF12809("CF-12809","인증서 관련 기타 오류입니다.", GP00003),
    CF12810("CF-12810","인증서 종류 오류입니다.", GP00003),
    CF12813("CF-12813","미등록 또는 폐기된 인증서입니다.", GP00004),
    CF12800("CF-12800","아이디 오류입니다.", GP00007),
    CF12801("CF-12801","비밀번호 오류입니다.", GP00007),
    CF12802("CF-12802","비밀번호 오류 횟수 초과입니다.", GP00007),
    CF12803("CF-12803","아이디 또는 비밀번호 오류입니다.", GP00007),
    CF12820("CF-12820","Customer ID 오류입니다.", GP00007),
    CF12821("CF-12821","Operator ID 오류입니다.", GP00007),
    CF12824("CF-12824","아이디 자릿수 오류입니다.", GP00007),
    CF12825("CF-12825","아이디 형식 오류입니다.", GP00007),
    CF12826("CF-12826","비밀번호 자릿수 오류입니다.", GP00007),
    CF12827("CF-12827","비밀번호 형식 오류입니다.", GP00007),
    CF12840("CF-12840","PIN 비밀번호 오류입니다.", GP00007),
    CF12841("CF-12841","PIN 비밀번호 오류 횟수 초과입니다.", GP00007),
    CF12842("CF-12842","PIN 비밀번호 형식 오류입니다.", GP00007),
    CF12843("CF-12843","PIN 비밀번호 관련 오류입니다.", GP00007),
    CF12844("CF-12844","PIN 비밀번호 미등록입니다.", GP00007),
    CF12804("CF-12804","주민등록번호(사업자 번호)와 공인인증 정보가 일치하지 않습니다.", GP99999),
    CF12001("CF-12001","사용자 입력 시간이 초과하였습니다. (User Time Out)", GP99999),
    CF12042("CF-12042","최대 조회 건수를 초과하였습니다.", GP99999),
    CF12401("CF-12401","로그인 파라미터가 누락되었습니다.", GP99999),
    CF12411("CF-12411","필수 파라미터가 누락되었습니다.", GP99999),
    CF13000("CF-13000","사업자 번호, 주민번호가 없거나 잘못되었습니다.", GP99999),
    CF13001("CF-13001","조회일 혹은 기간 체크 오류", GP99999),
    CF13002("CF-13002","잘못된 파라미터 오류입니다.", GP99999),
    CF13003("CF-13003","추가 정보 입력값이 잘못되었습니다.(2way)", GP99999),
    CF13004("CF-13004","카드번호 List에서 마스킹을 제외한 번호가 동일한 카드가 하나 이상입니다.", GP00005),
    CF13005("CF-13005","생년월일이 잘못되었습니다.", GP00005),
    CF13010("CF-13010","해지된 계좌입니다.", GP00005),
    CF13020("CF-13020","계좌번호를 입력하지 않았습니다.", GP00005),
    CF13021("CF-13021","계좌번호 List에 입력된 계좌번호와 일치하는 정보가 없습니다.", GP00005),
    CF13022("CF-13022","계좌번호 관련 오류입니다.", GP00005),
    CF13030("CF-13030","계좌 비밀번호를 입력하지 않았습니다.", GP00005),
    CF13031("CF-13031","계좌 비밀번호 오류입니다.", GP00005),
    CF13032("CF-13032","계좌 비밀번호 오류 횟수 초과입니다.", GP00005),
    CF13033("CF-13033","계좌 비밀번호 형식 오류입니다.", GP00005),
    CF13034("CF-13034","계좌 비밀번호 자릿수 오류입니다.", GP00005),
    CF13035("CF-13035","계좌 비밀번호 관련 오류입니다.", GP00005),
    CF13100("CF-13100","카드번호를 입력하지 않았습니다.", GP00006),
    CF13101("CF-13101","카드번호 리스트에 입력된 카드번호와 일치하는 정보가 없습니다.", GP00006),
    CF13102("CF-13102","카드번호 관련 오류입니다.", GP00006),
    CF13120("CF-13120","카드 비밀번호를 입력하지 않았습니다.", GP00006),
    CF13121("CF-13121","카드 비밀번호 오류입니다.", GP00006),
    CF13122("CF-13122","카드 비밀번호 오류 횟수 초과입니다.", GP00006),
    CF13123("CF-13123","카드 비밀번호 형식 오류입니다.", GP00006),
    CF13124("CF-13124","카드 비밀번호 자릿수 오류입니다.", GP00006),
    CF13125("CF-13125","카드 비밀번호 관련 오류입니다.", GP00006),
    CF13130("CF-13130","카드 CVC번호를 입력하지 않았습니다.", GP00006),
    CF13131("CF-13131","카드 CVC번호 오류입니다.", GP00006),
    CF13132("CF-13132","카드 CVC번호 오류 횟수 초과입니다.", GP00006),
    CF13133("CF-13133","카드 CVC번호 형식 오류입니다.", GP00006),
    CF13134("CF-13134","카드 CVC번호 자릿수 오류입니다.", GP00006),
    CF13135("CF-13135","카드 CVC번호 관련 오류입니다.", GP00006),
    CF13220("CF-13220","차량번호를 입력하지 않았습니다.", GP99999),
    CF13221("CF-13221","차량번호 리스트에 입력된 차량번호와 일치하는 정보가 없습니다.", GP99999),
    CF13222("CF-13222","차량번호 관련 오류입니다.", GP99999),
    CF13223("CF-13223","자동차보험 만기일 30일내인 차량만 조회가 가능합니다. 확인 후 거래하시기 바랍니다.", GP99999),
    CF00000("CF-00000","성공", GP00000),
    CF00001("CF-00001","필수 입력 파라미터가 누락되었습니다.", GP99999),
    CF00002("CF-00002","json형식이 올바르지 않습니다.", GP99999),
    CF00003("CF-00003","요청하신 서비스 상품 정보가 존재하지 않습니다.", GP99999),
    CF00004("CF-00004","요청 도메인이 올바르지 않습니다.", GP99999),
    CF00006("CF-00006","1회 요청 제한 건수를 초과했습니다.", GP99999),
    CF00007("CF-00007","요청 파라미터가 올바르지 않습니다.", GP99999),
    CF00009("CF-00009","지원하지 않는 형식으로 인코딩된 문자열입니다.", GP99999),
    CF00010("CF-00010","유효한 라이센스가 아닙니다.", GP99999),
    CF00011("CF-00011","사용 가능한 정보수집 서버 정보가 없습니다. 20분 이후 다시 요청하시기 바랍니다. 동일 현상이 지속되면 관리자에게 문의바랍니다.", GP99999),
    CF00012("CF-00012","일 100건 요청이 초과되었습니다.", GP99999),
    CF00400("CF-00400","클라이언트 요청 오류로 인해 요청을 처리 할 수 ​​없습니다.", GP99999),
    CF00401("CF-00401","요청 권한이 없습니다.", GP99999),
    CF00403("CF-00403","잘못된 요청입니다.", GP99999),
    CF00404("CF-00404","요청하신 페이지(Resource)를 찾을 수 없습니다.", GP99999),
    CF00405("CF-00405","요청하신 방법(Method)가 잘못되었습니다.", GP99999),
    CF01001("CF-01001","서버 접속 정보가 부정확합니다.", GP99999),
    CF01002("CF-01002","정보수집 서버 접속에 실패했습니다.", GP99999),
    CF01003("CF-01003","정보 전송 중 오류가 발생했습니다.", GP00001),
    CF01004("CF-01004","응답 대기시간을 초과했습니다.", GP00001),
    CF01005("CF-01005","엔진 초기화가 진행중입니다. 잠시 후 다시 시도해주세요.", GP00001),
    CF01006("CF-01006","중복 로그인 방지를 위해 요청을 제한합니다. 잠시 후 다시 시도해주세요.", GP00001),
    CF02000("CF-02000","데이터 저장에 실패했습니다.", GP99999),
    CF03000("CF-03000","결과 목록 가공에 실패했습니다.", GP99999),
    CF03001("CF-03001","2WAY 요청 처리를 위한 정보가 존재하지 않습니다.", GP99999),
    CF03002("CF-03002","API 요청 처리가 정상 진행 중입니다. 추가 정보를 입력하세요.", GP99999),
    CF03999("CF-03999","조회 결과가 없습니다.", GP99999),
    CF04000("CF-04000","사용자 계정정보 등록에 실패했습니다.", GP00008),
    CF04001("CF-04001","제3자인증을 위한 계정정보 설정에 실패했습니다.", GP99999),
    CF04002("CF-04002","사용자 계정정보 설정에 실패했습니다.", GP99999),
    CF04003("CF-04003","등록할 계정이 없습니다.", GP99999),
    CF04004("CF-04004","이미 계정이 등록된 기관입니다. 기존 계정 먼저 삭제하세요.", GP99999),
    CF04005("CF-04005","client_id가 존재하지 않습니다. 관리자에게 문의하세요.", GP99999),
    CF04006("CF-04006","service_no가 존재하지 않습니다. 관리자에게 문의하세요.", GP99999),
    CF04007("CF-04007","비공개키(privateKey)가 존재하지 않습니다.", GP99999),
    CF04008("CF-04008","커넥티드아이디(connectedId)가 존재하지 않습니다.", GP99999),
    CF04009("CF-04009","계정 정보 암호화 도중 에러가 발생했습니다.", GP99999),
    CF04010("CF-04010","사용자 계정정보 수정에 실패했습니다.", GP99999),
    CF04011("CF-04011","사용자 계정정보 삭제에 실패했습니다.", GP99999),
    CF04012("CF-04012","사용자 계정정보 등록이 부분적으로 성공했습니다.", GP00000),
    CF04013("CF-04013","사용자 계정정보 조회에 실패했습니다.", GP99999),
    CF04014("CF-04014","요청하신 커넥티드아이디(connectedId)에 연결된 계정이 존재하지 않습니다.", GP99999),
    CF04015("CF-04015","커넥티드아이디(connectedId)로 찾을 수 있는 기관 계정이 존재하지 않습니다. 요청 주소(API URL), 기관(organization), 토큰(access_token) 등을 확인하세요.", GP99999),
    CF04016("CF-04016","API 요청 주소와 계정 타입(개인/법인)이 일치하지 않습니다.", GP99999),
    CF04017("CF-04017","클라이언트 타입(clientType)이 존재하지 않습니다.", GP99999),
    CF04018("CF-04018","커넥티드아이디(connectedId) 조회에 실패했습니다.", GP99999),
    CF04019("CF-04019","존재하지 않는 커넥티드아이디(connectedId)입니다.", GP99999),
    CF04020("CF-04020","비밀번호 복호화에 문제가 발생했습니다. 요청 파라미터 중 비밀번호 항목의 암호화 여부를 확인하세요.", GP99999),
    CF04021("CF-04021","인증서 파일(pfx) 변환을 위한 디렉토리 생성에 실패했습니다.", GP99999),
    CF04022("CF-04022","인증서 파일(pfx) 생성에 실패했습니다. 요청 파라미터 중 certFile항목이 올바른지 확인하세요.", GP99999),
    CF04023("CF-04023","인증서 파일(der) 변환에 실패했습니다. 인증서와 비밀번호 정보가 올바른지 확인하세요.", GP99999),
    CF04024("CF-04024","pfx인증서로부터 핑거프린트 추출에 실패했습니다. 인증서와 비밀번호 정보가 올바른지 확인하세요.", GP99999),
    CF04025("CF-04025","pfx인증서로부터 der파일 생성에 실패했습니다. 인증서와 비밀번호 정보가 올바른지 확인하세요.", GP99999),
    CF04026("CF-04026","pfx인증서로부터 key파일 생성에 실패했습니다. 인증서와 비밀번호 정보가 올바른지 확인하세요.", GP99999),
    CF04027("CF-04027","pfx인증서로부터 der,key파일 생성에 실패했습니다. 요청 파라미터의 암호화 여부, 인코딩 여부를 확인하세요.", GP99999),
    CF04028("CF-04028","암호화된 파라미터에 공백이 포함되어 있습니다. 요청 파라미터는 URL인코딩이 되어야 합니다.", GP99999),
    CF09001("CF-09001","요청 모듈의 정보수집 타입이 정의되지 않았습니다. 관리자에게 문의하세요.", GP99999),
    CF09002("CF-09002","요청 모듈의 2Way 방식 여부가 정의되지 않았습니다. 관리자에게 문의하세요.", GP99999),
    CF09003("CF-09003","요청 모듈의 2Way 대기 시간이 정의되지 않았습니다. 관리자에게 문의하세요.", GP99999),
    CF09990("CF-09990","OAUTH2.0 토큰 에러입니다. 메시지를 확인하세요.", GP99999),
    CF09998("CF-09998","미등록된 에러코드입니다. 관리자에게 문의하세요.", GP99999),
    CF09999("CF-09999","서버 처리중 에러가 발생 했습니다. 관리자에게 문의하세요.", GP99999),
    UNKNOWN("UNKNOWN", "CODEF 서비스에서 알 수 없는 오류", GP99999),
    REQUEST_ERROR("REQUEST_ERROR", "CODEF 요청 실패", GP99999),
    LIMITED("LIMITED", "유힌회사", GP00010),
    KISED("KISED", "창진원 대상 법인", GP00010),
    INDIVIDUAL("INDIVIDUAL","(해당 기관 오류 메시지가 있습니다.)", GP00011)
    ;

    private String code;
    private String message;
    private ScrapingMessageGroup scrapingMessageGroup;

    private static final Map<String, ResponseCode> codes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ResponseCode::getCode, Function.identity())));

    public static ResponseCode findByCode(String code) {
        return Optional.ofNullable(codes.get(code)).orElse(UNKNOWN);
    }

}
