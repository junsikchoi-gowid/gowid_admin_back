<%@page import="java.awt.Graphics2D"%>
<%@page import="java.util.Iterator"%>
<%@page import="javax.imageio.IIOImage"%>
<%@page import="javax.imageio.ImageWriteParam"%>
<%@page import="javax.imageio.stream.ImageOutputStream"%>
<%@page import="javax.imageio.ImageWriter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@page import="java.io.OutputStream"
%><%@page import="java.awt.image.BufferedImage"
%><%@page import="javax.imageio.ImageIO"
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeKeyPad"
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

PluginFreeKeyPad keypad = new PluginFreeKeyPad(request, response, request.getSession());



/***********************************************************************/
/* 이미지 합성 */
/***********************************************************************/
/* if("i".equals(mode)) {
	// 이미지 합성
	OutputStream os = null;
	try {
		BufferedImage bi = keypad.getImage();
		BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) newBi.getGraphics();
		g2d.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
	    
		out.clear();
		response.setContentType("image/jpg");
		os = response.getOutputStream();
		
		ImageIO.write( newBi , "jpg" , os );
		os.flush();
	} catch (Exception e) {
		//e.printStackTrace();
	} finally {
		if(os != null) try{os.close();}catch(Exception e){}
	}
} */
	
if("i".equals(mode)) {
	// 이미지 합성
	OutputStream os = null;
	try {
		BufferedImage bi = keypad.getImage();
		out.clear();
		response.setContentType("image/png");
		os = response.getOutputStream();
		ImageIO.write( bi , "png" , os );
		os.flush();
	} catch (Exception e) {
		//e.printStackTrace();
	} finally {
		if(os != null) try{os.close();}catch(Exception e){}
	}
}

/***********************************************************************/
/* 세션값 삭제 */
/***********************************************************************/
else if("f".equals(mode)) {
	keypad.finalize();
//		request.getSession().removeAttribute(PluginFreeConst.PLUGINFREE_SESSION_KEY);
}


/***********************************************************************/
/* 공개키 얻기 */
/***********************************************************************/
else if("p".equals(mode)) {
	try{
		out.print(keypad.getKey());
	} catch(Exception e){
		e.printStackTrace();
	}
}


/***********************************************************************/
/* 키패드 스크립트 생성 */
/***********************************************************************/
else if("e".equals(mode)) {
	try {
		//System.out.println(keypad.getCode());
		out.print(keypad.getCode());
	} catch (Exception e) {
		e.printStackTrace();
	}
}


/***********************************************************************/
/* 마우스입력기 좌표 재설정 */
/***********************************************************************/
else if("s".equals(mode)) {
	try{
		out.print(keypad.getRefresh());
	} catch(Exception e){
		//e.printStackTrace();
	}
}


/***********************************************************************/
/* 키패드 치환 테이블 */
/***********************************************************************/
else if("t".equals(mode)) {
	try{
		out.print(keypad.getTable());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

	
/***********************************************************************/
/* 키패드 치환 데이터 */
/***********************************************************************/
else if("r".equals(mode)) {
	try{
		out.print(keypad.getReplacement());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

/***********************************************************************/
/* 키패드 치환 데이터 재암호(ARIA with IV) */
/***********************************************************************/
else if("c".equals(mode)) {
	try{
		out.print(keypad.getEncryptReplacement());
	} catch(Exception e){
		//e.printStackTrace();
	}
}

	
/***********************************************************************/
/* 키패드 복호화 */
/***********************************************************************/
else if("d".equals(mode)) {
	try{
		out.print(keypad.decode());
	} catch(Exception e){
		//e.printStackTrace();
	}
}
	
/***********************************************************************/
/* 마우스입력기 PKI 연동용페이지  */
/***********************************************************************/
else if("pki".equals(mode)) {
	try{
		out.print(keypad.decode());
	} catch(Exception e){
		//e.printStackTrace();
	}
}
%>