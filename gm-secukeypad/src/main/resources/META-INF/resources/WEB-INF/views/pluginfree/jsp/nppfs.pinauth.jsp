<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@page import="java.io.OutputStream"
%><%@page import="java.awt.image.BufferedImage"
%><%@page import="javax.imageio.ImageIO"
%><%@page import="com.nprotect.pluginfree.pinauth.PinAuthCtrl"
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

PinAuthCtrl pinAuth = new PinAuthCtrl(request, response, request.getSession());


/***********************************************************************/
/* 세션값 삭제 */
/***********************************************************************/
if("f".equals(mode)) {
	pinAuth.finalize();
//		request.getSession().removeAttribute(PluginFreeConst.PLUGINFREE_SESSION_KEY);
}


/***********************************************************************/
/* 공개키 얻기 */
/***********************************************************************/
else if("p".equals(mode)) {
	out.print(pinAuth.getKey());
}


/***********************************************************************/
/* PIN인증 스크립트 생성 */
/***********************************************************************/
else if("e".equals(mode)) {
	try {
		//System.out.println(pinAuth.getCode());
		out.print(pinAuth.getCode());
	} catch (Exception e) {
		e.printStackTrace();
	}
}


/***********************************************************************/
/* CSS */
/***********************************************************************/
else if("c".equals(mode)) {
	try{
		response.setContentType("text/css");
		out.print(pinAuth.getCss());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

/***********************************************************************/
/* PIN 정보 정합성 확인 */
/***********************************************************************/
else if("x".equals(mode)) {
	try{
		out.print(pinAuth.exists());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

/***********************************************************************/
/* PIN 정보 저장 */
/***********************************************************************/
else if("s".equals(mode)) {
	try{
		out.print(pinAuth.save());
	} catch(Exception e){
		//e.printStackTrace();
	}
}


/***********************************************************************/
/* PIN 정보 비교 */
/***********************************************************************/
else if("o".equals(mode)) {
	try{
		out.print(pinAuth.compare());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

/***********************************************************************/
/* PIN 정보 재저장 */
/***********************************************************************/
else if("r".equals(mode)) {
	try{
		out.print(pinAuth.reset());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

/***********************************************************************/
/* PIN 정보 삭제 */
/***********************************************************************/
else if("d".equals(mode)) {
	try{
		out.print(pinAuth.remove());
	} catch(Exception e){
		//e.printStackTrace();
	}
}



/***********************************************************************/
/* PIN인증 복호화 */
/***********************************************************************/
/*
else if("d".equals(mode)) {
	try{
		out.print(pinAuth.decode());
	} catch(Exception e){
		//e.printStackTrace();
	}
}
*/
%>