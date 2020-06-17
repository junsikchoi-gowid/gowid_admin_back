<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="com.nprotect.pluginfree.PluginFree"
%><%
String uniqueId = request.getParameter("id");
%><%PluginFree.finalize(request, uniqueId);%>