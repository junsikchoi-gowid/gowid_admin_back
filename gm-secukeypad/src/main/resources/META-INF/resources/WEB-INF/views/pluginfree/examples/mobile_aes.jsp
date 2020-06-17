<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="java.util.Enumeration"
%><%@page import="java.util.List"
%><%@page import="java.util.Collections"
%><%@page import="com.nprotect.pluginfree.PluginFreeMobile"
%><%@page import="com.nprotect.pluginfree.PluginFreeMobileDTO"
%><%@page import="com.nprotect.pluginfree.PluginFreeException"
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeDecrypt"
%><%!
private String n2b(String val){
	return n2b(val, "");
}
private String n2b(String val, String def) {
	return (val == null)? def : val;
}
%><%
response.setDateHeader ("Expires", 0);
if("HTTP/1.1".equals(request.getProtocol())) {
	response.setHeader ("Cache-Control", "no-cache");
} else {
	response.setHeader("Cache-Control", "no-store");
}
%><%



Enumeration<String> enumeration = request.getParameterNames();
List<String> list = Collections.list(enumeration);
if(list.size()>0){
	Collections.sort(list);
	for(String key : list) {
		try{
			//String key = (String)e.nextElement();
			String value = (String)request.getParameter(key);
			//out.println("nProtect Plugin Free Service (Mobile), Request [" + key + "][" + value + "]");
			System.out.println("nProtect Plugin Free Service (Mobile), Request [" + key + "][" + value + "]");
			//System.out.println("// " + key + "  => " + value + "");
		} catch(Exception ex){}
	}
	//out.println();
	//out.println();
	//out.println();
	System.out.println("nProtect Plugin Free Service (Mobile), -----------------------");
}


String m = n2b(request.getParameter("m"));

// 키발급
if("k".equals(m)) {
	String uuid = n2b(request.getParameter("u"));
	out.print(PluginFreeMobile.generate(request, uuid));
}

// 복호화
else if("d".equals(m)) {
/*
	u	<=	i_e2e_id  : 타임스탬프
	k	<=	i_e2e_key : 클라이언트 랜덤키를 공개키로 암호화
	d	<=	i_log_total : 암호화된 데이터
	
	// 이미 포함
	i_elapsed_tm : 소요 시간
	i_version : 수집모듈 버전
	i_tot_hash : 수집정보 해쉬값 암호화

	// 불필요
	i_borun : 암호화여부 01
	i_log_yn : Y
	i_efds_yn : Y
*/
	
	
//	PluginFree free = new PluginFree();
//	PluginFree.verify(request, new String[]{"cardNo1", "cardNo2", "cardNo3", "cardNo4"});

	//HttpSession session = request.getSession();
	{
		try {
			String uuid = n2b(request.getParameter("u"));
			String key = n2b(request.getParameter("k"));
			String value = n2b(request.getParameter("d"));

			PluginFreeDecrypt decoder = PluginFreeMobile.decoder(request, uuid, key);
			
			PluginFreeMobileDTO dto = PluginFreeMobile.mobile(decoder, value);
			if(dto != null){
				out.print(dto.toString().replaceAll("\n", "<br />\n"));
			} else {
				System.out.println("nProtect Plugin Free Service (Mobile, FDS), Empty.");
			}
		} catch(PluginFreeException e){
			//e.printStackTrace();
			System.err.println(String.format("nProtect Plugin Free Service (Mobile, FDS), %s : %s", e.getCode(), e.getMessage()));
		}
	}
	//out.print(PluginFreeMobile.generate(session, timestamp));

	try {
		String uuid = n2b(request.getParameter("uid"));
		String key = n2b(request.getParameter("key"));

		String[] inputs = request.getParameterValues("i");
		if(inputs != null && inputs.length > 0) {
			for(String inputname : inputs){
				String input = n2b(request.getParameter(inputname));
				String value = n2b(request.getParameter(inputname + "_e2e"));
				
				PluginFreeDecrypt decoder = PluginFreeMobile.decoder(request, uuid, key);
				String plain = PluginFreeMobile.keypad(decoder, input, value, "AES");
				if(plain != null) {
					out.print(plain + "<br />\n");
					System.out.println(String.format("nProtect Plugin Free Service (Mobile, Keypad), %s : %s => %s", inputname, input, plain));
				}
			}
		}
	} catch(PluginFreeException e){
		System.err.println(String.format("nProtect Plugin Free Service (Mobile, Keypad), %s : %s", e.getCode(), e.getMessage()));
	}

	try {
		String[] inputs = request.getParameterValues("i");
		if(inputs != null && inputs.length > 0) {
			for(String inputname : inputs){
				String input = n2b(request.getParameter(inputname));
				String uuid = n2b(request.getParameter(inputname + "_uid"));
				String key = n2b(request.getParameter(inputname + "_key"));
				String value = n2b(request.getParameter(inputname + "_e2e"));
				
				PluginFreeDecrypt decoder = PluginFreeMobile.decoder(request, uuid, key);
				
				String plain = PluginFreeMobile.keypad(decoder, input, value, "AES");
				if(plain != null) {
					out.print(plain + "<br />\n");
					System.out.println(String.format("nProtect Plugin Free Service (Mobile, Keypad), %s : %s => %s", inputname, input, plain));
				}
			}
		}
	} catch(PluginFreeException e){
		System.err.println(String.format("nProtect Plugin Free Service (Mobile, Keypad), %s : %s", e.getCode(), e.getMessage()));
	}
	
	// 세션삭제
	PluginFreeMobile.finalize(request);
} else {
}
%>