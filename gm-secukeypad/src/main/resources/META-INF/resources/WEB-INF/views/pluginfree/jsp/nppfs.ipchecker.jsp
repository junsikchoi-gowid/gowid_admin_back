<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%
if("HTTP/1.1".equals(request.getProtocol())) {
	response.setHeader ("Cache-Control", "no-cache");
} else {
	response.setHeader ("Cache-Control", "no-store");
}
response.setDateHeader ("Expires", 0);

out.print(request.getRemoteAddr());
%>