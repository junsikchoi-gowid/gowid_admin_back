package com.nomadconnection.dapp.secukeypad;

import com.nprotect.common.json.JSONParser;
import com.nprotect.pluginfree.PluginFree;
import com.nprotect.pluginfree.PluginFreeConst;
import com.nprotect.pluginfree.modules.PluginFreeRequest;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
public class SecuKeypad {

    public static Map<String, String> decrypt(HttpServletRequest request, String encryptData, String[] paramNames) {

        try {
            String str = request.getParameter(encryptData);
            Map<String, String> map = (Map<String, String>) new JSONParser().parse(str);

            HttpServletRequest decryptRequest = new PluginFreeRequest(request, map);
            loggingKeyPadSession(request);  // 키패드 세션로깅
            try {
                PluginFree.verify(decryptRequest, paramNames);
            } catch (Exception e) {
                log.error("[decrypt] $VERIFY_ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
                throw SecuKeypadException.builder().category(SecuKeypadException.Category.VERIFY_ERROR).data(e.getMessage()).build();
            }

            Map<String, String> decryptData = new HashMap<>();
            for (String param : paramNames) {
                decryptData.put(param, decryptRequest.getParameter(param));
            }

            return decryptData;

        } catch (Exception e) {
            log.error("[decrypt] $DECRYPT_ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw SecuKeypadException.builder().category(SecuKeypadException.Category.DECRYPT_ERROR).data(e.getMessage()).build();
        }
    }

    @SuppressWarnings(value = "unchecked")
    public static void loggingKeyPadSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60 * 60);
        Map<String, String> map3 = (Map<String, String>) session.getAttribute(PluginFreeConst.PLUGINFREE_SESSION_KEY);

        StringBuilder builder = new StringBuilder();
        builder.append("Print PluginFree Session Start....................................\n");

        if (map3 != null) {
            Set<String> set = map3.keySet();
            List<String> list3 = new ArrayList<String>(set);
            Collections.sort(list3);

            for (String key : list3) {
                String value1 = map3.get(key);
                builder.append("\t").append(key).append(" : ").append(value1).append("\n");
            }

        } else {
            builder.append("Print PluginFree Session is null.\n");
        }

        builder.append("Print PluginFree Session End...................................\n");
        log.debug("### PluginFree Session => {} ", builder.toString());

    }
}
