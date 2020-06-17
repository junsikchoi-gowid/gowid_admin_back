<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");
%><!doctype html>
<html>
<head>
</head>
<body>
<script>
<%--
var npFdsKey="npFdsDeviceCDS";function verifyResult(g,h){if(h==null||h==""){return g}if(h.length!=53){return g}isFound=true;try{var f=Number(h.substring(0,13));if(f>0){try{var c=Number(g.substring(0,13));if(!g||f<c){g=h}}catch(i){g=h}}}catch(i){}return g}function getSetCache(b,i,h){var a=cacheInMemory||{deviceId:""};if(i){if(i!==a.deviceId){i=verifyResult(a.deviceId,i);a.deviceId=i;var e=JSON.stringify(a);if(cookiesAreEnabled){w.localStorage.setItem(npFdsKey,e);d.cookie="__"+npFdsKey+"="+e+"; expires=Wed, 1 Jan 2020 01:26:00 GMT; path=/"}cacheInMemory=a;h&&h()}}else{if(b){h(a)}else{try{w.localStorage.removeItem(npFdsKey)}catch(c){}try{deleteCookie("__"+npFdsKey)}catch(c){}w.name="";cacheInMemory={deviceId:""};h&&h()}}}function deleteCookie(b){d.cookie=b+"; expires=Thu, 01 Jan 1970 00:00:00 UTC"}function jv(g){for(var h,f=d.cookie.split(";"),i=f.length;i--;){if(!(h=f[i].trim()).indexOf(g)){return h.slice(g.length+1)}}}function tryCatch(f,e){try{return f(e)}catch(g){}}function onMessage(b){if(b.origin===REFERRER){var a=tryCatch(JSON.parse,b.data);a&&getSetCache(a.key,a.value,function(c){b.source.postMessage(JSON.stringify({id:a.id,value:c}),REFERRER)})}}var w=window,d=document,l=w.location,nav=w.navigator,cookiesAreEnabled=nav.cookieEnabled,REFERRER=(d.referrer.match(/^.+\:\/\/[^\/]+/)||[])[0];w.addEventListener?w.addEventListener("message",onMessage,!0):w.attachEvent&&w.attachEvent("onmessage",onMessage);var json=jv("__"+npFdsKey)||w.localStorage.getItem(npFdsKey);var cacheInMemory=tryCatch(JSON.parse,json);
--%>

var npFdsKey = "npFdsDeviceCDS";

function getSetCache(key, value, callback) {
	var cache = cacheInMemory || {
		deviceId: ""
	};
	if (value) {
//		console.log("CDS : " + value);
		if(value !== cache.deviceId) {
			cache.deviceId = value;
			var f = JSON.stringify(cache);
			
			w.localStorage.setItem(npFdsKey, f);
			if(cookiesAreEnabled) {
				d.cookie = "__" + npFdsKey + "=" + f + "; expires=Wed, 1 Jan 2020 01:26:00 GMT; path=/";
			}
			cacheInMemory = cache;
			callback && callback();
		}
	} else if (key) {
		callback(cache);
	} else {
		try {
			w.localStorage.removeItem(npFdsKey)
		} catch (g) {}
		try {
			deleteCookie("__" + npFdsKey)
		} catch (g) {}
		
		w.name = "";
		cacheInMemory = {
			deviceId: ""
		};
		callback && callback()
	}
}

function deleteCookie(a) {
	d.cookie = a + "; expires=Thu, 01 Jan 1970 00:00:00 UTC"
}

function getCookie(a) {
	for (var e, b = d.cookie.split(";"), c = b.length; c--; )
		if (!(e = b[c].trim()).indexOf(a))
			return e.slice(a.length + 1)
}

function tryCatch(a, b) {
	try {
		return a(b)
	} catch (c) {}
}

function onMessage(event) {
	if (event.origin === REFERRER) {
		var json = tryCatch(JSON.parse, event.data);
		json && getSetCache(json.key, json.value, function(data) {
			event.source.postMessage(JSON.stringify({
				id: json.id,
				value: data
			}), REFERRER)
		})
	}
}

var w = window
  , d = document
  , l = w.location
  , nav = w.navigator
  , cookiesAreEnabled = nav.cookieEnabled
  , REFERRER = (d.referrer.match(/^.+\:\/\/[^\/]+/) || [])[0];

w.addEventListener ? w.addEventListener("message", onMessage, !0) : w.attachEvent && w.attachEvent("onmessage", onMessage);
var json = w.localStorage.getItem(npFdsKey) || getCookie("__" + npFdsKey);
var cacheInMemory = tryCatch(JSON.parse, json);
</script>
</body>
</html>
