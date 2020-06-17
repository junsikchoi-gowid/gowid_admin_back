<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="com.nprotect.pluginfree.PluginFree"
%><%
if("HTTP/1.1".equals(request.getProtocol())) {
	response.setHeader ("Cache-Control", "no-cache");
} else {
	response.setHeader ("Cache-Control", "no-store");
}
response.setDateHeader ("Expires", 0);
%><%
String mode = request.getParameter("m");
if("e".equals(mode)) {			// Expire Session
	session.invalidate();
} else {
	// Keep Session
}
%>