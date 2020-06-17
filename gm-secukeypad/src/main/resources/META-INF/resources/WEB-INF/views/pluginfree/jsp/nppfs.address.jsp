<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="com.nprotect.common.cipher.CipherUtil"
%><%@page import="com.nprotect.pluginfree.util.EncryptUtil"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%
if("HTTP/1.1".equals(request.getProtocol())) {
	response.setHeader ("Cache-Control", "no-cache");
} else {
	response.setHeader ("Cache-Control", "no-store");
}
response.setDateHeader ("Expires", 0);


String ip = request.getRemoteAddr();
String iv = StringUtil.n2b(request.getParameter("z"), "");
boolean doDefault = false;
if(!StringUtil.isBlank(iv) && iv.length() == 32){
	try{
		out.print(EncryptUtil.encrypt(EncryptUtil.KEY_TYPE.KEY_NPF, iv, ip, false));
	} catch(Exception e1) {
		doDefault = true;
	}
} else {
	doDefault = true;
}

if(doDefault){
	// IV값 변조시 처리(IV를 임의로 만들고 앞에 추가)
	iv = CipherUtil.getSecureRandom(16);
	try {
		out.print(EncryptUtil.encrypt(EncryptUtil.KEY_TYPE.KEY_NPF, iv, ip, true));
	} catch(Exception e2) {
	}
}
%>