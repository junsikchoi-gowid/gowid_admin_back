<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%>
<%@page import="java.text.SimpleDateFormat"
%>
<%@page import="java.util.Date"
%>
<%
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    String now = format.format(new Date());

    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
    response.setContentType("text/html; charset=utf-8");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>nProtect Online Security v1.0.0</title>
    <style>
        body, td, th {
            font-size: 10pt
        }

        input, textarea {
            font-size: 9pt;
        }
    </style>

    <script type="text/javascript" src="/static/pluginfree/js/jquery-1.11.0.min.js"></script>
    <!-- <script type="text/javascript" src="/pluginfree/jsp/nppfs.script.jsp?i=%2Fpluginfree%2Fjs%2Fnppfs-1.0.0.js"></script> -->
    <script type="text/javascript" src="/pluginfree/jsp/nppfs.script.jsp"></script>
    <script type="text/javascript" src="/static/pluginfree/js/nppfs-1.0.0.js?dummy=<%= now %>"></script>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            jQuery("#userAgent").text(navigator.userAgent);
            /*
            npPfsStartup(document.form1, false, true, true, "enc", "on");
            1. form 양식 : 기본값 DOM Document 상의 첫번째 form
            2. 개인방화벽 사용여부 : 기본값 false
            3. 키보드보안 사용여부 : 기본값 false
            4. 단말정보수집 사용여부 : 기본값 false
            5. 키보드보안 E2E 필드 설정 속성명 : 기본값 "enc"
            6. 키보드보안 E2E 필드 설정 속성값: 기본값 "on"
            부가적인 설정은(예, 설치확인 등) /pluginfree/js/nppfs-1.0.0.js를 수정하여 설정하십시오.
        */
            /*
                npPfsStartup(
                    document.form2
                    , true
                    , true
                    , true
                    , "npkencrypt"
                    , "on"
                );
            */
            //URL Rewriting : Cookie가 허용되지 않는 브라우저에서는 다음 구문을 추가하여 쿠키없이도 동작이 가능하도록 처리
            uV.dV.Gf = "<%= response.encodeURL("/pluginfree/jsp/nppfs.key.jsp") %>";    // 키발급 경로
            uV.dV.zf = "<%= response.encodeURL("/pluginfree/jsp/nppfs.remove.jsp") %>"; // 키삭제 경로
            uV.dV.zo = "<%= response.encodeURL("/pluginfree/jsp/nppfs.keypad.jsp") %>";  // 마우스입력기 페이지
            uV.dV.eP = "<%= response.encodeURL("/pluginfree/jsp/nppfs.ready.jsp") %>";  // 초기화상태 확인경로
            uV.dV.Fz = "<%= response.encodeURL("/pluginfree/jsp/nppfs.install.jsp") %>"; // 설치안내 페이지

            npPfsStartup(document.form1, false, false, false, false, "npkencrypt", "on");
        });


    </script>

</head>
<!-- <body oncontextmenu="return false" onselectstart="return false" ondragstart="return false"> -->
<body>
<table>
    <tr>
        <th style="text-align:left;font-size:14pt;">접속정보</th>
    </tr>
    <tr>
        <td>
            <span id="userAgent"></span>
        </td>
    </tr>
</table>


<script src="flash/swfobject.js"></script>
<script>
    var flashvars = {};
    var params = {
        menu: "false",
        scale: "noScale",
        allowFullscreen: "true",
        allowScriptAccess: "sameDomain",
        bgcolor: "",
        wmode: "direct" // can cause issues with FP settings & webcam
    };

    var attributes = {};
    attributes.id = "UserActionScript";
    attributes.name = "UserActionScript";
    attributes.align = "middle";
    swfobject.addDomLoadEvent(function () {
        swfobject.embedSWF(
            "./flash/UserActionScript.swf", "flashContent",
            "600px", "300px",
            "10.0.0", "./flash/playerProductInstall.swf",
            flashvars, params, attributes);
    });

</script>

<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">

    <table>
        <tr>
            <th style="text-align:left;font-size:14pt;">Flash SDK 테스트</th>
        </tr>
        <tr>
            <td>
                <div id="flashContent">
                    <h3>noskcsdk_test_flex4</h3>
                    <p><a href="http://www.adobe.com/go/getflashplayer">Get Adobe Flash player</a></p>
                </div>
            </td>
        </tr>
    </table>
</div>
<%--
<object
	type="application/x-shockwave-flash"
	id="UserActionScript"
	name="UserActionScript"
	data="./flash/UserActionScript.swf"
	width="600px" height="120px">
	<param name="menu" value="false">
	<param name="scale" value="noScale">
	<param name="allowFullscreen" value="true">
	<param name="allowScriptAccess" value="always">
	<param name="bgcolor" value="">
	<param name="wmode" value="direct">
</object>
 --%>
</body>
</html>
