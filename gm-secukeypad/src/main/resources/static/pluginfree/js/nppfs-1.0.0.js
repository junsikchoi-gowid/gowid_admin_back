/*
 ***************************************************************************
 * nProtect Online Security, 1.13.0
 *
 * For more information on this product, please see
 * http://www.inca.co.kr / http://www.nprotect.com
 *
 * Copyright (c) INCA Internet Co.,Ltd  All Rights Reserved.
 *
 * 본 코드에 대한 모든 권한은 (주)잉카인터넷에게 있으며 동의없이 사용/배포/가공할 수 없습니다.
 *
 ***************************************************************************
 */
/*
 ***************************************************************************
 * nProtect Online Security, User-Agent Information
 ***************************************************************************
 */
var w = window;
var nua = navigator.userAgent;

w.nua = (typeof (nua) == "undefined" || nua == null || nua == "") ? navigator.userAgent : nua;
/*
 ***************************************************************************
 * nProtect Online Security, Const Variables
 ***************************************************************************
 */
w.npPfsConst = {
    // 비동기작업 상태
    STATE_READY: 1
    , STATE_DOING: 2
    , STATE_DONE: 3

    // 실행 모드
    , MODE_RELEASE: 1
    , MODE_DEBUG: 2

    , E2E_RESULT: "__E2E_RESULT__"
    , E2E_UNIQUE: "__E2E_UNIQUE__"
    , E2E_KEYPAD: "__E2E_KEYPAD__"

    , JS_VERSION: "1.13.0"
    , JS_BUILD_DATE: "20200603055231773"
};


/*

 ***************************************************************************
 * nProtect Online Security, Configuration
 ***************************************************************************
 */


var servletUrl = "http://192.168.99.108:8080/nppfs.servlet.do";

w.npPfsPolicy = {
    // 제품 공통
    Common: {

        InstallUrl: "/pluginfree/jsp/nppfs.install.jsp",


        ServletUrl: servletUrl,
        KeyUrl: servletUrl + "?p=k",
        RemoveKey: servletUrl + "?p=m",
        ReadyUrl: servletUrl + "?p=r",
        KeyPadUrl: servletUrl + "?p=p",
        CryptoUrl: servletUrl + "?p=c",


        VersionUrl: "https://supdate.nprotect.net/nprotect/nos_service/nos.service",
        Protocol: "https",

        LocalPort: 14440,
        Range: 10,
        ContextPath: "",


        RuntimeMode: npPfsConst.MODE_DEBUG,

        WaitTimeout: 300,
        MaxWaitCount: 100,
        MaxFailCount: 15
    }

    // 제품 지원 정책(구매한 제품 여부)
    , License: {
        FW: true
        , SK: true
        , FD: true
        , KV: true
    }

    , Os: {
        WIN: {
            CODE: "10"
        },
        MAC: {
            CODE: "20"
        },
        LINUX: {
            CODE: "30",
            TYPE: {
                Fedora: "10",
                Ubuntu: "20",
                CentOS: "30",
                OpenSUSE: "40",
                OTHER: "99"
            }
        }
    }
};


w.npMessage = {
    m01: "보안프로그램이 업데이트되었습니다. 최신모듈로 업데이트가 필요합니다. 설치페이지로 이동하시겠습니까?",
    m02: "[nProtect Online Security] 모듈을 찾을 수 없습니다. 접속경로를 확인하시거나 관리자에게 문의하십시오.",
    m03: "Microsoft IE7 이하 브라이저에서는 입력 form 양식에 div[class=\"%p%\"] 항목이 필요합니다.",
    m04: "서버에서 키값을 받을 수 없습니다. 키발급 경로를 확인하거나 지속적으로 문제 발생시 서버관리자에게 문의하십시오.",
    m05: "개인방화벽을 실행할 수 있는 환경이 아닙니다.",
    m06: "키보드보안을 실행할 수 있는 환경이 아닙니다.",
    m07: "단말정보수집을 실행할 수 있는 환경이 아닙니다.",
    m08: "마우스입력기를 실행할 수 있는 환경이 아닙니다.",
    m09: "보안프로그램에서 개발자도구나 디버그도구를 탐지하였습니다.\n보안을 위하여 현재 페이지를 다시 호출합니다.",
    m10: "보안프로그램과의 연결이 원활하지 않습니다. 지속적으로 발생시 관리자에게 문의하십시오.",
    m11: "접속 가능한 포트(%p%)를 찾았습니다.",
    m12: "기본 포트(%p%)가 열려 있는지 검사합니다.",
    m13: "쿠키에 저장된 호스트(%h%)와 포트(%p%)가 있습니다. 이 호스트와 포트를 검사합니다.",
    m14: "사용 가능한 호스트(%h%)와 포트(%p%)를 찾았습니다. 이 호스트와 포트를 사용합니다.",
    m15: "업데이트 모듈이 실행중인 상태입니다.",
    m16: "정상적인 설치가 되었는지 확인합니다. 설치 후 초기화 완료시까지 수 초(대략 5~10초)가 소요됩니다. 설치가 완료되면 자동으로 첫 페이지로 이동합니다.",
    m17: "설치가 완료되었습니다.",
    m18: "Flash SDK를 정상적으로 시작되었습니다.",
    m19: "인증서 초기화에 너무 많은 재호출이 발생하여 초기화 작업을 중지합니다. 페이지를 다시 접속하시거나 지속적인문제 발생시 관리자에게 문의하십시오",
    m20: "장시간동안 사용자의 페이지 사용이 없어 현재 페이지의 접속을 종료합니다.",
    m21: "[%p%] 이름으로 여러 개의 form이 존재합니다. 해당 이름의  첫번째 form에 단말정보가 수집됩니다.",
    m22: "키보드보안프로그램에서 보호되지 않는 키가 입력되었습니다. 보안을 위해 페이지를 다시 호출합니다.",
    m23: "보안프로그램과의 연결이 중지되었습니다.\n보안을 위하여 현재 페이지를 다시 호출합니다.",
    m24: "초기 활성화된 객체(%p%)를 다시 활성화시킵니다.",
    m25: "초기 활성화된 객체(%p%)를 찾았습니다. 키보드보안 초기화 후에 다시 활성화시킵니다.",
    m26: "키보드보안이 초기화되지 않았습니다. 잠시 후 다시 시도해주십시오.",
    m27: "단말정보수집을 위한 [form] 필드가 존재하지 않습니다. 초기화값을 다시 확인하여 주십시오.",
    m28: "단말정보수집 모듈 초기화에 성공하였습니다.",
    m29: "단말정보수집 모듈을 초기화할 수 없습니다.",
    m30: "서버에서 키값을 얻어올 수 없습니다. 서버의 상태 또는 접속경로를 확인하여 주십시오.",
    m31: "Microsoft IE7 이하 브라이저에서는 입력 form(%p1%) 양식에 div[class=\"%p2%\"] 항목이 필요합니다.",
    m32: "입력 Form(%p%)이 존재하지 않거나 2개 이상입니다.",
    m33: "모듈이 설치되어 있지 않습니다.",
    m34: "모듈이 업데이트되었습니다.",
    m35: "설치페이지로 이동하시겠습니까?",
    m36: "설치페이로 이동하여 다시 설치하시겠습니까?",
    m37: "jQuery 객체를 찾을 수 없습니다. Microsoft IE Browser 9.0 이하 버전에서는 jQuery를 사용해야 합니다.",
    m38: "개발자도구의 단축키는 사용할 수 없습니다.",
    m39: "오른쪽 마우스는 사용할 수 없습니다.",
    m40: "현재의 브라우저는 Ajax를 지원하지 않습니다.",
    m41: "보안프로그램과의 연결시도 중 응답시간을 초과하였습니다.",
    m42: "응답값이 정상적인 규격이 아닙니다.",
    m43: "추가하려는 항목의 상위객체를 찾을 수 없습니다.",
    m44: "생성하려는 입력양식과 값의 개수가 일치하지 않습니다.",
    m45: "문자형키패드는 텍스트입력양식에서 사용할 수 없습니다. 텍스트입력양식에서는 숫자/한글형키패드만 지원합니다.",
    m46: "한글키패드는 암호입력양식에서 사용할 수 없습니다. 암호입력양식에서는 숫자/문자형키패드만 지원합니다.",
    m47: "동적 확장은 10개까지 가능합니다. 동적 필드 로직을 10개 이하로 구성하십시오.",
    m48: "가상운영체제 또는 원격으로 접속하셨습니다. 키보드보안을 지원하지 않는 환경입니다.",
    m49: "가상운영체제 또는 원격접속이 아닙니다. 키보드보안이 실행가능한 환경입니다.",
    m50: "[nProtect Online Security, %p1%] 모듈에 접근할 수 없어 종료합니다.",
    m51: "로컬 서버(%p1%:%p2%)에서 업데이트 확인을 요청하였습니다.",
    m52: "NOS의 세션을 유지합니다.",
    m53: "데이터를 받아서 처리할 Callback함수를 지정해야 합니다.",
    m54: "NOS와 통신할 수 없습니다. npPfsStartup()으로 먼저 페이지를 초기화하십시오.",
    m55: "개인방화벽의 세션을 유지합니다.",
    m56: "개인방화벽을 시작합니다.",
    m57: "개인방화벽이 정상적으로 시작되었습니다.",
    m58: "개인방화벽을 정상적으로 종료하였습니다.",
    m59: "E2E 초기화를 위한 설정변수가 지정되지 않았습니다. npPfsE2E 변수값을 설정하십시오.",
    m60: "랜덤값생성페이지(%p1%)에서 값을 정상적으로 얻어올 수 없습니다.",
    m61: "키보드보안에 입력양식(%p1%)을 등록합니다.",
    m62: "키보드보안에 입력양식(%p1%)이 정상적으로 등록되었습니다.",
    m63: "입력양식(%p1%)에 포커스가 들어왔습니다.",
    m64: "입력양식(%p1%)의 포커스가 사라졌습니다.",
    m65: "입력양식(%p1%)의 키보드보안 값(%p2%)이 입력되었습니다.",
    m66: "키 값이 입력되었습니다.",
    m67: "입력양식(%p1%)의 값이 삭제되었습니다. 현재값(%p2%).",
    m68: "단말정보수집을 정상적으로 종료하였습니다.",
    m69: "단말정보수집을 시작합니다.",
    m70: "단말정보수집이 정상적으로 시작되었습니다.",
    m71: "단말정보수집이 완료되었습니다.",
    m72: "마우스입력기를 시작합니다.",
    m73: "마우스입력기를 정상적으로 종료하였습니다.",
    m74: "마우스입력기 공개키정보(%p1%)",
    m75: "마우스입력기에 입력양식(%p1%)을 등록합니다.",
    m76: "마우스입력기가 정상적으로 시작되었습니다.",
    m77: "입력양식(%p1%)에 [(%p2%)] 속성으로 활성화양식명을 지정하여 주십시오.",
    m78: "입력양식(%p1%)의 마우스입력기가 정상적으로 초기화되었습니다.",
    m79: "마우스입력기(%p1%)가 활성화되었습니다.",
    m80: "마우스입력기(%p1%)가 비활성화되었습니다.",
    m81: "웹페이지에 등록된 Flash 객체가 없습니다.",
    m82: "Flash SDK를 시작합니다.",
    m83: "Flash SDK를 정상적으로 종료하였습니다.",
    m84: "키보드보안에 Flash 입력양식(%p1%)을 등록합니다.",
    m85: "최대길이값이 플래시에서 넘어오지 않았습니다. 최대길이 체크를 무시합니다.",
    m86: "키보드보안에 Flash 입력양식(%p1%)이 정상적으로 등록되었습니다.",
    m87: "폼 이름이 없어 동적필드 생성을 중단합니다.",
    m88: "키보드보안 프로그램이 지원되지 않는 환경에서는\n안전한 거래를 위해 가상키패드(마우스입력기)를\n반드시 사용하셔야 합니다.",
    m89: "공백버튼의 개수가 너무 큽니다. 줄 단위 당 버튼의 개수를 1/3 이하로 설정하십시오. 보통 줄 당 1~2개가 적당합니다.",
    m90: "입력양식(%p1%)의 마우스입력기를 보이게 하려고 합니다.",
    m91: "입력양식(%p1%)의 마우스입력기를 보이게 하였습니다.",
    m92: "입력양식(%p1%)의 마우스입력기를 안보이게 하였습니다.",
    m93: "입력양식(%p1%)의 마우스입력기가 닫혔습니다.",
    m94: "입력양식(%p1%)의 마우스입력기를 입력확인 처리하였습니다.",
    m95: "보안프로그램을 설치하셔야 이용이 가능한 서비스입니다. [확인]을 선택하시면 설치페이지로 연결됩니다.",
    m96: "보안프로그램을 업데이트하셔야 이용이 가능한 서비스입니다. [확인]을 선택하시면 재설치페이지로 연결됩니다.",
    m97: "보안프로그램이 설치되어 있지 않습니다.",
    m98: "입력양식(%p1%)의 마우스입력기를 삭제하였습니다.",
    m99: "키보드보안을 정상적으로 종료하였습니다.",
    m100: "보안프로그램에서 프록시 사용을 탐지하였습니다.\n보안을 위하여 현재 페이지를 다시 호출합니다.",
    m101: "보안프로그램에서 프록시 사용을 탐지하였습니다.\n프록시 기능을 종료하시겠습니까?"
};

var npOutCount = 0;
w.npConsole = {
    taskStart: new Date()
    , timelineStart: new Date()
    , timeline: []
    , info: function (text) {
        this.print(text, "blue");
    }
    , log: function (text) {
        this.print(text, "black");
    }
    , error: function (text) {
        this.print(text, "red");
    }
    , split: function () {
        var buf = [];
        for (var i = 0; i < 80; i++) {
            buf.push("-");
        }
        this.print(buf.join(""), "#ddd");
    }
    , reset: function () {
        this.taskStart = new Date();
        this.timelineStart = new Date();
        this.timeline = [];
    }
    , check: function (name) {
        this.timeline.push({name: name, start: this.timelineStart, end: new Date()});
        this.timelineStart = new Date();
    }
    , dateText: function (date) {
        if (npCommon.isNull(date)) {
            date = new Date();
        }
        //return npCommon.formatDate(date, "yyyy-MM-dd HH:mm:ss ms");
        return npCommon.formatDate(date, "HH:mm:ss ms");
    }
    , print: function (text, color) {
        if (npCommon.isBlank(text)) return;
        if (npCommon.isBlank(color)) {
            color = "black";
        }
        if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
            if (window.console) {
                window.console.log(this.dateText() + " : " + text);
            } else {
                npCommon.findDivision(document, "byid", "nppfs-console-log");
                if (npOutCount < 1000) {
                    npQuery("#nppfs-console-log").append("<div style=\"color:" + color + ";\">" + this.dateText() + " : " + npOutCount + ". " + text + "</div>");
                    npOutCount++;
                } else {
                    npBaseCtrl.hideLoading();
                }
            }
        }
    }
    , interval: function (prefix) {
        if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
            var start = this.taskStart;
            var end = new Date();
            npConsole.log("Task(" + prefix + ") Duration: " + ((end.getTime() - start.getTime()) / 1000) + "s, Start:" + npCommon.formatDate(start, "HH:mm:ss ms") + ", End:" + npCommon.formatDate(end, "HH:mm:ss ms"));
        }
    }
    , printTimeline: function () {
        //this.check("Stop Transaction....");
        var log = [];
        log.push("")
        log.push("Transaction Start : " + npCommon.formatDate(this.taskStart, "HH:mm:ss ms"))

        var timeline = this.timeline;
        for (var idx = 0; idx < timeline.length; idx++) {
            var name = timeline[idx].name;
            var tinterval = timeline[idx].end.getTime() - this.taskStart.getTime();
            var einterval = timeline[idx].end.getTime() - timeline[idx].start.getTime();
            var start = npCommon.formatDate(timeline[idx].start, "HH:mm:ss ms");
            var end = npCommon.formatDate(timeline[idx].end, "HH:mm:ss ms");

            log.push("Task(" + name + "), (" + npCommon.comma(tinterval) + " ms / " + npCommon.comma(einterval) + " ms), " + end)
        }
        //log.push("Transaction Stop : " + npCommon.formatDate(new Date(), "HH:mm:ss ms"))
        npConsole.log(log.join("\n"));

        this.reset();
    }
};


/*
 ***************************************************************************
 * nProtect Online Security, User Information
 ***************************************************************************
 */
// 접속자 정보
w.npDefine = new function () {
    var nav = navigator.appName;
    var plt = navigator.platform.toLowerCase();

    function npos(txt) {
        return nua.indexOf(txt);
    }

    function nin(txt) {
        return nua.indexOf(txt) >= 0;
    }

    function lnin(txt) {
        return nua.toLowerCase().indexOf(txt) >= 0;
    }

    this.ie = (nav == 'Microsoft Internet Explorer' || (nav == "Netscape" && (nin("MSIE") || nin("Trident")))) && !nin('QQBrowser');
    this.ie64 = this.ie && nin('Win64; x64');
    this.edge = npos('Mozilla') === 0 && (nin('Edge/') || nin('Edg/'));
    this.ff = nin('Firefox') && npos('Mozilla') === 0 && nav == 'Netscape' && !nin('Navigator');
    this.ns = nin('Gecko') && nin('Navigator');
    this.b360 = nin('360Browser') && nin('Chrome') && nin('Safari');
    this.qq = nin('QQBrowser') && nin("Trident");
    this.sf = nin('Safari') && !nin('Chrome');
    this.op = nin('Opera') || nin('OPR/');
    this.cr = nin('Chrome') && nin('Safari') && !nin('OPR/') && !nin('360Browser') && !nin('Edg/') && !nin('Edge/');
    this.win = (plt.indexOf('win') != -1) && !nin('Windows Phone');
//	this.win98 = nin("Windows 98") || nin("Win98");
//	this.winme = nin("Windows ME");
//	this.winnt40 = nin("Windows NT 4.0");
//	this.win64 = (plt.indexOf('win64') != -1) && !lnin('windows phone');
    this.win9x = nin('Windows 98') || nin('Win98') || nin('Windows ME') || nin('Windows NT 4.0') || nin('Windows NT 5.0') || nin('Windows 2000');
    this.winxp = nin('Windows NT 5.1');
    this.mac = nin('Mac');
    this.lnx64 = nin('Linux') && nin('x86_64');
    this.lnx32 = nin('Linux') && (nin('i386') || nin('i686'));
    this.lnx = nin('Linux');
    this.and = nin('Android');
    this.ios = nin('iPhone') || nin('iPod') || nin('iPad');
    this.iph = nin('iPhone');
    this.ipo = nin('iPod');
    this.ipa = nin('iPad');
    this.fdr = lnin('fedora');
    this.ubt = lnin('ubuntu');
    this.winphone = lnin('windows phone');
    this.winmob = (plt == 'windows mobile');

    this.osVersion = null;
    this.browserVersion = null;
    this.virtualMachine = false;

    this.isMobileDevice = function () {
        if (this.winmob || this.winphone || this.ipa || this.ipo || this.iph || this.and) return true;
        return false;
    };

    this.getOsVersion = function () {
        var version = null;
        var ua = nua;
        if (npDefine.win) {
            var css = [
                {v: "5.0", p: /(Windows NT 5.1|Windows XP)/},
                {v: "5.2", p: /Windows NT 5.2/},
                {v: "6.0", p: /Windows NT 6.0/},
                {v: "7.0", p: /(Windows 7|Windows NT 6.1)/},
                {v: "8.1", p: /(Windows 8.1|Windows NT 6.3)/},
                {v: "8.0", p: /(Windows 8|Windows NT 6.2)/},
                {v: "10.0", p: /(Windows 10|Windows NT 10.0)/},
                {v: "3.0", p: /Windows CE/},
                {v: "3.1", p: /Win16/},
                {v: "3.2", p: /(Windows 95|Win95|Windows_95)/},
                {v: "3.5", p: /(Win 9x 4.90|Windows ME)/},
                {v: "3.6", p: /(Windows 98|Win98)/},
                {v: "3.7", p: /Windows ME/},
                {v: "4.0", p: /(Windows NT 4.0|WinNT4.0|WinNT|Windows NT)/},
                {v: "4.0", p: /(Windows NT 5.0|Windows 2000)/}
            ];

            for (var i = 0; i < css.length; i++) {
                var cs = css[i];
                try {
                    if (cs.p.test(ua)) {
                        version = cs.v;
                        break;
                    }
                } catch (e) {
                }
            }
        } else if (npDefine.mac) {
            if (match = /Mac OS X ([0-9.]*)[._]([0-9.]*)/.exec(ua)) {
                version = match[1] + "." + match[2];
            }

        } else if (npDefine.lnx) {

        }
        //npConsole.log("운영체제 버전 : [" + version + "]");
        return version;
    };

    this.getBwVersion = function () {
        var version;
        var temp;
        var ua = nua;
        if (npDefine.ff) {
            version = ua.substring(ua.toLowerCase().lastIndexOf("firefox"));
            if (version.indexOf(" ") > -1) {
                version = version.substring(0, version.indexOf(" "));
            }
            temp = version.split("/");
            return temp[1];
        } else if (npDefine.op) {

            if (ua.indexOf("OPR/") > -1) {
                version = ua.split("OPR/")[1];
            } else if (ua.indexOf("Opera") > -1) {
                version = ua.split("Opera/")[1];
            }

            if (version.indexOf(" ") > -1) {
                temp = version.split(" ");
                return temp[0];
            } else {
                return version;
            }

        } else if (npDefine.cr || npDefine.b360) {
            version = ua.substring(ua.toLowerCase().lastIndexOf("chrome"));
            if (version.indexOf(" ") != -1) {
                version = version.substring(0, version.indexOf(" "));
                temp = version.split("/");
                return temp[1];
            }
        } else if (npDefine.sf) {
            var reSF = new RegExp(/Version[\/\s](\d+\.\d+)/.test(nua));
            var bwVer = RegExp["$1"];
            return bwVer;

        } else if (npDefine.ie || npDefine.qq) {
            if (ua.indexOf("MSIE") > -1) {
                tua = ua.substring(ua.indexOf("MSIE") + 4, ua.length);
                tua = tua.replace(/(^\s*)|(\s*$)/gi, "");
                temp = tua.split(";");
                temp = temp[0].split(" ");
                return temp[0];
            } else {//IE11
                return ua.substring(ua.indexOf("rv:") + 3, ua.indexOf("rv:") + 7);
            }
        } else if (npDefine.edge) {
            var idx1 = ua.lastIndexOf("Edge/");
            var idx2 = ua.lastIndexOf("Edg/");
            var idx = idx1 >= 0 ? idx1 : idx2;
            version = ua.substring(idx);
            if (version.indexOf(" ") != -1) {
                version = version.substring(0, version.indexOf(" "));
                temp = version.split("/");
                return temp[1];
            } else {
                temp = version.split("/");
                return temp[1];
            }
        }
        //npConsole.log("브라우저 버전 : [" + version + "]");
    };

    this.makeBrowserVersionCode = function () {
        function leftpad(val, len, ch) {
            var ret = val;
            if (ret.length < len) {
                for (var i = 0; i < (len - val.length); i++) {
                    ret = ch + ret;
                }
            } else if (val.length > len) {
                ret = val.substring(0, len);
            }
            return ret;
        };

        var result = "99-000-000";
        try {
            var major = "";
            var minor = "";

            var ver = npDefine.browserVersion;
            if (ver.indexOf(".") != -1) {
                var temp = ver.split(".");
                major = leftpad(temp[0], 3, "0");
                minor = leftpad(temp[1], 3, "0");
            } else {
                major = leftpad(ver, 3, "0");
                minor = leftpad("000", 3, "0");
            }

            var result = major + "-" + minor;
            if (npDefine.ie) {
                result = "10-" + result;
            } else if (npDefine.ff) {
                result = "20-" + result;
            } else if (npDefine.cr) {
                result = "30-" + result;
            } else if (npDefine.sf) {
                result = "40-" + result;
            } else if (npDefine.op) {
                result = "50-" + result;
            } else if (npDefine.edge) {
                result = "60-" + major + "-000";		// Edge 브라우저는 . 뒤의 숫자는 버림(빌드버전으로 사료됨)
                //result = "60-" + result;
            } else if (npDefine.b360) {
                result = "91-" + result;
            } else if (npDefine.qq) {
                result = "92-" + result;
            } else {
                result = "99-000-000";
            }
        } catch (e) {
            result = "99-000-000";
        }
        return result;
    };

    this.isSupported = function (version) {
        return this.isSupportedOs(version) && this.isSupportedBw(version);
    };

    this.isSupportedOs = function (version) {
        if (npCommon.isBlank(this.osVersion)) {
            this.osVersion = npDefine.getOsVersion();
        }
        var osVersion = this.osVersion;
        if (npDefine.win && version.WIN.Support) {
            if (npDefine.win9x) {
                return false;
            }
            return npCommon.checkVersion(osVersion, version.WIN.Os.Min, version.WIN.Os.Max);
        } else if (npDefine.mac && version.MAC.Support) {
            return npCommon.checkVersion(osVersion, version.MAC.Os.Min, version.MAC.Os.Max);
        } else if (npDefine.lnx && version.LINUX.Support) {
            //return checkVersionLinux(version, npBaseCtrl.linuxOsType, npBaseCtrl.linuxOsVersion);
            return true;
        }

        return false;
    };

    function checkVersionLinux(policy, os, ver) {
        var result = true;
        var min, max;

        if (os == npPfsPolicy.Os.LINUX.TYPE.Fedora) {
            min = policy.LINUX.Os.Fedora.Min;
            max = policy.LINUX.Os.Fedora.Max;
        } else if (os == npPfsPolicy.Os.LINUX.TYPE.Ubuntu) {
            min = policy.LINUX.Os.Ubuntu.Min;
            max = policy.LINUX.Os.Ubuntu.Max;
        } else if (os == npPfsPolicy.Os.LINUX.TYPE.CentOS) {
            min = policy.LINUX.Os.CentOS.Min;
            max = policy.LINUX.Os.CentOS.Max;
        } else if (os == npPfsPolicy.Os.LINUX.TYPE.OpenSUSE) {
            min = policy.LINUX.Os.OpenSUSE.Min;
            max = policy.LINUX.Os.OpenSUSE.Max;
        } else {
            // 99 또는 나머지 는 false
            result = false;
        }

        if (!npCommon.isBlank(min)) {
            result = result && npCommon.compareVersion(ver, min);
        }
        if (!npCommon.isBlank(max)) {
            result = result && npCommon.compareVersion(max, ver);
        }

        //console.log("======================================================");
        //console.log("result : "+result);
        //console.log("======================================================");

        return result;
    }


    this.isSupportedBw = function (ver) {
        if (!this.isSupportedOs(ver)) {
            return false;
        }

        var version = null;
        if (npDefine.win) {
            version = ver.WIN.Bw;
        } else if (npDefine.mac) {
            version = ver.MAC.Bw;
        } else if (npDefine.lnx) {
            version = ver.LINUX.Bw;
        }

        if (!npCommon.isNull(version)) {
            if (npCommon.isBlank(this.browserVersion)) {
                this.browserVersion = npDefine.getBwVersion();
            }
            var browserVersion = npDefine.browserVersion;
            if (npDefine.ie && version.IE.Support) {
                return npCommon.checkVersion(browserVersion, version.IE.Min, version.IE.Max);
            } else if (npDefine.ff && version.FF.Support) {
                return npCommon.checkVersion(browserVersion, version.FF.Min, version.FF.Max);
            } else if (npDefine.cr && version.CR.Support) {
                return npCommon.checkVersion(browserVersion, version.CR.Min, version.CR.Max);
            } else if (npDefine.sf && version.SF.Support) {
                return npCommon.checkVersion(browserVersion, version.SF.Min, version.SF.Max);
            } else if (npDefine.edge && version.EG.Support) {
                return npCommon.checkVersion(browserVersion, version.EG.Min, version.EG.Max);
            } else if (npDefine.op && version.OP.Support) {
                return npCommon.checkVersion(browserVersion, version.OP.Min, version.OP.Max);
            } else if (npDefine.b360 && version.B360.Support) {
                return npCommon.checkVersion(browserVersion, version.B360.Min, version.B360.Max);
            } else if (npDefine.qq && version.QQ.Support) {
                return npCommon.checkVersion(browserVersion, version.QQ.Min, version.QQ.Max);
            }
        }

        return false;
    };


    this.isMetroUi = function () {
        if (!this.ie) return false;
        if (!this.browserVersion) return false;
        if (!npCommon.compareVersion(this.browserVersion, "10.0")) return false;

        var supported = null;
        try {
            supported = !!new ActiveXObject("htmlfile");
        } catch (e) {
            supported = false;
        }
        if (supported) return false;

        if (window.screen.availWidth !== window.outerWidth) return false;

        //alert("window.screen.width : [" + window.screen.width + "], window.screen.availWidth : [" + window.screen.availWidth + "], window.outerWidth : [" + window.outerWidth + "], window.innerWidth  : [" + window.innerWidth  + "]");

        return (window.screen.availWidth == window.outerWidth);
    };


    this.IsOldIe = function () {
        return this.ie && (npCommon.compareVersion("7.0", this.browserVersion) || document.documentMode <= 7);
    };
    this.isSupportCORS = function () {

        //return (!this.ie && !this.qq) ? true : false;
        //19.08.07 jh 수정
        return (this.ie || this.qq) ? npDefine.isNewIe() : true;

//		 || ((this.ie || this.qq) && (npCommon.compareVersion(this.browserVersion, "10.0") && document.documentMode >= 10));
    };
    //19.08.07 jh 수정
    this.isNewIe = function () {
        return (this.ie || this.qq) ? (npCommon.compareVersion(this.browserVersion, "10.0") && document.documentMode >= 10) : false;
    };
};

w.npDefine.osVersion = npDefine.getOsVersion();
w.npDefine.browserVersion = npDefine.getBwVersion();
w.npPfsDefine = npDefine;


w.npPfsModules = new function () {
    this.plugins = [];

    this.define = function (data) {
        if (npCommon.isBlank(data.id)) {
            alert("제품 식별 고유코드가 필요합니다.");
            return;
        }
        if (npCommon.isBlank(data.controller)) {
            alert("제품 제어 스크립트 객체가 필요합니다.");
            return;
        }
        this.plugins.push(data);
    }
    this.isRequireHandshake = function () {
        var ret = false;
        npQuery(this.plugins).each(function () {
            if (!this.controller.isRunnable() || !this.controller.isSupported()) {
                return true;
            }

            var isExecutable = true;
            if (typeof (this.isExecutable) == "function") {
                isExecutable = this.isExecutable(npBaseCtrl.Options);
            }

            if (isExecutable == true && !npCommon.isNull(this.handshake) && this.handshake == true) {
                ret = true;
                return false;
            }
        });
        return ret;
    }
    this.isRequireE2E = function () {
        var ret = false;
        npQuery(this.plugins).each(function () {
            if (!this.controller.isRunnable() || !this.controller.isSupported()) {
                return true;
            }

            var isExecutable = true;
            if (typeof (this.isExecutable) == "function") {
                isExecutable = this.isExecutable(npBaseCtrl.Options);
            }

            if (isExecutable == true && !npCommon.isNull(this.endtoend) && this.endtoend == true) {
                ret = true;
                return false;
            }
        });
        return ret;
    }
    this.isRequireVM = function () {
        var ret = false;
        npQuery(this.plugins).each(function () {
            if (!this.controller.isSupported()) {
                //if(!this.controller.isRunnable() || !this.controller.isSupported()) {
                return true;
            }

            var isExecutable = true;
            if (typeof (this.isExecutable) == "function") {
                isExecutable = this.isExecutable(npBaseCtrl.Options);
            }

            if (isExecutable == true && !npCommon.isNull(this.runvirtualos) && this.runvirtualos == false) {
                ret = true;
                return false;
            }
        });
        return ret;
    }
    this.getPlugins = function () {
        return this.plugins;
    }

    var remainStarting = [];

    var isStarting = false;
    this.init = function (params) {
        if (isStarting == true) {
            return;
        }

        npQuery(document).bind("nppfs-module-startup", function (event) {
            var name = event.target;
            remainStarting.splice(npCommon.indexOf(remainStarting, name), 1);
            if (remainStarting.length == 0) {
                npQuery(document).trigger({type: "nppfs-nos-startup", time: new Date()});
                isStarting = false;
            }
        });

        var runcnt = 0;
        npQuery(this.plugins).each(function () {
            if (!this.controller.isRunnable()) return true;

            runcnt++;

            //if(!npCommon.isNull(this.controller) && !npCommon.isNull(this.controller.init) && typeof(this.controller.init) == "function"){
            this.controller.init(params);
            //}
        });

        if (runcnt == 0) {
            isStarting = false;
        }
    }

    this.startup = function (params) {
        var runcnt = 0;
        if (isStarting == true) {
            return;
        }

        isStarting = true;
        npQuery(this.plugins).each(function () {
            if (!this.controller.isRunnable()) return true;

            remainStarting.push(this.id);
            runcnt++;
        });
        npQuery(this.plugins).each(function () {
            if (!this.controller.isRunnable()) return true;

            //npConsole.log("........... modules.startup : " + this.id);
            //if(!npCommon.isNull(this.controller) && !npCommon.isNull(this.controller.startup) && typeof(this.controller.startup) == "function"){
            this.controller.startup(params);
            //}
        });
        if (runcnt == 0) {
            npQuery(document).trigger({type: "nppfs-nos-startup", time: new Date()});
            isStarting = false;
        }
    }

    this.isComplete = function () {
        var ret = true;
        npQuery(this.plugins).each(function () {
            if (!this.controller.isRunnable()) return true;

            remainStarting.push(this.id);
            runcnt++;

            //if(!npCommon.isNull(this.controller) && !npCommon.isNull(this.controller.isComplete) && typeof(this.controller.isComplete) == "function"){
            ret = ret && this.controller.isComplete();
            return ret;
            //}
        });
        return ret;
    }


    this.isSupported = function () {
        var ret = true;
        npQuery(this.plugins).each(function () {
            //if(!npCommon.isNull(this.controller) && !npCommon.isNull(this.controller.isSupported) && typeof(this.controller.isSupported) == "function"){
            ret = ret && this.controller.isSupported();
            return ret;
            //}
        });
        return ret;
    }
    this.finalize = function (params) {
        npQuery(this.plugins).each(function () {
            //if(!npCommon.isNull(this.controller) && !npCommon.isNull(this.controller.finalize) && typeof(this.controller.finalize) == "function"){
            this.controller.finalize(params);
            //}
        });
    }
};
w.npPfsPlugins = npPfsModules;


w.npPacket = {
    PRODUCT_DM: "d98cd0b1b8b29013e8a2d433cd08210d7bdb8265bc78818c676e6da4d1098570"
    , PRODUCT_FW: "a17edc0f7727b2d44ab08374e0aaadef0eb8ec251a4c5371d8d1319ad771ab26"
    , PRODUCT_KC: "993ffcfb5f6088f910a59907079b0cb56adb44799781f9dd52a85566ef6233c1"
    , PRODUCT_FD: "e9f8e723681bb131f1693589015859d22c44e0e3485c9ba7fbf29c9d321e7e61"
//	, PRODUCT_KP : "ac5f9c71d07d06f8a0daee6f8f484705d6ac3257c9b91cbf13b86d2c945ddb66"

    , CUSTOMER_ID: "791824798438a7ac59737f20ea6c0880f24e7182a424450fd1a819972148d694"

    , MODE_SYNC: "1"
    , MODE_ASYNC: "0"

    , RESULT_TRUE: "59615036FA2C1A9EFC35D43EC6C77269"
    , RESULT_FALSE: "B303AA8350126650FCE9111D899E21F0"
    , RESULT_REQ_READY: "FA48FAE45FDF6C6F29DD4766E50F5931"
    , RESULT_DETECT_DEBUG: "201A9DFAC7ED61A876CA0B1D7AF18161"
    , RESULT_REQ_REINSTALL: "14F1CF1F85E360D567D4A9C43B99C33B"
    , RESULT_CHECK_VERSION: "A0131152837EFEA26E0598577DE5E429"
    , RESULT_UNICODE: "94B53D15A6C345F18DB55F5C879B661E"
    , RESULT_PROXY: "64AD3D4FEF74428b9A206D4A17D72C3E"
    , RESULT_USER_PROXY: "2EA074D6A53044138EC6DB91CFE2691D"

    , CMD_HEALTH: "47494638396101000100820031FFFFFF"
    , CMD_HANDSHAKE: "c813d2cf4507620123345cd1504e298eeb02a727a28bbb3957849c690d3cdc35"
    , CMD_CHECK_VM: "ea9a084ba3013b62cb1bdc6f27fe09ef63511df6fd6aca05a26d5f8f11a198b3"
    , CMD_KEEP_ALIVE: "2f461abb34aeace45cfd92ad706ddbd3b9f0a9f9f541108c58d46c2e2aea951c"
    , CMD_CHECK_VERSION: "a5220bcbfba15b27d97b8d6cda9a5227c402e36c3da79e92efa28b89b72465e8"
    , CMD_ENCRYPT_VALUE: "965801862ef9bd75b26072bd1b03c710569fd09aa51878954c93398fab22ee2a"
    , CMD_GET_OS_VERSION: "ed4183d49cdabaf906cd040864aa4d2f80e93af23795a383302a392fb55b102b"
    , CMD_DISABLE_USER_PORXY: "37981636bff18b9e8360f1957a5fd86bc9b6407b7ed38546adc7ed65aeca4907"
    , CMD_IGNORE_PORXY: "6076ba41743fce481c2651f6448dda96a4b854e882de4a93c64d8e14dc71be98"
    , CMD_DISABLE_PORXY: "375c1c2b3da6312c2b736f31034e532f01163487a410efb4bc3a778e5e58f15c"
}


w.npQuery = (typeof (nosQuery) != "undefined") ? nosQuery : jQuery;
w.npCommon = new function () {
    function Padder(len, pad) {
        if (len === undefined) {
            len = 1;
        } else if (pad === undefined) {
            pad = '0';
        }

        var pads = '';
        while (pads.length < len) {
            pads += pad;
        }

        this.pad = function (what) {
            var s = what.toString();
            return pads.substring(0, pads.length - s.length) + s;
        };
    }

    this.isNull = function (data) {
        if (typeof (data) == "undefined" || data == null) {
            return true;
        }
        return false;
    };

    this.isBlank = function (data) {
        if (typeof (data) == "undefined" || data == null || data == "") {
            return true;
        }
        return false;
    };

    this.n2b = function (data, def) {
        if (this.isNull(def)) def = "";
        if (this.isNull(data)) return def;
        return data;
    };

    this.selectorByName = function (name, parents) {
        if (!npCommon.isBlank(name) && name.indexOf(":") >= 0) {
            if (typeof (parents) != "undefined") {
                return npQuery(document.getElementsByName(name)[0], parents);
            } else {
                return npQuery(document.getElementsByName(name)[0]);
            }
        } else {
            if (typeof (parents) != "undefined") {
                return npQuery("[name='" + name + "']", parents);
            } else {
                return npQuery("[name='" + name + "']");
            }
        }
    };

    this.selectorById = function (name, parents) {
        if (!npCommon.isBlank(name) && name.indexOf(":") >= 0) {
            if (typeof (parents) != "undefined") {
                return npQuery(document.getElementById(name), parents);
            } else {
                return npQuery(document.getElementById(name));
            }
        } else {
            if (typeof (parents) != "undefined") {
                return npQuery("#" + name, parents);
            } else {
                return npQuery("#" + name);
            }
        }
    };

    this.eraseSpecialChars = function (tmp) {
        var regExp = /[\{\}\[\]\/?.,;:|\)*~`!^\-+<>@#$%&\\\=\(\'\"]/gi;
        if (regExp.test(tmp)) {
            var t = tmp.replace(regExp, "");
            tmp = t;
        }
        return tmp;
    };

    this.makeUuid = function () {
        var datalength = new Padder(15);

        var dummy = Math.floor(Math.random() * 99) + 1;
        // dummy 값이 두자리가 아닐 경우 패딩 값이 맨 앞자리에 생성됨
        if (dummy < 10) dummy = dummy + 10;

        return datalength.pad((new Date().getTime()).toString() + dummy);
    }

    this.makeLength = function (text) {
        if (this.isNull(text)) {
            text = "";
        }
        var length = text.length;
        var datalength = new Padder(4);
        return datalength.pad((length).toString(16));
    }

    this.encParam = function (data) {
        return encodeURIComponent(data);
    },
        this.GetKeys = function (obj) {
            var keys = [];
            for (var key in obj) {
                // nexacro platform이 load된 경우
                if (typeof nexacro == "object" && nexacro._bInitPlatform) {
                    // 넥사크로플랫폼에서 슈퍼클래스(Object)를 핸들링하는 관계로
                    // IE8의 경우 아래의 2개의 Property가 추가되어 NOS 미동작
                    if (key == "getSetter" || key == "getNumSetter") continue;
                }
                keys.push(key);
            }
            return keys;
        }

    this.CountKeys = function (obj) {
        this.GetKeys(obj).length;
    }

    this.hexEncode = function (byteArray) {
        var result = "";
        if (!byteArray) {
            return;
        }
        for (var i = 0; i < byteArray.length; i++) {
            result += ((byteArray[i] < 16) ? "0" : "") + byteArray[i].toString(16);
        }
        return result;
    };

    this.hexDecode = function (hex, charset) {
        if (npCommon.isBlank(hex)) {
            return "";
        }

        var r = '';
        if (hex.indexOf("0x") == 0 || hex.indexOf("0X") == 0) {
            hex = hex.substr(2);
        }
        if (hex.length % 2) {
            hex += '0';
        }
        var arr = [];
        for (var i = 0; i < hex.length; i += 2) {
            arr.push(parseInt(hex.slice(i, i + 2), 16));
        }
        if (charset == "UTF8") {
            return toString(arr);
        } else {
            for (i = 0; i < arr.length; i++) {
                r += String.fromCharCode(arr[i]);
            }
        }
        return r;
    };

    function toString(array) {
        var char2, char3;
        var out = "";
        var len = array.length;
        var i = 0;
        while (i < len) {
            var c = array[i++];
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: // 0xxxxxxx
                    out += String.fromCharCode(c);
                    break;
                case 12:
                case 13: // 110x xxxx   10xx xxxx
                    char2 = array[i++];
                    out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14: // 1110 xxxx  10xx xxxx  10xx xxxx
                    char2 = array[i++];
                    char3 = array[i++];
                    out += String.fromCharCode(((c & 0x0F) << 12) |
                        ((char2 & 0x3F) << 6) |
                        ((char3 & 0x3F) << 0));
                    break;
            }
        }
        return out;
    }

    this.comma = function (str) {
        str = String(str);
        return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
    }

    this.uncomma = function (str) {
        str = String(str);
        return str.replace(/[^\d]+/g, '');
    }

    this.arrayIn = function (arr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === value) {
                return true;
            }
        }
        return false;
    };

    this.indexOf = function (arr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === value) {
                return i;
            }
        }
        return -1;
    };

    this.arrayNotIn = function (arr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === value) {
                return false;
            }
        }
        return true;
    };

    this.getRandomBytes = function (howMany) {

        var i;
        var bytes = new Array();
        for (i = 0; i < howMany; i++) {

            bytes[i] = Math.round(Math.random() * 255);

        }
        return bytes;
    }

    this.formatPlaintext = function (plaintext, bsize) {
        var bpb = AES.blockSizeInBits / 8;               // bytes per block
        var i;

        // if primitive string or String instance
        if (typeof plaintext == "string" || plaintext.indexOf) {
            plaintext = plaintext.split("");
            // Unicode issues here (ignoring high byte)
            for (i = 0; i < plaintext.length; i++) {
                plaintext[i] = plaintext[i].charCodeAt(0) & 0xFF;
            }
        }

        // zero padding
        for (i = bpb - (plaintext.length % bpb); i > 0 && i < bpb; i--) {
            plaintext[plaintext.length] = 0;
        }
        return plaintext;
    }

    this.getBytes = function (plaintext) {
        var i;
        var bytes = [];
        if (typeof plaintext == "string" || plaintext.indexOf) {
            /*
			bytes = plaintext.split("");
			for (i=0; i < bytes.length; i++) {
				bytes[i] = bytes[i].charCodeAt(0) & 0xff;
			}
			*/
            var utf8 = [];
            var str = plaintext;
            for (var i = 0; i < str.length; i++) {
                var charcode = str.charCodeAt(i);
                if (charcode < 0x80) utf8.push(charcode);
                else if (charcode < 0x800) {
                    utf8.push(0xc0 | (charcode >> 6), 0x80 | (charcode & 0x3f));
                } else if (charcode < 0xd800 || charcode >= 0xe000) {
                    utf8.push(0xe0 | (charcode >> 12), 0x80 | ((charcode >> 6) & 0x3f), 0x80 | (charcode & 0x3f));
                } else {
                    i++;
                    // UTF-16 encodes 0x10000-0x10FFFF by
                    // subtracting 0x10000 and splitting the
                    // 20 bits of 0x0-0xFFFFF into two halves
                    charcode = 0x10000 + (((charcode & 0x3ff) << 10) | (str.charCodeAt(i) & 0x3ff));
                    utf8.push(0xf0 | (charcode >> 18), 0x80 | ((charcode >> 12) & 0x3f), 0x80 | ((charcode >> 6) & 0x3f), 0x80 | (charcode & 0x3f));
                }
            }
            return utf8;
        }
        return bytes;
    }

    this.encrypt = function (plaintext, key, mode, bsize) {
        AES.blockSizeInBits = npCommon.isNull(bsize) ? 128 : bsize;
        AES.keySizeInBits = npCommon.isNull(key) ? 256 : key.length * 8;
        var bpb = AES.blockSizeInBits / 8; // bytes per block
        var i, aBlock;
        var ct;                                 // ciphertext

        if (!plaintext || !key)
            return;
        if (key.length * 8 != AES.keySizeInBits) {
            return;
        }
        if (mode == "CBC")
            ct = this.getRandomBytes(bpb);             // get IV
        else {
            mode = "ECB";
            ct = new Array();
        }

        // convert plaintext to byte array and pad with zeros if necessary.
        plaintext = this.formatPlaintext(plaintext);
        //console.log("plaintext : " + npCommon.hexEncode(plaintext));

        var expandedKey = new AES.keyExpansion(key);
        for (var block = 0; block < plaintext.length / bpb; block++) {
            aBlock = plaintext.slice(block * bpb, (block + 1) * bpb);
            if (mode == "CBC") {
                for (var i = 0; i < bpb; i++) {
                    aBlock[i] ^= ct[block * bpb + i];
                }
            }
            ct = ct.concat(AES.AESencrypt(aBlock, expandedKey));
        }

        return npCommon.hexEncode(ct);
    }


    this.decrypt = function (ciphertext, key, mode, bsize, charset) {
        AES.blockSizeInBits = npCommon.isNull(bsize) ? 128 : bsize;
        AES.keySizeInBits = npCommon.isNull(key) ? 256 : key.length * 8;
        var bpb = AES.blockSizeInBits / 8; // bytes per block
        var pt = new Array(); // plaintext array
        var aBlock; // a decrypted block
        var block; // current block number
        if (!ciphertext || !key) return;
        if (typeof (ciphertext) == "string") {
            ciphertext = ciphertext.split("");
            for (i = 0; i < ciphertext.length; i++) {
                ciphertext[i] = ciphertext[i].charCodeAt(0) & 0xFF;
            }
        }

        if (key.length * 8 != AES.keySizeInBits) {
            return;
        }
        if (!mode) {
            mode = "ECB"; // assume ECB if mode omitted
        }
        var expandedKey = new AES.prepareDecryption(key);
        for (block = (ciphertext.length / bpb) - 1; block > 0; block--) {
            aBlock = AES.AESdecrypt(ciphertext.slice(block * bpb, (block + 1) * bpb), expandedKey);
            if (mode == "CBC") {
                for (var i = 0; i < bpb; i++) {
                    pt[(block - 1) * bpb + i] = aBlock[i] ^ ciphertext[(block - 1) * bpb + i];
                }
            } else {
                pt = aBlock.concat(pt);
            }
        }
        // do last block if ECB (skips the IV in CBC)
        if (mode == "ECB") {
            pt = AES.AESdecrypt(ciphertext.slice(0, bpb), expandedKey).concat(pt);
        }

        // cleanup zero padding
        var tval = pt[pt.length - 1];
        while (typeof (tval) == "undefined" || tval == null || tval == 0) {
            pt.pop();
            tval = pt[pt.length - 1];
        }

        //return String.fromCharCode.apply(null, new Uint8Array(npCommon.hexDecode(npCommon.hexEncode(pt))));
        return npCommon.hexDecode(npCommon.hexEncode(pt), charset);
    }

    this.send = function (url, query, o) {
        var result = "";

        if (this.isNull(o)) {
            o = {};
        }
        if (this.isNull(o.async)) {
            o.async = false;		/* 변경하면 안됨, WAS와 통신하는 로직은 동기식으로 처리.. */
        }
        if (this.isNull(o.timeout) || o.timeout <= 0) {
            o.timeout = 3000;
        }
        if (this.isNull(o.callback)) {
            o.callback = function (xhr) {
                var result = "";
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        result = xhr.responseText;
                    } else {
                        //npConsole.log("주소(" + url + ")에 접속할 수 없습니다.");
                    }
                } else {
                    //npConsole.log("["+xhr.readyState+"] Statement error.");
                }
                return result;
            };

        }

        npConsole.log("REQ : " + query);

        try {
            npQuery.ajax({
                url: url,
                cache: false,
                async: o.async,
                type: "post",
                global: false,
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                data: query
                , error: function (xhr, textStatus, errorThrown) {
                    return o.callback(xhr);
                }, success: function (data, textStatus, xhr) {
                    result = data;
                    return o.callback(xhr);
                }
            });
        } catch (e) {
            o.callback({readyState: 4, status: 999, responseText: ""});
            npConsole.error("ERR : " + e);
        }
        return result;
    };


    this.requestState = {};
    var isSupportCORS = false;
    var tryCount = 0;
    this.sendCommand = function (command, o, callback) {
        if (npCommon.isNull(o)) {
            o = {};
        }
        if (npCommon.isNull(o.async)) {
            //o.async = false;
            o.async = true;
        }
        if (npCommon.isNull(o.host)) {
            o.host = npBaseCtrl.currentHost;
        }
        if (npCommon.isNull(o.port)) {
            o.port = npBaseCtrl.currentPort;
        }
        if (npCommon.isNull(o.direct)) {
            o.direct = false;
        }
        if (npCommon.isNull(o.callback)) {
            o.callback = function (data) {
                //npConsole.log("Default Callback [" + data + "]");
            };
        }

        var callback = !npCommon.isNull(callback) ? callback : function (xhr) {
            var result = "";
            if (xhr.readyState == 4) {
                if (xhr.status == 200 || xhr.status == 999) {
                    try {
                        result = xhr.responseText;
                        npConsole.log("RES : " + xhr.responseText);
                        o.callback(xhr.responseText);
                    } catch (e) {
                        //npConsole.log(e);
                    }
                } else {
                    //o.callback(xhr.responseText);
                    //npConsole.log("서버에 접속할 수 없습니다.");
                }
            } else {
                //npConsole.log("["+xhr.readyState+"] Statement error.");
            }
            return result;
        };

//		if(o.direct == true) {
//			if(npDefine.isSupportCORS()) {
//				return this.sendSyncCommand(command, callback, o);
//			} else {
//				return this.sendDirectCommand(command, callback, o);
//			}
//		} else if(o.byiframe == true && (npDefine.ie || npDefine.qq)) {
//			return this.sendIFrameCommand(command, callback, o);
//		} else if(npDefine.isSupportCORS()) {
//			return this.sendSyncCommand(command, callback, o);
//		} else {
//			return this.sendAsyncCommand(command, callback, o);
//		}

        if (npDefine.isSupportCORS() || isSupportCORS) {
            return this.sendSyncCommand(command, callback, o);
        } else if (o.direct == true) {
            return this.sendDirectCommand(command, callback, o);
        } else if (o.byiframe == true && (npDefine.ie || npDefine.qq)) {
            return this.sendIFrameCommand(command, callback, o);
        } else if (tryCount > 20) {
            return this.sendAsyncCommand(command, callback, o);
        } else {
            return this.sendSyncCommand(command, function (xhr) {
                var result = "";
                if (xhr.readyState == 4) {
                    // if(xhr.status == 200 || xhr.status == 999) {
                    if (xhr.status == 200) {
                        isSupportCORS = true;
                        callback.call(null, xhr);
                    } else {
                        tryCount++;
                        return npCommon.sendAsyncCommand(command, callback, o);
                    }
                } else {
                    //npConsole.log("["+xhr.readyState+"] Statement error.");
                }
                return result;
            }, o);
        }
    };
    this.sendSyncCommand = function (command, callback, o) {
        if (npCommon.isNull(o.timeout)) {
            o.timeout = 3000;
        }

        var isreturn = false;

        function docallback(data) {
            if (isreturn == false) {
                callback(data);
                isreturn = true;
            }
        }

        var url = npBaseCtrl.makeUrl(o.port, o.host);

        var timeoutid = null;

        if (o.timeout > 0) {
            timeoutid = setTimeout(function () {
                docallback({readyState: 4, status: 999, responseText: ""});
            }, o.timeout);
        }


        try {
            npConsole.log("REQ : " + command);
            npQuery.ajax({
                url: url,
                cache: false,
                async: o.async,
                type: "post",
                global: false,
                data: command,
                error: function (xhr, textStatus, errorThrown) {
                    //npConsole.log("url => " + url +", xhr.status => " + xhr.status);
                    docallback({readyState: 4, status: 999, responseText: textStatus + ":" + errorThrown});
                },
                success: function (data, textStatus, xhr) {
                    docallback(xhr);
                },
                complete: function (xhr, textStatus) {
                    if (timeoutid != null) clearTimeout(timeoutid);
                }
            });
        } catch (e) {
            docallback({readyState: 4, status: 999, responseText: ""});
            npConsole.error("ERR : " + e);
        }
    };

    this.lock = false;
    this.commandQueue = [];
    this.executeQueue = function () {
        var func = npCommon.commandQueue.shift();
        if (typeof (func) == "function") {
//			npConsole.log("Mutex..... Excute...");
            func();
        }
    };

    this.sendAsyncCommand = function (command, callback, o) {
        if (npCommon.isNull(o.timeout)) {
            o.timeout = 3000;
        } else if (o.timeout <= 0) {
            o.timeout = 60 * 1000;
        }

        if (npCommon.lock == true) {
            if (npCommon.commandQueue.length > 0) {
                npCommon.executeQueue();
//				return;
            }

//			npConsole.log("Mutex..... Queueing... " + command);
            npCommon.commandQueue.push(function () {
                npCommon.sendAsyncCommand(command, callback, o);
            });
        } else {
            npCommon.lock = true;
            var url = npBaseCtrl.makeUrl(o.port, o.host);

            var timeoutid = setTimeout(function () {
                callback({readyState: 4, status: 999, responseText: ""});
                try {
                    npCommon.lock = false;
                    npCommon.executeQueue();
                } catch (e) {
                }
            }, o.timeout);

            npConsole.log("REQ : " + command);
            try {
                npQuery.ajax({
                    url: url,
                    cache: false,
                    crossDomain: true,
                    async: false,
                    type: "GET",
                    global: false,
                    dataType: "jsonp",
                    jsonp: "jsonp_callback",
                    contentType: "application/javascript",
                    timeout: o.timeout,
                    data: {
                        Code: command
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        // parsererror
                        if (textStatus == "abort" || textStatus == "timeout" || textStatus == "parsererror" || textStatus == "error") {
                            callback({readyState: 4, status: 999, responseText: textStatus + ":" + errorThrown});
                        }
                    },
                    success: function (data, textStatus, xhr) {
                        if (data != null && data.RESULT != null) {
                            callback({readyState: 4, status: 200, responseText: data.RESULT});
                        }
                    },
                    complete: function (xhr, textStatus) {
                        clearTimeout(timeoutid);
                        try {
                            npCommon.lock = false;
                            npCommon.executeQueue();
                        } catch (e) {
                        }
                    }
                });
            } catch (e) {
                callback({readyState: 4, status: 999, responseText: ""});
                npConsole.error("ERR : " + e);
                npCommon.lock = false;
                npCommon.executeQueue();
            }
        }
    };


    this.sendDirectCommand = function (command, callback, o) {
        var url = npBaseCtrl.makeUrl(o.port, o.host);
        if (npCommon.isNull(o.timeout)) {
            o.timeout = 3000;
        } else if (o.timeout <= 0) {
            o.timeout = 60 * 1000;
        }

        var timeoutid = setTimeout(function () {
            callback({readyState: 4, status: 999, responseText: ""});
        }, o.timeout);

        npConsole.log("REQ : " + command);

        try {
            npQuery.ajax({
                url: url,
                cache: false,
                crossDomain: true,
                async: false,
                type: "GET",
                global: false,
                dataType: "jsonp",
                jsonp: "jsonp_callback",
                contentType: "application/javascript",
                timeout: o.timeout,
                data: {
                    Code: command
                },
                error: function (xhr, textStatus, errorThrown) {
                    // parsererror
                    if (textStatus == "abort" || textStatus == "timeout" || textStatus == "parsererror" || textStatus == "error") {
                        callback({readyState: 4, status: 999, responseText: textStatus + ":" + errorThrown});
                    }
                },
                success: function (data, textStatus, xhr) {
                    if (data != null && data.RESULT != null) {
                        callback({readyState: 4, status: 200, responseText: data.RESULT});
                    }
                },
                complete: function (xhr, textStatus) {
                    clearTimeout(timeoutid);
                }
            });
        } catch (e) {
            clearTimeout(timeoutid);
            callback({readyState: 4, status: 999, responseText: ""});
            npConsole.error("ERR : " + e);
        }
    };


    var eventBinded = false;
    this.sendIFrameCommand = function (command, callback, o) {
        var url = npBaseCtrl.makeUrl(o.port, o.host);
        url += "?ifrm=" + command;
//		url = "http://192.168.1.85:9999/message.jsp";

        //var targetOrigin = "http://192.168.1.85:9999";
        var targetOrigin = npBaseCtrl.makeUrl(o.port, o.host);

        // iframe 동적 생성
        var _iframe = document.getElementById("keep-alive-iframe");
        var $iframe = npQuery("#keep-alive-iframe");
        if ($iframe.length == 0) {
            _iframe = document.createElement("iframe");
            _iframe.id = "keep-alive-iframe";
            _iframe.style.display = "none";
            npQuery("body").append(_iframe);

            $iframe = npQuery("#keep-alive-iframe");

            if (eventBinded == false) {
                eventBinded = true;

                // MS Internet Explorer 8 부터 onMessage 이벤트가 동작함(7 이하 이벤트를 처리하지 않도록 함) by YGKIM 2017.03.03
                if (!npDefine.IsOldIe()) {
                    // 아래의 이벤트가 올 경우에는 KEEP-ALIVE의 결과가 TRUE가 아닌 경우
                    npQuery(window).on("message", function (evt) {
                        //					npConsole.log("response.......");
                        try {
                            //console.log(evt);
                            var resorigin = evt.origin || evt.originalEvent.origin;
                            var resdata = evt.data || evt.originalEvent.data;
                            //						npConsole.log("message : [" + evt + "][" + resdata + "][" + resorigin + "][" + targetOrigin + "]");
                            if (resorigin === targetOrigin) {
                                var data = npQuery.parseJSON(resdata);
                                if (data.caller == "nppfs-nos-response") {
                                    callback({readyState: 4, status: 200, responseText: data.response});
                                }
                            }
                        } catch (e) {
                        }
                    });
                }

            }

            $iframe.on("load", function (event) {
                //console.log(event);
//				npConsole.log("loaded.......");

                if (!npDefine.IsOldIe()) {
                    _iframe.contentWindow.postMessage("", targetOrigin);
                }

                try {
                    // 일단 성공메시지를 보내고 나중에 특정한 오류가 있을 경우에는 onMessage 에서 처리 by YGKIM 2017.03.03
                    callback({readyState: 4, status: 200, responseText: npPacket.RESULT_TRUE});
                } catch (e) {
                }
            });
            $iframe.on("error", function (event) {
                callback({readyState: 4, status: 999, responseText: ""});
            });
        }


        npConsole.log("REQ : " + command);

        $iframe.attr("src", url);
    };

    findElementCache = {};
    this.removeCacheElement = function (elename, formname) {
        var key = elename;
        if (!npCommon.isBlank(formname)) {
            if (typeof (formname) == "string") {
                key = elename + "_" + formname;
//				form = npQuery("form[name='" + formname + "']");
            } else if (typeof (formname) == "object") {
//				form = formname;
            }
        }
        if (findElementCache[key]) {
            findElementCache[key] = null;
        }
    };

    this.findElement = function (elename, formname) {
        var form = null;
        var key = elename;
        var obj = null;
        if (!npCommon.isBlank(formname)) {
            if (typeof (formname) == "string") {
                form = npQuery("form[name='" + formname + "']").get(0);
            }
            if (typeof (formname) == "object") {
                form = npQuery(formname);
            }
            if (!npCommon.isNull(form)) {
                form = this.findParentForm(form);
                formname = npQuery(form).attr("name");
                key = elename + "_" + npQuery(form).attr("name");
            }
        }

        if (typeof (elename) === "string") {
            if (npCommon.selectorById(elename).get(0)) {
                obj = npCommon.selectorById(elename).get(0);
            } else {
                var ret = (form != null && form.length > 0) ? npCommon.selectorByName(elename, form) : npCommon.selectorByName(elename);
                obj = (ret != null && ret.length > 0) ? ret.get(0) : npCommon.selectorById(elename).get(0);
            }
        }
        if (typeof (elename) === "object") {
            obj = elename;
        }

        return obj;

    }

    this.newElement = function (tag) {
        return document.createElement(tag);
    }
    this.makeElement = function (obj, arraylist, valuelist) {

        if (this.isNull(obj)) {
//			npConsole.log(npMessage.m43);
            return;
        }


        var arrInputList = arraylist;
        var arrValueList = valuelist;
        if (!npCommon.isNull(arrValueList)) {
            if (arrInputList.length != arrValueList.length) {
                alert(npMessage.m44);
            }
        }
// try {

        var parent = npCommon.findElement(obj);

        var div4ie7 = npCommon.findDivision(parent, "byclass", "nppfs-elements");


        if (!npDefine.IsOldIe()) {
            npQuery(div4ie7).hide();
        }

        var html = [];
        var createyn = false;
        for (var i = 0; i < arrInputList.length; i++) {
            var element = this.findElement(arrInputList[i], obj);
            if (npCommon.isNull(element)) {
                if (createyn == false) {
                    createyn = true;
                }
                var key = arrInputList[i];
                var value = "";
                if (!npCommon.isNull(arrValueList)) {
                    value = arrValueList[i];
                }

                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_RELEASE) {
                    html.push("<input type=\"hidden\" name=\"" + key + "\" value=\"" + value + "\">");
                } else {
                    html.push(key + "<input type=\"text\" name=\"" + key + "\" value=\"" + value + "\"><br />");
                }
            } else if (!npCommon.isNull(arrValueList)) {
                npQuery(element).val(arrValueList[i]);
            }
        }

        if (createyn == true) {
            npQuery(div4ie7).append(html.join("\n"));
            if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG && (!npDefine.cr || npDefine.browserVersion < 49)) {
                npQuery(div4ie7).show();
            }
        }

//		}catch(e){
//			npConsole.log("POS 010 [" + e + "]");
//		}
    };

    this.copyDivision = function (asis, tobe) {
        var asisdiv = npCommon.findDivision(asis, "byclass", "nppfs-elements");
        var tobediv = npCommon.findDivision(tobe, "byclass", "nppfs-elements");
        if (!npCommon.isNull(asisdiv) && !npCommon.isNull(tobediv)) {
            //tobediv.innerHTML = asisdiv.innerHTML;
            npQuery("input", npQuery(asisdiv)).each(function (index, element) {
                var key = element.name;
                var val = element.value;

                if (!npCommon.isNull(tobe.elements[key])) {
                    tobe.elements[key].value = val;
                } else {
                    //L.c1(tobe, [key], [value]);
                    if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_RELEASE) {
                        npQuery(tobediv).append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + val + "\" />");
                    } else {
                        npQuery(tobediv).append(key + " : <input type=\"text\" name=\"" + key + "\" value=\"" + val + "\" />");
                    }
                }
            });
        }
    };

    this.isShowMessage = false;
    this.createDivision = function (parent, type, element) {
        var div = npCommon.newElement("div");
        if (type == "byclass") {
            div.setAttribute("class", element);
        } else {
            parent = document.body;
            div.setAttribute("id", element);
        }
        div.setAttribute("style", "display:none;");

        //parent.appendChild(div);
        npQuery(parent).prepend(div);
        return div;
    }

    this.findParentForm = function (form) {
        var ret = form;
        var parents = npQuery(form).parents("form");
        if (parents.length > 0) {
            var parent = parents.last();
            ret = parent.get(0);
        }
        return ret;
    }
    this.findDivision = function (parent, type, element) {
        if (npCommon.isBlank(element)) return null;

        parent = parent || document;
        if (parent.tagName && parent.tagName.toLowerCase() === "form") {
            parent = this.findParentForm(parent);
        }

        var div = (type == "byid") ? npQuery("#" + element).get(0) : npQuery("div." + element, npQuery(parent)).get(0);
        return div || this.createDivision(parent, type, element);
    }

    this.stopEvent = function (event) {
        event.preventDefault ? event.preventDefault() : event.returnValue = false;
    };

    this.getElapsedTime = function (startTime) {
        var ret = -1;
        try {
            var endTime = new Date();
            ret = endTime - startTime;
            if (ret > 100000) ret = 99999;
        } catch (e) {
        }
        return ret + "";
    };


    this.splitVersion = function (strData) {
        var arr = strData.split(/ |,|\.|\_|\//g);
        var ret = new Array();
        var arrIndex = 0;
        for (var index = 0; index < arr.length; index++) {
            if (!npCommon.isBlank(arr[index])) {
                ret[arrIndex] = arr[index];
                arrIndex++;
            }
        }
        if (ret.length > 0) return ret;

        return null;
    };

    this.compareVersion = function (strOrgVer, strVer, strSplitData) {
        if (this.isBlank(strOrgVer)) return false;
        if (this.isBlank(strVer)) return false;
        //if(this.isBlank(strSplitData)) return false;

        var orgVerArr = this.splitVersion("" + strOrgVer);
        var orgVerArrLen = orgVerArr.length;
        var verArr = this.splitVersion("" + strVer);

        var i = 0;
        for (i = 0; i < orgVerArrLen; i++) {
            if (npCommon.isNull(verArr[i])) verArr[i] = 0;
            orgVerArr[i] = parseInt(orgVerArr[i], 10);
            verArr[i] = parseInt(verArr[i], 10);

            if (orgVerArr[i] > verArr[i]) {
                return true;
            } else if (orgVerArr[i] < verArr[i]) {
                return false;
            }
        }
        if (i == orgVerArrLen) {
            return true;
        }

        if (orgVerArr.toString() == verArr.toString()) {
            return true;
        }
        return false;
    };

    this.checkVersion = function (ver, min, max) {
        var result = true;
        if (!npCommon.isBlank(min)) {
            result = result && npCommon.compareVersion(ver, min);
        }
        if (!npCommon.isBlank(max)) {
            result = result && npCommon.compareVersion(max, ver);
        }
        return result;
    };

    this.setCookie = function (name, value, expires, path, domain) {
        try {
            var cookie = name + "=" + escape(value) + ";";
            if (expires) {
                if (expires instanceof Date) {
                    if (isNaN(expires.getTime())) {
                        expires = new Date();
                    }
                } else {
                    expires = new Date(new Date().getTime() + parseInt(expires, 10) * 1000 * 60 * 60 * 24);
                }
                cookie += "expires=" + expires.toGMTString() + ";";
            }
            if (!!path) cookie += "path=" + path + ";";
            if (!!domain) cookie += "domain=" + domain + ";";
            document.cookie = cookie;
        } catch (e) {
        }
    };

    this.getCookie = function (name) {
        name = name + '=';
        var cookieData = document.cookie;
        var start = cookieData.indexOf(name);
        var value = '';
        if (start != -1) {
            start += name.length;
            var end = cookieData.indexOf(';', start);
            if (end == -1) end = cookieData.length;
            value = cookieData.substring(start, end);
        }
        return unescape(value);
    };

    this.show = function (obj) {
        if (this.isBlank(obj)) {
            return;
        }
        if (typeof (obj) !== "object") {
            obj = npCommon.findElement(obj);
        }
        try {
            npQuery(obj).show();
        } catch (e) {
        }
        ;
    };

    this.hide = function (obj) {
        if (this.isBlank(obj)) {
            return;
        }
        if (typeof (obj) !== "object") {
            obj = npCommon.findElement(obj);
        }
        try {
            npQuery(obj).hide();
        } catch (e) {
        }
        ;
    };

    this.val = function (element, value) {
        if (!npCommon.isNull(element) && typeof (element) == "object") {
            if (typeof (value) == "undefined") {
                return npQuery(element).val() || "";
            } else {
                npQuery(element).val(value);
            }
        }
    }

    this.readOnly = function (element, value) {
        if (!npCommon.isNull(element) && typeof (element) == "object") {
            if (typeof (value) == "undefined") {
                return npQuery(element).prop("readonly");
            } else {
                npQuery(element).prop("readonly", value);
            }
        }
    }

    /************************************************************
     * 이벤트 처리
     ***********************************************************/
    this.addEvent = function (eventName, target, func, args) {
        npQuery(target).bind(eventName, function (event) {
            func(args);
        });
    };

    this.removeEvent = function (eventName, target, func, args) {
        npQuery(target).unbind(eventName, function (event) {
            func(args);
        });
    };

    this.addLoadEvent = function (func, args) {
        //var oldonload = window.onload;
        var exec = function () {
            if (!npCommon.isNull(args)) {
                func(args);
            } else {
                func();
            }
        };
        //if(typeof oldonload == "function") { setTimeout(oldonload, 500); window.onload=null; }
        if (npCommon.isNull(npQuery)) {
            //console.log("add load event.....");
            setTimeout(exec, 500);
        } else {
            //console.log("add load event by jquery.....");
            npQuery(document).ready(function () {
                exec();
            });
        }
    };

    this.u8d = function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;
        while (i < utftext.length) {
            c = utftext.charCodeAt(i);
            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }
        }
        return string;
    }

    this.h2b = function (d) {
        var ht = "0123456789abcdef";
        var rs = new Array();
        var j = 0;
        for (var i = 0; i < d.length; i += 2) {
            var ch1 = ht.indexOf(d.charAt(i));
            var ch2 = ht.indexOf(d.charAt(i + 1));
            var b1 = (ch1 << 4) | ch2;
            rs[j++] = String.fromCharCode(b1);
        }
        return rs.join('');
    }

    this.dispatch = function (fn, args) {
        fn = (typeof (fn) == "function") ? fn : window[fn];
        return fn.apply(this, args || []);
    }

    /************************************************************
     * Elements의 위치얻기
     ***********************************************************/
    this.getBounds = function (obj, formname) {
        var obj = (typeof (obj) == "object") ? obj : npCommon.findElement(obj, formname);
        if (!npCommon.isNull(obj)) {
            var o = npQuery(obj);
//			npConsole.log("["+o.offset().left+", "+o.offset().top+", "+ o.outerWidth()+", "+ o.outerHeight()+"]");
            return {left: o.offset().left, top: o.offset().top, width: o.outerWidth(), height: o.outerHeight()}
        }
    };

    this.formatDate = function (date, format) {
        function zerofill(obj, length) {
            if (typeof (obj) == "string") {
                var s = "";
                var i = 0;
                while (i++ < length - obj.length) {
                    s += "0";
                }
                return s + obj
            } else if (typeof (obj) == "number") {
                return zerofill(obj.toString(), length);
            }
            return obj;
        }

        if (!date.valueOf()) return " ";

        var weekName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        var d = date;

        return format.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|ms|a\/p)/gi, function ($1) {
            switch ($1) {
                case "yyyy":
                    return d.getFullYear();
                case "yy":
                    return zerofill((d.getFullYear() % 1000), 2);
                case "MM":
                    return zerofill((d.getMonth() + 1), 2);
                case "dd":
                    return zerofill(d.getDate(), 2);
                case "E":
                    return weekName[d.getDay()];
                case "HH":
                    return zerofill(d.getHours(), 2);
                case "hh":
                    return zerofill(((h = d.getHours() % 12) ? h : 12), 2);
                case "mm":
                    return zerofill(d.getMinutes(), 2);
                case "ss":
                    return zerofill(d.getSeconds(), 2);
                case "ms":
                    return zerofill(d.getMilliseconds(), 3);
                case "a/p":
                    return d.getHours() < 12 ? "AM" : "PM";
                default:
                    return $1;
            }
        });
    };

    this.trim = function (str) {
        if (str == null) return str;
        try {
            return str.replace(/(^\s*)|(\s*$)/gi, "");
        } catch (e) {
            try {
                return str.replace(/^\s+|\s+$/g, "");
            } catch (e) {
            }
        }
        return str;
    };


    this.isShowMaxFailure = false;

    // 결과가 없거나 오류가 있을 경우의 재 호출
    var connectionFailCount = 0;
    this.parseResult = function (result, callback, tr) {
        function checkMaxFailure(job) {
            try {
                npConsole.log(new Error("Stack Trace").stack);
            } catch (e) {
            }
            if (!npCommon.isNull(job)) {
                job.print();
            }

            npConsole.log("ERROR COUNT : " + connectionFailCount + "");
            if (connectionFailCount >= npPfsPolicy.Common.MaxFailCount) {
                if (this.isShowMaxFailure == false) {
                    alert(npMessage.m10);
                    npConsole.log(npMessage.m10);
                    this.isShowMaxFailure = true;
                }
                if (!npCommon.isNull(job)) {
                    job.setState(npPfsConst.STATE_DONE);
                }
                npTransaction.terminate = true;
                npBaseCtrl.hideLoading();

                try {
                    location.reload();
                } catch (e) {
                }

                return true;
            }
            connectionFailCount++;
            return false;
        };

        if (npCommon.isBlank(result) || result == npPacket.RESULT_FALSE) {
            var res = (tr) ? checkMaxFailure(tr) : checkMaxFailure();
            if (res) return true;
            if (callback) {
                setTimeout(function () {
                    callback();
                }, npPfsPolicy.Common.WaitTimeout);
            }
            return true;
        } else if (result == npPacket.RESULT_DETECT_DEBUG) {
            npBaseCtrl.showDetectDebug();
            return true;
        }

        connectionFailCount = 0;
        return false;
    };


    // 연결유지 패킷의 정합성여부
    var keepAliveFailCount = 0;
    this.parseKeepAliveResult = function (result, callback) {
        function checkKeepAliveMaxFailure() {
            try {
                npConsole.log(new Error("Stack Trace").stack);
            } catch (e) {
            }

            npConsole.log("ERROR COUNT : " + keepAliveFailCount + "");
            if (keepAliveFailCount >= npPfsPolicy.Common.MaxFailCount) {
                if (this.isShowMaxFailure == false) {
//					alert(npMessage.m23);
                    npConsole.log(npMessage.m23);
                    this.isShowMaxFailure = true;
                }

                npTransaction.terminate = true;
                npBaseCtrl.hideLoading();

//				try { location.reload(); } catch(e) { }

                return true;
            }
            keepAliveFailCount++;
            return false;
        };

        if (npCommon.isBlank(result) || result == npPacket.RESULT_FALSE) {
            var res = checkKeepAliveMaxFailure();
            if (res) return true;
            if (callback) {
                setTimeout(function () {
                    callback();
                }, npPfsPolicy.Common.WaitTimeout);
            }
            return true;
        } else if (result == npPacket.RESULT_DETECT_DEBUG) {
            npBaseCtrl.showDetectDebug();
            return true;
        }

        keepAliveFailCount = 0;
        return false;
    };

    //난수 발급 2016.12.07 SJO
    this.randomTable = [];
    this.randomIndex = 0;
    this.random = function () {
        var maxIndex = npCommon.randomTable.length;
        var randomIndex = npCommon.randomIndex;
        var value = npCommon.randomTable[randomIndex];

        if (maxIndex == randomIndex + 1) {
            npCommon.randomIndex = 0;
        } else {
            npCommon.randomIndex++;
        }

        return value;
    };

    this.sha256 = sha256;

    function sha256(ascii) {
        function rightRotate(value, amount) {
            return (value >>> amount) | (value << (32 - amount));
        };

        var mathPow = Math.pow;
        var maxWord = mathPow(2, 32);
        var lengthProperty = 'length'
        var i, j; // Used as a counter across the whole file
        var result = ''

        var words = [];
        var asciiBitLength = ascii[lengthProperty] * 8;

        //* caching results is optional - remove/add slash from front of this line to toggle
        // Initial hash value: first 32 bits of the fractional parts of the square roots of the first 8 primes
        // (we actually calculate the first 64, but extra values are just ignored)
        var hash = sha256.h = sha256.h || [];
        // Round constants: first 32 bits of the fractional parts of the cube roots of the first 64 primes
        var k = sha256.k = sha256.k || [];
        var primeCounter = k[lengthProperty];
        /*/
		var hash = [], k = [];
		var primeCounter = 0;
		//*/

        var isComposite = {};
        for (var candidate = 2; primeCounter < 64; candidate++) {
            if (!isComposite[candidate]) {
                for (i = 0; i < 313; i += candidate) {
                    isComposite[i] = candidate;
                }
                hash[primeCounter] = (mathPow(candidate, .5) * maxWord) | 0;
                k[primeCounter++] = (mathPow(candidate, 1 / 3) * maxWord) | 0;
            }
        }

        ascii += '\x80' // Append Ƈ' bit (plus zero padding)
        while (ascii[lengthProperty] % 64 - 56) ascii += '\x00' // More zero padding
        for (i = 0; i < ascii[lengthProperty]; i++) {
            j = ascii.charCodeAt(i);
            if (j >> 8) return; // ASCII check: only accept characters in range 0-255
            words[i >> 2] |= j << ((3 - i) % 4) * 8;
        }
        words[words[lengthProperty]] = ((asciiBitLength / maxWord) | 0);
        words[words[lengthProperty]] = (asciiBitLength)

        // process each chunk
        for (j = 0; j < words[lengthProperty];) {
            var w = words.slice(j, j += 16); // The message is expanded into 64 words as part of the iteration
            var oldHash = hash;
            // This is now the undefinedworking hash", often labelled as variables a...g
            // (we have to truncate as well, otherwise extra entries at the end accumulate
            hash = hash.slice(0, 8);

            for (i = 0; i < 64; i++) {
                var i2 = i + j;
                // Expand the message into 64 words
                // Used below if
                var w15 = w[i - 15], w2 = w[i - 2];

                // Iterate
                var a = hash[0], e = hash[4];
                var temp1 = hash[7]
                    + (rightRotate(e, 6) ^ rightRotate(e, 11) ^ rightRotate(e, 25)) // S1
                    + ((e & hash[5]) ^ ((~e) & hash[6])) // ch
                    + k[i]
                    // Expand the message schedule if needed
                    + (w[i] = (i < 16) ? w[i] : (
                            w[i - 16]
                            + (rightRotate(w15, 7) ^ rightRotate(w15, 18) ^ (w15 >>> 3)) // s0
                            + w[i - 7]
                            + (rightRotate(w2, 17) ^ rightRotate(w2, 19) ^ (w2 >>> 10)) // s1
                        ) | 0
                    );
                // This is only used once, so *could* be moved below, but it only saves 4 bytes and makes things unreadble
                var temp2 = (rightRotate(a, 2) ^ rightRotate(a, 13) ^ rightRotate(a, 22)) // S0
                    + ((a & hash[1]) ^ (a & hash[2]) ^ (hash[1] & hash[2])); // maj

                hash = [(temp1 + temp2) | 0].concat(hash); // We don't bother trimming off the extra ones, they're harmless as long as we're truncating when we do the slice()
                hash[4] = (hash[4] + temp1) | 0;
            }

            for (i = 0; i < 8; i++) {
                hash[i] = (hash[i] + oldHash[i]) | 0;
            }
        }

        for (i = 0; i < 8; i++) {
            for (j = 3; j + 1; j--) {
                var b = (hash[i] >> (j * 8)) & 255;
                result += ((b < 16) ? 0 : '') + b.toString(16);
            }
        }
        return result;
    };


};


w.npBaseCtrl = new function () {
    this.uuid = null;
    this.currentPort = -1;
    this.foundPort = false;
    this.enckey = null;
    this.terminate = false;

    var isRequiredReady = false;			// 모듈 업데이트 대기가 필요한 지 여부 확인
    var isRequiredCheckUpdate = false;
    var isRequiredReinstall = false;		// 모듈 재설치가 필요한 경우

    this.Options = {FW: true, SK: true, FD: true, KV: true};

    function parseOptions(options) {
        var o = {
            Firewall: true,
            SecureKey: true,
            Fds: true,
            Keypad: true,
            AutoStartup: true,
            Submit: true,
            Device: true,
            Debug: false,
            Form: null,
            AutoScanAttrName: "npkencrypt",
            AutoScanAttrValue: "on",
            MoveToInstall: function (url, isUpdate) {
                location.replace(url);
            },
            Loading: {
                Default: true,
                Before: function () {
                    npBaseCtrl.showDefaultLoading();
                },
                After: function () {
                    npBaseCtrl.hideDefaultLoading();
                }
            }
        };

        npQuery.extend(o, options);
        npBaseCtrl.Options = {
            FW: o.Firewall && npPfsPolicy.License.FW
            , SK: o.SecureKey && npPfsPolicy.License.SK
            , FD: o.Fds && npPfsPolicy.License.FD
            , KV: o.Keypad && npPfsPolicy.License.KV
            , SS: o.Submit
            , DV: o.Device
            , PA: o.PinAuth
            , AS: o.AutoStartup
            , FM: o.Form
            , LD: {
                DF: o.Loading.Default
                , BF: o.Loading.Before
                , AF: o.Loading.After
            }
            , AN: o.AutoScanAttrName
            , AV: (npCommon.isNull(o.AutoScanAttrValue) ? "" : o.AutoScanAttrValue.toLowerCase())
            , MI: o.MoveToInstall
        };

        if (o.Debug == true) {
            npPfsPolicy.Common.RuntimeMode = npPfsConst.MODE_DEBUG;
        } else {
            npPfsPolicy.Common.RuntimeMode = npPfsConst.MODE_RELEASE;
        }
    }

    this.focusElement = null;


    this.eventBinded = false;


    this.isStarting = false;

    this.functionQueue = [];
    this.functionExecute = function () {
        var func = npBaseCtrl.functionQueue.shift();
        if (typeof (func) == "function") {
            func();
        }
    };

    this.init = function (o) {
        npPfsCtrl.isStarting = true;
        npPfsCtrl.terminate = false;
        parseOptions(o);


        // 디버그모드에서 이벤트 모니터링 기능 추가  by YGKIM 2016.05.12
        if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
            var oldEventTrigger = npQuery.event.trigger;
            npQuery.event.trigger = function (event, data, elem, onlyHandlers) {
                if (!npCommon.isNull(event) && !npCommon.isBlank(event.type) && event.type.indexOf("nppfs") == 0) {
                    npConsole.log(event.message);
                }
                oldEventTrigger(event, data, elem, onlyHandlers);
            }
        }

        npConsole.reset();

        if (npPfsCtrl.functionQueue.length == 0) {
            npQuery(document).trigger({
                type: "nppfs-before-init",
                message: "Start the initialization of the NOS.",
                time: new Date()
            });
        }

        npConsole.check("NOS 초기화 작업 시작");

        npBaseCtrl.showLoading();

        // 페이지 작업 고유번호 생성
        if (npCommon.isBlank(npBaseCtrl.uuid)) {
            npBaseCtrl.uuid = npCommon.makeUuid();
            npConsole.log("UID : " + npBaseCtrl.uuid);
        }


        var ae = null;
        try {
            ae = document.activeElement;

            if (ae.tagName.toLowerCase() === "input" && !npCommon.isNull(ae.form) && !npCommon.isNull(npQuery(ae).attr("name"))) {

                this.focusElement = ae;
                npConsole.log(npMessage.m25.replace("%p%", npQuery(ae).attr("name")));
                ae.blur();
            }
        } catch (e) {
        }
        npConsole.check("NOS 포커스된 입력양식 찾기 완료");


        if (npBaseCtrl.eventBinded == false) {

            npQuery(document).unbind("keydown mousedown unload beforeunload");
            // 개발자 도구 막기
            npQuery(document).bind("keydown", function (event) {
                var e = (event || window.event);
                if (npCommon.isNull(e)) {
                    return;
                }

                var k = e.keyCode;
                var a = e.altKey;
                var c = e.ctrlKey;
                var s = e.shiftKey;
                var m = e.metaKey;
                //npConsole.log("ctrl : " + c + ", shift : " + s + ", alt : " + a + ", meta : " + m + ", keycode : " + k);
                var blockEvent = false;
                if (npDefine.win || npDefine.lnx) {
                    blockEvent = (k == 123) || (c && s && k == 73);/* F12, Ctrl+Shift+i */
                    if (npDefine.ff) {
                        blockEvent = blockEvent || (c && s && (k == 75 || k == 81 || k == 83));		/* Ctrl+Shift+k, Ctrl+Shift+q, Ctrl+Shift+s */
                        blockEvent = blockEvent || (s && (k == 113 || k == 116 || k == 118));		/* Shift+F2, Shift+F5, Shift+F7 */
                    }
                } else if (npDefine.mac) {
                    blockEvent = (a && m && (k == 73));/* Command+Alt+i */
                    if (npDefine.ff) {
                        blockEvent = blockEvent || (a && m && (k == 75 || k == 81 || k == 83));		/* Command+Shift+k, Command+Shift+q, Command+Shift+s */
                        blockEvent = blockEvent || (s && (k == 113 || k == 116 || k == 118));		/* Shift+F2, Shift+F5, Shift+F7 */
                    }
                }

                if (blockEvent == true) {
                    npConsole.log(npMessage.m38);
                    npCommon.stopEvent(e);
                    return false;
                }


                npKCtrl.stopTab(e);


            });

            npQuery(document).bind("mousedown", function (event) {
                var e = (event || window.event);
                if ((e.button == 2) || (e.button == 3)) {
                    npConsole.log(npMessage.m39);
                    npCommon.stopEvent(e);
                    return false;
                }
            });
            npConsole.check("NOS 단축키 차단");


            try {
                function finalize(event) {
                    event = (event || window.event);
                    try {		// 페이지 종료시 오류발생 가능
                        // 현대카드 UI확장기능(라온의 attribute를 NOS attribute로 변경) by YGKIM 2015.09.10
                        if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.beforeFinalize) == "function") {
                            var ret = npPfsExtension.beforeFinalize(event);
                            if (!npCommon.isNull(ret)) {
                                return ret;
                            }
                        }

                        npPfsModules.finalize();
                        if (npPfsModules.isRequireHandshake() == true) {
                            stopAlive();
                        }
                    } catch (e) {
                    }
                }

                if (typeof (window.onbeforeunload) != "undefined") {
                    npQuery(window).bind("beforeunload", finalize);
                } else {
                    npQuery(window).bind("unload", finalize);
                }
            } catch (e) {
                npConsole.log(e);
            }
            npConsole.check("NOS 종료 이벤트 추가");
            npBaseCtrl.eventBinded = true;
        }


        // 이벤트 처리 추가
        npQuery(document).unbind("nppfs-nos-jlk nppfs-nos-jhs nppfs-nos-jvc nppfs-nos-init nppfs-nos-startup");
        npQuery(document).bind("nppfs-nos-jlk nppfs-nos-jhs nppfs-nos-jvc nppfs-nos-init nppfs-nos-startup", eventHandler);


        if (npPfsModules.isRequireE2E() == true && npCommon.isBlank(npBaseCtrl.enckey)) {
            var url = npPfsPolicy.Common.KeyUrl;
            npConsole.log(url);
            var key = npCommon.send(url, "id=" + npBaseCtrl.uuid, {
                async: false,
                callback: function (xhr) {
                    if (xhr.readyState == 4) {
                        if (xhr.status == 200) {
                            var key = xhr.responseText;
                            if (npCommon.isBlank(key)) {
                                npConsole.log(npMessage.m04);
                            }
                            npBaseCtrl.enckey = npCommon.trim(key);
                        } else {
                            npConsole.log(npMessage.m04);
                        }
                        npQuery(document).trigger({type: "nppfs-nos-jlk", time: new Date()});
                    }
                }
            });
            npConsole.check("NOS E2E 초기화 완료");
        } else {
            npQuery(document).trigger({type: "nppfs-nos-jlk", time: new Date()});
        }


        if ((npPfsModules.isRequireHandshake() || npPfsModules.isRequireVM()) && (npCommon.isBlank(this.currentPort) || this.currentPort <= 0)) {
            npBaseCtrl.findPort(function () {
                if (isRequiredReinstall == true || npBaseCtrl.foundPort == false) {
                    if (npBaseCtrl.terminate == true) {
                        return;
                    }

                    npBaseCtrl.terminate = true;
                    npConsole.log(isRequiredReinstall ? npMessage.m01 : npMessage.m02);

                    if (typeof (npBaseCtrl.Options.MI) == "function") {
                        npBaseCtrl.Options.MI(npPfsPolicy.Common.InstallUrl, isRequiredReinstall, false);
                    }

                    npBaseCtrl.hideLoading();
                    return;
                }
            });
        } else {
            npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
        }
    };


    function eventHandler(event) {
        npQuery(document).unbind(event);

        switch (event.type) {
            case "nppfs-nos-jlk" :
                npConsole.check("NOS 키교환 완료");


                npPfsModules.init({
                    "form": npBaseCtrl.Options.FM
                });
                break;

            case "nppfs-nos-jhs" :

                npConsole.check("NOS 핸드쉐이크 완료");


                if (npPfsModules.isRequireHandshake() == true) {
                    startAlive();
                }


                npBaseCtrl.isVirtualMachine(function (result) {
                    npQuery(document).trigger({type: "nppfs-nos-jvc", time: new Date()});
                    npConsole.check("NOS 가상머신확인 완료");
                });
                break;

            case "nppfs-nos-jvc" :

                // Auto Start...
                if (npBaseCtrl.Options.AS == true) {
                    npBaseCtrl.startup();
                } else {
                    npBaseCtrl.hideLoading();
                }

                npQuery(document).trigger({type: "nppfs-nos-init", time: new Date()});
                break;

            case "nppfs-nos-init" :
                if (npPfsCtrl.functionQueue.length == 0) {
                    npQuery(document).trigger({
                        type: "nppfs-after-init"
                        , message: "Initialization of NOS has been successfully carried out."
                        , time: new Date()
                    });
                }
                npConsole.check("NOS 초기화 작업 종료");
                break;

            case "nppfs-nos-startup" :
                if (npPfsCtrl.functionQueue.length == 0) {
                    npBaseCtrl.hideLoading();
                    npQuery(document).trigger({
                        type: "nppfs-after-startup",
                        message: "NOS was driving successfully.",
                        time: new Date()
                    });
                }

                // 현대해상 NOS시작 후 콜백 by YGKIM 2017.02.13
                if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.startupCallback) == "function") {
                    npPfsExtension.startupCallback();
                }

                npConsole.check("NOS 모듈구동 작업 종료");
                npConsole.printTimeline();

                npPfsCtrl.isStarting = false;

                break;
        }
    }


    this.isStartup = false;
    this.startup = function (theform) {
        npConsole.check("NOS 모듈구동 작업 시작");

        if (npPfsCtrl.functionQueue.length == 0) {
            npQuery(document).trigger({
                type: "nppfs-before-startup",
                message: "Start driving the NOS.",
                time: new Date()
            });
        }

        this.verifyFormName();

        npConsole.check("NOS 폼이름 점검 종료");

        npBaseCtrl.isStartup = true;
        npPfsModules.startup();
    }


    var isAlreadyCheckVirtualMachine = false;
    this.resetVirtualMachine = function () {
        isAlreadyCheckVirtualMachine = false;
    };
    this.isVirtualMachine = function (callback) {
        callback = callback || function () {
        };

        if (isAlreadyCheckVirtualMachine == true) {
            callback(npDefine.virtualMachine);
            return;
        }

        if (npDefine.isMobileDevice() || npDefine.isMetroUi()) {
            npDefine.virtualMachine = false;
            npConsole.log("Can not be checked a virtual machine at Metro UI or Mobile.");
            callback(false);
            return;
        }

        if (!npPfsModules.isRequireHandshake()) {
            isAlreadyCheckVirtualMachine = true;
            npDefine.virtualMachine = false;
            if (!npCommon.isNull(callback) && typeof (callback) == "function") {
                callback(npDefine.virtualMachine);
            }
            return;
        }

        if (npBaseCtrl.foundPort == false) {
            callback(false);
            return;
        }


        var command = npBaseCtrl.makeCommand(npPacket.PRODUCT_DM, npPacket.MODE_SYNC, npPacket.CMD_CHECK_VM, null);
        npCommon.sendCommand(command, {
            callback: function (result) {
                // 결과가 없거나 오류가 있을 경우의 재 호출
                if (npCommon.isBlank(result)) {
                    setTimeout(function () {
                        npBaseCtrl.isVirtualMachine(callback);
                    }, npPfsPolicy.Common.WaitTimeout);
                    return;
                } else if (result == npPacket.RESULT_TRUE) {		// 가상머신
                    npBaseCtrl.Options.SK = false;			// 키보드보안 비활성화
                    npConsole.log(npMessage.m48);
                    npDefine.virtualMachine = true;
                } else if (result == npPacket.RESULT_FALSE) {
                    npConsole.log(npMessage.m49);
                    npDefine.virtualMachine = false;
                } else if (result == npPacket.RESULT_DETECT_DEBUG) {
                    npBaseCtrl.showDetectDebug();

                } else {
                    npDefine.virtualMachine = false;
                }

                isAlreadyCheckVirtualMachine = true;
                if (!npCommon.isNull(callback) && typeof (callback) == "function") {
                    callback(npDefine.virtualMachine);
                }
            }
        });


//		this.linuxOsType;
//		this.linuxOsVersion;
//		if(npDefine.lnx){
//			command = npBaseCtrl.makeCommand(npPacket.PRODUCT_DM, npPacket.MODE_SYNC, npPacket.CMD_GET_OS_VERSION, null);
//			npCommon.sendCommand(command, {
//				callback : function(result) {
//					// 결과가 없거나 오류가 있을 경우의 재 호출
//					if(npCommon.isBlank(result)) {
//						setTimeout(function(){
//							npBaseCtrl.isVirtualMachine(callback);
//						}, npPfsPolicy.Common.WaitTimeout);
//						return;
//					} else if(result == npPacket.RESULT_DETECT_DEBUG) {
//						npBaseCtrl.showDetectDebug();

//					} else if(result == npPacket.RESULT_PROXY) {
//						npBaseCtrl.showDetectProxy();
//					} else if(result == npPacket.RESULT_USER_PROXY){
//						npBaseCtrl.showDetectProxy("u");

//					} else {
//						var osInfo = result.split("|");
//						var os = osInfo[0];
//						var osType = osInfo[1];
//						var osVersion = osInfo[2];

//						if(os != npPfsPolicy.Os.LINUX.CODE ) {
//							alert("리눅스가 아닙니다. 현재 리눅스만 지원하고 있습니다. ");
//							return;
//						}

//						npPfsPolicy.Os.LINUX.Fedora
//						if (npCommon.arrayIn([npPfsPolicy.Os.LINUX.TYPE.Fedora, npPfsPolicy.Os.LINUX.TYPE.Ubuntu, npPfsPolicy.Os.LINUX.TYPE.CentOS, npPfsPolicy.Os.LINUX.TYPE.OpenSUSE, npPfsPolicy.Os.LINUX.TYPE.OTHER], osType)){
//							this.linuxOsType = osType;
//							this.linuxOsVersion = npCommon.n2b(osVersion, "");
//						}
//					}
//				}
//			});
//		}

    };

    this.waitSubmit = function (callback) {

        // npPfsStartup을 호출하지 않은 경우 확인(이전 스크립트에서는 없을 것임. 기본 true)
        if (typeof (this.isStartup) == "undefined") {
            this.isStartup = true;
        }


        if (this.isStartup == true && npBaseCtrl.Options.FD == true && npFCtrl.isRunnable()) {

            var timeoutid = setTimeout(function () {
                callback();
            }, 30000);

            function wwait() {

                if (npFCtrl.isFinish() == true) {

                    callback();
                    clearTimeout(timeoutid);
                } else {
                    setTimeout(wwait, npPfsPolicy.Common.WaitTimeout);
                }
            }

            wwait();
        } else {
            callback();
        }

    };

    this.showDetectDebug = function () {

        if (npDefine.sf == true) {
            alert("보안프로그램에서 개발자도구나 디버그도구를 탐지하였습니다.\n보안을 위하여 개발자도구를 종료합니다.");
            return;
        } else {
            alert(npMessage.m09);
            location.reload();
        }
    };

    this.copy = function (asis, tobe) {
        npCommon.copyDivision(asis, tobe);
    };

    this.showDefaultLoading = function () {
        var layer = npCommon.findDivision(document, "byid", "nppfs-loading-modal");
        if (npCommon.isNull(layer)) {
            return;
        }

        try {
            npQuery(layer).css({
                "display": "block"
                ,
                "position": "fixed"
                ,
                "z-index": "10000"
                ,
                "top": "0"
                ,
                "left": "0"
                ,
                "height": "100%"
                ,
                "width": "100%"

                // clock
                ,
                "background": "rgba( 255, 255, 255, .7) url(data:image/gif;base64,R0lGODlhIAAgAPMAAP///wAAAMbGxoSEhLa2tpqamjY2NlZWVtjY2OTk5Ly8vB4eHgQEBAAAAAAAAAAAACH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAIAAgAAAE5xDISWlhperN52JLhSSdRgwVo1ICQZRUsiwHpTJT4iowNS8vyW2icCF6k8HMMBkCEDskxTBDAZwuAkkqIfxIQyhBQBFvAQSDITM5VDW6XNE4KagNh6Bgwe60smQUB3d4Rz1ZBApnFASDd0hihh12BkE9kjAJVlycXIg7CQIFA6SlnJ87paqbSKiKoqusnbMdmDC2tXQlkUhziYtyWTxIfy6BE8WJt5YJvpJivxNaGmLHT0VnOgSYf0dZXS7APdpB309RnHOG5gDqXGLDaC457D1zZ/V/nmOM82XiHRLYKhKP1oZmADdEAAAh+QQJCgAAACwAAAAAIAAgAAAE6hDISWlZpOrNp1lGNRSdRpDUolIGw5RUYhhHukqFu8DsrEyqnWThGvAmhVlteBvojpTDDBUEIFwMFBRAmBkSgOrBFZogCASwBDEY/CZSg7GSE0gSCjQBMVG023xWBhklAnoEdhQEfyNqMIcKjhRsjEdnezB+A4k8gTwJhFuiW4dokXiloUepBAp5qaKpp6+Ho7aWW54wl7obvEe0kRuoplCGepwSx2jJvqHEmGt6whJpGpfJCHmOoNHKaHx61WiSR92E4lbFoq+B6QDtuetcaBPnW6+O7wDHpIiK9SaVK5GgV543tzjgGcghAgAh+QQJCgAAACwAAAAAIAAgAAAE7hDISSkxpOrN5zFHNWRdhSiVoVLHspRUMoyUakyEe8PTPCATW9A14E0UvuAKMNAZKYUZCiBMuBakSQKG8G2FzUWox2AUtAQFcBKlVQoLgQReZhQlCIJesQXI5B0CBnUMOxMCenoCfTCEWBsJColTMANldx15BGs8B5wlCZ9Po6OJkwmRpnqkqnuSrayqfKmqpLajoiW5HJq7FL1Gr2mMMcKUMIiJgIemy7xZtJsTmsM4xHiKv5KMCXqfyUCJEonXPN2rAOIAmsfB3uPoAK++G+w48edZPK+M6hLJpQg484enXIdQFSS1u6UhksENEQAAIfkECQoAAAAsAAAAACAAIAAABOcQyEmpGKLqzWcZRVUQnZYg1aBSh2GUVEIQ2aQOE+G+cD4ntpWkZQj1JIiZIogDFFyHI0UxQwFugMSOFIPJftfVAEoZLBbcLEFhlQiqGp1Vd140AUklUN3eCA51C1EWMzMCezCBBmkxVIVHBWd3HHl9JQOIJSdSnJ0TDKChCwUJjoWMPaGqDKannasMo6WnM562R5YluZRwur0wpgqZE7NKUm+FNRPIhjBJxKZteWuIBMN4zRMIVIhffcgojwCF117i4nlLnY5ztRLsnOk+aV+oJY7V7m76PdkS4trKcdg0Zc0tTcKkRAAAIfkECQoAAAAsAAAAACAAIAAABO4QyEkpKqjqzScpRaVkXZWQEximw1BSCUEIlDohrft6cpKCk5xid5MNJTaAIkekKGQkWyKHkvhKsR7ARmitkAYDYRIbUQRQjWBwJRzChi9CRlBcY1UN4g0/VNB0AlcvcAYHRyZPdEQFYV8ccwR5HWxEJ02YmRMLnJ1xCYp0Y5idpQuhopmmC2KgojKasUQDk5BNAwwMOh2RtRq5uQuPZKGIJQIGwAwGf6I0JXMpC8C7kXWDBINFMxS4DKMAWVWAGYsAdNqW5uaRxkSKJOZKaU3tPOBZ4DuK2LATgJhkPJMgTwKCdFjyPHEnKxFCDhEAACH5BAkKAAAALAAAAAAgACAAAATzEMhJaVKp6s2nIkolIJ2WkBShpkVRWqqQrhLSEu9MZJKK9y1ZrqYK9WiClmvoUaF8gIQSNeF1Er4MNFn4SRSDARWroAIETg1iVwuHjYB1kYc1mwruwXKC9gmsJXliGxc+XiUCby9ydh1sOSdMkpMTBpaXBzsfhoc5l58Gm5yToAaZhaOUqjkDgCWNHAULCwOLaTmzswadEqggQwgHuQsHIoZCHQMMQgQGubVEcxOPFAcMDAYUA85eWARmfSRQCdcMe0zeP1AAygwLlJtPNAAL19DARdPzBOWSm1brJBi45soRAWQAAkrQIykShQ9wVhHCwCQCACH5BAkKAAAALAAAAAAgACAAAATrEMhJaVKp6s2nIkqFZF2VIBWhUsJaTokqUCoBq+E71SRQeyqUToLA7VxF0JDyIQh/MVVPMt1ECZlfcjZJ9mIKoaTl1MRIl5o4CUKXOwmyrCInCKqcWtvadL2SYhyASyNDJ0uIiRMDjI0Fd30/iI2UA5GSS5UDj2l6NoqgOgN4gksEBgYFf0FDqKgHnyZ9OX8HrgYHdHpcHQULXAS2qKpENRg7eAMLC7kTBaixUYFkKAzWAAnLC7FLVxLWDBLKCwaKTULgEwbLA4hJtOkSBNqITT3xEgfLpBtzE/jiuL04RGEBgwWhShRgQExHBAAh+QQJCgAAACwAAAAAIAAgAAAE7xDISWlSqerNpyJKhWRdlSAVoVLCWk6JKlAqAavhO9UkUHsqlE6CwO1cRdCQ8iEIfzFVTzLdRAmZX3I2SfZiCqGk5dTESJeaOAlClzsJsqwiJwiqnFrb2nS9kmIcgEsjQydLiIlHehhpejaIjzh9eomSjZR+ipslWIRLAgMDOR2DOqKogTB9pCUJBagDBXR6XB0EBkIIsaRsGGMMAxoDBgYHTKJiUYEGDAzHC9EACcUGkIgFzgwZ0QsSBcXHiQvOwgDdEwfFs0sDzt4S6BK4xYjkDOzn0unFeBzOBijIm1Dgmg5YFQwsCMjp1oJ8LyIAACH5BAkKAAAALAAAAAAgACAAAATwEMhJaVKp6s2nIkqFZF2VIBWhUsJaTokqUCoBq+E71SRQeyqUToLA7VxF0JDyIQh/MVVPMt1ECZlfcjZJ9mIKoaTl1MRIl5o4CUKXOwmyrCInCKqcWtvadL2SYhyASyNDJ0uIiUd6GGl6NoiPOH16iZKNlH6KmyWFOggHhEEvAwwMA0N9GBsEC6amhnVcEwavDAazGwIDaH1ipaYLBUTCGgQDA8NdHz0FpqgTBwsLqAbWAAnIA4FWKdMLGdYGEgraigbT0OITBcg5QwPT4xLrROZL6AuQAPUS7bxLpoWidY0JtxLHKhwwMJBTHgPKdEQAACH5BAkKAAAALAAAAAAgACAAAATrEMhJaVKp6s2nIkqFZF2VIBWhUsJaTokqUCoBq+E71SRQeyqUToLA7VxF0JDyIQh/MVVPMt1ECZlfcjZJ9mIKoaTl1MRIl5o4CUKXOwmyrCInCKqcWtvadL2SYhyASyNDJ0uIiUd6GAULDJCRiXo1CpGXDJOUjY+Yip9DhToJA4RBLwMLCwVDfRgbBAaqqoZ1XBMHswsHtxtFaH1iqaoGNgAIxRpbFAgfPQSqpbgGBqUD1wBXeCYp1AYZ19JJOYgH1KwA4UBvQwXUBxPqVD9L3sbp2BNk2xvvFPJd+MFCN6HAAIKgNggY0KtEBAAh+QQJCgAAACwAAAAAIAAgAAAE6BDISWlSqerNpyJKhWRdlSAVoVLCWk6JKlAqAavhO9UkUHsqlE6CwO1cRdCQ8iEIfzFVTzLdRAmZX3I2SfYIDMaAFdTESJeaEDAIMxYFqrOUaNW4E4ObYcCXaiBVEgULe0NJaxxtYksjh2NLkZISgDgJhHthkpU4mW6blRiYmZOlh4JWkDqILwUGBnE6TYEbCgevr0N1gH4At7gHiRpFaLNrrq8HNgAJA70AWxQIH1+vsYMDAzZQPC9VCNkDWUhGkuE5PxJNwiUK4UfLzOlD4WvzAHaoG9nxPi5d+jYUqfAhhykOFwJWiAAAIfkECQoAAAAsAAAAACAAIAAABPAQyElpUqnqzaciSoVkXVUMFaFSwlpOCcMYlErAavhOMnNLNo8KsZsMZItJEIDIFSkLGQoQTNhIsFehRww2CQLKF0tYGKYSg+ygsZIuNqJksKgbfgIGepNo2cIUB3V1B3IvNiBYNQaDSTtfhhx0CwVPI0UJe0+bm4g5VgcGoqOcnjmjqDSdnhgEoamcsZuXO1aWQy8KAwOAuTYYGwi7w5h+Kr0SJ8MFihpNbx+4Erq7BYBuzsdiH1jCAzoSfl0rVirNbRXlBBlLX+BP0XJLAPGzTkAuAOqb0WT5AH7OcdCm5B8TgRwSRKIHQtaLCwg1RAAAOwAAAAAAAAAAAA==) 50% 50% no-repeat"
                ,
                "opacity": "0.7"

                // for IE7, IE8
                ,
                "backgroundColor": "#ffffff"
                ,
                "filter": "alpha(opacity=70)"
            });
        } catch (e) {
        }
        showLoadingCount = 0;
    };

    this.hideDefaultLoading = function () {
        var layer = npCommon.findDivision(document, "byid", "nppfs-loading-modal");
        if (npCommon.isNull(layer)) {
            return;
        }

        npQuery(layer).css({
            "display": "none"
            , "width": "0px"
            , "height": "0px"
        });

        //document.body.style.overflow = "auto";
        //document.body.style.overflow = this.oldOverflow;
        hideLoadingCount = 0;
    };

    var showLoadingCount = 0;
    this.showLoading = function () {
        if (npCommon.isNull(npBaseCtrl.Options) || npCommon.isNull(npBaseCtrl.Options.LD) || npCommon.isNull(npBaseCtrl.Options.LD.DF) || npBaseCtrl.Options.LD.DF == true) {
            this.showDefaultLoading();
        } else if (!npCommon.isNull(npBaseCtrl.Options.LD.BF) && typeof (npBaseCtrl.Options.LD.BF) == "function") {
            if (showLoadingCount > 0) {
                this.showDefaultLoading();
            } else {
                showLoadingCount++;
                npBaseCtrl.Options.LD.BF();
            }
        } else {
            this.showDefaultLoading();
        }
    };

    var hideLoadingCount = 0;
    this.hideLoading = function () {
        if (npCommon.isNull(npBaseCtrl.Options) || npCommon.isNull(npBaseCtrl.Options.LD) || npCommon.isNull(npBaseCtrl.Options.LD.DF) || npBaseCtrl.Options.LD.DF == true) {
            this.hideDefaultLoading();
        } else if (!npCommon.isNull(npBaseCtrl.Options.LD.AF) && typeof (npBaseCtrl.Options.LD.AF) == "function") {
            if (hideLoadingCount > 0) {
                this.hideDefaultLoading();
            } else {
                hideLoadingCount++;
                npBaseCtrl.Options.LD.AF();
            }
        } else {
            this.hideDefaultLoading();
        }
    };


    this.makeUrl = function (port, host) {

        var ret = [];
        ret.push(npPfsPolicy.Common.Protocol);
        ret.push("://");
        ret.push(host);
        ret.push(((npPfsPolicy.Common.Protocol == "http" && port == 80) || (npPfsPolicy.Common.Protocol == "https" && port == 443)) ? "" : ":" + port);
        ret.push(npPfsPolicy.Common.ContextPath);
        return ret.join("");
    };


    this.makeHeader = function (pcode, sync, headers) {
        if (npCommon.isNull(headers) || typeof (headers) != "array") {
            var random = npCommon.getRandomBytes(16);
            headers = new Array(4);
            headers[0] = "";
//			headers[0] = document.domain;
            headers[1] = npPacket.CUSTOMER_ID;
            headers[2] = npCommon.hexEncode(random) + npCommon.encrypt(document.domain, npCommon.hexDecode(npCommon.hexEncode(random)), "ECB", 128);
            headers[3] = "1000";

//			headers = new Array(2);
//			headers[0] = document.domain;
//			headers[1] = npPacket.CUSTOMER_ID;
        }

        var headerCount = headers.length;

        var command = [];
        command.push(pcode);
        if (npCommon.isBlank(sync)) {
            command.push("1");
        } else {
            command.push(sync);
        }
        command.push(headerCount);
        for (var idx = 0; idx < headerCount; idx++) {
            command.push(npCommon.makeLength(headers[idx]));
            command.push(headers[idx]);
        }

        return command;
    };

    this.makeCommand = function (pcode, sync, ccode, parameter) {
        var command = npBaseCtrl.makeHeader(pcode, sync);

        if (!npCommon.isBlank(ccode)) {
            command.push(ccode);
        }

        if (!npCommon.isBlank(parameter)) {
            command.push(npCommon.makeLength(parameter));
            command.push(parameter);
        }

        return command.join("");
    };


    var isCheckedCookie = false;
    var isCheckedDefault = false;
    var isCheckedAll = false;
    var remainTask = [];
    var taskRunning = false;

    var funcarray = [];
    var functionpointer = function () {
        for (var i = 0; i < funcarray.length; i++) {
            if (typeof (funcarray[i]) == "function") {
                npCommon.dispatch(funcarray[i]);
            }
        }
        funcarray = [];
    };

    this.findPort = function (callback) {
        remainTask = [];

        // 남은 작업들이 있다면 바로 종료..
        if (taskRunning == true) {
            if (typeof (callback) == "function") funcarray.push(callback);
            return;
        }
        taskRunning = true;

        if (typeof (callback) == "function") funcarray.push(callback);

        npBaseCtrl.foundPort = false;

        // 접속가능여부 확인
        if (isCheckedDefault == false) {

            var cookieHost = npCommon.getCookie("npPfsHost");
            var cookiePort = npCommon.getCookie("npPfsPort");


//			cookieHost = "";
//			cookiePort = -1;
            if (!npCommon.isBlank(cookieHost) && !npCommon.isBlank(cookiePort) && cookiePort > 0 && isCheckedCookie == false) {
                // 쿠키값 체크
                npConsole.log(npMessage.m13.replace("%h%", cookieHost).replace("%p%", cookiePort));
                handshake(cookieHost, cookiePort, functionpointer);
                isCheckedCookie = true;
            } else {
                // 기본값 체크

                handshake("127.0.0.1", npPfsPolicy.Common.LocalPort, functionpointer);
                isCheckedCookie = true;
                isCheckedDefault = true;
            }
        } else {
            // 전체값 체크
            for (var index = 0; index < npPfsPolicy.Common.Range; index++) {

                handshake("127.0.0.1", npPfsPolicy.Common.LocalPort + index, functionpointer);
            }
            isCheckedAll = true;
        }


        function handshake(host, port, callback) {
            if (npBaseCtrl.terminate == true) {
                return;
            }
            var taskName = "task_" + host.split(".").join("_") + "_" + port;
            if (npCommon.indexOf(remainTask, taskName) < 0) {
                remainTask.push(taskName);
            }


            function docommand(o) {
                // 핸드쉐이크 명령 생성
                var paramCount = "1";			// 파라메터개수
                var random = npCommon.getRandomBytes(16);
                var hp = npCommon.hexEncode(random) + npCommon.encrypt(nua, npCommon.hexDecode(npCommon.hexEncode(random)), "ECB", 128);
                var body = paramCount + npCommon.makeLength(hp) + hp;
                var command = npBaseCtrl.makeCommand(npPacket.PRODUCT_DM, npPacket.MODE_SYNC, npPacket.CMD_HANDSHAKE, body);
                //npConsole.check("NOS 핸드쉐이크 - 명령송신 완료");

                npCommon.sendCommand(command, o, function (xhr) {
                    if (xhr.readyState == 4) {
                        //npConsole.check("NOS 핸드쉐이크 - 명령수신 완료");

                        var result = "";
                        if (xhr.status == 200) {
                            result = xhr.responseText;
                        }

                        o.callback(result, o.host, o.port);
                    }
                });
            };

            (function (options) {
                try {
                    npQuery.extend(options, {
                        host: host
                        , port: port
                        , timeout: 3 * 1000
                    });

                    var url = npBaseCtrl.makeUrl(port, host);

                    // 사파리에서는 이미지 이벤트가 정상적으로 오지 않아서 예외처리
                    if (npDefine.sf == true || npDefine.isNewIe()) {
                        docommand(options);
                        return;
                    }

                    var imagepath = url + "/?code=" + npPacket.CMD_HEALTH + "&dummy=" + npCommon.makeUuid();
                    var image = npCommon.newElement("img");
                    var timeoutid = null;
                    var isreturned = false;
                    npQuery(image).bind("load", function (e) {
                        // 19.08.07 jh 수정
                        if (timeoutid != null) clearTimeout(timeoutid);
                        delete image;
                        npConsole.check("NOS 이미지 체크 완료" + ", " + port);
                        docommand(options);
                    }).bind("error", function (e) {
                        // 19.08.07 jh 수정
                        if (timeoutid != null) clearTimeout(timeoutid);
                        delete image;
//						npConsole.log(npMessage.m50.replace("%p1%", port));
                        npConsole.check("NOS 핸드쉐이크 - 이미지 체크 에러");
                        if (isreturned == false) options.callback("", host, port);
                        isreturned = true;
                    });
                    image.src = imagepath;
                    // 19.08.07 jh 수정
                    timeoutid = setTimeout(function () {
                        delete image;
                        //npConsole.check("NOS 핸드쉐이크 - 이미지 체크 에러");
                        if (isreturned == false) options.callback("", host, port);
                        isreturned = true;
                    }, 2000);
                } catch (e) {
                    if (isreturned == false) options.callback("", host, port);
                    isreturned = true;
                    npConsole.log(e);
                }
            })({
                callback: function (result, host, port) {
                    var taskName = "task_" + host.split(".").join("_") + "_" + port;
                    remainTask.splice(npCommon.indexOf(remainTask, taskName), 1);

                    var foundPort = false;
                    // 응답값 재처리
                    switch (result) {
                        case npPacket.RESULT_TRUE :
                            var foundPort = true;
                            break;
                        case npPacket.RESULT_DETECT_DEBUG :
                            foundPort = true;
                            npBaseCtrl.showDetectDebug();
                            break;
                        case npPacket.RESULT_REQ_REINSTALL :
                            foundPort = true;
                            isRequiredReinstall = true;
                            break;
                        case npPacket.RESULT_CHECK_VERSION :
                            foundPort = true;


                            npConsole.log(npMessage.m51.replace("%p1%", host).replace("%p2%", port));
                            isRequiredCheckUpdate = true;

                            break;
                        case npPacket.RESULT_REQ_READY :

                            foundPort = false;
                            isRequiredReady = true;
                            npConsole.log(npMessage.m15);
                            break;

                        default :
                            foundPort = false;
                    }


                    if (npBaseCtrl.foundPort == false && foundPort == true) {
                        isRequiredReady = false;
                        npBaseCtrl.foundPort = true;
                        npBaseCtrl.currentHost = host;
                        npBaseCtrl.currentPort = port;
                        npConsole.log(npMessage.m14.replace("%h%", host).replace("%p%", port));


                        npCommon.setCookie("npPfsHost", npBaseCtrl.currentHost, 7, "/");
                        npCommon.setCookie("npPfsPort", npBaseCtrl.currentPort, 7, "/");

                        isCheckedDefault = true;
                        isCheckedAll = true;

                        //isRequiredCheckUpdate = true;
                        if (isRequiredCheckUpdate == true) {
                            npBaseCtrl.checkVersion(callback);
                        } else {
                            callback();
                            npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
                        }
                    }

                    if (remainTask.length == 0) {
                        taskRunning = false;
                        handshakeCallback = null;

                        if (isRequiredReady == true) {
                            isCheckedAll = false;
                            setTimeout(function () {
                                npBaseCtrl.findPort();
                            }, npPfsPolicy.Common.WaitTimeout);
                        } else if (isCheckedAll == false) {
                            npBaseCtrl.findPort();
                        } else if (npBaseCtrl.foundPort == false) {

                            npCommon.setCookie("npPfsHost", "", -1, "/");
                            npCommon.setCookie("npPfsPort", "", -1, "/");

                            callback();
                            npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
                        }
                    }
                }
            });
        };


    };


    this.checkVersion = function (callback) {
        var version = "";
        var url = npPfsPolicy.Common.VersionUrl;

        npConsole.log("업데이트버전 경로 : " + url + "");

        var timeoutid = setTimeout(function () {
            npConsole.log("업데이트버전 다운로드 실패(Timeout 10초).");
            callback();
            npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
        }, 10 * 1000);


        npBaseCtrl.foundPort = true;
        isRequiredReinstall = false;
        isRequiredCheckUpdate = false;

        npQuery.ajax({
            url: url,
            cache: false,
            crossDomain: true,
            async: false,
            type: "GET",
            global: false,
            dataType: "jsonp",
            jsonp: "jsonp_callback",
            jsonpCallback: "VersionInfo",
            contentType: "application/json",
            error: function (xhr, textStatus, errorThrown) {
                clearTimeout(timeoutid);
                callback();
                npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
            },
            success: function (data, textStatus, xhr) {
                clearTimeout(timeoutid);
                if (npCommon.isBlank(data)) {
                    callback();
                    npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
                    return;
                }

                var version = data;

                npConsole.log("업데이트버전 정보 : " + version);

                var paramCount = "1";			// 파라메터개수
                var command = npBaseCtrl.makeCommand(npPacket.PRODUCT_DM, npPacket.MODE_SYNC, npPacket.CMD_CHECK_VERSION, (paramCount + npCommon.makeLength(version) + version));
                npCommon.sendCommand(command, {
                    callback: function (result) {
                        // 결과가 없거나 오류가 있을 경우의 재 호출
                        switch (result) {
                            case npPacket.RESULT_FALSE :
                                // 업데이트 필요없습니다.
                                isRequiredReinstall = false;
                                break;
                            case npPacket.RESULT_TRUE :
                                isRequiredReinstall = true;
                            default :

                                break;
                        }

                        isRequiredCheckUpdate = false;
                        callback();
                        npQuery(document).trigger({type: "nppfs-nos-jhs", time: new Date()});
                        npConsole.check("NOS 핸드쉐이크 - 버전비교 종료");
                    }
                });

            },
            complete: function (xhr, textStatus) {
                clearTimeout(timeoutid);
                npConsole.check("NOS 핸드쉐이크 - 버전얻기 종료");
            }
        });
    }

    this.isInstall = function (callbacks) {
        if (npCommon.isNull(callbacks)) {
            callbacks = {};
        }
        if (npCommon.isNull(callbacks.success)) {
            callbacks.success = function () {
            }
        }
        if (npCommon.isNull(callbacks.fail)) {
            callbacks.fail = function () {
            }
        }


        if (npCommon.isBlank(this.currentPort) || this.currentPort <= 0 || isRequiredReinstall == true) {
            npBaseCtrl.findPort(function () {
                if (npBaseCtrl.foundPort == false || isRequiredReinstall == true) {
                    callbacks.fail();
                } else {
                    callbacks.success();
                }
            });
        } else {
            callbacks.success();
        }
    }

    var callBefore = false;
    this.checkInstall = function (callbacks) {
        if (npCommon.isNull(callbacks)) {
            callbacks = {};
        }
        if (npCommon.isNull(callbacks.before)) {
            callbacks.before = function () {
            }
        }
        if (npCommon.isNull(callbacks.after)) {
            callbacks.after = function () {
            }
        }

        if (callBefore == false) {
            // 설치전 메시지
            callbacks.before();
            callBefore = true;
        }

        npBaseCtrl.findPort(function () {

            if (npBaseCtrl.foundPort == false || isRequiredReinstall == true || isRequiredCheckUpdate == true) {
                if (isRequiredReinstall == true || npCommon.isBlank(this.currentPort) || this.currentPort <= 0) {
                    isRequiredReinstall = false;
                }


                setTimeout(function () {
                    //isCheckedCookie = false;
                    isCheckedDefault = false;
                    isCheckedAll = false;

                    npConsole.log("npBaseCtrl.checkInstall(callbacks);");
                    npBaseCtrl.checkInstall(callbacks);
                }, 2 * 1000);
                return;
            } else {
                // 설치 완료 후 작업
                callbacks.after();
            }
        });
    };


    var aliveTimer = null;

    function aliveTask() {
        if (npBaseCtrl.foundPort == false || npBaseCtrl.terminate == true) {
            return;
        }

        function reload(result) {
            try {		// 페이지 종료시 오류발생 가능
                // 결과가 없거나 오류가 있을 경우의 재 호출
                if (npCommon.parseKeepAliveResult(result, function () {
                    if (aliveTimer != null) {
                        clearInterval(aliveTimer);
                        aliveTimer == null;
                    }
                    setTimeout(function () {
                        aliveTask();
                    }, 3000);
                })) return;

                if (aliveTimer == null) {
                    aliveTimer = setInterval(aliveTask, 3000);
                }
            } catch (e) {
            }
        }

        npQuery(document).trigger({type: "nppfs-keep-alive", message: npMessage.m52, time: new Date()});

        function pad(n, width) {
            n = n + '';
            return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;
        }

        var products = 0;
        if (npBaseCtrl.Options.FW && npNCtrl.isRunning()) {
            products += 1;
        }
        if (npBaseCtrl.Options.SK && npKCtrl.isRunning()) {
            products += 2;

        }
        if (npBaseCtrl.Options.FD && npFCtrl.isRunning()) {
            products += 4;
        }
        if (npBaseCtrl.Options.KV && npVCtrl.isRunning()) {
            products += 8;
        }

        products = pad(products, 4);

        var random = npCommon.getRandomBytes(16);
        var param = [];
        param.push(npCommon.hexEncode(random) + npCommon.encrypt(products, npCommon.hexDecode(npCommon.hexEncode(random)), "ECB", 128));

        var parameter = param.length.toString(16);			// 파라메터개수
        for (var i = 0; i < param.length; i++) {
            var value = param[i];
            parameter += npCommon.makeLength(value) + value;
        }

        var command = npBaseCtrl.makeCommand(npPacket.PRODUCT_DM, npPacket.MODE_SYNC, npPacket.CMD_KEEP_ALIVE, parameter);

        if (npDefine.win) {

            npCommon.sendCommand(command, {callback: reload});


        } else {
            npCommon.sendCommand(command, {callback: reload});
        }

    };


    function startAlive() {
        if (npBaseCtrl.terminate == true) {
            return;
        }

        if (aliveTimer != null) {
            clearInterval(aliveTimer);
        }

        aliveTimer = setInterval(aliveTask, 3000);
    };

    function stopAlive() {
        if (aliveTimer != null) {
            //clearInterval(aliveTimer);
        }
    };


    // 지원가능 환경 확인
    this.isSupport = function () {
        var plugins = npPfsModules.getPlugins();

        // NOS를 지원하는 환경인지 체크하는 함수를 AND 조건에서 OR로 변경 by YGKIM @ 2016.10.28 with 김도현
        var ret = false;
        npQuery(plugins).each(function () {
            if (!npCommon.isNull(this.controller) && !npCommon.isNull(this.controller.isSupported) && typeof (this.controller.isSupported) == "function") {

                if (this.id === "nppfs.npk.module") {
                    ret = this.controller.isSupported() || npVCtrl.isSupported();
                } else {
                    ret = this.controller.isSupported();
                }

                return !ret;	// continue or break
            }
        });

        return ret;
        //return npPfsModules.isSupported();		// 무조건 AND 조건(키보드보안/마우스입력기 종속성 없이)
    };


    // 치환 데이터 얻기
    this.GetReplaceField = function (form, field) {


//		alert("[" + npVCtrl.isRunning() + "][" + field + "][" + form + "][" + npVCtrl.isUseYn(field, form) + "][" + npVCtrl.isKeypadUse(field, form) + "]");
        if (npVCtrl.isRunning() == true && npVCtrl.isKeypadUse(field)) {
            return npVCtrl.GetReplaceValue(form, field);
        }


        if (npKCtrl.isRunning() == true) {
            return npKCtrl.GetReplaceValue(form, field);
        }

    };

    // 치환 테이블 얻기
    this.GetResultField = function (form, field) {


//		alert("[" + npVCtrl.isRunning() + "][" + field + "][" + form + "][" + npVCtrl.isUseYn(field, form) + "][" + npVCtrl.isKeypadUse(field, form) + "]");
        if (npVCtrl.isRunning() == true && npVCtrl.isKeypadUse(field)) {
            return npVCtrl.GetReplaceTable(form, field);
        }


        if (npKCtrl.isRunning() == true) {
            return npKCtrl.GetReplaceTable(form, field);
        }

    };

    this.GetEncryptResult = function (form, field) {


        //alert("[" + npVCtrl.isRunning() + "][" + field + "][" + form + "][" + npVCtrl.isUseYn(field, form) + "][" + npVCtrl.isKeypadUse(field, form) + "]");
        if (npVCtrl.isRunning() == true && npVCtrl.isKeypadUse(field)) {
            return npVCtrl.GetEncryptResult(form, field).trim();
        }


        if (npKCtrl.isRunning() == true) {
            return npKCtrl.GetEncryptResult(form, field).trim();
        }

    };


    this.ReuseModules = function () {

        if (npKCtrl.isRunning() == true) {
            return npKCtrl.ReuseModules();
        } else {
            return;
        }

    };

    // 동적 필드 등록
    this.RegistDynamicField = function (form, field) {

        try {
            var fe = document.activeElement;
            if (!npCommon.isNull(fe) && fe.tagName && fe.tagName.toLowerCase() == "input" && (fe.type == "password" || fe.type == "text" || fe.type == "tel")) {
                npBaseCtrl.focusElement = fe;
            }
        } catch (e) {
        }

        if (npKCtrl.isRunning() == true) {
            npKCtrl.addDynamicField(form, field);
//			return;
        }


        if (npVCtrl.isRunning() == true) {

            npVCtrl.addDynamicField(form, field);
//			return;
        }

    };

    // 입력값 초기화
    this.ResetField = function (form, inputname) {


        if (npVCtrl.isRunning() == true) {
            npVCtrl.resetKeypad(inputname);
            npVCtrl.hideKeypad(inputname);
//			return;
        }


        if (npKCtrl.isRunning() == true) {
            npKCtrl.resetField(form, inputname);
//			return;
        }

    };

    this.RescanField = function () {
        try {
            var fe = document.activeElement;
            if (!npCommon.isNull(fe) && fe.tagName && fe.tagName.toLowerCase() == "input" && (fe.type == "password" || fe.type == "text" || fe.type == "tel")) {
                npBaseCtrl.focusElement = fe;
            }
        } catch (e) {
        }
        this.verifyFormName();

        if (npVCtrl.isRunning() == true) {
            npVCtrl.rescanField();
        }


        if (npKCtrl.isRunning() == true) {
            npKCtrl.rescanField();
        }

    };

    this.globalKeyValidation = null;

    this.SetGlobalKeyValidation = function (func) {
        this.globalKeyValidation = null;
        if (npCommon.isNull(func)) return;
        if (typeof (func) !== "function") return;
        this.globalKeyValidation = func;
    };

    this.verifyFormName = function () {
        npQuery(npQuery("form")).each(function (index, value) {
            var $form = npQuery(this);

            npCommon.findDivision(this, "byclass", "nppfs-elements");

            var dummy = "d" + npCommon.hexEncode(npCommon.getRandomBytes(8));
            if (npCommon.isBlank($form.attr("name"))) {
                $form.attr({"name": dummy});
            }
            // 동일이름으로 여러 개의  form을 사용하는 경우가 있어 form의 식별자를 추가  by YGKIM 2015.05.12
            if (npCommon.isBlank($form.attr("data-nppfs-form-id"))) {
                $form.attr({"data-nppfs-form-id": dummy});
            }
        });

        npQuery("input").each(function () {
            var type = npQuery(this).attr("type");
            if (npCommon.isBlank(type)) {
                npQuery(this).attr("type", "text");
                type = "text";
            }
            if (!npCommon.isBlank(type) && type != "text" && type != "password" && type != "tel") {
                return true;
            }
        });
    };

    this.encryptValue = function (value, callback) {
        if (typeof (callback) != "function") {
            alert(npMessage.m53);
            return;
        }
        if (npCommon.isBlank(value)) {
            callback("");
            return;
        }

        if (npBaseCtrl.foundPort == false || npBaseCtrl.terminate == true) {
            alert("NOS와 통신할 수 없습니다. npPfsStartup()으로 먼저 페이지를 초기화하십시오.");
            callback("");
            return;
        }

        var param = npCommon.hexEncode(npCommon.getBytes(value));
//		npConsole.log("파라메터...... : [" + value + "]");
//		npConsole.log("파라메터...... : [" + param + "]");

        var paramCount = "1";			// 파라메터개수
        var parameter = paramCount + npCommon.makeLength(param) + param;
        var command = npBaseCtrl.makeCommand(npPacket.PRODUCT_DM, npPacket.MODE_SYNC, npPacket.CMD_ENCRYPT_VALUE, parameter);
        npCommon.sendCommand(command, {
            callback: function (result) {
                // 결과가 없거나 오류가 있을 경우의 재 호출
                if (!npCommon.isBlank(result)) {
                    callback(result);
                    return;
                }
                callback("");
            }
        });
    };

    // 동적 생성 필드 매핑
    this.dynamicField = {};
    this.putDynamicField = function (form, field, dynamicField) {

        var prefix = (typeof (form) == "object" && form != null) ? npQuery(form).attr("name") + "_" : form + "_";

        var key = prefix + npQuery("input[name='" + field + "']").attr("name");
        var val = this.dynamicField[key];
        if (npCommon.isNull(val)) {
            this.dynamicField[key] = [dynamicField];
        } else {
            this.dynamicField[key].push(dynamicField);
        }
    }

    // 지정한 입력양식에 해당하는 동적생성필드 얻기
    this.getDynamicField = function (form, field) {

        var prefix = (typeof (form) == "object" && form != null) ? npQuery(form).attr("name") + "_" : form + "_";


        var key = prefix + npQuery("input[name='" + field + "']").attr("name");
        var val = this.dynamicField[key];
        if (npCommon.isNull(val)) {
            return [];
        }
        return this.dynamicField[key];
    }

    function find(value) {
        var arr = [npPfsConst.E2E_UNIQUE, npPfsConst.E2E_RESULT, npPfsConst.E2E_KEYPAD
            , "i_borun", "i_e2e_id", "i_e2e_key", "i_tot_hash", "i_log_total"
            , "i_elapsed_tm", "i_log_yn", "i_version", "i_tot_log"
            , "f_uuid", "f_key", "f_uuid"];
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }


    this.serializeArray = function (formname, dynamicOnly) {
        var form = (typeof (formname) == "object" && formname != null) ? formname : npCommon.findElement(formname);
        dynamicOnly = typeof (dynamicOnly) == "undefined" ? true : dynamicOnly;

        if (dynamicOnly == false) {
            return npQuery(form).serializeArray();
        }

        var o = [];
        npQuery.each(npQuery(form).serializeArray(), function () {
            if (this.name.indexOf("__E2E__") > 0
                || this.name.indexOf("__KI_") == 0
                || this.name.indexOf("__KIEXT_") == 0
                || this.name.indexOf("__KH_") == 0
                || this.name.indexOf("__KU_") == 0
                || find(this.name) >= 0) {
                o.push(this);
            }
        });
        return o;
    }


    this.toJson = function (formname) {
        var o = {};
        var form = (typeof (formname) == "object" && formname != null) ? formname : npCommon.findElement(formname);
        npQuery.each(npQuery(form).serializeArray(), function () {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }

    this.setColor = function (color) {

        npKCtrl.setColor(color);


        npVCtrl.setColor(color);

    };


    this.doFocusOut = function (obj, callback) {

        npKCtrl.doFocusOut(obj, callback);

    }

    this.enableUI = function (elementname, formname) {

        npKCtrl.enableUI(elementname, formname);


        npVCtrl.enableUI(elementname, formname);


    }

    this.disableUI = function (elementname, formname) {

        npKCtrl.disableUI(elementname, formname);


        npVCtrl.disableUI(elementname, formname);


    }


    // 함수이름 대소문자 구분
    this.IsVirtualMachine = this.isVirtualMachine;
    this.IsMetroUi = this.isMetroUi;
    this.IsInstall = this.isInstall;
    this.IsSupport = this.isSupport;
    this.CheckInstall = this.checkInstall;

    // V2.0 호환용
    this.isMobileDevice = this.IsMobileDevice = npDefine.isMobileDevice;
    this.launch = function (options) {
        // launch
        npPfsCtrl.init(options);
    }
};

w.npPfsCtrl = npBaseCtrl;


w.npNCtrl = new function () {
    this.id = "nppfs.npn.module";

    // 제품별 정책 - 개인방화벽
    var policy = {
        product_uuid: npPacket.PRODUCT_FW
        // 지원범위
        , support: {

            WIN: {
                Support: true,
                Os: {Min: "5.0", Max: "10.0"},
                Bw: {
                    IE: {Support: true, Min: "7.0", Max: "11.0"},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    OP: {Support: true, Min: "9.0"},
                    SF: {Support: true, Min: "5.0"},
                    EG: {Support: true, Min: "12.0"},
                    NC: {Support: false},
                    B360: {Support: true, Min: "7.5"},		// Chrome Version
                    QQ: {Support: true, Min: "38.0"}		// IE Version
                }
            }


            , MAC: {
                Support: true,
                Os: {Min: "10.8", Max: "10.16"},
                Bw: {
                    IE: {Support: false},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    SF: {Support: true, Min: "6.0"},
                    OP: {Support: true, Min: "18.0"}
                }
            }


            , LINUX: {
                Support: true,
                Os: {
                    Fedora: {Support: false},
                    Ubuntu: {Support: false},
                    CentOS: {Support: false},
                    OpenSUSE: {Support: false}
                },
                Bw: {
                    IE: {Support: false},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    OP: {Support: true, Min: "11.10"},
                    SF: {Support: false}
                }
            }

        }
    };

    var isRunning = false;
    this.isRunning = function () {
        return isRunning;
    };

    this.isRunnable = function () {
        return npBaseCtrl.Options.FW && this.isSupported();
    };

    this.isSupported = function () {
        if (npDefine.isMobileDevice() || npDefine.isMetroUi()) {
            return false;
        }
        return npDefine.isSupported(policy.support);
    };

    var CMD_FW_WIN_start = "781239ed5185ebc5b2424e3962bbc2c4460546713fcc5e709fe569a965c0e4a1";

    var CMD_FW_WIN_keepAlive = "833f01bcd477fbb24fde490cb2790420cfc69a206b331266defeaaceed2a4e7c";
    var CMD_FW_WIN_status = "56d9fe8c3e0de0dadbb0c14fd5e97e185124d491bbc55a88e880207abc39bed3";
    var CMD_FW_WIN_version = "7715c5487fdb5f06bcee7cdccad7cc55d583437467596d782ac025ecf5d9b037";
    var PRM_FW_WIN_data = "0100aeba9fd431f9456129461f63989fe51810639dd63a089a42a73e9f2cf72ca0a736f5f5f40a4f280ba374fcf36430468bc160c2aa0ed534cb53bb79aee7503210ce04edf0a7b2b987c17a95e8d585d5b91f76d6faac7302fca6a1dc337ee18ee7bec57edbedd5498c86bafaabcaf8e2d28024a7f239bfa866732174d29766680b";
    var CMD_FW_MAC_start = "fa24dacd3fcec5228beb27934a69f7debc71932d7b05f7c680149291e46b933e";
    var CMD_FW_MAC_cert = "54c46556e5d59ebd67a7f5a40d6d51037d43c66ea28c6c4636b5b1573a3340ae";
    var CMD_FW_MAC_keepAlive = "252f94f3048e65a36475ed3a0b7ad70f443408ec20cd47a76233eb2dd133a2b21ac0762da746dfa35eec0a46a63ea1e5a717f65c16c52093c44d33c46fc37e2c109be673f82a522b3863ee8e9df630abd664356551df0619306699163426acf6dc94c8e0c653fe73880f297d46dea87f";


    var CMD_FW_LNX_start = "8069b1fd61df7569fca5fa943918dbc04ff80142197d27d7fe6a4362dbcde036";
    var CMD_FW_LNX_cert = "35bd59ef7292d0878a68c2ae1c88df108b2dd8bc2faffbc005d2fd2be58374bc";
    var CMD_FW_LNX_keepAlive = "dc3528a51be1daf04a1b890255f7ce1eccce2b287bc12b00926f3caf02535383871ba83e25b14d579b6e0f1f895663be3cd4ca25c329b7c0ee25668a4c4e00ae44e59295ed4acb4380b48dd41bb739967f1dd486e14b6c544091c0eef53cc748caea938163b634ac453bbdad3b9aab612878dff7e3918e32dc8a407ca2ed1148";


    var aliveTimer = null;

    function send(cmd, param, o) {
        try {		// 페이지 종료시 오류발생 가능

            var command = npBaseCtrl.makeHeader(policy.product_uuid, npPacket.MODE_SYNC);
            if (!npCommon.isBlank(cmd)) {
                command.push(cmd);
            }
            if (!npCommon.isBlank(param)) {
                command.push(npCommon.makeLength(param));
                command.push(param);
            }
            npCommon.sendCommand(command.join(""), o);
        } catch (e) {
        }
    };

    function resetInterval() {
        if (aliveTimer != null) {
            //clearInterval(aliveTimer);
            aliveTimer = null;
        }
    };

    function aliveTask(interval) {
        if (npBaseCtrl.terminate == true) {
            return;
        }

        function reload(result) {
            // 결과가 없거나 오류가 있을 경우의 재 호출
            if (npCommon.parseKeepAliveResult(result, function () {
                resetInterval();

                setTimeout(function () {
                    aliveTask();
                }, npPfsPolicy.Common.WaitTimeout);
            })) return;

            if (aliveTimer == null) {
                aliveTimer = setInterval(aliveTask, 3000);
            }
        }

        if (npDefine.win) {

        } else if (npDefine.mac) {
            npQuery(document).trigger({type: "nppfs-npn-keep-alive", message: npMessage.m55, time: new Date()});
            send(null, CMD_FW_MAC_keepAlive, {callback: reload});
        } else if (npDefine.lnx) {
            npQuery(document).trigger({type: "nppfs-npn-keep-alive", message: npMessage.m55, time: new Date()});
            send(null, CMD_FW_LNX_keepAlive, {callback: reload});
        }
    };

    this.init = function () {
        //this.start();
    };

    this.startup = function () {
        if (npBaseCtrl.foundPort == false || npBaseCtrl.terminate == true || isCompleteStartup == true) {
            npQuery(document).trigger({type: "nppfs-module-startup", target: npNCtrl.id, time: new Date()});
            return;
        }

        npQuery(document).trigger({type: "nppfs-npn-before-startup", message: npMessage.m56, time: new Date()});

        resetInterval();

        function callback(result) {
            if (npCommon.parseResult(result, function () {
                npNCtrl.startup();
            })) return;

            isCompleteStartup = true;

            npQuery(document).trigger({type: "nppfs-module-startup", target: npNCtrl.id, time: new Date()});
            npQuery(document).trigger({type: "nppfs-npn-after-startup", message: npMessage.m57, time: new Date()});
        }

        if (npDefine.win) {
            send(CMD_FW_WIN_start + PRM_FW_WIN_data, null, {callback: callback});
        } else if (npDefine.mac) {
            send(null, CMD_FW_MAC_start, {callback: callback});
        } else if (npDefine.lnx) {
            send(null, CMD_FW_LNX_start, {callback: callback});
        }

        aliveTimer = setInterval(aliveTask, 3000);
        isRunning = true;
    };

    var isCompleteStartup = false;
    this.isComplete = function () {
        if (!this.isSupported() || !this.isRunnable()) {
            return true;
        }
        return isCompleteStartup;
    };

    this.finalize = function () {
        resetInterval();
        npQuery(document).trigger({type: "nppfs-npn-finalized", message: npMessage.m58, time: new Date()});
    };
};
w.npPfsModules.define({
    "id": npNCtrl.id
    , "name": "nProtect Online Security V1.0, Network Protection"
    , "handshake": true
    , "endtoend": false
    , "runvirtualos": true
    , "controller": npNCtrl
    , "isExecutable": function (options) {
        return (typeof (options.FW) != "undefined") ? options.FW : true;
        //return npBaseCtrl.Options.FW;
    }
});


w.npKCtrl = new function () {
    this.id = "nppfs.npk.module";

    // 제품별 정책 - 키보드보안
    var policy = {
        product_uuid: npPacket.PRODUCT_KC
        // 지원범위
        , support: {

            WIN: {
                Support: true,
                Os: {Min: "5.0", Max: "10.0"},
                Bw: {
                    IE: {Support: true, Min: "7.0", Max: "11.0"},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    OP: {Support: true, Min: "18.0"},
                    SF: {Support: true, Min: "5.0"},
                    EG: {Support: true, Min: "12.0"},
                    NC: {Support: false},
                    B360: {Support: true, Min: "7.5"},		// Chrome Version
                    QQ: {Support: true, Min: "38.0"}		// IE Version
                }
            }


            , MAC: {
                Support: true,
                Os: {Min: "10.8", Max: "10.11"},
                Bw: {
                    IE: {Support: false},
                    FF: {Support: true, Min: "18.0"},
                    CR: {Support: true, Min: "21.0"},
                    SF: {Support: true, Min: "6.0"},
                    OP: {Support: true, Min: "30.0"}
                }
            }


            , LINUX: {
                Support: true,
                Os: {
                    Fedora: {Support: false},
                    Ubuntu: {Support: false},
                    CentOS: {Support: false},
                    OpenSUSE: {Support: false}
                },
                Bw: {
                    IE: {Support: false},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    OP: {Support: true, Min: "11.10"},
                    SF: {Support: false}
                }
            }

        }

        , UserColor: {
            FieldTextColor: "#FF0000",
            FieldBackColor: "#A9D0F5",
            ReFieldTextColor: "#FF0000",
            ReFieldBackColor: "#AFD7AF"
        }
    };

    var isRunning = false;
    this.isRunning = function () {
        return isRunning;
    };

    this.isRunnable = function () {
        var ret = npBaseCtrl.Options.SK && this.isSupported();
        return ret;
    };

    this.isSupported = function () {
        if (npDefine.isMobileDevice() || npDefine.isMetroUi()) {
            return false;
        }

        if (npDefine.virtualMachine == true) {
            return false;
        }

        //return npVCtrl.isSupported();

        return npDefine.isSupported(policy.support);
    };

    var CMD_SK_npkEvent = "d572fa62064d1efb00223e695b8aaf8b1cb89849e62b55f6346198a449b9e7da";
    var CMD_SK_npkParam = "14438ce96411977a1f15b87d3843ffc8d0696e1a021525882dae7dbb7d275968";
    var CMD_SK_npkFeild = "3dfd33d807a50b94fa20832d15d79ffee74a9f1d1752968185341210883b62a7";
    var CMD_SK_npkCertInfo = "30dfb79d7a83354ef87bef216093b36ec13eac8a9a9b4bedfbffe924a71656c3";
    var CMD_SK_npkCertData = "2ad6778008fad26799de499da02e6d36b9979c3ebefdfe784e8c9cec93bd0712";
    var PRM_SK_Init = "e0eca52b087c3fbc4980a66b202110f0f8e8d0eca842882c934015eda45ec622";
    var PRM_SK_UnInit = "1c4f43760e2bef19908e0f15d7bbd1899b17f27d7d25abfe4ef872a2bd6735a5";
    var PRM_SK_Set = "83eb544dd49cffb79e8baa8c0847acbb0869159476cd8a1c201e4bbd53e3ebe3";
    var PRM_SK_Kill = "dd3fbe3d19384e9508128f577b22a21329b044c30f4e2c417a3b407ebf559d6f";
    var PRM_SK_Down1 = "00ea1520b178016376bc12896f383562b281c07ca091324d0d31bc4c4f60675d";
    var PRM_SK_Down2 = "ed17ad84a730406d3679d0b05684059f88c7f28a0d3dd19d7f5db6cfc1803f42";
    var PRM_SK_GetKey = "a11913c7410b60aca5f54dc6f0c93008fab7fc6d43639187e9a2c048e1d51f02";
    var PRM_SK_GetEncData = "3fa4465b95aa8b2a249657aae7704440b5d047d545c74fbf1bdae545f446886b";
    var PRM_SK_GetDoubleKey = "5eec4f574e7275e157eb1091392a192e9a81acbb887a871182f647c2303d1ef3";


    this.ID = "";
    this.Field = null;
    this.uuid = null;
    this.enckey = null;
    this.e2ekey = null;

    this.replaceTable = "";


    this.useInitechHex = "off";

    this.resetElements = [];

    function send(cmd, param, o) {
        try {		// 페이지 종료시 오류발생 가능

            var command = npBaseCtrl.makeHeader(policy.product_uuid, npPacket.MODE_SYNC);
            command.push(cmd);
            command.push(param);
            return npCommon.sendCommand(command.join(""), o);
        } catch (e) {
        }
    };
    this.send = function (cmd, param, o) {
        send(cmd, param, o);
    };

    var attr = function (element, name) {
        var attr = npQuery(element).attr(name);
        return (npCommon.isNull(attr)) ? "" : attr.toLowerCase();
    }

    this.init = function () {
        this.uuid = npBaseCtrl.uuid;
        this.enckey = npBaseCtrl.enckey;
    };

    var isCompleteStartup = false;
    this.isComplete = function () {
        if (!this.isSupported() || !this.isRunnable()) {
            return true;
        }
        return isCompleteStartup;
    };

    var isFinishInit = false;

    this.startup = function () {
        if (npBaseCtrl.foundPort == false || npBaseCtrl.terminate == true) {
            return;
        }

        startup();


    };

    this.finalize = function () {
        if (npBaseCtrl.foundPort == false) {
            return;
        }

        npKCtrl.doFocusOut();

        var params = PRM_SK_UnInit + "=" + npKCtrl.ID;
        send(CMD_SK_npkEvent, params, {async: false, direct: true});

        npQuery(document).trigger({
            type: "nppfs-npk-finalized"
            , message: npMessage.m99
            , time: new Date()
        });

    };

    this.GetEncryptResult = function (form, field) {
        if (npCommon.isNull(field)) {
            return;
        }
        if (npCommon.isNull(document.getElementsByName(field))) {
            return;
        }
        if (npKCtrl.isRunning() == true) {
            var ele1 = npCommon.findElement(npPfsConst.E2E_UNIQUE, form);
            var ele2 = npCommon.findElement(npPfsConst.E2E_RESULT, form);
            var hiddenVal = GetHiddenField(field);

            if (npCommon.isNull(ele1) || npCommon.isNull(ele2) || npCommon.isNull(hiddenVal) || npCommon.isNull(ele1.value) || npCommon.isNull(ele2.value) || npCommon.isNull(hiddenVal.value)) {
                return;
            }
            var q = [];
            q.push("m=c");
            q.push("u=" + npCommon.encParam(ele1.value));
            q.push("r=" + npCommon.encParam(ele2.value));
            q.push("v=" + npCommon.encParam(hiddenVal.value));
            var value = npCommon.send(npPfsPolicy.Common.CryptoUrl, q.join("&"));
            return value;
        }
    };


    function startup() {
        npConsole.check("NPK 초기화 시작");

        if (!npKCtrl.isSupported()) {
            npQuery(document).trigger({type: "nppfs-npk-jksc", time: new Date()});
            isCompleteStartup = true;
            return;
        }

        npQuery(document).trigger({
            type: "nppfs-npk-before-startup"
            , message: "키보드보안을 시작합니다."
            , time: new Date()
        });


        // 키보드보안 이벤트 처리
        npQuery(document).bind("nppfs-npk-jks nppfs-npk-jkc nppfs-npk-jki nppfs-npk-jkrf nppfs-npk-jksc", function (event) {
            npQuery(document).unbind(event);
            switch (event.type) {
                case "nppfs-npk-jks" :

                    initCertification();

                    break;
                case "nppfs-npk-jkc" :
                    bindReplaceData();
                    break;
                case "nppfs-npk-jki" :
                    npConsole.check("NPK 초기화 완료");
                    initFields();
                    break;
                case "nppfs-npk-jkrf" :
                    refocus();
                    npConsole.check("NPK 필드등록 완료");
                    npQuery(document).trigger({type: "nppfs-npk-jksc", time: new Date()});
                    break;
                case "nppfs-npk-jksc" :
                    isCompleteStartup = true;
                    isFinishInit = true;
                    npQuery(document).trigger({
                        type: "nppfs-npk-after-startup"
                        , message: "키보드보안이 정상적으로 시작되었습니다."
                        , time: new Date()
                    });
                    npConsole.check("NPK 시작 완료");
                    npQuery(document).trigger({type: "nppfs-module-startup", target: npKCtrl.id, time: new Date()});
                    break;
            }
        });


        if (isCompleteStartup == true) {
            npKCtrl.rescanField();
            return;
        }

        isRunning = true;

        send(CMD_SK_npkEvent, PRM_SK_Init, {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    startup();
                    // 원인미명의 키보드보안 응답값 공백으로 지연 수행 2016.06.08
//					setTimeout(function(){
//						startup();
//					}, npPfsPolicy.Common.WaitTimeout);
                })) return;

                var pageId = result.split("&&");
                if (pageId[0] == "ID") {
                    if (npCommon.isBlank(pageId[1])) {
                        npKCtrl.startup();
                        return;
                    }

                    npKCtrl.ID = pageId[1];
                }

                npQuery(document).trigger({type: "nppfs-npk-jks", time: new Date()});
            }
        });
    };

    function refocus() {
        try {
            if (document.hasFocus()) {
                var fe = npBaseCtrl.focusElement;
                if (!npCommon.isNull(fe) && fe.tagName && fe.tagName.toLowerCase() == "input" && (fe.type == "password" || fe.type == "text" || fe.type == "tel")) {
                    fe.blur();
                    fe.focus();

                    npBaseCtrl.focusElement = null;
                    if (!npCommon.isBlank(fe.name)) {
                        npConsole.log(npMessage.m24.replace("%p%", fe.name));
                    }
                }
            }
        } catch (e) {
        }

    }

    function initCertification() {
        if (npBaseCtrl.terminate == true) {
            return;
        }

        function delayTrigger(eventtype) {
            setTimeout(function () {
                npQuery(document).trigger({type: eventtype, time: new Date()});
            }, npPfsPolicy.Common.WaitTimeout);
        }

        var bindCertErrorCount = 0;
        npQuery(document).bind("nppfs-npk-jkci", function (event) {
            if (bindCertErrorCount >= npPfsPolicy.Common.MaxFailCount) {
                alert(npMessage.m19);
                bindCertErrorCount = 0;
                npQuery(document).trigger({type: "nppfs-npk-jkc", time: new Date()});
                return;
            }

            // Bind Cert
            var param = PRM_SK_GetKey + "=" + npKCtrl.ID;
            send(CMD_SK_npkEvent, param, {
                callback: function (result) {
                    if (npCommon.parseResult(result, function () {
                        bindCertErrorCount++;
                        delayTrigger("nppfs-npk-jkci");
                    })) return;

                    var pageId = result.split("&&");
                    if (npCommon.isNull(pageId) || pageId.length != 2) {
                        bindCertErrorCount++;
                        delayTrigger("nppfs-npk-jkci");
                        return;
                    }

                    if (pageId[0] == "CLIENTADDRESS") {
                        var DATA = pageId[1].split("&^&");
                        if (npCommon.isNull(DATA) || DATA.length != 2 || npCommon.isBlank(DATA[1])) {
                            bindCertErrorCount++;
                            delayTrigger("nppfs-npk-jkci");
                            return;
                        }

                        npKCtrl.e2ekey = DATA[1];
                    }

                    npQuery(document).trigger({type: "nppfs-npk-jkc", time: new Date()});

                    bindCertErrorCount = 0;
                }
            });
        });


        var params = [];
        params.push("Cert=");
        params.push("PKI=5");
        params.push("CertEnc=" + npKCtrl.enckey);
        params.push("ID=" + npKCtrl.ID);
        send(CMD_SK_npkParam, params.join("&"), {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    initCertification();
                })) return;
                npQuery(document).trigger({type: "nppfs-npk-jkci", time: new Date()});
            }
        });

    };


    function bindReplaceData() {
        if (npBaseCtrl.terminate == true) {
            return;
        }

        var param = PRM_SK_GetEncData + "=" + npKCtrl.ID;
        send(CMD_SK_npkEvent, param, {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    bindReplaceData();
                })) return;
                var pageId = result.split("&&");
                if (npCommon.isNull(pageId) || pageId.length != 2) {
                    bindReplaceData();
                    return;
                }

                if (pageId[0] == "ENCREPLACETABLE") {
                    var DATA = pageId[1].split("&^&");
                    if (npCommon.isNull(DATA) || DATA.length != 2 || npCommon.isBlank(DATA[1])) {
                        bindReplaceData();
                        return;
                    }
                    if (npCommon.isBlank(npKCtrl.replaceTable)) {
                        npKCtrl.replaceTable = DATA[1];
                    }
                }

                npQuery(document).trigger({type: "nppfs-npk-jki", time: new Date()});
            }
        });
    };

    this.rescanField = function () {
        var timeoutid = null;

        function wwait() {
            if (isCompleteStartup == true) {
                clearTimeout(timeoutid);
                initFields();
                npQuery(document).bind("nppfs-npk-jkrf", function (event) {
                    npQuery(document).unbind(event);
                    refocus();
                });
            } else {
                timeoutid = setTimeout(wwait, npPfsPolicy.Common.WaitTimeout);
            }
        }

        wwait();
    }

    function initFields() {
//		try {
        var elementCount = 0;
        npQuery("input").each(function () {
            var type = npQuery(this).attr("type");
            if (npCommon.isBlank(type)) {
                npQuery(this).attr("type", "text");
                type = "text";
            }
            if (!npCommon.isBlank(type) && type != "text" && type != "password" && type != "tel") {
                return true;
            }
            var name = npQuery(this).attr("name");
            if (npCommon.isBlank(name)) {
                npQuery(this).attr("name", npQuery(this).attr("id"));
            }
        });


        npQuery("input[type=text], input[type=password], input[type=tel]").each(function () {


            var element = this;
            var form = this.form;
            var name = npQuery(element).attr("name");

            // 이미 생성된 필드인지 확인
            if (npCommon.isBlank(name) || name == npPfsConst.E2E_RESULT || name == npPfsConst.E2E_UNIQUE || name == npPfsConst.E2E_KEYPAD) {
                return true;
            }
            // 이미 생성된 값 필드인지 확인
            if (name.indexOf("__E2E__") > 0 || name.indexOf("__KI_") == 0 || name.indexOf("__KH_") == 0) {
                return true;
            }


            // 이미 등록된 항목
            if (npQuery(element).hasClass("nppfs-npk")) {
                return true;
            }

            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                if (name.indexOf("__FORMATTER__") > 0) {
                    return true;
                }
            }

            // 현대카드 UI확장기능(라온의 attribute를 NOS attribute로 변경) by YGKIM 2015.09.10
            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.secureKeyUiModifier) == "function") {
                //npConsole.log("현대카드 UI확장기능(라온의 attribute를 NOS attribute로 변경) by YGKIM 2015.09.10");
                npPfsExtension.secureKeyUiModifier(element);
            }

            var exectype = attr(element, "npexecutetype");
            if (exectype != "" && exectype.indexOf("k") == -1) {
                return true;
            }


            npCommon.makeElement(form, [npPfsConst.E2E_RESULT, npPfsConst.E2E_UNIQUE], [npKCtrl.e2ekey, npKCtrl.uuid]);


            // 키보드보안을 사용하지 않는 필드인지 확인
            var av = attr(element, npBaseCtrl.Options.AN);


            // 키보드보안 사용안함/E2E/치환/일반키보드보안 기능에 속하지 않는 필드는 등록 배제
            if (av === "off" || npCommon.arrayNotIn([npBaseCtrl.Options.AV, "re", "sub", "des", "db", "key"], av)) {
                return true;
            }


            // 객체 비활성화
            element.blur();

            npQuery(document).trigger({
                type: "nppfs-npk-before-regist-field"
                , message: npMessage.m61.replace("%p1%", element.name)
                , target: element
                , form: (!npCommon.isNull(form)) ? npQuery(form).attr("name") : null
                , name: element.name
                , time: new Date()
            });

            npKCtrl.registEachField(form, element);

            elementCount++;
        });

        if (elementCount == 0) {
            npQuery(document).trigger({type: "nppfs-npk-jkrf", time: new Date()});
            return;
        }
//		} catch (e) {
//			npConsole.log(e);
//			alert(e);
//		}
    };


    function makeCodeE2E(element) {
        var av = attr(element, npBaseCtrl.Options.AN);
        var etoe = "OFF";
        if (npCommon.isBlank(av)) {
            etoe = "OFF";
        } else if (av === "key") {
            etoe = "OFF";

        } else if (av === "re") {
            etoe = "RE";
        } else if (av === "sub") {
            etoe = "SUB";
        } else if (av === "des") {
            etoe = "DES";


        } else if (av == npBaseCtrl.Options.AV) {
            etoe = "ON";
        } else if (av === "db") {
            etoe = "DB";

        } else {
            etoe = "OFF";
        }

        return etoe;
    };

    function makeCodeIME(element) {
        var ime = "ON";
        try {
            var style = attr(element, "style");
            var styleSplit = style.split(";");
            for (var i = 0; i < styleSplit.length; i++) {
                var sstyle = npCommon.trim(styleSplit[i]);
                if (sstyle.indexOf("ime-mode:") == 0 || sstyle.indexOf("-ms-ime-mode:") == 0) {
                    var styleOption = styleSplit[i].split(':');
                    if (npCommon.trim(styleOption[1]) == "disabled") {
                        ime = "OFF";
                        break;
                    }
                }
            }
        } catch (e) {
        }
        return ime;
    };

    function makeHiddenFieldName(element) {
        var name = npQuery(element).attr("name");
        var ret = "";
        var av = attr(element, npBaseCtrl.Options.AN);


        if (npCommon.arrayIn([npBaseCtrl.Options.AV, "db", "re", "sub", "des"], av)) {

            ret = name + "__E2E__";

        }

        return ret;
    };

    function setFieldColor(element) {

        var ret = "";
        var $element = npQuery(element);
        var av = attr(element, npBaseCtrl.Options.AN);
        if (npCommon.arrayIn(["re", "sub", "des"], av)) {
            $element.css({
                "color": policy.UserColor.ReFieldTextColor,
                "background-color": policy.UserColor.ReFieldBackColor
            });
        } else if (npCommon.arrayIn([npBaseCtrl.Options.AV, "db"], av) && true) {
            $element.css({
                "color": policy.UserColor.FieldTextColor,
                "background-color": policy.UserColor.FieldBackColor
            });
        }

    };

    var remainTask = [];
    this.registEachField = function (form, element) {
        if (typeof (element) == "string") {
            element = npCommon.findElement(element, form);
        }
        if (typeof (element) == "undefined") {
            return ret;
        }

        var $element = npQuery(element);
        var $form = npQuery(form);

        // 이미 초기화된 입력양식
        if ($element.hasClass("nppfs-npk")) {
            return true;
        }
//		var exectype = attr(element, "npexecutetype");
//		if(exectype != "" && exectype.indexOf("k") == -1){
//			return true;
//		}


        // 마우스입력기와의 충돌로 인하여 마우스입력기를 초기화
        if (npQuery(element).hasClass("nppfs-npv")) {
            npVCtrl.resetKeypad($element.attr("name"), formname);
            npVCtrl.setKeypadUse($element.attr("name"), false);
            npVCtrl.destroyKeypad($element.attr("name"), formname);
        }


        var av = attr(element, npBaseCtrl.Options.AN);
        var formname = npCommon.isNull(form) ? "blank" : $form.attr("name");
        var taskName = "task_" + $element.attr("name") + "_" + formname;
        if (npCommon.indexOf(remainTask, taskName) < 0) {
            remainTask.push(taskName);
        }

        $element.addClass("nppfs-npk");

        if (npDefine.ie) {
            $element.bind("contextmenu dragstart click focusin focusout keypress selectstart keydown", function (event) {
                return eventHandler(event);
            });
        } else {
            $element.bind("contextmenu dragstart click focus blur keypress selectstart keydown", function (event) {
                return eventHandler(event);
            });
        }

        if (npCommon.arrayIn([npBaseCtrl.Options.AV, "db"], av)) {
            $element.attr({
                "autocorrect": "off"
                , "spellcheck": "false"
                , "autocomplete": "off"
                , "autocapitalize": "off"
            });
        }

        //try {
        var params = [];
        params.push("name=" + element.name);
        params.push("Length=" + element.maxLength);
        params.push("type=" + element.type);
        params.push("E2E=" + makeCodeE2E(element));
        params.push("ID=" + npKCtrl.ID);
        params.push("IME=" + makeCodeIME(element));

        params.push("Dummy=OFF");


        send(CMD_SK_npkFeild, params.join("&"), {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    npKCtrl.registEachField(form, element);
                })) return;

                var av = attr(element, npBaseCtrl.Options.AN);
                if (!npCommon.isBlank(result)) {
                    setFieldColor(element);

                    npQuery(document).trigger({
                        type: "nppfs-npk-after-regist-field"
                        , message: npMessage.m62.replace("%p1%", $element.attr("name"))
                        , target: element
                        , form: (!npCommon.isNull(form)) ? $form.attr("name") : null
                        , name: element.name
                        , time: new Date()
                    });
                }
                remainTask.splice(npCommon.indexOf(remainTask, taskName), 1);
                if (remainTask.length == 0) {
                    npQuery(document).trigger({type: "nppfs-npk-jkrf", time: new Date()});
                }
            }
        });

        if (!npCommon.isBlank(makeHiddenFieldName(element))) {
            npCommon.makeElement(form, [makeHiddenFieldName(element)]);
            if ($element.attr("nppfs-formatter-type") != undefined) {
                npCommon.makeElement(form, [element.name + "__FORMATTER__"]);
            }
            npPfsCtrl.putDynamicField(form, element.name, [makeHiddenFieldName(element)]);
        }
        //} catch(e) {
        //	alert(e);
        //}
    }


    this.resetField = function (form, element) {
        if (typeof (element) == "string") {
            element = npCommon.findElement(element, form);
        }
        if (typeof (element) == "undefined") {
            return;
        }
        element.value = "";
        var hidden = GetHiddenField(element);
        if (!npCommon.isNull(hidden)) {
            hidden.value = "";
        }
        this.resetElements.push(element.name);
    };

    this.enableUI = function (elementname, formname) {
        if (typeof (elementname) == "string") {
            element = npCommon.findElement(elementname, formname);
        }
        if (typeof (element) == "undefined") {
            return;
        }
        npQuery(element).attr("keypad-disabled-ui", "false");
        //npQuery(element).prop("disabled",false);
    };

    this.disableUI = function (elementname, formname) {
        if (typeof (elementname) == "string") {
            element = npCommon.findElement(elementname, formname);
        }
        if (typeof (element) == "undefined") {
            return;
        }
        npQuery(element).attr("keypad-disabled-ui", "true");
        //npQuery(element).prop("disabled",true);
    };

    this.setColor = function (color) {
        policy.UserColor.FieldTextColor = color.TextColor;
        policy.UserColor.FieldBackColor = color.FieldBgColor;
        policy.UserColor.ReFieldTextColor = color.ReTextColor;
        policy.UserColor.ReFieldBackColor = color.ReFieldBgColor;
    };


    function eventHandler(event) {
        var ret = true;
        var f = event.target ? event.target : event.srcElement;

        // IE에서 인증서 연동 후 엔터를 누르면 .isNull에서 스크립트 오류발생하여 예외처리
        try {
            if (npCommon.isNull(event) || npBaseCtrl.terminate == true || npKCtrl.ID == "") {
                npCommon.stopEvent(event);
                return false;
            }

            switch (event.type) {
                case "contextmenu" :
                case "dragstart" :
                    npCommon.stopEvent(event);
                    break;
                case "selectstart" :
                    if (npDefine.op) {
                        npCommon.stopEvent(event);
                        ret = false;
                    }

                    break;
                case "focusin" :
                case "focus" :
                    ret = onFocusIn(event);
                    break;
                case "focusout" :
                case "blur" :
                    ret = onFocusOut(event);
                    break;
                case "click" :
                    ret = onClearClickEvent(event);
                    break;
                case "keydown" :
                    ret = onKeyDown(event);
                    break;
                case "keypress" :
                    if (typeof (npKCtrl) == "undefined" || npKCtrl == null) return false;

                    if (npDefine.ff) {
                        var keycode = keycode = event.which;
                        if (keycode == 8 || event.keyCode == 9) {
                            return true;
                        }

                        if (112 <= event.keyCode && event.keyCode <= 123) {
                            return true;
                        }

                        npCommon.stopEvent(event);
                        if ((event.ctrlKey == true && keycode == 97)
                            || (event.ctrlKey == true && keycode == 99)
                            || (event.ctrlKey == true && keycode == 118)
                            || (event.ctrlKey == true && keycode == 120)
                        ) {
                            ret = false;
                        }
                    }
                    break;
            }
        } catch (e) {
        }
        return ret;
    };

    function onClearClickEvent(event) {
        if (typeof (npKCtrl) == "undefined" || npKCtrl == null) return false;

        try {
            var f = event.target ? event.target : event.srcElement;
            if (f.type == "text" || f.type == "password" || f.type == "tel") {

                var pos = f.value.length;
                if (f.createTextRange) {
                    var range = f.createTextRange();
                    range.move('character', pos);
                    range.select();
                } else if (f.setSelectionRange) {
                    f.setSelectionRange(pos, pos);
                } else {
                    //f.focus();
                }

            }
        } catch (e) {
        }
    };

    function onFocusIn(event) {
        if (typeof (npKCtrl) == "undefined" || npKCtrl == null) return false;

        try {
            function doJob() {


                var f = event.target ? event.target : event.srcElement;

                //마우스 키패드 활성화 방식이 수동이 아니고 npexecutetype 속성이 k(e2e)일 경우 focusin 이벤트 될때 기존 키패드 숨김 pjh
                if (typeof (npVCtrl) != "undefined" && npVCtrl.isRunning() == true) {
                    npVCtrl.hideAll(f);
                }

                if (npQuery(f).prop("readonly") == true || npQuery(f).prop("disabled") == true) {
                    npCommon.stopEvent(event);
                    return;
                }

                if (npQuery(f).attr("keypad-disabled-ui") == "true") return;
                if (!npQuery(f).is(":visible")) {
                    f = null;
                    npCommon.stopEvent(event);
                    return false;
                }

                if (!npCommon.isNull(f)) {
                    f.selectionStart = 0;
                    f.selectionEnd = 0;
                    if (!npDefine.ie && !npDefine.qq) f.focus();		// IE 10에서 입력필드의 포커스 교환이 발생하는 오류로 인해 주석처리

                    npKCtrl.Field = f;


                    f.value = "";
                    var hidden = GetHiddenField(f);
                    if (!npCommon.isNull(hidden)) {
                        hidden.value = "";
                    }

                    var $element = npQuery(f);
                    if ($element.attr("nppfs-formatter-type") != undefined) {
                        var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
                        $formatter.val("");
                    }

                    var params = PRM_SK_Set + "=" + npKCtrl.ID + "=" + f.name;

                    send(CMD_SK_npkEvent, params, {
                        callback: function (result) {
                            if (!npDefine.IsOldIe()) {
                                if (npCommon.parseResult(result, function () {
                                    onFocusIn(event);
                                    // 원인미명의 키보드보안 응답값 공백으로 지연 수행 2016.06.08
//								setTimeout(function(){
//									onFocusIn(event);
//								}, npPfsPolicy.Common.WaitTimeout);
                                })) return;
                            }

                        }
                    });
                    npQuery(document).trigger({
                        type: "nppfs-npk-focusin"
                        , message: npMessage.m63.replace("%p1%", npQuery(f).attr("name"))
                        , target: f
                        , form: (!npCommon.isNull(f.form)) ? npQuery(f.form).attr("name") : null
                        , name: f.name
                        , time: new Date()
                    });
                }
            }

            if (!npCommon.isNull(npKCtrl.Field)) {
                npKCtrl.doFocusOut(npKCtrl.Field, doJob);
            } else {
                doJob();
            }
        } catch (e) {
        }
    };

    this.doFocusOut = function (obj, callback) {
        if (npCommon.isNull(npKCtrl.Field)) return;
        if (npCommon.isNull(obj)) obj = npKCtrl.Field;
        var params = PRM_SK_Kill + "=" + npKCtrl.ID + "=" + obj.name;
        send(CMD_SK_npkEvent, params, {
            direct: true,
            callback: function (result) {
                //일반키보드 보안 연속으로 키입력 중 키 입력 불가 증상 현상으로 인해 주석
//				if(!npDefine.IsOldIe()) {
//					if(npCommon.parseResult(result, function(){
//						npKCtrl.doFocusOut(obj);
//					})) return;
//				}

                npQuery(document).trigger({
                    type: "nppfs-npk-focusout"
                    , message: npMessage.m64.replace("%p1%", npQuery(obj).attr("name"))
                    , target: obj
                    , form: (!npCommon.isNull(obj.form)) ? npQuery(obj.form).attr("name") : null
                    , name: npQuery(obj).attr("name")
                    , time: new Date()
                });
                npQuery(obj).trigger({type: 'change'});

                if (typeof (callback) == "function") {
                    callback(obj);
                }
            }
        });
        npKCtrl.Field = null;
    };

    function onFocusOut(event) {
        if (typeof (npKCtrl) == "undefined" || npKCtrl == null) return false;

        var f = event.target ? event.target : event.srcElement;
        npKCtrl.doFocusOut(f)
    };

    function fireKeyEvent(target, keyCode) {
        npQuery(target).trigger({type: 'keypress', which: keyCode, keyCode: keyCode});
//		npQuery(target).trigger({ type : 'keydown', which : keyCode, keyCode : keyCode });
        npQuery(target).trigger({type: 'keyup', which: keyCode, keyCode: keyCode});
//		npQuery(target).trigger({ type : 'change'});
    }

    function onKeyDown(event) {
        if (typeof (npKCtrl) == "undefined" || npKCtrl == null) return false;

        try {
            var f = event.target ? event.target : event.srcElement;

            if (npCommon.isNull(f) || (f.type != "text" && f.type != "password" && f.type != "tel") || npCommon.isNull(npKCtrl.Field)) {
                npCommon.stopEvent(event);
                return false;
            }

            var f = npKCtrl.Field;
            var av = attr(f, npBaseCtrl.Options.AN);


            npConsole.log("키보드보안 키다운 이벤트발생 : " + f.name + " : " + f.value + " : " + event.keyCode);

            try {

                // Backspace
                if (event.keyCode == 8) {
                    npCommon.stopEvent(event);

                }

            } catch (e) {
            }

            // 맥용 사파리 패치 2015.08.05
            if (npDefine.mac && npDefine.sf) {
                if (event.keyCode == 38) {
                    event.keyCode = 49;
                } else if (event.keyCode == 40) {
                    event.keyCode = 32;
                }
            }

            // Ctrl + C, Ctrl + V, Ctrl + X
            if ((event.ctrlKey == true || event.metaKey == true) && (event.keyCode == 67 || event.keyCode == 86 || event.keyCode == 88)) {
                npCommon.stopEvent(event);
                return false;
            }

            // 93 : Func
            if (event.keyCode == 93) {
                npCommon.stopEvent(event);
                return false;
            }


            // 32 : space, 33 : Page Up, 34 : Page Down, 35 : End, 36 : Home, 37 : Arrow Left, 38 : Arrow Up, 39 : Arrow Right, 40 : Arrow Down, 45 : Insert, 46 : Delete

            // 33 : Page Up, 34 : Page Down, 36 : Home, 37 : Arrow Left, 38 : Arrow Up, 39 : Arrow Right, 40 : Arrow Down, 45 : Insert, 46 : Delete
            if (npCommon.arrayIn([33, 34, 36, 37, 38, 39, 40, 45, 46], event.keyCode)) {
                npCommon.stopEvent(event);
                return false;
            }


            // 키 값 처리
            if (!npCommon.isNull(event.charCode) && event.charCode != 0) {
                return false;
            }

            // 16 : shift, 49 : 1, 32 : space
            if (event.keyCode == 32 || event.keyCode == 49 || (event.keyCode == 16 && npDefine.win)) {
                if (npDefine.lnx || npDefine.mac) {
                    var params = PRM_SK_Down1 + "=" + npKCtrl.ID + "=" + f.name;
                } else {
                    var params = PRM_SK_Down1 + "=" + npKCtrl.ID + "=" + f.name + "=" + npKCtrl.useInitechHex;
                }
                send(CMD_SK_npkEvent, params, {
                    callback: function (result) {
                        // 결과가 없거나 오류가 있을 경우의 재 호출
                        if (npCommon.parseResult(result, function () {
                            onKeyDown(event);
                        })) return;
                        var pageId = result.split("&&");
                        onSetFieldData(pageId[1], pageId[2], event.keyCode);

                    }
                });
                npCommon.stopEvent(event);
                return false;
            } else if (event.keyCode == 8) {
                var params = PRM_SK_Down2 + "=" + npKCtrl.ID + "=" + f.name;
                send(CMD_SK_npkEvent, params, {
                    callback: function (result) {
                        // 결과가 없거나 오류가 있을 경우의 재 호출
                        if (npCommon.parseResult(result, function () {
                            onKeyDown(event);
                        })) return;
                        var pageId = result.split("&&");
                        OnDeleteFieldData();
                    }
                });

            } else if (event.keyCode == 9) {		// Tab
                //npKCtrl.stopTab(event);			// 중복처리로 인한 삭제 by KIM 20150126
            } else if (event.keyCode == 13) {		// Enter
                //npConsole.log("onkeydown-enter");

            } else if ((av === "" || av === "key") && npDefine.mac && npDefine.ff) { 		// 한글
                if (npDefine.lnx || npDefine.mac) {
                    var params = PRM_SK_Down1 + "=" + npKCtrl.ID + "=" + f.name;
                } else {
                    var params = PRM_SK_Down1 + "=" + npKCtrl.ID + "=" + f.name + "=" + npKCtrl.useInitechHex;
                }
                send(CMD_SK_npkEvent, params, {
                    callback: function (result) {
                        if (result === npPacket.RESULT_UNICODE || result === npPacket.RESULT_TRUE) {
                            //do nothing
                            //return;
                        } else {
                            // 결과가 없거나 오류가 있을 경우의 재 호출
                            if (npCommon.parseResult(result, function () {
                                onKeyDown(event);
                            })) return;
                            var pageId = result.split("&&");
                            onSetFieldData(pageId[1], pageId[2], event.keyCode);
                        }
                    }
                });
            } else if (event.keyCode == 229) {
                // do nothing...
            } else if (event.keyCode == 255) {
                npCommon.stopEvent(event);
                return false;

            } else {

                npCommon.stopEvent(event);
                return false;

            }
        } catch (e) {
        }
    };

    var arrowPressed = false;

    this.lastTab = null;
    this.stopTab = function (event) {
        if (typeof (npKCtrl) == "undefined" || npKCtrl == null) return false;

        if (!npCommon.isNull(event) && event.keyCode == 9) {
            var current = new Date().getTime();
            if (npCommon.isNull(npKCtrl.lastTab)) {
                //npConsole.log("onkeydown-tab");
            } else {
                if (current - npKCtrl.lastTab <= 150) {
                    //npConsole.log("onkeydown-tab-stop");
                    npCommon.stopEvent(event);
                } else {
                    //npConsole.log("onkeydown-tab-with-last-tab");
                }
            }
            npKCtrl.lastTab = current;
        }
    };

    function onSetFieldData(EncData, EncData2, eventKeyCode) {
        if (npCommon.isNull(npKCtrl.Field)) {
            return;
        }

        // 최대길이 체크
        var f = npKCtrl.Field;
        var $element = npQuery(f);
        var val = f.value;

        if ($element.prop("readonly") == true || $element.prop("disabled") == true) {
            return;
        }

        if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
            val = npPfsExtension.formatter($element, false);
        }

        if ($element.attr("data-keypad-action") == "amount") {
            val = npCommon.uncomma($element.val());
        }

        var maxlength = npQuery(f).prop("maxlength");

        if (!npCommon.isBlank(maxlength) && !npCommon.isBlank(val) && maxlength > 0 && val.length >= maxlength) {
            fireKeyEvent(f, eventKeyCode);
            return;
        }


        try {
            var str = npCommon.hexDecode(EncData);
            if (npCommon.isBlank(str)) return;

            var temp = npCommon.decrypt(str, npCommon.hexDecode(npKCtrl.ID), "ECB", 128);
            if (npCommon.isBlank(temp)) return;

            if (temp.length > 0) {
                temp = temp.substring(0, 1);
            }

            if (npCommon.isBlank(temp)) {
                return;
            }

            // keydown event에 따라 키코드 검증
            var keyCode = temp.charCodeAt(0);
            var doContinue = npCommon.isNull(npBaseCtrl.globalKeyValidation);
            doContinue = doContinue || typeof (npBaseCtrl.globalKeyValidation) !== "function"
            doContinue = doContinue || npBaseCtrl.globalKeyValidation(keyCode, f);

            // 키 검증 기능 추가 by YGKIM 2015.09.10
            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.keyValidation) == "function") {
                doContinue = doContinue && npPfsExtension.keyValidation(f, keyCode);
            }

            if (doContinue == false) {
                // 키보드보안 버퍼에서 키를 삭제하도록 처리
                // var params = PRM_SK_Down2 + "=" + npKCtrl.ID;
                var params = PRM_SK_Down2 + "=" + npKCtrl.ID + "=" + f.name;
                send(CMD_SK_npkEvent, params, {
                    callback: function (result) {
                        // 결과가 없거나 오류가 있을 경우의 재 호출
                        if (npCommon.parseResult(result, function () {
                            onSetFieldData(EncData, EncData2, eventKeyCode);
                        })) return;
                        var pageId = result.split("&&");
                        //	OnDeleteFieldData();
                    }
                });
                npConsole.log("The key value(" + keyCode + ") is invalid, clear the keystroke.");
                //npCommon.stopEvent(event);
                return;
            }

            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                $element.val(npPfsExtension.formatter($element, false));
            }

            if ($element.attr("data-keypad-action") == "amount") {
                $element.val(npCommon.uncomma($element.val()));
            }
            var av = attr(f, npBaseCtrl.Options.AN);
            if ((npCommon.arrayIn([npBaseCtrl.Options.AV, "db"], av) && true) || npCommon.arrayIn(["re", "sub", "des"], av)) {
                var h = GetHiddenField(f);
                if (npCommon.isNull(h)) {
                    return;
                }

                if (npCommon.arrayIn(["re", "sub"], av)) {
                    var str = npCommon.hexDecode(EncData2);
                    if (npCommon.isBlank(str)) return;

                    var dec = npCommon.decrypt(str, npCommon.hexDecode(npKCtrl.ID), "ECB", 128);
                    if (npCommon.isBlank(dec)) return;
                    if (dec.length > 0) {
                        dec = dec.substring(0, 1);
                    }

                    f.value += temp;
                    h.value += dec;
                } else if (npCommon.arrayIn([npBaseCtrl.Options.AV, "des", "db"], av)) {
                    if (npKCtrl.useInitechHex == "on") {
                        var str = npCommon.hexDecode(EncData2);
                        if (npCommon.isBlank(str)) return;

                        f.value += temp;
                        h.value += str;
                    } else {
                        f.value += temp;
                        h.value += EncData2;
                    }

                    if ($element.attr("nppfs-formatter-type") != undefined) {
                        var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
                        $formatter.val($formatter.val() + "a");
                        //npConsole.log($formatter.attr("name")+"==> ["+$formatter.val()+"]");
                    }
                }
            } else {

                f.value += temp;

            }
            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                $element.val(npPfsExtension.formatter($element, true));
            }

            if ($element.attr("data-keypad-action") == "amount") {
                $element.val(npCommon.comma($element.val()));
            }

            npConsole.log(npMessage.m65.replace("%p1%", f.name).replace("%p2%", temp.charCodeAt(0)));
            npQuery(document).trigger({
                type: "nppfs-npk-put-complete"
                , message: npMessage.m66
                , target: f
                , form: (!npCommon.isNull(f.form)) ? npQuery(f.formm).attr("name") : null
                , name: f.name
                , time: new Date()
            });

            var keyCode = temp.charCodeAt(0);
            fireKeyEvent(f, keyCode);
        } catch (e) {
        }
    };


    function GetHiddenField(f) {
        var name = (typeof (f) == "object") ? makeHiddenFieldName(f) : makeHiddenFieldName(npQuery("input[name='" + f + "']"));

        var form = !npCommon.isNull(f.form) ? f.form : null;
        return npCommon.findElement(name, form);

    };

    function OnDeleteFieldData() {
        try {
            var f = npKCtrl.Field;
            var h = GetHiddenField(f);
            var $element = npQuery(f);

            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                $element.val(npPfsExtension.formatter($element, false));
            }

            if ($element.attr("data-keypad-action") == "amount") {
                $element.val(npCommon.uncomma($element.val()));
            }
            var av = attr(f, npBaseCtrl.Options.AN);
            if (npCommon.isBlank(av)) {
                return;
            }

            if (!npCommon.isNull(h)) {
                f.value = f.value.substring(0, f.value.length - 1);
                if (npCommon.arrayIn(["re", "sub"], av)) {
                    h.value = h.value.substring(0, h.value.length - 1);

                } else if (npCommon.arrayIn([npBaseCtrl.Options.AV, "des", "db"], av)) {
                    h.value = h.value.substring(0, h.value.length - 64);
                }

            } else if (av == "key") {
                f.value = f.value.substring(0, f.value.length - 1);

            }
            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
                $formatter.val($formatter.val().substring(0, $formatter.val().length - 1));
                $element.val(npPfsExtension.formatter($element, true));
            }

            if ($element.attr("data-keypad-action") == "amount") {
                $element.val(npCommon.comma($element.val()));
            }

            npConsole.log(npMessage.m67.replace("%p1%", f.name).replace("%p2%", f.value));
        } catch (e) {
        }
    };

    this.resetColor = function (f) {

        if (npCommon.isNull(f)) return;
        var av = attr(f, npBaseCtrl.Options.AN);
        if (npCommon.arrayIn(["re", "sub", "des"], av)) {
            f.style.color = policy.UserColor.ReFieldTextColor;
            f.style.backgroundColor = policy.UserColor.ReFieldBackColor;
        } else if (npCommon.arrayIn([npBaseCtrl.Options.AV, "db"], av) && true) {
            f.style.color = policy.UserColor.FieldTextColor;
            f.style.backgroundColor = policy.UserColor.FieldBackColor;
        }

    };

    this.GetReplaceValue = function (form, field) {
        if (npCommon.isNull(field)) {
            return "";
        }
        var element = (typeof (field) == "object") ? field : npCommon.findElement(field, form);
        var hidden = GetHiddenField(element);
        if (npCommon.isNull(hidden) || npCommon.isNull(hidden.value)) {
            return "";
        }


        if (npKCtrl.isRunnable() && (npVCtrl.isRunning() == false || !npVCtrl.isKeypadUse(field))) {

            return hidden.value;
        }
        return "";
    };

    this.GetReplaceTable = function (form, field) {

        if (npKCtrl.isRunnable() && (npVCtrl.isRunning() == false || !npVCtrl.isKeypadUse(field))) {

            if (npCommon.isNull(npKCtrl.replaceTable)) {
                return;
            }

            var element = field;
            if (typeof (element) == "string") {
                element = npCommon.findElement(field, form);
            }

            if (typeof (element) == "undefined") {
                return "";
            }

            try {
                var av = npQuery(element).attr(npBaseCtrl.Options.AN);
                av = (npCommon.isNull(av)) ? "" : av.toLowerCase();
                if (npCommon.arrayIn(["sub", "des"], av)) {
                    return npKCtrl.ID + "=" + element.name;
                }
                return npKCtrl.replaceTable;
            } catch (e) {
            }

            return npKCtrl.replaceTable;
        }
    };

    this.ReuseModules = function () {
        if (!npKCtrl.isRunnable() && isFinishInit != true) return;
        if (npCommon.isBlank(npKCtrl.e2ekey) && npCommon.isBlank(npKCtrl.uuid)) return;

        npQuery("input[type=text], input[type=password], input[type=tel]").each(function () {
            var element = this;
            var form = this.form;
            var name = npQuery(element).attr("name");

            if (npCommon.isBlank(name) || name == npPfsConst.E2E_RESULT || name == npPfsConst.E2E_UNIQUE || name == npPfsConst.E2E_KEYPAD) {
                npQuery(element).remove();
            }
            if (name.indexOf("__E2E__") > 0 || name.indexOf("__KI_") == 0 || name.indexOf("__KH_") == 0) {
                npQuery(element).remove();
            }
            if (npQuery(element).hasClass("nppfs-npk")) {
                npQuery(element).removeClass("nppfs-npk");
            }
            if (npQuery(element).val().length > 0) {
                npQuery(element).val("");
            }
        });
        npQuery(".nppfs-elements").html("");

        isCompleteStartup = false;
        npKCtrl.init();
        npKCtrl.startup();

    }

    this.addDynamicField = function (form, field) {
        var timeoutid = null;

        function wwait() {
            if (npKCtrl.isComplete() == true) {
                if (!npCommon.isBlank(form)) {
                    if (typeof (form) == "string") {
                        form = npQuery("form[name='" + form + "']").get(0);
                    }
                }
                if (typeof (field) == "string") {
                    field = npCommon.findElement(field, form);
                }
                if (field == null || typeof (field) == "undefined") {
                    return;
                }

                if (!npKCtrl.isRunnable()) {
                    return;
                }

                var av = attr(field, npBaseCtrl.Options.AN);

                // 키보드보안 사용안함/E2E/치환/일반키보드보안 기능에 속하지 않는 필드는 등록 배제
                if (av === "off" || npCommon.arrayNotIn([npBaseCtrl.Options.AV, "re", "sub", "des", "db", "key"], av)) {
                    return;
                }


                npKCtrl.registEachField(form, field);
                npQuery(document).bind("nppfs-npk-jkrf", function (event) {
                    refocus();
                });
            } else {
                timeoutid = setTimeout(wwait, npPfsPolicy.Common.WaitTimeout);
            }
        }

        wwait();
    };


};
w.npPfsModules.define({
    "id": npKCtrl.id
    , "name": "nProtect Online Security V1.0, Key Protection"
    , "handshake": true
    , "endtoend": true
    , "runvirtualos": false
    , "controller": npKCtrl
    , "isExecutable": function (options) {
        return (typeof (options.SK) != "undefined") ? options.SK : true;
        //return npBaseCtrl.Options.SK;
    }
});


w.npFCtrl = new function () {
    this.id = "nppfs.npf.module";

    // 제품별 정책 - 단말정보수집
    var policy = {
        product_uuid: npPacket.PRODUCT_FD
        // 지원범위
        , support: {

            WIN: {
                Support: true,
                Os: {Min: "5.0", Max: "10.0"},
                Bw: {
                    IE: {Support: true, Min: "7.0", Max: "11.0"},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    OP: {Support: true, Min: "18.0"},
                    SF: {Support: true, Min: "5.0"},
                    EG: {Support: true, Min: "12.0"},
                    NC: {Support: false},
                    B360: {Support: true, Min: "7.5"},		// Chrome Version
                    QQ: {Support: true, Min: "38.0"}		// IE Version
                }
            }


            , MAC: {
                Support: true,
                Os: {Min: "10.8", Max: "10.16"},
                Bw: {
                    IE: {Support: false},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    SF: {Support: true, Min: "6.0"},
                    OP: {Support: true, Min: "18.0"}
                }
            }


            , LINUX: {
                Support: true,
                Os: {
                    Fedora: {Support: false},
                    Ubuntu: {Support: false},
                    CentOS: {Support: false},
                    OpenSUSE: {Support: false}
                },
                Bw: {
                    IE: {Support: false},
                    FF: {Support: true, Min: "21.0"},
                    CR: {Support: true, Min: "30.0"},
                    OP: {Support: true, Min: "11.10"},
                    SF: {Support: false}
                }
            }

        }
    };

    var isRunning = false;
    this.isRunning = function () {
        return isRunning;
    };

    this.isRunnable = function () {
        var ret = npBaseCtrl.Options.FD && this.isSupported();
        return ret;
    };

    this.isSupported = function () {
        if (npDefine.isMobileDevice() || npDefine.isMetroUi()) {
            return false;
        }
        return npDefine.isSupported(policy.support);
    };

    var isCompleteStartup = false;
    this.isComplete = function () {
        if (!this.isSupported() || !this.isRunnable()) {
            return true;
        }
        return isCompleteStartup;
    };

    var isFinish = false;
    this.isFinish = function () {
        if (!this.isSupported() || !this.isRunnable()) {
            return true;
        }
        return isFinish;
    };

    var CMD_FD_initParam = "084fb01e428dcff41dd8ebbb5945db39e2f49d906e5d8c896dd8ddffb724b14c";
    var CMD_FD_initFunc = "373374d631b3256e6c16f1c50c8ec4e6ca703efce9545868991454ef89ca228c";
    var CMD_FD_getVersion = "c19ee2df959c2def62a374fd328f8d1f40bac8b534d0e98bc01e6857e2c76c1a";
    var CMD_FD_paramFunc = "b47e5a1016e8658c57ee1e073c87888c33bfd6831bd29a09dcd35d8518128ef7";

    var CMD_FD_putFunc = "2b05ed1fc83ed8315f79148ddafe2b370bdb95f47d183de21a1335c1858b9571";
    var CMD_FD_getFunc = "2b7ffef9dd20d8e935450fc0c1a49b32d621e9a45fff2a11646e19cb367507ac";
    var CMD_FD_finalizeFunc = "46515b9c7703c8533355696295192692598cd9cad996587523711bbf806e31d5";
    var PRM_FD_configParam = "570d821207ef18da3833cc733b824418a7e0b5e5870398d821ae8af78391d40ea8648693403e788c0aa58722cdcd8e743c98dc1ff15127565b808793db71440ae5dd2861411603645a5625214e1a52cad13cc25d4838e3d41ec5a3a9e87a1605e29ff483a1d6debec5f9836553f79916298d708c352d1562ca0db039c35c000caac4aeed2f32aa98a81b019db35aada6d48806b83f374239d4d7c9962550e814ef03e33bd7dc570a2c46c68e7202f2426271c3bd0a24386c8ff1aded09bba9f31ac849984335772e21a14af231ec376bc684ffb36846f594fa3560c606b07caec73148f8891bf1252353d13e9fe8b89053479e08aefb13f60657f40263e7f15ba8c0c5908498a854863a317d73a53f53805ec266d177ef795ac6ba074b429d33";
    var PRM_FD_initETOE = "3dc1210c9ead6260f3315eacd87cae4f8a7d6fb34f4b5956dc7f2b21d7cddccbf84da336bbaf6b1e8a8f28bffe7c8733";

    var PRM_FD_getBorun = "e7b2f1c8fcd6f729384fe82a0231c596a0aaf2d265518eb291ac484457b7afa2";
    var PRM_FD_getETOEKey = "2d86186766325742494b3a74b125b107aa0c8c27212cb51922202629b6c93007";
    var PRM_FD_getTotalLog = "345c72c0a1b5ce87f796eaa730b566f95353a9b802943ed724b7c0f1ad8fc178";
    var PRM_FD_getTotalLogHash = "6cb955dca5065146f1902145cf513e13814cf0cd6bc158040bd54cac134c25d7";

    var PRM_FD_getEncrypt = "777a352208ff200e2c887c4a053a1005408dce22f22b79ec1c593be34065eed7";

    var PRM_FD_putBrowserVersion = "f5e40fbb180ee2eb700876c454574ac35edab1388fd29af6d81ee4aa36527a27";
    var PRM_FD_putFirewallStatus = "bc6933115b87f1241720210ab77c42ebf99ed39a82654e6582a6e8eef992af52";
    var PRM_FD_putSecureKeyStatus = "e53a530cc0ca2f37eb35393390c9238f3a7f798598fe291b737ee71918328a91";
    var PRM_FD_putAdditionalData = "cafe61ee7f2862bb5a7d0c5b15530b41f4be020959c2d3e7cefd916771b49ca8";

    var PRM_FD_getFunc = "2fb8fcd7e937d488d8c8bc4e721cfb069567fbd2ffb3a41763b53e5621fc11bb4a1b18a9fb9840e0824818e5183ef1f46a6b8a4adef18086b4e8133ae5fc827e8254a3f07b3aabb717023890121d6296e229ca74b7884399d77c31b8d2100883b07c839cca763ac3b9c22b002d073f8af2c0b028511c6345f0bb8de5f7d9895750ef2068ba84228b51911786a868e7eead70f4b2837a081af4933c6aca05378336260b1fc05e1e24cd77c98777e65f89f00a7134ca7672008e632936df7414600a1908ae7df79a7f5c828130fb80e23a17cc9b2ae6229b6c22495ed5bbfd9e63f8cffa0f7dbf415166c73b774fafe30e99af08d2533b5322b9c36eb0c832e381a1f0c13029f0109eebfdd46980f94610";

    this.uuid = null;
    this.form = null;
    this.enckey = null;
    this.startTime = null;

    function send(cmd, parameter, plain, o) {
        try {		// 페이지 종료시 오류발생 가능

            var command = npBaseCtrl.makeHeader(policy.product_uuid, npPacket.MODE_SYNC);

            if (!npCommon.isBlank(npFCtrl.uuid)) {
                command.push(npFCtrl.uuid);
            }
            command.push(cmd);

            if (!npCommon.isNull(parameter)) {
                var param = [];
                param.push(npCommon.makeLength(parameter));
                param.push(parameter);
                if (!npCommon.isBlank(plain)) {
                    param.push(plain);
                }

                command.push(npCommon.makeLength(param.join("")));
                command.push(param.join(""));
            }

            // npConsole.log(command.join(""));
            npCommon.sendCommand(command.join(""), o);
        } catch (e) {
        }
        ;
    };


    this.init = function (params) {
        // 이벤트 처리
        npQuery(document).bind("nppfs-npf-jfs nppfs-npf-jfa nppfs-npf-jfi nppfs-npf-jfg nppfs-npf-jfp nppfs-npf-jfb nppfs-npf-jfc", eventHandler);

        this.uuid = npBaseCtrl.uuid;
        this.enckey = npBaseCtrl.enckey;

        if (npCommon.isNull(params) || npCommon.isNull(params["form"])) {
            this.form = npQuery("form").get(0);
        } else {
            this.form = params["form"];
        }
        if (npCommon.isNull(this.form)) {
            npConsole.log(npMessage.m27);
            // 로그수집 마지막 단계로 설정
            npQuery(document).trigger({type: "nppfs-npf-jfc", time: new Date()});
            return;
        }

        var formname = npQuery(this.form).attr("name");
        var forms = npQuery("form[name=" + formname + "]");
        if (forms.length > 1) {
            alert(npMessage.m21.replace("%p%", formname));
            this.form = forms.get(0);
        }


    };

    this.finalize = function () {
        npQuery(document).trigger({
            type: "nppfs-npf-finalized"
            , message: npMessage.m68
            , time: new Date()
        });
    };


    var resultVersion = null;
    var resultLog = null;
    var resultHash = null;
    var resultBuff = null;
    var resultElapsed = null;

    this.startup = function () {
        if (npBaseCtrl.foundPort == false) {
            return;
        }

        if (isCompleteStartup == true) {
            makeElement();
            setValue("i_borun", datamap["gb"]);
            setValue("i_e2e_key", datamap["gk"]);
            setValue("i_log_yn", datamap["sv_y"]);

            setValue("i_e2e_id", npFCtrl.uuid);
            setValue("i_version", resultVersion);
            setValue("i_log_total", resultLog);
            setValue("i_elapsed_tm", resultElapsed);
            setValue("i_tot_hash", resultHash);

            if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
                setValue("i_tot_log", resultBuff);
            }
            return;
        }

        isRunning = true;

        npQuery(document).trigger({
            type: "nppfs-npf-before-startup"
            , message: npMessage.m69
            , time: new Date()
        });

        // 작업 시작
        npQuery(document).trigger({type: "nppfs-npf-jfs", time: new Date()});
    };


    function eventHandler(event) {
        if (npBaseCtrl.terminate == true) {
            return;
        }

        npQuery(document).unbind(event);
        switch (event.type) {
            /* 인자값 전달 */
            case "nppfs-npf-jfs" :
                setParameter();
                break;

            /* 모듈 초기화 */
            case "nppfs-npf-jfa" :
                initModule();
                break;

            /* 수집 데이터 저장 입력양식 생성 및 데이터 수집 */
            case "nppfs-npf-jfi" :
                makeElement();

                gathering();

                isCompleteStartup = true;

                npQuery(document).trigger({type: "nppfs-module-startup", target: npFCtrl.id, time: new Date()});
                npQuery(document).trigger({
                    type: "nppfs-npf-after-startup"
                    , message: npMessage.m70
                    , time: new Date()
                });
                break;

            /* 데이터 가공 */
            case "nppfs-npf-jfg" :
                processing();
                break;

            /* 데이터 바인딩 */
            case "nppfs-npf-jfp" :
                binding();
                break;

            /* 종료 처리 */
            case "nppfs-npf-jfb" :
                clean();

                npQuery(document).trigger({
                    type: "nppfs-npf-complete"
                    , message: npMessage.m71
                    , time: new Date()
                });
                break;

            /* 수집 완료 */
            case "nppfs-npf-jfc" :
                isFinish = true;
                break;
        }
    };

    function setParameter() {
        /* 웹 파라메터 세팅 */
        send(CMD_FD_initParam, PRM_FD_configParam, null, {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    setParameter();
                })) return;
                npQuery(document).trigger({type: "nppfs-npf-jfa", time: new Date()});
            }
        });
    }

    function initModule() {
        var param = PRM_FD_initETOE;
        send(CMD_FD_initFunc, param, npFCtrl.enckey, {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    initModule();
                })) return;
                if (result != "true" && result != "Y") {
                    npConsole.log(npMessage.m29);
                } else {
                    npConsole.log(npMessage.m28);
                }
                npQuery(document).trigger({type: "nppfs-npf-jfi", time: new Date()});
            }
        });
    }

    function makeElement() {
        var arrInputList = ["i_borun", "i_e2e_id", "i_e2e_key", "i_tot_hash", "i_log_total", "i_elapsed_tm", "i_log_yn", "i_version"];
        if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
            arrInputList.push("i_tot_log");
        }


        npCommon.makeElement(npFCtrl.form, arrInputList);

    };

    var datamap = {};
    var remainTask = [];

    function gathering() {
        npFCtrl.startTime = new Date();

        callGatheringFunction("s", "Y", function (result) {
            datamap["sv_y"] = result;
        });
        callGatheringFunction("s", "01", function (result) {
            datamap["sv_01"] = result;
        });
        callGatheringFunction("s", "02", function (result) {
            datamap["sv_02"] = result;
        });
        callGatheringFunction("s", "03", function (result) {
            datamap["sv_03"] = result;
        });

        callGatheringFunction("o", PRM_FD_getBorun, function (result) {
            datamap["gb"] = result;
        });
        callGatheringFunction("o", PRM_FD_getETOEKey, function (result) {
            datamap["gk"] = result;
        });

        // Check Plugin version
        if (npDefine.win) {
            callGatheringFunction("v", "f418b8e9a40a6ab6d8fb7d3fca664e1ce8b365b9528b6b9b2f8175fd4a1ad139", function (result) {
                resultVersion = result;
            });
        } else if (npDefine.lnx) {
            callGatheringFunction("v", "dd261783a58d0465f55db0a927fc65b2ed687d5ce84e7256dfefae74c0ad76d2", function (result) {
                resultVersion = result;
            });
        } else if (npDefine.mac) {
            callGatheringFunction("v", "0344a3947f7aa86a1632a999dc64ae5495bdad3df386ccfa71365453b8dd1b67", function (result) {
                resultVersion = result;
            });
        }
    };

    function processing() {
        callProcessingFunction("pbc", PRM_FD_putBrowserVersion, npDefine.makeBrowserVersionCode());
        callProcessingFunction("pfs", PRM_FD_putFirewallStatus, toPlain(security.getDhack()));
        callProcessingFunction("pss", PRM_FD_putSecureKeyStatus, toPlain(security.getSecuKey()));
        if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.additionalData) == "function") {
            // 단말정보수집 추가 데이터 인터페이스 호출
            var value = npPfsExtension.additionalData();
            var data = npCommon.hexEncode(npCommon.getBytes(value));
            if (!npCommon.isBlank(data)) {
                callProcessingFunction("pad", PRM_FD_putAdditionalData, data);
            }
        }
    };


    function callGatheringFunction(type, param, callback) {
        function doReload(result) {
            remainTask.splice(npCommon.indexOf(remainTask, taskName), 1);

            if (!npCommon.isBlank(result) && npCommon.parseResult(result, function () {
                callGatheringFunction(type, param, callback);
            })) return;

            callback(result);

            if (remainTask.length == 0) {
                npQuery(document).trigger({type: "nppfs-npf-jfg", time: new Date()});
            }
        }


        var taskName = "task_jfg_" + type + "_" + param;
        if (npCommon.indexOf(remainTask, taskName) < 0) {
            remainTask.push(taskName);
        }

        switch (type) {
            case "v" :
                send(CMD_FD_getVersion, param, null, {callback: doReload});
                break;
            case "o" :
                send(CMD_FD_paramFunc, param, null, {callback: doReload});
                break;
            case "s" :
                send(CMD_FD_paramFunc, PRM_FD_getEncrypt, param, {callback: doReload});
                break;
            case "m" :
                send(CMD_FD_multiFunc, PRM_FD_multiFunc, null, {callback: doReload});
                break;
        }
    }

    function clean() {
        send(CMD_FD_finalizeFunc, null, null, {
            callback: function (result) {
                npQuery(document).trigger({type: "nppfs-npf-jfc", time: new Date()});
            }
        });
    };

    function setValue(key, value) {
        try {

            var element = npCommon.findElement(key, npFCtrl.form);

            if (!npCommon.isNull(element)) {
                element.value = value;
            }
        } catch (e) {
            npConsole.log(e);
        }
    };

    function toPlain(data) {
        switch (data) {
            case datamap["sv_02"] :
                return "02";
                break;
            case datamap["sv_03"] :
                return "03";
                break;
        }
        return "01";
    };


    var additionalDataErrorCount = 0;
    var processingTask = [];

    function callProcessingFunction(key, param, plain) {
        var taskName = "task_" + key;
        processingTask.push(taskName);

        send(CMD_FD_putFunc, param, plain, {
            callback: function (result) {
                processingTask.splice(npCommon.indexOf(processingTask, taskName), 1);

                if (result == npPacket.RESULT_FALSE) {
                    npConsole.log("Put Error ... " + key + "...");
                    if (additionalDataErrorCount < 5) {
                        additionalDataErrorCount++;
                        setTimeout(function () {
                            callProcessingFunction(key, param, plain);
                        }, npPfsPolicy.Common.WaitTimeout);
                        return;
                    } else {
                        // 추가 데이터에 대한 함수가 만들어지지 않은 이전 모듈에서는 5대 호출후 계속 문제가 있으면 추가데이터 수집 무시하도록 처리 2016.07.26 by YGKIM
                        additionalDataErrorCount = 0;
                    }
                } else if (result == npPacket.RESULT_DETECT_DEBUG) {
                    npBaseCtrl.showDetectDebug();
                    return;
                }

                if (processingTask.length == 0) {
                    npQuery(document).trigger({type: "nppfs-npf-jfp", time: new Date()});
                }
            }
        });
    }


    function binding() {
        if (npPfsPolicy.Common.ReadyUrl != "") {
            npCommon.send(npPfsPolicy.Common.ReadyUrl, "id=" + npFCtrl.uuid);
        }

        setValue("i_version", resultVersion);
        setValue("i_e2e_id", npFCtrl.uuid);
        setValue("i_borun", datamap["gb"]);
        setValue("i_e2e_key", datamap["gk"]);

        send(CMD_FD_getFunc, PRM_FD_getFunc, null, {
            callback: function (result) {
                if (npCommon.parseResult(result, function () {
                    binding();
                })) return;
                resultLog = result;

                setValue("i_log_yn", datamap["sv_y"]);
                setValue("i_log_total", result);

                function job1() {
                    send(CMD_FD_paramFunc, PRM_FD_getEncrypt, npCommon.getElapsedTime(npFCtrl.startTime), {
                        callback: function (result) {
                            if (npCommon.parseResult(result, function () {
                                job1();
                            })) return;
                            resultElapsed = result;
                            setValue("i_elapsed_tm", result);
                        }
                    });
                };
                job1();

                function job2() {
                    send(CMD_FD_paramFunc, PRM_FD_getTotalLogHash, null, {
                        callback: function (result) {
                            if (npCommon.parseResult(result, function () {
                                job2();
                            })) return;

                            resultHash = result;
                            setValue("i_tot_hash", result);

                            if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_RELEASE) {
                                npQuery(document).trigger({type: "nppfs-npf-jfb", time: new Date()});
                            }
                        }
                    });
                };
                job2();

                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
                    function job3() {
                        send(CMD_FD_paramFunc, PRM_FD_getTotalLog, null, {
                            callback: function (result) {
                                if (npCommon.parseResult(result, function () {
                                    job3();
                                })) return;

                                resultBuff = result;
                                setValue("i_tot_log", result);

                                npQuery(document).trigger({type: "nppfs-npf-jfb", time: new Date()});
                            }
                        });
                    };
                    job3();
                }
            },
            timeout: 30000
        });

    };


    var security = new function () {
        // 개인방화벽 상태
        this.getDhack = function () {

            if (npNCtrl.isRunning()) {
                return datamap["sv_03"];
            } else {
                return datamap["sv_02"];
            }

        };

        // 키보드보안 상태
        this.getSecuKey = function () {

            if (npKCtrl.isRunning()) {
                return datamap["sv_03"];
            } else {
                return datamap["sv_02"];
            }

        };
    };
};

w.npPfsModules.define({
    "id": npFCtrl.id
    , "name": "nProtect Online Security V1.0, Fraud Dection System"
    , "handshake": true
    , "endtoend": true
    , "runvirtualos": true
    , "controller": npFCtrl
    , "isExecutable": function (options) {
        return (typeof (options.FD) != "undefined") ? options.FD : true;
        //return npBaseCtrl.Options.FD;
    }
});


var Randomizer = new function () {
    this.make = function (mode, total, size) {
        var mode = npCommon.isBlank(mode) ? "outer" : ((mode != "inner") ? "outer" : "inner");
        //if(size == 0) return null;
        if (size == 0) return new Array(0);

        var blankndexies = new Array(size);


        var next1 = (mode == "inner") ? Math.floor(Math.random() * (total - 1)) + 1 : Math.floor(Math.random() * (total + 1));


        blankndexies[0] = next1;
        //blankndexies[0] = 4;

        if (size > 1) {
            var jdx = 1;
            while (jdx < size) {

                var next2 = (mode == "inner") ? Math.floor(Math.random() * (total - 1)) + 1 : Math.floor(Math.random() * (total + 1));

                if (this.validation(total, blankndexies, jdx, next2)) {
                    blankndexies[jdx] = next2;
                    jdx++;
                }
            }
        }

        return blankndexies.sort();
    }

    this.indexOf = function (arr, index) {
        var ret = -1;
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == index) return i;
        }

        return ret;
    }

    this.countOf = function (arr, index) {
        var ret = 0;
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] <= index) ret++;
        }

        return ret;
    }

    this.validation = function (total, arr, current, index) {
        var ret = true;
        for (var i = 0; i < current; i++) {
            var a = arr[i];
            if (a == 0 && (index == 0 || index == 1)) return false;
            if (a == total && (index == total || index == total - 1)) return false;
            if (a == index || (a + 1) == index || (a - 1) == index) return false;
        }

        return ret;
    }

    this.random = function (max) {

        return Math.floor(Math.random() * max);

    }


    this.sequenceIndex = function (size) {
        var ret = new Array(size);
        for (var i = 0; i < ret.length; i++) {
            ret[i] = i;
        }
        return ret;
    }
    this.maxiedIndex = function (size) {
        function validation(arr, current, index) {
            var ret = true;
            for (var i = 0; i < current; i++) {
                var a = arr[i];
                if (a == index) return false;
            }

            return ret;
        }

        var ret = [];
        if (size > 1) {
            ret = new Array(size);
            for (var i = 0; i < ret.length; i++) {
                ret[i] = -1;
            }

            var jdx = 0;
            while (jdx < size) {

                var next = Math.floor(Math.random() * size);

                while (!validation(ret, jdx + 1, next)) {
                    next = (next + 1) % size;
                }

                ret[jdx] = next;
                jdx++;
            }
        }

        return ret;
    }
};


var npKeyPadMaker = function (element, opt) {
    this._element = element;
    this._keypadinfo = opt.data.info;
    this._keypaditems = opt.data.items;
    //this._uuid = "nppfs-keypad-" + npQuery(element).attr("name");
    this._uuid = "nppfs-keypad-" + npCommon.eraseSpecialChars(npQuery(element).attr("name"));	// opt.data.info.inputs.inputhash;
    this._isOldIe = false;
    this._isVeryOldIe = false;

    this._parent = (element.form != null) ? element.form : document.body;

    this._useynfield = "";
    this._hashfield = "";
    this._hashelement = "";
    this._useMultiCursor = false;

    this.init = function () {
        var $element = npQuery(this._element);
        var $parent = npQuery(this._parent);
        if ($element.hasClass("nppfs-npv")) {
            return true;
        }
        $element.attr("nppfs-keypad-uuid", this._uuid);

        var msVersion = navigator.userAgent.match(/MSIE ([0-9]{1,}[\.0-9]{0,})/), msie = !!msVersion;
        this._isOldIe = (msie && parseInt(msVersion[1], 10) <= 8) || document.documentMode <= 8;
        this._isVeryOldIe = (msie && parseInt(msVersion[1], 10) <= 7) || document.documentMode <= 7;


        this.makeKeyPadDivHtml($parent);

        var info = this._keypadinfo;
        this._useynfield = info.inputs.useyn;
        this._hashfield = info.inputs.hash;
        this._togglefield = info.inputs.toggle;

        var $div = npQuery(".nppfs-elements", $parent);
        if (!npCommon.isNull(info.dynamic) && info.dynamic.length > 0) {
            for (var i = 0; i < info.dynamic.length; i++) {
                var key = info.dynamic[i].k;
                var val = info.dynamic[i].v;


                if (npQuery("input[name='" + key + "']", $parent).length > 0) continue;

                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.MODE_DEBUG) {
                    $div.append(key + " : <input type=\"text\" name=\"" + key + "\" value=\"" + val + "\" /><br />");
                    if ($element.attr("nppfs-formatter-type") != undefined) {
                        var formatter = $element.attr("name") + "__FORMATTER__";
                        if (npQuery("input[name='" + formatter + "']", $parent).length == 0) {
                            $div.append(formatter + " : <input type=\"text\" name=\"" + formatter + "\" value=\"" + val + "\" /><br />");
                        }
                    }
                } else {
                    $div.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + val + "\" />");
                    if ($element.attr("nppfs-formatter-type") != undefined) {
                        var formatter = $element.attr("name") + "__FORMATTER__";
                        if (npQuery("input[name='" + formatter + "']", $parent).length == 0) {
                            $div.append("<input type=\"hidden\" name=\"" + formatter + "\" value=\"" + val + "\" />");
                        }
                    }
                }
            }
        }
        this._hashelement = npQuery("input[name='" + this._hashfield + "']", $parent);


        // 이니텍 결제 요구사항 : 이미 만들어진 키패드가 있으면 해당 DIV 삭제 후 생 by YGKIM 2016.07.04

        var $prevKeypad = npQuery("#" + this._uuid);
        npConsole.log("이전마우스입력기 기 생성여부 : " + $prevKeypad.length + ", UUID : " + this._uuid);
        if ($prevKeypad.length > 0) {
            $prevKeypad.remove();
        }

        var div = [];
        div.push("<div id=\"" + this._uuid + "\" class=\"nppfs-keypad\" data-width=\"" + info.iw + "\" data-height=\"" + info.ih + "\">");

        this._uuid = this._uuid.replace(".", "\\.");

        div.push("<style type=\"text/css\">");
        div.push("	#" + this._uuid + " .kpd-wrap { position:relative; width:" + info.iw + "px; height:" + info.ih + "px; white-space:normal;}");
        //jh 수정 2019.01.07
        div.push("	#" + this._uuid + " .kpd-preview .preview{ background-repeat:no-repeat; background-position:0px 0px; }");
        div.push("	#" + this._uuid + " .kpd-button { position:absolute; width:" + info.coords.bw + "px; height:" + info.coords.bh + "px; overflow:hidden; /* border:1px solid #f88; */ }");
        if (info.touch.use == true) {
            div.push("	#" + this._uuid + " .kpd-touch .kpd-button { background-color : " + info.touch.color + "; opacity : " + (info.touch.opacity / 100) + "; filter: alpha(opacity=" + info.touch.opacity + "); }");
        }
        div.push("	#" + this._uuid + " .kpd-group { width:" + info.iw + "px; height:" + info.ih + "px; overflow:hidden;}");
        if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
            div.push("	#" + this._uuid + " .kpd-text {" + info.text.style + "position:absolute;width:" + info.text.dw + "px; height:" + info.text.dh + "px; left:" + info.text.dx + "px; top:" + info.text.dy + "px; font-size:" + info.text.spanFontSize + "px;}");
            div.push("	#" + this._uuid + " .textfield {" + info.text.spanStyle + "}");
        }

        div.push("</style>");
        div.push("<div class=\"kpd-wrap " + info.type + "\">");
        var ariaAttr = "";

        div.push("		<img  style=\"position:absolute;left:0;top:0;border:0px;width:100%;height:100%\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAMAAAAoyzS7AAADAFBMVEX///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALI7fhAAAAAXRSTlMAQObYZgAAAAlwSFlzAAAOwwAADsMBx2+oZAAAAApJREFUCJljYAAAAAIAAfRxZKYAAAAASUVORK5CYII=\" alt=\"키패드\" id=\"" + info.keypadUuid + "_bg_img\"" + ariaAttr + "\ /> ");

        if (info.touch.use == true) {
            div.push("<div class=\"kpd-touch\">");
            div.push("	<div class=\"kpd-button touch1\"></div>");
            div.push("	<div class=\"kpd-button touch2\"></div>");
            div.push("</div>");
        }

        if (info.preview.use == true) {
            div.push("<div class=\"kpd-preview\">");
            div.push("	<div class=\"preview " + info.type + "\"></div>");
            div.push("</div>");
        }

        if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
            div.push("<div class=\"kpd-text" + "\" data-left=\"" + info.text.dx + "\" data-top=\"" + info.text.dy + "\" data-width=\"" + info.text.dw + "\" data-height=\"" + info.text.dh + "\" data-font-size=\"" + info.text.spanFontSize + "\">");
            div.push("	<span class=\"textfield\"></span>");
            div.push("</div>");
        }

        var _uuid = this._uuid;
        npQuery(this._keypaditems).each(function (idx) {
            var buttonGroup = this;
            if (idx == 0) {
                div.push("<div class=\"kpd-group " + buttonGroup.id + "\" style=\"position:relative;\">");
            } else {
                div.push("<div class=\"kpd-group " + buttonGroup.id + "\" style=\"position:relative;display:none;\";>");
            }

            var imageMargin = 0;

            if (buttonGroup.id == "upper") {
                imageMargin = info.ih;
            } else if (buttonGroup.id == "special") {
                imageMargin = info.ih * 2;
            } else {
                imageMargin = 0;
            }

            div.push("		<img class=\"kpd-image-button\" style=\"position:absolute;left:0;top:0;border:0px;margin-top:-" + imageMargin + "px;\" aria-hidden=\"true\" /> ");
            npQuery(this.buttons).each(function (jdx) {
                var buttonCoord = this.coord.x1 + "," + this.coord.y1 + "," + this.coord.x2 + "," + this.coord.y2;
                var buttonPreCoord = this.preCoord.x1 + "," + this.preCoord.y1 + "," + this.preCoord.x2 + "," + this.preCoord.y2;

                var label = this.label;
                if (typeof (label) == "undefined" || label == "") {
                    label = "키패드";
                }
                var style = "position: absolute;";
                style += "left: " + this.coord.x1 + "px;";
                style += "top: " + (this.coord.y1 - imageMargin) + "px;";
                style += "width: " + (this.coord.x2 - this.coord.x1) + "px;";
                style += "height: " + (this.coord.y2 - this.coord.y1) + "px;";
                style += "margin-top: " + imageMargin;
                style += "border: 1px solid #ff0000;";


                div.push("		<img class=\"kpd-data\" alt=\"" + label + "\" style=\"" + style + "\" data-coords=\"" + buttonCoord + "\" pre-coords=\"" + buttonPreCoord + "\" precoords=\"" + buttonPreCoord + "\" data-action=\"" + this.action + "\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAMAAAAoyzS7AAADAFBMVEX///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALI7fhAAAAAXRSTlMAQObYZgAAAAlwSFlzAAAOwwAADsMBx2+oZAAAAApJREFUCJljYAAAAAIAAfRxZKYAAAAASUVORK5CYII=\" /> ");

            });

            div.push("</div>");
        });

        div.push("</div>");
        div.push("</div>");

        npQuery("div." + opt.div, npQuery(this._parent)).append(div.join("\n"));

        var $divkeypad = npQuery("#" + this._uuid);


        var currentURL = info.src;
        window.console.log("info.src", info.src);
        var actualURL;
        if (currentURL.split(";jsessionid=").length === 1) {
            actualURL = currentURL;
        } else {
            var frontURL = currentURL.split(";jsessionid=")[0];
            var backURL = currentURL.split(";jsessionid=")[1].split("?")[1];
            var jsessionid = currentURL.split(";jsessionid=")[1].split("?")[0];
            actualURL = frontURL + "?" + backURL;
        }
        npQuery("div.kpd-group img.kpd-image-button", $divkeypad).attr("src", actualURL);
        npQuery(".kpd-preview .preview", $divkeypad).css({"background-image": "url('" + actualURL + "')"});
        //var $divkeypad = npCommon.selectorById(_this._uuid);

        $divkeypad.hide();

        this.bindEvents(opt);

        if (typeof (info.range) != "undefined" && info.range != "") {
            npQuery(".kpd-group", $divkeypad).hide();
            if (info.range.indexOf("lower") >= 0) {
                npQuery(".kpd-group.lower", $divkeypad).show();
            } else if (info.range.indexOf("upper") >= 0) {
                npQuery(".kpd-group.upper", $divkeypad).show();
            } else if (info.range.indexOf("special") >= 0) {
                npQuery(".kpd-group.special", $divkeypad).show();
            } else {
                npQuery(".kpd-group.lower", $divkeypad).show();
            }
        }

        $element.addClass("nppfs-npv");
    };


    this.makeKeyPadDivHtml = function ($parent) {
        var $form = npQuery("form");
        if (npQuery(".nppfs-keypad-div", $parent).length == 0) {
            var html = "<" + "div class=\"nppfs-keypad-div\"></div>";

            if ($form.length == 0) {
                npQuery("body").prepend(html);
            } else {
                $form.each(function () {
                    var form = npQuery(this);
                    npQuery(this).append(html);
                });
            }

        }

        if (npQuery(".nppfs-elements", $parent).length == 0) {
            var html = "<" + "div class=\"nppfs-elements\"><" + "/div>";

            if ($form.length == 0) {
                npQuery("body").prepend(html);
            } else {
                $form.each(function () {
                    var form = npQuery(this);
                    npQuery(this).append(html);
                });
            }

        }

        if (npQuery(".nppfs-keypad-style", npQuery("body")).length == 0) {
            var div = [];
            div.push("<" + "style type=\"text/css\" class=\"nppfs-keypad-style\">");
            div.push("	div.nppfs-keypad-div { position:absolute; display:none; width:0px; height:0px; white-space:normal; overflow:visible;}");
            div.push("	div.nppfs-keypad-wrap { position:absolute; white-space:normal;}");
            div.push("	div.nppfs-keypad { position:relative; margin:0px; z-index:9999; white-space:normal;}");
            div.push("	div.nppfs-keypad .kpd-group { position:relative; z-index:10; width:0px; height:0px; white-space:normal;}");
            div.push("	div.nppfs-keypad .kpd-touch { position:relative; z-index:30; display:none; white-space:normal;}");
            div.push("	div.nppfs-keypad .kpd-preview { position:relative; z-index:40; margin-left:50%; white-space:normal;}");
            div.push("	div.nppfs-keypad .kpd-data { cursor:pointer; }");
            if (!this._useMultiCursor) {
                div.push("	div.nppfs-keypad .kpd-button { cursor:pointer; }");
                div.push("	div.nppfs-keypad .kpd-blank { cursor:default; }");
            }
            div.push("<" + "/style>");
            npQuery("body").prepend(div.join("\n"));
        }
    };


    this.touch = function (element, touchEventMode) { //터치모드 추가 2016.10.27 SJO
        if (npCommon.isNull(element)) return;
        var $element = npQuery(element);
        var action = $element.attr("data-action");
        if (action.indexOf("data:") < 0) {
            return;
        }

        var left1 = $element.css("left");
        var top1 = $element.css("top");
        var width1 = $element.css("width");
        var height1 = $element.css("height");
        var margintop1 = parseInt($element.css("margin-top").replace("px", ""));

        //터치모드 수정 2019.01.09 jh 수정
        var divs = [];
        npQuery(".kpd-data", $element.parent()).each(function () {
            if (npQuery(this).attr("data-action").indexOf("data:") != -1) {
                divs.push(this);
            }
        });

        var ri = (divs.length <= 2) ? 0 : Math.round(Math.random() * (divs.length) * 10) % (divs.length);

        var touch = divs[ri];
        var left2 = npQuery(touch).css("left");
        var top2 = npQuery(touch).css("top");
        var margintop2 = parseInt(npQuery(touch).css("margin-top").replace("px", ""));
        if (left1 == left2 && top1 == top2) {
            ri = (ri + 1) % divs.length;
            touch = divs[ri];
            left2 = npQuery(touch).css("left");
            top2 = npQuery(touch).css("top");
        }
        var width2 = npQuery(touch).css("width");
        var height2 = npQuery(touch).css("height");

        var divtouch = npQuery(".kpd-touch", $element.parent().parent());
        if (touchEventMode != "single") {
            if (margintop1 < 0) {
                top1 = parseInt(top1.replace("px", "")) - (margintop1 * -1) + "px";
            }
            if (margintop2 < 0) {
                top2 = parseInt(top2.replace("px", "")) - (margintop2 * -1) + "px";
            }
        } else {
            if (margintop1 < 0) {
                top1 = parseInt(top1.replace("px", "")) - (margintop1 * -1) + "px";
            }
        }

        npQuery(".kpd-button.touch1", divtouch).css({"left": left1, "top": top1, "width": width1, "height": height1});
        npQuery(".kpd-button.touch2", divtouch).css({"left": left2, "top": top2, "width": width2, "height": height2});
        if (touchEventMode != "single") {		//터치모드 추가 2016.10.27 SJO
            npQuery(".kpd-button.touch2", divtouch).css({
                "left": left2,
                "top": top2,
                "width": width2,
                "height": height2
            });
        } else {
            npQuery(".kpd-button.touch2", divtouch).css({"display": "none"});
        }
        if (this._isOldIe) {
            divtouch.css({opacity: 1}).show();
            setTimeout(function () {
                divtouch.hide();
            }, this._keypadinfo.touch.timeout);
        } else {
            divtouch.stop().animate({opacity: 1}, 1);
            divtouch.show().animate({opacity: 0}, this._keypadinfo.touch.timeout, function () {
                npQuery(this).hide();
            });
        }
    };

    this.preview = function (element) {
        var $element = npQuery(element);
        var action = $element.attr("data-action");
        if (action.indexOf("data:") < 0) {
            return;
        }

        var coords = npQuery(element).attr("preCoords");
        var pairs = coords.split(',');
        var sx = Math.ceil(pairs[0]);
        var sy = Math.ceil(pairs[1]);
        var ex = Math.ceil(pairs[2]);
        var ey = Math.ceil(pairs[3]);
        var viewWidth = ex - sx;
        var viewHeight = ey - sy;

        var previewtop = 4;
        var previewborder = 2;

        var divitem = npQuery(".kpd-preview .preview", $element.parent().parent());
        divitem.css({"width": viewWidth, "height": viewHeight});
        divitem.css({
            "position": "absolute"
            , "left": -1 * divitem.width() / 2
            , "top": previewtop + "px"
            , "width": divitem.width() - previewborder * 2
            , "height": divitem.height() - previewborder * 2
            , "background-position": -(sx + 4 - previewborder) + "px " + -(sy + 4 - previewborder) + "px"
        });

        var divpreview = npQuery(".kpd-preview", $element.parent().parent());
        if (this._isOldIe) {
            divpreview.css({opacity: 1}).show();
            setTimeout(function () {
                divpreview.hide();
            }, 500);
        } else {
            divpreview.stop().animate({opacity: 1}, 1);
            divpreview.show().animate({opacity: 0}, 500, function () {
                npQuery(this).hide();
            });
        }
    };

    this.getBounds = function (obj) {
        var o = npQuery(obj);
        return {left: o.offset().left, top: o.offset().top, width: o.outerWidth(), height: o.outerHeight()}
    };

    this.show = function (options, useyn, input, formname) {
        var _this = this;
        var $element = npQuery(_this._element);
        var $parent = npQuery(_this._parent);
        var $divkeypad = npQuery("#" + _this._uuid, $parent);
        //var $divkeypad = npCommon.selectorById(_this._uuid, $parent);
        var $divtext = npQuery("div.kpd-text", $divkeypad);
        var $inputElement = npQuery(
            '[name="' + options.data.info.inputs.info.split("__KI_")[1] + '"]'
        );


        // 삭제된 키패드일 경우에는 보이기 작업 취소.
        if ($divkeypad == null || $divkeypad.length <= 0) return;

        var $window = npQuery(window);
        var opt = {
            mode: "layer"
            , tw: 0
            , th: 0
            , resize: true
            , resizeRadio: 90
            , position: {x: "default", y: "default", deltax: 0, deltay: 5}
        };

        npQuery.extend(opt, options.data.info);

        npVCtrl.hideAll(_this._uuid);

        if (!_this.isUseYn()) {
            _this.hide();
            return;
        }

        var isVisibleDiv = $divkeypad.is(":visible");
        if (isVisibleDiv == false) {
            npQuery(document).trigger({
                type: "nppfs-npv-before-show"
                , form: (npCommon.isNull(_this._element.form)) ? "" : npQuery(_this._element.form).attr("name")
                , message: npMessage.m90.replace("%p1%", $element.attr("name"))
                , target: _this._element
                , name: $element.attr("name")
                , time: new Date()
            });
        }

        var $div = $divkeypad.parents(".nppfs-keypad-div");
        $div.show();

        if (opt.mode == "layer") {
            //if(opt.mode == "layer") {
            var resized = false;
            var owidth = $divkeypad.attr("data-width");
            var oheight = $divkeypad.attr("data-height");
            var newWidth = owidth;
            var newHeight = oheight;
            if (opt.resize === true && !_this._isOldIe) {
                //if(opt.resize === true) {
                var rate = opt.resizeRadio / 100;
                var bodyWidth = $window.width();
                if (bodyWidth < Math.round(owidth / rate)) {
                    newWidth = Math.round(bodyWidth * rate);
                    rate = newWidth / owidth;
                    newHeight = Math.round(oheight * rate);
                    resized = true;
                    //					console.log('[' + newWidth + '][' + newHeight + '][' + bodyWidth + '][' + rate + ']');

                    // 모바일단말기에서는 화면폭의 비율을 유지하여 확대 처리 by YGKIM @ 2015.07.02
                    //				} else if(npDefine.isMobileDevice() && bodyWidth >= (owidth/" + rate + ")) {
                } else if (npDefine.isMobileDevice() && $window.width() <= $window.height() && bodyWidth >= (owidth / " + rate + ")) {
                    newWidth = Math.round(bodyWidth * rate);
                    rate = newWidth / owidth;
                    newHeight = Math.round(oheight * rate);
                    resized = true;
                } else {
                    newWidth = owidth;
                    newHeight = oheight;
                    rate = 1;
                    resized = false;
                }

                // main div의 폭을 지정할 경우 브라우저의 UI가 깨지는 경우가 있어서 주석처리
                $divkeypad.css({"width": newWidth + "px", "height": newHeight + "px", "overflow": "hidden"});
                npQuery("div.kpd-wrap", $divkeypad).css({
                    "width": newWidth + "px",
                    "height": newHeight + "px",
                    "overflow": "hidden"
                });
                npQuery("div.kpd-wrap .keypad", $divkeypad).css({"background-size": newWidth * 2 + "px " + newHeight + "px"});
                npQuery("div.kpd-wrap .keyboard", $divkeypad).css({"background-size": newWidth * 2 + "px " + newHeight * 3 + "px"});

                npQuery("div.kpd-preview.preview.keypad", $divkeypad).css({"background-size": newWidth * 2 + "px " + newHeight + "px"});
                npQuery("div.kpd-preview.preview.keyboard", $divkeypad).css({"background-size": newWidth * 2 + "px " + newHeight + "px"});

                //npQuery("div.kpd-group.number", $divkeypad).css({"width": newWidth + "px", "height": newHeight + "px"});
                //npQuery("div.kpd-group.lower", $divkeypad).css({"width": newWidth + "px", "height": newHeight + "px"});
                //npQuery("div.kpd-group.upper", $divkeypad).css({"width": newWidth + "px", "height": newHeight + "px"});
                //npQuery("div.kpd-group.special", $divkeypad).css({"width": newWidth + "px", "height": newHeight + "px"});

                npQuery("div.kpd-group.number img.kpd-image-button", $divkeypad).css({
                    "width": newWidth * 2 + "px",
                    "height": newHeight + "px"
                });
                npQuery("div.kpd-group.lower img.kpd-image-button", $divkeypad).css({
                    "width": newWidth * 2 + "px",
                    "height": newHeight * 3 + "px"
                });
                npQuery("div.kpd-group.upper img.kpd-image-button", $divkeypad).css({
                    "width": newWidth * 2 + "px",
                    "height": newHeight * 3 + "px",
                    "margin-top": "-" + newHeight + "px"
                });
                npQuery("div.kpd-group.special img.kpd-image-button", $divkeypad).css({
                    "width": newWidth * 2 + "px",
                    "height": newHeight * 3 + "px",
                    "margin-top": "-" + newHeight * 2 + "px"
                });

                npQuery("div.kpd-group.upper img.kpd-data", $divkeypad).css({"margin-top": "-" + newHeight + "px"});
                npQuery("div.kpd-group.special img.kpd-data", $divkeypad).css({"margin-top": "-" + newHeight * 2 + "px"});

                if (typeof _this._keypadinfo.text != "undefined" && _this._keypadinfo.text.use == true && $element.attr("data-keypad-text") == "on") {
                    npQuery("div.kpd-text", $divkeypad).css({
                        "width": Math.floor($divtext.attr("data-width") * rate) + 1 + "px",
                        "height": Math.floor($divtext.attr("data-height") * rate) + 1 + "px",
                        "top": Math.floor($divtext.attr("data-top") * rate) + "px",
                        "left": Math.floor($divtext.attr("data-left") * rate) + "px",
                        "font-size": Math.floor($divtext.attr("data-font-size") * rate) + "px",
                        "z-index": "20"
                    });
                }

                npQuery("img.kpd-data", npQuery("div.kpd-group", $divkeypad)).each(function () {
                    var coords = npQuery(this).attr("data-coords");
                    var preCoords = npQuery(this).attr("pre-coords");

                    var vals = coords.split(",");
                    npQuery(this).css({
                        "left": parseInt(vals[0] * rate) + "px",
                        "top": parseInt(vals[1] * rate) + "px",
                        "width": parseInt((vals[2] - vals[0]) * rate) + "px",
                        "height": parseInt((vals[3] - vals[1]) * rate) + "px"
                    });

                    vals = preCoords.split(",");
                    newCoords = "";
                    newCoords += parseInt(vals[0] * rate) + ",";
                    newCoords += parseInt(vals[1] * rate) + ",";
                    newCoords += parseInt(vals[2] * rate) + ",";
                    newCoords += parseInt(vals[3] * rate);
                    npQuery(this).attr("preCoords", newCoords);
                });

            }			// End of Resize

            //System.out.println(buffer.toString());
            // 입력양식
            var ele1 = $element;
            var pos1 = _this.getBounds(ele1);

            // 부모 키패드 DIV
            //var ele2 = L.bZ(npKpd" + nsKeypad + "._kpddiv);
            var ele2 = $divkeypad.parents(".nppfs-keypad-div");
            //			ele2.style.border = '2px solid #ff0000';
            var pos2 = _this.getBounds(ele2);

            // 현재 키패드 DIV
            var ele3 = $divkeypad;
            //			ele3.style.border = '2px solid #0000ff';
            var pos3 = _this.getBounds(ele3);
            var kwidth = parseInt(newWidth, 10);
            var kheight = parseInt(newHeight, 10);
            // browser
            var bwidth = $window.width();
            var bheight = $window.height();
            if ("left" === opt.position.x) {
                if (resized) {
                    ele3.css("left", ((bwidth - kwidth) / 2 - pos2.left) + "px");
                } else {
                    ele3.css("left", (opt.position.deltax - pos2.left) + "px");
                }
            } else if ("right" === opt.position.x) {
                if (resized) {
                    ele3.css("left", ((bwidth - kwidth) / 2 - pos2.left) + "px");
                } else {
                    ele3.css("left", (bwidth - kwidth - (opt.position.deltax) - pos2.left) + "px");
                }
            } else if ("center" === opt.position.x) {
                // if (resized) {
                //     ele3.css("left", ((bwidth - kwidth) / 2 - pos2.left) + "px");
                // } else {
                //     ele3.css("left", ((bwidth - kwidth) / 2 + (opt.position.deltax) - pos2.left) + "px");
                // }
                ele3.css(
                    "left",
                    $inputElement.offset().left + opt.position.deltax - pos2.left + "px"
                );
            } else {
                if (resized) {
                    ele3.css("left", ((bwidth - kwidth) / 2 - pos2.left) + "px");
                } else {
                    var posx = pos1.left - pos2.left + (opt.position.deltax);
                    if (posx + kwidth + 10 > bwidth) {
                        posx = bwidth - kwidth - 10;
                    }
                    if (posx < -1 * pos2.left) {
                        posx = -1 * pos2.left;
                    }
                    ele3.css("left", posx + "px");
                }
            }

            if ("top" === opt.position.y) {
                var posy = ($window.scrollTop() + (opt.position.deltay) - pos2.top);
                ele3.css("top", posy + "px");
            } else if ("bottom" === opt.position.y) {
                var posy = ($window.scrollTop() + bheight - kheight - (opt.position.deltay) - pos2.top);
                ele3.css("top", posy + "px");
            } else if ("middle" === opt.position.y) {
                ele3.css("top", ($window.scrollTop() + (bheight - kheight) / 2 + (opt.position.deltay) - pos2.top) + "px");
            } else if ("auto" === opt.position.y) {
                var posy = 0;
                // 입력양식 위치기준 키패드 높이를 합친 하단위치
                var posy1 = (pos1.top + pos1.height + kheight + (opt.position.deltay));
                // 입력양식 위치기준 키패드 높이를 뺀 상단위치
                var posy2 = (pos1.top - kheight - (opt.position.deltay));
                // 스크롤 왼쪽상단
                var posy3 = $window.scrollTop();
                //				console.log('[' + pos1.top + '][' + pos1.height + '][opt.position.deltay]-[' + posy1 + '][' + posy2 + '][' + posy3 + '][' + bheight + '][' + kheight + ']');
                if (posy1 > posy3 + bheight) {
                    if (posy2 < posy3) {
                        posy = ((pos1.top + pos1.height) - pos2.top + (opt.position.deltay));
                    } else {
                        posy = ((pos1.top - kheight) - pos2.top - (opt.position.deltay));
                    }
                } else {
                    posy = ((pos1.top + pos1.height) - pos2.top + (opt.position.deltay));
                }
                ele3.css("top", posy + "px");
                //				console.debug('POS 1 : ' + posy1 + ', ' + posy2 + ', ' + posy3 + ' ');
            } else {
                var posy = ((pos1.top + pos1.height) - pos2.top + (opt.position.deltay));
                ele3.css("top", posy + "px");
            }
            //ele3.style.width = kwidth + \"px\";
        }


        if ($element.attr("data-keypad-action") != "pin") {
            $element.css({"background-color": _this._keypadinfo.color.nbc, "color": _this._keypadinfo.color.nfc});
        }

        if (isVisibleDiv == false) {
            showButtonGroup(_this._uuid, _this._keypadinfo);

            $divkeypad.show();
            npQuery(document).trigger({
                type: "nppfs-npv-after-show"
                , form: (npCommon.isNull(_this._element.form)) ? "" : npQuery(_this._element.form).attr("name")
                , message: npMessage.m91.replace("%p1%", $element.attr("name"))
                , target: _this._element
                , name: $element.attr("name")
            });
        }


    };

    function showButtonGroup(uuid, info) {
        var $divkeypad = npQuery("#" + _this._uuid);
        //var $divkeypad = npCommon.selectorById(_this._uuid);
        if (info.type == "keyboard") {
            npQuery(".kpd-group", $divkeypad).hide();
            if (!npCommon.isBlank(info.range)) {
                if (info.range.indexOf("lower") >= 0) {
                    npQuery(".kpd-group.lower", $divkeypad).show();
                } else if (info.range.indexOf("upper") >= 0) {
                    npQuery(".kpd-group.upper", $divkeypad).show();
                } else if (info.range.indexOf("special") >= 0) {
                    npQuery(".kpd-group.special", $divkeypad).show();
                } else {
                    npQuery(".kpd-group.lower", $divkeypad).show();
                }
            } else {
                npQuery(".kpd-group.lower", $divkeypad).show();
            }
        } else {
            npQuery(".kpd-group", $divkeypad).show();
        }
    }

    this.hide = function () {
        var _this = this;
        var $element = npQuery(_this._element);
        var $parent = npQuery(_this._parent);
        var $divkeypad = npQuery("#" + _this._uuid, $parent);
        //var $divkeypad = npCommon.selectorById(_this._uuid, $parent);
        var isTrigger = $divkeypad.is(":visible");
        // 입력필드 포커스 시 키패드 재배열  -jh 2019.04.18
        if (isTrigger && $element.attr("data-keypad-refresh") == "on") {
            $divkeypad.hide();
            _this.refreshDiv("");
        } else {
            $divkeypad.hide();
        }
        //$divkeypad.parents(".nppfs-keypad-div").hide();


        if ($element.attr("data-keypad-action") != "pin") {
            $element.css({"background-color": _this._keypadinfo.color.fbc, "color": _this._keypadinfo.color.ffc});
        }

        if (!_this.isUseYn() && npKCtrl.isRunning()) {
            npKCtrl.resetColor(_this._element);
        }


        if (typeof ($element.attr("nppfs-readonly")) == "undefined") {
            $element.removeAttr("readonly");
        }


        npQuery(document).trigger({
            type: "nppfs-npv-after-hide"
            , form: (npCommon.isNull(_this._element.form)) ? "" : npQuery(_this._element.form).attr("name")
            , message: npMessage.m92.replace("%p1%", $element.attr("name"))
            , target: _this._element
            , name: $element.attr("name")
            , time: new Date()
        });

        if (isTrigger) {
            $element.trigger({type: 'change'});
        }
    };


    var _this = this;
    this.close = function () {

        if (!npVCtrl.isAbsoluteUse()) {
            _this.setUseYn(false);
        }

        this.hide();
        var evt = {
            type: "nppfs-npv-closed"
            , message: npMessage.m93.replace("%p1%", npQuery(_this._element).attr("name"))
            , target: _this._element
            , name: npQuery(_this._element).attr("name")
            , form: (npCommon.isNull(_this._element.form)) ? "" : npQuery(_this._element.form).attr("name")
            , time: new Date()
        };
        npQuery(document).trigger(evt);
        //evt.type = "nppfs.npv.closed";
        //npQuery(document).trigger(evt);
    };

    this.refreshDiv = function (divid) {
        var info = _this._keypadinfo;

        var params = npVCtrl.prepareKeypad(element.form, element, "s");
        //params = params.replace("m=e&", "");
        //params = "m=s&n=" + info.keypadUuid + "&" + params;
        params += "&n=" + info.keypadUuid;

        //var params = "m=s&u=" + info.keypadUuid;
        npCommon.send(npPfsPolicy.Common.KeyPadUrl, params, {
            async: false,
            callback: function (xhr) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        var data = xhr.responseText
                        var json = npQuery.parseJSON(data);
                        npQuery(element).keypad({data: json});
                        refreshButtonCoords(json, divid);
                    } else {
                        npConsole.log(npMessage.m30);
                    }
                }
            }
        });
    };


    function refreshButtonCoords(opt, divid) {
        var $parent = npQuery(_this._parent);
        var $divkeypad = npQuery("#" + _this._uuid, $parent);
        //var $divkeypad = npCommon.selectorById(_this._uuid, $parent);
        var _uuid = _this._uuid;
        var div = [];
        //jh 수정 2019.01.07
        npQuery(".kpd-preview .preview", $divkeypad).css({"background-image": "url('" + opt.info.src + "')"});
        //npQuery(".kpd-image-button", $divkeypad).attr({"src" : opt.info.src});
        npQuery("div.kpd-group img.kpd-image-button", $divkeypad).attr("src", opt.info.src);
        npQuery(".kpd-group .kpd-data", $divkeypad).remove();

        npQuery(opt.items).each(function (idx) {
            var buttonGroup = this;
            divid = divid == "" ? buttonGroup.id : divid;

            var imageMargin = 0;
            if (buttonGroup.id == "upper") {
                imageMargin = opt.info.ih;
            } else if (buttonGroup.id == "special") {
                imageMargin = opt.info.ih * 2;
            } else {
                imageMargin = 0;
            }

            npQuery(this.buttons).each(function (jdx) {
                var buttonCoord = this.coord.x1 + "," + this.coord.y1 + "," + this.coord.x2 + "," + this.coord.y2;
                var buttonPreCoord = this.preCoord.x1 + "," + this.preCoord.y1 + "," + this.preCoord.x2 + "," + this.preCoord.y2;
                var label = this.label;
                if (typeof (label) == "undefined" || label == "") {
                    label = "키패드";
                }
                var style = "position: absolute;";
                style += "left: " + this.coord.x1 + "px;";
                style += "top: " + (this.coord.y1 - imageMargin) + "px;";
                style += "width: " + (this.coord.x2 - this.coord.x1) + "px;";
                style += "height: " + (this.coord.y2 - this.coord.y1) + "px;";
                //style += "border: 1px solid #ff0000;";


                div.push("		<img class=\"kpd-data\" alt=\"" + label + "\" style=\"" + style + "\" data-coords=\"" + buttonCoord + "\" pre-coords=\"" + buttonPreCoord + "\" precoords=\"" + buttonPreCoord + "\" data-action=\"" + this.action + "\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAMAAAAoyzS7AAADAFBMVEX///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALI7fhAAAAAXRSTlMAQObYZgAAAAlwSFlzAAAOwwAADsMBx2+oZAAAAApJREFUCJljYAAAAAIAAfRxZKYAAAAASUVORK5CYII=\" /> ");

            });

            npQuery(".kpd-group." + buttonGroup.id + "", $divkeypad).append(div.join("\n"));
        });

        var $buttons = npQuery(".kpd-group .kpd-data", $divkeypad);

        if ($buttons != null) {
            if (npDefine.isMobileDevice() && !npDefine.ff) {

                if (npDefine.winmob || npDefine.winphone) {
                    $buttons.on("pointerdown", _this.eventHandler);
                } else {
                    $buttons.on("touchstart", _this.eventHandler);
                }

            } else {
                $buttons.on("click", _this.eventHandler);
            }
        }


    };

    this.eventHandler = function (event) {
        event.stopPropagation();
        event.preventDefault();
        event.stopImmediatePropagation();

        var info = _this._keypadinfo;
        var items = _this._keypaditems;
        var $element = npQuery(_this._element);
        var $hashelement = npQuery(_this._hashelement);
        var $parent = npQuery(_this._parent);
        var $divkeypad = npQuery("#" + _this._uuid, $parent);
        //var $divkeypad = npCommon.selectorById(_this._uuid, $parent);

        var divelement = event.target;
        var action = npQuery(divelement).attr("data-action");
        var textinput = null;


        //터치모드 추가 2016.10.27 SJO
        if (info.touch.use == true && !_this._useMultiCursor) {
            _this.touch(divelement, npCommon.n2b(info.touch.touchEventMode, "default"));
        }

        if (info.preview.use == true && !_this._useMultiCursor) {
            _this.preview(divelement);
        }

        if (action == null || action == "") {
            return;
        }

        if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
            textinput = npQuery("div.kpd-text span.textfield", $divkeypad);
        }

        var maxlength = $element.prop("maxlength");
        if (npCommon.isNull(maxlength)) maxlength = 0;
        if (action.indexOf("action") == 0) {
            if (action.indexOf("show") == 7) {
                var divid = action.substring(12);
                npQuery(".kpd-group", $divkeypad).hide();
                npQuery(".kpd-group." + divid, $divkeypad).show();

            } else if (action.indexOf("hide") == 7) {
                _this.hide();
            } else if (action.indexOf("close") == 7) {
                _this.close();
            } else if (action.indexOf("delete") == 7) {
                if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function" && $element.attr("nppfs-formatter-type") != undefined) {
                    $element.val(npPfsExtension.formatter($element, false));
                }

                if ($element.attr("data-keypad-action") == "amount") {
                    $element.val(npCommon.uncomma($element.val()));
                }

                var ival = $element.val();
                var hval = $hashelement.val();
                if (npVCtrl.rsa == true) {
                    $element.val(ival.substring(0, ival.length - 1));
                    $hashelement.val(hval.substring(0, hval.length - 96));
                    if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                        textinput.text(textinput.text().substring(0, textinput.text().length - 1));
                    }
                } else {
                    $element.val(ival.substring(0, ival.length - 1));
                    $hashelement.val(hval.substring(0, hval.length - 40));
                }

                if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function" && $element.attr("nppfs-formatter-type") != undefined) {
                    $element.val(npPfsExtension.formatter($element, true));
                    var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
                    $formatter.val($formatter.val().substring(0, $formatter.val().length - 1));
                    if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                        textinput.text($element.val());
                    }
                }

                if ($element.attr("data-keypad-action") == "amount") {
                    $element.val(npCommon.comma($element.val()));
                    if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                        textinput.text($element.val());
                    }
                }

                $element.trigger({type: 'keypress', which: 8, keyCode: 8});
                $element.trigger({type: 'keyup', which: 8, keyCode: 8});
            } else if (action.indexOf("clear") == 7) {
                $element.val("");
                $hashelement.val("");
                if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                    textinput.text("");
                }
                if ($element.attr("nppfs-formatter-type") != undefined) {
                    var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
                    $formatter.val("");
                }
                $element.trigger({type: 'keypress', which: 8, keyCode: 8});
                $element.trigger({type: 'keyup', which: 8, keyCode: 8});
            } else if (action.indexOf("enter") == 7) {
                if (info.enter.indexOf("function") == 0) {
                    var jscript = info.enter.substring(9);
                    try {
                        if (window.execScript) { // FOR IE
                            window.execScript(jscript);
                        } else {
                            window["eval"].call(window, jscript);
                        }
                    } catch (e) {
                        npConsole.log(e);
                    }
                } else if (info.enter == "hideall") {
                    npVCtrl.hideAll();
                } else if (info.enter == "hide") {
                    _this.hide();
                } else {
                    if (_this._parent.tagName.toLowerCase() == "form") {
                        _this._parent.submit();
                    }
                }

                npQuery(document).trigger({
                    type: "nppfs-npv-after-enter"
                    , message: npMessage.m94.replace("%p1%", npQuery(_this._element).attr("name"))
                    , target: _this._element
                    , name: npQuery(_this._element).attr("name")
                    , form: (npCommon.isNull(_this._element.form)) ? "" : npQuery(_this._element.form).attr("name")
                    , time: new Date()
                });

            } else if (action.indexOf("refresh") == 7) {
                var divid = action.substring(15);
                _this.refreshDiv(divid);

                npQuery(window).trigger("resize");
            } else if (action.indexOf("link") == 7) {
                var linkArray = action.split("|");
                var linkUrl = "";
                var tmpUrl = [];
                var target = linkArray[1];

                if (linkArray.length > 3) {
                    for (var i = 2; i < linkArray.length; i++) {
                        tmpUrl.push(linkArray[i]);
                    }
                    linkUrl = tmpUrl.join("|");
                } else {
                    linkUrl = linkArray[2];
                }

                window.open(linkUrl, target);
            }
        } else if (action.indexOf("data") == 0) {
            var idx = action.indexOf(":", 5) == -1 ? 45 : action.indexOf(":", 5);
            var value = action.substring(5, idx);
            var disvalue = (idx >= 0 && action.length > idx + 1) ? action.substring(idx + 1) : "*";

            if (value == "korean") {
                disvalue = String.fromCharCode(disvalue);

                var element = _this._element;
                var hidden = _this._hashelement;
                if (disvalue.charCodeAt(0) < 128) {
                    npCommon.val(element, npCommon.val(hidden) + disvalue);
                    npCommon.val(hidden, npCommon.val(hidden) + disvalue);
                } else {
                    var orgValue = npCommon.val(hidden) + disvalue;
                    var arrTmp = npVCtrl.Hangul.splitWord(orgValue, 2);
                    var returnString = npVCtrl.Hangul.composeHangul(arrTmp[1]);
                    //npConsole.log('putHangul 001 : [' + disvalue + '][' + orgValue + '][' + arrTmp[0] + '][' + arrTmp[1] + '][' + returnString + ']');
                    npCommon.val(element, arrTmp[0] + returnString);
                    npCommon.val(hidden, arrTmp[0] + returnString);
                }
                npCommon.val(element, npCommon.val(hidden));
                if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                    textinput.text(npQuery(element).val());
                }
            } else {

                var encvalue = npVCtrl.encrypt(value);


                if (disvalue.indexOf("p") == 0) {
                    disvalue = String.fromCharCode(parseInt(disvalue.substring(1)));
                }

                if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                    $element.val(npPfsExtension.formatter($element, false));
                }

                if ($element.attr("data-keypad-action") == "amount") {
                    $element.val(npCommon.uncomma($element.val()));
                }

                if (maxlength <= 0 || $element.val().length < maxlength) {
                    $element.val($element.val() + disvalue);

                    $hashelement.val($hashelement.val() + encvalue);
                    if ($element.attr("nppfs-formatter-type") != undefined) {
                        var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
                        $formatter.val($formatter.val() + "1");
                    }

                    var keyCode = disvalue.charCodeAt(0);
                    $element.trigger({type: 'keypress', which: keyCode, keyCode: keyCode});
                    $element.trigger({type: 'keyup', which: keyCode, keyCode: keyCode});

                    // 다음 입력양식으로 이동하기
                    var nextelement = $element.attr("data-keypad-next");
                    var datalength = (npVCtrl.rsa == true) ? $hashelement.val().length / 96 : $hashelement.val().length / 40;
                    if (maxlength > 0 && datalength >= maxlength && !npCommon.isBlank(nextelement)) {
                        if (nextelement == "__hide__") {
                            _this.hide();
                        } else if (nextelement == "__doenter__") {
                            if (info.enter.indexOf("function") == 0) {
                                var jscript = info.enter.substring(9);
                                try {
                                    if (window.execScript) { // FOR IE
                                        window.execScript(jscript);
                                    } else {
                                        window["eval"].call(window, jscript);
                                    }
                                } catch (e) {
                                    npConsole.log(e);
                                }
                            } else if (info.enter == "hideall") {
                                npVCtrl.hideAll();
                            } else if (info.enter == "hide") {
                                _this.hide();
                            } else {
                                if (_this._parent.tagName.toLowerCase() == "form") {
                                    _this._parent.submit();
                                }
                            }
                        } else {
                            npQuery("input[name='" + nextelement + "']")[0].focus();
                        }
                    }
                    if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                        if ($element.attr("type") == "password") {
                            textinput.text(textinput.text() + "*");
                        } else {
                            textinput.text(textinput.text() + disvalue);
                        }
                    }
                }

                if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                    $element.val(npPfsExtension.formatter($element, true));
                    if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on" && typeof $element.attr("nppfs-formatter-type") != "undefined") {
                        textinput.text($element.val());
                    }
                }

                if ($element.attr("data-keypad-action") == "amount") {
                    $element.val(npCommon.comma($element.val()));
                    if (typeof info.text != "undefined" && info.text.use == true && $element.attr("data-keypad-text") == "on") {
                        textinput.text($element.val());
                    }
                }
            }


            // Shift 를 사용할 경우 대문자 키패드의 문자를 입력하면 자동으로 소문자키패드로 변경
            if (info.type == "keyboard") {
                if (typeof (info.capslock) != "undefined" && info.capslock == false) {
                    if (typeof (info.shift) != "undefined" && info.shift == true && npQuery(".kpd-group.upper", $divkeypad).css("display") != "none") {
                        if (typeof (info.range) != "undefined" && info.range != "") {
                            npQuery(".kpd-group", $divkeypad).hide();
                            if (info.range.indexOf("lower") >= 0) {
                                npQuery(".kpd-group.lower", $divkeypad).show();
                            } else if (info.range.indexOf("upper") >= 0) {
                                npQuery(".kpd-group.upper", $divkeypad).show();
                            } else if (info.range.indexOf("special") >= 0) {
                                npQuery(".kpd-group.special", $divkeypad).show();
                            } else {
                                npQuery(".kpd-group.lower", $divkeypad).show();
                            }
                        } else {
                            npQuery(".kpd-group", $divkeypad).hide();
                            npQuery(".kpd-group.lower", $divkeypad).show();
                        }
                    }
                }
            }


        } else {

        }

        event.stopPropagation();
        event.preventDefault();
        event.stopImmediatePropagation();
    };

    // Emulate touch Event for image map
    var isTouch = false;
    var npvHandler = "simulated";
    var touch_click_array = {};

    function mouseHandler(event) {
        if (isTouch) {
            if (!event.hasOwnProperty(npvHandler)) {
                var fixed = new npQuery.Event(event);
                fixed.preventDefault();
                fixed.stopPropagation();
            }
        } else {
        }
    }

    function mouseFromTouch(type, event) {
        var newEvent = document.createEvent("MouseEvent");
        newEvent.initMouseEvent(type, true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
        newEvent[npvHandler] = true;

        event.target.dispatchEvent(newEvent);
    }

    function touchHandler(event) {
        if (!event.hasOwnProperty(npvHandler)) {
            var $target = npQuery(event.target);
            if ($target.hasClass("kpd-data")) {
                isTouch = true;
                mouseFromTouch("mousedown", event);
            }
        }
    }

    this.bindEvents = function (opt) {
        var _this = this;
        var $element = npQuery(_this._element);
        var $parent = npQuery(_this._parent);
        var $divkeypad = npQuery("#" + _this._uuid, $parent);
        //var $divkeypad = npCommon.selectorById(_this._uuid, $parent);
        var useyn = $element.attr("data-keypad-useyn-type");
        var input = $element.attr("data-keypad-useyn-input");


        //if(!npKCtrl.isRunning()) {


        if (npDefine.isMobileDevice() && !npDefine.ff) {

            $divkeypad.bind("touchstart", touchHandler);				// Emulate touch Event for image map
            if (npDefine.winmob || npDefine.winphone) {
                npQuery(".kpd-group .kpd-data", $divkeypad).on("pointerdown", this.eventHandler);
            } else {
                npQuery(".kpd-group .kpd-data", $divkeypad).on("touchstart", this.eventHandler);
            }

        } else {
            npQuery(".kpd-group .kpd-data", $divkeypad).on("click", this.eventHandler);
        }


        $element.on("focus", function (event) {
            npVCtrl.hideAll(_this._uuid);

            if (npQuery("#" + _this._uuid).css("display") == "block" && !npDefine.isMobileDevice()) {
                event.preventDefault();
                event.stopPropagation();
                return;
            }

            var useMapping = npCommon.n2b($element.attr("data-keypad-mapping"));
            var useKeypadUi = npCommon.n2b($element.attr("data-keypad-ui"));
            if (useMapping == "true" && useKeypadUi != "true") {
                event.preventDefault();
                event.stopPropagation();
                return;
            }

            if (($element.attr("readonly") == true || $element.attr("readonly") == "readonly") && (typeof (npKCtrl) != "undefined" && npKCtrl.isRunning())) {
                $element.attr("nppfs-readonly", true);
            }

            // 입력양식이 비활성화 상태면 이벤트 처리 중지
            if ($element.prop("disabled") == true) {
                event.preventDefault();
                event.stopPropagation();
                return;
            }
            if ($element.attr("keypad-disabled-ui") == "true") {
                return;
            }

            if (_this.isUseYn()) {
                $element.attr("readonly", true);
                if (useyn != "checkbox" && useyn != "radio") {
                    //useynfield = "useyn-input-" + $element.attr("name");
                    _this.show(opt, useyn, _this._useynfield);
                } else {
                    _this.show(opt, useyn, input);
                }


                $element.blur();

            } else {

                if ($element.attr("nppfs-readonly") != "true") {
                    $element.attr("readonly", false);
                } else {
                    $element.attr("readonly", true);
                }
            }

            if (_this._keypadinfo.focusmode == "clear") {
                if ($element.attr("nppfs-readonly") != "true" && !npVCtrl.isRefresh) {
                    _this.reset();
                }
            }

            npVCtrl.isRefresh = false;

            event.stopPropagation();
            event.preventDefault();
        });

        $element.on("focusout blur", function (event) {
        });

        //}

        // 입력양식에서 값을 입력하였을 경우 특정키(백스페이스 8, 엔터  13) 막기
        $element.on("keydown", function (event) {
            event = (event) ? event : ((typeof (event) != 'undefined') ? event : null);
            var charCode = (event.charCode) ? event.charCode : ((event.keyCode) ? event.keyCode : event.which);
            if (_this.isUseYn() == true) {
                npCommon.stopEvent(event);
            }
        });

        if (useyn == "checkbox" || useyn == "radio") {
            if (npVCtrl.isAbsoluteUse()) {
                npQuery("input[name='" + input + "'][value='Y']").prop("checked", true);
            }
            npQuery("input[name='" + input + "']").on("click", function (event) {

                if ($element.attr("keypad-disabled-ui") == "true") {
                    event.preventDefault();
                    return;
                }

                if (!npQuery("input[name='" + input + "']").hasClass("nppfs-npv")) {
                    npQuery("input[name='" + input + "']").addClass("nppfs-npv");
                }


                // 입력양식이 비활성화 상태면 이벤트 처리 중지
                if ($element.prop("disabled") == true) {
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }
                var visibleKeypad = npCommon.n2b(npQuery("input[name='" + input + "']").attr("data-keypad-focus-field"), "");
                if (npVCtrl.isAbsoluteUse() && ((useyn == "checkbox" && !this.checked) || useyn == "radio" && this.value != "Y")) {
                    alert(npMessage.m88);
                    npQuery("input[name='" + input + "'][value='Y']").prop("checked", true);
                    event.preventDefault();
                } else {
                    if (visibleKeypad != "") {
                        if (visibleKeypad == $element.attr("name")) {
                            _this.show(opt, useyn, input);
                        }
                    } else {
                        _this.show(opt, useyn, input);
                    }

                }

                event.stopPropagation();
                //event.preventDefault();

                $element.attr("readonly", _this.isUseYn());
                _this.setUseYn(_this.isUseYn());
                _this.reset();
            });
        } else if (useyn == "toggle") {
            //var useynfield = "useyn-input-" + $element.attr("name");
            var checkvalue = npVCtrl.isAbsoluteUse() ? "Y" : "N";

            if (npVCtrl.isAbsoluteUse()) {
                $element.attr({
                    "readonly": true,
                    "data-input-useyn-type": "toggle",
                    "data-keypad-useyn-input": _this._useynfield
                });
                _this.reset();
                npQuery(".nppfs-elements", $parent).append("<input type=\"hidden\" name=\"" + _this._useynfield + "\" value=\"Y\" class=\"nppfs-dynamic-field\" />");
                npQuery("#" + input).attr("src", _this._keypadinfo.inputs.toggleon);
            } else {
                $element.attr({
                    "readonly": false,
                    "data-input-useyn-type": "toggle",
                    "data-keypad-useyn-input": _this._useynfield
                });
                npQuery(".nppfs-elements", $parent).append("<input type=\"hidden\" name=\"" + _this._useynfield + "\" value=\"N\" class=\"nppfs-dynamic-field\" />");
                npQuery("#" + input).attr("src", _this._keypadinfo.inputs.toggleoff);
            }

            npQuery("#" + input).css("cursor", "pointer").on("click", function (event) {

                if ($element.attr("keypad-disabled-ui") == "true") {
                    event.preventDefault();
                    return;
                }

                if (!npQuery("#" + input).hasClass("nppfs-npv")) {
                    npQuery("#" + input).addClass("nppfs-npv");
                }
                // 입력양식이 비활성화 상태면 이벤트 처리 중지
                if ($element.prop("disabled") == true) {
                    event.preventDefault();
                    event.stopPropagation();
                    return;
                }
                //var useynfield = "useyn-input-" + $element.attr("name");
                $input = npQuery("input[name='" + _this._useynfield + "']");
                if (npVCtrl.isAbsoluteUse() && $input.val() == "Y") {
                    alert(npMessage.m88);
                } else {
                    if ($input.val() == "Y") {
                        $element.attr("readonly", false);
                        $input.val("N");
                        npQuery(this).attr("src", _this._keypadinfo.inputs.toggleoff);
                    } else {
                        $element.attr("readonly", true);
                        $input.val("Y");
                        npQuery(this).attr("src", _this._keypadinfo.inputs.toggleon);
                    }
                    _this.show(opt, useyn, _this._useynfield);
                    var visibleKeypad = npCommon.n2b(npQuery("#" + input).attr("data-keypad-focus-field"), "");
                    if (visibleKeypad != "") {
                        npQuery("input[name='" + visibleKeypad + "']").focus();
                    }
                }

                $element.attr("readonly", _this.isUseYn());
                _this.setUseYn(_this.isUseYn());
                _this.reset();

                event.stopPropagation();
                //event.preventDefault();
            });
        } else {
            //var useynfield = "useyn-input-" + $element.attr("name");
            var checkvalue = npVCtrl.isAbsoluteUse() ? "Y" : "N";
            $element.attr({
                "readonly": npVCtrl.isAbsoluteUse(),
                "data-input-useyn-type": "toggle",
                "data-keypad-useyn-input": _this._useynfield
            });
            npQuery(".nppfs-elements", $parent).append("<input type=\"hidden\" name=\"" + _this._useynfield + "\" value=\"" + checkvalue + "\" class=\"nppfs-dynamic-field\" />");
        }


        //if(!npDefine.ie || !(npCommon.compareVersion("8.0", npDefine.browserVersion) || document.documentMode <= 8)) {
        //if(!this._isOldIe) {
        if (!this._isVeryOldIe) {
            var $divkeypad = npQuery("#" + _this._uuid);
            //var $divkeypad = npCommon.selectorById(_this._uuid);
            npQuery(window).on("resize scroll", function (event) {
                if ($divkeypad.css("display") != "none") {
                    if (useyn == "checkbox" || useyn == "radio") {
                        _this.show(opt, useyn, input);
                    } else {
                        _this.show(opt, useyn, _this._useynfield);
                    }
                }
                //event.stopPropagation();
                //event.preventDefault();
            });
        }
    }


    this.isUseYn = function () {
        if (npVCtrl.isAbsoluteUse()) {
            return true;
        }

        var _this = this;
        var $element = npQuery(this._element);
        var useyn = $element.attr("data-keypad-useyn-type");
        var input = $element.attr("data-keypad-useyn-input");

        if (useyn == "checkbox") {
            return npQuery("input[name='" + input + "'][value='Y']").prop("checked");
        } else if (useyn == "radio") {
            if (npQuery("input[name='" + input + "'][value='Y']").prop("checked")) {
                return true;
            } else {
                return false;
            }
//			} else if(useyn == "toggle") {
//				$input = npQuery("input[name='" + _this._useynfield + "']");
//				if($input.val() == "Y") {
//					return true;
//				} else {
//					return false;
//				}
        } else {
            $input = npQuery("input[name='" + _this._useynfield + "']");
            if ($input.val() == "Y") {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    this.setUseYn = function (isUse) {
        if (npVCtrl.isAbsoluteUse() && isUse == false) {
            alert(npMessage.m88);
            return;
        }

        var _this = this;
        var $element = npQuery(this._element);
        var useyn = $element.attr("data-keypad-useyn-type");
        var input = $element.attr("data-keypad-useyn-input");

        if (useyn == "checkbox") {
            npQuery("input[name='" + input + "'][value='Y']").prop("checked", isUse);
        } else if (useyn == "radio") {
            npQuery("input[name='" + input + "'][value='Y']").prop("checked", isUse);
            npQuery("input[name='" + input + "'][value='N']").prop("checked", !isUse);
        } else if (useyn == "toggle") {
            input = _this._togglefield;
            $input = npQuery("input[name='" + _this._useynfield + "']");
            if (isUse) {
                $input.val("Y");
                npQuery("#" + input).attr("src", _this._keypadinfo.inputs.toggleon);
            } else {
                $input.val("N");
                npQuery("#" + input).attr("src", _this._keypadinfo.inputs.toggleoff);
            }
        } else {
            npQuery("input[name='" + _this._useynfield + "']").val("Y");
        }

        $element.attr("readonly", _this.isUseYn());
        _this.reset();

        var evt = {
            type: isUse ? "nppfs-npv-enabled" : "nppfs-npv-disabled"
            ,
            message: isUse ? npMessage.m79.replace("%p1%", $element.attr("name")) : npMessage.m80.replace("%p1%", $element.attr("name"))
            ,
            target: this._element
            ,
            name: $element.attr("name")
            ,
            form: npQuery(this._parent).attr("name")
            ,
            time: new Date()
        };
        npQuery(document).trigger(evt);
    }

    this.hash = function () {
        var _this = this;
        var $hashelement = npQuery(_this._hashelement);
        return $hashelement.val();
    }

    this.reset = function () {
        var _this = this;
        var $element = npQuery(_this._element);
        var $hashelement = npQuery(_this._hashelement);
        $element.val("");
        $hashelement.val("");
        if (typeof _this._keypadinfo.text != "undefined" && _this._keypadinfo.text.use == true && $element.attr("data-keypad-text") == "on") {
            npQuery("div.kpd-text span.textfield", npQuery("#" + this._uuid)).text("");
        }
        if ($element.attr("nppfs-formatter-type") != undefined) {
            var $formatter = npQuery("input[name='" + $element.attr("name") + "__FORMATTER__" + "']");
            $formatter.val("");
        }
    }

    this.destroy = function () {
        var _this = this;
        var $element = npQuery(_this._element);
        var $parent = npQuery(_this._parent);
        var info = _this._keypadinfo;

        // 키패드 DIV 삭제
        npQuery("#" + this._uuid).remove();

        // 키패드 동적 필드 삭제
        if (!npCommon.isNull(info.dynamic) && info.dynamic.length > 0) {
            for (var i = 0; i < info.dynamic.length; i++) {
                var key = info.dynamic[i].k;
                npQuery("input[name='" + key + "']", $parent).remove();
            }
        }

        // 동적생성된 활성화 필드 삭제
        var useyn = $element.attr("data-keypad-useyn-type");
        var input = $element.attr("data-keypad-useyn-input");
        var $useyn = npQuery(input);
        if (useyn == "toggle") {
            npQuery("#" + input + ".nppfs-dynamic-field").remove();
        } else {
            npQuery("input[name='" + input + "'].nppfs-dynamic-field").remove();
        }
        $element.removeClass("nppfs-npv");

        var evt = {
            type: "nppfs-npv-destroyed"
            , message: npMessage.m98.replace("%p1%", $element.attr("name"))
            , target: this._element
            , name: $element.attr("name")
            , form: npQuery(this._parent).attr("name")
            , time: new Date()
        };
        npQuery(document).trigger(evt);
    }

    this.init();
};


npQuery.fn.keypad = function (options) {
    var opt = {
        div: "nppfs-keypad-div"
        , data: null
    };

    npQuery.extend(opt, options);

    return this.each(function () {
        if (opt.data == null) {
            return true;
        }
        var maker = new npKeyPadMaker(this, opt);
        npVCtrl.keypadObject.push(maker);
    });
};


w.npVCtrl = new function () {
    this.id = "nppfs.npv.module";

    // 제품별 정책 - 마우스입력기
    var policy = {
        UserColor: {
            OnFieldTextColor: null,
            OnFieldBackColor: null,
            OffFieldTextColor: null,
            OffFieldBackColor: null
        }
    };

    this.uuid = null;
    this.rsaresult = null;
    this.seedkey = null;
    this.rsa = false;

    this.focused = false;
    this.focusedElementName = null;
    this.isRefresh = false;

    this.isRunning = function () {
        return isRunning;
    };

    this.isRunnable = function () {
        return npBaseCtrl.Options.KV;
    };

    this.isSupported = function () {
        return true;
    };

    var isCompleteStartup = false;
    this.isComplete = function () {
        if (!this.isSupported() || !this.isRunnable()) {
            return true;
        }
        return isCompleteStartup;
    };

    var isRunning = false;

    this.init = function () {
        this.uuid = npBaseCtrl.uuid;


        // 이벤트 처리
        npQuery(document).bind("nppfs-npv-jvs nppfs-npv-jvp nppfs-npv-jvi", eventHandler);
    };

    var isTouch = false;
    var npvHandler = "simulated";
    var touch_click_array = {};

    function mouseHandler(event) {
        if (isTouch) {
            if (!event.hasOwnProperty(npvHandler)) {
                var fixed = new npQuery.Event(event);
                fixed.preventDefault();
                fixed.stopPropagation();
            }
        } else {
        }
    }

    function mouseFromTouch(type, event) {
        var newEvent = document.createEvent("MouseEvent");
        newEvent.initMouseEvent(type, true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
        newEvent[npvHandler] = true;

        event.target.dispatchEvent(newEvent);
    }

    function touchHandler(event) {
        if (!event.hasOwnProperty(npvHandler)) {
            var $target = npQuery(event.target);
            if ($target.hasClass("kpd-data")) {
                isTouch = true;
                mouseFromTouch("mousedown", event);
            }
        }
    }

    function eventHandler(event) {
        //npConsole.log("event : " + event.type);
        npQuery(document).unbind(event);

        switch (event.type) {
            // 공개키 파싱 및 암호화 키 생성
            case "nppfs-npv-jvs" :
                makeKey();
                break;

            // 공개키 파싱 후 각 입력양식에 따라 마우스입력기 초기화
            case "nppfs-npv-jvp" :
                registField();
                break;

            // 마우스입력기 초기화 완료 처리
            case "nppfs-npv-jvi" :
                refocus();

                isCompleteStartup = true;

                npQuery(document).trigger({
                    type: "nppfs-npv-after-startup"
                    , message: npMessage.m76
                    , time: new Date()
                });

                npQuery(document).trigger({type: "nppfs-module-startup", target: npVCtrl.id, time: new Date()});


                //if(npDefine.isMobileDevice() && !npDefine.ff){
                //npQuery(".nppfs-keypad").bind("touchstart", touchHandler);
                //}


                break;
        }
    }


    this.startup = function () {
        if (isCompleteStartup == true) {
            this.rescanField();
            return;
        }

        npQuery(document).trigger({
            type: "nppfs-npv-before-startup"
            , message: "마우스입력기를 시작합니다."
            , time: new Date()
        });

        function startup() {
            isRunning = true;
            npQuery(document).trigger({type: "nppfs-npv-jvs", time: new Date()});
        };


        // 키보드보안이 실행가능한 환경인 경우
        if (npKCtrl.isRunnable()) {

            function wwait() {
                if (npKCtrl.isComplete() == true) {
                    startup();
                } else {
                    setTimeout(wwait, npPfsPolicy.Common.WaitTimeout);
                }
            }

            wwait();

        } else {
            startup();
        }

    };

    function makeKey() {
        function parseKey(result, seedkey, is3rd) {
//			if(!npCommon.isNull(npVCtrl.seedkey)){
//				return;
//			}

            try {
                var ppm = null;
                result = npCommon.trim(result);
                if (!npCommon.isBlank(result) && result.length > 64) {
                    var key = result.substring(0, 64);
                    var data = result.substring(64);
                    ppm = npCommon.decrypt(npCommon.hexDecode(data), npCommon.hexDecode(key), "ECB", AES.blockSizeInBits);
                }

                if (npCommon.isBlank(ppm)) {
                    alert(npMessage.m30);
                } else {
                    var pk = npCommon.trim(getPublicKey(("" + ppm)));
                    var rsa = new RSAKey();
                    rsa.setPublic(pk.modulus, pk.encryptionExponent);
                    var enc = rsa.encrypt(seedkey);


                    npVCtrl.rsaresult = enc;
                    npVCtrl.rsa = true;


                    npConsole.log("Enc Key : [" + npVCtrl.rsaresult + "]");


                    npQuery(document).trigger({type: "nppfs-npv-jvp", time: new Date()});

                }
            } catch (e) {
                npVCtrl.rsa = false;
            }
        };

        // Load RSA Public Key
        npCommon.send(npPfsPolicy.Common.KeyPadUrl, "m=p&u=" + npVCtrl.uuid, {
            async: false,
            callback: function (xhr) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        npVCtrl.seedkey = npCommon.hexEncode(npCommon.getRandomBytes(32));
                        parseKey(xhr.responseText, npVCtrl.seedkey, false);
                        window.console.log("xhr.responseText", xhr.responseText)
                        window.console.log("npVCtrl.seedkey", npVCtrl.seedkey)
                    } else {
                        npConsole.log(npMessage.m30);
                        npVCtrl.rsa = false;
                    }
                }
            }
        });


    };

    function refocus() {
        try {
            if (document.hasFocus()) {
                var focusElement = npBaseCtrl.focusElement;
                if (!npCommon.isNull(focusElement)) {
                    focusElement.blur();
                    focusElement.focus();

                    npBaseCtrl.focusElement = null;
                    if (!npCommon.isBlank(focusElement.name)) {
                        npConsole.log(npMessage.m24.replace("%p%", focusElement.name));
                    }
                }
            }
        } catch (e) {
        }
    }


    this.finalize = function () {
        npQuery(document).trigger({
            type: "nppfs-npv-finalized"
            , message: npMessage.m73
            , time: new Date()
        });

        var params = "m=f";
        var result = npCommon.send(npPfsPolicy.Common.KeyPadUrl, params);
        return result;
    };


    this.encrypt = function (data, av) {
        return npCommon.encrypt(data, npCommon.hexDecode(npVCtrl.seedkey), "ECB", AES.blockSizeInBits);
    };


    this.rescanField = function () {
        var timeoutid = null;

        function wwait() {
            if (npVCtrl.isComplete() == true) {
                npQuery(npQuery("form")).each(function (index, value) {
                    if (npQuery(this).hasClass("nppfs-ssm-form")) return true;


                    npCommon.makeElement(value, [npPfsConst.E2E_KEYPAD, npPfsConst.E2E_UNIQUE], [npVCtrl.rsaresult, npVCtrl.uuid]);


                });

                function load() {
                    if (npKCtrl.isComplete() == true) {
                        registField();
                    } else {
                        setTimeout(load, npPfsPolicy.Common.WaitTimeout);
                    }
                }

                load();

            } else {
                timeoutid = setTimeout(wwait, npPfsPolicy.Common.WaitTimeout);
            }
        }

        wwait();
    }

    function putActionItems(element, actionItems) {
        var $keypadObject = npVCtrl.keypadObject;
        npQuery($keypadObject).each(function () {

            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == npQuery(element).attr("name")) {
                this._actionItem = actionItems.action;
            }
        });
    }

    function getActionItems(element, value2) {
        var ret = "";
        var $keypadObject = npVCtrl.keypadObject;

        npQuery($keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == element) {
                var $action = this._actionItem;
                npQuery($action).each(function () {
                    if (this.hasOwnProperty(value2)) {
                        ret = this[value2];
                    }
                });
            }
        });

        if (npCommon.isBlank(ret)) {
            ret = "";
        }
        return ret;
    }

    var remainTask = [];

    function registField() {
        npQuery("input, select, textarea").each(function () {
            var name = npQuery(this).attr("name");
            if (npCommon.isBlank(name)) {
                npQuery(this).attr("name", npQuery(this).attr("id"));
            }
            if (this.tagName.toLowerCase() === "input") {
                var type = npQuery(this).attr("type");
                if (npCommon.isBlank(type)) {
                    npQuery(this).attr("type", "text");
                }
                if (!npCommon.isBlank(type) && type != "text" && type != "password" && type != "tel") {
                    return true;
                }
            }
        });


        var fs = npQuery("form");
        if (fs.length > 0) {
            npQuery(fs).each(function () {
                if (npQuery(this).hasClass("nppfs-ssm-form")) return true;

                npCommon.makeElement(this, [npPfsConst.E2E_KEYPAD, npPfsConst.E2E_UNIQUE], [npVCtrl.rsaresult, npVCtrl.uuid]);

            });
        } else {

            npCommon.makeElement(document.body, [npPfsConst.E2E_KEYPAD, npPfsConst.E2E_UNIQUE], [npVCtrl.rsaresult, npVCtrl.uuid]);

        }

        var keypadParameters = [];
        npQuery("input, select, textarea").each(function () {
            var element = this;
            var name = npQuery(element).attr("name");
            //var name = npQuery(this).attr("name");
            //var element = npCommon.selectorByName(name).get(0);

            //if(this.tagName.toLowerCase() === "textarea" || this.tagName.toLowerCase() === "select"){
            //element=npQuery(document.getElementsByName(name)[0]).get(0);
            //}

            if (this.tagName.toLowerCase() === "textarea" && npQuery(this).hasClass("nppfs-keypad-script")) {
                return true;
            }

            // 이미 생성된 필드인지 확인
            if (npCommon.isBlank(name) || name == npPfsConst.E2E_RESULT || name == npPfsConst.E2E_UNIQUE || name == npPfsConst.E2E_KEYPAD) {
                return true;
            }
            // 이미 생성된 값 필드인지 확인
            if (name.indexOf("__E2E__") > 0 || name.indexOf("__KI_") == 0 || name.indexOf("__KH_") == 0) {
                return true;
            }


            // 이미 등록된 항목
            if (npQuery(element).hasClass("nppfs-npv")) {
                return true;
            }

            if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.formatter) == "function") {
                if (name.indexOf("__FORMATTER__") > 0) {
                    return true;
                }
            }

            if ((element.type != "text" && element.type != "password" && element.type != "tel")) {
                try {
                    if (element.type == "checkbox" || element.type == "radio") {
                        return true;
                    }
                    npQuery(element).focus(function () {
                        npVCtrl.hideAll();
                        npQuery("div.nppfs-keypad").hide();
                    });
                } catch (e) {
                }
                return true;
            }

            // 객체 비활성화
            element.blur();

            var av = attr(element, npBaseCtrl.Options.AN);
            if (npCommon.arrayIn([npBaseCtrl.Options.AV, "db", "re", "sub", "des", "key", "mo"], av)) {
                npQuery(element).attr({"autocomplete": "off"});
                npQuery(document).trigger({
                    type: "nppfs-npv-before-regist-field"
                    , message: npMessage.m75.replace("%p1%", name)
                    , target: element

                    , form: npQuery(element.form).attr("name")

                    , name: name
                    , time: new Date()
                });
                var exectype = attr(element, "npexecutetype");
                if (exectype != "" && exectype.indexOf("v") == -1) {
                    return true;
                }
                var param = npVCtrl.prepareKeypad(element.form, element);
                if (!npCommon.isBlank(param)) {
                    keypadParameters.push({form: element.form, element: element, param: param});
                }
            } else {
                try {
                    npQuery(element).focus(function () {
                        npVCtrl.hideAll();
                    });
                } catch (e) {
                }
            }
        });

        if (keypadParameters.length == 0) {
            npQuery(document).trigger({type: "nppfs-npv-jvi", time: new Date()});
        } else {
            for (var nIndex = 0; nIndex < keypadParameters.length; nIndex++) {
                var params = keypadParameters[nIndex];
                npVCtrl.createKeypad(params.form, params.element, params.param);
            }
        }
    };


    this.keypadObject = [];


//	this.htmlBuffer = [];
    var isShowMessage1 = false;
    var isShowMessage2 = false
    this.prepareKeypad = function (form, element, mode) {
        var keypadConst = {
            TYPE_KEYPAD: "p"
            , TYPE_KEYBOARD: "b"
            , TYPE_KOREAN: "k"
            , ENABLE_FOCUS: "f"
            , ENABLE_RADIO: "r"
            , ENABLE_CHECKBOX: "c"
            , ENABLE_TOGGLE: "t"
            , SHOW_LAYER: "l"
            , SHOW_BLOCK: "b"
            , SHOW_DIVISION: "d"
            , EXEC_PLAIN: "l"
            , EXEC_PASSWORD: "p"
            , EXEC_CONFIRM: "c"
            , EXEC_AMOUNT: "m"
            , EXEC_ACCOUNT: "a"
            , EXEC_NUMBER: "n"
            , EXEC_SAFECARD: "s"
            , EXEC_REPLACE: "r"
        };


        // 현대카드 UI확장기능(라온의 attribute를 NOS attribute로 변경) by YGKIM 2015.09.10
        if (typeof (npPfsExtension) != "undefined" && typeof (npPfsExtension.keypadUiModifier) == "function") {
            //npConsole.log("현대카드 UI확장기능(라온의 attribute를 NOS attribute로 변경) by YGKIM 2015.09.10");
            npPfsExtension.keypadUiModifier(element);
        }

        var prefix = "data-keypad-";
        var useyninput = attr(element, prefix + "useyn");

        var p = [];
        addOptions(p, "m", (!!mode) ? mode : "e");
        addOptions(p, "u", npVCtrl.uuid);
        addOptions(p, "ev", "v4");
        addOptions(p, "d", "nppfs-keypad-div");
        addOptions(p, "jv", "1.13.0");

        var eletype = element.type.toString().toLowerCase();
        var keypadtype = attr(element, prefix + "type") || "num";
        if (keypadtype == "alpha") {
            addOptions(p, "t", keypadConst.TYPE_KEYBOARD);
        } else if (keypadtype == "korean") {
            addOptions(p, "t", keypadConst.TYPE_KOREAN);
        } else {
            addOptions(p, "t", keypadConst.TYPE_KEYPAD);
        }

        // 한글키패드는 암호입력양식에서는 사용할 수 없음
        if (keypadtype == "korean" && eletype == "password") {
            alert(npMessage.m46);
            npConsole.log(npMessage.m46);
            task.setState(npPfsConst.STATE_DONE);
            return;
        }

        var actiontype = attr(element, prefix + "action");
        if (actiontype == "account") {
            addOptions(p, "at", keypadConst.EXEC_ACCOUNT);
        } else if (actiontype == "amount") {
            addOptions(p, "at", keypadConst.EXEC_AMOUNT);
//		} else if(actiontype == "safecard") {
//			addOptions(p, "at", keypadConst.EXEC_SAFECARD);
//		} else if(actiontype == "confirm") {
//			addOptions(p, "at", keypadConst.EXEC_CONFIRM);
        } else if (actiontype == "number") {
            addOptions(p, "at", keypadConst.EXEC_NUMBER);
        } else if (actiontype == "replace") {
            addOptions(p, "at", keypadConst.EXEC_REPLACE);
        } else if (keypadtype == "num" && (eletype == "text" || eletype == "tel")) {
            addOptions(p, "at", keypadConst.EXEC_NUMBER);
        } else if (keypadtype == "alpha" && eletype == "text") {
            addOptions(p, "at", keypadConst.EXEC_PLAIN);
        } else if (actiontype == "password" || actiontype == "pin") {
            addOptions(p, "at", keypadConst.EXEC_PASSWORD);
        } else {
            addOptions(p, "at", keypadConst.EXEC_REPLACE);
        }

        var showtype = attr(element, prefix + "show");
        if (showtype == "div") {
            addOptions(p, "st", keypadConst.SHOW_DIVISION);
            addOptions(p, "dp", "show");
        } else if (showtype == "block") {
            addOptions(p, "st", keypadConst.SHOW_BLOCK);
            addOptions(p, "dp", "hide");
        } else {
            addOptions(p, "st", keypadConst.SHOW_LAYER);
            addOptions(p, "dp", "hide");
        }

        var useyntype = attr(element, prefix + "useyn-type");
        var useyninput = attr(element, prefix + "useyn-input");


        var av = npQuery(element).attr(npBaseCtrl.Options.AN);
        if (av == "mo") {
            if (npCommon.isBlank(useyntype) || npCommon.isBlank(useyninput)) {
                useyntype = "focus";
            }
        } else if (npKCtrl.isRunning()) {

            if (!npCommon.isBlank(useyntype) && npCommon.isBlank(useyninput)) {
                alert(npMessage.m77.replace("%p1%", element.name).replace("%p1%", prefix + "useyn-input"));
            }

            var executetype = attr(element, "npexecutetype");
            if (executetype == "v") {
                useyntype = "focus";
            } else if (npCommon.isBlank(useyntype) || useyntype == "focus" || !npCommon.isBlank(useyntype) && npCommon.isBlank(useyninput)) {
                //npConsole.log("마우스입력기 등록 취소 : " + element.name);
                return;
            }
        } else {
            if (npCommon.isBlank(useyntype) || npCommon.isBlank(useyninput)) {
                useyntype = "focus";
            }
        }


        if (!npCommon.isBlank(useyntype) && useyntype != "focus" && npCommon.isBlank(useyninput)) {
            alert(npMessage.m77.replace("%p1%", element.name).replace("%p1%", prefix + "useyn-input"));
            return;
        }
        //console.log("[" + element.name + "][" + useyntype + "][" + useyninput + "]["+("data-keypad-useyn-input" == prefix + "useyn-input")+"]");

        if (useyntype == "checkbox") {
            //alert(useyninput + "["+("data-keypad-useyn-input" == prefix + "useyn-input")+"]");
            addOptions(p, "ut", keypadConst.ENABLE_CHECKBOX);
            addOptions(p, "ui", useyninput);

            var button = npCommon.findElement(useyninput, form);
            if (npCommon.isNull(button)) {

            }
        } else if (useyntype == "radio") {
            addOptions(p, "ut", keypadConst.ENABLE_RADIO);
            addOptions(p, "ui", useyninput);
        } else if (useyntype == "toggle") {
            addOptions(p, "ut", keypadConst.ENABLE_TOGGLE);
            addOptions(p, "ui", useyninput);
            var toggleActive = attr(element, prefix + "toggle-active");
            var toggleOn = attr(element, prefix + "toggle-on");
            var toggleOff = attr(element, prefix + "toggle-off");

            toggleOn = npCommon.isBlank(toggleOn) ? "/pluginfree/icon/icon_mouse_on.gif" : toggleOn;
            toggleOff = npCommon.isBlank(toggleOff) ? "/pluginfree/icon/icon_mouse_off.gif" : toggleOff;
            addOptions(p, "ta", npCommon.isBlank(toggleActive) ? "false" : toggleActive);
            addOptions(p, "to", toggleOn);
            addOptions(p, "tf", toggleOff);

            var button = npCommon.findElement(useyninput, form);
            if (npCommon.isNull(button)) {

            }
        } else {
            // 활성화방식이 미지정인경우 focus로 지정하여 마우스입력기 강제사용.
            addOptions(p, "ut", keypadConst.ENABLE_FOCUS);
        }

        var inputrange = attr(element, prefix + "input-range");
        if (keypadtype == "alpha" && !npCommon.isBlank(inputrange)) {
            addOptions(p, "ir", inputrange);
        }

        var usepreview = attr(element, prefix + "preview");
        if (!npCommon.isBlank(usepreview)) {
            addOptions(p, "up", usepreview);
        }

        //addOptions(p, "f", npQuery(form).attr("name"));
        addOptions(p, "f", npQuery(form).attr("data-nppfs-form-id"));
        addOptions(p, "i", element.name);
        addOptions(p, "il", attr(element, "maxlength"));
        addOptions(p, "ni", attr(element, prefix + "next"));
        addOptions(p, "th", attr(element, prefix + "theme"));
        addOptions(p, "x", attr(element, prefix + "x"));
        addOptions(p, "y", attr(element, prefix + "y"));
        addOptions(p, "tx", attr(element, prefix + "type-x"));
        addOptions(p, "ty", attr(element, prefix + "type-y"));
        addOptions(p, "w", document.body.offsetWidth);
        addOptions(p, "h", document.body.offsetHeight);
        addOptions(p, "cf", attr(element, prefix + "enter"));

        addOptions(p, "ln", attr(element, prefix + "language"));

        // 2017.01.05 시각 장애인 지원

        addOptions(p, "ar", attr(element, prefix + "aria"));


        // 2018.07.03 데이터 맵핑 pjh -> mapping , ui 변경
        var usemapping = attr(element, prefix + "mapping");
        if (!npCommon.isBlank(usemapping)) {
            addOptions(p, "um", usemapping);
        }

        function makeimageurl() {
            var src = npPfsPolicy.Common.KeyPadUrl;
            if (src.indexOf("http:") != 0 && src.indexOf("https:") != 0 && src.indexOf("//:") != 0) {
                var full = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : "");
                src = full + src;
            }
            return src;
        }

        addOptions(p, "ip", makeimageurl());

        if (this.isAbsoluteUse()) {
            element.readOnly = true;
        }

        var taskName = "task_" + npQuery(element.form).attr("name") + "_" + npQuery(element).attr("name");
        if (npCommon.indexOf(remainTask, taskName) < 0) {
            remainTask.push(taskName);
        }

        return p.join("&");
    };


    this.createKeypad = function (form, element, params) {
        npCommon.send(npPfsPolicy.Common.KeyPadUrl, params, {
            //async : false,
            callback: function (xhr) {
                if (xhr.readyState == 4) {
                    var taskName = "task_" + npQuery(form).attr("name") + "_" + npQuery(element).attr("name");
                    remainTask.splice(npCommon.indexOf(remainTask, taskName), 1);

                    if (xhr.status == 200) {
                        var data = xhr.responseText;
                        if (!npCommon.isBlank(data)) {
                            //npConsole.log(data);

                            var json = npQuery.parseJSON(data);

                            // 서버의 테마속성보다 스크립트에서 사용자가 정의한 정의 색상으로 변경
                            if (!npCommon.isNull(policy) && !npCommon.isNull(policy.UserColor)) {
                                if (!npCommon.isBlank(policy.UserColor.OnFieldTextColor)) json.info.color.nfc = policy.UserColor.OnFieldTextColor;
                                if (!npCommon.isBlank(policy.UserColor.OnFieldBackColor)) json.info.color.nbc = policy.UserColor.OnFieldBackColor;
                                if (!npCommon.isBlank(policy.UserColor.OffFieldTextColor)) json.info.color.ffc = policy.UserColor.OffFieldTextColor;
                                if (!npCommon.isBlank(policy.UserColor.OffFieldBackColor)) json.info.color.fbc = policy.UserColor.OffFieldBackColor;
                            }
                            npQuery(element).keypad({data: json});

                            //동적필드 put 2016.11.23 SJO

                            npPfsCtrl.putDynamicField(form, npQuery(element).attr("name"), [json.info.inputs.info]);

                            npPfsCtrl.putDynamicField(form, npQuery(element).attr("name"), [json.info.inputs.hash]);
                            npPfsCtrl.putDynamicField(form, npQuery(element).attr("name"), [json.info.inputs.useyn]);

                            // 데이터 맵핑 데이터 저장
                            if (!npCommon.isNull(json.actionItems)) {
                                var key = json.actionItems.substring(0, 64);
                                var data = json.actionItems.substring(64);
                                var val = npCommon.decrypt(npCommon.hexDecode(data), npCommon.hexDecode(key), "ECB", AES.blackSizeInBits);
                                var action = JSON.parse(val);
                                putActionItems(element, action);
                            }

                            var executetype = npQuery(element).attr("npexecutetype");
                            if ((typeof (executetype) != "undefined" && executetype.indexOf("k") == -1) && (typeof (npKCtrl) != "undefined" && npKCtrl.isRunning())) {
                                if (executetype == "v") {
                                    npVCtrl.setKeypadUse(npQuery(element).attr("name"), true);
                                }
                            }
                            npQuery(document).trigger({
                                type: "nppfs-npv-after-regist-field"
                                , message: npMessage.m78.replace("%p1%", element.name)
                                , target: element
                                , form: npQuery(form).attr("name")
                                , name: element.name
                                , time: new Date()
                            });
                        }
                    }

                    if (remainTask.length == 0) {
                        npQuery(document).trigger({type: "nppfs-npv-jvi", time: new Date()});
                    }
                }
            }
        });
    };

    function addOptions(a, n, v) {
        if (!npCommon.isBlank(v)) {
            a.push(n + "=" + npCommon.encParam(v));
        }
    };

    function checkValueType(val) {
        var type;
        var regExp1 = /^[A-Z]+$/;
        var regExp2 = /^[a-z]+$/;
        var regExp3 = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@#$%&\\\=\(\'\"]/gi;
        var regExp4 = /^[0-9]*$/;
        if (!npCommon.isBlank(val)) {
            if (val.match(regExp1)) {
                type = "upper";
            } else if (val.match(regExp2)) {
                type = "lower";
            } else if (val.match(regExp3)) {
                type = "special";
            } else if (val.match(regExp4)) {
                type = "number";
            }
        }
        return type;
    };

    var attr = function (element, name, toLowerCase) {
        var value = npQuery(element).attr(name);
        if (!npCommon.isNull(toLowerCase)) toLowerCase = true;
        return npCommon.isBlank(value) ? "" : ((toLowerCase) ? value.toLowerCase() : value);
    }

    this.setColor = function (color) {
        policy.UserColor.OnFieldTextColor = color.OnTextColor;
        policy.UserColor.OnFieldBackColor = color.OnFieldBgColor;
        policy.UserColor.OffFieldTextColor = color.OffTextColor;
        policy.UserColor.OffFieldBackColor = color.OffFieldBgColor;
    };

    this.encryptData = function (name, plain) {
        var element = (typeof (name) == "object") ? name : npQuery("input[name='" + name + "']");
        var parents = (element.form != null) ? element.form : document.body;
        var maxlength = element.attr("maxlength");
        var $hashElement;

        if (npCommon.isNull(element.attr("data-keypad-mapping"))) {
            return;
        }

        //KH element 검색
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == element.attr("name")) {
                $hashElement = this._hashelement;
                var hVal = $hashElement.val();
                if (!npCommon.isBlank(hVal)) {
                    $hashElement.val("");
                    element.val("");
                }
            }
        });

        if (!npCommon.isNull(maxlength) && (plain.length > maxlength)) {
            plain = plain.substring(0, maxlength);
        }

        for (var i = 0; i < plain.length; i++) {
            var val = plain.charAt(i);
            var action = getActionItems(name, val.charCodeAt(0));
            var idx = action.indexOf(":", 5) == -1 ? 45 : action.indexOf(":", 5);
            var value = action.substring(5, idx);
            var disvalue = (idx >= 0 && action.length > idx + 1) ? action.substring(idx + 1) : "*";

            if (value == "korean") {
                disvalue = String.fromCharCode(disvalue);
                var hidden = hashelement;

                if (disvalue.charCodeAt(0) < 128) {
                    npCommon.val(element, npCommon.val(hidden) + disvalue);
                    npCommon.val(hidden, npCommon.val(hidden) + disvalue);
                } else {
                    var orgValue = npCommon.val(hidden) + disvalue;
                    var arrTmp = npVCtrl.Hangul.splitWord(orgValue, 2);
                    var returnString = npVCtrl.Hangul.composeHangul(arrTmp[1]);
                    npConsole.log('putHangul 001 : [' + disvalue + '][' + orgValue + '][' + arrTmp[0] + '][' + arrTmp[1] + '][' + returnString + ']');
                    npCommon.val(element, arrTmp[0] + returnString);
                    npCommon.val(hidden, arrTmp[0] + returnString);
                }
                npCommon.val(element, npCommon.val(hidden));
            } else {
                var encvalue = npVCtrl.encrypt(value);

                if (disvalue.indexOf("p") == 0) {
                    disvalue = String.fromCharCode(parseInt(disvalue.substring(1)));
                }

                element.val(element.val() + disvalue);
                $hashElement.val($hashElement.val() + encvalue);
            }
        }
    };

    this.GetReplaceValue = function (form, field) {
        if (npCommon.isNull(field)) {
            return;
        }
        if (npCommon.isNull(document.getElementsByName(field)[0])) {
            return;
        }

        if (npVCtrl.isRunning() == true && npVCtrl.isKeypadUse(field)) {

            var val = npVCtrl.getKeypadHash(field);

            var ele1 = npCommon.findElement(npPfsConst.E2E_UNIQUE, form);
            var ele2 = npCommon.findElement(npPfsConst.E2E_KEYPAD, form);
            var ele3 = npCommon.findElement("__KI_" + field, form);
//			console.log("[" + ele1.value + "][" + ele2.value + "][" + ele3.value + "]");
            if (npCommon.isNull(ele1) || npCommon.isNull(ele2) || npCommon.isNull(ele3) || npCommon.isNull(ele1.value) || npCommon.isNull(ele2.value) || npCommon.isNull(ele3.value)) {
                return;
            }

            var q = [];
            q.push("m=r");
            q.push("u=" + npCommon.encParam(ele1.value));
            q.push("r=" + npCommon.encParam(ele2.value));
            q.push("k=" + npCommon.encParam(ele3.value));
            q.push("v=" + npCommon.encParam(val));
            var value = npCommon.send(npPfsPolicy.Common.KeyPadUrl, q.join("&"));
            return value;
        }
    };

    this.GetReplaceTable = function (form, field) {
        if (npVCtrl.isRunning() == true && npVCtrl.isKeypadUse(field)) {
            var ele1 = npCommon.findElement(npPfsConst.E2E_UNIQUE, form);
            var ele2 = npCommon.findElement(npPfsConst.E2E_KEYPAD, form);
            var ele3 = npCommon.findElement("__KI_" + field, form);
            if (npCommon.isNull(ele1) || npCommon.isNull(ele2) || npCommon.isNull(ele3) || npCommon.isNull(ele1.value) || npCommon.isNull(ele2.value) || npCommon.isNull(ele3.value)) {
                return;
            }

            var q = [];
            q.push("m=t");
            q.push("u=" + npCommon.encParam(ele1.value));
            q.push("r=" + npCommon.encParam(ele2.value));
            q.push("k=" + npCommon.encParam(ele3.value));
            var value = npCommon.send(npPfsPolicy.Common.KeyPadUrl, q.join("&"));
            return value;
        }
    };

    this.GetEncryptResult = function (form, field) {
        if (npCommon.isNull(field)) {
            return;
        }
        if (npCommon.isNull(document.getElementsByName(field)[0])) {
            return;
        }

        if (npVCtrl.isRunning() == true && npVCtrl.isKeypadUse(field)) {
            var val = npVCtrl.getKeypadHash(field);
            var ele1 = npCommon.findElement(npPfsConst.E2E_UNIQUE, form);
            var ele2 = npCommon.findElement(npPfsConst.E2E_KEYPAD, form);
            var ele3 = npCommon.findElement("__KI_" + field, form);
//			console.log("[" + ele1.value + "][" + ele2.value + "][" + ele3.value + "]");
            if (npCommon.isNull(ele1) || npCommon.isNull(ele2) || npCommon.isNull(ele3) || npCommon.isNull(ele1.value) || npCommon.isNull(ele2.value) || npCommon.isNull(ele3.value)) {
                return;
            }

            var q = [];
            q.push("m=c");
            q.push("u=" + npCommon.encParam(ele1.value));
            q.push("r=" + npCommon.encParam(ele2.value));
            q.push("k=" + npCommon.encParam(ele3.value));
            q.push("v=" + npCommon.encParam(val));
            var value = npCommon.send(npPfsPolicy.Common.KeyPadUrl, q.join("&"));
            return value;
        }
    };

    this.addDynamicField = function (form, field) {
        var timeoutid = null;

        function wwait() {
            if (npVCtrl.isComplete() == true) {
                if (!npCommon.isBlank(form)) {
                    if (typeof (form) == "string") {
                        form = npQuery("form[name='" + form + "']").get(0);
                    }
                }

                if (typeof (field) == "string") {
                    field = npCommon.findElement(field, form);
                }

                if (field == null || typeof (field) == "undefined") {
                    return;
                }

                var av = attr(field, npBaseCtrl.Options.AN);
                if (npCommon.arrayIn([npBaseCtrl.Options.AV, "db", "re", "sub", "des", "key", "mo"], av)) {
                    if (npVCtrl.isRunning() == true) {
                        npVCtrl.startup();
                    }
                }
            } else {
                timeoutid = setTimeout(wwait, npPfsPolicy.Common.WaitTimeout);
            }
        }

        wwait();
    };


    /************************************************************
     * 가상키패드 강제사용정책 적용
     ***********************************************************/
    this.isAbsoluteUse = function () {

        var isRunSecureKey = npKCtrl.isRunning();

        return !isRunSecureKey;
    };

    /************************************************************
     * 키패드 사용/비사용
     ***********************************************************/
    this.setKeypadUse = function (elementname, isUse) {


        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            var $element = npQuery(this._element);
            if (npQuery(this._element).attr("name") == elementname) {
                this.setUseYn(isUse);
                return false;
            }
        });

    };

    this.setKeypadUi = function (elementname, isUse) {
        var $element = npQuery("input[name=" + elementname + "]");
        if (npQuery($element).attr("data-keypad-mapping") == "true") {
            npQuery($element).attr("data-keypad-ui", isUse);
            if (isUse) {
                npQuery($element).focus();
            } else {
                npVCtrl.hideAll();
            }
        }
        return false;
    };


    /************************************************************
     * 키패드 사용여부 확인
     ***********************************************************/
    this.isUseYn = function (elementname, formname) {
        var useyn = false;
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                useyn = this.isUseYn();
                return false;
            }
        });
        return useyn;
    };
    this.isKeypadUse = function (elementid) {
        return this.isUseYn(elementid);
    };

    /************************************************************
     * 키패드 숨기기
     ***********************************************************/
    this.hideKeypad = function (elementname) {
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname) {
                this.hide();
                return true;
            }
        });
    };

    /************************************************************
     * 모든 키패드의 숨기기
     ***********************************************************/
    this.hideAll = function (exclude) {
        npQuery("div.nppfs-div-keypad").hide();
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (this._uuid != exclude) {
                this.hide();
            }
        });
    };

    /************************************************************
     * 키패드 show
     ***********************************************************/
    this.showKeypad = function (elementname, formname) {
        var options = {};

        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname) {
                options["data"] = {info: this._keypadinfo};
                this.show(options, npVCtrl.isKeypadUse(elementname), elementname, formname);
                return true;
            }
        });
    };

    /************************************************************
     * 키패드 stop
     ***********************************************************/
    this.stopKeypad = function (event) {
        npQuery(this.keypadObject).each(function () {
            var $divkeypad = npQuery("#" + this._uuid);
            if ($divkeypad.is(":visible") == true) {
                npVCtrl.closeKeypad(npQuery(this._element).attr("name"));
                npQuery(this._element).focus();

                if (npKCtrl.isRunning() == true) {
                    event.stopPropagation();
                    event.preventDefault();
                    event.stopImmediatePropagation();
                }

            }
        });
    };

    /************************************************************
     * 키패드 중지
     ***********************************************************/
    this.closeKeypad = function (elementname) {
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname) {
                this.close();
                return true;
            }
        });
    };

    /************************************************************
     * 모든 키패드의 기능중지
     ***********************************************************/
    this.closeAll = function (exclude) {
        npQuery("div.nppfs-div-keypad").hide();
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (this._uuid != exclude) {
                this.close();
            }
        });
    };

    /************************************************************
     * 입력된 해쉬값얻기
     ***********************************************************/
    this.getKeypadHash = function (elementname) {
        var ret = false;
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname) {
                ret = this.hash();
                return false;
            }
        });
        return ret;
    };


    /************************************************************
     * 키패드 값초기화
     ***********************************************************/
    this.resetKeypad = function (elementname, formname) {
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                this.reset();
                return false;
            }
        });
    };


    /************************************************************
     * 키패드 완전히 삭제(동적필드삭제, 히든필드삭제)
     ***********************************************************/
    this.destroyKeypad = function (elementname, formname) {
        npQuery(this.keypadObject).each(function (idx) {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                this.destroy();
                npVCtrl.keypadObject.splice(idx, 1);
                return false;
            }
        });
    };

    /************************************************************
     * 키패드 활성화 설정
     ***********************************************************/
    this.enableUI = function (elementname, formname) {
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                npQuery(this._element).attr("keypad-disabled-ui", "false");
                return false;
            }
        });
    };

    /************************************************************
     * 키패드 비 활성화 설정
     ***********************************************************/
    this.disableUI = function (elementname, formname) {
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                npQuery(this._element).attr("keypad-disabled-ui", "true");
                return false;
            }
        });
    };

    /************************************************************
     * 독립 가상 키패드 입력양식 명의 값 읽기
     ***********************************************************/
    this.text = function (elementname, formname) {
        var ret = "";
        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                ret = npQuery(this._element).val();
            }
        });
        return ret;
    };

    /************************************************************
     * 독립 가상 키패드 입력양식 복호화 값 읽기
     ***********************************************************/
    this.value = function (elementname, formname) {
        var ret = "";
        var enval = "";
        var hval = "";
        var obj = null;

        npQuery(this.keypadObject).each(function () {
            if (npCommon.isNull(this)) return true;
            if (npQuery(this._element).attr("name") == elementname && (npCommon.isBlank(formname) || npQuery(this._parent).attr("name") == formname)) {
                obj = this;
            }
        });

        enval = obj._encvalue;
        hval = obj._hval;

        if (npCommon.isBlank(enval) || npCommon.isBlank(hval)) return;

        if (npCommon.sha256(enval) != hval) {
            obj._encvalue = "";
            obj._hval = "";
            obj._element.value = "";

            npConsole.log("intergrity check failed.");
            return "";
        }

        var datalength = enval.length / 32;
        for (var idx = 0; idx < datalength; idx++) {
            var data = enval.substring(idx * 32, (idx + 1) * 32);
            ret = ret + npCommon.decrypt(npCommon.hexDecode(data), npCommon.hexDecode(npVCtrl.standalonekey), "ECB", AES.blockSizeInBits);
        }

        return ret;
    };


    this.Hangul = {
        initial: [12593, 12594, 12596, 12599, 12600, 12601, 12609, 12610, 12611, 12613, 12614, 12615, 12616, 12617, 12618, 12619, 12620, 12621, 12622],
        finale: [0, 12593, 12594, 12595, 12596, 12597, 12598, 12599, 12601, 12602, 12603, 12604, 12605, 12606, 12607, 12608, 12609, 12610, 12612, 12613, 12614, 12615, 12616, 12618, 12619, 12620, 12621, 12622],
        dMedial: [0, 0, 0, 0, 0, 0, 0, 0, 0, 800, 801, 820, 0, 0, 1304, 1305, 1320, 0, 0, 1820],
        dFinale: [0, 0, 0, 119, 0, 422, 427, 0, 0, 801, 816, 817, 819, 825, 826, 827, 0, 0, 1719, 0, 1919],
        SBase: 44032, VCount: 21, LCount: 19, TCount: 28, NCount: 588, VBase: 12623, SCount: 11172,
        composeHangul: function (word) {		//한글 조합
            var wordLength = word.length;
            var firstKeyCode = word.charCodeAt(0);
            var returnValue = word; 						//1자일 경우
            for (var i = 1; i < wordLength; i++) {
                var prevKeyCode = word.charCodeAt(i - 1);
                var curKeyCode = word.charCodeAt(i);
                var prevWord = String.fromCharCode(prevKeyCode);
                var idxLastKey = "";
                var subCurKey = "";
                var subPrevKey = "";
                var idxFirstKey = this.findCode(this.initial, prevKeyCode);
                if (idxFirstKey != -1) {//이전 단어가 초성일 때
                    subCurKey = curKeyCode - this.VBase;
                    if (0 <= subCurKey && subCurKey < this.VCount) {		//현재 단어가 중성일 때
                        combineKeyCode = this.SBase + (idxFirstKey * this.VCount + subCurKey) * this.TCount;
                        returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                        continue;
                    }
                }
                subPrevKey = prevKeyCode - this.SBase;
                if (0 <= subPrevKey && subPrevKey < 11145 && (subPrevKey % this.TCount) == 0) {		//이전 단어가 '히' 전이면  11145 = D789 = '힉';
                    idxLastKey = this.findCode(this.finale, curKeyCode);
                    if (idxLastKey != -1) {
                        combineKeyCode = prevKeyCode + idxLastKey;
                        returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                        continue;
                    }
                    subCurKey = (subPrevKey % this.NCount) / this.TCount;
                    var tmp = this.findCode(this.dMedial, (subCurKey * 100) + (curKeyCode - this.VBase));
                    if (tmp > 0) {
                        combineKeyCode = prevKeyCode + (tmp - subCurKey) * this.TCount;
                        returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                    }
                }
                if (0 <= subPrevKey && subPrevKey < 11172 && (subPrevKey % this.TCount) != 0) {		//이전 단어가 한글의 끝 이면 11172 = D7A4 = ' 힤'
                    idxLastKey = subPrevKey % this.TCount;
                    subCurKey = curKeyCode - this.VBase;
                    if (0 <= subCurKey && subCurKey < this.VCount) {		//현재 단어가 중성일 때
                        curKeyCode = this.findCode(this.initial, this.finale[idxLastKey]);
                        if (0 <= curKeyCode && curKeyCode < this.LCount) {
                            var tmpPrevWord = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(firstKeyCode - idxLastKey);
                            var tmpFirstKeyCode = this.SBase + (curKeyCode * this.VCount + subCurKey) * this.TCount;
                            returnValue = tmpPrevWord + String.fromCharCode(tmpFirstKeyCode);
                            continue;
                        }
                        if (idxLastKey < this.dFinale.length && this.dFinale[idxLastKey] != 0) {
                            var tmpPrevWord = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(firstKeyCode - idxLastKey + Math.floor(this.dFinale[idxLastKey] / 100)); 	//받침빼기
                            var tmp = this.findCode(this.initial, this.finale[(this.dFinale[idxLastKey] % 100)])
                            var tmpFirstKeyCode = this.SBase + (tmp * this.VCount + subCurKey) * this.TCount;
                            returnValue = tmpPrevWord + String.fromCharCode(tmpFirstKeyCode);
                        }
                    }
                    var tmpIdx = this.findCode(this.finale, curKeyCode);
                    var c = this.findCode(this.dFinale, (idxLastKey * 100) + tmpIdx);
                    if (c > 0) {
                        combineKeyCode = prevKeyCode + c - idxLastKey;
                        returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                        continue;
                    }
                }
            }//for end
            return returnValue;
        }, //end combineHangul
        findCode: function (codeList, key) {
            for (var i = 0; i < codeList.length; i++) {
                if (codeList[i] == key) {
                    return i;
                }
            }
            return -1
        },// end findCode
        backSpace: function (word) {
            var wordLength = word.length;
            var returnValue = "";
            var idxFirstKey, idxMiddleKey, idxLastKey;
            for (var i = 0; i < wordLength; i++) {
                var curKeyCode = word.charCodeAt(i);
                var idxCurKey = curKeyCode - this.SBase;
                if (idxCurKey < 0 || idxCurKey >= this.SCount) {
                    returnValue = String.fromCharCode(curKeyCode);
                    continue;
                }
                idxFirstKey = this.initial[Math.floor(idxCurKey / this.NCount)];
                idxMiddleKey = this.VBase + (idxCurKey % this.NCount) / this.TCount;
                idxLastKey = this.finale[idxCurKey % this.TCount];
                returnValue = String.fromCharCode(idxFirstKey, idxMiddleKey);
                if (idxLastKey != 0) {
                    returnValue = returnValue + String.fromCharCode(idxLastKey);
                }
            }
            return returnValue;
        },
        splitWord: function (word, idxSplit) {
            var staticWord = word.substring(0, word.length - idxSplit);
            var tmpWord = word.substring(word.length - idxSplit, word.length);
            var returnArr = new Array(staticWord, tmpWord);
            return returnArr;
        }
    };

};
w.npPfsModules.define({
    "id": npVCtrl.id
    , "name": "nProtect Online Security V1.0, Virtual Keypad"
    , "handshake": false
    , "endtoend": false
    , "runvirtualos": true
    , "controller": npVCtrl
    , "isExecutable": function (options) {
        //return (typeof(options.KV) != "undefined") ? options.KV : true;
        return true;
    }
});


var AES = new function () {
    /*
	 * Rijndael (AES) Encryption Copyright 2005 Herbert Hanewinkel,
	 * www.haneWIN.de version 1.1, check www.haneWIN.de for the latest version
	 *
	 * This software is provided as-is, without express or implied warranty.
	 * Permission to use, copy, modify, distribute or sell this software, with
	 * or without fee, for any purpose and by any individual or organization, is
	 * hereby granted, provided that the above copyright notice and this
	 * paragraph appear in all copies. Distribution as a part of an application
	 * or binary must include the above copyright notice in the documentation
	 * and/or other materials provided with the application or distribution.
	 */

    // The round constants used in subkey expansion
    var Rcon = [0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36,
        0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97,
        0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91];

    // Precomputed lookup table for the SBox
    var S = [99, 124, 119, 123, 242, 107, 111, 197, 48, 1, 103, 43, 254, 215,
        171, 118, 202, 130, 201, 125, 250, 89, 71, 240, 173, 212, 162, 175,
        156, 164, 114, 192, 183, 253, 147, 38, 54, 63, 247, 204, 52, 165,
        229, 241, 113, 216, 49, 21, 4, 199, 35, 195, 24, 150, 5, 154, 7,
        18, 128, 226, 235, 39, 178, 117, 9, 131, 44, 26, 27, 110, 90, 160,
        82, 59, 214, 179, 41, 227, 47, 132, 83, 209, 0, 237, 32, 252, 177,
        91, 106, 203, 190, 57, 74, 76, 88, 207, 208, 239, 170, 251, 67, 77,
        51, 133, 69, 249, 2, 127, 80, 60, 159, 168, 81, 163, 64, 143, 146,
        157, 56, 245, 188, 182, 218, 33, 16, 255, 243, 210, 205, 12, 19,
        236, 95, 151, 68, 23, 196, 167, 126, 61, 100, 93, 25, 115, 96, 129,
        79, 220, 34, 42, 144, 136, 70, 238, 184, 20, 222, 94, 11, 219, 224,
        50, 58, 10, 73, 6, 36, 92, 194, 211, 172, 98, 145, 149, 228, 121,
        231, 200, 55, 109, 141, 213, 78, 169, 108, 86, 244, 234, 101, 122,
        174, 8, 186, 120, 37, 46, 28, 166, 180, 198, 232, 221, 116, 31, 75,
        189, 139, 138, 112, 62, 181, 102, 72, 3, 246, 14, 97, 53, 87, 185,
        134, 193, 29, 158, 225, 248, 152, 17, 105, 217, 142, 148, 155, 30,
        135, 233, 206, 85, 40, 223, 140, 161, 137, 13, 191, 230, 66, 104,
        65, 153, 45, 15, 176, 84, 187, 22];

    var T1 = [0xa56363c6, 0x847c7cf8, 0x997777ee, 0x8d7b7bf6, 0x0df2f2ff,
        0xbd6b6bd6, 0xb16f6fde, 0x54c5c591, 0x50303060, 0x03010102,
        0xa96767ce, 0x7d2b2b56, 0x19fefee7, 0x62d7d7b5, 0xe6abab4d,
        0x9a7676ec, 0x45caca8f, 0x9d82821f, 0x40c9c989, 0x877d7dfa,
        0x15fafaef, 0xeb5959b2, 0xc947478e, 0x0bf0f0fb, 0xecadad41,
        0x67d4d4b3, 0xfda2a25f, 0xeaafaf45, 0xbf9c9c23, 0xf7a4a453,
        0x967272e4, 0x5bc0c09b, 0xc2b7b775, 0x1cfdfde1, 0xae93933d,
        0x6a26264c, 0x5a36366c, 0x413f3f7e, 0x02f7f7f5, 0x4fcccc83,
        0x5c343468, 0xf4a5a551, 0x34e5e5d1, 0x08f1f1f9, 0x937171e2,
        0x73d8d8ab, 0x53313162, 0x3f15152a, 0x0c040408, 0x52c7c795,
        0x65232346, 0x5ec3c39d, 0x28181830, 0xa1969637, 0x0f05050a,
        0xb59a9a2f, 0x0907070e, 0x36121224, 0x9b80801b, 0x3de2e2df,
        0x26ebebcd, 0x6927274e, 0xcdb2b27f, 0x9f7575ea, 0x1b090912,
        0x9e83831d, 0x742c2c58, 0x2e1a1a34, 0x2d1b1b36, 0xb26e6edc,
        0xee5a5ab4, 0xfba0a05b, 0xf65252a4, 0x4d3b3b76, 0x61d6d6b7,
        0xceb3b37d, 0x7b292952, 0x3ee3e3dd, 0x712f2f5e, 0x97848413,
        0xf55353a6, 0x68d1d1b9, 0x00000000, 0x2cededc1, 0x60202040,
        0x1ffcfce3, 0xc8b1b179, 0xed5b5bb6, 0xbe6a6ad4, 0x46cbcb8d,
        0xd9bebe67, 0x4b393972, 0xde4a4a94, 0xd44c4c98, 0xe85858b0,
        0x4acfcf85, 0x6bd0d0bb, 0x2aefefc5, 0xe5aaaa4f, 0x16fbfbed,
        0xc5434386, 0xd74d4d9a, 0x55333366, 0x94858511, 0xcf45458a,
        0x10f9f9e9, 0x06020204, 0x817f7ffe, 0xf05050a0, 0x443c3c78,
        0xba9f9f25, 0xe3a8a84b, 0xf35151a2, 0xfea3a35d, 0xc0404080,
        0x8a8f8f05, 0xad92923f, 0xbc9d9d21, 0x48383870, 0x04f5f5f1,
        0xdfbcbc63, 0xc1b6b677, 0x75dadaaf, 0x63212142, 0x30101020,
        0x1affffe5, 0x0ef3f3fd, 0x6dd2d2bf, 0x4ccdcd81, 0x140c0c18,
        0x35131326, 0x2fececc3, 0xe15f5fbe, 0xa2979735, 0xcc444488,
        0x3917172e, 0x57c4c493, 0xf2a7a755, 0x827e7efc, 0x473d3d7a,
        0xac6464c8, 0xe75d5dba, 0x2b191932, 0x957373e6, 0xa06060c0,
        0x98818119, 0xd14f4f9e, 0x7fdcdca3, 0x66222244, 0x7e2a2a54,
        0xab90903b, 0x8388880b, 0xca46468c, 0x29eeeec7, 0xd3b8b86b,
        0x3c141428, 0x79dedea7, 0xe25e5ebc, 0x1d0b0b16, 0x76dbdbad,
        0x3be0e0db, 0x56323264, 0x4e3a3a74, 0x1e0a0a14, 0xdb494992,
        0x0a06060c, 0x6c242448, 0xe45c5cb8, 0x5dc2c29f, 0x6ed3d3bd,
        0xefacac43, 0xa66262c4, 0xa8919139, 0xa4959531, 0x37e4e4d3,
        0x8b7979f2, 0x32e7e7d5, 0x43c8c88b, 0x5937376e, 0xb76d6dda,
        0x8c8d8d01, 0x64d5d5b1, 0xd24e4e9c, 0xe0a9a949, 0xb46c6cd8,
        0xfa5656ac, 0x07f4f4f3, 0x25eaeacf, 0xaf6565ca, 0x8e7a7af4,
        0xe9aeae47, 0x18080810, 0xd5baba6f, 0x887878f0, 0x6f25254a,
        0x722e2e5c, 0x241c1c38, 0xf1a6a657, 0xc7b4b473, 0x51c6c697,
        0x23e8e8cb, 0x7cdddda1, 0x9c7474e8, 0x211f1f3e, 0xdd4b4b96,
        0xdcbdbd61, 0x868b8b0d, 0x858a8a0f, 0x907070e0, 0x423e3e7c,
        0xc4b5b571, 0xaa6666cc, 0xd8484890, 0x05030306, 0x01f6f6f7,
        0x120e0e1c, 0xa36161c2, 0x5f35356a, 0xf95757ae, 0xd0b9b969,
        0x91868617, 0x58c1c199, 0x271d1d3a, 0xb99e9e27, 0x38e1e1d9,
        0x13f8f8eb, 0xb398982b, 0x33111122, 0xbb6969d2, 0x70d9d9a9,
        0x898e8e07, 0xa7949433, 0xb69b9b2d, 0x221e1e3c, 0x92878715,
        0x20e9e9c9, 0x49cece87, 0xff5555aa, 0x78282850, 0x7adfdfa5,
        0x8f8c8c03, 0xf8a1a159, 0x80898909, 0x170d0d1a, 0xdabfbf65,
        0x31e6e6d7, 0xc6424284, 0xb86868d0, 0xc3414182, 0xb0999929,
        0x772d2d5a, 0x110f0f1e, 0xcbb0b07b, 0xfc5454a8, 0xd6bbbb6d,
        0x3a16162c];

    var T2 = [0x6363c6a5, 0x7c7cf884, 0x7777ee99, 0x7b7bf68d, 0xf2f2ff0d,
        0x6b6bd6bd, 0x6f6fdeb1, 0xc5c59154, 0x30306050, 0x01010203,
        0x6767cea9, 0x2b2b567d, 0xfefee719, 0xd7d7b562, 0xabab4de6,
        0x7676ec9a, 0xcaca8f45, 0x82821f9d, 0xc9c98940, 0x7d7dfa87,
        0xfafaef15, 0x5959b2eb, 0x47478ec9, 0xf0f0fb0b, 0xadad41ec,
        0xd4d4b367, 0xa2a25ffd, 0xafaf45ea, 0x9c9c23bf, 0xa4a453f7,
        0x7272e496, 0xc0c09b5b, 0xb7b775c2, 0xfdfde11c, 0x93933dae,
        0x26264c6a, 0x36366c5a, 0x3f3f7e41, 0xf7f7f502, 0xcccc834f,
        0x3434685c, 0xa5a551f4, 0xe5e5d134, 0xf1f1f908, 0x7171e293,
        0xd8d8ab73, 0x31316253, 0x15152a3f, 0x0404080c, 0xc7c79552,
        0x23234665, 0xc3c39d5e, 0x18183028, 0x969637a1, 0x05050a0f,
        0x9a9a2fb5, 0x07070e09, 0x12122436, 0x80801b9b, 0xe2e2df3d,
        0xebebcd26, 0x27274e69, 0xb2b27fcd, 0x7575ea9f, 0x0909121b,
        0x83831d9e, 0x2c2c5874, 0x1a1a342e, 0x1b1b362d, 0x6e6edcb2,
        0x5a5ab4ee, 0xa0a05bfb, 0x5252a4f6, 0x3b3b764d, 0xd6d6b761,
        0xb3b37dce, 0x2929527b, 0xe3e3dd3e, 0x2f2f5e71, 0x84841397,
        0x5353a6f5, 0xd1d1b968, 0x00000000, 0xededc12c, 0x20204060,
        0xfcfce31f, 0xb1b179c8, 0x5b5bb6ed, 0x6a6ad4be, 0xcbcb8d46,
        0xbebe67d9, 0x3939724b, 0x4a4a94de, 0x4c4c98d4, 0x5858b0e8,
        0xcfcf854a, 0xd0d0bb6b, 0xefefc52a, 0xaaaa4fe5, 0xfbfbed16,
        0x434386c5, 0x4d4d9ad7, 0x33336655, 0x85851194, 0x45458acf,
        0xf9f9e910, 0x02020406, 0x7f7ffe81, 0x5050a0f0, 0x3c3c7844,
        0x9f9f25ba, 0xa8a84be3, 0x5151a2f3, 0xa3a35dfe, 0x404080c0,
        0x8f8f058a, 0x92923fad, 0x9d9d21bc, 0x38387048, 0xf5f5f104,
        0xbcbc63df, 0xb6b677c1, 0xdadaaf75, 0x21214263, 0x10102030,
        0xffffe51a, 0xf3f3fd0e, 0xd2d2bf6d, 0xcdcd814c, 0x0c0c1814,
        0x13132635, 0xececc32f, 0x5f5fbee1, 0x979735a2, 0x444488cc,
        0x17172e39, 0xc4c49357, 0xa7a755f2, 0x7e7efc82, 0x3d3d7a47,
        0x6464c8ac, 0x5d5dbae7, 0x1919322b, 0x7373e695, 0x6060c0a0,
        0x81811998, 0x4f4f9ed1, 0xdcdca37f, 0x22224466, 0x2a2a547e,
        0x90903bab, 0x88880b83, 0x46468cca, 0xeeeec729, 0xb8b86bd3,
        0x1414283c, 0xdedea779, 0x5e5ebce2, 0x0b0b161d, 0xdbdbad76,
        0xe0e0db3b, 0x32326456, 0x3a3a744e, 0x0a0a141e, 0x494992db,
        0x06060c0a, 0x2424486c, 0x5c5cb8e4, 0xc2c29f5d, 0xd3d3bd6e,
        0xacac43ef, 0x6262c4a6, 0x919139a8, 0x959531a4, 0xe4e4d337,
        0x7979f28b, 0xe7e7d532, 0xc8c88b43, 0x37376e59, 0x6d6ddab7,
        0x8d8d018c, 0xd5d5b164, 0x4e4e9cd2, 0xa9a949e0, 0x6c6cd8b4,
        0x5656acfa, 0xf4f4f307, 0xeaeacf25, 0x6565caaf, 0x7a7af48e,
        0xaeae47e9, 0x08081018, 0xbaba6fd5, 0x7878f088, 0x25254a6f,
        0x2e2e5c72, 0x1c1c3824, 0xa6a657f1, 0xb4b473c7, 0xc6c69751,
        0xe8e8cb23, 0xdddda17c, 0x7474e89c, 0x1f1f3e21, 0x4b4b96dd,
        0xbdbd61dc, 0x8b8b0d86, 0x8a8a0f85, 0x7070e090, 0x3e3e7c42,
        0xb5b571c4, 0x6666ccaa, 0x484890d8, 0x03030605, 0xf6f6f701,
        0x0e0e1c12, 0x6161c2a3, 0x35356a5f, 0x5757aef9, 0xb9b969d0,
        0x86861791, 0xc1c19958, 0x1d1d3a27, 0x9e9e27b9, 0xe1e1d938,
        0xf8f8eb13, 0x98982bb3, 0x11112233, 0x6969d2bb, 0xd9d9a970,
        0x8e8e0789, 0x949433a7, 0x9b9b2db6, 0x1e1e3c22, 0x87871592,
        0xe9e9c920, 0xcece8749, 0x5555aaff, 0x28285078, 0xdfdfa57a,
        0x8c8c038f, 0xa1a159f8, 0x89890980, 0x0d0d1a17, 0xbfbf65da,
        0xe6e6d731, 0x424284c6, 0x6868d0b8, 0x414182c3, 0x999929b0,
        0x2d2d5a77, 0x0f0f1e11, 0xb0b07bcb, 0x5454a8fc, 0xbbbb6dd6,
        0x16162c3a];

    var T3 = [0x63c6a563, 0x7cf8847c, 0x77ee9977, 0x7bf68d7b, 0xf2ff0df2,
        0x6bd6bd6b, 0x6fdeb16f, 0xc59154c5, 0x30605030, 0x01020301,
        0x67cea967, 0x2b567d2b, 0xfee719fe, 0xd7b562d7, 0xab4de6ab,
        0x76ec9a76, 0xca8f45ca, 0x821f9d82, 0xc98940c9, 0x7dfa877d,
        0xfaef15fa, 0x59b2eb59, 0x478ec947, 0xf0fb0bf0, 0xad41ecad,
        0xd4b367d4, 0xa25ffda2, 0xaf45eaaf, 0x9c23bf9c, 0xa453f7a4,
        0x72e49672, 0xc09b5bc0, 0xb775c2b7, 0xfde11cfd, 0x933dae93,
        0x264c6a26, 0x366c5a36, 0x3f7e413f, 0xf7f502f7, 0xcc834fcc,
        0x34685c34, 0xa551f4a5, 0xe5d134e5, 0xf1f908f1, 0x71e29371,
        0xd8ab73d8, 0x31625331, 0x152a3f15, 0x04080c04, 0xc79552c7,
        0x23466523, 0xc39d5ec3, 0x18302818, 0x9637a196, 0x050a0f05,
        0x9a2fb59a, 0x070e0907, 0x12243612, 0x801b9b80, 0xe2df3de2,
        0xebcd26eb, 0x274e6927, 0xb27fcdb2, 0x75ea9f75, 0x09121b09,
        0x831d9e83, 0x2c58742c, 0x1a342e1a, 0x1b362d1b, 0x6edcb26e,
        0x5ab4ee5a, 0xa05bfba0, 0x52a4f652, 0x3b764d3b, 0xd6b761d6,
        0xb37dceb3, 0x29527b29, 0xe3dd3ee3, 0x2f5e712f, 0x84139784,
        0x53a6f553, 0xd1b968d1, 0x00000000, 0xedc12ced, 0x20406020,
        0xfce31ffc, 0xb179c8b1, 0x5bb6ed5b, 0x6ad4be6a, 0xcb8d46cb,
        0xbe67d9be, 0x39724b39, 0x4a94de4a, 0x4c98d44c, 0x58b0e858,
        0xcf854acf, 0xd0bb6bd0, 0xefc52aef, 0xaa4fe5aa, 0xfbed16fb,
        0x4386c543, 0x4d9ad74d, 0x33665533, 0x85119485, 0x458acf45,
        0xf9e910f9, 0x02040602, 0x7ffe817f, 0x50a0f050, 0x3c78443c,
        0x9f25ba9f, 0xa84be3a8, 0x51a2f351, 0xa35dfea3, 0x4080c040,
        0x8f058a8f, 0x923fad92, 0x9d21bc9d, 0x38704838, 0xf5f104f5,
        0xbc63dfbc, 0xb677c1b6, 0xdaaf75da, 0x21426321, 0x10203010,
        0xffe51aff, 0xf3fd0ef3, 0xd2bf6dd2, 0xcd814ccd, 0x0c18140c,
        0x13263513, 0xecc32fec, 0x5fbee15f, 0x9735a297, 0x4488cc44,
        0x172e3917, 0xc49357c4, 0xa755f2a7, 0x7efc827e, 0x3d7a473d,
        0x64c8ac64, 0x5dbae75d, 0x19322b19, 0x73e69573, 0x60c0a060,
        0x81199881, 0x4f9ed14f, 0xdca37fdc, 0x22446622, 0x2a547e2a,
        0x903bab90, 0x880b8388, 0x468cca46, 0xeec729ee, 0xb86bd3b8,
        0x14283c14, 0xdea779de, 0x5ebce25e, 0x0b161d0b, 0xdbad76db,
        0xe0db3be0, 0x32645632, 0x3a744e3a, 0x0a141e0a, 0x4992db49,
        0x060c0a06, 0x24486c24, 0x5cb8e45c, 0xc29f5dc2, 0xd3bd6ed3,
        0xac43efac, 0x62c4a662, 0x9139a891, 0x9531a495, 0xe4d337e4,
        0x79f28b79, 0xe7d532e7, 0xc88b43c8, 0x376e5937, 0x6ddab76d,
        0x8d018c8d, 0xd5b164d5, 0x4e9cd24e, 0xa949e0a9, 0x6cd8b46c,
        0x56acfa56, 0xf4f307f4, 0xeacf25ea, 0x65caaf65, 0x7af48e7a,
        0xae47e9ae, 0x08101808, 0xba6fd5ba, 0x78f08878, 0x254a6f25,
        0x2e5c722e, 0x1c38241c, 0xa657f1a6, 0xb473c7b4, 0xc69751c6,
        0xe8cb23e8, 0xdda17cdd, 0x74e89c74, 0x1f3e211f, 0x4b96dd4b,
        0xbd61dcbd, 0x8b0d868b, 0x8a0f858a, 0x70e09070, 0x3e7c423e,
        0xb571c4b5, 0x66ccaa66, 0x4890d848, 0x03060503, 0xf6f701f6,
        0x0e1c120e, 0x61c2a361, 0x356a5f35, 0x57aef957, 0xb969d0b9,
        0x86179186, 0xc19958c1, 0x1d3a271d, 0x9e27b99e, 0xe1d938e1,
        0xf8eb13f8, 0x982bb398, 0x11223311, 0x69d2bb69, 0xd9a970d9,
        0x8e07898e, 0x9433a794, 0x9b2db69b, 0x1e3c221e, 0x87159287,
        0xe9c920e9, 0xce8749ce, 0x55aaff55, 0x28507828, 0xdfa57adf,
        0x8c038f8c, 0xa159f8a1, 0x89098089, 0x0d1a170d, 0xbf65dabf,
        0xe6d731e6, 0x4284c642, 0x68d0b868, 0x4182c341, 0x9929b099,
        0x2d5a772d, 0x0f1e110f, 0xb07bcbb0, 0x54a8fc54, 0xbb6dd6bb,
        0x162c3a16];

    var T4 = [0xc6a56363, 0xf8847c7c, 0xee997777, 0xf68d7b7b, 0xff0df2f2,
        0xd6bd6b6b, 0xdeb16f6f, 0x9154c5c5, 0x60503030, 0x02030101,
        0xcea96767, 0x567d2b2b, 0xe719fefe, 0xb562d7d7, 0x4de6abab,
        0xec9a7676, 0x8f45caca, 0x1f9d8282, 0x8940c9c9, 0xfa877d7d,
        0xef15fafa, 0xb2eb5959, 0x8ec94747, 0xfb0bf0f0, 0x41ecadad,
        0xb367d4d4, 0x5ffda2a2, 0x45eaafaf, 0x23bf9c9c, 0x53f7a4a4,
        0xe4967272, 0x9b5bc0c0, 0x75c2b7b7, 0xe11cfdfd, 0x3dae9393,
        0x4c6a2626, 0x6c5a3636, 0x7e413f3f, 0xf502f7f7, 0x834fcccc,
        0x685c3434, 0x51f4a5a5, 0xd134e5e5, 0xf908f1f1, 0xe2937171,
        0xab73d8d8, 0x62533131, 0x2a3f1515, 0x080c0404, 0x9552c7c7,
        0x46652323, 0x9d5ec3c3, 0x30281818, 0x37a19696, 0x0a0f0505,
        0x2fb59a9a, 0x0e090707, 0x24361212, 0x1b9b8080, 0xdf3de2e2,
        0xcd26ebeb, 0x4e692727, 0x7fcdb2b2, 0xea9f7575, 0x121b0909,
        0x1d9e8383, 0x58742c2c, 0x342e1a1a, 0x362d1b1b, 0xdcb26e6e,
        0xb4ee5a5a, 0x5bfba0a0, 0xa4f65252, 0x764d3b3b, 0xb761d6d6,
        0x7dceb3b3, 0x527b2929, 0xdd3ee3e3, 0x5e712f2f, 0x13978484,
        0xa6f55353, 0xb968d1d1, 0x00000000, 0xc12ceded, 0x40602020,
        0xe31ffcfc, 0x79c8b1b1, 0xb6ed5b5b, 0xd4be6a6a, 0x8d46cbcb,
        0x67d9bebe, 0x724b3939, 0x94de4a4a, 0x98d44c4c, 0xb0e85858,
        0x854acfcf, 0xbb6bd0d0, 0xc52aefef, 0x4fe5aaaa, 0xed16fbfb,
        0x86c54343, 0x9ad74d4d, 0x66553333, 0x11948585, 0x8acf4545,
        0xe910f9f9, 0x04060202, 0xfe817f7f, 0xa0f05050, 0x78443c3c,
        0x25ba9f9f, 0x4be3a8a8, 0xa2f35151, 0x5dfea3a3, 0x80c04040,
        0x058a8f8f, 0x3fad9292, 0x21bc9d9d, 0x70483838, 0xf104f5f5,
        0x63dfbcbc, 0x77c1b6b6, 0xaf75dada, 0x42632121, 0x20301010,
        0xe51affff, 0xfd0ef3f3, 0xbf6dd2d2, 0x814ccdcd, 0x18140c0c,
        0x26351313, 0xc32fecec, 0xbee15f5f, 0x35a29797, 0x88cc4444,
        0x2e391717, 0x9357c4c4, 0x55f2a7a7, 0xfc827e7e, 0x7a473d3d,
        0xc8ac6464, 0xbae75d5d, 0x322b1919, 0xe6957373, 0xc0a06060,
        0x19988181, 0x9ed14f4f, 0xa37fdcdc, 0x44662222, 0x547e2a2a,
        0x3bab9090, 0x0b838888, 0x8cca4646, 0xc729eeee, 0x6bd3b8b8,
        0x283c1414, 0xa779dede, 0xbce25e5e, 0x161d0b0b, 0xad76dbdb,
        0xdb3be0e0, 0x64563232, 0x744e3a3a, 0x141e0a0a, 0x92db4949,
        0x0c0a0606, 0x486c2424, 0xb8e45c5c, 0x9f5dc2c2, 0xbd6ed3d3,
        0x43efacac, 0xc4a66262, 0x39a89191, 0x31a49595, 0xd337e4e4,
        0xf28b7979, 0xd532e7e7, 0x8b43c8c8, 0x6e593737, 0xdab76d6d,
        0x018c8d8d, 0xb164d5d5, 0x9cd24e4e, 0x49e0a9a9, 0xd8b46c6c,
        0xacfa5656, 0xf307f4f4, 0xcf25eaea, 0xcaaf6565, 0xf48e7a7a,
        0x47e9aeae, 0x10180808, 0x6fd5baba, 0xf0887878, 0x4a6f2525,
        0x5c722e2e, 0x38241c1c, 0x57f1a6a6, 0x73c7b4b4, 0x9751c6c6,
        0xcb23e8e8, 0xa17cdddd, 0xe89c7474, 0x3e211f1f, 0x96dd4b4b,
        0x61dcbdbd, 0x0d868b8b, 0x0f858a8a, 0xe0907070, 0x7c423e3e,
        0x71c4b5b5, 0xccaa6666, 0x90d84848, 0x06050303, 0xf701f6f6,
        0x1c120e0e, 0xc2a36161, 0x6a5f3535, 0xaef95757, 0x69d0b9b9,
        0x17918686, 0x9958c1c1, 0x3a271d1d, 0x27b99e9e, 0xd938e1e1,
        0xeb13f8f8, 0x2bb39898, 0x22331111, 0xd2bb6969, 0xa970d9d9,
        0x07898e8e, 0x33a79494, 0x2db69b9b, 0x3c221e1e, 0x15928787,
        0xc920e9e9, 0x8749cece, 0xaaff5555, 0x50782828, 0xa57adfdf,
        0x038f8c8c, 0x59f8a1a1, 0x09808989, 0x1a170d0d, 0x65dabfbf,
        0xd731e6e6, 0x84c64242, 0xd0b86868, 0x82c34141, 0x29b09999,
        0x5a772d2d, 0x1e110f0f, 0x7bcbb0b0, 0xa8fc5454, 0x6dd6bbbb,
        0x2c3a1616];

    function B0(x) {
        return (x & 255);
    }

    function B1(x) {
        return ((x >> 8) & 255);
    }

    function B2(x) {
        return ((x >> 16) & 255);
    }

    function B3(x) {
        return ((x >> 24) & 255);
    }

    function F1(x0, x1, x2, x3) {
        return B1(T1[x0 & 255]) | (B1(T1[(x1 >> 8) & 255]) << 8)
            | (B1(T1[(x2 >> 16) & 255]) << 16) | (B1(T1[x3 >>> 24]) << 24);
    }

    function packBytes(octets) {
        var i, j;
        var len = octets.length;
        var b = new Array(len / 4);

        if (!octets || len % 4)
            return;

        for (i = 0, j = 0; j < len; j += 4)
            b[i++] = octets[j] | (octets[j + 1] << 8) | (octets[j + 2] << 16)
                | (octets[j + 3] << 24);

        return b;
    }

    function unpackBytes(packed) {
        var j;
        var i = 0, l = packed.length;
        var r = new Array(l * 4);

        for (j = 0; j < l; j++) {
            r[i++] = B0(packed[j]);
            r[i++] = B1(packed[j]);
            r[i++] = B2(packed[j]);
            r[i++] = B3(packed[j]);
        }
        return r;
    }


    var maxkc = 8;
    var maxrk = 14;

    this.keyExpansion = function (key) {
        var kc, i, j, r, t;
        var rounds;
        var keySched = new Array(maxrk + 1);
        var keylen = key.length;
        var k = new Array(maxkc);
        var tk = new Array(maxkc);
        var rconpointer = 0;

        if (keylen == 16) {
            rounds = 10;
            kc = 4;
        } else if (keylen == 24) {
            rounds = 12;
            kc = 6
        } else if (keylen == 32) {
            rounds = 14;
            kc = 8
        } else {
            //alert('Invalid AES key length ' + keylen);
            return;
        }

        for (i = 0; i < maxrk + 1; i++)
            keySched[i] = new Array(4);

        for (i = 0, j = 0; j < keylen; j++, i += 4)
            k[j] = key.charCodeAt(i) | (key.charCodeAt(i + 1) << 8) | (key.charCodeAt(i + 2) << 16) | (key.charCodeAt(i + 3) << 24);

        for (j = kc - 1; j >= 0; j--)
            tk[j] = k[j];

        r = 0;
        t = 0;
        for (j = 0; (j < kc) && (r < rounds + 1);) {
            for (; (j < kc) && (t < 4); j++, t++) {
                keySched[r][t] = tk[j];
            }
            if (t == 4) {
                r++;
                t = 0;
            }
        }

        while (r < rounds + 1) {
            var temp = tk[kc - 1];

            tk[0] ^= S[B1(temp)] | (S[B2(temp)] << 8) | (S[B3(temp)] << 16)
                | (S[B0(temp)] << 24);
            tk[0] ^= Rcon[rconpointer++];

            if (kc != 8) {
                for (j = 1; j < kc; j++)
                    tk[j] ^= tk[j - 1];
            } else {
                for (j = 1; j < kc / 2; j++)
                    tk[j] ^= tk[j - 1];

                temp = tk[kc / 2 - 1];
                tk[kc / 2] ^= S[B0(temp)] | (S[B1(temp)] << 8)
                    | (S[B2(temp)] << 16) | (S[B3(temp)] << 24);

                for (j = kc / 2 + 1; j < kc; j++)
                    tk[j] ^= tk[j - 1];
            }

            for (j = 0; (j < kc) && (r < rounds + 1);) {
                for (; (j < kc) && (t < 4); j++, t++) {
                    keySched[r][t] = tk[j];
                }
                if (t == 4) {
                    r++;
                    t = 0;
                }
            }
        }
        this.rounds = rounds;
        this.rk = keySched;
        return this;
    }

    this.blockSizeInBits = 128;
    this.keySizeInBits = 256;

    // Precomputed lookup table for the inverse SBox
    var S5 = [82, 9, 106, 213, 48, 54, 165, 56, 191, 64, 163, 158, 129, 243,
        215, 251, 124, 227, 57, 130, 155, 47, 255, 135, 52, 142, 67, 68,
        196, 222, 233, 203, 84, 123, 148, 50, 166, 194, 35, 61, 238, 76,
        149, 11, 66, 250, 195, 78, 8, 46, 161, 102, 40, 217, 36, 178, 118,
        91, 162, 73, 109, 139, 209, 37, 114, 248, 246, 100, 134, 104, 152,
        22, 212, 164, 92, 204, 93, 101, 182, 146, 108, 112, 72, 80, 253,
        237, 185, 218, 94, 21, 70, 87, 167, 141, 157, 132, 144, 216, 171,
        0, 140, 188, 211, 10, 247, 228, 88, 5, 184, 179, 69, 6, 208, 44,
        30, 143, 202, 63, 15, 2, 193, 175, 189, 3, 1, 19, 138, 107, 58,
        145, 17, 65, 79, 103, 220, 234, 151, 242, 207, 206, 240, 180, 230,
        115, 150, 172, 116, 34, 231, 173, 53, 133, 226, 249, 55, 232, 28,
        117, 223, 110, 71, 241, 26, 113, 29, 41, 197, 137, 111, 183, 98,
        14, 170, 24, 190, 27, 252, 86, 62, 75, 198, 210, 121, 32, 154, 219,
        192, 254, 120, 205, 90, 244, 31, 221, 168, 51, 136, 7, 199, 49,
        177, 18, 16, 89, 39, 128, 236, 95, 96, 81, 127, 169, 25, 181, 74,
        13, 45, 229, 122, 159, 147, 201, 156, 239, 160, 224, 59, 77, 174,
        42, 245, 176, 200, 235, 187, 60, 131, 83, 153, 97, 23, 43, 4, 126,
        186, 119, 214, 38, 225, 105, 20, 99, 85, 33, 12, 125];

    var T5 = [0x50a7f451, 0x5365417e, 0xc3a4171a, 0x965e273a, 0xcb6bab3b,
        0xf1459d1f, 0xab58faac, 0x9303e34b, 0x55fa3020, 0xf66d76ad,
        0x9176cc88, 0x254c02f5, 0xfcd7e54f, 0xd7cb2ac5, 0x80443526,
        0x8fa362b5, 0x495ab1de, 0x671bba25, 0x980eea45, 0xe1c0fe5d,
        0x02752fc3, 0x12f04c81, 0xa397468d, 0xc6f9d36b, 0xe75f8f03,
        0x959c9215, 0xeb7a6dbf, 0xda595295, 0x2d83bed4, 0xd3217458,
        0x2969e049, 0x44c8c98e, 0x6a89c275, 0x78798ef4, 0x6b3e5899,
        0xdd71b927, 0xb64fe1be, 0x17ad88f0, 0x66ac20c9, 0xb43ace7d,
        0x184adf63, 0x82311ae5, 0x60335197, 0x457f5362, 0xe07764b1,
        0x84ae6bbb, 0x1ca081fe, 0x942b08f9, 0x58684870, 0x19fd458f,
        0x876cde94, 0xb7f87b52, 0x23d373ab, 0xe2024b72, 0x578f1fe3,
        0x2aab5566, 0x0728ebb2, 0x03c2b52f, 0x9a7bc586, 0xa50837d3,
        0xf2872830, 0xb2a5bf23, 0xba6a0302, 0x5c8216ed, 0x2b1ccf8a,
        0x92b479a7, 0xf0f207f3, 0xa1e2694e, 0xcdf4da65, 0xd5be0506,
        0x1f6234d1, 0x8afea6c4, 0x9d532e34, 0xa055f3a2, 0x32e18a05,
        0x75ebf6a4, 0x39ec830b, 0xaaef6040, 0x069f715e, 0x51106ebd,
        0xf98a213e, 0x3d06dd96, 0xae053edd, 0x46bde64d, 0xb58d5491,
        0x055dc471, 0x6fd40604, 0xff155060, 0x24fb9819, 0x97e9bdd6,
        0xcc434089, 0x779ed967, 0xbd42e8b0, 0x888b8907, 0x385b19e7,
        0xdbeec879, 0x470a7ca1, 0xe90f427c, 0xc91e84f8, 0x00000000,
        0x83868009, 0x48ed2b32, 0xac70111e, 0x4e725a6c, 0xfbff0efd,
        0x5638850f, 0x1ed5ae3d, 0x27392d36, 0x64d90f0a, 0x21a65c68,
        0xd1545b9b, 0x3a2e3624, 0xb1670a0c, 0x0fe75793, 0xd296eeb4,
        0x9e919b1b, 0x4fc5c080, 0xa220dc61, 0x694b775a, 0x161a121c,
        0x0aba93e2, 0xe52aa0c0, 0x43e0223c, 0x1d171b12, 0x0b0d090e,
        0xadc78bf2, 0xb9a8b62d, 0xc8a91e14, 0x8519f157, 0x4c0775af,
        0xbbdd99ee, 0xfd607fa3, 0x9f2601f7, 0xbcf5725c, 0xc53b6644,
        0x347efb5b, 0x7629438b, 0xdcc623cb, 0x68fcedb6, 0x63f1e4b8,
        0xcadc31d7, 0x10856342, 0x40229713, 0x2011c684, 0x7d244a85,
        0xf83dbbd2, 0x1132f9ae, 0x6da129c7, 0x4b2f9e1d, 0xf330b2dc,
        0xec52860d, 0xd0e3c177, 0x6c16b32b, 0x99b970a9, 0xfa489411,
        0x2264e947, 0xc48cfca8, 0x1a3ff0a0, 0xd82c7d56, 0xef903322,
        0xc74e4987, 0xc1d138d9, 0xfea2ca8c, 0x360bd498, 0xcf81f5a6,
        0x28de7aa5, 0x268eb7da, 0xa4bfad3f, 0xe49d3a2c, 0x0d927850,
        0x9bcc5f6a, 0x62467e54, 0xc2138df6, 0xe8b8d890, 0x5ef7392e,
        0xf5afc382, 0xbe805d9f, 0x7c93d069, 0xa92dd56f, 0xb31225cf,
        0x3b99acc8, 0xa77d1810, 0x6e639ce8, 0x7bbb3bdb, 0x097826cd,
        0xf418596e, 0x01b79aec, 0xa89a4f83, 0x656e95e6, 0x7ee6ffaa,
        0x08cfbc21, 0xe6e815ef, 0xd99be7ba, 0xce366f4a, 0xd4099fea,
        0xd67cb029, 0xafb2a431, 0x31233f2a, 0x3094a5c6, 0xc066a235,
        0x37bc4e74, 0xa6ca82fc, 0xb0d090e0, 0x15d8a733, 0x4a9804f1,
        0xf7daec41, 0x0e50cd7f, 0x2ff69117, 0x8dd64d76, 0x4db0ef43,
        0x544daacc, 0xdf0496e4, 0xe3b5d19e, 0x1b886a4c, 0xb81f2cc1,
        0x7f516546, 0x04ea5e9d, 0x5d358c01, 0x737487fa, 0x2e410bfb,
        0x5a1d67b3, 0x52d2db92, 0x335610e9, 0x1347d66d, 0x8c61d79a,
        0x7a0ca137, 0x8e14f859, 0x893c13eb, 0xee27a9ce, 0x35c961b7,
        0xede51ce1, 0x3cb1477a, 0x59dfd29c, 0x3f73f255, 0x79ce1418,
        0xbf37c773, 0xeacdf753, 0x5baafd5f, 0x146f3ddf, 0x86db4478,
        0x81f3afca, 0x3ec468b9, 0x2c342438, 0x5f40a3c2, 0x72c31d16,
        0x0c25e2bc, 0x8b493c28, 0x41950dff, 0x7101a839, 0xdeb30c08,
        0x9ce4b4d8, 0x90c15664, 0x6184cb7b, 0x70b632d5, 0x745c6c48,
        0x4257b8d0];

    var T6 = [0xa7f45150, 0x65417e53, 0xa4171ac3, 0x5e273a96, 0x6bab3bcb,
        0x459d1ff1, 0x58faacab, 0x03e34b93, 0xfa302055, 0x6d76adf6,
        0x76cc8891, 0x4c02f525, 0xd7e54ffc, 0xcb2ac5d7, 0x44352680,
        0xa362b58f, 0x5ab1de49, 0x1bba2567, 0x0eea4598, 0xc0fe5de1,
        0x752fc302, 0xf04c8112, 0x97468da3, 0xf9d36bc6, 0x5f8f03e7,
        0x9c921595, 0x7a6dbfeb, 0x595295da, 0x83bed42d, 0x217458d3,
        0x69e04929, 0xc8c98e44, 0x89c2756a, 0x798ef478, 0x3e58996b,
        0x71b927dd, 0x4fe1beb6, 0xad88f017, 0xac20c966, 0x3ace7db4,
        0x4adf6318, 0x311ae582, 0x33519760, 0x7f536245, 0x7764b1e0,
        0xae6bbb84, 0xa081fe1c, 0x2b08f994, 0x68487058, 0xfd458f19,
        0x6cde9487, 0xf87b52b7, 0xd373ab23, 0x024b72e2, 0x8f1fe357,
        0xab55662a, 0x28ebb207, 0xc2b52f03, 0x7bc5869a, 0x0837d3a5,
        0x872830f2, 0xa5bf23b2, 0x6a0302ba, 0x8216ed5c, 0x1ccf8a2b,
        0xb479a792, 0xf207f3f0, 0xe2694ea1, 0xf4da65cd, 0xbe0506d5,
        0x6234d11f, 0xfea6c48a, 0x532e349d, 0x55f3a2a0, 0xe18a0532,
        0xebf6a475, 0xec830b39, 0xef6040aa, 0x9f715e06, 0x106ebd51,
        0x8a213ef9, 0x06dd963d, 0x053eddae, 0xbde64d46, 0x8d5491b5,
        0x5dc47105, 0xd406046f, 0x155060ff, 0xfb981924, 0xe9bdd697,
        0x434089cc, 0x9ed96777, 0x42e8b0bd, 0x8b890788, 0x5b19e738,
        0xeec879db, 0x0a7ca147, 0x0f427ce9, 0x1e84f8c9, 0x00000000,
        0x86800983, 0xed2b3248, 0x70111eac, 0x725a6c4e, 0xff0efdfb,
        0x38850f56, 0xd5ae3d1e, 0x392d3627, 0xd90f0a64, 0xa65c6821,
        0x545b9bd1, 0x2e36243a, 0x670a0cb1, 0xe757930f, 0x96eeb4d2,
        0x919b1b9e, 0xc5c0804f, 0x20dc61a2, 0x4b775a69, 0x1a121c16,
        0xba93e20a, 0x2aa0c0e5, 0xe0223c43, 0x171b121d, 0x0d090e0b,
        0xc78bf2ad, 0xa8b62db9, 0xa91e14c8, 0x19f15785, 0x0775af4c,
        0xdd99eebb, 0x607fa3fd, 0x2601f79f, 0xf5725cbc, 0x3b6644c5,
        0x7efb5b34, 0x29438b76, 0xc623cbdc, 0xfcedb668, 0xf1e4b863,
        0xdc31d7ca, 0x85634210, 0x22971340, 0x11c68420, 0x244a857d,
        0x3dbbd2f8, 0x32f9ae11, 0xa129c76d, 0x2f9e1d4b, 0x30b2dcf3,
        0x52860dec, 0xe3c177d0, 0x16b32b6c, 0xb970a999, 0x489411fa,
        0x64e94722, 0x8cfca8c4, 0x3ff0a01a, 0x2c7d56d8, 0x903322ef,
        0x4e4987c7, 0xd138d9c1, 0xa2ca8cfe, 0x0bd49836, 0x81f5a6cf,
        0xde7aa528, 0x8eb7da26, 0xbfad3fa4, 0x9d3a2ce4, 0x9278500d,
        0xcc5f6a9b, 0x467e5462, 0x138df6c2, 0xb8d890e8, 0xf7392e5e,
        0xafc382f5, 0x805d9fbe, 0x93d0697c, 0x2dd56fa9, 0x1225cfb3,
        0x99acc83b, 0x7d1810a7, 0x639ce86e, 0xbb3bdb7b, 0x7826cd09,
        0x18596ef4, 0xb79aec01, 0x9a4f83a8, 0x6e95e665, 0xe6ffaa7e,
        0xcfbc2108, 0xe815efe6, 0x9be7bad9, 0x366f4ace, 0x099fead4,
        0x7cb029d6, 0xb2a431af, 0x233f2a31, 0x94a5c630, 0x66a235c0,
        0xbc4e7437, 0xca82fca6, 0xd090e0b0, 0xd8a73315, 0x9804f14a,
        0xdaec41f7, 0x50cd7f0e, 0xf691172f, 0xd64d768d, 0xb0ef434d,
        0x4daacc54, 0x0496e4df, 0xb5d19ee3, 0x886a4c1b, 0x1f2cc1b8,
        0x5165467f, 0xea5e9d04, 0x358c015d, 0x7487fa73, 0x410bfb2e,
        0x1d67b35a, 0xd2db9252, 0x5610e933, 0x47d66d13, 0x61d79a8c,
        0x0ca1377a, 0x14f8598e, 0x3c13eb89, 0x27a9ceee, 0xc961b735,
        0xe51ce1ed, 0xb1477a3c, 0xdfd29c59, 0x73f2553f, 0xce141879,
        0x37c773bf, 0xcdf753ea, 0xaafd5f5b, 0x6f3ddf14, 0xdb447886,
        0xf3afca81, 0xc468b93e, 0x3424382c, 0x40a3c25f, 0xc31d1672,
        0x25e2bc0c, 0x493c288b, 0x950dff41, 0x01a83971, 0xb30c08de,
        0xe4b4d89c, 0xc1566490, 0x84cb7b61, 0xb632d570, 0x5c6c4874,
        0x57b8d042];

    var T7 = [0xf45150a7, 0x417e5365, 0x171ac3a4, 0x273a965e, 0xab3bcb6b,
        0x9d1ff145, 0xfaacab58, 0xe34b9303, 0x302055fa, 0x76adf66d,
        0xcc889176, 0x02f5254c, 0xe54ffcd7, 0x2ac5d7cb, 0x35268044,
        0x62b58fa3, 0xb1de495a, 0xba25671b, 0xea45980e, 0xfe5de1c0,
        0x2fc30275, 0x4c8112f0, 0x468da397, 0xd36bc6f9, 0x8f03e75f,
        0x9215959c, 0x6dbfeb7a, 0x5295da59, 0xbed42d83, 0x7458d321,
        0xe0492969, 0xc98e44c8, 0xc2756a89, 0x8ef47879, 0x58996b3e,
        0xb927dd71, 0xe1beb64f, 0x88f017ad, 0x20c966ac, 0xce7db43a,
        0xdf63184a, 0x1ae58231, 0x51976033, 0x5362457f, 0x64b1e077,
        0x6bbb84ae, 0x81fe1ca0, 0x08f9942b, 0x48705868, 0x458f19fd,
        0xde94876c, 0x7b52b7f8, 0x73ab23d3, 0x4b72e202, 0x1fe3578f,
        0x55662aab, 0xebb20728, 0xb52f03c2, 0xc5869a7b, 0x37d3a508,
        0x2830f287, 0xbf23b2a5, 0x0302ba6a, 0x16ed5c82, 0xcf8a2b1c,
        0x79a792b4, 0x07f3f0f2, 0x694ea1e2, 0xda65cdf4, 0x0506d5be,
        0x34d11f62, 0xa6c48afe, 0x2e349d53, 0xf3a2a055, 0x8a0532e1,
        0xf6a475eb, 0x830b39ec, 0x6040aaef, 0x715e069f, 0x6ebd5110,
        0x213ef98a, 0xdd963d06, 0x3eddae05, 0xe64d46bd, 0x5491b58d,
        0xc471055d, 0x06046fd4, 0x5060ff15, 0x981924fb, 0xbdd697e9,
        0x4089cc43, 0xd967779e, 0xe8b0bd42, 0x8907888b, 0x19e7385b,
        0xc879dbee, 0x7ca1470a, 0x427ce90f, 0x84f8c91e, 0x00000000,
        0x80098386, 0x2b3248ed, 0x111eac70, 0x5a6c4e72, 0x0efdfbff,
        0x850f5638, 0xae3d1ed5, 0x2d362739, 0x0f0a64d9, 0x5c6821a6,
        0x5b9bd154, 0x36243a2e, 0x0a0cb167, 0x57930fe7, 0xeeb4d296,
        0x9b1b9e91, 0xc0804fc5, 0xdc61a220, 0x775a694b, 0x121c161a,
        0x93e20aba, 0xa0c0e52a, 0x223c43e0, 0x1b121d17, 0x090e0b0d,
        0x8bf2adc7, 0xb62db9a8, 0x1e14c8a9, 0xf1578519, 0x75af4c07,
        0x99eebbdd, 0x7fa3fd60, 0x01f79f26, 0x725cbcf5, 0x6644c53b,
        0xfb5b347e, 0x438b7629, 0x23cbdcc6, 0xedb668fc, 0xe4b863f1,
        0x31d7cadc, 0x63421085, 0x97134022, 0xc6842011, 0x4a857d24,
        0xbbd2f83d, 0xf9ae1132, 0x29c76da1, 0x9e1d4b2f, 0xb2dcf330,
        0x860dec52, 0xc177d0e3, 0xb32b6c16, 0x70a999b9, 0x9411fa48,
        0xe9472264, 0xfca8c48c, 0xf0a01a3f, 0x7d56d82c, 0x3322ef90,
        0x4987c74e, 0x38d9c1d1, 0xca8cfea2, 0xd498360b, 0xf5a6cf81,
        0x7aa528de, 0xb7da268e, 0xad3fa4bf, 0x3a2ce49d, 0x78500d92,
        0x5f6a9bcc, 0x7e546246, 0x8df6c213, 0xd890e8b8, 0x392e5ef7,
        0xc382f5af, 0x5d9fbe80, 0xd0697c93, 0xd56fa92d, 0x25cfb312,
        0xacc83b99, 0x1810a77d, 0x9ce86e63, 0x3bdb7bbb, 0x26cd0978,
        0x596ef418, 0x9aec01b7, 0x4f83a89a, 0x95e6656e, 0xffaa7ee6,
        0xbc2108cf, 0x15efe6e8, 0xe7bad99b, 0x6f4ace36, 0x9fead409,
        0xb029d67c, 0xa431afb2, 0x3f2a3123, 0xa5c63094, 0xa235c066,
        0x4e7437bc, 0x82fca6ca, 0x90e0b0d0, 0xa73315d8, 0x04f14a98,
        0xec41f7da, 0xcd7f0e50, 0x91172ff6, 0x4d768dd6, 0xef434db0,
        0xaacc544d, 0x96e4df04, 0xd19ee3b5, 0x6a4c1b88, 0x2cc1b81f,
        0x65467f51, 0x5e9d04ea, 0x8c015d35, 0x87fa7374, 0x0bfb2e41,
        0x67b35a1d, 0xdb9252d2, 0x10e93356, 0xd66d1347, 0xd79a8c61,
        0xa1377a0c, 0xf8598e14, 0x13eb893c, 0xa9ceee27, 0x61b735c9,
        0x1ce1ede5, 0x477a3cb1, 0xd29c59df, 0xf2553f73, 0x141879ce,
        0xc773bf37, 0xf753eacd, 0xfd5f5baa, 0x3ddf146f, 0x447886db,
        0xafca81f3, 0x68b93ec4, 0x24382c34, 0xa3c25f40, 0x1d1672c3,
        0xe2bc0c25, 0x3c288b49, 0x0dff4195, 0xa8397101, 0x0c08deb3,
        0xb4d89ce4, 0x566490c1, 0xcb7b6184, 0x32d570b6, 0x6c48745c,
        0xb8d04257];

    var T8 = [0x5150a7f4, 0x7e536541, 0x1ac3a417, 0x3a965e27, 0x3bcb6bab,
        0x1ff1459d, 0xacab58fa, 0x4b9303e3, 0x2055fa30, 0xadf66d76,
        0x889176cc, 0xf5254c02, 0x4ffcd7e5, 0xc5d7cb2a, 0x26804435,
        0xb58fa362, 0xde495ab1, 0x25671bba, 0x45980eea, 0x5de1c0fe,
        0xc302752f, 0x8112f04c, 0x8da39746, 0x6bc6f9d3, 0x03e75f8f,
        0x15959c92, 0xbfeb7a6d, 0x95da5952, 0xd42d83be, 0x58d32174,
        0x492969e0, 0x8e44c8c9, 0x756a89c2, 0xf478798e, 0x996b3e58,
        0x27dd71b9, 0xbeb64fe1, 0xf017ad88, 0xc966ac20, 0x7db43ace,
        0x63184adf, 0xe582311a, 0x97603351, 0x62457f53, 0xb1e07764,
        0xbb84ae6b, 0xfe1ca081, 0xf9942b08, 0x70586848, 0x8f19fd45,
        0x94876cde, 0x52b7f87b, 0xab23d373, 0x72e2024b, 0xe3578f1f,
        0x662aab55, 0xb20728eb, 0x2f03c2b5, 0x869a7bc5, 0xd3a50837,
        0x30f28728, 0x23b2a5bf, 0x02ba6a03, 0xed5c8216, 0x8a2b1ccf,
        0xa792b479, 0xf3f0f207, 0x4ea1e269, 0x65cdf4da, 0x06d5be05,
        0xd11f6234, 0xc48afea6, 0x349d532e, 0xa2a055f3, 0x0532e18a,
        0xa475ebf6, 0x0b39ec83, 0x40aaef60, 0x5e069f71, 0xbd51106e,
        0x3ef98a21, 0x963d06dd, 0xddae053e, 0x4d46bde6, 0x91b58d54,
        0x71055dc4, 0x046fd406, 0x60ff1550, 0x1924fb98, 0xd697e9bd,
        0x89cc4340, 0x67779ed9, 0xb0bd42e8, 0x07888b89, 0xe7385b19,
        0x79dbeec8, 0xa1470a7c, 0x7ce90f42, 0xf8c91e84, 0x00000000,
        0x09838680, 0x3248ed2b, 0x1eac7011, 0x6c4e725a, 0xfdfbff0e,
        0x0f563885, 0x3d1ed5ae, 0x3627392d, 0x0a64d90f, 0x6821a65c,
        0x9bd1545b, 0x243a2e36, 0x0cb1670a, 0x930fe757, 0xb4d296ee,
        0x1b9e919b, 0x804fc5c0, 0x61a220dc, 0x5a694b77, 0x1c161a12,
        0xe20aba93, 0xc0e52aa0, 0x3c43e022, 0x121d171b, 0x0e0b0d09,
        0xf2adc78b, 0x2db9a8b6, 0x14c8a91e, 0x578519f1, 0xaf4c0775,
        0xeebbdd99, 0xa3fd607f, 0xf79f2601, 0x5cbcf572, 0x44c53b66,
        0x5b347efb, 0x8b762943, 0xcbdcc623, 0xb668fced, 0xb863f1e4,
        0xd7cadc31, 0x42108563, 0x13402297, 0x842011c6, 0x857d244a,
        0xd2f83dbb, 0xae1132f9, 0xc76da129, 0x1d4b2f9e, 0xdcf330b2,
        0x0dec5286, 0x77d0e3c1, 0x2b6c16b3, 0xa999b970, 0x11fa4894,
        0x472264e9, 0xa8c48cfc, 0xa01a3ff0, 0x56d82c7d, 0x22ef9033,
        0x87c74e49, 0xd9c1d138, 0x8cfea2ca, 0x98360bd4, 0xa6cf81f5,
        0xa528de7a, 0xda268eb7, 0x3fa4bfad, 0x2ce49d3a, 0x500d9278,
        0x6a9bcc5f, 0x5462467e, 0xf6c2138d, 0x90e8b8d8, 0x2e5ef739,
        0x82f5afc3, 0x9fbe805d, 0x697c93d0, 0x6fa92dd5, 0xcfb31225,
        0xc83b99ac, 0x10a77d18, 0xe86e639c, 0xdb7bbb3b, 0xcd097826,
        0x6ef41859, 0xec01b79a, 0x83a89a4f, 0xe6656e95, 0xaa7ee6ff,
        0x2108cfbc, 0xefe6e815, 0xbad99be7, 0x4ace366f, 0xead4099f,
        0x29d67cb0, 0x31afb2a4, 0x2a31233f, 0xc63094a5, 0x35c066a2,
        0x7437bc4e, 0xfca6ca82, 0xe0b0d090, 0x3315d8a7, 0xf14a9804,
        0x41f7daec, 0x7f0e50cd, 0x172ff691, 0x768dd64d, 0x434db0ef,
        0xcc544daa, 0xe4df0496, 0x9ee3b5d1, 0x4c1b886a, 0xc1b81f2c,
        0x467f5165, 0x9d04ea5e, 0x015d358c, 0xfa737487, 0xfb2e410b,
        0xb35a1d67, 0x9252d2db, 0xe9335610, 0x6d1347d6, 0x9a8c61d7,
        0x377a0ca1, 0x598e14f8, 0xeb893c13, 0xceee27a9, 0xb735c961,
        0xe1ede51c, 0x7a3cb147, 0x9c59dfd2, 0x553f73f2, 0x1879ce14,
        0x73bf37c7, 0x53eacdf7, 0x5f5baafd, 0xdf146f3d, 0x7886db44,
        0xca81f3af, 0xb93ec468, 0x382c3424, 0xc25f40a3, 0x1672c31d,
        0xbc0c25e2, 0x288b493c, 0xff41950d, 0x397101a8, 0x08deb30c,
        0xd89ce4b4, 0x6490c156, 0x7b6184cb, 0xd570b632, 0x48745c6c,
        0xd04257b8];

    var U1 = [0x00000000, 0x0b0d090e, 0x161a121c, 0x1d171b12, 0x2c342438,
        0x27392d36, 0x3a2e3624, 0x31233f2a, 0x58684870, 0x5365417e,
        0x4e725a6c, 0x457f5362, 0x745c6c48, 0x7f516546, 0x62467e54,
        0x694b775a, 0xb0d090e0, 0xbbdd99ee, 0xa6ca82fc, 0xadc78bf2,
        0x9ce4b4d8, 0x97e9bdd6, 0x8afea6c4, 0x81f3afca, 0xe8b8d890,
        0xe3b5d19e, 0xfea2ca8c, 0xf5afc382, 0xc48cfca8, 0xcf81f5a6,
        0xd296eeb4, 0xd99be7ba, 0x7bbb3bdb, 0x70b632d5, 0x6da129c7,
        0x66ac20c9, 0x578f1fe3, 0x5c8216ed, 0x41950dff, 0x4a9804f1,
        0x23d373ab, 0x28de7aa5, 0x35c961b7, 0x3ec468b9, 0x0fe75793,
        0x04ea5e9d, 0x19fd458f, 0x12f04c81, 0xcb6bab3b, 0xc066a235,
        0xdd71b927, 0xd67cb029, 0xe75f8f03, 0xec52860d, 0xf1459d1f,
        0xfa489411, 0x9303e34b, 0x980eea45, 0x8519f157, 0x8e14f859,
        0xbf37c773, 0xb43ace7d, 0xa92dd56f, 0xa220dc61, 0xf66d76ad,
        0xfd607fa3, 0xe07764b1, 0xeb7a6dbf, 0xda595295, 0xd1545b9b,
        0xcc434089, 0xc74e4987, 0xae053edd, 0xa50837d3, 0xb81f2cc1,
        0xb31225cf, 0x82311ae5, 0x893c13eb, 0x942b08f9, 0x9f2601f7,
        0x46bde64d, 0x4db0ef43, 0x50a7f451, 0x5baafd5f, 0x6a89c275,
        0x6184cb7b, 0x7c93d069, 0x779ed967, 0x1ed5ae3d, 0x15d8a733,
        0x08cfbc21, 0x03c2b52f, 0x32e18a05, 0x39ec830b, 0x24fb9819,
        0x2ff69117, 0x8dd64d76, 0x86db4478, 0x9bcc5f6a, 0x90c15664,
        0xa1e2694e, 0xaaef6040, 0xb7f87b52, 0xbcf5725c, 0xd5be0506,
        0xdeb30c08, 0xc3a4171a, 0xc8a91e14, 0xf98a213e, 0xf2872830,
        0xef903322, 0xe49d3a2c, 0x3d06dd96, 0x360bd498, 0x2b1ccf8a,
        0x2011c684, 0x1132f9ae, 0x1a3ff0a0, 0x0728ebb2, 0x0c25e2bc,
        0x656e95e6, 0x6e639ce8, 0x737487fa, 0x78798ef4, 0x495ab1de,
        0x4257b8d0, 0x5f40a3c2, 0x544daacc, 0xf7daec41, 0xfcd7e54f,
        0xe1c0fe5d, 0xeacdf753, 0xdbeec879, 0xd0e3c177, 0xcdf4da65,
        0xc6f9d36b, 0xafb2a431, 0xa4bfad3f, 0xb9a8b62d, 0xb2a5bf23,
        0x83868009, 0x888b8907, 0x959c9215, 0x9e919b1b, 0x470a7ca1,
        0x4c0775af, 0x51106ebd, 0x5a1d67b3, 0x6b3e5899, 0x60335197,
        0x7d244a85, 0x7629438b, 0x1f6234d1, 0x146f3ddf, 0x097826cd,
        0x02752fc3, 0x335610e9, 0x385b19e7, 0x254c02f5, 0x2e410bfb,
        0x8c61d79a, 0x876cde94, 0x9a7bc586, 0x9176cc88, 0xa055f3a2,
        0xab58faac, 0xb64fe1be, 0xbd42e8b0, 0xd4099fea, 0xdf0496e4,
        0xc2138df6, 0xc91e84f8, 0xf83dbbd2, 0xf330b2dc, 0xee27a9ce,
        0xe52aa0c0, 0x3cb1477a, 0x37bc4e74, 0x2aab5566, 0x21a65c68,
        0x10856342, 0x1b886a4c, 0x069f715e, 0x0d927850, 0x64d90f0a,
        0x6fd40604, 0x72c31d16, 0x79ce1418, 0x48ed2b32, 0x43e0223c,
        0x5ef7392e, 0x55fa3020, 0x01b79aec, 0x0aba93e2, 0x17ad88f0,
        0x1ca081fe, 0x2d83bed4, 0x268eb7da, 0x3b99acc8, 0x3094a5c6,
        0x59dfd29c, 0x52d2db92, 0x4fc5c080, 0x44c8c98e, 0x75ebf6a4,
        0x7ee6ffaa, 0x63f1e4b8, 0x68fcedb6, 0xb1670a0c, 0xba6a0302,
        0xa77d1810, 0xac70111e, 0x9d532e34, 0x965e273a, 0x8b493c28,
        0x80443526, 0xe90f427c, 0xe2024b72, 0xff155060, 0xf418596e,
        0xc53b6644, 0xce366f4a, 0xd3217458, 0xd82c7d56, 0x7a0ca137,
        0x7101a839, 0x6c16b32b, 0x671bba25, 0x5638850f, 0x5d358c01,
        0x40229713, 0x4b2f9e1d, 0x2264e947, 0x2969e049, 0x347efb5b,
        0x3f73f255, 0x0e50cd7f, 0x055dc471, 0x184adf63, 0x1347d66d,
        0xcadc31d7, 0xc1d138d9, 0xdcc623cb, 0xd7cb2ac5, 0xe6e815ef,
        0xede51ce1, 0xf0f207f3, 0xfbff0efd, 0x92b479a7, 0x99b970a9,
        0x84ae6bbb, 0x8fa362b5, 0xbe805d9f, 0xb58d5491, 0xa89a4f83,
        0xa397468d];

    var U2 = [0x00000000, 0x0d090e0b, 0x1a121c16, 0x171b121d, 0x3424382c,
        0x392d3627, 0x2e36243a, 0x233f2a31, 0x68487058, 0x65417e53,
        0x725a6c4e, 0x7f536245, 0x5c6c4874, 0x5165467f, 0x467e5462,
        0x4b775a69, 0xd090e0b0, 0xdd99eebb, 0xca82fca6, 0xc78bf2ad,
        0xe4b4d89c, 0xe9bdd697, 0xfea6c48a, 0xf3afca81, 0xb8d890e8,
        0xb5d19ee3, 0xa2ca8cfe, 0xafc382f5, 0x8cfca8c4, 0x81f5a6cf,
        0x96eeb4d2, 0x9be7bad9, 0xbb3bdb7b, 0xb632d570, 0xa129c76d,
        0xac20c966, 0x8f1fe357, 0x8216ed5c, 0x950dff41, 0x9804f14a,
        0xd373ab23, 0xde7aa528, 0xc961b735, 0xc468b93e, 0xe757930f,
        0xea5e9d04, 0xfd458f19, 0xf04c8112, 0x6bab3bcb, 0x66a235c0,
        0x71b927dd, 0x7cb029d6, 0x5f8f03e7, 0x52860dec, 0x459d1ff1,
        0x489411fa, 0x03e34b93, 0x0eea4598, 0x19f15785, 0x14f8598e,
        0x37c773bf, 0x3ace7db4, 0x2dd56fa9, 0x20dc61a2, 0x6d76adf6,
        0x607fa3fd, 0x7764b1e0, 0x7a6dbfeb, 0x595295da, 0x545b9bd1,
        0x434089cc, 0x4e4987c7, 0x053eddae, 0x0837d3a5, 0x1f2cc1b8,
        0x1225cfb3, 0x311ae582, 0x3c13eb89, 0x2b08f994, 0x2601f79f,
        0xbde64d46, 0xb0ef434d, 0xa7f45150, 0xaafd5f5b, 0x89c2756a,
        0x84cb7b61, 0x93d0697c, 0x9ed96777, 0xd5ae3d1e, 0xd8a73315,
        0xcfbc2108, 0xc2b52f03, 0xe18a0532, 0xec830b39, 0xfb981924,
        0xf691172f, 0xd64d768d, 0xdb447886, 0xcc5f6a9b, 0xc1566490,
        0xe2694ea1, 0xef6040aa, 0xf87b52b7, 0xf5725cbc, 0xbe0506d5,
        0xb30c08de, 0xa4171ac3, 0xa91e14c8, 0x8a213ef9, 0x872830f2,
        0x903322ef, 0x9d3a2ce4, 0x06dd963d, 0x0bd49836, 0x1ccf8a2b,
        0x11c68420, 0x32f9ae11, 0x3ff0a01a, 0x28ebb207, 0x25e2bc0c,
        0x6e95e665, 0x639ce86e, 0x7487fa73, 0x798ef478, 0x5ab1de49,
        0x57b8d042, 0x40a3c25f, 0x4daacc54, 0xdaec41f7, 0xd7e54ffc,
        0xc0fe5de1, 0xcdf753ea, 0xeec879db, 0xe3c177d0, 0xf4da65cd,
        0xf9d36bc6, 0xb2a431af, 0xbfad3fa4, 0xa8b62db9, 0xa5bf23b2,
        0x86800983, 0x8b890788, 0x9c921595, 0x919b1b9e, 0x0a7ca147,
        0x0775af4c, 0x106ebd51, 0x1d67b35a, 0x3e58996b, 0x33519760,
        0x244a857d, 0x29438b76, 0x6234d11f, 0x6f3ddf14, 0x7826cd09,
        0x752fc302, 0x5610e933, 0x5b19e738, 0x4c02f525, 0x410bfb2e,
        0x61d79a8c, 0x6cde9487, 0x7bc5869a, 0x76cc8891, 0x55f3a2a0,
        0x58faacab, 0x4fe1beb6, 0x42e8b0bd, 0x099fead4, 0x0496e4df,
        0x138df6c2, 0x1e84f8c9, 0x3dbbd2f8, 0x30b2dcf3, 0x27a9ceee,
        0x2aa0c0e5, 0xb1477a3c, 0xbc4e7437, 0xab55662a, 0xa65c6821,
        0x85634210, 0x886a4c1b, 0x9f715e06, 0x9278500d, 0xd90f0a64,
        0xd406046f, 0xc31d1672, 0xce141879, 0xed2b3248, 0xe0223c43,
        0xf7392e5e, 0xfa302055, 0xb79aec01, 0xba93e20a, 0xad88f017,
        0xa081fe1c, 0x83bed42d, 0x8eb7da26, 0x99acc83b, 0x94a5c630,
        0xdfd29c59, 0xd2db9252, 0xc5c0804f, 0xc8c98e44, 0xebf6a475,
        0xe6ffaa7e, 0xf1e4b863, 0xfcedb668, 0x670a0cb1, 0x6a0302ba,
        0x7d1810a7, 0x70111eac, 0x532e349d, 0x5e273a96, 0x493c288b,
        0x44352680, 0x0f427ce9, 0x024b72e2, 0x155060ff, 0x18596ef4,
        0x3b6644c5, 0x366f4ace, 0x217458d3, 0x2c7d56d8, 0x0ca1377a,
        0x01a83971, 0x16b32b6c, 0x1bba2567, 0x38850f56, 0x358c015d,
        0x22971340, 0x2f9e1d4b, 0x64e94722, 0x69e04929, 0x7efb5b34,
        0x73f2553f, 0x50cd7f0e, 0x5dc47105, 0x4adf6318, 0x47d66d13,
        0xdc31d7ca, 0xd138d9c1, 0xc623cbdc, 0xcb2ac5d7, 0xe815efe6,
        0xe51ce1ed, 0xf207f3f0, 0xff0efdfb, 0xb479a792, 0xb970a999,
        0xae6bbb84, 0xa362b58f, 0x805d9fbe, 0x8d5491b5, 0x9a4f83a8,
        0x97468da3];

    var U3 = [0x00000000, 0x090e0b0d, 0x121c161a, 0x1b121d17, 0x24382c34,
        0x2d362739, 0x36243a2e, 0x3f2a3123, 0x48705868, 0x417e5365,
        0x5a6c4e72, 0x5362457f, 0x6c48745c, 0x65467f51, 0x7e546246,
        0x775a694b, 0x90e0b0d0, 0x99eebbdd, 0x82fca6ca, 0x8bf2adc7,
        0xb4d89ce4, 0xbdd697e9, 0xa6c48afe, 0xafca81f3, 0xd890e8b8,
        0xd19ee3b5, 0xca8cfea2, 0xc382f5af, 0xfca8c48c, 0xf5a6cf81,
        0xeeb4d296, 0xe7bad99b, 0x3bdb7bbb, 0x32d570b6, 0x29c76da1,
        0x20c966ac, 0x1fe3578f, 0x16ed5c82, 0x0dff4195, 0x04f14a98,
        0x73ab23d3, 0x7aa528de, 0x61b735c9, 0x68b93ec4, 0x57930fe7,
        0x5e9d04ea, 0x458f19fd, 0x4c8112f0, 0xab3bcb6b, 0xa235c066,
        0xb927dd71, 0xb029d67c, 0x8f03e75f, 0x860dec52, 0x9d1ff145,
        0x9411fa48, 0xe34b9303, 0xea45980e, 0xf1578519, 0xf8598e14,
        0xc773bf37, 0xce7db43a, 0xd56fa92d, 0xdc61a220, 0x76adf66d,
        0x7fa3fd60, 0x64b1e077, 0x6dbfeb7a, 0x5295da59, 0x5b9bd154,
        0x4089cc43, 0x4987c74e, 0x3eddae05, 0x37d3a508, 0x2cc1b81f,
        0x25cfb312, 0x1ae58231, 0x13eb893c, 0x08f9942b, 0x01f79f26,
        0xe64d46bd, 0xef434db0, 0xf45150a7, 0xfd5f5baa, 0xc2756a89,
        0xcb7b6184, 0xd0697c93, 0xd967779e, 0xae3d1ed5, 0xa73315d8,
        0xbc2108cf, 0xb52f03c2, 0x8a0532e1, 0x830b39ec, 0x981924fb,
        0x91172ff6, 0x4d768dd6, 0x447886db, 0x5f6a9bcc, 0x566490c1,
        0x694ea1e2, 0x6040aaef, 0x7b52b7f8, 0x725cbcf5, 0x0506d5be,
        0x0c08deb3, 0x171ac3a4, 0x1e14c8a9, 0x213ef98a, 0x2830f287,
        0x3322ef90, 0x3a2ce49d, 0xdd963d06, 0xd498360b, 0xcf8a2b1c,
        0xc6842011, 0xf9ae1132, 0xf0a01a3f, 0xebb20728, 0xe2bc0c25,
        0x95e6656e, 0x9ce86e63, 0x87fa7374, 0x8ef47879, 0xb1de495a,
        0xb8d04257, 0xa3c25f40, 0xaacc544d, 0xec41f7da, 0xe54ffcd7,
        0xfe5de1c0, 0xf753eacd, 0xc879dbee, 0xc177d0e3, 0xda65cdf4,
        0xd36bc6f9, 0xa431afb2, 0xad3fa4bf, 0xb62db9a8, 0xbf23b2a5,
        0x80098386, 0x8907888b, 0x9215959c, 0x9b1b9e91, 0x7ca1470a,
        0x75af4c07, 0x6ebd5110, 0x67b35a1d, 0x58996b3e, 0x51976033,
        0x4a857d24, 0x438b7629, 0x34d11f62, 0x3ddf146f, 0x26cd0978,
        0x2fc30275, 0x10e93356, 0x19e7385b, 0x02f5254c, 0x0bfb2e41,
        0xd79a8c61, 0xde94876c, 0xc5869a7b, 0xcc889176, 0xf3a2a055,
        0xfaacab58, 0xe1beb64f, 0xe8b0bd42, 0x9fead409, 0x96e4df04,
        0x8df6c213, 0x84f8c91e, 0xbbd2f83d, 0xb2dcf330, 0xa9ceee27,
        0xa0c0e52a, 0x477a3cb1, 0x4e7437bc, 0x55662aab, 0x5c6821a6,
        0x63421085, 0x6a4c1b88, 0x715e069f, 0x78500d92, 0x0f0a64d9,
        0x06046fd4, 0x1d1672c3, 0x141879ce, 0x2b3248ed, 0x223c43e0,
        0x392e5ef7, 0x302055fa, 0x9aec01b7, 0x93e20aba, 0x88f017ad,
        0x81fe1ca0, 0xbed42d83, 0xb7da268e, 0xacc83b99, 0xa5c63094,
        0xd29c59df, 0xdb9252d2, 0xc0804fc5, 0xc98e44c8, 0xf6a475eb,
        0xffaa7ee6, 0xe4b863f1, 0xedb668fc, 0x0a0cb167, 0x0302ba6a,
        0x1810a77d, 0x111eac70, 0x2e349d53, 0x273a965e, 0x3c288b49,
        0x35268044, 0x427ce90f, 0x4b72e202, 0x5060ff15, 0x596ef418,
        0x6644c53b, 0x6f4ace36, 0x7458d321, 0x7d56d82c, 0xa1377a0c,
        0xa8397101, 0xb32b6c16, 0xba25671b, 0x850f5638, 0x8c015d35,
        0x97134022, 0x9e1d4b2f, 0xe9472264, 0xe0492969, 0xfb5b347e,
        0xf2553f73, 0xcd7f0e50, 0xc471055d, 0xdf63184a, 0xd66d1347,
        0x31d7cadc, 0x38d9c1d1, 0x23cbdcc6, 0x2ac5d7cb, 0x15efe6e8,
        0x1ce1ede5, 0x07f3f0f2, 0x0efdfbff, 0x79a792b4, 0x70a999b9,
        0x6bbb84ae, 0x62b58fa3, 0x5d9fbe80, 0x5491b58d, 0x4f83a89a,
        0x468da397];

    var U4 = [0x00000000, 0x0e0b0d09, 0x1c161a12, 0x121d171b, 0x382c3424,
        0x3627392d, 0x243a2e36, 0x2a31233f, 0x70586848, 0x7e536541,
        0x6c4e725a, 0x62457f53, 0x48745c6c, 0x467f5165, 0x5462467e,
        0x5a694b77, 0xe0b0d090, 0xeebbdd99, 0xfca6ca82, 0xf2adc78b,
        0xd89ce4b4, 0xd697e9bd, 0xc48afea6, 0xca81f3af, 0x90e8b8d8,
        0x9ee3b5d1, 0x8cfea2ca, 0x82f5afc3, 0xa8c48cfc, 0xa6cf81f5,
        0xb4d296ee, 0xbad99be7, 0xdb7bbb3b, 0xd570b632, 0xc76da129,
        0xc966ac20, 0xe3578f1f, 0xed5c8216, 0xff41950d, 0xf14a9804,
        0xab23d373, 0xa528de7a, 0xb735c961, 0xb93ec468, 0x930fe757,
        0x9d04ea5e, 0x8f19fd45, 0x8112f04c, 0x3bcb6bab, 0x35c066a2,
        0x27dd71b9, 0x29d67cb0, 0x03e75f8f, 0x0dec5286, 0x1ff1459d,
        0x11fa4894, 0x4b9303e3, 0x45980eea, 0x578519f1, 0x598e14f8,
        0x73bf37c7, 0x7db43ace, 0x6fa92dd5, 0x61a220dc, 0xadf66d76,
        0xa3fd607f, 0xb1e07764, 0xbfeb7a6d, 0x95da5952, 0x9bd1545b,
        0x89cc4340, 0x87c74e49, 0xddae053e, 0xd3a50837, 0xc1b81f2c,
        0xcfb31225, 0xe582311a, 0xeb893c13, 0xf9942b08, 0xf79f2601,
        0x4d46bde6, 0x434db0ef, 0x5150a7f4, 0x5f5baafd, 0x756a89c2,
        0x7b6184cb, 0x697c93d0, 0x67779ed9, 0x3d1ed5ae, 0x3315d8a7,
        0x2108cfbc, 0x2f03c2b5, 0x0532e18a, 0x0b39ec83, 0x1924fb98,
        0x172ff691, 0x768dd64d, 0x7886db44, 0x6a9bcc5f, 0x6490c156,
        0x4ea1e269, 0x40aaef60, 0x52b7f87b, 0x5cbcf572, 0x06d5be05,
        0x08deb30c, 0x1ac3a417, 0x14c8a91e, 0x3ef98a21, 0x30f28728,
        0x22ef9033, 0x2ce49d3a, 0x963d06dd, 0x98360bd4, 0x8a2b1ccf,
        0x842011c6, 0xae1132f9, 0xa01a3ff0, 0xb20728eb, 0xbc0c25e2,
        0xe6656e95, 0xe86e639c, 0xfa737487, 0xf478798e, 0xde495ab1,
        0xd04257b8, 0xc25f40a3, 0xcc544daa, 0x41f7daec, 0x4ffcd7e5,
        0x5de1c0fe, 0x53eacdf7, 0x79dbeec8, 0x77d0e3c1, 0x65cdf4da,
        0x6bc6f9d3, 0x31afb2a4, 0x3fa4bfad, 0x2db9a8b6, 0x23b2a5bf,
        0x09838680, 0x07888b89, 0x15959c92, 0x1b9e919b, 0xa1470a7c,
        0xaf4c0775, 0xbd51106e, 0xb35a1d67, 0x996b3e58, 0x97603351,
        0x857d244a, 0x8b762943, 0xd11f6234, 0xdf146f3d, 0xcd097826,
        0xc302752f, 0xe9335610, 0xe7385b19, 0xf5254c02, 0xfb2e410b,
        0x9a8c61d7, 0x94876cde, 0x869a7bc5, 0x889176cc, 0xa2a055f3,
        0xacab58fa, 0xbeb64fe1, 0xb0bd42e8, 0xead4099f, 0xe4df0496,
        0xf6c2138d, 0xf8c91e84, 0xd2f83dbb, 0xdcf330b2, 0xceee27a9,
        0xc0e52aa0, 0x7a3cb147, 0x7437bc4e, 0x662aab55, 0x6821a65c,
        0x42108563, 0x4c1b886a, 0x5e069f71, 0x500d9278, 0x0a64d90f,
        0x046fd406, 0x1672c31d, 0x1879ce14, 0x3248ed2b, 0x3c43e022,
        0x2e5ef739, 0x2055fa30, 0xec01b79a, 0xe20aba93, 0xf017ad88,
        0xfe1ca081, 0xd42d83be, 0xda268eb7, 0xc83b99ac, 0xc63094a5,
        0x9c59dfd2, 0x9252d2db, 0x804fc5c0, 0x8e44c8c9, 0xa475ebf6,
        0xaa7ee6ff, 0xb863f1e4, 0xb668fced, 0x0cb1670a, 0x02ba6a03,
        0x10a77d18, 0x1eac7011, 0x349d532e, 0x3a965e27, 0x288b493c,
        0x26804435, 0x7ce90f42, 0x72e2024b, 0x60ff1550, 0x6ef41859,
        0x44c53b66, 0x4ace366f, 0x58d32174, 0x56d82c7d, 0x377a0ca1,
        0x397101a8, 0x2b6c16b3, 0x25671bba, 0x0f563885, 0x015d358c,
        0x13402297, 0x1d4b2f9e, 0x472264e9, 0x492969e0, 0x5b347efb,
        0x553f73f2, 0x7f0e50cd, 0x71055dc4, 0x63184adf, 0x6d1347d6,
        0xd7cadc31, 0xd9c1d138, 0xcbdcc623, 0xc5d7cb2a, 0xefe6e815,
        0xe1ede51c, 0xf3f0f207, 0xfdfbff0e, 0xa792b479, 0xa999b970,
        0xbb84ae6b, 0xb58fa362, 0x9fbe805d, 0x91b58d54, 0x83a89a4f,
        0x8da39746];

    this.prepareDecryption = function (key) {
        var r, w;
        var rk2 = new Array(maxrk + 1);
        var ctx = new AES.keyExpansion(key);
        var rounds = ctx.rounds;

        for (r = 0; r < maxrk + 1; r++) {
            rk2[r] = new Array(4);
            rk2[r][0] = ctx.rk[r][0];
            rk2[r][1] = ctx.rk[r][1];
            rk2[r][2] = ctx.rk[r][2];
            rk2[r][3] = ctx.rk[r][3];
        }

        for (r = 1; r < rounds; r++) {
            w = rk2[r][0];
            rk2[r][0] = U1[B0(w)] ^ U2[B1(w)] ^ U3[B2(w)] ^ U4[B3(w)];
            w = rk2[r][1];
            rk2[r][1] = U1[B0(w)] ^ U2[B1(w)] ^ U3[B2(w)] ^ U4[B3(w)];
            w = rk2[r][2];
            rk2[r][2] = U1[B0(w)] ^ U2[B1(w)] ^ U3[B2(w)] ^ U4[B3(w)];
            w = rk2[r][3];
            rk2[r][3] = U1[B0(w)] ^ U2[B1(w)] ^ U3[B2(w)] ^ U4[B3(w)];
        }
        this.rk = rk2;
        this.rounds = rounds;
        return this;
    }


    this.AESencrypt = function (block, ctx) {
        var r;
        var t0, t1, t2, t3;

        var b = packBytes(block);
        var rounds = ctx.rounds;
        var b0 = b[0];
        var b1 = b[1];
        var b2 = b[2];
        var b3 = b[3];

        for (r = 0; r < rounds - 1; r++) {
            t0 = b0 ^ ctx.rk[r][0];
            t1 = b1 ^ ctx.rk[r][1];
            t2 = b2 ^ ctx.rk[r][2];
            t3 = b3 ^ ctx.rk[r][3];

            b0 = T1[t0 & 255] ^ T2[(t1 >> 8) & 255] ^ T3[(t2 >> 16) & 255] ^ T4[t3 >>> 24];
            b1 = T1[t1 & 255] ^ T2[(t2 >> 8) & 255] ^ T3[(t3 >> 16) & 255] ^ T4[t0 >>> 24];
            b2 = T1[t2 & 255] ^ T2[(t3 >> 8) & 255] ^ T3[(t0 >> 16) & 255] ^ T4[t1 >>> 24];
            b3 = T1[t3 & 255] ^ T2[(t0 >> 8) & 255] ^ T3[(t1 >> 16) & 255] ^ T4[t2 >>> 24];
        }

        // last round is special
        r = rounds - 1;

        t0 = b0 ^ ctx.rk[r][0];
        t1 = b1 ^ ctx.rk[r][1];
        t2 = b2 ^ ctx.rk[r][2];
        t3 = b3 ^ ctx.rk[r][3];

        b[0] = F1(t0, t1, t2, t3) ^ ctx.rk[rounds][0];
        b[1] = F1(t1, t2, t3, t0) ^ ctx.rk[rounds][1];
        b[2] = F1(t2, t3, t0, t1) ^ ctx.rk[rounds][2];
        b[3] = F1(t3, t0, t1, t2) ^ ctx.rk[rounds][3];

        return unpackBytes(b);
    }


    this.AESdecrypt = function (block, ctx) {
        var r;
        var t0, t1, t2, t3;
        var rounds = ctx.rounds;

        var b = packBytes(block);

        for (r = rounds; r > 1; r--) {
            t0 = b[0] ^ ctx.rk[r][0];
            t1 = b[1] ^ ctx.rk[r][1];
            t2 = b[2] ^ ctx.rk[r][2];
            t3 = b[3] ^ ctx.rk[r][3];

            b[0] = T5[B0(t0)] ^ T6[B1(t3)] ^ T7[B2(t2)] ^ T8[B3(t1)];
            b[1] = T5[B0(t1)] ^ T6[B1(t0)] ^ T7[B2(t3)] ^ T8[B3(t2)];
            b[2] = T5[B0(t2)] ^ T6[B1(t1)] ^ T7[B2(t0)] ^ T8[B3(t3)];
            b[3] = T5[B0(t3)] ^ T6[B1(t2)] ^ T7[B2(t1)] ^ T8[B3(t0)];
        }

        // last round is special
        t0 = b[0] ^ ctx.rk[1][0];
        t1 = b[1] ^ ctx.rk[1][1];
        t2 = b[2] ^ ctx.rk[1][2];
        t3 = b[3] ^ ctx.rk[1][3];

        b[0] = S5[B0(t0)] | (S5[B1(t3)] << 8) | (S5[B2(t2)] << 16) | (S5[B3(t1)] << 24);
        b[1] = S5[B0(t1)] | (S5[B1(t0)] << 8) | (S5[B2(t3)] << 16) | (S5[B3(t2)] << 24);
        b[2] = S5[B0(t2)] | (S5[B1(t1)] << 8) | (S5[B2(t0)] << 16) | (S5[B3(t3)] << 24);
        b[3] = S5[B0(t3)] | (S5[B1(t2)] << 8) | (S5[B2(t1)] << 16) | (S5[B3(t0)] << 24);

        b[0] ^= ctx.rk[0][0];
        b[1] ^= ctx.rk[0][1];
        b[2] ^= ctx.rk[0][2];
        b[3] ^= ctx.rk[0][3];

        return unpackBytes(b);
    }
};


// jsbn.js

// Copyright (c) 2005  Tom Wu
// All Rights Reserved.
// See "LICENSE" for details.

// Basic JavaScript BN library - subset useful for RSA encryption.

// Bits per digit
var dbits;

// JavaScript engine analysis
var canary = 0xdeadbeefcafe;
var j_lm = ((canary & 0xffffff) == 0xefcafe);

// (public) Constructor
function BigInteger(a, b, c) {
    if (a != null)
        if ("number" == typeof a)
            this.fromNumber(a, b, c);
        else if (b == null && "string" != typeof a)
            this.fromString(a, 256);
        else
            this.fromString(a, b);
}

// return new, unset BigInteger
function nbi() {
    return new BigInteger(null);
}

// am: Compute w_j += (x*this_i), propagate carries,
// c is initial carry, returns final carry.
// c < 3*dvalue, x < 2*dvalue, this_i < dvalue
// We need to select the fastest one that works in this environment.
if (j_lm && (navigator.appName == "Microsoft Internet Explorer")) {
    // am2 avoids a big mult-and-extract completely.
    // Max digit bits should be <= 30 because we do bitwise ops
    // on values up to 2*hdvalue^2-hdvalue-1 (< 2^31)
    BigInteger.prototype.am = function (i, x, w, j, c, n) {
        var xl = x & 0x7fff, xh = x >> 15;
        while (--n >= 0) {
            var l = this[i] & 0x7fff;
            var h = this[i++] >> 15;
            var m = xh * l + h * xl;
            l = xl * l + ((m & 0x7fff) << 15) + w[j] + (c & 0x3fffffff);
            c = (l >>> 30) + (m >>> 15) + xh * h + (c >>> 30);
            w[j++] = l & 0x3fffffff;
        }
        return c;
    };
    dbits = 30;
} else if (j_lm && (navigator.appName != "Netscape")) {
    // am1: use a single mult and divide to get the high bits,
    // max digit bits should be 26 because
    // max internal value = 2*dvalue^2-2*dvalue (< 2^53)
    BigInteger.prototype.am = function (i, x, w, j, c, n) {
        while (--n >= 0) {
            var v = x * this[i++] + w[j] + c;
            c = Math.floor(v / 0x4000000);
            w[j++] = v & 0x3ffffff;
        }
        return c;
    };
    dbits = 26;
} else { // Mozilla/Netscape seems to prefer am3
    // Alternately, set max digit bits to 28 since some
    // browsers slow down when dealing with 32-bit numbers.
    BigInteger.prototype.am = function (i, x, w, j, c, n) {
        var xl = x & 0x3fff, xh = x >> 14;
        while (--n >= 0) {
            var l = this[i] & 0x3fff;
            var h = this[i++] >> 14;
            var m = xh * l + h * xl;
            l = xl * l + ((m & 0x3fff) << 14) + w[j] + c;
            c = (l >> 28) + (m >> 14) + xh * h;
            w[j++] = l & 0xfffffff;
        }
        return c;
    };
    dbits = 28;
}

BigInteger.prototype.DB = dbits;
BigInteger.prototype.DM = ((1 << dbits) - 1);
BigInteger.prototype.DV = (1 << dbits);

var BI_FP = 52;
BigInteger.prototype.FV = Math.pow(2, BI_FP);
BigInteger.prototype.F1 = BI_FP - dbits;
BigInteger.prototype.F2 = 2 * dbits - BI_FP;

// Digit conversions
var BI_RM = "0123456789abcdefghijklmnopqrstuvwxyz";
var BI_RC = new Array();
var rr, vv;
rr = "0".charCodeAt(0);
for (vv = 0; vv <= 9; ++vv)
    BI_RC[rr++] = vv;
rr = "a".charCodeAt(0);
for (vv = 10; vv < 36; ++vv)
    BI_RC[rr++] = vv;
rr = "A".charCodeAt(0);
for (vv = 10; vv < 36; ++vv)
    BI_RC[rr++] = vv;

function int2char(n) {
    return BI_RM.charAt(n);
}

function intAt(s, i) {
    var c = BI_RC[s.charCodeAt(i)];
    return (c == null) ? -1 : c;
}

// (protected) copy this to r
function bnpCopyTo(r) {
    for (var i = this.t - 1; i >= 0; --i)
        r[i] = this[i];
    r.t = this.t;
    r.s = this.s;
}

// (protected) set from integer value x, -DV <= x < DV
function bnpFromInt(x) {
    this.t = 1;
    this.s = (x < 0) ? -1 : 0;
    if (x > 0)
        this[0] = x;
    else if (x < -1)
        this[0] = x + this.DV;
    else
        this.t = 0;
}

// return bigint initialized to value
function nbv(i) {
    var r = nbi();
    r.fromInt(i);
    return r;
}

// (protected) set from string and radix
function bnpFromString(s, b) {
    var k;
    if (b == 16)
        k = 4;
    else if (b == 8)
        k = 3;
    else if (b == 256)
        k = 8; // byte array
    else if (b == 2)
        k = 1;
    else if (b == 32)
        k = 5;
    else if (b == 4)
        k = 2;
    else {
        this.fromRadix(s, b);
        return;
    }
    this.t = 0;
    this.s = 0;
    var i = s.length, mi = false, sh = 0;
    while (--i >= 0) {
        var x = (k == 8) ? s[i] & 0xff : intAt(s, i);
        if (x < 0) {
            if (s.charAt(i) == "-")
                mi = true;
            continue;
        }
        mi = false;
        if (sh == 0)
            this[this.t++] = x;
        else if (sh + k > this.DB) {
            this[this.t - 1] |= (x & ((1 << (this.DB - sh)) - 1)) << sh;
            this[this.t++] = (x >> (this.DB - sh));
        } else
            this[this.t - 1] |= x << sh;
        sh += k;
        if (sh >= this.DB)
            sh -= this.DB;
    }
    if (k == 8 && (s[0] & 0x80) != 0) {
        this.s = -1;
        if (sh > 0)
            this[this.t - 1] |= ((1 << (this.DB - sh)) - 1) << sh;
    }
    this.clamp();
    if (mi)
        BigInteger.ZERO.subTo(this, this);
}

// (protected) clamp off excess high words
function bnpClamp() {
    var c = this.s & this.DM;
    while (this.t > 0 && this[this.t - 1] == c)
        --this.t;
}

// (public) return string representation in given radix
function bnToString(b) {
    if (this.s < 0)
        return "-" + this.negate().toString(b);
    var k;
    if (b == 16)
        k = 4;
    else if (b == 8)
        k = 3;
    else if (b == 2)
        k = 1;
    else if (b == 32)
        k = 5;
    else if (b == 4)
        k = 2;
    else
        return this.toRadix(b);
    var km = (1 << k) - 1, d, m = false, r = "", i = this.t;
    var p = this.DB - (i * this.DB) % k;
    if (i-- > 0) {
        if (p < this.DB && (d = this[i] >> p) > 0) {
            m = true;
            r = int2char(d);
        }
        while (i >= 0) {
            if (p < k) {
                d = (this[i] & ((1 << p) - 1)) << (k - p);
                d |= this[--i] >> (p += this.DB - k);
            } else {
                d = (this[i] >> (p -= k)) & km;
                if (p <= 0) {
                    p += this.DB;
                    --i;
                }
            }
            if (d > 0)
                m = true;
            if (m)
                r += int2char(d);
        }
    }
    return m ? r : "0";
}

// (public) -this
function bnNegate() {
    var r = nbi();
    BigInteger.ZERO.subTo(this, r);
    return r;
}

// (public) |this|
function bnAbs() {
    return (this.s < 0) ? this.negate() : this;
}

// (public) return + if this > a, - if this < a, 0 if equal
function bnCompareTo(a) {
    var r = this.s - a.s;
    if (r != 0)
        return r;
    var i = this.t;
    r = i - a.t;
    if (r != 0)
        return (this.s < 0) ? -r : r;
    while (--i >= 0)
        if ((r = this[i] - a[i]) != 0)
            return r;
    return 0;
}

// returns bit length of the integer x
function nbits(x) {
    var r = 1, t;
    if ((t = x >>> 16) != 0) {
        x = t;
        r += 16;
    }
    if ((t = x >> 8) != 0) {
        x = t;
        r += 8;
    }
    if ((t = x >> 4) != 0) {
        x = t;
        r += 4;
    }
    if ((t = x >> 2) != 0) {
        x = t;
        r += 2;
    }
    if ((t = x >> 1) != 0) {
        x = t;
        r += 1;
    }
    return r;
}

// (public) return the number of bits in "this"
function bnBitLength() {
    if (this.t <= 0) return 0;
    return this.DB * (this.t - 1) + nbits(this[this.t - 1] ^ (this.s & this.DM));
}

// (protected) r = this << n*DB
function bnpDLShiftTo(n, r) {
    var i;
    for (i = this.t - 1; i >= 0; --i)
        r[i + n] = this[i];
    for (i = n - 1; i >= 0; --i)
        r[i] = 0;
    r.t = this.t + n;
    r.s = this.s;
}

// (protected) r = this >> n*DB
function bnpDRShiftTo(n, r) {
    for (var i = n; i < this.t; ++i)
        r[i - n] = this[i];
    r.t = Math.max(this.t - n, 0);
    r.s = this.s;
}

// (protected) r = this << n
function bnpLShiftTo(n, r) {
    var bs = n % this.DB;
    var cbs = this.DB - bs;
    var bm = (1 << cbs) - 1;
    var ds = Math.floor(n / this.DB), c = (this.s << bs) & this.DM, i;
    for (i = this.t - 1; i >= 0; --i) {
        r[i + ds + 1] = (this[i] >> cbs) | c;
        c = (this[i] & bm) << bs;
    }
    for (i = ds - 1; i >= 0; --i)
        r[i] = 0;
    r[ds] = c;
    r.t = this.t + ds + 1;
    r.s = this.s;
    r.clamp();
}

// (protected) r = this >> n
function bnpRShiftTo(n, r) {
    r.s = this.s;
    var ds = Math.floor(n / this.DB);
    if (ds >= this.t) {
        r.t = 0;
        return;
    }
    var bs = n % this.DB;
    var cbs = this.DB - bs;
    var bm = (1 << bs) - 1;
    r[0] = this[ds] >> bs;
    for (var i = ds + 1; i < this.t; ++i) {
        r[i - ds - 1] |= (this[i] & bm) << cbs;
        r[i - ds] = this[i] >> bs;
    }
    if (bs > 0)
        r[this.t - ds - 1] |= (this.s & bm) << cbs;
    r.t = this.t - ds;
    r.clamp();
}

// (protected) r = this - a
function bnpSubTo(a, r) {
    var i = 0, c = 0, m = Math.min(a.t, this.t);
    while (i < m) {
        c += this[i] - a[i];
        r[i++] = c & this.DM;
        c >>= this.DB;
    }
    if (a.t < this.t) {
        c -= a.s;
        while (i < this.t) {
            c += this[i];
            r[i++] = c & this.DM;
            c >>= this.DB;
        }
        c += this.s;
    } else {
        c += this.s;
        while (i < a.t) {
            c -= a[i];
            r[i++] = c & this.DM;
            c >>= this.DB;
        }
        c -= a.s;
    }
    r.s = (c < 0) ? -1 : 0;
    if (c < -1)
        r[i++] = this.DV + c;
    else if (c > 0)
        r[i++] = c;
    r.t = i;
    r.clamp();
}

// (protected) r = this * a, r != this,a (HAC 14.12)
// "this" should be the larger one if appropriate.
function bnpMultiplyTo(a, r) {
    var x = this.abs(), y = a.abs();
    var i = x.t;
    r.t = i + y.t;
    while (--i >= 0)
        r[i] = 0;
    for (i = 0; i < y.t; ++i)
        r[i + x.t] = x.am(0, y[i], r, i, 0, x.t);
    r.s = 0;
    r.clamp();
    if (this.s != a.s)
        BigInteger.ZERO.subTo(r, r);
}

// (protected) r = this^2, r != this (HAC 14.16)
function bnpSquareTo(r) {
    var x = this.abs();
    var i = r.t = 2 * x.t;
    while (--i >= 0)
        r[i] = 0;
    for (i = 0; i < x.t - 1; ++i) {
        var c = x.am(i, x[i], r, 2 * i, 0, 1);
        if ((r[i + x.t] += x.am(i + 1, 2 * x[i], r, 2 * i + 1, c, x.t - i - 1)) >= x.DV) {
            r[i + x.t] -= x.DV;
            r[i + x.t + 1] = 1;
        }
    }
    if (r.t > 0)
        r[r.t - 1] += x.am(i, x[i], r, 2 * i, 0, 1);
    r.s = 0;
    r.clamp();
}

// (protected) divide this by m, quotient and remainder to q, r (HAC 14.20)
// r != q, this != m. q or r may be null.
function bnpDivRemTo(m, q, r) {
    var pm = m.abs();
    if (pm.t <= 0)
        return;
    var pt = this.abs();
    if (pt.t < pm.t) {
        if (q != null)
            q.fromInt(0);
        if (r != null)
            this.copyTo(r);
        return;
    }
    if (r == null)
        r = nbi();
    var y = nbi(), ts = this.s, ms = m.s;
    var nsh = this.DB - nbits(pm[pm.t - 1]); // normalize modulus
    if (nsh > 0) {
        pm.lShiftTo(nsh, y);
        pt.lShiftTo(nsh, r);
    } else {
        pm.copyTo(y);
        pt.copyTo(r);
    }
    var ys = y.t;
    var y0 = y[ys - 1];
    if (y0 == 0)
        return;
    var yt = y0 * (1 << this.F1) + ((ys > 1) ? y[ys - 2] >> this.F2 : 0);
    var d1 = this.FV / yt, d2 = (1 << this.F1) / yt, e = 1 << this.F2;
    var i = r.t, j = i - ys, t = (q == null) ? nbi() : q;
    y.dlShiftTo(j, t);
    if (r.compareTo(t) >= 0) {
        r[r.t++] = 1;
        r.subTo(t, r);
    }
    BigInteger.ONE.dlShiftTo(ys, t);
    t.subTo(y, y); // "negative" y so we can replace sub with am later
    while (y.t < ys)
        y[y.t++] = 0;
    while (--j >= 0) {
        // Estimate quotient digit
        var qd = (r[--i] == y0) ? this.DM : Math.floor(r[i] * d1
            + (r[i - 1] + e) * d2);
        if ((r[i] += y.am(0, qd, r, j, 0, ys)) < qd) { // Try it out
            y.dlShiftTo(j, t);
            r.subTo(t, r);
            while (r[i] < --qd)
                r.subTo(t, r);
        }
    }
    if (q != null) {
        r.drShiftTo(ys, q);
        if (ts != ms)
            BigInteger.ZERO.subTo(q, q);
    }
    r.t = ys;
    r.clamp();
    if (nsh > 0)
        r.rShiftTo(nsh, r); // Denormalize remainder
    if (ts < 0)
        BigInteger.ZERO.subTo(r, r);
}

// (public) this mod a
function bnMod(a) {
    var r = nbi();
    this.abs().divRemTo(a, null, r);
    if (this.s < 0 && r.compareTo(BigInteger.ZERO) > 0)
        a.subTo(r, r);
    return r;
}

// Modular reduction using "classic" algorithm
function Classic(m) {
    this.m = m;
}

function cConvert(x) {
    if (x.s < 0 || x.compareTo(this.m) >= 0)
        return x.mod(this.m);
    else
        return x;
}

function cRevert(x) {
    return x;
}

function cReduce(x) {
    x.divRemTo(this.m, null, x);
}

function cMulTo(x, y, r) {
    x.multiplyTo(y, r);
    this.reduce(r);
}

function cSqrTo(x, r) {
    x.squareTo(r);
    this.reduce(r);
}

Classic.prototype.convert = cConvert;
Classic.prototype.revert = cRevert;
Classic.prototype.reduce = cReduce;
Classic.prototype.mulTo = cMulTo;
Classic.prototype.sqrTo = cSqrTo;

// (protected) return "-1/this % 2^DB"; useful for Mont. reduction
// justification:
// xy == 1 (mod m)
// xy = 1+km
// xy(2-xy) = (1+km)(1-km)
// x[y(2-xy)] = 1-k^2m^2
// x[y(2-xy)] == 1 (mod m^2)
// if y is 1/x mod m, then y(2-xy) is 1/x mod m^2
// should reduce x and y(2-xy) by m^2 at each step to keep size bounded.
// JS multiply "overflows" differently from C/C++, so care is needed here.
function bnpInvDigit() {
    if (this.t < 1)
        return 0;
    var x = this[0];
    if ((x & 1) == 0)
        return 0;
    var y = x & 3; // y == 1/x mod 2^2
    y = (y * (2 - (x & 0xf) * y)) & 0xf; // y == 1/x mod 2^4
    y = (y * (2 - (x & 0xff) * y)) & 0xff; // y == 1/x mod 2^8
    y = (y * (2 - (((x & 0xffff) * y) & 0xffff))) & 0xffff; // y == 1/x mod 2^16
    // last step - calculate inverse mod DV directly;
    // assumes 16 < DB <= 32 and assumes ability to handle 48-bit ints
    y = (y * (2 - x * y % this.DV)) % this.DV; // y == 1/x mod 2^dbits
    // we really want the negative inverse, and -DV < y < DV
    return (y > 0) ? this.DV - y : -y;
}

// Montgomery reduction
function Montgomery(m) {
    this.m = m;
    this.mp = m.invDigit();
    this.mpl = this.mp & 0x7fff;
    this.mph = this.mp >> 15;
    this.um = (1 << (m.DB - 15)) - 1;
    this.mt2 = 2 * m.t;
}

// xR mod m
function montConvert(x) {
    var r = nbi();
    x.abs().dlShiftTo(this.m.t, r);
    r.divRemTo(this.m, null, r);
    if (x.s < 0 && r.compareTo(BigInteger.ZERO) > 0)
        this.m.subTo(r, r);
    return r;
}

// x/R mod m
function montRevert(x) {
    var r = nbi();
    x.copyTo(r);
    this.reduce(r);
    return r;
}

// x = x/R mod m (HAC 14.32)
function montReduce(x) {
    while (x.t <= this.mt2)
        // pad x so am has enough room later
        x[x.t++] = 0;
    for (var i = 0; i < this.m.t; ++i) {
        // faster way of calculating u0 = x[i]*mp mod DV
        var j = x[i] & 0x7fff;
        var u0 = (j * this.mpl + (((j * this.mph + (x[i] >> 15) * this.mpl) & this.um) << 15))
            & x.DM;
        // use am to combine the multiply-shift-add into one call
        j = i + this.m.t;
        x[j] += this.m.am(0, u0, x, i, 0, this.m.t);
        // propagate carry
        while (x[j] >= x.DV) {
            x[j] -= x.DV;
            x[++j]++;
        }
    }
    x.clamp();
    x.drShiftTo(this.m.t, x);
    if (x.compareTo(this.m) >= 0)
        x.subTo(this.m, x);
}

// r = "x^2/R mod m"; x != r
function montSqrTo(x, r) {
    x.squareTo(r);
    this.reduce(r);
}

// r = "xy/R mod m"; x,y != r
function montMulTo(x, y, r) {
    x.multiplyTo(y, r);
    this.reduce(r);
}

Montgomery.prototype.convert = montConvert;
Montgomery.prototype.revert = montRevert;
Montgomery.prototype.reduce = montReduce;
Montgomery.prototype.mulTo = montMulTo;
Montgomery.prototype.sqrTo = montSqrTo;

// (protected) true iff this is even
function bnpIsEven() {
    return ((this.t > 0) ? (this[0] & 1) : this.s) == 0;
}

// (protected) this^e, e < 2^32, doing sqr and mul with "r" (HAC 14.79)
function bnpExp(e, z) {
    if (e > 0xffffffff || e < 1)
        return BigInteger.ONE;
    var r = nbi(), r2 = nbi(), g = z.convert(this), i = nbits(e) - 1;
    g.copyTo(r);
    while (--i >= 0) {
        z.sqrTo(r, r2);
        if ((e & (1 << i)) > 0)
            z.mulTo(r2, g, r);
        else {
            var t = r;
            r = r2;
            r2 = t;
        }
    }
    return z.revert(r);
}

// (public) this^e % m, 0 <= e < 2^32
function bnModPowInt(e, m) {
    var z;
    if (e < 256 || m.isEven())
        z = new Classic(m);
    else
        z = new Montgomery(m);
    return this.exp(e, z);
}

// protected
BigInteger.prototype.copyTo = bnpCopyTo;
BigInteger.prototype.fromInt = bnpFromInt;
BigInteger.prototype.fromString = bnpFromString;
BigInteger.prototype.clamp = bnpClamp;
BigInteger.prototype.dlShiftTo = bnpDLShiftTo;
BigInteger.prototype.drShiftTo = bnpDRShiftTo;
BigInteger.prototype.lShiftTo = bnpLShiftTo;
BigInteger.prototype.rShiftTo = bnpRShiftTo;
BigInteger.prototype.subTo = bnpSubTo;
BigInteger.prototype.multiplyTo = bnpMultiplyTo;
BigInteger.prototype.squareTo = bnpSquareTo;
BigInteger.prototype.divRemTo = bnpDivRemTo;
BigInteger.prototype.invDigit = bnpInvDigit;
BigInteger.prototype.isEven = bnpIsEven;
BigInteger.prototype.exp = bnpExp;

// public
BigInteger.prototype.toString = bnToString;
BigInteger.prototype.negate = bnNegate;
BigInteger.prototype.abs = bnAbs;
BigInteger.prototype.compareTo = bnCompareTo;
BigInteger.prototype.bitLength = bnBitLength;
BigInteger.prototype.mod = bnMod;
BigInteger.prototype.modPowInt = bnModPowInt;

// "constants"
BigInteger.ZERO = nbv(0);
BigInteger.ONE = nbv(1);

// prng4.js - uses Arcfour as a PRNG

function Arcfour() {
    this.i = 0;
    this.j = 0;
    this.S = new Array();
}

// Initialize arcfour context from key, an array of ints, each from [0..255]
Arcfour.prototype.init = function (key) {
    var i, j, t;
    for (i = 0; i < 256; ++i)
        this.S[i] = i;
    j = 0;
    for (i = 0; i < 256; ++i) {
        j = (j + this.S[i] + key[i % key.length]) & 255;
        t = this.S[i];
        this.S[i] = this.S[j];
        this.S[j] = t;
    }
    this.i = 0;
    this.j = 0;
};
Arcfour.prototype.next = function () {
    var t;
    this.i = (this.i + 1) & 255;
    this.j = (this.j + this.S[this.i]) & 255;
    t = this.S[this.i];
    this.S[this.i] = this.S[this.j];
    this.S[this.j] = t;
    return this.S[(t + this.S[this.i]) & 255];
};

// Plug in your RNG constructor here
function prng_newstate() {
    return new Arcfour();
}

// Pool size must be a multiple of 4 and greater than 32.
// An array of bytes the size of the pool will be passed to init()
var rng_psize = 256;

// Random number generator - requires a PRNG backend, e.g. prng4.js

// For best results, put code like
// <body onClick='rng_seed_time();' onKeyPress='rng_seed_time();'>
// in your main HTML document.

var rng_state;
var rng_pool;
var rng_pptr;

// Mix in a 32-bit integer into the pool
function rng_seed_int(x) {
    rng_pool[rng_pptr++] ^= x & 255;
    rng_pool[rng_pptr++] ^= (x >> 8) & 255;
    rng_pool[rng_pptr++] ^= (x >> 16) & 255;
    rng_pool[rng_pptr++] ^= (x >> 24) & 255;
    if (rng_pptr >= rng_psize)
        rng_pptr -= rng_psize;
}

// Mix in the current time (w/milliseconds) into the pool
function rng_seed_time() {
    rng_seed_int(new Date().getTime());
}

// Initialize the pool with junk if needed.
if (rng_pool == null) {
    rng_pool = new Array();
    rng_pptr = 0;
    var t;
    if (window.crypto && window.crypto.getRandomValues) {
        // Use webcrypto if available
        var ua = new Uint8Array(32);
        window.crypto.getRandomValues(ua);
        for (t = 0; t < 32; ++t)
            rng_pool[rng_pptr++] = ua[t];
    }
    if (navigator.appName == "Netscape" && navigator.appVersion < "5"
        && window.crypto) {
        // Extract entropy (256 bits) from NS4 RNG if available
        var z = window.crypto.random(32);
        for (t = 0; t < z.length; ++t)
            rng_pool[rng_pptr++] = z.charCodeAt(t) & 255;
    }
    while (rng_pptr < rng_psize) {

        t = Math.floor(65536 * Math.random());

        rng_pool[rng_pptr++] = t >>> 8;
        rng_pool[rng_pptr++] = t & 255;
    }
    rng_pptr = 0;
    rng_seed_time();
    // rng_seed_int(window.screenX);
    // rng_seed_int(window.screenY);
}

function rng_get_byte() {
    if (rng_state == null) {
        rng_seed_time();
        rng_state = prng_newstate();
        rng_state.init(rng_pool);
        for (rng_pptr = 0; rng_pptr < rng_pool.length; ++rng_pptr)
            rng_pool[rng_pptr] = 0;
        rng_pptr = 0;
        // rng_pool = null;
    }
    // TODO: allow reseeding after first request
    return rng_state.next();
}

function rng_get_bytes(ba) {
    var i;
    for (i = 0; i < ba.length; ++i)
        ba[i] = rng_get_byte();
}

function SecureRandom() {
}

SecureRandom.prototype.nextBytes = rng_get_bytes;

// Depends on jsbn.js and rng.js
// Version 1.1: support utf-8 encoding in pkcs1pad2
// convert a (hex) string to a bignum object
function parseBigInt(str, r) {
    return new BigInteger(str, r);
}

function linebrk(s, n) {
    var ret = "";
    var i = 0;
    while (i + n < s.length) {
        ret += s.substring(i, i + n) + "\n";
        i += n;
    }
    return ret + s.substring(i, s.length);
}

function byte2Hex(b) {
    if (b < 0x10)
        return "0" + b.toString(16);
    else
        return b.toString(16);
}

// PKCS#1 (type 2, random) pad input string s to n bytes, and return a bigint
function pkcs1pad2(s, n) {
    if (n < s.length + 11) { // TODO: fix for utf-8
        alert("Message too long for RSA");
        return null;
    }
    var ba = new Array();
    var i = s.length - 1;
    while (i >= 0 && n > 0) {
        var c = s.charCodeAt(i--);
        if (c < 128) { // encode using utf-8
            ba[--n] = c;
        } else if ((c > 127) && (c < 2048)) {
            ba[--n] = (c & 63) | 128;
            ba[--n] = (c >> 6) | 192;
        } else {
            ba[--n] = (c & 63) | 128;
            ba[--n] = ((c >> 6) & 63) | 128;
            ba[--n] = (c >> 12) | 224;
        }
    }
    ba[--n] = 0;
    var rng = new SecureRandom();
    var x = new Array();
    while (n > 2) { // random non-zero pad
        x[0] = 0;
        while (x[0] == 0)
            rng.nextBytes(x);
        ba[--n] = x[0];
    }
    ba[--n] = 2;
    ba[--n] = 0;
    return new BigInteger(ba);
}

// "empty" RSA key constructor
function RSAKey() {
    this.n = null;
    this.e = 0;
    this.d = null;
    this.p = null;
    this.q = null;
    this.dmp1 = null;
    this.dmq1 = null;
    this.coeff = null;
}

RSAKey.prototype = {
    // Perform raw public operation on "x": return x^e (mod n)
    doPublic: function (x) {
        return x.modPowInt(this.e, this.n);
    },

    //Return the PKCS#1 RSA encryption of "text" as an even-length hex string
    setPublic: function (N, E) {
        if (N != null && E != null && N.length > 0 && E.length > 0) {
            this.n = parseBigInt(N, 16);
            this.e = parseInt(E, 16);
        } else {
            //npConsole.log("N : " + N);
            //npConsole.log("E : " + E);
            //alert("Invalid RSA public key");
            npConsole.log("Invalid RSA public key");
        }
    },

    //Return the PKCS#1 RSA encryption of "text" as a Base64-encoded string
    encrypt: function (text) {
        var m = pkcs1pad2(text, (this.n.bitLength() + 7) >> 3);
        if (m == null)
            return null;
        var c = this.doPublic(m);
        if (c == null)
            return null;
        var h = c.toString(16);
        if ((h.length & 1) == 0)
            return h;
        else
            return "0" + h;
    },


    encrypt_b64: function (text) {
        var h = this.encrypt(text);
        if (h) return hex2b64(h);
        else return null;
    }
}


var Base64 = {
    base64: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
    encode: function ($input) {
        if (!$input) {
            return false;
        }
        //$input = UTF8.encode($input);
        var $output = "";
        var $chr1, $chr2, $chr3;
        var $enc1, $enc2, $enc3, $enc4;
        var $i = 0;
        do {
            $chr1 = $input.charCodeAt($i++);
            $chr2 = $input.charCodeAt($i++);
            $chr3 = $input.charCodeAt($i++);
            $enc1 = $chr1 >> 2;
            $enc2 = (($chr1 & 3) << 4) | ($chr2 >> 4);
            $enc3 = (($chr2 & 15) << 2) | ($chr3 >> 6);
            $enc4 = $chr3 & 63;
            if (isNaN($chr2)) $enc3 = $enc4 = 64;
            else if (isNaN($chr3)) $enc4 = 64;
            $output += this.base64.charAt($enc1) + this.base64.charAt($enc2) + this.base64.charAt($enc3) + this.base64.charAt($enc4);
        } while ($i < $input.length);
        return $output;
    },
    decode: function ($input) {
        if (!$input) return false;
        $input = $input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
        var $output = "";
        var $enc1, $enc2, $enc3, $enc4;
        var $i = 0;
        do {
            $enc1 = this.base64.indexOf($input.charAt($i++));
            $enc2 = this.base64.indexOf($input.charAt($i++));
            $enc3 = this.base64.indexOf($input.charAt($i++));
            $enc4 = this.base64.indexOf($input.charAt($i++));
            $output += String.fromCharCode(($enc1 << 2) | ($enc2 >> 4));
            if ($enc3 != 64) $output += String.fromCharCode((($enc2 & 15) << 4) | ($enc3 >> 2));
            if ($enc4 != 64) $output += String.fromCharCode((($enc3 & 3) << 6) | $enc4);
        } while ($i < $input.length);
        return $output; //UTF8.decode($output);
    }
};

var Hex = {
    hex: "0123456789abcdef",
    encode: function ($input) {
        if (!$input) return false;
        var $output = "";
        var $k;
        var $i = 0;
        do {
            $k = $input.charCodeAt($i++);
            $output += this.hex.charAt(($k >> 4) & 0xf) + this.hex.charAt($k & 0xf);
        } while ($i < $input.length);
        return $output;
    },
    decode: function ($input) {
        if (!$input) return false;
        $input = $input.replace(/[^0-9abcdef]/g, "");
        var $output = "";
        var $i = 0;
        do {
            $output += String.fromCharCode(((this.hex.indexOf($input.charAt($i++)) << 4) & 0xf0) | (this.hex.indexOf($input.charAt($i++)) & 0xf));
        } while ($i < $input.length);
        return $output;
    }
};

var ASN1Data = function ($data) {
    this.error = false;
    this.parse = function ($data) {
        if (!$data) {
            this.error = true;
            return null;
        }
        var $result = [];
        while ($data.length > 0) {
            // get the tag
            var $tag = $data.charCodeAt(0);
            $data = $data.substr(1);
            // get length
            var $length = 0;
            // ignore any null tag
            if (($tag & 31) == 0x5) $data = $data.substr(1);
            else {
                if ($data.charCodeAt(0) & 128) {
                    var $lengthSize = $data.charCodeAt(0) & 127;
                    $data = $data.substr(1);
                    if ($lengthSize > 0) $length = $data.charCodeAt(0);
                    if ($lengthSize > 1) $length = (($length << 8) | $data.charCodeAt(1));
                    if ($lengthSize > 2) {
                        this.error = true;
                        return null;
                    }
                    $data = $data.substr($lengthSize);
                } else {
                    $length = $data.charCodeAt(0);
                    $data = $data.substr(1);
                }
            }
            // get value
            var $value = "";
            if ($length) {
                if ($length > $data.length) {
                    this.error = true;
                    return null;
                }
                $value = $data.substr(0, $length);
                $data = $data.substr($length);
            }
            if ($tag & 32)
                $result.push(this.parse($value)); // sequence
            else
                $result.push(this.value(($tag & 128) ? 4 : ($tag & 31), $value));
        }
        return $result;
    };
    this.value = function ($tag, $data) {
        if ($tag == 1)
            return $data ? true : false;
        else if ($tag == 2) //integer
            return $data;
        else if ($tag == 3) //bit string
            return this.parse($data.substr(1));
        else if ($tag == 5) //null
            return null;
        else if ($tag == 6) { //ID
            var $res = [];
            var $d0 = $data.charCodeAt(0);
            $res.push(Math.floor($d0 / 40));
            $res.push($d0 - $res[0] * 40);
            var $stack = [];
            var $powNum = 0;
            var $i;
            for ($i = 1; $i < $data.length; $i++) {
                var $token = $data.charCodeAt($i);
                $stack.push($token & 127);
                if ($token & 128)
                    $powNum++;
                else {
                    var $j;
                    var $sum = 0;
                    for ($j = 0; $j < $stack.length; $j++)
                        $sum += $stack[$j] * Math.pow(128, $powNum--);
                    $res.push($sum);
                    $powNum = 0;
                    $stack = [];
                }
            }
            return $res.join(".");
        }
        return null;
    }
    this.data = this.parse($data);
};


var RSAPublicKey = function ($modulus, $encryptionExponent) {
    //    this.modulus = new BigInteger(Hex.encode($modulus), 16);			// n value
    //    this.encryptionExponent = new BigInteger(Hex.encode($encryptionExponent), 16);		// e value
    this.modulus = Hex.encode($modulus); // n value
    this.encryptionExponent = Hex.encode($encryptionExponent); // e value
};

var getPublicKey = function ($pem) {
    if ($pem.length < 50) {
        return false;
    }
    if ($pem.substr(0, 26) != "-----BEGIN PUBLIC KEY-----") {
        return false;
    }
    $pem = $pem.substr(26);
    if ($pem.substr($pem.length - 24) != "-----END PUBLIC KEY-----") {
        return false;
    }
    $pem = $pem.substr(0, $pem.length - 24);
    $pem = new ASN1Data(Base64.decode($pem));
    if ($pem.error)
        return false;
    $pem = $pem.data;
    if ($pem[0][0][0] == "1.2.840.113549.1.1.1") {
        return new RSAPublicKey($pem[0][1][0][0], $pem[0][1][0][1]);
    }
    return false;
};


// 기존 함수 호환용
function npPfsStartup(form, firewall, securekey, fds, keypad, e2eattr, e2eval) {
    npPfsStartupV2(form, [firewall, securekey, fds, keypad], e2eattr, e2eval);
}

var startupParameters = null;

function npPfsStartupV2(form, flags, e2eattr, e2eval) {
    var flags = flags || [];
    var firewall = false;
    var securekey = false;
    var fds = false;
    var keypad = false;
    var submit = false;
    var device = false;
    var pinauth = false;
    for (var i = 0; i < flags.length; i++) {
        switch (i) {
            case 0 :
                firewall = flags[i];
                break;
            case 1 :
                securekey = flags[i];
                break;
            case 2 :
                fds = flags[i];
                break;
            case 3 :
                keypad = flags[i];
                break;
            case 4 :
                submit = flags[i];
                break;
            case 5 :
                device = flags[i];
                break;
            case 6 :
                pinauth = flags[i];
                break;
        }
    }

    var options = {
        Firewall: firewall,
        SecureKey: securekey,
        Fds: fds,
        Keypad: keypad,
        Submit: submit,
        Device: device,
        PinAuth: pinauth,
        AutoStartup: true,
        Debug: true,
        Form: !!!form ? null : form,
        AutoScanAttrName: e2eattr || "npkencrypt",
//		AutoScanAttrName : e2eattr || "enc",
        AutoScanAttrValue: e2eval || "on",
        MoveToInstall: function (url, isUpdate, useLayer, callback) {
            callback = callback || function () {
            };
            var obj = typeof npMessage != "undefined" ? npMessage : window.N;
            var message = isUpdate ? obj.m96 : obj.m95;
            if (url !== null && url !== "") {
                if (useLayer) {
                    startupParameters = {form: form, flags: flags, e2eattr: e2eattr, e2eval: e2eval};
                    url = url + "?redirect=" + encodeURIComponent(location.href);
                    try {
                        L.showInstallLayer(url);
                    } catch (e) {
                        npCommon.showInstallLayer(url);
                    }

                    return;
                }

                if (confirm(message)) {
                    callback(false);
                    var postback = document.getElementById("nppfs-postback");
                    if (!!postback && postback.tagName.toLowerCase() == "form") {
                        postback.action = url;
                        postback.submit();
                        return;
                    }

                    url = url + "?redirect=" + encodeURIComponent(location.href);
                    var a = document.createElement("a");
                    if (a.click) {
                        a.setAttribute("href", url);
                        a.style.display = "none";
                        document.body.appendChild(a);
                        a.click();
                        return;
                    }

                    location.href = url;
                    //location.replace(url);
                } else {

//					npPfsCtrl.setCookie("npPfsIgnore", "true");
                    if (options.Keypad || options.Submit || options.Device) {
                        //npPfsCtrl.isStarting = false;
                        //npPfsCtrl.terminate = false;
                        //npPfsCtrl.isStartup =false;

                        options.Firewall = false;
                        options.SecureKey = false;
                        options.Fds = false;

                        npPfsCtrl.launch(options);
                    } else {
                        callback(true);
                    }

                }
            } else {
                callback(true);
                alert((typeof (npMessage) != "undefined") ? npMessage.m97 : N.m97);
            }
        },
        Loading: {
            Default: true,
            Before: function () {
                //alert("작업시작 전에 사용자 로딩함수를 여기에 구현합니다.");
            },
            After: function () {
                //alert("작업시작 후에 사용자 로딩함수를 여기에 구현합니다.");
            }
        }

    };

    //npPfsCtrl.setCookie("npPfsIgnore", "");
//	if("true" !== npPfsCtrl.getCookie("npPfsIgnore")) {
//		npPfsCtrl.setCookie("npPfsIgnore", "");
    npPfsCtrl.launch(options);
//	}

    /*
	npPfsCtrl.isInstall({
		success:function() {
			options.Loading.Default = false;
			npPfsCtrl.launch(options);
		},
		fail : function() {
			options.Loading.Default = true;
			npPfsCtrl.launch(options);
		}
	});
*/
}

/*
w.uV.dV.Gf = "/pluginfree/jsp/nppfs.key.jsp";    // 키발급 경로
w.uV.dV.zf = "/pluginfree/jsp/nppfs.remove.jsp"; // 키삭제 경로
w.uV.dV.zo = "/pluginfree/jsp/nppfs.keypad.jsp;  // 마우스입력기 페이지
w.uV.dV.eP = "/pluginfree/jsp/nppfs.ready.jsp";  // 초기화상태 확인경로
w.uV.dV.Fz = "/pluginfree/jsp/nppfs.install.jsp; // 설치안내 페이지
w.uV.dV.de = "/pluginfree/jsp/nppfs.session.jsp; // 세션유지 페이지
w.uV.dV.iB = "/pluginfree/jsp/nppfs.submit.jsp; // 구간암호화 페이지
 */

/*
function checkInstallKeyCryptPlugin(){
	if(typeof(bh) == "undefined") {
		return false;
	}
	if(typeof(D) != "undefined" && D.virtualMachine == true){
		return false;
	}
	return true;
}

npPfsCtrl.SetGlobalKeyValidation(function(keyCode, element) {
	//console.log("global key validataion");
	// true : do process biz logic, false : stop event
	if(keyCode >= 48 && keyCode <=57) return false;
	return true;
});


npPfsCtrl.makeJson = function(original, formname, keyName){
	var ret = original;

	if(typeof(ret) == "undefined" || ret == null) ret = {};
	if(typeof(keyName) == "undefined" || keyName == null || keyName == "") keyName = "__nppfs_json_vo__";

	ret[keyName] = npPfsCtrl.toJson(formname);

	return original;
}
*/


/*
 * ----- NOS 확장기능 스크립트 -----
 *  npPfsStartup() 함수 호출 전 선언되야 함
 * ------------------------------
 * 1. 키 유효성체크
 * 2. 페이지 벗어남 경고
 * 3. 키보드보안 초기화 전 추가 옵션적용
 * 4. 마우스입력기 초기화 전 추가 옵션적용
 * 5. 단말정보수집 추가정보 데이터 반환
 *//*
npPfsExtension = new function() {
	// 입력양식의 키 유효성 체크
	this.keyValidation = function(element, keyCode) {
		// 0 = 48, 9 = 57, a = 97, z = 122, A = 65, Z = 90
		var key = parseInt("" + keyCode);
		if(key < 48 || key > 57) {
			return false;
		}

		return true;			// true : 입력가능문자, false : 정합성불가/입력불가문자
	},
	// 페이지 벗어나기 전의 경고메시지 추가
	this.beforeFinalize = function(event) {
		if(false) {
			event = (event || window.event);
			var m = '작업이 아직 진행중에 있습니다. 저장하지 않은 채로 다른 페이지로 이동하시겠습니까?';  // a space
			(event || window.event).returnValue = m;
			return m;
		}
		return null;
	},
	// 키보드보안 초기화 전 추가 옵션적용
	this.secureKeyUiModifier = function(element) {
		var attr = jQuery(element).attr("enc");
		if(typeof(attr) == "undefined" || attr == "") {
			jQuery(element).attr({"enc" : "off"});
		}
	},
	// 마우스입력기 초기화 전 추가 옵션적용
	this.keypadUiModifier = function(element) {

	},
	// 단말정보수집 추가정보 데이터 반환
	this.additionalData = function() {
		return "";
	}
};

// 필드 색상 변경
npPfsCtrl.setColor({
	TextColor : "", 			// 키보드보안 글자 색상
	FieldBgColor : "", 			// 키보드보안 배경 색상
	ReTextColor : "", 			// 키보드보안 치환 글자 색상
	ReFieldBgColor : "", 		// 키보드보안 치환 배경 색상
	OnTextColor : "#FF0000", 	// 마우스입력기 포커스 글자 색상
	OnFieldBgColor : "#0100FF", // 마우스입력기 포커스 배경 색상
	OffTextColor : "#1DDB16", 	// 마우스입력기 글자 색상
	OffFieldBgColor : "#FF007F" // 마우스입력기 배경 색상
});



jQuery(document).on("nppfs-npv-enabled", function(event){
	console.log(event.message);
});
jQuery(document).on("nppfs-npv-disabled", function(event){
	console.log(event.message);
});

jQuery(document).on("nppfs-npv-before-show", function(event){
	console.log(event.message);
});

jQuery(document).on("nppfs-npv-after-show", function(event){
	console.log(event.message);
});

jQuery(document).on("nppfs-npv-after-hide", function(event){
	console.log(event.message);
});

$(document).ready(function(){
	$(document).bind("nppfs-npk-focusin nppfs-npk-focusout", function(e){
		var element = e.target;
		var type = $(element).attr("data-format");
		if(type == "num") {
		}
		//console.log(e.type + " : " + element.name);
		switch(e.type) {
			case "nppfs-npk-focusin" :
				break;
			case "nppfs-npk-focusout" :
			break;
		}
	});
});
*/


