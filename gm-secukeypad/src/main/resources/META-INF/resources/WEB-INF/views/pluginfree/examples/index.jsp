<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@page import="java.io.ByteArrayOutputStream"
%>
<%@page import="java.io.OutputStreamWriter"
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
    <script type="text/javascript" src="/static/pluginfree/js/nppfs-1.0.0.js?dummy=<%= now %>" charset="utf-8"></script>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            jQuery("#userAgent").text(navigator.userAgent);

//	uV.dV.dk = ad.jt; // Debug

            npPfsCtrl.isInstall({
                success: function () {
                    npPfsCtrl.hideLoading();
                    $("#nos-install").html("설치됨");
                    //alert("보안프로그램이 설치되어 있습니다.");
                },
                fail: function () {
                    npPfsCtrl.hideLoading();
                    //alert("보안프로그램의 설치가 필요합니다.");
                }
            });


            document.form1.cardNo1.focus();

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
            npPfsStartup(document.form1, true, true, true, true, "npkencrypt", "on");
        });


        function decryptKeyCryptData() {
            npPfsCtrl.waitSubmit(function () {
                document.form1.submit();
            });
        }

        function doDecrypt() {
            npPfsCtrl.waitSubmit(function () {
                document.form2.submit();
            });
        }

    </script>

</head>
<!-- <body oncontextmenu="return false" onselectstart="return false" ondragstart="return false"> -->
<body>
<!-- 
<iframe id="localDaemon" name="localDaemon" src="https://pfs.nprotect.com:14430" style="width:1px;height:1px;"></iframe>
<iframe src="about:blank;" frameborder="0" scrolling="auto" style="width:0px;height:0px;"></iframe>
 -->
<%!
    private String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        String enc = writer.getEncoding();
        return enc;
    }

%>
<table>
    <tr>
        <th style="text-align:left;font-size:14pt;">접속정보</th>
    </tr>
    <tr>
        <td>
            <span id="userAgent"></span>
        </td>
        <td>
            <span id="nos-install"></span>
        </td>
    </tr>
</table>

<form name="dummy">
</form>

<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">
    <table>
        <tr>
            <th style="text-align:left;font-size:14pt;">개인방화벽 테스트</th>
        </tr>
        <tr>
            <td>
                <input type="button" name="startNos" id="startNos" value="방화벽 시작" onclick="npNCtrl.start();">
                <input type="button" name="stopNos" id="stopNos" value="방화벽 종료" onclick="npNCtrl.stop();">

            </td>
        </tr>
    </table>
</div>
<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">

    <form name="form1" action="<%= response.encodeURL("/pluginfree/decrypt") %>" method="post" target="resultTarget">
        <div id="nppfs-loading-modal" style="display:none;"></div>

        <input type="hidden" name="mode" value="KEYCRYPT"/>
        <table width="100%">
            <colgroup>
                <col width="10%"></col>
                <col width="90%"></col>
            </colgroup>
            <tr>
                <th colspan="2" style="text-align:left;font-size:14pt;">키보드보안 테스트</th>
            </tr>
            <tr>
                <td> 미보호</td>
                <td><input type="text" name="NONE_TEXT_2" id="n2" value="" npkencrypt="off"/></td>
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
                <td>E2E Id(Inca):</td>
                <td><input type="text" name="E2E_TEXT_1" id="t1" style="ime-mode:disabled;" npkencrypt="on"
                           data-keypad-type="alpha" value="" maxlength="14"/> : 14글자
                </td>
            </tr>
            <tr>
                <td>E2E PW(Inca):</td>
                <td><input type="password" name="E2E_PASS_1" id="p1" style="ime-mode:disabled;" npkencrypt="on"
                           data-keypad-type="alpha" data-keypad-theme="mobile" value="" maxlength="16"/> : 16글자
                </td>
            </tr>
            <tr>
                <td>E2E Card(Inca):</td>
                <td>
                    <input type="password" name="cardNo1" id="cardNo1" style="ime-mode:disabled;" npkencrypt="on"
                           data-keypad-type="num" data-keypad-theme="mobile" value="" maxlength="4" size="4"
                           style="width:20px;"/>
                    <input type="password" name="cardNo2" id="cardNo2" style="ime-mode:disabled;" npkencrypt="on"
                           data-keypad-type="num" value="" maxlength="4" size="4" style="width:20px;"/>
                    <input type="password" name="cardNo3" id="cardNo3" style="ime-mode:disabled;" npkencrypt="on"
                           data-keypad-type="num" data-keypad-theme="mobile" value="" maxlength="4" size="4"
                           style="width:20px;"/>
                    <input type="password" name="cardNo4" id="cardNo4" style="ime-mode:disabled;" npkencrypt="on"
                           data-keypad-type="num" value="" maxlength="4" size="4" style="width:20px;"/>
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
                <td colspan="2">
                    <input type="button" name="getClientKey" id="getClientKey" value="복호화"
                           onclick="decryptKeyCryptData();">
                </td>
            </tr>
        </table>
    </form>
</div>

<%--
<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">
<form name="form2" action="decrypt.jsp" method="post" target="resultTarget">
<div class="nppfs-elements" style="display:none;"></div>
	<input type="hidden" name="mode" value="" />
	<table>
	 	<tr>
	 		<th style="text-align:left;font-size:14pt;"> 단말정보수집 테스트</th>
	 	</tr>
		<tr>
			<td><input type="button" name="doDec" onclick="doDecrypt();" value="복호화" /></td>
		</tr>
	</table>
</form>
</div>
 --%>

<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">
    <table width="100%">
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
</div>

</body>
</html>
