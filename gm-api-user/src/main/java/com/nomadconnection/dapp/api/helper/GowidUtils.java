package com.nomadconnection.dapp.api.helper;

import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class GowidUtils {

	//
	//	todo: check mdn regex pattern
	//
	private static final Pattern PATTERN = Pattern.compile("^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$");

	public static boolean isValid(String mdn) {
		if (StringUtils.isEmpty(mdn)) {
			return false;
		}
		return PATTERN.matcher(mdn).matches();
	}

	public static Double doubleTypeGet(String str){
		Double dReturn = 0.0;

		if(str != null && !str.isEmpty()){
			dReturn = Double.parseDouble(str);
		}

		return dReturn;
	}

    public static String getEmptyStringToString(JSONObject obj, String objName) {
		if(obj.get(objName) != null ){
			return obj.get(objName).toString();
		}else{
			return "";
		}
    }

	public static String getMonth(int i) {
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.MONDAY, i);
		return df.format(cal.getTime());
	}
}
