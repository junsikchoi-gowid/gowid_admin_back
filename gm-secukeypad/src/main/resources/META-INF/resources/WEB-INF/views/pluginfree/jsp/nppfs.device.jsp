<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@page import="java.io.OutputStream"
%><%@page import="java.util.Map"
%><%@page import="com.nprotect.pluginfree.PluginFree"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%@page import="com.nprotect.common.json.JSONParser"
%><%@page import="com.nprotect.common.json.JSONObject"
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeDevice"
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

/***********************************************************************/
/* 데이터 파싱 및 수집정보 분석/가공 */
/***********************************************************************/
else if("e".equals(mode)) {
	String uuid = StringUtil.n2b(request.getParameter("u"), "");
	String rsaresult = StringUtil.n2b(request.getParameter("r"), "");
	String data = StringUtil.n2b(request.getParameter("d"), "");
	
	try {
		String ret = PluginFreeDevice.exchange(request, session, uuid, rsaresult, data);
		//System.out.println(ret);
		out.print(ret);
	} catch(Exception e){
		out.print("{}");
	}
}

/***********************************************************************/
/* IP 수집 경로 얻어오기 */
/***********************************************************************/
else if("i".equals(mode)) {
	String ret = PluginFreeDevice.getIpcAddress();
	out.print(ret);
}
%>