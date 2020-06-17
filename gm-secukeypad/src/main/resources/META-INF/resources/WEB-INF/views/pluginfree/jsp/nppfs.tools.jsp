<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%>
<%@page import="com.nprotect.common.cipher.Cipher"
%>
<%@page import="com.nprotect.common.cipher.wrapper.RijndaelWrapper"
%>
<%@page import="com.nprotect.pluginfree.PluginFree"
%>
<%@page import="com.nprotect.pluginfree.PluginFreeDTO"
%>
<%@page import="com.nprotect.pluginfree.PluginFreeException"
%>
<%@page import="com.nprotect.pluginfree.modules.PluginFreeRequest"
%>
<%@page import="com.nprotect.pluginfree.util.EncryptUtil"
%>
<%@page import="com.nprotect.pluginfree.util.StringUtil"
%>
<%@page import="com.nprotect.pluginfree.util.ToolUtil"
%>
<%@page import="java.text.SimpleDateFormat"
%>
<%@page import="java.util.*"
%>
<%
    // 필요에 따라서 아래의 접근경로를 수정하십시오.
    String currentRequestURI = request.getRequestURI();
//String currentRequestURI = "/pluginfree/jsp/nppfs.tools.jsp";
%><%!
    private String n2b(String str) {
        return n2b(str, "");
    }

    private String n2b(String str, String tovalue) {
        return (str == null) ? tovalue : str;
    }

    private boolean isNull(String str) {
        return (str == null) ? true : false;
    }

    private boolean isBlank(String str) {
        return (str == null || "".equals(str)) ? true : false;
    }

    private String bool(boolean value) {
        return value ? "true" : "false";
    }

    private String bool(String value) {
        return bool(value, "Y");
    }

    private String bool(HttpServletRequest request, String key) {
        return bool(request.getParameter(key), "Y");
    }

    private String bool(String value, String key) {
        return n2b(value).equals(key) ? "true" : "false";
    }

    private String param(HttpServletRequest request, String key) {
        return n2b(request.getParameter(key));
    }

    private String param(HttpServletRequest request, String key, String def) {
        return n2b(request.getParameter(key), def);
    }
%><%
    String remoteAddr = request.getRemoteAddr();
    String serverName = request.getServerName();

    if ((
            request.getServerName().indexOf("192.168.1.111") >= 0
                    || request.getServerName().indexOf("192.168.1.24") >= 0
                    || request.getServerName().indexOf("192.168.1.27") >= 0
    )
            && (
            "192.168.1.111".equals(remoteAddr)
                    || !"192.168.1.24".equals(remoteAddr)
                    || !"192.168.1.85".equals(remoteAddr)
                    || !"127.0.0.1".equals(remoteAddr)
    )) {
        ToolUtil.login(ToolUtil.SET_LOGIN, session);
    }


//System.out.println("URL : " + request.getRequestURL().toString());
//System.out.println("URI : " + request.getRequestURI().toString());
//System.out.println("PATH : " + request.getPathInfo());


    if ("HTTP/1.1".equals(request.getProtocol())) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    } else {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
    }
%><%


    // 요구사항1. 시간을 통한 동적암호로 본 페이지 인증처리
// 요구사항2. 시간만료를 통한 본 페이지 인증로그아웃 처리
// 요구사항3. 한 페이지만으로 처리가 가능하도록 처리
/*
기능 목록
0. 운영환경 테스트
   1. 쿠키/세션지원 가능 여부 확인
   2. 암/복호화 모듈 구동가능여부 테스트
   3. 통신확인
      - 다운로드/업데이트 서버(https://supdate.nprotect.net) 접속 확인
      - NOS로컬 대몬 (https://pfs.nprotect.com) 접속 확인
1. ARIA 암호화/복호화
2. AES 암호화/복호화
3. 고객 코드 확인
4. 지원기능(라이선스) 확인
    지원기능 선택에 의한 동작 테스트
5. 현재 쿠키/세션값 상황보기
6. 명령어 분석
7. 키보드보안 응답 데이터 복호화
8. 변수치환 목록
*/

    String mode = param(request, "m", "page");
    if ("css".equals(mode)) {
        response.setContentType("text/css");
%>@charset "utf-8";
body,td, th{
font-family:'NanumGothic', '나눔고딕','nng', '맑은 고딕', 'Malgun Gothic', Dotum, sans-serif;
font-size:10pt;
}
input, textarea{font-size:9pt;}

.box {
border : 1px solid #000;
width : 300px;
height : 650px;
overflow : auto;
padding : 3px;
}
.box.readonly {
background-color : #eeeeff;
}
.block-replace-mapping {
border : 1px solid #cccccc;
width : 520px;
height : 250px;
}
.input-text {
border : 1px solid #bbbbbb;
background-color : white;
width : 300px;
}
.input-text.small {
width : 100px;
}
.input-text.middle {
width : 200px;
}
.input-text.tiny {
width : 60px;
}
.input-text.readonly {
background-color : #eeeeff;
}

table th {
white-space: nowrap
}
a, u {
text-decoration: none;
}
pre {
margin-top: 2px;
margin-bottom : 2px;
}
.btn {
border-radius: 0;
-moz-border-radius: 0;
-webkit-border-radius: 0;
}
.modal-dialog {
display : none;
position : fixed;
top : 0;
left : 0;
height : 100%;
width : 100%;
background : rgba( 180, 180, 180, .5);
z-index : 888;
text-align:center !important;
}
.modal-dialog .content {
z-index : 889;

position : fixed;
border:1px solid #7c786f;

background-color : #ffffff;

width:610px;
height:430px;
left: 50%;
top: 50%;
margin-left: -310px;
margin-top: -220px;

padding:5px;
background : rgba( 255, 255, 255, 0.8);
-moz-border-radius: 7px; /*모질라*/
-webkit-border-radius: 7px; /*웹킷*/
border-radius: 7px; /*IE9+,FF4+,chrome,Safari5,+opera*/

-moz-box-shadow: 0 0 2px 2px #A5A29C;
-webkit-box-shadow: 0 0 2px 2px #A5A29C;
box-shadow: 0 0 2px 2px #A5A29C;
}
.modal-dialog h3 { text-align:left; padding-left:5px; margin:2px 0px ; border:0; color:#333; font-size:12px; font-weight:bold;}
.modal-dialog .close { position:absolute; top:0px; right:12px; width:40px; height:18px; margin:0 ; border:1px solid #A5A29C; padding:2px 0 0 0; border-top-width:0px;}
.modal-dialog .close:hover { background-color:#dddddd;}
.modal-dialog iframe { width:600px; height:400px; border:#7c786f solid 1px; overflow:hidden; background-color:#ffffff}

.ui-tooltip {
padding: 4px;
position: absolute;
z-index: 9999;
max-width: 300px;
-webkit-box-shadow: 0 0 5px #aaa;
box-shadow: 0 0 5px #aaa;
}
body .ui-tooltip {
border-width: 1px;
}
.ui-widget {
font-family: Verdana,Arial,sans-serif;
font-size: 1.1em;
}
.ui-widget .ui-widget {
font-size: 1em;
}
.ui-widget-content {
font-size:9pt;
border: 1px solid #000000;
background: #ffffff;
color: #222222;
}
.divider {
height:1px;background-color:#bbb;padding:0px;
}
.question {
text-align:left;
color : #0000ff;
}
.code-example {
border : 1px solid #27684C;
margin : 5px 5px 5px 20px;
padding : 5px;
color : #008000;
}
<%
} else if ("logout".equals(mode)) {
    ToolUtil.login(ToolUtil.CLEAR_LOGIN, session);
%>
<script type="text/javascript">parent.top.location.href = "<%= currentRequestURI %>?m=login";</script>
<%
} else if ("check".equals(mode)) {
    String password = param(request, "pwd", "");
    if (ToolUtil.match(password, 8)) {
        ToolUtil.login(ToolUtil.SET_LOGIN, session);
%>
<script type="text/javascript">parent.top.location.href = "<%= currentRequestURI %>";</script>
<%
} else {
%>
<script type="text/javascript">parent.top.location.href = "<%= currentRequestURI %>?m=login";</script>
<%
    }
} else if ("c".equals(mode)) {
    String exec = param(request, "e", "e");
    String algorithm = param(request, "a", "aria");
    String type = param(request, "t", "n");
    String key = param(request, "k", "");
    String iv = param(request, "i", "");
    String value = param(request, "v", "");
    boolean defaultIv = ("y".equals(param(request, "d", "n"))) ? true : false;
    boolean includeIv = ("y".equals(param(request, "c", "n"))) ? true : false;
    out.print(ToolUtil.cipher(type, key, iv, value, exec, algorithm, defaultIv, includeIv));
} else {
    if (!"login".equals(mode) && !ToolUtil.login(ToolUtil.IS_LOGIN, session)) {
%>
<script type="text/javascript">
    parent.top.location.href = "<%= currentRequestURI %>?m=login";
</script>
<%
        return;
    }

%><!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>nProtect Online Security v1.0.0</title>
    <link type="text/css" href="<%= currentRequestURI %>?m=css" rel="stylesheet"/>
    <script type="text/javascript" src="/static/pluginfree/js/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="/pluginfree/js/jquery-ui-1.10.3.js"></script>

    <!-- <script type="text/javascript" src="/pluginfree/jsp/nppfs.script.jsp?i=%2Fpluginfree%2Fjs%2Fnppfs-1.0.0.js"></script> -->
    <script type="text/javascript" src="/pluginfree/jsp/nppfs.script.jsp"></script>
    <script type="text/javascript" src="/static/pluginfree/js/nppfs-1.0.0.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $(document).tooltip({
                track: true
            });
        });

        Date.prototype.format = function (f) {
            if (!this.valueOf()) return " ";

            var weekName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
            var d = this;

            return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|ms|a\/p)/gi, function ($1) {
                switch ($1) {
                    case "yyyy":
                        return d.getFullYear();
                    case "yy":
                        return (d.getFullYear() % 1000).zf(2);
                    case "MM":
                        return (d.getMonth() + 1).zf(2);
                    case "dd":
                        return d.getDate().zf(2);
                    case "E":
                        return weekName[d.getDay()];
                    case "HH":
                        return d.getHours().zf(2);
                    case "hh":
                        return ((h = d.getHours() % 12) ? h : 12).zf(2);
                    case "mm":
                        return d.getMinutes().zf(2);
                    case "ss":
                        return d.getSeconds().zf(2);
                    case "ms":
                        return d.getMilliseconds().zf(3);
                    case "a/p":
                        return d.getHours() < 12 ? "AM" : "PM";
                    default:
                        return $1;
                }
            });
        };
        String.prototype.string = function (len) {
            var s = '', i = 0;
            while (i++ < len) {
                s += this;
            }
            return s;
        };
        String.prototype.zf = function (len) {
            return "0".string(len - this.length) + this;
        };
        Number.prototype.zf = function (len) {
            return this.toString().zf(len);
        };


        var getAjaxRequest = function () {
            var http = null;
            try {
                if (window.XMLHttpRequest) {
                    http = new XMLHttpRequest();
                } else if (window.ActiveXObject) {
                    http = new ActiveXObject("MSXML2.XMLHTTP");
                    if (!http) {
                        http = new ActiveXObject("Microsoft.XMLHTTP");
                    }
                }
            } catch (e) {
            }
            return http;
        };
        var ajaxResultMessage;
        var send = function (url, query, callback) {
            var httpcallback = function (xhr) {
                var result = "";
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        result = xhr.responseText;
                        if (typeof (callback) == "function") {
                            callback(result);
                        }
                    } else {
                        ajaxResultMessage = "Can not connect to remote address(" + url + ").";
                    }
                } else {
                    //alert("["+xhr.readyState+"] Statement error.");
                }
                return result;
            };

            var result = "";
            var http = getAjaxRequest();
            ajaxResultMessage = "";
            if (typeof (http) != "undefined" && http != null) {
                if (typeof (http.onprogress) == 'object') {
                    http.onload = http.onerror = http.onabort = function () {
                        result = httpcallback(http);
                    };
                } else {
                    http.onreadystatechange = function () {  // CallBack 함수 지정
                        result = httpcallback(http);
                    };
                }

                http.open("post", url, true);
                //http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                http.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
                if (typeof (query) != "undefined" && query != null && query != "") {
                    http.send(query);
                } else {
                    http.send();
                }
            } else {
                ajaxResultMessage = "This browser is not support Ajax.";
            }

            //alert(result);
            return result;
        };

        function checkUrl(url) {
            ajax(url, null, function () {
                if (ajaxResultMessage != "") {
                    alert(ajaxResultMessage);
                } else {
                    alert("서버에 파일이 존재합니다.");
                }
            });
        }


        function ajax(url, param, options) {
            var result = "";
            $.ajax({
                url: url,
                async: false,
                type: "POST",
                contentType: "text/html",
                data: param,
                error: function (xhr, textStatus, errorThrown) {
                },
                success: options.callback,
                complete: function () {
                }
            });

            return result;
        }
    </script>
</head>
<body>


<%
    if ("page".equals(mode)) {
	/*
	try{
		int len = "*".getBytes().length;
		byte[] encryptBytes = new byte[len];
		com.nprotect.common.security.Cipher rc4 = com.nprotect.common.security.Cipher.getInstance("RC4");
		rc4.setKey("incagood".getBytes());
		rc4.encrypt("*".getBytes(), 0, encryptBytes, 0, len);

		System.out.println(StringUtil.hexEncode(encryptBytes));
	}catch(Exception e){
		e.printStackTrace();
	}
	*/
%>
<script type="text/javascript">
    jQuery(document).ready(function () {
        function resize() {
            var width = $(window).width();
            var height = $(window).height();
            $(".box-options").css({width: 300, height: height - 80});
            $(".box-content").css({width: width - 320, height: height - 80});
        }

        $(window).resize(function () {
            resize();
        });
        resize();
    });
</script>


<form id="nos-options" id="nos-options" method="post" action="about:blank" onsubmit="return false;">
    <table border="0" style="border-spacing:0px;padding:0px;">
        <tr>
            <td colspan="2" style="text-align:left;font-size:14pt;"><strong>nProtect Online Security</strong>, Plugin
                Free Service - Debugging Tools v1.0
            </td>
        </tr>
        <tr>
            <td style="vertical-align:top;">
                <div class="box box-options" style="background-color:#EEEEEE">
                    <table style="width:100%;border-spacing:0px;padding:0px;">
                        <tr>
                            <th style="text-align:left;">1. NOS 프로그램 정보</th>
                        </tr>
                        <tr>
                            <td>
                                <ol style="margin-top:0px;">
                                    <li><a href="<%= currentRequestURI %>?m=system" target="nos-tool-iframe"
                                           title="접속정보 및 상태정보"">접속정보 및 상태정보</a></li>
                                    <li><a href="<%= currentRequestURI %>?m=about" target="nos-tool-iframe"
                                           title="프로그램 정보">NOS 프로그램 정보</a></li>
                                </ol>
                            </td>
                        </tr>
                        <tr>
                            <th style="text-align:left;">2. 테스트</th>
                        </tr>
                        <tr>
                            <td>
                                <ol style="margin-top:0px;">
                                    <li><a href="<%= currentRequestURI %>?m=test" target="nos-tool-iframe"
                                           title="NOS제품을 구동테스트합니다.">구동 테스트</a></li>
                                    <!-- <li>설치 테스트</li> -->
                                </ol>
                            </td>
                        </tr>
                        <tr>
                            <th style="text-align:left;">3. 도구</th>
                        </tr>
                        <tr>
                            <td>
                                <ol style="margin-top:0px;">
                                    <li>
                                        암호화/복호화
                                        <ul style="padding-left:20px;">
                                            <li><a href="<%= currentRequestURI %>?m=cipher&c=aria256"
                                                   target="nos-tool-iframe" title="데이터 암호화/복호화 도구">ARIA 256</a></li>
                                            <li><a href="<%= currentRequestURI %>?m=cipher&c=aes128"
                                                   target="nos-tool-iframe" title="데이터 암호화/복호화 도구">AES 128</a></li>
                                            <li><a href="<%= currentRequestURI %>?m=cipher&c=aes256"
                                                   target="nos-tool-iframe" title="데이터 암호화/복호화 도구">AES 256</a></li>
                                            <%-- <li><a href="<%= currentRequestURI %>?m=cipher&c=rsa2048" target="nos-tool-iframe" title="데이터 암호화/복호화 도구">RSA 2048</a></li> --%>
                                            <%-- <li><a href="<%= currentRequestURI %>?m=cipher&c=seed128" target="nos-tool-iframe" title="데이터 암호화/복호화 도구">SEED 128</a></li> --%>
                                        </ul>
                                    </li>
                                    <li>
                                        <a href="<%= currentRequestURI %>?m=command&d=req" target="nos-tool-iframe"
                                           title="NOS 명령어를 분석합니다.">명령어 분석</a>
                                        <%--
									<ul style="padding-left:20px;">
										<li><a href="<%= currentRequestURI %>?m=command&d=req" target="nos-tool-iframe" title="NOS에 질의값을 분석합니다.">요청값분석</a></li>
										<li><a href="<%= currentRequestURI %>?m=command&d=res" target="nos-tool-iframe" title="NOS에 응답값을 분석합니다.">응답값분석</a></li>
									</ul>
--%>
                                    </li>
                                    <li>
                                        연결확인
                                        <ul style="padding-left:20px;">
                                            <li><a href="https://pfs.nprotect.com:14430" target="nos-tool-iframe"
                                                   title="https://pfs.nprotect.com:14430으로의 연결상태를 확인합니다.">https://pfs.nprotect.com:14430</a>
                                            </li>
                                            <li><a href="https://127.0.0.1:14440" target="nos-tool-iframe"
                                                   title="https://127.0.0.1:14440으로의 연결상태를 확인합니다.">https://127.0.0.1:14440</a>
                                            </li>
                                        </ul>
                                    </li>
                                    <li><a href="<%= currentRequestURI %>?m=majorjs" target="nos-tool-iframe"
                                           title="주요 자바스크립트 변수/함수/구문">주요 자바스크립트 변수/함수/구문</a></li>
                                    <!-- <li><a href="about:blank" onclick="$('#modal-replace-mapping').show();return false;" title="자바스크립트 변수치환 목록을 조회합니다.">변수치환 목록 정보</a></li> -->
                                    <li><a href="<%= currentRequestURI %>?m=replacement" target="nos-tool-iframe"
                                           title="자바스크립트 변수치환 목록을 조회합니다.">변수치환 목록 정보</a></li>
                                </ol>
                            </td>
                        </tr>
                        <tr>
                            <th style="text-align:left;">4. 기술지원가이드</th>
                        </tr>
                        <tr>
                            <td>
                                <ol style="margin-top:0px;">
                                    <li><a href="<%= currentRequestURI %>?m=tsfaq" target="nos-tool-iframe"
                                           title="자주묻는 질문과 답변(기술지원)">기술지원 FAQ</a></li>
                                    <li><a href="<%= currentRequestURI %>?m=csfaq" target="nos-tool-iframe"
                                           title="자주묻는 질문과 답변(고객상담)">고객상담 FAQ</a></li>
                                    <li><a href="<%= currentRequestURI %>?m=supdateState" target="nos-tool-iframe"
                                           title="https://supdate.nprotect.net 상태확인">supdate 접속상태확인</a></li>
                                </ol>
                            </td>
                        </tr>
                        <tr>
                            <th style="text-align:left;">5. <a href="<%= currentRequestURI %>?m=logout"
                                                               target="nos-tool-iframe" title="">로그아웃</a></th>
                        </tr>
                    </table>
                </div>
            </td>
            <td style="vertical-align:top;">
                <iframe id="nos-tool-iframe" name="nos-tool-iframe" src="<%= currentRequestURI %>?m=system"
                        class="box box-content"></iframe>
            </td>
        </tr>
    </table>
</form>
<%
    }        // end of mode ""

    if ("system".equals(mode)) {
        String sessionYn = "사용불가";
        try {
            String dummy = new Date().toString();
            session.setAttribute("dummy", dummy);
            String value = (String) session.getAttribute("dummy");
            if (dummy.equals(value)) {
                sessionYn = "사용가능";
            }
            session.removeAttribute("dummy");
        } catch (Exception e) {
        }

        StringBuilder builder1 = new StringBuilder();
        Enumeration<String> e = session.getAttributeNames();
        List<String> list1 = Collections.list(e);
        if (list1.size() > 0) {
            Collections.sort(list1);
            for (String name : list1) {
                Object value = session.getAttribute(name);
                if (value instanceof Map) {
                    builder1.append("<span style=\"color:#ff0000;\">" + name + "</span> : <br />");
                    Set set = ((Map) value).keySet();
                    List<String> list2 = new ArrayList<String>(set);
                    Collections.sort(list2);
                    for (String key : list2) {
                        Object value1 = ((Map) value).get(key);
                        builder1.append("<span style=\"color:#0000ff;margin-left:30px;\">" + key + "</span> : " + value1 + "<br />");
                    }
                } else {
                    builder1.append("<span style=\"color:#ff0000;\">" + name + "</span> : " + value + "<br />");
                }
            }
        }
%>
<script type="text/javascript">
    function setCookie(cName, cValue, cDay) {
        if (L.au(cValue)) cValue = "";
        if (L.au(cDay)) cDay = 0;
        var expire = new Date();
        expire.setDate(expire.getDate() + cDay);
        cookies = cName + '=' + escape(cValue) + '; path=/ '; // 한글 깨짐을 막기위해 escape(cValue)를 합니다.
        if (typeof cDay != 'undefined') cookies += ';expires=' + expire.toGMTString() + ';';
        document.cookie = cookies;
    };

    function getCookie(cName) {
        cName = cName + '=';
        var cookieData = document.cookie;
        var start = cookieData.indexOf(cName);
        var cValue = '';
        if (start != -1) {
            start += cName.length;
            var end = cookieData.indexOf(';', start);
            if (end == -1) end = cookieData.length;
            cValue = cookieData.substring(start, end);
        }
        return unescape(cValue);
    };

    function listCookies() {
        var pairs = document.cookie.split(';');
        var html = [];
        for (var i = 0; i < pairs.length; i++) {
            var pair = pairs[i].split("=");
            html.push("<span style=\"color:#ff0000;\">" + pair[0] + "</span> : " + escape(pair[1]) + "<br />");
        }
        return html.join("\n");
    };

    $(document).ready(function () {
        $(".userAgent").text(navigator.userAgent);

        var doc = [];
        if (npPfsDefine.ie) {
            doc.push("" + "\nIE " + document.documentMode + "문서로 인식되며 ");
        }
        doc.push("" + (document.compatMode === 'CSS1Compat' ? '표준(Standards)' : '호환(Quirks)') + " 문서모드로 처리하고 있습니다.");
        $(".procDocument").text(doc.join(""));


        var osInfo = "기타";
        if (D.win) osInfo = "Windows " + (npPfsDefine.osVersion || D.cR)
        if (D.mac) osInfo = "Macintosh " + (npPfsDefine.osVersion || D.cR)
        if (D.lnx) osInfo = "Linux";
        osInfo += D.isMobileDevice() ? " (모바일)" : "";
        $(".osInfo").text(osInfo);

        var browserInfo = "기타";
        if (D.ie) browserInfo = "Internet Explorer " + (npPfsDefine.browserVersion || D.bd) + "(" + ((D.ie64) ? "64bit" : "32bit") + ")";
        if (D.ff) browserInfo = "FireFox " + (npPfsDefine.browserVersion || D.bd);
        if (D.cr) browserInfo = "Google Chrome " + (npPfsDefine.browserVersion || D.bd);
        if (D.ns) browserInfo = "Netscape " + (npPfsDefine.browserVersion || D.bd);
        if (D.sf) browserInfo = "Safari " + (npPfsDefine.browserVersion || D.bd);
        if (D.op) browserInfo = "Opera " + (npPfsDefine.browserVersion || D.bd);
        if (D.b360) browserInfo = "360 Browser " + (npPfsDefine.browserVersion || D.bd);
        $(".browserInfo").text(browserInfo);


        var date = new Date();
        setCookie("dummy", date, 1);
        var cdate = getCookie("dummy");
        if (date == cdate) {
            $(".isAvailableCookie").html("사용가능");
        } else {
            $(".isAvailableCookie").html("사용불가");
        }

        setCookie("dummy", date, -1);
        $(".viewCookie").html(listCookies);
    });
</script>
<table style="width:100%" ;border-spacing:0px;padding:0px;
">
<tr>
    <th style="text-align:left;font-size:14pt;" colspan="2">접속정보 및 상태</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>UserAgent(js)</th>
    <td><span class="userAgent"></span></td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>UserAgent(jsp)</th>
    <td><span><%= request.getHeader("User-Agent") %></span></td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>운영체제</th>
    <td><span class="osInfo"></span></td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>브라우저</th>
    <td><span class="browserInfo"></span></td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>문서처리</th>
    <td><span class="procDocument"></span></td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>쿠키사용</th>
    <td><span class="isAvailableCookie"></span></td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>저장된 쿠키</th>
    <td>
        <span class="viewCookie"></span>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>세션사용</th>
    <td><%= sessionYn %>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>저장된 세션</th>
    <td>
        <%= builder1.toString() %>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
</table>
<%
    }        // end of mode "system"

    if ("about".equals(mode)) {
%>
<script type="text/javascript">
    jQuery(document).ready(function () {
        $(".jsVersion").text(ad.k5);
        $(".jsBuildDate").text(ad.Qd);
        $(".jsVersionShortCut").text("ad.k5");
        $(".jsBuildDateShortCut").text("ad.Qd");
        var customerId = "";
        if (ad.k5 === "1.2.0") {
            customerId = Ye.x5;
            $(".customerId").text(customerId);
            $(".customerIdShortCut").text("Ye.x5");
        } else {
            customerId = Ye.Qb;
            $(".customerId").text(customerId);
            $(".customerIdShortCut").text("Ye.Qb");
        }

        var param = [];
        param.push("m=c");
        param.push("e=d");
        param.push("t=nos");
        param.push("c=y");
        param.push("d=n");
        param.push("v=" + encodeURIComponent(customerId));
        send("<%= currentRequestURI %>", param.join("&"), function (responseText) {
            $(".plainCustomerId").text(responseText);
        });

    });
</script>
<table style="width:100%" ;border-spacing:0px;padding:0px;
">
<tr>
    <th style="text-align:left;font-size:14pt;" colspan="3">자바스크립트 모듈 정보</th>
</tr>
<tr>
    <td colspan="3" class="divider"></td>
</tr>
<tr>
    <th>고객 코드</th>
    <td><span class="customerId"></span></td>
    <td><span class="customerIdShortCut"></span></td>
</tr>
<tr>
    <td colspan="3" class="divider"></td>
</tr>
<tr>
    <th>고객 코드(평문)</th>
    <td><span class="plainCustomerId"></span></td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td colspan="3" class="divider"></td>
</tr>
<tr>
    <th>스크립트 버전</th>
    <td><span class="jsVersion"></span></td>
    <td><span class="jsVersionShortCut"></span></td>
</tr>
<tr>
    <td colspan="3" class="divider"></td>
</tr>
<tr>
    <th>스크립트 생성일</th>
    <td><span class="jsBuildDate"></span></td>
    <td><span class="jsBuildDateShortCut"></span></td>
</tr>
<tr>
    <td colspan="3" class="divider"></td>
</tr>
</table>
<%
    }        // end of mode "about"

    if ("test".equals(mode)) {
%>
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery("#userAgent").text(navigator.userAgent);

        //$("#cardNo1").focus();
        $("#cardNo1").focus();


        if (npPfsDefine.ie) {
            //alert("이 브라우저(" + (npPfsDefine.browserVersion || npPfsDefine.bd) + ")는 현재 페이지는 " + "\nIE " + document.documentMode + "문서로 인식되며 "  + (document.compatMode === 'CSS1Compat' ? '표준(Standards)' : '호환(Quirks)') + " 문서모드로 처리하고 있습니다.");
        } else {
            //alert("이 브라우저(" + (npPfsDefine.browserVersion || npPfsDefine.bd) + ")는 현재 페이지를 " + (document.compatMode === 'CSS1Compat' ? '표준(Standards)' : '호환(Quirks)') + " 문서모드로 처리하고 있습니다.");
        }

        var isSupport = npPfsCtrl.IsSupport();
        if (!isSupport) {
            alert("보안프로그램을 지원하지 않는 환경입니다. 접속 가능 환경을 확인하시고 다시 시도하십시오.");
        }

        // 가상운영체제 여부 확인
        npPfsCtrl.isVirtualMachine(function (result) {
            if (result == true) {
                alert("현재 가상운영체제 또는 원격프로그램으로 접속하셨습니다. 보안프로그램의 일부기능(키보드보안)이 비활성화됩니다.");
            }
        });

        npPfsStartup(document.form1, true, true, true, true, "npkencrypt", "on");
    });

    function doInputEnable(ele, isDisabled) {
        try {
            npVCtrl.hideAll();
            //npVCtrl.resetKeypad(ele);
        } catch (e) {

        }
        var element = document.getElementById(ele);
        if (element != null) {
            element.disabled = !isDisabled;
            //alert('script ' + element.name + ' => ' + element.disabled);
            if (element.disabled == true) {
                //element.style.backgroundColor="#aaaaaa";
            } else {
                //element.style.backgroundColor="#ffaaaa";
            }
        }
    }

    function doInputReadOnly(ele, isReadOnly) {
        try {
            npVCtrl.hideAll();
            //npVCtrl.resetKeypad(ele);
        } catch (e) {

        }
        var element = document.getElementById(ele);
        if (element != null) {
            $(element).prop("readonly", isReadOnly);
            if (element.disabled == true) {
                //element.style.backgroundColor="#aaaaaa";
            } else {
                //element.style.backgroundColor="#ffaaaa";
            }
        }
    }

    function copyData() {
        var asis = document.form1;
        var tobe = document.form2;
        npPfsCtrl.copy(asis, tobe);
    }


    function GetReplaceKeyData(form, field) {
        var table = npPfsCtrl.GetReplaceField(form, field);
        alert(table);
        console.log(table);
    }

    function GetReplaceTable(form, field) {
        var data = npPfsCtrl.GetResultField(form, field);
        alert(data);
        console.log(data);
    }

    var dynamicIndex = 1;

    function AddGeneralField() {
        var form = document.form1;
        var name = "dynamicField" + dynamicIndex;

//	var field = [];
//	field.push("<input type=\"text\" name=\"" + name + "\" />");
//	form.appendChild(field.join(""));

        var input = document.createElement("input");
        input.setAttribute("type", "text");
        input.setAttribute("name", name);
        form.appendChild(input);
        form.appendChild(document.createElement("br"));

        npPfsCtrl.RegistDynamicField(form, name);

        dynamicIndex++;
    }

    function AddE2EField() {
        var form = document.form1;
        var name = "dynamicField" + dynamicIndex;

//	var field = [];
//	field.push("<input type=\"text\" name=\"" + name + "\" />");
//	form.appendChild(field.join(""));

        var input = document.createElement("input");
        input.setAttribute("type", "text");
        input.setAttribute("name", name);
        input.setAttribute("npkencrypt", "on");
        form.appendChild(input);
        form.appendChild(document.createElement("br"));

        npPfsCtrl.RegistDynamicField(form, name);

        dynamicIndex++;
    }

    function AddReplaceField() {
        var form = document.form1;
        var name = "dynamicField" + dynamicIndex;
        /*
	var field = [];
	field.push("<input type=\"text\" name=\"" + name + "\" />");
	field.push("<button onclick=\"GetReplaceTable(\"" + form.name + "\", \"" + name + "\");\" >치환테이블얻기</button>");
	field.push("<button onclick=\"GetReplaceKeyData(\"" + form.name + "\", \"" + name + "\")\">치환데이터얻기</button>");
	form.appendChild(field.join(""));
	*/

        var input = document.createElement("input");
        input.setAttribute("type", "text");
        input.setAttribute("name", name);
        input.setAttribute("npkencrypt", "re");
        form.appendChild(input);

        var button1 = document.createElement("input");
        button1.setAttribute("type", "button");
        button1.setAttribute("value", "치환테이블얻기");
        button1.setAttribute("onclick", "GetReplaceTable(\"" + form.name + "\", \"" + name + "\");");
        form.appendChild(button1);

        var button2 = document.createElement("input");
        button2.setAttribute("type", "button");
        button2.setAttribute("value", "치환데이터얻기");
        button2.setAttribute("onclick", "GetReplaceKeyData(\"" + form.name + "\", \"" + name + "\");");
        form.appendChild(button2);

        form.appendChild(document.createElement("br"));

        npPfsCtrl.RegistDynamicField(form, name);
        dynamicIndex++;
    }

    function doDecrypt1() {
        npPfsCtrl.waitSubmit(function () {
            document.form1.submit();
        });
    }

    function checkVM() {
        npPfsCtrl.isVirtualMachine(function (result) {
            if (result == true) {
                alert("가상운영체제로 판단됩니다.");
            } else {
                alert("가상운영체제가 아닌 것으로 판단됩니다.");
            }
        });
    }

</script>

<form name="form1" action="<%= currentRequestURI %>" method="post" target="resultTarget">
    <input type="hidden" name="m" value="decrypt"/>
    <table style="width:100%">
        <tr>
            <th style="text-align:left;font-size:14pt;" colspan="2">구동 테스트</th>
        </tr>
        <tr>
            <td colspan="2" class="divider"></td>
        </tr>
        <tr>
            <th>접속정보</th>
            <td><span id="userAgent"></span></td>
        </tr>
        <tr>
            <td>필드 활성/비활성(Disabled)</td>
            <td>
                <input type="radio" name="radioDisabled" onclick="doInputEnable('cardNo1', true);" checked="checked">
                cardNo1 활성화
                <input type="radio" name="radioDisabled" onclick="doInputEnable('cardNo1', false);"> cardNo1 비활성화
            </td>
        </tr>
        <tr>
            <td>필드 읽기전용(Readonly)</td>
            <td>
                <input type="radio" name="radioReadonly" onclick="doInputReadOnly('cardNo2', false);" checked="checked">
                cardNo2 읽기전용 아님
                <input type="radio" name="radioReadonly" onclick="doInputReadOnly('cardNo2', true);"> cardNo2 읽기전용
            </td>
        </tr>
        <tr>
            <td colspan="2" style="text-align:center;">
                <button type="button" onclick="checkVM();">가상운영체제인지 확인</button>
                <button type="button" onclick="copyData();">form1값을 form2에 복사</button>
                <input type="button" name="startNos" id="startNos" value="방화벽 시작"
                       onclick="npNCtrl.start();return false;"/>
                <!-- <input type="button" name="stopNos" id="stopNos" value="방화벽 종료" onclick="npNCtrl.stop();return false;" /> -->
            </td>
        </tr>
        <tr>
            <td> 일반필드(미보호)</td>
            <td><input type="text" name="NONE_TEXT_1" id="t4" value="" npkencrypt="off"></td>
        </tr>
        <tr>
            <td> FormOut ID</td>
            <td><input type="text" name="NONE_TEXT_4" id="t4" value=""></td>
        </tr>
        <tr>
            <td> FormOut PW</td>
            <td><input type="password" name="NONE_PASS_4" id="p4" value=""></td>
        </tr>
        <tr>
            <td>E2E Id(문자형):</td>
            <td><input type="text" name="KPD_TEXT_1" id="kt1" style="ime-mode:disabled;" npkencrypt="on"
                       data-keypad-type="alpha" value="" maxlength="20" data-keypad-theme="kcp-w320"/> : 20글자
            </td>
        </tr>
        <tr>
            <td>E2E PW(문자형):</td>
            <td><input type="password" name="KPD_PASS_1" id="kp1" style="ime-mode:disabled;" npkencrypt="on"
                       data-keypad-type="alpha" value="" maxlength="20" data-keypad-theme="kcp-w480"/> : 20글자
            </td>
        </tr>
        <tr>
            <td>E2E Id(Inca):</td>
            <td><input type="text" name="E2E_TEXT_1" id="t1" style="ime-mode:disabled;" npkencrypt="on"
                       data-keypad-type="korean" value="" maxlength="10" data-keypad-theme="mobile-mini"/> : 10글자
            </td>
        </tr>
        <tr>
            <td>E2E PW(Inca):</td>
            <!-- <td><input type="password" name="E2E_PASS_1" id="p1" style="ime-mode:disabled;" npkencrypt="on" data-keypad-type="alpha" value="" maxlength="16" /> : 16글자</td> -->
            <td><input type="password" name="E2E_PASS_1" id="p1" style="ime-mode:disabled;" npkencrypt="on"
                       data-keypad-enter="npVCtrl.hideKeypad('E2E_PASS_1');" data-keypad-type="alpha" value=""
                       maxlength="16"
                       onchange="if(this.value.length==4){/*alert('alert message debugging for Firefox in Macintosh');*/}"/>
                : 16글자
            </td>
        </tr>
        <tr>
            <td>E2E Card(Inca):</td>
            <td>
                <input type="password" name="cardNo1" id="cardNo1" style="ime-mode:disabled;" npkencrypt="on" value=""
                       maxlength="4"
                       onchange="console.log('onchange 1');if(this.value.length==4){this.form.cardNo2.focus();}"
                       size="4" data-keypad-next="cardNo2" data-keypad-preview="on" style="width:20px;"/>
                <input type="password" name="cardNo2" id="cardNo2" style="ime-mode:disabled;" npkencrypt="on" value=""
                       maxlength="4"
                       onkeyup="console.log('onchange 2');if(this.value.length==4){this.form.cardNo3.focus();}" size="4"
                       data-keypad-next="cardNo3" data-keypad-preview="off" style="width:20px;"/>
                <input type="password" name="cardNo3" id="cardNo3" style="ime-mode:disabled;" npkencrypt="on" value=""
                       maxlength="4"
                       onchange="console.log('onchange 3');if(this.value.length==4){this.form.cardNo4.focus();}"
                       size="4" data-keypad-next="cardNo4" data-keypad-preview="on" style="width:20px;"/>
                <input type="password" name="cardNo4" id="cardNo4" style="ime-mode:disabled;" npkencrypt="on" value=""
                       maxlength="4" size="4" data-keypad-type="num" style="width:20px;" data-keypad-preview="off"
                       data-keypad-next="__hide__"/>
            </td>
        </tr>
        <tr>
            <td>E2E Id(Raon):</td>
            <td><input type="text" name="E2E_RAON_TEXT_1" id="rt2" style="ime-mode:disabled;" enc="on" value=""
                       maxlength="4"/> : 4글자
            </td>
        </tr>
        <tr>
            <td>E2E PW(Raon):</td>
            <td><input type="password" name="E2E_RAON_PASS_1" id="rp2" style="ime-mode:disabled;" enc="on" value=""
                       maxlength="6"/> : 6글자
            </td>
        </tr>
        <tr>
            <td>RE ID(기본):</td>
            <td>
                <input type="text" name="replace_text1" id="f2t1" npkencrypt="RE" value="" data-keypad-type-x="default"
                       data-keypad-type-y="default"/>
                <input type="button" name="Button1" id="f2bt1" value="치환테이블얻기"
                       onclick='GetReplaceTable("form1", "replace_text1");'/>
                <input type="button" name="f2Button1" id="f2bt1" value="치환데이터얻기"
                       onclick='GetReplaceKeyData("form1", "replace_text1")'/>
            </td>
        </tr>
        <tr>
            <td>RE PW(왼쪽/위):</td>
            <td>
                <input type="password" name="replace_pass2" id="f2p2" npkencrypt="Re" value="" data-keypad-type-x="left"
                       data-keypad-type-y="top" data-keypad-x="30" data-keypad-y="30"/>
                <input type="button" name="Button2" id="f2bt2" value="치환테이블얻기"
                       onclick='GetReplaceTable("form1", "replace_pass2");'/>
                <input type="button" name="f2Button2" id="f2bt2" value="치환데이터얻기"
                       onclick='GetReplaceKeyData("form1", "replace_pass2")'/>
            </td>
        </tr>
        <tr>
            <td>RE PW(오른쪽/아래):</td>
            <td>
                <input type="password" name="replace_pass3" id="f2p3" npkencrypt="re" maxlength="8" value=""
                       data-keypad-type-x="right" data-keypad-type-y="bottom" data-keypad-x="30" data-keypad-y="30"/>
                <input type="button" name="Button3" id="f2bt3" value="치환테이블얻기"
                       onclick='GetReplaceTable("form1", "replace_pass3");'/>
                <input type="button" name="f2Button3" id="f2bt3" value="치환데이터얻기"
                       onclick='GetReplaceKeyData("form1", "replace_pass3")'/>
            </td>
        </tr>
        <tr>
            <td>RE PW(가운데/자동):</td>
            <td>
                <input type="password" name="replace_pass4" id="f2p4" npkencrypt="re" maxlength="8" value=""
                       data-keypad-type-x="center" data-keypad-type-y="auto"/>
                <input type="button" name="Button4" id="f2bt4" value="치환테이블얻기"
                       onclick='GetReplaceTable("form1", "replace_pass4");'/>
                <input type="button" name="f2Button4" id="f2bt3" value="치환데이터얻기"
                       onclick='GetReplaceKeyData("form1", "replace_pass4")'/>
            </td>
        </tr>
        <tr>
            <td>동적 필드</td>
            <td>
                <button type="button" onclick='AddGeneralField();'>필드추가(일반)</button>
                <button type="button" onclick='AddE2EField();'>필드추가(E2E)</button>
                <button type="button" onclick='AddReplaceField();'>필드추가(치환)</button>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="button" name="doDec1" value="복호화" onclick="doDecrypt1();">
            </td>
        </tr>
    </table>
    <div class="nppfs-elements" style="display:none;"></div>
    <div class="nppfs-keypad-div" style="display:none;"></div>
</form>

<form name="form2" action="decrypt.jsp" method="post" target="resultTarget">
</form>

<table style="width:100%">
    <tr>
        <th style="text-align:left;font-size:14pt;"> 복호화 테스트</th>
    </tr>
    <tr>
        <td>
            <iframe id="resultTarget" name="resultTarget" src="about:blank"
                    style="border:0px solid #000;width:100%;height:300px;"></iframe>
        </td>
    </tr>
</table>

<div id="nppfs-loading-modal"
     style="display:none;"><%= com.nprotect.pluginfree.PluginFree.postback(request, currentRequestURI.toString()) %>
</div>
<%
    }        // end of mode "test"

    if ("decrypt".equals(mode)) {
        String npk = request.getParameter("__E2E_RESULT__");
        if (npk != null && !"".equals(npk)) {

            try {
                PluginFree.verify(request, new String[]{"cardNo1", "cardNo2", "cardNo3", "cardNo4"});
            } catch (Exception e) {
                e.printStackTrace();
            }


            ServletRequest pluginfreeRequest = new PluginFreeRequest(request);
            String cardNo1 = pluginfreeRequest.getParameter("cardNo1");
            String cardNo2 = pluginfreeRequest.getParameter("cardNo2");
            String cardNo3 = pluginfreeRequest.getParameter("cardNo3");
            String cardNo4 = pluginfreeRequest.getParameter("cardNo4");
            out.println("<strong>수동복호화(cardNo1) : " + cardNo1 + "</strong><br />");
            out.println("<strong>수동복호화(cardNo2) : " + cardNo2 + "</strong><br />");
            out.println("<strong>수동복호화(cardNo3) : " + cardNo3 + "</strong><br />");
            out.println("<strong>수동복호화(cardNo4) : " + cardNo4 + "</strong><br />");
            out.println("<br />");


            List<String> viewParams = new ArrayList<>();

            //	Map<String, String[]> parameters = request.getParameterMap();
            //	for(String parameter : parameters.keySet()) {
            Enumeration<String> enumeration = request.getParameterNames();
            List<String> list = Collections.list(enumeration);
            Collections.sort(list);
            for (String parameter : list) {
                if (parameter.endsWith("__E2E__")) {
                    String plainkey = parameter.substring(0, parameter.length() - "__E2E__".length());
                    viewParams.add(plainkey);
                    viewParams.add(parameter);

                    String inputValue = request.getParameter(plainkey);
                    //String decryptValue = e2e.decrypt(plainkey);
                    //String decryptValue = e2e.decryptValue(request.getParameter(parameter));

                    out.println("<strong>");
                    out.println("입력양식명 : " + plainkey + "<br />");
                    //out.println("전달받은값 : " + inputValue +"<br />");
                    out.println("E2E복호화값 : " + inputValue + "<br />");
                    //out.println("동일여부 : " + decryptValue.equals(inputValue) +"<br />");
                    out.println("</strong>");
                    out.println("<br />");
                }
            }


            //	for(String parameter : parameters.keySet()) {
            for (String parameter : list) {
                if (parameter.endsWith("__E2E__")) {
                    continue;
                }
                if (viewParams.indexOf(parameter) >= 0) {
                    continue;
                }

                String value2 = request.getParameter(parameter);
                out.println("입력양식명 : " + parameter + "<br />");
                out.println("전달받은값 : " + value2 + "<br />");
                out.println("<br />");
            }
        } else {
            Enumeration<String> enumeration = request.getParameterNames();
            List<String> list = Collections.list(enumeration);
            Collections.sort(list);
            for (String parameter : list) {
                String value2 = request.getParameter(parameter);
                out.println("입력양식명 : " + parameter + "<br />");
                out.println("전달받은값 : " + value2 + "<br />");
                out.println("<br />");
            }
        }


        try {
            String npf = request.getParameter("i_e2e_key");
            if (npf != null && !"".equals(npf)) {
                out.println("===================================================<br />");

                try {
                    PluginFree.verifyFds(request);
                } catch (PluginFreeException e) {
                    out.println("FDS값 복호화를 위한 올바른 암호화 키값이 아닙니다." + e.toString());
                    return;
                }

                PluginFree pf = new PluginFree(request);

                PluginFreeDTO dto = pf.get();
                if (dto != null) {
                    out.println(dto.toString().replace("\n", "<br />\n") + "<br />");
                } else {
                    out.println("값을 파싱할 수 없습니다.<br />");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }        // end of mode "decrypt"


    if ("replacement".equals(mode)) {
%>
<table style="width:100%">
    <tr>
        <th style="text-align:left;font-size:14pt;">변수치환 목록</th>
    </tr>
    <tr>
        <td>
			<textarea rows="50" wrap="off" id="replace-mapping" name="replace-mapping" class="block-replace-mapping"
                      style="width:100%;height:500px;">
//var 이렇게 //과 var가 붙어있어야 주석이 됩니다.
//var AESencrypt = "J"; <<=== 주석 예
//var ? DEFINE 접두어 (그리고 모두 대문자) : DF_
//var ? User defined 함수 접두어 : FN_
//var ? 변수 접두어 : VR_

//navigator.userAgent = 함수로 별도 생성

//var AES = cX; // 변경가능 -> aes 로 변경 요함
var AESdecrypt = er;
var AESencrypt = J;
var Arcfour = a0;
var Async = eX;
var B0 = T;
var B1 = C;
var B2 = G;
var B3 = V;
var BigInteger = b0;
var BlockDevTools = Wz;
var Bw = al;
var C01 = im;
var C06 = iu;
var C07 = iG;
var C08 = jj;
var C09 = iB;
var C10 = iK;
var C11 = io;
var CERT = fn;
var CHECK_VERSION = c7;
var CKKeyPro = et;
var CMD_CHECK_VERSION = am;
var CMD_CHECK_VM = j0;
var CMD_FD_finalizeFunc = h7;
var CMD_FD_getFunc = g7;
var CMD_FD_getVersion = n6;
var CMD_FD_initFunc = q7;
var CMD_FD_initParam = i3;
var CMD_FD_paramFunc = f4;
var CMD_FD_putFunc = e5;
var CMD_FW_LNX_cert = u8;
var CMD_FW_LNX_checkFiles = s1;
var CMD_FW_LNX_keepAlive = f5;
var CMD_FW_LNX_start = v5;
var CMD_FW_LNX_stop = f2;
var CMD_FW_MAC_cert = g9;
var CMD_FW_MAC_checkFiles = b9;
var CMD_FW_MAC_keepAlive = k8;
var CMD_FW_MAC_start = s5;
var CMD_FW_MAC_stop = l0;
var CMD_FW_WIN_Stop = c9;
var CMD_FW_WIN_keepAlive = r3;
var CMD_FW_WIN_start = s4;
var CMD_FW_WIN_status = s3;
var CMD_FW_WIN_version = g8;
var CMD_HANDSHAKE = h6;
var CMD_HEALTH = e2;
var CMD_KEEP_ALIVE = d8;
var CMD_SK_npkEvent = s7;
var CMD_SK_npkFeild = b8;
var CMD_SK_npkParam = f3;
var CODE_V1 = iS;
var CODE_V2 = eG;
var CUSTOMER_ID = x5;
var CheckVM = at;
var CheckVersion = v3;
var CodeVersion = fZ;
var Command = Vh;
var Common = dV;
var ContextPath = iI;
var CountKeys = jC;
var CustomerId = Qb;
var DETECT_DEBUG = Ks;
var DH_AHNLAB = eV;
var DH_AOS = hi;
var DH_INCA = fg;
var DH_KINGS = fX;
var DM = hs;
var Daemon = Pm;
var Define = ac;
var DivNode = bu;
var Down1 = l9;
var Down2 = gq;
var E01 = kn;
var E02 = ku;
var E03 = lh;
var E2E_RESULT = Ix;
var E2E_UNIQUE = wG;
var ENABLE_CHECKBOX = i9;
var ENABLE_FOCUS = q2;
var ENABLE_RADIO = s2;
var ENABLE_TOGGLE = x3;
var ETOE = dO;
var EXEC_ACCOUNT = u7;
var EXEC_CONFIRM = c4;
var EXEC_NUMBER = w3;
var EXEC_PASSWORD = x8;
var EXEC_REPLACE = b5;
var EXEC_SAFECARD = p4;
var EncCERT = eJ;
var EncData = gR;
var EncData2 = gZ;
var EncryptMode = dU;
var EncryptionMode = dI;
var F1 = h;
var FALSE = kN;
//var FD = hj;
//var FW = jn;
//var Fds = az;
var Field = av;
var FieldBgColor = gx;
//var Firewall = aH;
var GetClientPCAppoint = je;
var GetClientPCSignUp = jb;
var GetEncData = iX;
var GetHiddenField = fp;
var GetKey = he;
var GetKeys = bj;
var GetOCXVersion = hJ;
var H01 = jw;
var H02 = jV;
var H03 = iP;
var H06 = hN;
var H07 = js;
var H08 = jh;
var H09 = ig;
var H10 = jd;
var H11 = kk;
var H12 = kg;
var H13 = kW;
var H14 = kP;
var Handshake = hX;
var Health = c5;
var HiddenName = hq;
var Host = gE;
//var IE = bq;
var InstallUrl = Fz;
var IsOldIe = CB;
var IsSupported = cL;
var IsSupportedBw = fN;
var IsSupportedOs = cJ;
var JS_BUILD_DATE = Qd;
var JS_VERSION = k5;
var Job = bO;
var KC = gF;
var KEY_DYNAMIC = gG;
var KEY_ETOE = ez;
var KEY_STATIC = hF;
var KeepAlive = We;
var KeyPadUrl = zo;
var KeyUrl = Gf;
var KeyValue = aL;
var Kill = gL;
var LINUX = bx;
var License = ki;
var LocalPort = l5;
var LocalServer = bW;
var MG = kS;
var MODE_ASYNC = x6;
var MODE_DEBUG = jt;
var MODE_RELEASE = fJ;
var MODE_SYNC = j3;
var Max = Oc;
var MaxFailCount = Qa;
var MaxWaitCount = Ux;
var Min = qs;
var Montgomery = m0;
var N01 = iV;
var N02 = iv;
var N03 = hV;
var N04 = iJ;
var N05 = iQ;
var N06 = hQ;
var N07 = hM;
var N08 = jr;
var N09 = jL;
var N10 = iU;
var N11 = jz;
var N12 = jE;
var NC = fv;
var ObjectClass = eb;
var OnDeleteFieldData = il;
var Options = aG;
var Os = di;
var P02 = jf;
var PKI_CODE = cT;
var PKI_DREAM = ln;
var PKI_HTML5CS = b1;
var PKI_INCA = kT;
var PKI_INCAPF = gN;
var PKI_INITECH = jy;
var PKI_NONE = a1;
var PKI_SOFO = iL;
var PRM_FD_configParam = c0;
var PRM_FD_getBorun = k7;
var PRM_FD_getETOEKey = mh;
var PRM_FD_getEncrypt = g6;
var PRM_FD_getFunc = se;
var PRM_FD_getTotalLog = g4;
var PRM_FD_getTotalLogHash = w9;
var PRM_FD_initETOE = r6;
var PRM_FD_putBrowserVersion = p3;
var PRM_FD_putFirewallStatus = h1;
var PRM_FD_putSecureKeyStatus = l3;
var PRM_FW_WIN_data = o9;
var PRM_SK_Down1 = d7;
var PRM_SK_Down2 = d9;
var PRM_SK_GetEncData = e9;
var PRM_SK_GetKey = m3;
var PRM_SK_Init = f8;
var PRM_SK_Kill = g5;
var PRM_SK_Set = f1;
var PRM_SK_UnInit = m8;
var PRODUCT_DM = d3;
var PRODUCT_FD = h4;
var PRODUCT_FW = p8;
var PRODUCT_KC = x2;
var Padder = H;
var Parameter = aZ;
var PkiCode = ho;
var Policy = ck;
var Port = fb;
var Product = eS;
var Protocol = dZ;
var REQ_READY = aq;
var REQ_REINSTALL = rx;
//var RESULT = fP; // 변경하면 안됨!!!!!
var RESULT_CHECK_VERSION = aj;
var RESULT_DETECT_DEBUG = p0;
var RESULT_FALSE = h5;
var RESULT_REQ_READY = d4;
var RESULT_REQ_REINSTALL = ag;
var RESULT_TRUE = a4;
var Range = Cc;
var Rcon = ge;
var ReFieldBgColor = Xe;
var ReTextColor = Kq;
var ReadyUrl = eP;
var RemoveKey = zf;
var Return = eI;
var RuntimeMode = dk;
var S01 = jM;
var S02 = jZ;
var S04 = kl;
var S05 = kI;
var S06 = kj;
var S07 = lf;
var S08 = ll;
var S09 = kb;
var S10 = lv;
var S11 = jP;
var S12 = jN;
var S5 = by;
var SHOW_BLOCK = s8;
var SHOW_DIVISION = x4;
var SHOW_LAYER = b4;
var SITE_DIV = lq;
var SITE_FORM = ea;
var SK_AOS = fV;
var SK_INCA = fK;
var SK_KINGS = fT;
var SK_RAON_TEK = gz;
var SK_SOFTCAMP = gA;
var SK_SOFTFORUM = hh;
var STATE = iO;
var STATE_DOING = cG;
var STATE_DONE = bb;
var STATE_READY = hE;
//var SecureKey = af;
var Set = hC;
var SiteType = cu;
var Support = aX;
var Sync = cj;
var T1 = dH;
var T2 = cp;
var T3 = cS;
var T4 = cN;
var T5 = dJ;
var T6 = dh;
var T7 = dj;
var T8 = eL;
var TRUE = el;
var TYPE_KEYBOARD = i8;
var TYPE_KEYPAD = K1;
var TYPE_KOREAN = n3;
var TaskState = ar;
var TextColor = hZ;
var U1 = eB;
var U2 = fi;
var U3 = cE;
var U4 = ct;
var UnInit = iH;
var UserColor = eK;
var VersionUrl = cM;
var WIN = aF;
var WaitTimeout = kK;
var aBlock = fq;
var addEvent = ja;
var addJob = aC;
var addOptions = o2;
var addTask = cP;
var ajaxRequest = Hy;
var aliveTask = fU;
var aliveTimer = dQ;
var appendNode = fY;
var arr = cc;
var arrIndex = fD;
var arrInputList = bB;
var arraylist = Gu;
var availabeVersion = dK;
//var b360 = fm;
var bindCertData = jq;
var bindEventListener = iY;
var bindReplaceData = b7;
var binding = jF;
var blockEvent = aW;
var blockSizeInBits = eU;
var bpb = dA;
var browserVersion = bd;
var bwVer = Jk;
var byteArray = fE;
var cDay = fu;
var cName = cn;
var cValue = cO;
var callGatheringFunction = fQ;
var callHandshake = k6;
var callProcessingFunction = hm;
var callback = ax;
var ccode = aA;
var certParam = fA;
var checkAlive = jk;
var checkCompleteProcessingTask = jc;
var checkFiles = dP;
var checkMaxFailure = mH;
var checkVersion = ak;
var chkPCAppoint = hK;
var chkPCSignUp = iw;
var ciphertext = aK;
var clientStrings = dS;
var cmd = dw;
var compareVersion = db;
var configParam = gw;
var connectionFailCount = c3;
//var console = aV; // 변경하면 안됨(스크립트 내부함수)
var cookieData = eT;
var cookiePort = dz;
var cookies = fz;
//var cr = fj;
var createDivision = wT;
var createKeypad = Iq;
//var crossDomain = hT;
var cs = fI;
var ctx = aN;
var currentHost = c6;
var currentPort = cB;
var dataArray = fC;
var datalength = ed;
var datamap = aI;
var decrypt = gu;
var delPCSignUp = jB;
var deley = jx;
var destory = cv;
var destoryAll = cl;
var destoryJobs = dr;
var dhack = cw;
var diableDevTools = d5;
var divIndex = ey;
var doExecuteKeypad = d2;
var domainCount = bf;
var dynamicFields = Lw;
var e2ekey = hb;
var element = as;
var elename = bS;
var enc01 = eh;
var enc02 = bC;
var enc03 = dc;
var encParam = sz;
var enckey = dn;
//var encrypt = Eq;
var endTime = fS;
var err = fR;
var errorThrown = lc;
var eventName = fG;
var eventObject = aE;
var executeKeypad = e3;
//var executemode = gh;
var expandedKey = eR;
var expire = fF;
//var fdr = gp; // fdr <- 원래 fedora 였음
//var ff = dm;
var fieldMap = ek;
var finalize = bm;
var finalizeFunc = gH;
var findDivision = xw;
var findElement = bZ;
var findPort = eC;
var findScript = dl;
var firewallStatus = ji;
var focusElement = v4;
var formatPlaintext = wm;
var formname = an;
var foundPort = cz;
var gathering = hU;
var getBorun = fe;
var getBwVersion = iT;
var getBwVersionCode = jo;
var getCookie = jv;
var getDhack = ij;
var getETOEKey = ef;
var getElapsedTime = eD;
var getEncrypt = eE;
var getFunc = eY;
var getJob = aQ;
var getMulti = eW;
var getOne = bw;
var getOsVersion = gC;
var getPCAppoint = jA;
var getPCSignUp = hS;
var getRandomBytes = mL;
var getSecuKey = gO;
var getState = it;
//var getStatus = iZ; // 변경하면 안됨
var getTask = cA;
var getTotalLog = jp;
var getTotalLogHash = iq;
var getValue = jD;
var getVersion = iA;
var globalKeyValidation = cI;
var handshakeIe = c2;
var haveExtentionPoint = p9;
var hex = dE;
var hexDecode = ha;
var hexEncode = hH;
var hiddenFields = fo;
//var hideLoading = gD;
var idefense = ht;
var idx = bF;
//var ie64 = gm;
//var init = cV; // 변경하면 안됨
var initCertification = gs;
var initCompleteJobs = ei;
var initDefault = hA;
var initETOE = fW;
var initEncrypt = gI;
var initFields = hB;
var initFunc = gY;
var initModule = gr;
var initParam = ds;
var inputObject = bH;
//var ipa = iz;
//var iph = ip;
//var ipo = iE;
var isBlank = bn;
var isCheckedAll = b2;
var isCheckedCookiePort = C3;
var isCheckedDefaultPort = u4;
var isComplete = bA;
var isCompleteInitTask = hW;
var isCompleteSetParam = ic;
var isInsKey = bL;
var isInstallCKKP = en;
var isInstallIncaKrypt = eO;
var isInstallKD = ec;
var isInstallKSID = eH;
var isInstallSCSK = ev;
//var isInstalledTouchEnKey = gf; // 변경하면 안됨.. 외부업체 변수/함수임...
var isNull = au;
var isRequiredCheckUpdate = s6;
var isRequiredReady = i6;
var isRequiredReinstall = pl;
var isRunAOS = dg;
var isRunNetizen = ee;
var isShowMaxFailure = x7;
var isShowMessage = tY;
var isShowMessage1 = sg;
var isShowMessage2 = mw;
var j = ap;
var jobs = cb;
//var k = gg;
//var kdefense = hD; // 변경하면 안됨.. 외부업체 변수/함수임...
var keepAlive = ay;
var keyExpansion = F;
var keySched = cK;
var keySizeInBits = gl;
var keylen = dR;
var keymode = ca;
var keypadElements = xI;
var kpdAutomaticWidth = Ex;
var kpdRunType = i1;
var kpdShowType = wI;
var kpdToggleOff = Zi;
var kpdToggleOn = oM;
var kpdUseynCreate = Ia;
var kpdUseynForm = By;
var kpdUseynType = mx;
//var l = hw;
//var layer = dp;
var leftpad = cg;
var len = be;
var libnplc = fB;
//var lnx = bg;
//var lnx32 = jH;
//var lnx64 = ks;
//var mac = bp;
var major = fa;
var makeCommand = cQ;
var makeElement = c1;
var makeHeader = qh;
var makeLength = cC;
var makePath = pH;
var makeUrl = cZ;
var makeUuid = gv;
var maxkc = gi;
var maxrk = df;
var minor = eA;
var minversion = dX;
var moduleVersion = BF;
var multiFunc = fr;
var nElement = bN;
var nIndex = bG;
var newElement = n5;
var node = eo;
var nodeTpe = gP;
var nosDData = dq;
var nosKeepAlive = fl;
var nosStart = ga;
var nosStatus = hu;
var nosStop = gc;
var nosVersion = ih;
var npBaseCtrl = zp;
var npCommon = L;
var npConsole = Mc;
var npDPacket = eF;
var npDefine = D;
var npEfdsWCtrl = lo;
var npEfdsWCtrl64 = kE;
var npFCtrl = Xv;
var npFPacket = bV;
var npKCtrl = bh;
var npKPacket = bv;
var npLogCollectorw = eg; // 문자열(vcw)
var npLogCollectorwDll = dW; // 문자열(vcwDll)
var npLogCollectorwDll64 = es; // 문자열(vcwDll64)
var npMessage = N;
//var npNCtrl = bQ;
var npNPacket = bI;
var npPacket = Ye;
var npPfsConst = ad;
//var npPfsCtrl = aP; // 변경하면 안됨
var npPfsPolicy = uV;
var npPfsUserInformation = O;
var npTransaction = bk;
var npkEvent = cq;
var npkFeild = ia;
var npkParam = ib;
//var ns = jm;
var obj = aa;
var objIDefense = cF;
var objKDefense = du;
var octets = ce;
var onCheckFFKeyEvent = gy;
var onCheckFFKeyPreeEvent = gB;
var onCheckFFKeyPressEvent = m7;
var onCheckKeyEvent = gT;
var onClearClickEvent = fc;
var onClearKeyEnvet = ju;
var onClearMouseEvent = hI;
var onFinalizeFrameWork = jl;
var onKeydown = O2;
var onSetFieldData = iy;
var onSetMouseEvent = iM;
//var op = dM;
var orgEvent = aS;
var orgVerArr = cf;
var orgVerArrLen = fM;
var osVersion = cR;
var pNosPacket = hx;
var packBytes = K;
var packed = ci;
var pad = bY;
var pads = dF;
var pageId = bi;
var paramFunc = bK;
var params = bc;
var parseOptions = Qm;
var parseResult = r9;
var pcode = dC;
var plain = bP;
var prepareDecryption = gU;
var printInterval = aR;
var printStackTrace = ps;
var processing = gJ;
var processingDataMap = de;
var putFunc = cD;
var putOne = gS;
//var qq = eu;
var query = eN;
var rconpointer = hc;
var reSF = kO;
var regPCSignUp = ky;
var registEachField = f7;
var registField = Ze;
var registFieldToDiv = hd;
var registFieldToForm = hn;
var requestItems = ba;
var requestState = jI;
var ret = bs;
var returnResult = ai;
var rk = bo;
var rk2 = ao;
var rounds = aU;
var rsaresult = Nw;
var secukey = bU;
var seculogVersion = aT;
var secureKeyStatus = hl;
var secureValue = co;
var security = xX;
var seedkey = Qh;
var sendAsyncCommand = gj;
var sendCommand = fs;
var sendSyncCommand = gb;
var setCookie = hf;
var setFieldInformation = gd;
var setFieldOption = gW;
var setPCAppoint = jJ;
var setPCAppointPath = kM;
var setParameter = sp;
var setState = aO;
var setStateGroup = x0;
var setValue = ab;
//var sf = dY;
var showDefaultLoading = Wb;
var showDetectDebug = mW;
//var showLoading = hg;
var signupFunc = kp;
var splitVersion = gn;
var startAlive = sa;
//var startup = iF;
var stopAlive = qa;
var stopEvent = so;
var strData = hG;
var strOrgVer = gM;
var strSplitData = iC;
var strVer = hk;
//var t = aY;
var t0 = aw;
var task = bM;
var taskStart = dB;
var tasks = cd;
var td1 = ft;
var td2 = ep;
var temp = aM;
var tempPort = cU;
var terminate = JF;
var textStatus = ej;
var theform = bX;
var timestamp = iR;
var tk = bD;
var toPlain = ew;
var tua = fw;
//var ubt = fO;
var ucode = eZ;
var unpackBytes = R;
var unusedExtentionPointIndex = j8;
var uuid = aJ;
var ver = dv;
var verArr = cY;
var versionParam = dG;
var waitRun = Cb;
var what = ik;
//var win98 = ix;
//var win9x = hO;
//var winme = ii;
//var winmob = iW;
//var winnt40 = jg;
//var winphone = hL;
var x0 = gX;
var x3 = gQ;
</textarea>
        </td>
    </tr>
</table>

<%
    }        // end of mode "replacement"

    if ("majorjs".equals(mode)) {
%>
<script type="text/javascript">
</script>
<table style="width:100%" ;border-spacing:0px;padding:0px;
">
<tr>
    <th style="text-align:left;font-size:14pt;" colspan="4">주요 자바스크립트 변수/함수/구문</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>항목</th>
    <th>치환 전/후 코드</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="5">URL 수정</th>
    <td>접근하는 페이지를 임의로 변경합니다.</td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td><pre>
npPfsPolicy.Common.KeyUrl = "custom.path/pluginfree/jsp/nppfs.key.jsp";    // 키발급 경로
npPfsPolicy.Common.RemoveKey = "custom.path/pluginfree/jsp/nppfs.remove.jsp"; // 키삭제 경로
npPfsPolicy.Common.KeyPadUrl = "custom.path/pluginfree/jsp/nppfs.keypad.jsp;  // 마우스입력기 페이지
npPfsPolicy.Common.ReadyUrl  = "custom.path/pluginfree/jsp/nppfs.ready.jsp";  // 초기화상태 확인경로
npPfsPolicy.Common.InstallUrl = "custom.path/pluginfree/jsp/nppfs.install.jsp; // 설치안내 페이지
</pre>
    </td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td><pre>
uV.dV.Gf = "custom.path/pluginfree/jsp/nppfs.key.jsp";    // 키발급 경로
uV.dV.zf = "custom.path/pluginfree/jsp/nppfs.remove.jsp"; // 키삭제 경로
uV.dV.zo = "custom.path/pluginfree/jsp/nppfs.keypad.jsp;  // 마우스입력기 페이지
uV.dV.eP = "custom.path/pluginfree/jsp/nppfs.ready.jsp";  // 초기화상태 확인경로
uV.dV.Fz = "custom.path/pluginfree/jsp/nppfs.install.jsp; // 설치안내 페이지
</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="5">디버그모드전환</th>
    <td>릴리즈로 동작하는 스크립트를 디버그 모드 동작으로 전환합니다.</td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>
        <pre>npPfsPolicy.Common.RuntimeMode = npPfsConst.MODE_DEBUG;</pre>
    </td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>
        <pre>uV.dV.dk = ad.jt;</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="5">콘솔로그출력</th>
    <td>개발자도구의 console에 로그를 출력합니다.</td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>
        <pre>npConsole.log("메시지 출력");</pre>
    </td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>
        <pre>Mc.log("메시지 출력");</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="5">작업상태보기</th>
    <td>5초마다 주기적으로 작업상태를 출력합니다.</td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td><pre>
setInterval(function(){
	npConsole.log("--------- 현재 작업상태 출력 ---------");
	npTransaction.print();
}, 5000);
</pre>
    </td>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td><pre>
setInterval(function(){
	Mc.log("--------- 현재 작업상태 출력 ---------");
	bk.print();
}, 5000);
</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
</table>
<%
    }        // end of mode "majorjs"


    if ("tsfaq".equals(mode)) {
%>
<table style="width:100%" ;border-spacing:0px;padding:0px;
">
<tr>
    <th style="text-align:left;font-size:14pt;" colspan="4">자주묻는 질문과 답변(기술지원)</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>구분</th>
    <th>질문/답변</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기본 설치</th>
    <th class="question">질문 : 기본적인 설치방법은 무엇입니까?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 다음 작업을 순차적으로 진행합니다.
        <ol style="margin-top:3px;">
            <li>/pluginfree/js/* 파일을 WEB 서버에 복사합니다.</li>
            <li>/pluginfree/jsp/* 파일을 WAS 서버에 복사합니다.</li>
            <li>*.jar 파일을 WAS 라이브러리 경로에 복사합니다. 일반적으로 /WEB-INF/lib 경로입니다.</li>
            <li>마우스입력기를 사용할 경우 /WEB-INF/resources/* 파일을 WEB서버가 아닌 WAS서버에 복사합니다.</li>
            <li>/WEB-INF/web.xml 파일을 열어 filter 기능을 추가합니다. nprotect.properties경로 및 사이트 언어를 적절하게 수정합니다.</li>
            <li>nprotect.properties파일을 열어 라이선스에 맞게 적절하게 수정합니다.</li>
            <li>WEB/WAS 서버를 재기동 합니다.</li>
            <li>WAS 서버기동 시 공개키 및 환경설정값이 정상적으로 출력되는지 확인합니다.</li>
            <li>/pluginfree/examples/index.jsp의 동작을 확인합니다.</li>
        </ol>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기본 설치</th>
    <th class="question">질문 : NOS제품의 구동가능여부를 확인할 수 있나요?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : npPfsCtrl.IsSupport() 함수를 사용하여 현재의 접속환경이 NOS 지원환경인지를 확인합니다. 반환값 : true : 지원환경, false 미지원 환경
        <pre class="code-example">
var isSupport = npPfsCtrl.IsSupport();
if(!isSupport) {
	// 이 부분에 미지원 환경에 대한 가이드 또는 추가 업무 로직을 구현합니다.
	alert("보안프로그램을 지원하지 않는 환경입니다.");
}
</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기본 설치</th>
    <th class="question">질문 : 가상운영체제에서 동작하는지 확인할 수 있나요?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : npPfsCtrl.isVirtualMachine() 함수를 사용하여 현재 접속환경이 가상운영체제인지를 판단합니다. Callback 함수를 사용하여 전달된 인자 값이 true일 경우
        가상운영체제로 판단된 결과입니다.
        <pre class="code-example">
// 가상운영체제 여부 확인
npPfsCtrl.isVirtualMachine(function(result){
	if(result == true) {
		alert("가상운영체제 또는 원격으로 접속하셨습니다. 보안프로그램의 일부 기능을 사용하실 수 없습니다. ");
	}
});</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기본 설치</th>
    <th class="question">질문 : NOS제품의 설치여부를 확인할 수 있나요?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : npPfsCtrl.isInstall() 함수를 사용하여 설치여부를 확인할 수 있습니다.
        <pre class="code-example">
npPfsCtrl.isInstall({
	success : function(){
		//설치됨
	},
	fail : function(){
		// 설치안됨
	}
});</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">페이지 적용</th>
    <th class="question">질문 : 키보드입력(또는 마우스입력)되는 form양식과 데이터전송용 form이 상이합니다. 어떻게 적용해야하나요?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 적용가이드 문서의 "동적으로 생성되는 필드 및 값 복사" 항목을 참고하십시오.
        <ol style="margin-top:3px;">
            <li>npPfsCtrl.copy() 함수를 사용하여 동적 생성 필드 및 값을 복사합니다. 동적으로 생성된 필드만을 대상으로 합니다. 즉, 기존에 존재하는 입력양식의 필드들은 복사하지 않습니다.
                개발자가 수동으로 등록해야 합니다.
            </li>
            <li>대상 입력양식이 이미 존재할 경우 덮어씁니다. 누락되는 데이터가 발생하지 않게 주의하여 사용해야 합니다.</li>
        </ol>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">페이지 적용</th>
    <th class="question">질문 : 동적으로 생성되는 필드가 있습니다. 키보드보안/마우스입력기는 어떻게 적용해야하나요?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 적용가이드 문서의 "동적으로 생성되는 필드에 키보드보안/마우스입력기 설정" 항목을 참고하십시오.
        <ol style="margin-top:3px;">
            <li>npPfsCtrl.RegistDynamicField() 함수를 이용하여 입력양식이 동적으로 추가되는 경우 생성된 입력양식에 키보드보안/마우스입력기를 사용하도록 처리합니다. 입력양식이
                생성되고 난 이후에 호출하도록 해야 합니다.
            </li>
        </ol>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">페이지 적용</th>
    <th class="question">질문 : 웹페이지에서 특정키만 사용할 수 있도록 필터링기능을 넣으려고 합니다. 가능한가요?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 적용가이드 문서의 "키보드보안 키 필터링" 항목을 참고하십시오.
        <ol style="margin-top:3px;">
            <li>npPfsCtrl.SetGlobalKeyValidation() 함수를 사용하여 필드에 입력되는 키 값을 예외처리 합니다. 본 함수는 NOS가 실행되고 있는 페이지 전체에 일괄적으로
                적용됩니다.
            </li>
            <li>NOS의 키보드보안이 초기화되기 전(npPfsStartup함수 호출 전)에 본 함수를 적용해야 정상 동작합니다.</li>
            <li>키 값을 검증 로직이 구현된 함수를 인자 값으로 전달합니다. 이때 구현되는 함수는 keycode 값을 인자 값으로 받습니다. 로직 구현이 키 값을 사용하려면 true를 반환하고 취소하려면
                false를 반환되도록 합니다.
            </li>
        </ol>
        <pre class="code-example">
// 숫자만 입력되도록 구현된 예제
npPfsCtrl.SetGlobalKeyValidation(function(keyCode) {
	// true : 정합성 충족, false : 정합성 미충족
	if(keyCode >= 48 && keyCode <=57) return true;
	return false;
});
</pre>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>

</table>
<%
    }        // end of mode "tsfaq"

    if ("csfaq".equals(mode)) {
%>
<table style="width:100%" ;border-spacing:0px;padding:0px;
">
<tr>
    <th style="text-align:left;font-size:14pt;" colspan="4">자주묻는 질문과 답변(고객상담)</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>
<tr>
    <th>구분</th>
    <th>질문/답변</th>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기본확인</th>
    <th class="question">질문 : NOS가 적용된 페이지의 기본적인 확인사항은 무엇입니까?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 다음사항을 먼저 확인합니다.
        <ol style="margin-top:3px;">
            <li>/pluginfree/jsp/nppfs.script.jsp, /pluginfree/js/nppfs-1.0.0.js 가 정상적으로 링크되어 있는지 확인합니다.</li>
            <li>jquery-1.7.x.js 이상버전이 정상적으로 링크되어 있는지 확인합니다.<br/>jquery-2.x.x.js은 IE 6/7/8브라우저를 지원하지 않아서 NOS에서는 사용할 수
                없습니다.
            </li>
            <li>$(document).ready() 또는 $(window).load()를 통해서 npPfsStartup 함수가 정상적으로 호출되지 있는지 확인합니다.</li>
        </ol>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기본확인</th>
    <th class="question">질문 : End-User PC의 기본 확인사항은 무엇입니까?</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 다음사항을 먼저 확인합니다.
        <ol style="margin-top:3px;">
            <li>IE의 경우 도구 > 인터넷 옵션 > 고급 > HTTP설정에서 "HTTP1.1" 항목이 체크되어 있어야 합니다.<br/>HTTP1.0에서는 GET 방식사용시 전달값의 길이 제한이
                있습니다.
            </li>
            <li>최신 NOS 모듈이 설치되어 있는지 확인합니다.</li>
            <li>브라우저에서 https://pfs.nprotect.com:14430 을 브라우저에 직접입력하여 보여지는 값을 확인합니다.</li>
            <li>브라우저에서 https://127.0.0.1:14440 을 브라우저에 직접입력하여 보여지는 값을 확인합니다.</li>
            <li>브라우저의 브록시 설정이 올바른지 확인합니다.</li>
            <li>명령프롬프트(cmd) 창에서 nslookup pfs.nprotect.com 의 값이 127.0.0.1로 나오는지 확인합니다.</li>
            <li>명령프롬프트(cmd) 창에서 telnet pfs.nprotect.com 14430 으로의 연결의 응답값이 "B303AA8350126650FCE9111D899E21F0"인지 확인합니다.
            </li>
            <li>명령프롬프트(cmd) 창에서 telnet 127.0.0.1 14440 으로의 연결의 응답값이 "B303AA8350126650FCE9111D899E21F0"인지 확인합니다.</li>
            <li>방화벽에서 14430~14449로의 포트 또는 프로세스 차단이 있는지 확인합니다.</li>
            <li>폐쇄망(가상망, Proxy, Auto Proxy)에서 동작하는지 확인합니다.</li>
            <li>타업체 보안프로그램과 충돌이 없는 확인합니다.</li>
        </ol>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>


<tr>
    <th rowspan="3">기타</th>
    <th class="question">질문 : "보안프로그램과의 연결이 원활하지 않습니다."라는 메시지가 나옵니다.</th>
</tr>
<tr>
    <td class="divider"></td>
</tr>
<tr>
    <td>답변 : 웹페이지와 NOS 프로그램과의 통신과정에서 오류가 15회일어났을 때 발생합니다. 자바스크립트를 최신으로 패치해야 합니다.
        <ol style="margin-top:3px;">
            <li>2015년 04월 15일 이전 자바스크립트라면 웹페이지 전체 15회 오류가 일어났을 때 해당메시지를 출력합니다.</li>
            <li>2015년 04월 15일 이후 자바스크립트라면 웹페이지 연속 15회 오류가 일어났을 때 해당메시지를 출력합니다.</li>
            <li>일부 저사양 PC에서는 보안프로그램과의 오류발생이 증가할 수 있습니다. PC의 권장사양을 확인하십시오.</li>
            <li>WAS 모듈 설치시 정상적으로 RSA키페어 쌍을 생성할 수 없을 경우에 발생합니다.</li>
        </ol>
    </td>
</tr>
<tr>
    <td colspan="2" class="divider"></td>
</tr>

</table>
<%
    }        // end of mode "csfaq"


    if ("cipher".equals(mode)) {
%>
<script type="text/javascript">
    var npCommon = L;


    function doGenerateRandom(target, length) {
        var key = L.hH(L.mL(length));
        //var key = npCommon.hexEncode(npCommon.getRandomBytes(32));
        $(target).val(key);
        return false;
    }


    function doEncryptAes128() {
        var mode = "ECB";
        var key = $("#aeskey128").val();
        var plain = $("#aesplain128").val();
        var enckey = L.ha(key);
        var ciphertext = L.encrypt(plain, enckey, mode, 128);
        //var enckey = npCommon.hexDecode(key);
        //var ciphertext = npCommon.encrypt(plain, enckey, mode, 128);
        $("#aesencrypt128").val(ciphertext);
    }

    function doDecryptAes128() {
        var mode = "ECB";
        var key = $("#aeskey128").val();
        var enc = $("#aesencrypt128").val();
        var enckey = L.ha(key);
        var dec = L.gu(L.ha(enc), enckey, mode, 128);
        //var enckey = npCommon.hexDecode(key);
        //var dec = npCommon.decrypt(npCommon.hexToString(enc), enckey, mode, 128);
        $("#aesdecrypt128").val(dec);
    }

    function doEncryptAes256() {
        var mode = "ECB";
        var key = $("#aeskey256").val();
        var plain = $("#aesplain256").val();
        var enckey = L.ha(key);
        var ciphertext = L.encrypt(plain, enckey, mode, 128);
        //var enckey = npCommon.hexDecode(key);
        //var ciphertext = npCommon.encrypt(plain, enckey, mode, 128);
        $("#aesencrypt256").val(ciphertext);
    }

    function doDecryptAes256() {
        var mode = "ECB";
        var key = $("#aeskey256").val();
        var enc = $("#aesencrypt256").val();
        var enckey = L.ha(key);
        var dec = L.gu(L.ha(enc), enckey, mode, 128);
        //var enckey = npCommon.hexDecode(key);
        //var dec = npCommon.decrypt(npCommon.hexToString(enc), enckey, mode, 128);
        $("#aesdecrypt256").val(dec);
    }


    <%
String exec = param(request, "c", "aria256");
%>
    $(document).ready(function () {
        $('.cipher').hide();
        <%
if("aria256".equals(exec)) {
	%>
        $('.cipher-aria256').show();
        <%
} else if("aes128".equals(exec)) {
	%>
        $('.cipher-aes128').show();
        <%
} else if("aes256".equals(exec)) {
	%>
        $('.cipher-aes256').show();
        <%
} else if("rsa2048".equals(exec)) {
	%>
        $('.cipher-rsa2048').show();
        <%
} else if("seed128".equals(exec)) {
	%>
        $('.cipher-seed128').show();
        <%
} else {
	%>
        $('.cipher-aria256').show();
        <%
}
%>

    });

    function reflectOption() {
        var mode = $("input[name=ariaMode]:checked").val();
        if (mode === "m2" || mode === "m3" || mode === "m4" || mode === "m5" || mode === "m6") {
            $("#ariakey").val("").prop({readonly: true}).addClass("readonly");
            $("#ariaiv").val("").prop({readonly: true}).addClass("readonly");
            $("#ariaWithIv").prop({checked: true});
            if (mode === "m6") {
                $("#ariaWithIv").prop({checked: false});
            }
        } else {
            $("#ariakey").val("").prop({readonly: false}).removeClass("readonly");
            $("#ariaiv").val("").prop({readonly: false}).removeClass("readonly");
            $("#ariaWithIv").prop({checked: false});
        }
    }

    function getAriaMode() {
        var mode = $("input[name=ariaMode]:checked").val();
        if (mode === "m2") return "nos";
        if (mode === "m3") return "fw";
        if (mode === "m4") return "fm";
        if (mode === "m5") return "key";
        if (mode === "m6") return "fds";
        return "";
    }

    function doEncryptAria256() {
        <%--
	String exec = param(request, "e", "e");
	String alorithm = param(request, "a", "aria");
	String type = param(request, "t", "n");
	String key = param(request, "k", "");
	String iv = param(request, "i", "");
	String value = param(request, "v", "");
	String result = value;
	boolean defaultIv = ("y".equals(param(request, "d", "n"))) ? true : false;
	boolean includeIv = ("y".equals(param(request, "c", "n"))) ? true : false;
--%>

        var key = $("#ariakey").val();
        var iv = $("#ariaiv").val();
        var value = $("#ariaplain").val();

//	alert("[" + key + "][" + iv + "][" + value + "]");

        var param = [];
        param.push("m=c");
        param.push("e=e");
        param.push("t=" + getAriaMode());
        param.push("k=" + encodeURIComponent(key));
        param.push("i=" + encodeURIComponent(iv));
        param.push("v=" + encodeURIComponent(value));
        param.push("d=" + ((iv === "") ? "y" : "n"));
        param.push("c=" + ($("#ariaWithIv").prop("checked") ? "y" : "n"));
        send("<%= currentRequestURI %>", param.join("&"), function (responseText) {
            $("#ariaencrypt").val(responseText);
        });
    }

    function doDecryptAria256() {
        var key = $("#ariakey").val();
        var iv = $("#ariaiv").val();
        var value = $("#ariaencrypt").val();

        var param = [];
        param.push("m=c");
        param.push("e=d");
        param.push("t=" + getAriaMode());
        param.push("k=" + encodeURIComponent(key));
        param.push("i=" + encodeURIComponent(iv));
        param.push("v=" + encodeURIComponent(value));
        param.push("d=" + ((iv === "") ? "y" : "n"));
        param.push("c=" + ($("#ariaWithIv").prop("checked") ? "y" : "n"));
        send("<%= currentRequestURI %>", param.join("&"), function (responseText) {
            $("#ariadecrypt").val(responseText);
        });
    }
</script>

<form onsubmit="return false;">
    <table>
        <tr>
            <th style="text-align:left;font-size:14pt;" colspan="2">암/복호화 테스트</th>
        </tr>
        <tr>
            <td>
                <button onclick="$('.cipher').hide();$('.cipher-aria256').show();">ARIA 256</button>
                <button onclick="$('.cipher').hide();$('.cipher-aes128').show();">AES 128</button>
                <button onclick="$('.cipher').hide();$('.cipher-aes256').show();">AES 256</button>
                <!-- <button onclick="$('.cipher').hide();$('.cipher-rsa2048').show();">RSA 2048</button> -->
                <!-- <button onclick="$('.cipher').hide();$('.cipher-seed128').show();">Seed 128</button> -->
            </td>
        </tr>
        <tr class="cipher cipher-aria256" style="display:none;">
            <td>
                <table>
                    <tr>
                        <th colspan="3">ARIA 암호화(Server) - 알고리즘 : Key : 256bit, Block : CBC, Padding : PKCS5Padding</th>
                    </tr>
                    <tr>
                        <td>용도</td>
                        <td colspan="2">
                            <input type="radio" id="aria1" name="ariaMode" value="m1" onclick="reflectOption();"
                                   checked="checked"/><label for="aria1">기본</label><br/>
                            <input type="radio" id="aria2" name="ariaMode" value="m2" onclick="reflectOption();"/><label
                                for="aria2">통신 - 로컬 대몬</label><br/>
                            <input type="radio" id="aria3" name="ariaMode" value="m3" onclick="reflectOption();"/><label
                                for="aria3">통신 - 방화벽(윈도우)</label><br/>
                            <input type="radio" id="aria4" name="ariaMode" value="m4" onclick="reflectOption();"/><label
                                for="aria4">통신 - 방화벽(맥,리눅스)</label><br/>
                            <input type="radio" id="aria5" name="ariaMode" value="m5" onclick="reflectOption();"/><label
                                for="aria5">통신 - 키보드보안</label><br/>
                            <input type="radio" id="aria6" name="ariaMode" value="m6" onclick="reflectOption();"/><label
                                for="aria6">통신 - 단말정보수집</label><br/>
                        </td>
                    </tr>
                    <tr>
                        <td>키</td>
                        <td><input type="text" id="ariakey" name="ariakey" value="" class="input-text"></td>
                        <td>
                            <button onclick="doGenerateRandom(this.form.ariakey, 32);return false;">키생성</button>
                        </td>
                    </tr>
                    <tr>
                        <td>IV</td>
                        <td><input type="text" id="ariaiv" name="ariaiv" value="" class="input-text"></td>
                        <td>
                            <button onclick="doGenerateRandom(this.form.ariaiv, 16);return false;">IV생성</button>
                        </td>
                    </tr>
                    <tr>
                        <td>평문</td>
                        <td><input type="text" id="ariaplain" name="ariaplain" value="" class="input-text"></td>
                        <td>
                            <button onclick="doEncryptAria256();">암호화</button>
                            <input type="checkbox" id="ariaWithIv" name="ariaWithIv" value=""><label for="ariaWithIv1">IV
                            병합</label>
                        </td>
                    </tr>
                    <tr>
                        <td>암호화</td>
                        <td><input type="text" id="ariaencrypt" name="ariaencrypt" value="" class="input-text"></td>
                        <td>
                            <button onclick="doDecryptAria256();">복호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>복호화</td>
                        <td colspan="2">
                            <input type="text" id="ariadecrypt" name="ariadecrypt" value="" class="input-text">
                        </td>
                    </tr>
                    <tr>
                        <td>용도</td>
                        <td colspan="2">E2E 데이터 암호화</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="cipher cipher-aes128" style="display:none;">
            <td>
                <table>
                    <tr>
                        <th colspan="3">AES 암호화(Javascript) - 알고리즘 : Key : 128bit, Block : ECB, Padding : ZeroPadding
                        </th>
                    </tr>
                    <tr>
                        <td>키</td>
                        <td><input type="text" id="aeskey128" name="aeskey128" value="" class="input-text"></td>
                        <td>
                            <button onclick="doGenerateRandom(this.form.aeskey128, 16);return false;">키생성</button>
                        </td>
                    </tr>
                    <tr>
                        <td>평문</td>
                        <td><input type="text" id="aesplain128" name="aesplain128" value="" class="input-text"></td>
                        <td>
                            <button onclick="doEncryptAes128();">암호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>암호화</td>
                        <td><input type="text" id="aesencrypt128" name="aesencrypt128" value="" class="input-text"></td>
                        <td>
                            <button onclick="doDecryptAes128();">복호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>복호화</td>
                        <td colspan="2"><input type="text" id="aesdecrypt128" name="aesdecrypt128" value=""
                                               class="input-text"></td>
                    </tr>
                    <tr>
                        <td>용도</td>
                        <td colspan="2">키보드보안 입력양식 응답값</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="cipher cipher-aes256" style="display:none;">
            <td>
                <table>
                    <tr>
                        <th colspan="3">AES 암호화(Javascript) - 알고리즘 : Key : 256bit, Block : ECB, Padding : ZeroPadding
                        </th>
                    </tr>
                    <tr>
                        <td>키</td>
                        <td><input type="text" id="aeskey256" name="aeskey256" value="" class="input-text"></td>
                        <td>
                            <button onclick="doGenerateRandom(this.form.aeskey256, 32);return false;">키생성</button>
                        </td>
                    </tr>
                    <tr>
                        <td>평문</td>
                        <td><input type="text" id="aesplain256" name="aesplain256" value="" class="input-text"></td>
                        <td>
                            <button onclick="doEncryptAes256();">암호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>암호화</td>
                        <td><input type="text" id="aesencrypt256" name="aesencrypt256" value="" class="input-text"></td>
                        <td>
                            <button onclick="doDecryptAes256();">복호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>복호화</td>
                        <td colspan="2"><input type="text" id="aesdecrypt256" name="aesdecrypt256" value=""
                                               class="input-text"></td>
                    </tr>
                    <tr>
                        <td>용도</td>
                        <td colspan="2">마우스입력기 입력양식 응답값</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="cipher cipher-rsa2048" style="display:none;">
            <td>
                <table>
                    <tr>
                        <th colspan="3">RSA 암호화(Javascript) - 알고리즘 : Key : 256bit, Block : ECB, Padding : PKCS1Padding
                        </th>
                    </tr>
                    <tr>
                        <td>키</td>
                        <td><input type="text" id="aeskey256" name="aeskey256" value="" class="input-text"></td>
                        <td>
                            <button onclick="">키생성</button>
                        </td>
                    </tr>
                    <tr>
                        <td>평문</td>
                        <td><input type="text" id="aesplain256" name="aesplain256" value="" class="input-text"></td>
                        <td>
                            <button onclick="doEncryptAes256();">암호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>암호화</td>
                        <td><input type="text" id="aesencrypt256" name="aesencrypt256" value="" class="input-text"></td>
                        <td>
                            <button onclick="doDecryptAes256();">복호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>복호화</td>
                        <td colspan="2"><input type="text" id="aesdecrypt256" name="aesdecrypt256" value=""
                                               class="input-text"></td>
                    </tr>
                    <tr>
                        <td>용도</td>
                        <td colspan="2">키보드보안, 단말정보수집, 마우스입력기 키교환</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="cipher cipher-seed128" style="display:none;">
            <td>
                <table>
                    <tr>
                        <th colspan="3">SEED 암호화(Javascript) - 알고리즘 : Key : 256bit, Block : ECB, Padding : NoPadding
                        </th>
                    </tr>
                    <tr>
                        <td>키</td>
                        <td><input type="text" id="seedkey128" name="seedkey128" value="" class="input-text"></td>
                        <td>
                            <button onclick="doGenerateRandom(this.form.seedkey128, 16);return false;">키생성</button>
                        </td>
                    </tr>
                    <tr>
                        <td>평문</td>
                        <td><input type="text" id="seedplain128" name="seedplain128" value="" class="input-text"></td>
                        <td>
                            <button onclick="doEncryptAes128();">암호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>암호화</td>
                        <td><input type="text" id="seedencrypt128" name="seedencrypt128" value="" class="input-text">
                        </td>
                        <td>
                            <button onclick="doDecryptAes128();">복호화</button>
                        </td>
                    </tr>
                    <tr>
                        <td>복호화</td>
                        <td colspan="2"><input type="text" id="seeddecrypt128" name="seeddecrypt128" value=""
                                               class="input-text"></td>
                    </tr>
                    <tr>
                        <td>용도</td>
                        <td colspan="2">KCP Quick Pay 데이터 연동</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>
<%
    }        // end of mode "cipher"


    if ("command".equals(mode)) {
%>
<script type="text/javascript">
    <%
String exec = param(request, "d", "req");
%>
    $(document).ready(function () {
        <%
if("res".equals(exec)) {
	%>
        $('#command-res').prop({checked: true});
        <%
} else {
	%>
        $('#command-req').prop({checked: true});
        <%
}
%>
    });
</script>

<form action="<%= currentRequestURI %>" method="post" target="nos-command-iframe">
    <input type="hidden" name="m" value="parseCommand">
    <table style="width:100%;height:100%">
        <tr>
            <th style="text-align:left;font-size:14pt;" colspan="2">명령어 요청/응답 분석</th>
        </tr>
        <tr>
            <td colspan="2" class="divider"></td>
        </tr>
        <tr>
            <th>구문</th>
            <td>
                <textarea id="commandCode" name="code" cols="40" rows="10" style="width:100%;"></textarea>
            </td>
            <!-- 요청 -->
            <!--  -->
            <!--  -->

        </tr>
        <tr>
            <td colspan="2" class="divider"></td>
        </tr>
        <!--
	<tr>
		<th>구분</th>
		<td>
			<input type="radio" id="command-req" name="commandMode" value="req" checked="checked" /><label for="aria1">요청</label>
			<input type="radio" id="command-res" name="commandMode" value="res" /><label for="aria2">응답</label><br />
		</td>
	</tr>
	<tr><td colspan="2" class="divider"></td></tr>
-->
        <tr>
            <th>요청 명령(예시)</th>
            <td>
                <table>
                    <tr>
                        <td style="vertical-align:top;">
                            <!-- 로컬대몬 -->
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('36a364c4e48a8a76d80f0cd03350e74a7c137a81089c3af5d003ccf810b555831400000040493aba5002ffddb5bf9fe225fe8812e835b49b75ab5e4a75f583fc1bcd7b7041004088f8759ec2c5719b2d05eaedb616e96e16900ac057755a977e858992e78787f50004100017bb338a7d2c02f7ed1bb1824707b19242a5eddabd92118f35b92b5f80f3feb7010510100dfb7f016bf147eb151fc6c501c4e0e96941ba1815193f186f777d7c8bc53a912389bf2080f49404df2021d760e93a327064c66fc8aaecd54514369bd6d3ce6b06fdd4e102a576a3d8a08aac29db457d36859098cd70835dfdcbb5d7e11a60a52395052ea6cda16f91e14f34ae255e7d66a47e8ce8e52452da2c12ec0dbd98938');"/>로컬대몬
                            - 핸드쉐이크(윈도우)<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('73ff06f0735d2cc8fa3910c2f2c1d1438b9f9b36a7ac9a976aa5ca683c85cdd91400000040d06a92a66bf69d6c73a9ea672f2aabf66d9adcaec15077822571b934e4fd59dc00405dc2c0d5ec7113f4a60751d601e129d37781fd3fed401b7944988b9a464adaab0004100064a6c53192f54cc26a084eb9f9a8e7868ea8f2b016ffc57f4a78d16bc8473b0d012510120a34bad70c6b75718b18a3afde7bc77d44da2840ac6ff7c4459647a2834ae698478cc8b79db07f0e5cdc166481870ae607d97632098df09a32d0ebdec66d989c8026c32f543c71aafe5a686bb2a9cdca7f6d3aff61fb1eadee23a5a8d27c35997577d5094ca2db12c259fe8ee9ac643ded05b174057a489c2dbffcb6f78064c9179f0c5342a66280f608df200f2db8259');"/>로컬대몬
                            - 핸드쉐이크(멀티)<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('0884d75451a28619ebd0d3974e5bb7ece1d45bbdeb733197453018dbcaa7ba8e1400000040bf4c4ae9647a30a546cfc08dd163a358fb4f126fa771988c130aaadb87b3267c00408813a6250569f1a6f773888078bf21d29a330f5eb7c84b7bd797749856df3b9900041000884c1ee05f9a373ff2b0b776f1f3b52095d63edac9e90c12ddeb940873461584');"/>로컬대몬
                            - 가상머신동작여부확인<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('73ff06f0735d2cc8fa3910c2f2c1d1438b9f9b36a7ac9a976aa5ca683c85cdd91400000040d06a92a66bf69d6c73a9ea672f2aabf66d9adcaec15077822571b934e4fd59dc00404ad3143e5f703b1a32764b7d58cb76405f282a48233334f1f69c55eae0303793000410003dcbaeb72d0588e423bae6f665898b051d019523d84a81a91d96e4eb5e1f25db');"/>로컬대몬
                            - 서비스 동작여부 확인(연결유지)<br/>
                            <!-- 개인방화벽(윈도우) -->
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('0dc9edd8542ed87278860a9590afadc1ab499ed2e8fca7467e635b216013ced91400000040493aba5002ffddb5bf9fe225fe8812e835b49b75ab5e4a75f583fc1bcd7b70410040adb02d056afaa3c4a2c34aba29b44528aefda219f4775591a787182297a2422800041000887fa278e773c26f4c6ac24f89bcba435c20dc915ca1a74fd63b18c020d8bb680100f05e2da775f68987ed4cbd75ae2efceee1c6db7a2ec45c99c53e6ca573a8d9b7955b102b047dccdb5368a503cd08e5972b8c8f5a7384bc8b31631f90b9978e989fae0b17321f0922be6b243507a903b7a0ed57bdcfc4771b7380a031855f97806a7cf82957270528bcc1b44c741dd06c01b2084c1d87b3507f23787a5d824d52');"/>개인방화벽(윈도우)
                            - 시작<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('a04318f6997a354ecbfe61fd6b1cc19e96cfc1ece8d0b946f7be3975cb4cc0d81400000040a6a7db5da21fb0aa83a12f04c9ba4f87e294ced40cc212da4af3da97577aed6a004026808f617dff7a38a94971d97999a85ae4216d613e16d5a28ede28a8b4b2e80a00041000d8851523cedbabb99d15600d68e4ee6499ff85afa17dd58dfc22dfa1582536320100e288d5e13dbb6c110b6ba7e1ee939bf28112f08e8ca154d03a6aaecd1e7bf160fc5f2acf95287ac35f7482648725cd075741722c344d1535577129250886ce0114e94a3d6bfa9288c1faa0a1c2cfd1b2ca7513b27da42da3f363e2a60b16f84a85aebc77b27c3a6405f904cb8099b606c0f7966654c4884994daaea729e94b19');"/>개인방화벽(윈도우)
                            - 연결유지<br/>
                            <!-- 개인방화벽(멀티) -->
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('f181f270f7c7afc0ce2d9e7af9d5139f1b990cf70d42fec270ebed639dfc92781400000040bf4c4ae9647a30a546cfc08dd163a358fb4f126fa771988c130aaadb87b3267c0040e08a027aadcd3daa025e59f102dbdfd96942f60810478a5cbcea12e4f92c19b2000410000040582a1e6d3f0efde3400e71666b8789f77339a0fb766a8a16566be19d1a4a9c80');"/>개인방화벽(멀티)
                            - 시작<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('f5ef192a3cf073feeea8c6f57dda94ef62cb6041d486d203661fdd7d5e149bb01400000040d06a92a66bf69d6c73a9ea672f2aabf66d9adcaec15077822571b934e4fd59dc0040ae1fde89f7a477886ac74776e05a21283ce285237229ed545c1267547efd5b610004100000e017a92ecd55c428234bfead4161ab14d1ab3056af01ed404c8b498e99ab9782592cf4ff97b64037886ea9799fb95b7fdfd13a300d410b5373980c9eb114d6faa7de6aff88cd3952f5bd6ec768fcc3e2cf3db08726b027dfc97b14f2c5b339ea45f445644a60d8e6ba45ae4e210dcf84c9');"/>개인방화벽(멀티)
                            - 연결유지<br/>
                        </td>
                        <td style="vertical-align:top;">
                            <!-- 키보드보안 -->
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c0040be0e5c436e19910c8d595e05df669f9f06c819ad32451067e4ab2204515055db000410007747b11944112dd27be0f6115a64fd9e5283ef3dc9438c809bbc3c8e390e36485650c5019ca26d4b984d0be819790e54169589e5b538d6c782ef9007e80dd9e7');"/>키보드보안
                            - 초기화<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c00406269a35704ec061e39445fb53ca4f6924c71acecdd6cd3da450856aea9b16cf7000410008a4a89673dca754179b6fdb5f1df61772e6c1872c4bc9a55db053db4cb437734PKI=5&Cert=&CertEnc=a903665378d764a98eaba0c40df690cc3537e9f0835f9818675891d4604b269047c219bca45d50640e665f704f53546725648cb0c8e9dfa6166b1b4da7dfbff3a379332c056126fd66a2b9379adfe9f23bf814b343408045c84812bae14f1404b92cf93a7fcb002a167df4c399481142842c0f4473b11a4b1a327b0e6350f8be14c7141d68920e70d1f84c38dd52ae4f8a0fe83584a48df9cb2e164cc983f4f3d0206d6f72a98cd2108b36b393ec423717a80769b9d8506ba07530dcdc4c2e3fd5635b7939ca12bd5a66538356db9dc6650f2d531815abc717f6c32585bc24c956d9b275c8624f64ca94c36f2180ad9f710e185adb1f1f4d34f103e08f4a3a507866a2654a0984c48b1ce4416511b607c0f155e599890e5a6c2cfb363cfcb7b20f9612199a65bfb2c2ede0beb0a91186c66d5a23064b7f637830efb20a88cc3678f5a589ebae6cc4e99949d4f377d92eb1fcc48ebf13f0829b0902db0e1a03f19db761b9885d2292a439c9709d1da58e0071ba5fb888c7ef0dc53db93d4ae99eaf378b0ca84bc4c43df64ee4cf4512c52ddd9ae731b8d1312deaad0e904e42901e119915895200bb26eda582717b932468bd29bf220e837383f05ca2762372c038ab0638c041ca80af43004028b622083835a6ffbb7904a9443e33be88ad65645ce76f9f9baf6832470abc4839aad9e5ee76a9794980b20c7877b073af86ec3e9285ce3d57cf0727d8b187f172612cc85615481c75ccab95b2e9da4a68bd5643&ID=ae5e8ad53f364a95b16d31ea72a8d72f');"/>키보드보안
                            - E2E 초기화<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c0040e9a6747b236907eeabe88b60c0fb25a3264813120eebe7de2b0812a7ca6f3c93000410007747b11944112dd27be0f6115a64fd9e5283ef3dc9438c809bbc3c8e390e36484d7e867a4f312791161aa1c67b9e5a25a5f6ed425a7afb90da2aa023cbea0643=ae5e8ad53f364a95b16d31ea72a8d72f');"/>키보드보안
                            - E2E 랜덤키 받기<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c00404e1c89e5077ddeaf6bac5a0634223d0436c03584faed13147fef144b05374f89000410004d82e1c0b44d88058804e169a7efcb3454d6f2768da9642a45c5541c422d73d8name=NONE_TEXT_1&Length=524288&type=text&E2E=off&ID=ae5e8ad53f364a95b16d31ea72a8d72f&IME=ON');"/>키보드보안
                            - 필드초기화<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c00405c55a4839481fef42724629cadbba87f73d77ff29722e18116c759606cc2490d000410007747b11944112dd27be0f6115a64fd9e5283ef3dc9438c809bbc3c8e390e3648fb36d9a446164376a2d04a99719325b43f2a6a3f29ba0a52dc61e4f8f2c7f5f7=ae5e8ad53f364a95b16d31ea72a8d72f=cardNo1');"/>키보드보안
                            - 입력양식 Focus In<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c0040f594e46dbcb1350f24752d790b419f0e0c56b16ae11f034be71ca9c83e156b23000410007747b11944112dd27be0f6115a64fd9e5283ef3dc9438c809bbc3c8e390e3648fc2f6d4f33877a7405281c2ca431c187f08fab4b1aac4ce39caba82161038e65=ae5e8ad53f364a95b16d31ea72a8d72f=cardNo1');"/>키보드보안
                            - 입력양식 Focus Out<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c00409b0a708f4b722011a9ad4d5b71963dee137cfdbf7e9ace61a7c2315c40e6c36b000410007747b11944112dd27be0f6115a64fd9e5283ef3dc9438c809bbc3c8e390e36487ee8f18f44f143cada23559afac6763898c9b936eb14741aa2cfe5621a1d467a=ae5e8ad53f364a95b16d31ea72a8d72f');"/>키보드보안
                            - 입력양식 Key Down<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('b9a50248aa0a1b94f16e0d68eac2db4517d3c5f28ba54fe74a06a035aace7edf14000000400dea9dfddc813c5142f6740ce714b90a0d330087f8e32bda227fd0999eaba71c004002a4dffeec389bfb6f7cf94425577f532f64a99c8b61eaf37d2b00da3443075a000410007747b11944112dd27be0f6115a64fd9e5283ef3dc9438c809bbc3c8e390e36483d7c82cb1e863ba8b205eb2afccc0c68ecdb979af3bc6a4f7cdc1395a6159218=ae5e8ad53f364a95b16d31ea72a8d72f');"/>키보드보안
                            - 로직종료<br/>
                        </td>
                        <td style="vertical-align:top;">
                            <!-- 단말정보수집 -->
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c100040804edf5e52e01189d217322e232dd3a4b2439a395967107913a555c42ce61dcd000410000014289742062918902734dcc3399885fb1896d11bc296e1700cf6631f48ab2a0024480ddfcd04a01a401a07929158fe476f189b93334609afa9b135ca9d0712e9a2d2a510bfc98d0ce4382a0221fead8609eba83529910f091954f90d37e37c5d4cf3f7d82d089d204c12764aa1c15aad1e01aa5b988e7aa9b8236176226c3247dea855d4fd7b07d642b3034b72bfbb906b701f0e356c42947ef133f551159b3bc3047fdfd9b892b5318b7fbeb476cfde535dfd2b933c3a0c4f20736aa41f84a0b4f03422a58351e64ef23bee99946f39d5785299b255c43e0b7b9970f7f5af1b97974e6d2f79f15444cca0b7529c05ff15fc44a58fd43f1ed5f0c');"/>단말정보수집
                            초기화/파라메터세팅<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c100040f32927fdd1272980fd57174b90496073f850e4f49c7374a7759a9bc1407ae9cf00041000001428974206291fb793848a3402effa970790747169a93a8a9274a47e31ffe6e43207c4335ae4204a400607ce8179faea985f136bbd37897dda00d2be8bfb8eb4d81203ee2d569c8eb5e1661471e6e857795b5f899143671400b293f4476db2bcd3119ebf88090739283b587a94dba460bcc7a4db6023c8c7100edfff261194dcf5c654cf5450f31ba30cc95a7b0d41463bf74bc7f80b146945b75c9ac195d19f9d26ac66cef26af25fd423a1a419fa7e3ba3faf272cd6081e41c3f56ac735972efcee25512bcb71b4b4bf74f283dbc8d05493640ffceb9f13cc82aa8ae2e765a7dab74417e8cca1861a0759bafc1e3f9e71edac51c60251d9d1fe53656b651eb1602ccc3d21af2ec06ada0746b1be459c8f02544e5c86e82a0cebea3864f510249937462d9829ea113e3395b30038d4fdfaf9bd54f9b35cc7f7a35548cce7f882b71a9030204d431964080882ed80ab9e11005f6acc2f2f1ffb48754b66c625975f342b885c777ca534b4c525e99cdb4acd4ce58d4487989b87f7682cf8dd1ef0c7441c5388c3b97ceaf307ab50ee4a840e641497e4a758f5537cc36d2fa610aae11bd9e470ec8d0e1560b96d65ab2f2bd70943715fef5a173fc827927cc1c583ffc2423e43e4e7b048be6ec1db400704bd7690aa09f227342463349fe6ec1c165ea02411f927adc831474bbbc3eaedab3f95539d8879f8772d56a6376220a72b1f5fd1f9b44b9dae805020706e4e0218a9c5ab0cd1a4f84b7560bf78b4560b8f2f61c46e8eb47f2ad5d2e882711732433aae82652357127c2775c2ad2f81ddcd4bc8b325a5287e1c53a38e8d9b884fa9f8ea1a8e7977ff536dace428c2b48a44c7cf231c798304f9e3113299ef37689dcbc0aed0df5aa15e0c35');"/>단말정보수집
                            초기화<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c1000408dd2eae4cf6ff02dcd3151dbeede30bab76034420151dc090346c8c14fb69a32000410000014289742062918e86219d05b8414ba9bf94a65f3296d194d827b5dee5fa507d85f39ef1bf887400450040531a2766b26c508f169c3bf4c1e93d3a30dd5bf36132a4090982d8eea0868b01Y');"/>단말정보수집
                            단위함수호출(인자값)<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c100040a357f74f9cb3751549f1d57617b001684a2a11fed1d77402bac38ac10033d42a000410000014289742062913a765a8e96a76a68cc737d10dbc03f88a51ad287b1b2a317f2144fae10c8bc3100440040296965710a53658aa932d42f1935ebe9ffac7f2d5a72cd7bb383343420869c6b');"/>단말정보수집
                            버전얻기<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c100040a7f505c83732362c485ffe98ddd82bb89e6085b2696c62d27a5ff380d0d040110004100000142897420629151478267c9320ec455afe7eae9f8919a18b370d5232d13e72ff51b7380b659d0004600407d1f471c57f9eacc13ff24e2e3aadf9c9d4635da468a87374faa24d0ff62fca401');"/>단말정보수집
                            PUT함수<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c10004048fbf14d0b929e6621390526c897b9306fa5926179d7bf8a859f3cfcd08da832000410000014289742062914fb5644b4f5d38c413b5421faf3eb9ad0a1cf3e1e6e81e414611e008cbdc3642020402000c808c740ec168bcaecbe19c073b33e1aa7c3d26f8d4d099b3ac479e39d19f2b4f6e2900e7f73f6b68afad53aa8880e64cac917f706f57c89287bd335508e56ebd52309de986bc87f903fa3202295316be4765985d20234f54395e599581233eff9a07785c3b5331f214fa54345683b602e7c4fbdd15925b6011bc13b1a7a9821fefa300c7e3c51e02dd145d7a33b8756b50e3d38aa4f7bf2ff7346e9da57e47de3e13f24f8f2b284102486ba059ebc4d6adb481c6de8725a4d2355b8afd7f40a1efbeeea8289289f230e0a441cd1fa02619d1df538dd1fde7f14c399c84b795306f59b45b14984f90c468a32e2d4ffdac827d54d91831859a701450b1d95c7b');"/>단말정보수집
                            GET함수<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('9aa13002613d9f52c17d5caa10d541ecb72f76a749fbc9c864418f451bcd6ae51400000040b14ec099e1215a87e14e6b6bd2b621a11e30323814d4c72093356a3c72643c1000403fe7e160f81aadfc5bdbad5cd6ba7b7db59a482942c24a87d19df1d2bf90bf3c00041000001428974206291f458bf9be39b5a605b7b8bc96f1a551d54b6adad20fee06c46b2aada96caf1e7');"/>단말정보수집
                            수집종료<br/>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
        <tr>
            <td colspan="2" class="divider"></td>
        </tr>
        <tr>
            <th>명령 응답(예시)</th>
            <td>
                <table>
                    <tr>
                        <td style="vertical-align:top;">
                            <!-- 로컬대몬 -->
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('59615036FA2C1A9EFC35D43EC6C77269');"/>공통 -
                            59615036FA2C1A9EFC35D43EC6C77269 : 성공/가상OS<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('B303AA8350126650FCE9111D899E21F0');"/>공통 -
                            B303AA8350126650FCE9111D899E21F0 : 실패/가상OS아님<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('FA48FAE45FDF6C6F29DD4766E50F5931');"/>공통 -
                            FA48FAE45FDF6C6F29DD4766E50F5931 : 모듈 업데이트 실행 중<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('201A9DFAC7ED61A876CA0B1D7AF18161');"/>공통 -
                            201A9DFAC7ED61A876CA0B1D7AF18161 : 개발자도구/디버깅도구 검출<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('14F1CF1F85E360D567D4A9C43B99C33B');"/>공통 -
                            14F1CF1F85E360D567D4A9C43B99C33B : 모듈재설치 필요<br/>
                            <input type="radio" name="sampleCommand"
                                   onclick="$('#commandCode').val('A0131152837EFEA26E0598577DE5E429');"/>공통 -
                            A0131152837EFEA26E0598577DE5E429 : 모듈버전검사 필요<br/>
                        </td>
                        <!-- 응답 FDS -->
                        <!-- 150eac3c48108b13e0bb91f8a6b89f6a93a819d5063cc552aff5a5e52d19da8d5f865e58761143dd82888e2406008eb26f341f3ec4cbe3846b1308851bd3ea44ee4d46618422c643391abe8f5b442fd270b1e5becc2719d1b6cad88f925c4944b3ca771be8101252f0bc9f10572016bd30a4b49ee10c7f1bf460b3943a8c67d71cf9a564d08cdbbe44f3f797ae74bcba7a7d5bae9a09f291f13acf8c305a11e9021081dd205edd583869809bb2642ed23129d401af11da0bd46618aaab5b46bbcdeb2285529d2acdcf760bf08dfffb341f19c1a26f1cca664c024d56f6996a58c6c965f3f1181e9a252ea2f095b9b951d56bcdbc2df37c5c0a3e1d855848358f -->
                        <!-- 04fffc49b0bae705e8d164e735de3f9b58dffdf50d3f9e923a8e152af9c446de855e32ad39beea83ab21629ae2f127a6cbd00d5b0400a2c9bcef44b7b3699ac5d3ec8a9329944c2fac3d042d437da957c115c2e0668b7b39cd669578b5c7025b2abf3eff9177db78c699bc1bec783f0c7086f21206e1731adacae1664f771d9b2e98824c59c642eab95de1540cc8e4c106c7c1a1c7d027d6a0448d60c2de23425632170eb8bee37c79d1e6537e40abd52ea808fb6ceefc8169294336e8988e52cec9b7c16d4070dfe2239301b0933cf34ce9a9378e8b29182c6bd39f530badcb5b671ca843b239f1119487b9997b14d4f4f5a8eb2131fc66a71fc2ba8fe4d429f9b710c098d71d970a5d696c168bb51e822295dd085c190ea518b7b486b94a9bc60c6a1241d37f78f1454a84defb2c04097032aa0e538880c0c98367958acd3beddcda8a3045d5411a6d814b61a638892678151605c2a3f09976f4c521567a8ca3b14b909a5941bcbf6c0099b7659c68d8ea3903c6eb35e5dc7c9d0290359c81dca484d2ef58b1523b9f81bb9f0082db290ec358aa08de4aa6ec0f7404701f9be19d70976d537d048c8550161caedcfdb5b0d336931b67019e703781a75244fc4e8af64ab86b62a34e96304c58f50cfabd86af8f0a3e81eb7a05e4a5b37fa44b4e4bff635e8766df32b6c78c91e0a821ebe5f89bf66e1c9a7d7dc044159dfe39cb3f38f8e8d51e40b988acaf2b3bb58f4a3c5e9859be58b02dea35174890488c87acb5782c0721f4655e7aacbd1662cafbddd2c56453cd61bd82ff508b13cac98f35727901c0f38f388d33d280f27896b604203c10b485414c29e0778ca2e730133abfe9df24609116a268ab3f6e6949a4f3238fe69d381950fdfe0a61e53302c8c774c1e9865a8ba538df2e80285686171d503b9c685f581c8c2cd887c2cacfe6e21a3a748aaeba354bf4f6d04ef195466d27da15330af6d2611e6b84283eff2dedf291fe39f54c66dbc83a5cdb7f40f9a4ce4b1784b27e09d5842e31c69d012438272c40c26d296f7a631c5ad6f53727cb501375284e3839cf90d8fe5233477776d8fb3eb90fef8932e7c786658e66506b73e23d03196b68f30119644a30394fb924594e125455b12180b8fe631e08d30a3a89c3641127b6f05fe599e08b4833a175bb2a34bc5146df2ae35b92b174757deedfa5820478c312b4eb835dfff614570617f87341353513b786132e51adc3e6e9eeef3afda79e7f3ac8e35f6d72b24c3f2ad8fd7e1dbeeedc5d1322873aa3088853fce96c3e9b0b912b16032e718e89e6c08f943e1762ab838dcaee1b51227758ef02c342e21884522ca650cce46196aa701933fdddb8536da28f001a296afa0cd2786ad3c9ea924a794bb9786d4ac575ad933d51e0c67faeaaeaaff3edb3181d07a736010eeae3dcb344a0d710dab511e221ebc353d4914dbafeaf480e2a3c6af3b57367700abe57e57867081675190cf2aa9b0ead27efff70e632ec402fa8ed25ed60c1e69265384cfe9b2c11853679f5c746c58074a0223b56b6a786c4ece577bb3132b1772ddacaece9b1354e2520c48badacf9a29bb37e370ad137532fa5bd5c2600876ac3b790c49ee093dfae93cb41bfe011bfa98545e28c3da961aaf03d01860c94527fce9fc1d63c9cb90e051b7583a37b6743f67d5818bdafe1a227eab8790f5ba44a4babc256d430a0832b85c1bf4d8a080169b8067c44243bc228731b7c7b79d69f1ddeedf066e1490536a0cd871a73cef48c3590c99791d0c36721e6142cf24ca0e9096ff09ab35a7958430c2bbe761d20cbcab993508cd85d4b1d740d7d68d6abc1dfe907d04657eb4aa8cfc4283a03e407e52ba5805bf1998917beb8ee120c1e5d8bad23770ea5031907d8930f81bdc189c9edb391dc4a613651e6dff106818b9609b993d0db0aab46059579e2c16dc12e287f9892f2d96cbc532eaf6f1aed88d505ed8bca2f6c045a7e8e927701b5a44027faef6b40f15002e77f91127b19a710840756d3d4f9470b2100ba6ed192379731c1d3327b4cd413dc174093e2f8713cdf2ecd18a0f063703920c3fa4afbdce73611d79c9f403bd1ab8d1f234b2b35b011f0de0cd29ff2067d9eb778d5c3617d45053e854d37a07c538469454a98f05cc3f6710e7f050b4a09b57165237671d4a18d6a5126b81527c8d518318b45496b9f82ed6527e9ee2d288c57fe2e2e7c143778143498 -->
                        <!-- 2471561b369324a92d99825a890e7e82ca0d7f76d063df7c18d8624ca907fdf2c862307bc2b5037828d51c893ca5145359e24d4dffad05a02af0f2d705c2ee62d8cf0622ac538070cfa021ea4b253bc071cf8ff9d4792a3c5dbe9f57be9c6043c3f0e0e88a612f6c8a0a33eabdc78893b994197f6edd56d5128c0a6d9feebab5ce771cbf89520007cf084e5a4b1bf71bb5fcf39aad911d79d35a81d97edbbf7c5abf9b779a347a388207cd8d20ab3bc090f473ce9cdb19da6ee53b7a9c04eb282228c7b886fef0def825271ee2d0ea7924bd8d8c67cd1e845d0c0370ca36dbbfe9eaceefc87f9259c1c17369bd31cce1ae2af0d478f035979e23b97382aaa7346d105dfbba5c4346d0b3978cfcfbaa8ef41e3aa3552d7c17a5562a11da6074407f69c0b4109eec05e6483bf52c221e4e1a193209396cc63f5c73e86742fde60477c23965787a3fdaf4efe67db96d3905618b832a32c1ff8cc17e4621b0b815d85c2f027c96538de6b3a0a572b80313a81472719209da2e678e4f37cfa0d23e4475e673a8e8ca71623c505cb9391eae4a21c53f22f74c24429cabd2af984f00a87c1dc9410972e0c7f925446a67c8f65a1184d9e2842eb06cffb43911069e769a158d378f980cf9b7b6e05cf8b2293050da4ce8ee4ef10f4739161a7ddfbc51e8730d015f09051c112d0732e66fdb3620d86b208254bb2deea1fb5919922a524af20f8d79d1ce600b3e6d5cffc25a9bdbec8226136325641a0a96e4727f6f9617b70bc72e2b48aa1b08d071a146a8ff5b51b9ea007530699dd4263057d117530863b78bfa746087e411d6467e719be0772687f1f7f7f17dce5696e07a3646f27147f7dfdedfbff124f97936ed07b2c6554ce4afc91757ba2e17971646d4cce69df28dfac05e3a0cf7b0eaabafdc1abbbbf3d3e812cf4eb84a9b2330be8c20f2b107ac4a2ef30fd25fb6f43ecc4c274b2ead18014a1334c2d72de4347b9d55108f21b1d4818eaa8853d1171ce7eb3e77e742e2f1fb08296ef6578e61cded70dcce32ffa8dba7c275ff9cf679c1d526090ea0c63e2cb71b22269f64035bcc02ead3dcb4d9f4b4924bb6cd0999b3d9c6e81a318cb9a1bb4d9e350dc6317a24a6e531fed7cc148ed864aeff838392ce15be18 -->
                        <!-- d563ef884c86ec46865adb0d53b738ce83e62a6a13811fca69d296a544b434ef16f157725167bd16db72f95ad55283c2164b310b98d1206815d7aed4408a59eb2d9243daf28ff807684311cd2bf1ef47 -->

                        <!-- 응답 키보드보안 -->
                        <!-- CLIENTADDRESS&&ae5e8ad53f364a95b16d31ea72a8d72f&^&70f7524d3a62016e599b985f67556d2d6cd771f19b81c2073d875f46fd1d407643acd4edfb5d6d760aaaeb396bb14aaf00a8b8c97647adf8df8e0daa6adf4838cee3a7a75a4aeb2fa4c6611a439ab21a7e6428d15580dde158a785c464288afad0e8a5b8354494079e44d292a139d361aa6ea30917a80ce3fb39932ccfbd1c983802e14d9c245dcbf1b9c17d5f31ae02af1835415ea60eef62a0b4d6691c7aa4433147b223b2a31e319f38ac120a88363be452abe5f64e7da60266937266aed0c0d588d81a568c6ac0133a546abf3cd865b9ed3cfe5fa627880ee4fb675a9dca638f5e3820fb9d0bac3bac672272e84a1aa372d0e4b75240502283953ef6f9fa -->
                        <!-- ENCREPLACETABLE&&ae5e8ad53f364a95b16d31ea72a8d72f&^&3ebd5631dbc4c4e0a151becb068f541d8361040ff2fd8f35a357f037709bc03a3099bb9fd03f6ca22c850b094d257126f762d0bb5bb9b3c60e9e8836a73c8824c5a2a2857fefbbebcfce94b07053bfc4c88c7e3d0255b339833e1d9675ab0f2f28078eb5b73cb5bd3bd72c3276a27ca33f6850db7505675b066f3d5879692ffa11de2ed7577a9c787b33075d8180133c99213ad527ac0dcf7b932d0de4e3fc3f9990c4d56c9cdc172f1a54d3ce0cbd4d -->
                        <!-- KEY&&f81989fac545b02f9311f5da63dde079&& -->
                        <!-- KEY&&f81989fac545b02f9311f5da63dde079&&7e986747b31a58c6ade171b3ffa570ec4d1cb4735dd998c55bd13638d5779e07 -->
                        <!-- ENCREPLACETABLE&&ae5e8ad53f364a95b16d31ea72a8d72f&^&3ebd5631dbc4c4e0a151becb068f541d8361040ff2fd8f35a357f037709bc03a3099bb9fd03f6ca22c850b094d257126f762d0bb5bb9b3c60e9e8836a73c8824c5a2a2857fefbbebcfce94b07053bfc4c88c7e3d0255b339833e1d9675ab0f2f28078eb5b73cb5bd3bd72c3276a27ca33f6850db7505675b066f3d5879692ffa11de2ed7577a9c787b33075d8180133c99213ad527ac0dcf7b932d0de4e3fc3f9990c4d56c9cdc172f1a54d3ce0cbd4d -->
                        <!-- 3ebd5631dbc4c4e0a151becb068f541d8361040ff2fd8f35a357f037709bc03a3099bb9fd03f6ca22c850b094d257126f762d0bb5bb9b3c60e9e8836a73c8824c5a2a2857fefbbebcfce94b07053bfc4c88c7e3d0255b339833e1d9675ab0f2f28078eb5b73cb5bd3bd72c3276a27ca33f6850db7505675b066f3d5879692ffa11de2ed7577a9c787b33075d8180133c99213ad527ac0dcf7b932d0de4e3fc3f9990c4d56c9cdc172f1a54d3ce0cbd4d -->
                        <td style="vertical-align:top;">
                            <!-- 키보드보안 -->
                        </td>
                        <td style="vertical-align:top;">
                            <!-- 단말정보수집 -->
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
        <tr>
            <td colspan="2" class="divider"></td>
        </tr>
        <tr>
            <td colspan="2" style="text-align:center;">
                <button name="parseCommand"
                        onclick="if($('#commandCode').val().trim() === '') {alert('명령어 구문을 입력하십시오.'); return false; }">
                    명령어 분석
                </button>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <iframe id="nos-command-iframe" name="nos-command-iframe" src="about:blank;" class="box"
                        style="border:0px;width:100%;height:450px;"></iframe>
            </td>
        </tr>
    </table>
</form>
<%
    }        // end of mode "command"

    if ("parseCommand".equals(mode)) {
        String code = param(request, "code").trim();
        String commandMode = param(request, "commandMode", "req");

        if ("59615036FA2C1A9EFC35D43EC6C77269".equals(code)
                || "B303AA8350126650FCE9111D899E21F0".equals(code)
                || "FA48FAE45FDF6C6F29DD4766E50F5931".equals(code)
                || "201A9DFAC7ED61A876CA0B1D7AF18161".equals(code)
                || "14F1CF1F85E360D567D4A9C43B99C33B".equals(code)
                || "A0131152837EFEA26E0598577DE5E429".equals(code)) {
            String codeName = "";
            if ("59615036FA2C1A9EFC35D43EC6C77269".equals(code)) {
                codeName = "성공/가상OS";
            } else if ("B303AA8350126650FCE9111D899E21F0".equals(code)) {
                codeName = "실패/가상OS아님";
            } else if ("FA48FAE45FDF6C6F29DD4766E50F5931".equals(code)) {
                codeName = "모듈 업데이트 실행 중";
            } else if ("201A9DFAC7ED61A876CA0B1D7AF18161".equals(code)) {
                codeName = "개발자도구/디버깅도구 검출";
            } else if ("14F1CF1F85E360D567D4A9C43B99C33B".equals(code)) {
                codeName = "모듈재설치 필요";
            } else if ("A0131152837EFEA26E0598577DE5E429".equals(code)) {
                codeName = "모듈버전검사 필요";
            }
%>
<table style="width:100%;">
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <tr>
        <th>값</th>
        <td><%= code %>
        </td>
    </tr>
    <tr>
        <th>설명</th>
        <td><%= codeName %>
        </td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
</table>
<%
} else {
/*
		var headerCount = headers.length;
		var command = [];
		command.push(pcode);
		if(npCommon.isBlank(sync)) {
			command.push("1");
		} else {
			command.push(sync);
		}
		command.push(headerCount);
		for(var idx = 0 ; idx < headerCount ; idx++) {
			command.push(npCommon.makeLength(headers[idx]));
			command.push(headers[idx]);
		}

		return command;
*/

    String remain = code;
    String product = null;
    String productCode = null;
    String productName = null;
    if (remain.length() >= 64) {
        product = remain.substring(0, 64);
        productCode = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NOS, product, true);        // ARIA
        remain = remain.substring(64);
    }

    String sync = null;
    String syncName = "";
    if (remain.length() >= 1) {
        sync = remain.substring(0, 1);
        remain = remain.substring(1);
        if ("1".equals(sync)) {
            syncName = "동기";
        } else {
            syncName = "비동기";
        }
    }

    String headerLength = null;
    if (remain.length() >= 1) {
        headerLength = remain.substring(0, 1);
        remain = remain.substring(1);
    }

    int headerCount = 0;
    int[] headerSize = null;
    String[] headerContent = null;
    String[] decryptContent = null;
    try {
        headerCount = Integer.parseInt(headerLength);
    } catch (Exception e) {
    }
    if (headerCount > 0) {
        headerSize = new int[headerCount];
        headerContent = new String[headerCount];
        decryptContent = new String[headerCount];

        for (int idx = 0; idx < headerCount; idx++) {
            if (remain.length() >= 4) {
                int len = Integer.parseInt(remain.substring(0, 4), 16);
                headerSize[idx] = len;
                headerContent[idx] = remain.substring(4, 4 + len);
                decryptContent[idx] = "";

                if (headerCount == 4 && idx == 1 && headerContent[idx].length() >= 64) {            // ARIA 256, Customer Id
                    try {
                        decryptContent[idx] = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NOS, headerContent[idx], true);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
                if (headerCount == 4 && idx == 2 && headerContent[idx].length() >= 64) {            // AES 128
                    try {
                        String key = headerContent[idx].substring(0, 32);
                        String value = headerContent[idx].substring(32);
                        byte[] decryptBytes = RijndaelWrapper.decrypt(Cipher.ECB, StringUtil.hexDecode(key), null, StringUtil.hexDecode(value));
                        decryptContent[idx] = new String(decryptBytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println("[" + remain.substring(0, 4) + "][" + len + "][" + remain.substring(4, 4 + len) + "]");

                remain = remain.substring(4 + len);
            }
        }
    }


    if ("0001".equals(productCode)) {
        productName = "로컬대몬";
    } else if ("0002".equals(productCode)) {
        productName = "개인방화벽";
    } else if ("0003".equals(productCode)) {
        productName = "키보드보안";
    } else if ("0004".equals(productCode)) {
        productName = "단말정보수집";
    }
%>
<table style="width:100%;">
    <tr>
        <td colspan="4" class="divider"></td>
    </tr>
    <tr>
        <th rowspan="<%= 3 + headerCount %>">헤더</th>
        <th>제품</th>
        <td><%= product %><br/>=&gt; <%= productName %>(<%= productCode %>)</td>
    </tr>
    <tr>
        <th>모드</th>
        <td><%= sync %>(<%= syncName %>)</td>
    </tr>
    <tr>
        <th>헤더 개수</th>
        <td><%= headerCount %>
        </td>
    </tr>
    <%
        for (int idx = 0; idx < headerCount; idx++) {
            if (headerCount == 4) {
                String headerName = "";
                if (idx == 0) {
                    headerName = "버전호환";
                }
                if (idx == 1) {
                    headerName = "고객코드";
                }
                if (idx == 2) {
                    headerName = "도메인";
                }
                if (idx == 3) {
                    headerName = "명령어버전";
                }
    %>
    <tr>
        <th>헤더(<%= headerName %>)</th>
        <td><%= headerSize[idx] %>
            / <%= headerContent[idx] %><%= (decryptContent[idx] != null && !"".equals(decryptContent[idx])) ? "<br />=&gt; " + decryptContent[idx] : "" %>
        </td>
    </tr>
    <%
            }
        }

        String command = "";
        String commandCode = "";
        String commandName = "";
        String parameter = "";
        String uuid = "";
        boolean isMultiOs = false;
        if (remain.length() >= 32 && remain.startsWith("47494638396101000100820031FFFFFF")) {
            command = remain.substring(0, 32);
            remain = remain.substring(32);
            commandName = "이미지 포트 찾기";
            parameter = remain;
        } else if ("0001".equals(productCode)) {
            try {
                if (remain.length() >= 64) {
                    command = remain.substring(0, 64);
                    remain = remain.substring(64);
                    parameter = remain;
                }

                commandCode = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NOS, command, true);
                if ("1000".equals(commandCode)) {        // 대몬 연결 확인
                    commandName = "대몬연결/핸드쉐이크";
                } else if ("1002".equals(commandCode)) {        // 가상머신 동작여부 확인
                    commandName = "가상머신 동작여부 확인";
                } else if ("1003".equals(commandCode)) {        // 서비스 동작여부 확인
                    commandName = "서비스 동작여부 확인";
                } else if ("1004".equals(commandCode)) {        // 업데이트 버전 확인
                    commandName = "업데이트 버전 확인";
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } else if ("0002".equals(productCode)) {
            try {
                if (remain.length() >= 64) {
                    command = remain.substring(0, 64);
                    remain = remain.substring(64);
                    parameter = remain;
                }

                commandCode = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPN_WINDOWS, command, true);
                commandName = "개인방화벽(윈도우) ";
                if ("0002".equals(commandCode)) {
                    commandName += "연결유지";
                } else if ("0003".equals(commandCode)) {
                    commandName += "상태";
                } else if ("0004".equals(commandCode)) {
                    commandName += "버전";
                } else if ("0005".equals(commandCode)) {
                    commandName += "시작";
                } else if ("0006".equals(commandCode)) {
                    commandName += "종료";
                }
            } catch (Exception e) {
                //e.printStackTrace();
                parameter = command + remain;        // 윈도우에서 자른값 다시 붙이기
                isMultiOs = true;
                command = "";
                remain = "";
            }
        } else if ("0003".equals(productCode)) {
            if (remain.length() >= 64) {
                command = remain.substring(0, 64);
                remain = remain.substring(64);
                parameter = remain;
            }
            try {
                commandCode = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPK, command, true);
                if ("1001".equals(commandCode)) {
                    commandName += "npkEvent";
                } else if ("2001".equals(commandCode)) {
                    commandName += "npkParam";
                } else if ("2002".equals(commandCode)) {
                    commandName += "npkCertInfo";
                } else if ("2003".equals(commandCode)) {
                    commandName += "npkCertData";
                } else if ("3001".equals(commandCode)) {
                    commandName += "npkFeild";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("0004".equals(productCode)) {
            if (remain.length() >= 15) {
                uuid = remain.substring(0, 15);
                remain = remain.substring(15);
            }

            if (remain.length() >= 64) {
                command = remain.substring(0, 64);
                remain = remain.substring(64);
                parameter = remain;
            }
            try {
                commandCode = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPF, command, true);
                if ("0001".equals(commandCode)) {
                    commandName += "초기화/파라메터세팅";
                } else if ("0002".equals(commandCode)) {
                    commandName += "초기화";
                } else if ("0003".equals(commandCode)) {
                    commandName += "버전얻기";
                } else if ("0004".equals(commandCode)) {
                    commandName += "단위함수호출/인자값";
                } else if ("0005".equals(commandCode)) {
                    commandName += "멀티";
                } else if ("0006".equals(commandCode)) {
                    commandName += "PUT 함수";
                } else if ("0007".equals(commandCode)) {
                    commandName += "GET 함수";
                } else if ("0008".equals(commandCode)) {
                    commandName += "수집종료";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    %>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <tr>
        <th rowspan="<%= 3 + headerCount %>">본문</th>
        <th>명령</th>
        <td><%= command %><%= (commandCode != null && !"".equals(commandCode)) ? "<br />=&gt; " + commandName + "(" + commandCode + ")" : "" %>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <%

        if (parameter != null && !"".equals(parameter)) {
            String parameterContent = "";
            String parameterPlain = "";
            String parameterName = "";

            if ("0001".equals(productCode)) {
                if (parameter.length() >= 0 && ("1000".equals(commandCode))) {
                    int totalLength = Integer.parseInt(parameter.substring(0, 4), 16);
                    remain = remain.substring(4);

                    int bodySize = Integer.parseInt(remain.substring(0, 1));
                    remain = remain.substring(1);

    %>
    <tr>
        <th>파라메터</th>
        <td>
            전체길이 : <%= totalLength %><br/>
            본문개수 : <%= bodySize %><br/>
            본문내용 : <%= remain %><br/>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <%
        try {
            for (int idx = 0; idx < bodySize; idx++) {
                int size = Integer.parseInt(remain.substring(0, 4), 16);
                if (idx == 0) {
                    String key = remain.substring(4, 4 + 32);
                    String value = remain.substring(4 + 32, 4 + size);

                    byte[] decryptBytes = RijndaelWrapper.decrypt(Cipher.ECB, StringUtil.hexDecode(key), null, StringUtil.hexDecode(value));
                    //plainBody[idx] = ;
    %>
    <tr>
        <th>User Agent</th>
        <td>
            길이 : <%= size %><br/>
            벡터 : <%= key %><br/>
            내용 : <%= value %><br/>
            평문 : <%= new String(decryptBytes) %><br/>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <%
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } else if ("0002".equals(productCode)) {
        int parameterLength = 0;
        if (parameter.length() >= 0 && ("0002".equals(commandCode) || "0005".equals(commandCode) || "0006".equals(commandCode))) {
            parameterLength = Integer.parseInt(parameter.substring(0, 4), 16);
            parameterContent = parameter.substring(4, 4 + parameterLength);
            parameterPlain = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPN_WINDOWS, parameterContent, true);
        } else if (isMultiOs) {
            parameterLength = Integer.parseInt(parameter.substring(0, 4), 16);
            parameterContent = parameter.substring(4, 4 + parameterLength);
            parameterPlain = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPN_MULTI, parameterContent, true);

            parameterName = "개인방화벽(멀티) ";
            if ("0005".equals(parameterPlain)) {
                parameterName += "시작";
            } else if (parameterPlain.startsWith("updatepolicyurl")) {
                parameterName += "연결유지";
            }
        }
    %>
    <tr>
        <th>파라메터</th>
        <td>
            <%= parameter %>
            <%= (parameterName != null && !"".equals(parameterName)) ? "<br />=&gt; 이름 : " + parameterName : "" %>
            <%= (parameterLength > 0) ? "<br />=&gt; 길이 : " + parameterLength : "" %>
            <%= (parameterContent != null && !"".equals(parameterContent)) ? "<br />=&gt; 내용 : " + parameterContent : "" %>
            <%= (parameterPlain != null && !"".equals(parameterPlain)) ? "<br />=&gt; 평문 : " + parameterPlain : "" %>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <%
    } else if ("0003".equals(productCode)) {
        String extra = "";
        if ("1001".equals(commandCode)) {
            int positionEqual = parameter.indexOf("=");
            if (positionEqual >= 0) {
                extra = parameter.substring(positionEqual + 1);
                parameter = parameter.substring(0, positionEqual);
            }
            parameterPlain = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPK, parameter, true);
            if ("Initialize".equals(parameterPlain)) {
                parameterName = "키보드보안 초기화";
            } else if ("UnInitialize".equals(parameterPlain)) {
                parameterName = "키보드보안 로직종료";
            } else {

            }
        } else if ("2001".equals(commandCode)) {
            parameterName = "";
        } else if ("3001".equals(commandCode)) {
            parameterName += "";
        }
    %>
    <tr>
        <th>파라메터</th>
        <td>
            <%= parameter %>
            <%= (parameterName != null && !"".equals(parameterName)) ? "<br />=&gt; 이름 : " + parameterName : "" %>
            <%= (parameterContent != null && !"".equals(parameterContent)) ? "<br />=&gt; 내용 : " + parameterContent : "" %>
            <%= (parameterPlain != null && !"".equals(parameterPlain)) ? "<br />=&gt; 평문 : " + parameterPlain : "" %>
            <%= (extra != null && !"".equals(extra)) ? "<br />=&gt; 부가정보 : " + extra : "" %>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <%
    } else if ("0004".equals(productCode)) {
        int totalLength = Integer.parseInt(parameter.substring(0, 4), 16);
        int parameterLength = Integer.parseInt(parameter.substring(4, 8), 16);
        String additional = "";
        String additionalName = "";
        try {
            parameterContent = parameter.substring(8, 8 + parameterLength);
            parameterPlain = EncryptUtil.decrypt(EncryptUtil.KEY_TYPE.KEY_NPF, parameterContent, true);
            additional = parameter.substring(8 + parameterLength);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ("0001".equals(commandCode)) {
            additionalName += "";
        } else if ("0002".equals(commandCode)) {
            additionalName += "서버랜덤키값";
        } else if ("0003".equals(commandCode)) {
            additionalName += "";
        } else if ("0004".equals(commandCode) && !"".equals(additional)) {
            additionalName += "인자값";
        } else if ("0005".equals(commandCode)) {
            additionalName += "";
        } else if ("0006".equals(commandCode)) {
            additionalName += "";
        } else if ("0007".equals(commandCode)) {
            additionalName += "";
        } else if ("0008".equals(commandCode)) {
            additionalName += "";
        }
    %>
    <tr>
        <th>파라메터</th>
        <td>
            <%= parameter %>
            <%= (parameterName != null && !"".equals(parameterName)) ? "<br />=&gt; 이름 : " + parameterName : "" %>
            <%= (parameterLength > 0) ? "<br />=&gt; 길이 : " + parameterLength : "" %>
            <%= (parameterContent != null && !"".equals(parameterContent)) ? "<br />=&gt; 내용 : " + parameterContent : "" %>
            <%= (parameterPlain != null && !"".equals(parameterPlain)) ? "<br />=&gt; 평문 : " + parameterPlain : "" %>
            <%= (additionalName != null && !"".equals(additionalName)) ? "<br />=&gt; 인자구분 : " + additionalName : "" %>
            <%= (additional != null && !"".equals(additional)) ? "<br />=&gt; 인자값 : " + additional : "" %>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="divider"></td>
    </tr>
    <%
            }
        }
    %>
</table>
<%
        }

    }        // end of mode "parseCommand"


    if ("supdateState".equals(mode)) {
%>
<table style="width:100%;">
    <tr>
        <th style="text-align:left;font-size:14pt;" colspan="2">Supdate 서버 상태확인</th>
    </tr>
    <tr>
        <td colspan="2">
            https://supdate.nprotect.net/nprotect/nos_service/windows3/stt/npcstt.npx.nz<br/>
            => https://아이피/nprotect/nos_service/windows3/stt/npcstt.npx.nz<br/>
            => 열려진 창에서 도메인 경고를 무시하고 강제 접속해야 합니다.
        </td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <!--
	<tr>
		<td colspan="2">
			<iframe id="nos-supdate-iframe" name="nos-supdate-iframe" src="about:blank;" class="box" style="border:0px;width:100%;height:250px;"></iframe>
		</td>
	</tr>
	<tr><td colspan="2" class="divider"></td></tr>
 -->
    <tr>
        <td>110.4.113.55</td>
        <td><a href="https://110.4.113.55/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.56</td>
        <td><a href="https://110.4.113.56/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.57</td>
        <td><a href="https://110.4.113.57/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.58</td>
        <td><a href="https://110.4.113.58/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.59</td>
        <td><a href="https://110.4.113.59/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <tr>
        <td>110.4.113.60</td>
        <td><a href="https://110.4.113.60/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.61</td>
        <td><a href="https://110.4.113.61/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.62</td>
        <td><a href="https://110.4.113.62/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.63</td>
        <td><a href="https://110.4.113.63/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.64</td>
        <td><a href="https://110.4.113.64/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <tr>
        <td>110.4.113.66</td>
        <td><a href="https://110.4.113.66/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.113.67</td>
        <td><a href="https://110.4.113.67/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.3</td>
        <td><a href="https://110.4.99.3/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.4</td>
        <td><a href="https://110.4.99.4/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.5</td>
        <td><a href="https://110.4.99.5/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <tr>
        <td>110.4.99.6</td>
        <td><a href="https://110.4.99.6/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.7</td>
        <td><a href="https://110.4.99.7/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.8</td>
        <td><a href="https://110.4.99.8/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.9</td>
        <td><a href="https://110.4.99.9/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.10</td>
        <td><a href="https://110.4.99.10/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <tr>
        <td>110.4.99.11</td>
        <td><a href="https://110.4.99.11/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.12</td>
        <td><a href="https://110.4.99.12/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.13</td>
        <td><a href="https://110.4.99.13/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.14</td>
        <td><a href="https://110.4.99.14/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.15</td>
        <td><a href="https://110.4.99.15/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
    <tr>
        <td>110.4.99.16</td>
        <td><a href="https://110.4.99.16/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td>110.4.99.18</td>
        <td><a href="https://110.4.99.18/nprotect/nos_service/windows3/stt/npcstt.npx.nz" target="_blank">연결확인</a></td>
    </tr>
    <tr>
        <td colspan="2" class="divider"></td>
    </tr>
</table>
<%
    }        // end of mode "supdateState"

    if ("login".equals(mode)) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String date = format.format(new Date());
%>
<form action="<%= currentRequestURI %>" method="post">
    <input type="hidden" name="m" value="check"/>
    <div id="modal-replace-mapping" class="modal-dialog" style="display:block;">
        <div class="content" style="width:440px;height:200px;margin-left: -220px;margin-top: -100px;">
            <div class="title">
                <h3 style="font-size:14pt;">nProtect Online Security V1.0, Authentication</h3>
            </div>
            <div class="body" style="padding-left:5px;padding-right:10px;">
                <table style="width:100%;height:100%">
                    <tr>
                        <td colspan="2" class="divider"></td>
                    </tr>
                    <tr>
                        <th style="font-size:12pt;">기준시각</th>
                        <th style="text-align:left;font-size:12pt;"><%= date %>
                        </th>
                    </tr>
                    <tr>
                        <th style="font-size:12pt;">접근번호</th>
                        <td style="text-align:left;font-size:12pt;">
                            <input type="password" id="nos-tool-password" name="pwd" size="12" maxlength="8"
                                   class="input-text" value="" style="font-size:12pt;width:160px;"/> 8자리
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <button style="font-size:14pt;">로그인</button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" style="text-align:left;">
                            <ol style="margin-top:3px;">
                                <li>인가된 사용자 외에는 본 페이지에 접근할 수 없습니다.</li>
                                <li>불법적으로 사용하실 경우 민형사상의 책임이 따를 수 있음을 알려드립니다.</li>
                            </ol>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</form>
<%
    }        // end of mode "login"

%>


</body>
</html>
<%
    }
%>