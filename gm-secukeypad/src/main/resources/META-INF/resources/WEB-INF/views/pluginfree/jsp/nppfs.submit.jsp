<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@page import="java.io.OutputStream"
%><%@page import="com.nprotect.pluginfree.PluginFree"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%
/*
System.out.println("-------------------");
Enumeration<String> enumeration = request.getParameterNames();
List<String> list = Collections.list(enumeration);
if(list.size()>0) {
	Collections.sort(list);
	for(String key : list) {
		try{
			//String key = (String)e.nextElement();
			String value = (String)request.getParameter(key);
			System.out.println("Request [" + key + "][" + value + "]");
			//out.println("// " + key + "  => " + value + "");
		} catch(Exception ex){}
	}
	//out.println();
	//out.println();
	//out.println();
}
*/
%><%
// 수행작업 구분
String mode = request.getParameter("m");
mode = (mode == null) ? "" : mode;


/***********************************************************************/
/* 공개키 얻기 */
/***********************************************************************/
if("p".equals(mode)) {
	String uuid = StringUtil.n2b(request.getParameter("u"), "");
	out.print(PluginFree.pem(request, uuid));
}
%>