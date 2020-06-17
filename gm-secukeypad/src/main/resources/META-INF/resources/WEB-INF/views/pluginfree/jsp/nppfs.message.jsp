<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="com.nprotect.common.cipher.CipherUtil"
%><%@page import="com.nprotect.pluginfree.util.EncryptUtil"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");
%><%
if("HTTP/1.1".equals(request.getProtocol())) {
	response.setHeader ("Cache-Control", "no-cache");
} else {
	response.setHeader ("Cache-Control", "no-store");
}
response.setDateHeader ("Expires", 0);

String encryptIp = "";

String ip = null;
if(StringUtil.isBlank(ip) && !StringUtil.isBlank(request.getHeader("True-Client-IP"))){
	ip = request.getHeader("True-Client-IP");
} else if(StringUtil.isBlank(ip) && !StringUtil.isBlank(request.getHeader("X-Forwarded-For"))){
	ip = request.getHeader("X-Forwarded-For");
} else {
	ip = request.getRemoteAddr();
}

// IV값 변조시 처리(IV를 임의로 만들고 앞에 추가)
String iv = CipherUtil.getSecureRandom(16);
try {
	encryptIp = EncryptUtil.encrypt(EncryptUtil.KEY_TYPE.KEY_NPF, iv, ip, true);
} catch(Exception e2) {
}
%><!doctype html>
<html>
<head></head>
<body>
<script type="text/javascript">
function tryCatch(a, b) {
	try {
		return a(b)
	} catch (c) {}
}

function onMessage(event) {
	if (event.origin === REFERRER) {
//		var json = tryCatch(JSON.parse, event.data);
//		console.log("POST origin : " + event.origin + ", " + REFERRER);
//		console.log("POST MESSAGE : <%= encryptIp %>");
		event.source.postMessage("<%= encryptIp %>", REFERRER);
	}
}

var w = window
  , d = document
  , l = w.location
  , REFERRER = (d.referrer.match(/^.+\:\/\/[^\/]+/) || [])[0];
w.addEventListener ? w.addEventListener("message", onMessage, !0) : w.attachEvent && w.attachEvent("onmessage", onMessage);
</script>
</body>
</html>
