package com.nomadconnection.dapp.secukeypad;

import com.nprotect.common.json.JSONParser;
import com.nprotect.pluginfree.PluginFree;
import com.nprotect.pluginfree.modules.PluginFreeRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class SecuKeypad {

	public static void decrypt(HttpServletRequest request, String paramName) {

		try {
			String str = request.getParameter(paramName);
			Map<String, String> map = (Map<String, String>) new JSONParser().parse(str);

			HttpServletRequest req = new PluginFreeRequest(request, map);
			try {
				PluginFree.verify(req, new String[]{paramName});
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("키보드보안/마우스입력기 복호화 검증 오류가 발생하였습니다." + e.getMessage());
			}


			System.out.println("============== <b>수동복호화(필터사용안함)</b> ==============<br />");
			System.out.println("<strong>수동복호화(e2e_id) : " + req.getParameter(paramName) + "</strong><br />");

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public static void setPath() {
		System.setProperty("PLUGINFREE_WEBAPP_PATH", "");
		System.setProperty("PLUGINFREE_PROPERTIES_PATH", SecuKeypad.class.getClassLoader().getResource("nprotect.properties").getPath());
	}
}
