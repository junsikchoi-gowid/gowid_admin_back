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




try {
	String slog = request.getParameter("nos_script_log");
	String klog = request.getParameter("nos_npk_log");
	String flog = request.getParameter("nos_npf_log");
//	System.out.println(slog);

	out.println("============== <b>Debug Log - Script</b> ==============<br />");
	if(!StringUtil.isBlank(slog)) {

		String[] lines = slog.split("\\`\\^\\`");
		for(String line : lines) {
			out.println(line + "<br />\n");
		}
		
	}
	out.println("<br />");

	
	
	out.println("============== <b>Debug Log - NOS(Secure Key)</b> ==============<br />");
	if(!StringUtil.isBlank(klog)) {
		String[] lines = klog.split("\\|");
		for(String line : lines) {
			out.println(line.replace(":", "&nbsp;&nbsp;&nbsp;&nbsp;") + "<br />");
		}
	}
	out.println("<br />");

	
	
	out.println("============== <b>Debug Log - NOS(FDS)</b> ==============<br />");
	if(!StringUtil.isBlank(flog)) {
/* 		StringTokenizer tokens = new StringTokenizer(flog, "'|'" );
		while(tokens.hasMoreElements()) {
			String line = tokens.nextToken();
			out.println(line.replace("`^`", "&nbsp;&nbsp;&nbsp;&nbsp;") + "<br />");

		} */

		String[] lines = flog.split("'\\|'");
		for(String line : lines) {
			out.println(line.replace("'^'", "&nbsp;&nbsp;&nbsp;&nbsp;") + "<br />");
		}
	}

	out.println("<br />");
	out.println("<br />");
	out.println("<br />");
} catch(Exception e1) { }




out.println("============== <b>Decrypt with Map<String, String></b> ==============<br />");
// Map from npPfsCtrl.toJson()
String json = request.getParameter("_np_param_");	//"some of json string by npPfsCtrl.toJson()";
Map<String, String> map = RequestUtil.parseJson(json);			// npPfsCtrl.toJson()
ServletRequest req = new PluginFreeRequest(request, map);


// Print
Enumeration<String> names1 = (Enumeration<String>)request.getParameterNames();
List<String> list1 = Collections.list(names1);
Collections.sort(list1);
for(String name : list1) {
	if("i_debug_log".equals(name) || "nos_script_log".equals(name)) continue;
	String values[] = req.getParameterValues(name);
	if(values != null && values.length > 0) {
		out.println("입력양식명 : " + name + " => ");
		for(String value : values) {
			out.println("[" + value + "]");
		}
		out.println("<br />");
	}
}
out.println("<br />");
out.println("<br />");
out.println("<br />");


out.println("============== <b>getParameterValues()</b> ==============<br />");
Enumeration<String> names2 = (Enumeration<String>)request.getParameterNames();
List<String> list2 = Collections.list(names2);
Collections.sort(list2);
for(String name : list2) {
	if("i_debug_log".equals(name) || "nos_script_log".equals(name)) continue;
	String values[] = request.getParameterValues(name);
	if(values != null && values.length > 0) {
		out.println("입력양식명 : " + name + " => ");
		for(String value : values) {
			out.println("[" + value + "]");
		}
		out.println("<br />");
	}
}
out.println("<br />");
out.println("<br />");
out.println("<br />");






String npk = request.getParameter("__E2E_UNIQUE__");
if(npk != null && !"".equals(npk)){
	out.println("키보드보안 또는 마우스입력기가 실행중입니다.<br />");

	try {
		PluginFree.verify(request, new String[]{"cardNo1", "cardNo2", "cardNo3", "cardNo4", "E2E_DB_AMOUNT_1"});
	} catch(Exception e) {
		e.printStackTrace();
		out.println("키보드보안/마우스입력기 복호화 검증 오류가 발생하였습니다.<br />");
	}
	
	out.println("============== <b>단순복호화(값직접세팅)</b> ==============<br />");
	String uuid = request.getParameter("__E2E_UNIQUE__");
	String e2eResult = request.getParameter("__E2E_RESULT__");
	out.println("<strong>단순복호화(cardNo1) : " + PluginFree.decrypt(session, uuid, e2eResult, request.getParameter("cardNo1"), request.getParameter("cardNo1__E2E__")) + "</strong><br />");
	out.println("<strong>단순복호화(cardNo2) : " + PluginFree.decrypt(session, uuid, e2eResult, request.getParameter("cardNo2"), request.getParameter("cardNo2__E2E__")) + "</strong><br />");
	out.println("<strong>단순복호화(cardNo3) : " + PluginFree.decrypt(session, uuid, e2eResult, request.getParameter("cardNo3"), request.getParameter("cardNo3__E2E__")) + "</strong><br />");
	out.println("<strong>단순복호화(cardNo4) : " + PluginFree.decrypt(session, uuid, e2eResult, request.getParameter("cardNo4"), request.getParameter("cardNo4__E2E__")) + "</strong><br />");
	out.println("<br />");
	out.println("<br />");
	
	out.println("============== <b>수동복호화(필터사용안함)</b> ==============<br />");
	ServletRequest pluginfreeRequest = new PluginFreeRequest(request);
	String cardNo1 = pluginfreeRequest.getParameter("cardNo1");
	String cardNo2 = pluginfreeRequest.getParameter("cardNo2");
	String cardNo3 = pluginfreeRequest.getParameter("cardNo3");
	String cardNo4 = pluginfreeRequest.getParameter("cardNo4");
	
	out.println("<strong>수동복호화(cardNo1) : " + cardNo1 + "</strong><br />");
	out.println("<strong>수동복호화(cardNo2) : " + cardNo2 + "</strong><br />");
	out.println("<strong>수동복호화(cardNo3) : " + cardNo3 + "</strong><br />");
	out.println("<strong>수동복호화(cardNo4) : " + cardNo4 + "</strong><br />");
	out.println("<br />");
	out.println("<br />");


	out.println("============== <b>복호화(필터사용)</b> ==============<br />");
	List<String> viewParams = new ArrayList<String>();

//	Map<String, String[]> parameters = request.getParameterMap();
//	for(String parameter : parameters.keySet()) {
	Enumeration<String> enumeration = request.getParameterNames();
	List<String> list = Collections.list(enumeration);
	Collections.sort(list);
	for(String parameter : list) {
		if("i_debug_log".equals(parameter) || "nos_script_log".equals(parameter)) continue;
		if(parameter.endsWith("__E2E__")){
			String plainkey = parameter.substring(0, parameter.length() - "__E2E__".length());
			viewParams.add(plainkey);
			viewParams.add(parameter);
			
			String inputValue = request.getParameter(plainkey);
			//String decryptValue = e2e.decrypt(plainkey);
			//String decryptValue = e2e.decryptValue(request.getParameter(parameter));
			

			out.println("<strong>");
			out.println("입력양식명 : " + plainkey +" : ");
			//out.println("전달받은값 : " + inputValue +"<br />");
			out.println("E2E복호화값 : " + inputValue +"<br />");
			out.println("E2E복호화방법 : " + PluginFree.findDecryptionMode(request, plainkey) +"<br />");
			//out.println("동일여부 : " + decryptValue.equals(inputValue) +"<br />");
			out.println("</strong>");
			out.println("<br />");
		}
	}
	
	
//	for(String parameter : parameters.keySet()) {
	for(String parameter : list) {
		if("i_debug_log".equals(parameter) || "nos_script_log".equals(parameter)) continue;
		if(parameter.endsWith("__E2E__")){
			continue;
		}
		if(viewParams.indexOf(parameter)>=0){
			continue;
		}
		
		String value2 = request.getParameter(parameter);
		out.println("입력양식명 : " + parameter+ " => [" + value2 +"]");
		out.println("<br />");
	}
} else {
	Enumeration<String> enumeration = request.getParameterNames();
	List<String> list = Collections.list(enumeration);
	Collections.sort(list);
	for(String parameter : list) {
		if("i_debug_log".equals(parameter) || "nos_script_log".equals(parameter)) continue;
		String value2 = request.getParameter(parameter);
		out.println("입력양식명 : " + parameter + " => [" + value2 +"]");
		out.println("<br />");
	}
}


try{
	String npf = request.getParameter("i_e2e_key");
	if(npf != null && !"".equals(npf)){
		out.println("===================================================<br />");
		
		try{
			//PluginFree.verifyFds(new PluginFreeRequest(request));
			PluginFree.verifyFds(request);
		} catch(PluginFreeException e) {
			out.println("FDS값 복호화를 위한 올바른 암호화 키값이 아닙니다." + e.toString());
			//out.println("단말정보수집 복호화 검증 오류가 발생하였습니다.<br />");
			return;
		}

		//PluginFree pf = new PluginFree(new PluginFreeRequest(request));
		PluginFree pf = new PluginFree(request);
		PluginFreeDTO dto = pf.get();
		
		if(dto != null){
			out.println(dto.toString().replace("\n", "<br />\n") + "<br />");
		} else {
			out.println("값을 파싱할 수 없습니다.<br />");
		}
		
		out.println("------- PluginFreeWarning -------<br />");	
		List<PluginFreeWarning> warningList = pf.getLastResult();
		if(warningList != null){
			for(int i = 0 ; i < warningList.size() ; i++){
				out.println("<br />"+warningList.get(i)+"<br />");	
			}	
		}
		out.println("<br />");
		out.println("<br />");
		out.println("<br />");
	}
} catch(Exception e){
	e.printStackTrace();
}



String npd = request.getParameter("f_key");
if(npd != null && !"".equals(npd)){
	try {
		PluginFreeDeviceDTO dto = PluginFree.getDevice(request, response, session);
//		System.out.println(dto.toString());
		
		out.println("============== <b>Device Info - NOS(무설치단말정보 수집)</b> ==============<br />");
		out.println(dto.toString().replace("\n", "<br />\n") + "<br />");
		out.println("<br />");
		out.println("<br />");
		out.println("<br />");
	} catch(Exception e) {
		e.printStackTrace();
	}
}



try{
	String npf = request.getParameter("i_e2e_key");
	if(!PluginFreeConfig.getInstance().isUseFdsTranslate()){
		return;
	}
	if(npf != null && !"".equals(npf)){
		out.println("============== <b>TRANSLATE DTO(TO MAP)</b> ==============<br />");
		
		try{
			PluginFree.verifyFds(request);
		} catch(PluginFreeException e) {
			out.println("FDS값 복호화를 위한 올바른 암호화 키값이 아닙니다." + e.toString());
			return;
		}

		PluginFree pf = new PluginFree(request);
		Object dto = pf.translate();
		
		if (dto instanceof Map) {
			Map<String,Object> translateDto = (Map<String,Object>) dto;
			
			Iterator<String> iterator = translateDto.keySet().iterator();
	        
	        while(iterator.hasNext()){            
	            String key = iterator.next();
	            Object value = translateDto.get(key);
	            
	            if(value == null) value = "";
	            if(value instanceof String[]){
	            	String[] tmp = (String[])value;
	            	String tmpValue = "";
	            	if(tmp != null && tmp.length > 0){
	        			for(int i = 0 ; i < tmp.length ; i++) {
	        				if(i > 0){
	        					tmpValue += ", ";
	        				}
	        				tmpValue += tmp[i];
	        			}
	        		}
	            	out.println(key+" : "+tmpValue+"<br />");
	            } else {
	            	out.println(key+" : "+value+"<br />");	
	            }
	        }
        } else {
        	PluginFreeDTO translateDto = (PluginFreeDTO) dto;
        	if(dto != null){
    			out.println(translateDto.toString().replace("\n", "<br />\n") + "<br />");
    		} else {
    			out.println("값을 파싱할 수 없습니다.<br />");
    		}
        }
	}
} catch(Exception e){
	e.printStackTrace();
}


// 세션삭제
//PluginFree.finalize(session);

%>
</body>
</html>
