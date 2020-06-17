<%@page import="com.nprotect.pluginfree.modules.PluginFreeCtrl"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%
String mode = request.getParameter("m");
mode = (mode == null) ? "" : mode;

/***********************************************************************/
/* 키패드 치환 데이터 재암호(ARIA with IV) */
/***********************************************************************/
if("c".equals(mode)) {
	try{
		out.print(PluginFreeCtrl.getEncryptResult(request,response));
	} catch(Exception e){
		//e.printStackTrace();
	}
}


%>
