<%@page import="com.nprotect.pluginfree.modules.PluginFreeKeyPad" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%>
<%@page import="com.nprotect.pluginfree.util.StringUtil"
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

    String rand = request.getParameter("rand");
    String mode = request.getParameter("m");
    if ("d".equals(mode)) {
	/*
	String value = request.getParameter("pkipassword");
	String random = CipherUtil.getSecureRandom(16);
	byte[] keyBytes = CipherUtil.makekey(StringUtil.hexDecode(random));
	byte[] encoded = ARIAWrapper.encrypt(keyBytes, ARIAWrapper.getDefaultIV(), value.getBytes());
	String output = StringUtil.hexEncode(encoded);
	out.print(random + "" + output);
	*/


        StringBuffer buffer = new StringBuffer();
        String result = null;
        try {
            PluginFreeKeyPad keypad = new PluginFreeKeyPad(request, response, request.getSession());
            result = keypad.getEncryptReplacement();
        } catch (Exception e) {
        }

        String output = "result=" + result;
        out.print(output);

    } else if (StringUtil.isBlank(rand)) {
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>nProtect Online Security v1.0.0</title>
    <script type="text/javascript" src="/static/pluginfree/js/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="/pluginfree/jsp/nppfs.script.jsp"></script>
    <script type="text/javascript" src="/static/pluginfree/js/nppfs-1.0.0.js?dummy=<%= now %>"></script>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            jQuery("#pkipassword").focus();
            npPfsStartup(document.form1, false, false, false, true, "npkencrypt", "on");
        });

        function ajax(url, params, options) {
            var result = "";
            jQuery.ajax({
                url: url,
                async: true,
                type: "POST",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                data: params,
                error: function (xhr, textStatus, errorThrown) {
                },
                success: options.callback,
                complete: function () {
                }
            });

            return result;
        }

        function decrypt() {
            var values = npPfsCtrl.serializeArray(document.form1, false);
            var params = {};

            for (var i = 0; i < values.length; i++) {
                var key = values[i].name;
                if (key == "__E2E_UNIQUE__") {
                    params["u"] = values[i].value;
                } else if (key == "__E2E_KEYPAD__") {
                    params["r"] = values[i].value;
                } else if (key.indexOf("__KH_") != -1) {
                    params["v"] = values[i].value;
                } else if (key.indexOf("__KI_") != -1) {
                    params["k"] = values[i].value;
                }
            }

            var result = ajax("<%=request.getRequestURI()%>?m=d", params, {
                callback: function (result) {
                    if (result != null && result != "" && result.length > 32) {
                        <%--
                                        var v1 = result.substring(0, 32);
                                        var v2 = result.substring(32);
                                        //alert("[" + result + "]\n[" + v1 + "]\n[" + v2 + "]")
                                        //var location = "transkey://localhost/?rand="+ v1 + "&result=" + v2;

                                        var location = "<%=request.getRequestURI()%>?rand="+ v1 + "&result=" + v2;
                                        //console.log(location);
                                        document.location.href = location;
                        --%>
                        var location = "<%=request.getRequestURI()%>?" + result.trim();
                        document.location.href = location;
                    }
                }
            });
        }
    </script>

</head>
<body oncontextmenu="return false" onselectstart="return false" ondragstart="return false">
<div class="nppfs-elements" style="display:none;"></div>
<form name="form1" action="#>" method="post">
    <table style="width:100%;">
        <tr>
            <td>
                <input type="password"
                       id="pkipassword"
                       name="pkipassword"
                       npkencrypt="on"
                       data-keypad-type="alpha"
                       data-keypad-show="div"
                       data-keypad-enter="decrypt()"
                       value=""
                       style="ime-mode:disabled;width:100%;"
                />
            </td>
        </tr>
    </table>
    <div class="nppfs-keypad-div" style="display:block;"></div>
</form>
<div id="nppfs-loading-modal" style="display:none;"></div>

</body>
</html>
<% } %>