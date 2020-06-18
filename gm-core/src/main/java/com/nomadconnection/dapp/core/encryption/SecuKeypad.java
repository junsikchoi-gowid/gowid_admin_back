package com.nomadconnection.dapp.core.encryption;

import javax.servlet.http.HttpServletRequest;

public class SecuKeypad {

	public static void decrypt(HttpServletRequest request) {
		// TODO : 가상키패드 복호화
//		try {
//			String str = request.getParameter("s");
//			Map<String, String> map = (Map<String, String>) new JSONParser().parse(str);
//			HttpServletRequest req = new PluginFreeRequest(request, map);
//			try {
//				PluginFree.verify(req, new String[]{"e2e_id", "e2e_password"});
//			} catch (Exception e) {
//				System.out.println("키보드보안/마우스입력기 복호화 검증 오류가 발생하였습니다." + e.getMessage());
//			}
//
//			String e2e_id = req.getParameter("e2e_id");
//			String e2e_password = req.getParameter("e2e_password");
//
//
//			System.out.println("============== <b>수동복호화(필터사용안함)</b> ==============<br />");
//			System.out.println("<strong>수동복호화(e2e_id) : " + e2e_id + "</strong><br />");
//			System.out.println("<strong>수동복호화(e2e_password) : " + e2e_password + "</strong><br />");
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//		}
	}
}
