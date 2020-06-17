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
    <script type="text/javascript" src="/pluginfree/js/nppfs.device-1.0.0.js?dummy=<%= now %>" charset="utf-8"></script>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            jQuery("#userAgent").text(navigator.userAgent);

            uV.dV.Gf = "<%= response.encodeURL("/pluginfree/jsp/nppfs.key.jsp") %>";    // 키발급 경로
            uV.dV.zf = "<%= response.encodeURL("/pluginfree/jsp/nppfs.remove.jsp") %>"; // 키삭제 경로
            uV.dV.zo = "<%= response.encodeURL("/pluginfree/jsp/nppfs.keypad.jsp") %>";  // 마우스입력기 페이지
            uV.dV.eP = "<%= response.encodeURL("/pluginfree/jsp/nppfs.ready.jsp") %>";  // 초기화상태 확인경로
            uV.dV.Fz = "<%= response.encodeURL("/pluginfree/jsp/nppfs.install.jsp") %>"; // 설치안내 페이지

            npPfsStartupV2(document.form1, [false, false, false, false, false, true], "npkencrypt", "on");
            npPfsCtrl.showLoading();
            doDecrypt();

        });

        function doDecrypt() {
            npPfsCtrl.waitSubmit(function () {
                document.form1.submit();
                npPfsCtrl.hideLoading();
            });
        }

    </script>

</head>
<body>
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

    <form name="form1" action="<%= response.encodeURL("decrypt.device.jsp") %>" method="post" target="resultTarget">
        <div id="nppfs-loading-modal" style="display:none;"></div>

        <input type="hidden" name="mode" value="KEYCRYPT"/>
        <table width="100%">
            <tr>
                <th style="text-align:left;font-size:14pt;">무설치단말정보 수집데이터</th>
            </tr>
            <tr>
                <td>
                    <iframe id="resultTarget" name="resultTarget" src="about:blank"
                            style="border:0px solid #000;width:100%;height:500px;"></iframe>
                </td>
            </tr>
        </table>
    </form>
</div>


</body>
</html>
