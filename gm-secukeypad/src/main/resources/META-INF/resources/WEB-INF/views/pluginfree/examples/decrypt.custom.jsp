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
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeRequest"
%><%@page import="com.nprotect.pluginfree.util.RequestUtil"
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

out.println("============== <b>Decrypt with Map<String, String></b> ==============<br />");
// Map from npPfsCtrl.toJson()
//String json = "some of json string by npPfsCtrl.toJson()"; 
//Map<String, String> map = RequestUtil.parseJson(json);			// npPfsCtrl.toJson()
//ServletRequest req = new PluginFreeRequest(request, map);


String json = request.getParameter("json");
Map<String, String> map = RequestUtil.parseJson(json);

//out.println(json);

//out.println(map);

// Map from HttpServletRequest
//Map<String, String> map = RequestUtil.getRequestToMap(request);
ServletRequest req = new PluginFreeRequest(request, map);

Iterator<String> iter = map.keySet().iterator();
while(iter.hasNext()){
	String name = iter.next();
	out.println("입력양식명 : " + name + " => [" + req.getParameter(name) + "]");
	String values[] = req.getParameterValues(name);
	if(values != null && values.length > 0) {
		for(String value : values) {
			out.println("[" + value + "]");
		}
	}
	out.println("<br />");
}


// 세션삭제
//PluginFree.finalize(session);

%>
</body>
</html>
