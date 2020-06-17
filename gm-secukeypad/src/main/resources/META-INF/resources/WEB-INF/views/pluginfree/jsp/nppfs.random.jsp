<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="java.security.SecureRandom"
%><%!
private static String random(int arraySize){
	SecureRandom rand = new SecureRandom();
	StringBuilder builder = new StringBuilder();
	builder.append("[");
	for(int idx = 0; idx < arraySize; idx++){
		if(idx > 0) builder.append(",");
		builder.append(Math.abs(rand.nextInt(255)));
	}
	builder.append("]");
	
	return builder.toString();
}
%><%
if("HTTP/1.1".equals(request.getProtocol())) {
	response.setHeader ("Cache-Control", "no-cache");
} else {
	response.setHeader ("Cache-Control", "no-store");
}
response.setDateHeader ("Expires", 0);
%><%
// String uniqueId = request.getParameter("s");

%><%= random(100) %>