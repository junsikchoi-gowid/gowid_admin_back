package com.nomadconnection.dapp.secukeypad;

import com.nprotect.common.json.JSONParser;
import com.nprotect.pluginfree.PluginFree;
import com.nprotect.pluginfree.modules.PluginFreeRequest;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SecuKeypad {

    public static Map<String, String> decrypt(HttpServletRequest request, String encryptData, String[] paramNames) {

        try {
            String str = request.getParameter(encryptData);
            Map<String, String> map = (Map<String, String>) new JSONParser().parse(str);

            HttpServletRequest decryptRequest = new PluginFreeRequest(request, map);
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
}
