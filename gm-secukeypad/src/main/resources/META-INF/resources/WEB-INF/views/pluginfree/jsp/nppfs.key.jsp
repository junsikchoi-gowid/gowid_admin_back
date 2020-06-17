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
String uniqueId = request.getParameter("id");
String vendor = request.getParameter("c");
%><%=PluginFree.generate(request, uniqueId, vendor)%>