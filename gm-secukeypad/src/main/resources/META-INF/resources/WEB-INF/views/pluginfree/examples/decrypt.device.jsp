<%@page import="com.nprotect.pluginfree.modules.PluginFreeConfig"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="java.util.ArrayList"
%><%@page import="java.util.List"
%><%@page import="java.util.Map"
%><%@page import="java.util.Set"
%><%@page import="java.util.Enumeration"
%><%@page import="java.util.Collections"
%><%@page import="java.util.Iterator"
%><%@page import="com.nprotect.pluginfree.PluginFree"
%><%@page import="com.nprotect.pluginfree.PluginFreeDTO"
%><%@page import="com.nprotect.pluginfree.PluginFreeException"
%><%@page import="com.nprotect.pluginfree.PluginFreeWarning"
%><%@page import="com.nprotect.pluginfree.PluginFreeDeviceDTO"
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeRequest"
%><%@page import="com.nprotect.pluginfree.util.RequestUtil"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%@page import="java.util.StringTokenizer"
%><!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>None-Plugin 테스트</title>
<style type="text/css">
body,td, th{font-size:10pt}
</style>
</head>
<body><%
String npd = request.getParameter("f_key");
if(npd != null && !"".equals(npd)){
	try {
		PluginFreeDeviceDTO dto = PluginFree.getDevice(request, response, session);
		
		out.println("============== <b>Device Info - NOS(무설치단말정보 수집)</b> ==============<br />");
		out.println(dto.toString().replace("\n", "<br />\n") + "<br />");
		out.println("<br />");
		out.println("<br />");
		out.println("<br />");
	} catch(Exception e) {
		e.printStackTrace();
	}
}
%>

</body>
</html>
