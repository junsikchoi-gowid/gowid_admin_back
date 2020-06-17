<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <!-- <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" /> -->
    <title>nProtect Online Security v1.0.0</title>
    <style>
        body, td, th {
            font-size: 10pt
        }

        input, textarea {
            font-size: 9pt;
        }
    </style>

    <script type="text/javascript" src="/static/pluginfree/js/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="/pluginfree/jsp/nppfs.script.jsp"></script>
    <script type="text/javascript" src="/static/pluginfree/js/nppfs-1.0.0.js"></script>
    <script type="text/javascript">

        /*
 ***************************************************************************
 * nProtect Online Security, Const Variables
 ***************************************************************************
 */
        var npPfsConst = new function () {
            // 비동기작업 상태
            this.TaskState = {
                STATE_READY: 1,
                STATE_DOING: 2,
                STATE_DONE: 3
            };

            // 실행 모드
            this.RuntimeMode = {
                RELEASE: 1, DEBUG: 2
            };

            this.Keypad = new function () {
                this.types = {
                    KEYPAD: "p",
                    KEYBOARD: "b",
                    KOREAN: "k"
                };
                this.activate = {
                    FOCUS: "f",
                    RADIO: "r",
                    CHECKBOX: "c",
                    TOGGLE: "t"
                };
                this.showMode = {
                    LAYER: "l",
                    BLOCK: "b",
                    DIVISION: "d"
                };
                this.actionMode = {
                    PASSWORD: "p",
                    CONFIRM: "c",
                    ACCOUNT: "a",
                    NUMBER: "n",
                    SAFECARD: "s"
                };
            };
        };


        /*
 ***************************************************************************
 * nProtect Online Security, Configuration
 ***************************************************************************
 */
        var npPfsPolicy = new function () {
            // 제품 공통
            this.Common = {
                Protocol: "https",
                Host: "pfs.nprotect.com",
                Port: 14430,
                Range: 10,
                ContextPath: "",

                KeyUrl: "/pluginfree/jsp/nppfs.key.jsp",
                RemoveKey: "/pluginfree/jsp/nppfs.remove.jsp",
                ReadyUrl: "/pluginfree/jsp/nppfs.ready.jsp",
                KeyPadUrl: "/pluginfree/jsp/nppfs.keypad.jsp",
                InstallUrl: "/pluginfree/examples/install.jsp",
                RuntimeMode: npPfsConst.RuntimeMode.RELEASE,
                BlockDevTools: true,

                WaitTimeout: 100,
                MaxWaitCount: 100
            };

            // 제품 지원 정책(구매한 제품 여부)
            this.License = {
                FW: false,
                SK: false,
                FD: false,
                KV: true
            };


            // 제품별 정책 - 마우스입력기
            this.KV = new function () {
                this.kpdRunType = "single";		// single, dual, support
                this.kpdUseynType = "focus";		// focus, checkbox, toggle, image
                this.kpdToggleOn = "";
                this.kpdToggleOff = "";
                this.kpdUseynCreate = "none";		// none, warning, use
                this.kpdUseynForm = "";
                this.kpdShowType = "layer";		// layer, block, division
                this.kpdAutomaticWidth = "false";		// Y
            };

        };


        var npConsole = new function () {
            this.taskStart = new Date();

            this.info = function (text) {
                this.print(text, "blue");
            },
                this.log = function (text) {
                    this.print(text, "black");
                },
                this.error = function (text) {
                    this.print(text, "red");
                },
                this.split = function () {
                    var buf = [];
                    for (var i = 0; i < 80; i++) {
                        buf.push("-");
                    }
                    this.print(buf.join(""), "#ddd");
                }

            this.reset = function () {
                this.taskStart = new Date();
            };

            this.dateText = function (date) {
                if (npCommon.isNull(date)) {
                    date = new Date();
                }
                return date.format("yyyy-MM-dd HH:mm:ss ms");
            }

            this.print = function (text, color) {
                if (npCommon.isBlank(color)) {
                    color = "black";
                }
                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.RuntimeMode.DEBUG) {
                    if (window.console) {
                        window.console.log(this.dateText() + " : " + text);
                    } else {
                        jQuery("#nppfs-console-log").append("<div style=\"color:" + color + ";\">" + this.dateText() + " : " + text + "</div>");
                    }
                }
            };

            this.interval = function (prefix) {
                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.RuntimeMode.DEBUG) {
                    var start = this.taskStart;
                    var end = new Date();
                    npConsole.log("Task(" + prefix + ") Duration : " + ((end.getTime() - start.getTime()) / 1000) + "s, Start:" + start.format("HH:mm:ss ms") + ", End:" + end.format("HH:mm:ss ms"));
                }
            };
        };


        /*
 ***************************************************************************
 * nProtect Online Security, User Information
 ***************************************************************************
 */
        // 접속자 정보
        var npPfsDefine = new function () {
            this.ie = (navigator.appName == 'Microsoft Internet Explorer'
                    || (navigator.appName == "Netscape" && (
                        navigator.userAgent.indexOf("MSIE") != -1
                        || navigator.userAgent.indexOf("Trident") >= 0)
                    )
                )
                && (navigator.userAgent.indexOf('QQBrowser') == -1);
            this.ie64 = this.ie && (navigator.userAgent.indexOf('Win64; x64') != -1);
            this.ff = (navigator.userAgent.indexOf('Mozilla') == 0)
                && (navigator.appName == 'Netscape') && (navigator.userAgent.indexOf('Navigator') == -1)
                && (navigator.userAgent.lastIndexOf('Firefox') > -1);
            this.ns = (navigator.userAgent.lastIndexOf('Gecko') > -1)
                && (navigator.userAgent.indexOf('Navigator') > -1);
            this.b360 = (navigator.userAgent.match('360Browser') == '360Browser')
                && (navigator.userAgent.match('Chrome') == 'Chrome')
                && (navigator.userAgent.match('Safari') == 'Safari');
            this.qq = (navigator.userAgent.match('QQBrowser') == 'QQBrowser')
                && (navigator.userAgent.indexOf("Trident") >= 0);
            this.sf = (navigator.userAgent.lastIndexOf('Safari') != -1)
                && (navigator.userAgent.lastIndexOf('Chrome') != -1) == false;
            this.op = (navigator.userAgent.lastIndexOf('Opera') != -1 || navigator.userAgent.lastIndexOf('OPR') != -1);
            this.cr = (navigator.userAgent.match('Chrome') == 'Chrome')
                && (navigator.userAgent.match('Safari') == 'Safari')
                && (navigator.userAgent.lastIndexOf('OPR') != -1 || navigator.userAgent.lastIndexOf('360Browser') != -1) == false;
            this.win = (navigator.platform.toLowerCase().indexOf('win') != -1)
                && (navigator.userAgent.toLowerCase().indexOf('windows phone') == -1),
                this.win98 = (window.navigator.userAgent.indexOf("Windows 98") >= 0 || window.navigator.userAgent.indexOf("Win98") >= 0);
            this.winme = (window.navigator.userAgent.indexOf("Windows ME") >= 0);
            this.winnt40 = (window.navigator.userAgent.indexOf("Windows NT 4.0") >= 0);
            this.win64 = (navigator.platform.toLowerCase().indexOf('win64') != -1)
                && (navigator.userAgent.toLowerCase().indexOf('windows phone') == -1);
            this.win9x = (navigator.userAgent.indexOf('Windows 98') != -1)
                || (navigator.userAgent.indexOf('Win98') != -1)
                || (navigator.userAgent.indexOf('Windows ME') != -1)
                || (navigator.userAgent.indexOf('Windows NT 4.0') != -1)
                || (navigator.userAgent.indexOf('Windows NT 5.0') != -1)	// Windows 2K
                || (navigator.userAgent.indexOf('Windows 2000') != -1);
            this.mac = (navigator.userAgent.indexOf('Mac') != -1);
            this.lnx64 = ((navigator.userAgent.indexOf('Linux') != -1)
                && (navigator.userAgent.toLowerCase().indexOf('x86_64') != -1));
            this.lnx32 = ((navigator.userAgent.indexOf('Linux') != -1)
                && ((navigator.userAgent.toLowerCase().indexOf('i386') != -1)
                    || (navigator.userAgent.toLowerCase().indexOf('i686') != -1)));
            this.lnx = (navigator.userAgent.indexOf('Linux') != -1);
            this.and = (navigator.userAgent.match('Android') == 'Android');
            this.iph = (navigator.userAgent.match('iPhone') == 'iPhone');
            this.ipo = (navigator.userAgent.match('iPod') == 'iPod');
            this.ipa = (navigator.userAgent.match('iPad') == 'iPad');
            this.fdr = (navigator.userAgent.toLowerCase().indexOf('fedora') != -1);
            this.ubt = (navigator.userAgent.toLowerCase().indexOf('ubuntu') != -1);
            this.winmob = (navigator.platform == 'Windows Mobile');
            this.winphone = (navigator.userAgent.toLowerCase().indexOf('windows phone') != -1);


            this.osVersion = null;
            this.browserVersion = null;

            this.getOsVersion = function () {
                var version = null;
                var ua = navigator.userAgent;
                if (npPfsDefine.win) {
                    var css = [
                        {v: "5.0", p: /(Windows NT 5.1|Windows XP)/},
                        {v: "5.2", p: /Windows NT 5.2/},
                        {v: "6.0", p: /Windows NT 6.0/},
                        {v: "7.0", p: /(Windows 7|Windows NT 6.1)/},
                        {v: "8.1", p: /(Windows 8.1|Windows NT 6.3)/},
                        {v: "8.0", p: /(Windows 8|Windows NT 6.2)/},
                        {v: "3.0", p: /Windows CE/},
                        {v: "3.1", p: /Win16/},
                        {v: "3.2", p: /(Windows 95|Win95|Windows_95)/},
                        {v: "3.5", p: /(Win 9x 4.90|Windows ME)/},
                        {v: "3.6", p: /(Windows 98|Win98)/},
                        {v: "3.7", p: /Windows ME/},
                        {v: "4.0", p: /(Windows NT 4.0|WinNT4.0|WinNT|Windows NT)/},
                        {v: "4.0", p: /(Windows NT 5.0|Windows 2000)/}
                    ];
                    for (var id in css) {
                        var cs = css[id];
                        if (cs.p.test(ua)) {
                            version = cs.v;
                            break;
                        }
                    }
                } else if (npPfsDefine.mac) {
                    if (/Mac OS X 10_5/.test(ua) || /Mac OS X 10.5/.test(ua)) {
                        version = "10.5";
                    } else if (/Mac OS X 10_6/.test(ua) || /Mac OS X 10.6/.test(ua)) {
                        version = "10.6";
                    } else if (/Mac OS X 10_7/.test(ua) || /Mac OS X 10.7/.test(ua)) {
                        version = "10.7";
                    } else if (/Mac OS X 10_8/.test(ua) || /Mac OS X 10.8/.test(ua)) {
                        version = "10.8";
                    } else if (/Mac OS X 10_9/.test(ua) || /Mac OS X 10.9/.test(ua)) {
                        version = "10.9";
                    } else if (/Mac OS X 10_10/.test(ua) || /Mac OS X 10.10/.test(ua)) {
                        version = "10.10";
                    }
                } else if (npPfsDefine.lnx) {
//			if (ua.match('Linux')) {
//			if (ua.match('Fedora')) {
//				if (match = /Fedora\/[0-9\.\-]+fc([0-9]+)/.exec(ua)) {
//					version = match[1];
//				}
//			} else if (ua.match('Ubuntu')) {
//				if (match = /Ubuntu\/([0-9.]*)/.exec(ua)) {
//					version = match[1];
//				}
//			}
                }
                //npConsole.log("OS Version : [" + version + "]");
                return version;
            };

            this.getBwVersion = function () {
                var version;
                var ua = navigator.userAgent;

                if (npPfsDefine.ff) {
                    var version = ua.substring(ua.toLowerCase().lastIndexOf("firefox"));
                    if (version.indexOf(" ") > -1) {
                        version = version.substring(0, version.indexOf(" "));
                    }
                    var temp = version.split("/");
                    return temp[1];
                } else if (npPfsDefine.op) {
                    if (ua.lastIndexOf(" ") < ua.lastIndexOf("/")) {
                        version = ua.substring(ua.lastIndexOf(" "));
                        var temp = version.split("/");
                        return temp[1];
                    }
                } else if (npPfsDefine.b360) {
                    version = ua.substring(ua.toLowerCase().lastIndexOf("chrome"));
                    if (version.indexOf(" ") != -1) {
                        version = version.substring(0, version.indexOf(" "));
                        var temp = version.split("/");
                        return temp[1];
                    }
                } else if (npPfsDefine.cr) {
                    if (ua.lastIndexOf(" ") < ua.lastIndexOf("/")) {
                        version = ua.substring(ua.toLowerCase().lastIndexOf("chrome"));
                        var temp = version.split(" ");
                        temp = temp[0].split("/");
                        return temp[1];
                    }
                } else if (npPfsDefine.sf) {
                    var reSF = new RegExp(/Version[\/\s](\d+\.\d+)/.test(navigator.userAgent));
                    var bwVer = RegExp["$1"];
                    return bwVer;
                } else if (npPfsDefine.ie || npPfsDefine.qq) {
                    if (ua.indexOf("MSIE") > -1) {
                        tua = ua.substring(ua.indexOf("MSIE") + 4, ua.length);
                        tua = tua.replace(/(^\s*)|(\s*$)/gi, "");
                        var temp = tua.split(";");
                        temp = temp[0].split(" ");
                        return temp[0];
                    } else {//IE11
                        return ua.substring(ua.indexOf("rv:") + 3, ua.indexOf("rv:") + 7);
                    }
                }
                //npConsole.log("Bw Version : [" + version + "]");
            };

            this.IsSupported = function (version) {
                return this.IsSupportedOs(version) && this.IsSupportedBw(version);
            };

            this.IsSupportedOs = function (version) {
                if (npCommon.isBlank(this.osVersion)) {
                    this.osVersion = npPfsDefine.getOsVersion();
                }
                var osVersion = this.osVersion;
                if (npPfsDefine.win && version.WIN.Support) {
                    if (npPfsDefine.win9x) {
                        return false;
                    }
                    return npCommon.checkVersion(osVersion, version.WIN.Os.Min, version.WIN.Os.Max);
                } else if (npPfsDefine.mac && version.MAC.Support) {
                    return npCommon.checkVersion(osVersion, version.MAC.Os.Min, version.MAC.Os.Max);
                } else if (npPfsDefine.lnx && version.LINUX.Support) {
                    return true;
                }

                return false;
            };

            this.IsSupportedBw = function (ver) {
                if (!this.IsSupportedOs(ver)) {
                    return false;
                }

                var version = null;
                if (npPfsDefine.win) {
                    version = ver.WIN.Bw;
                } else if (npPfsDefine.mac) {
                    version = ver.MAC.Bw;
                } else if (npPfsDefine.lnx) {
                    version = ver.LINUX.Bw;
                }

                if (!npCommon.isNull(version)) {
                    if (npCommon.isBlank(this.browserVersion)) {
                        this.browserVersion = npPfsDefine.getBwVersion();
                    }
                    var browserVersion = npPfsDefine.browserVersion;
                    if (npPfsDefine.ie && version.IE.Support) {
                        return npCommon.checkVersion(browserVersion, version.IE.Min, version.IE.Max);
                    } else if (npPfsDefine.ff && version.FF.Support) {
                        return npCommon.checkVersion(browserVersion, version.FF.Min, version.FF.Max);
                    } else if (npPfsDefine.cr && version.CR.Support) {
                        return npCommon.checkVersion(browserVersion, version.CR.Min, version.CR.Max);
                    } else if (npPfsDefine.sf && version.SF.Support) {
                        return npCommon.checkVersion(browserVersion, version.SF.Min, version.SF.Max);
                    } else if (npPfsDefine.op && version.OP.Support) {
                        return npCommon.checkVersion(browserVersion, version.OP.Min, version.OP.Max);
                    } else if (npPfsDefine.b360 && version.B360.Support) {
                        return npCommon.checkVersion(browserVersion, version.B360.Min, version.B360.Max);
                    } else if (npPfsDefine.qq && version.QQ.Support) {
                        return npCommon.checkVersion(browserVersion, version.QQ.Min, version.QQ.Max);
                    }
                }

                return false;
            }

        };

        npPfsDefine.osVersion = npPfsDefine.getOsVersion();
        npPfsDefine.browserVersion = npPfsDefine.getBwVersion();

        var npPacket = new function () {
            this.Product = {
                DM: "223e3a4384eef95d0ca826197209f3e1a8a30009e3fe2ff802b758d850f9661c"


            };

            this.CustomerId = "68a595f34a53100c1d153c6313716ad3d7cb99f172221785be54f949329e30f1";

            this.Sync = {
                Sync: "1",
                Async: "0"
            };

            this.Daemon = {
                Command: {
                    Handshake: "a0c69321b4a87d17351a283f7c7c0d2d30c19b5136221423981f82a39cbfaf37"
                },
                Parameter: {},
                Result: {
                    TRUE: "59615036FA2C1A9EFC35D43EC6C77269",
                    FALSE: "B303AA8350126650FCE9111D899E21F0"
                }
            };
        };


        var npTransaction = new function () {
            this.jobs = {};

            this.Job = function (name, state) {
                this.tasks = {};
                this.name = name;
                this.state = state || npPfsConst.TaskState.STATE_READY;
                this.data;
                this.waitCount = 0;
            };

            this.Job.prototype = {
                addTask: function (task) {
                    this.tasks[task.name] = task;
                },
                setData: function (data) {
                    this.data = data;
                },
                getData: function () {
                    return this.data;
                },
                setState: function (state) {
                    this.state = state;
                },
                getState: function () {
                    return this.state;
                },
                getTask: function (name) {
                    return this.tasks[name];
                },
                isComplete: function () {
                    var isComplete = true;
                    var keys = npCommon.GetKeys(this.tasks);
                    if (keys.length > 0) {
                        for (var index = 0; index < keys.length; index++) {
                            var task = this.getTask(keys[index]);
                            isComplete = isComplete && ((task) ? task.isComplete() : false);
                        }
                        if (isComplete == true) {
                            this.setState(npPfsConst.TaskState.STATE_DONE);
                        }
                    } else {
                        isComplete = (this.state == npPfsConst.TaskState.STATE_DONE);
                    }

                    return isComplete;
                },
                print: function () {
                    npConsole.log("----> Job " + this.name + ", " + this.isComplete());

                    var keys = npCommon.GetKeys(this.tasks);
                    if (keys.length > 0) {
                        for (var index = 0; index < keys.length; index++) {
                            var task = this.getTask(keys[index]);
                            task.print();
                        }
                    } else {
                        npConsole.log("----> Task is empty..");
                    }
                },
                destory: function (task) {
                    npConsole.log("destory task(" + task + ").....");
                    if (this.getTask(task)) {
                        this.getTask(task).destoryAll();
                        delete this.tasks[task];
                    }
                },
                destoryAll: function (task) {
                    var keys = npCommon.GetKeys(this.tasks);
                    if (keys.length > 0) {
                        for (var index = 0; index < keys.length; index++) {
                            this.destory(keys[index]);
                        }
                    }
                }
            }


            this.addJob = function (job) {
                this.jobs[job.name] = job;
            };

            this.getJob = function (key) {
                return this.jobs[key];
            };

            this.isComplete = function (key) {
                return (this.jobs[key]) ? this.jobs[key].isComplete() : false;
            };

            this.waitRun = function (job, callback, delay) {
                if (typeof (job) === "function") {
                    job();
                    return;
                }

                if (npCommon.isNull(delay)) {
                    deley = 100;
                }

                var isComplete = false;
                if (typeof (job) === "string") {
                    isComplete = npTransaction.isComplete(job);
                } else if (typeof (job) === "object") {
                    if (job.length > 0) {
                        isComplete = true;
                        for (var idx = 0; idx < job.length; idx++) {
                            isComplete = isComplete && npTransaction.isComplete(job[idx]);
                        }
                    } else {
                        isComplete = true;
                    }
                }

                if (!isComplete) {
                    npConsole.log("wait for " + job + "...");
                    job.waitCount++;
                    if (job.waitCount > npPfsPolicy.Common.MaxWaitCount) {
                        npConsole.log("job " + job + " is stop by max wait count...");
                        return;
                    }
                    setTimeout(function () {
                        npTransaction.waitRun(job, callback, delay);
                    }, delay);
                    return;
                }

                callback();
            };


            this.destory = function (job) {
                npConsole.log("destory job(" + job + ").....");
                if (this.jobs[job]) {
                    this.jobs[job].destoryAll();
                }
                delete this.jobs[job];
            };

            this.destoryAll = function () {
                var keys = npCommon.GetKeys(this.jobs);
                for (var index = 0; index < keys.length; index++) {
                    npTransaction.destory(keys[index]);
                }
            };

            this.print = function () {
                //npConsole.log("print transaction..............");
                var keys = npCommon.GetKeys(this.jobs);
                if (keys.length > 0) {
                    for (var index = 0; index < keys.length; index++) {
                        var job = this.getJob(keys[index]);
                        job.print();
                    }
                } else {
                    npConsole.log("Job is empty..");
                }
            };
        };


        var npCommon = new function () {
            function Padder(len, pad) {
                if (len === undefined) {
                    len = 1;
                } else if (pad === undefined) {
                    pad = '0';
                }

                var pads = '';
                while (pads.length < len) {
                    pads += pad;
                }

                this.pad = function (what) {
                    var s = what.toString();
                    return pads.substring(0, pads.length - s.length) + s;
                };
            }

            this.isNull = function (data) {
                if (typeof (data) == "undefined" || data == null) {
                    return true;
                }
                return false;
            };

            this.isBlank = function (data) {
                if (typeof (data) == "undefined" || data == null || data == "") {
                    return true;
                }
                return false;
            };

            this.makeUuid = function () {
                var datalength = new Padder(15);
                return datalength.pad((new Date().getTime()).toString());
            }

            this.makeLength = function (text) {
                if (this.isNull(text)) {
                    text = "";
                }
                var length = text.length;
                var datalength = new Padder(4);
                return datalength.pad((length).toString(16));
            }

            this.GetKeys = function (obj) {
                var keys = [];
                for (var key in obj) {
                    keys.push(key);
                }
                return keys;
            }

            this.CountKeys = function (obj) {
                this.GetKeys(obj).length;
            }

            this.byteArrayToHex = function (byteArray) {
                var result = "";
                if (!byteArray) {
                    return;
                }
                for (var i = 0; i < byteArray.length; i++) {
                    result += ((byteArray[i] < 16) ? "0" : "")
                        + byteArray[i].toString(16);
                }
                return result;
            };

            this.hexToString = function (hex) {
                var r = '';
                if (hex.indexOf("0x") == 0 || hex.indexOf("0X") == 0) {
                    hex = hex.substr(2);
                }
                if (hex.length % 2) {
                    hex += '0';
                }
                for (var i = 0; i < hex.length; i += 2) {
                    r += String.fromCharCode(parseInt(hex.slice(i, i + 2), 16));
                }
                return r;
            };

            this.ajaxRequest = function () {
                var http = null;
                try {
                    if (window.XMLHttpRequest) {
                        http = new XMLHttpRequest();
                    } else if (window.ActiveXObject) {
                        http = new ActiveXObject("MSXML2.XMLHTTP");
                        if (!http) {
                            http = new ActiveXObject("Microsoft.XMLHTTP");
                        }
                    }
                } catch (e) {
                }
                return http;
            };

            this.send = function (url, query, options) {
                if (this.isNull(options)) {
                    options = {};
                }
                if (this.isNull(options.async)) {
                    options.async = false;		/* 변경하면 안됨, WAS와 통신하는 로직은 동기식으로 처리.. */
                }
                if (this.isNull(options.timeout) || options.timeout <= 0) {
                    options.timeout = 1000;
                }
                if (this.isNull(options.callback)) {
                    options.callback = function (xhr) {
                        var result = "";
                        if (xhr.readyState == 4) {
                            if (xhr.status == 200) {
                                result = xhr.responseText;
                            } else {
                                //npConsole.log("Can not connect to remote address(" + url + ").");
                            }
                        } else {
                            //npConsole.log("["+xhr.readyState+"] Statement error.");
                        }
                        return result;
                    };
                }

                npConsole.log("REQ : " + query);

                var result = "";
                var xhr = this.ajaxRequest();
                if (typeof (xhr) != "undefined" && xhr != null) {

                    if (typeof (xhr.onprogress) == "object") {
                        xhr.onload = xhr.onerror = xhr.onabort = function () {
                            result = options.callback(xhr);
                        };
                    } else {
                        xhr.onreadystatechange = function () { // CallBack 함수 지정
                            result = options.callback(xhr);
                        };
                    }
                    xhr.ontimeout = function () {
                        //npConsole.error("The request for " + url + " timed out.");
                        result = options.callback(xhr);
                    };

                    var timestamp = new Date().getTime();

                    xhr.open("post", url, options.async);
                    //xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charaset=UTF-8");
                    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

                    try {
                        if (!this.isBlank(query)) {
                            xhr.send(query);
                        } else {
                            xhr.send();
                        }
                    } catch (e) {
                        result = options.callback(xhr);
                        npConsole.error("Socket Timeout.");
                    }
                } else {
                    npConsole.error("This browser is not support Ajax.");
                }

                return result;
            };


            this.requestState = {};
            this.sendCommand = function (command, options) {
                if (npCommon.isNull(options)) {
                    options = {};
                }
                if (npCommon.isNull(options.async)) {
                    //options.async = false;
                    options.async = true;
                }
                if (npCommon.isNull(options.timeout) || options.timeout <= 0) {
                    options.timeout = 1000;
                }
                if (npCommon.isNull(options.callback)) {
                    options.callback = function (data) {
                        //npConsole.log("Default Callback [" + data + "]");
                    };
                }

                var callback = function (xhr) {
                    var result = "";
                    if (xhr.readyState == 4) {
                        if (xhr.status == 200) {
                            npConsole.log("RES : " + xhr.responseText);
                            result = xhr.responseText;
                            options.callback(result);
                        } else {
                            //npConsole.log("Can not connect to remote address.");
                        }
                    } else {
                        //npConsole.log("["+xhr.readyState+"] Statement error.");
                    }
                    return result;
                };

                npConsole.log("REQ : " + command);
                if (npPfsDefine.ie && npCommon.compareVersion(9.0, npPfsDefine.browserVersion)) {
//		if((npPfsDefine.ie && npCommon.compareVersion(9.0, npPfsDefine.browserVersion)) || (npPfsDefine.op && npPfsDefine.lnx)){
                    return this.sendAsyncCommand(command, callback, options);
                } else {
                    return this.sendSyncCommand(command, callback, options);
                }
            };

            this.sendSyncCommand = function (command, callback, options) {
                var url = npPfsCtrl.makeUrl(options.port);
                var result = "";
                var xhr = this.ajaxRequest();
                if (typeof (xhr) != "undefined" && xhr != null) {
                    if (typeof (xhr.onprogress) == "object") {
                        xhr.onload = xhr.onerror = xhr.onabort = function () {
                            result = callback(xhr);
                        };
                    } else {
                        xhr.onreadystatechange = function () {  // CallBack 함수 지정
                            result = callback(xhr);
                        };
                    }
                    xhr.ontimeout = function () {
                        npConsole.error("The request for " + url + " timed out.");
                        result = callback(xhr);
                    };

                    try {
                        xhr.open("post", url, options.async);
                        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                        //npConsole.log("Request : " + command);
                        if (!this.isBlank(command)) {
                            xhr.send("code=" + command);
                            //xhr.send("" + command);
                        } else {
                            xhr.send();
                        }
                    } catch (err) {
                        npConsole.log(err);
                    }
                } else {
                    npConsole.log("This browser is not support Ajax.");
                }

                return result;
            };

            this.sendAsyncCommand = function (command, callback, options) {
                var url = npPfsCtrl.makeUrl(options.port);
                $.ajax({
                    url: url,
                    crossDomain: true,
                    async: false,
                    type: "GET",
                    dataType: "jsonp",
                    jsonp: "jsonp_callback",
                    contentType: "application/json",
                    data: {
                        Code: command
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        if ("parsererror" == textStatus) {
                            npConsole.log("Unknown data format.");
                        }
                        callback({readyState: 4, status: 999, responseText: ""});
                    },
                    success: function (data, textStatus, xhr) {
                        if (data != null && data.RESULT != null) {
                            npConsole.log("RES : " + data.RESULT);
                            callback({readyState: 4, status: 200, responseText: data.RESULT});
                        }
                    }, complete: function () {
                    }
                });
            };

            this.findElement = function (elename, formname) {
                if (typeof (formname) != "undefined" && typeof (document.forms[formname]) != "undefined") {
                    try {
                        if (typeof (document.forms[formname][elename]) != "undefined") {
                            return document.forms[formname][elename];
                        }

                        var elements = document.getElementsByName(elename);
                        if (elements.length > 0) {
                            for (i = 0; i < elements.length; i++) {
                                if (elements[i].form.name == formname) return elements[i];
                            }
                            return null;
                        }
                        if (document.getElementById(elename)) {
                            return document.getElementById(elename);
                        } else return null;
                    } catch (e) {
                        return null;
                    }
                }
                if (document.getElementsByName(elename)[0]) {
                    return document.getElementsByName(elename)[0];
                } else if (document.getElementById(elename)) {
                    return document.getElementById(elename);
                } else return null;
            }

            this.createElement = function (obj, arraylist) {
                if (this.isNull(obj)) {
                    npConsole.log("object is not defined");
                    return;
                }

                var formname = (typeof (obj.name) != "undefined" && obj.name != "") ? obj.name : null;
                var div4ie7 = npCommon.findDivision(form, "byclass", "nppfs-elements");
                if (npCommon.isNull(div4ie7)) {
                    //npConsole.log("In IE7, div[class=\"nppfs-elements\"] in form(" + formname + ") is required.");
                    //alert("In IE7, div[class=\"nppfs-elements\"] in form(" + formname + ") is required.");
                    return;
                }

                var arrInputList = arraylist;
                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.RuntimeMode.RELEASE) {
                    if (npPfsDefine.ie && npCommon.compareVersion(7.0, npPfsDefine.browserVersion)) {
                        var html = [];
                        for (var i = 0; i < arrInputList.length; i++) {
                            var element = this.findElement(arrInputList[i], formname);
                            if (npCommon.isNull(element)) {
                                html.push("<input type=\"text\" name=\"" + arrInputList[i] + "\" style=\"display:none;\" />");
                                //html.push("<input type=\"text\" id=\"" + arrInputList[i] + "\" name=\"" + arrInputList[i] + "\" style=\"display:none;\" />");
                                //html.push("<input type=\"text\" id=\"" + arrInputList[i] + "\" name=\"" + arrInputList[i] + "\" />");
                            }
                        }

                        div4ie7.style.display = "none";
                        div4ie7.innerHTML = div4ie7.innerHTML + html.join("\n");
                        //console.log(div4ie7.innerHTML);
                    } else {
                        for (var i = 0; i < arrInputList.length; i++) {
                            var element = this.findElement(arrInputList[i], formname);
                            if (npCommon.isNull(element)) {
                                var input = document.createElement("input");
                                input.setAttribute("type", "text");
                                input.setAttribute("style", "display:none;");
                                //input.setAttribute("id",arrInputList[i]);
                                input.setAttribute("name", arrInputList[i]);
                                obj.appendChild(input);
                                input.style.display = "none";
                            }
                        }
                    }

                } else {
                    var appendNode = false;
                    for (var i = 0; i < arrInputList.length; i++) {
                        var element = this.findElement(arrInputList[i], formname);
                        if (npCommon.isNull(element)) {
                            appendNode = true;
                            break;
                        }
                    }

                    if (!appendNode) {
                        return;
                    }

                    if (npPfsDefine.ie && npCommon.compareVersion(7.0, npPfsDefine.browserVersion)) {
                        var html = [];
                        html.push("<table border=\"1\ cellpadding=\"1\" cellspacing=\"1\">");
                        for (var i = 0; i < arrInputList.length; i++) {
                            var element = this.findElement(arrInputList[i], formname);
                            if (npCommon.isNull(element)) {
                                html.push("<tr>");
                                html.push("<td><span>" + arrInputList[i] + " : </span></td>");
                                html.push("<td><input type=\"text\" name=\"" + arrInputList[i] + "\" size=\"50\"/></td>");
                                //html.push("<td><input type=\"text\" id=\"" + arrInputList[i] + "\" name=\"" + arrInputList[i] + "\" size=\"50\"/></td>");
                                html.push("</tr>");
                            }
                        }
                        html.push("</table>");

                        div4ie7.style.display = "block";
                        div4ie7.innerHTML = div4ie7.innerHTML + html.join("\n");
                        //console.log(div4ie7.innerHTML);
                    } else {
                        var table = document.createElement("table");
                        table.setAttribute("border", "1");
                        table.setAttribute("cellpadding", "1");
                        table.setAttribute("cellspacing", "1");

                        for (var i = 0; i < arrInputList.length; i++) {
                            var element = this.findElement(arrInputList[i], formname);
                            if (npCommon.isNull(element)) {
                                var tr = document.createElement("tr");
                                //tr.setAttribute("id", "debug_tr_" + arrInputList[i]);
                                tr.setAttribute("name", "debug_tr_" + arrInputList[i]);

                                var td1 = document.createElement("td");
                                var span = document.createElement("span");
                                span.innerHTML = arrInputList[i] + " : ";
                                td1.appendChild(span);

                                var td2 = document.createElement("td");
                                var input = document.createElement("input");
                                input.setAttribute("type", "text");
                                //input.setAttribute("id",arrInputList[i]);
                                input.setAttribute("name", arrInputList[i]);
                                input.setAttribute("size", 100);
                                td2.appendChild(input);

                                tr.appendChild(td1);
                                tr.appendChild(td2);

                                table.appendChild(tr);
                            }
                        }

                        obj.appendChild(table);
                    }
                }
            };

            this.createDivision = function (parent, type, element) {
                var div = document.createElement("div");
                if (type == "byclass") {
                    div.setAttribute("class", element);
                } else {
                    parent = document.body;
                    div.setAttribute("id", element);
                }
                if (npPfsDefine.ie && npCommon.compareVersion(7.0, npPfsDefine.browserVersion)) {
                    alert("In IE7 or older, can not create dynamic DOM Division[" + element + "] element.")
                } else {
                    parent.appendChild(div);
                }

                return div;
            }

            this.findDivision = function (parent, type, element) {
                var div = null;
                if (npCommon.isBlank(element)) {
                    return div;
                }
                if (npCommon.isNull(parent)) {
                    parent = document;
                }
                if (npCommon.isBlank(type)) {
                    type = "byclass";
                }
                if (type == "byclass") {
                    var elements = parent.getElementsByTagName("div");
                    for (var idx = 0; idx < elements.length; idx++) {
                        var css = elements[idx].getAttribute("class");
                        if (npCommon.isBlank(css) && !npCommon.isNull(elements[idx].attributes["class"])) {
                            css = elements[idx].attributes["class"].nodeValue;
                        }
                        if (npCommon.isBlank(css)) {
                            continue;
                        }
                        if (css.indexOf(element) >= 0) {
                            div = elements[idx];
                            break;
                        }
                    }
                } else {
                    div = parent.getElementById(element);
                }
                if (npCommon.isNull(div)) {
                    div = this.createDivision(parent, type, element);
                }
                return div;
            }


            this.getElapsedTime = function (startTime) {
                var ret = -1;
                try {
                    var endTime = new Date();
                    ret = endTime - startTime;
                    if (ret > 100000) ret = 99999;
                } catch (e) {
                }
                return ret + "";
            };


            this.splitVersion = function (strData) {
                var arr = strData.split(/ |,|\.|\_|\//g);
                var ret = new Array();
                var arrIndex = 0;
                for (var index = 0; index < arr.length; index++) {
                    if (typeof (arr[index]) != "undefined" && arr[index] != "") {
                        ret[arrIndex] = arr[index];
                        arrIndex++;
                    }
                }
                if (ret.length > 0) return ret;

                return null;
            };

            this.compareVersion = function (strOrgVer, strVer, strSplitData) {
                if (this.isBlank(strOrgVer)) return false;
                if (this.isBlank(strVer)) return false;
                //if(this.isBlank(strSplitData)) return false;

                var orgVerArr = this.splitVersion("" + strOrgVer);
                var orgVerArrLen = orgVerArr.length;
                var verArr = this.splitVersion("" + strVer);

                var i = 0;
                for (i = 0; i < orgVerArrLen; i++) {
                    if (typeof verArr[i] == "undefined") verArr[i] = 0;
                    orgVerArr[i] = parseInt(orgVerArr[i], 10);
                    verArr[i] = parseInt(verArr[i], 10);

                    if (orgVerArr[i] > verArr[i]) {
                        return true;
                    } else if (orgVerArr[i] < verArr[i]) {
                        return false;
                    }
                }
                if (i == orgVerArrLen) {
                    return true;
                }

                if (orgVerArr.toString() == verArr.toString()) {
                    return true;
                }
                return false;
            };

            this.checkVersion = function (ver, min, max) {
                var result = true;
                if (!npCommon.isBlank(min)) {
                    result = result && npCommon.compareVersion(ver, min);
                }
                if (!npCommon.isBlank(max)) {
                    result = result && npCommon.compareVersion(max, ver);
                }
                return result;
            };

            this.setCookie = function (cName, cValue, cDay) {
                var expire = new Date();
                expire.setDate(expire.getDate() + cDay);
                cookies = cName + '=' + escape(cValue) + '; path=/ '; // 한글 깨짐을 막기위해 escape(cValue)를 합니다.
                if (typeof cDay != 'undefined') cookies += ';expires=' + expire.toGMTString() + ';';
                document.cookie = cookies;
            };

            this.getCookie = function (cName) {
                cName = cName + '=';
                var cookieData = document.cookie;
                var start = cookieData.indexOf(cName);
                var cValue = '';
                if (start != -1) {
                    start += cName.length;
                    var end = cookieData.indexOf(';', start);
                    if (end == -1) end = cookieData.length;
                    cValue = cookieData.substring(start, end);
                }
                return unescape(cValue);
            };


            this.show = function (obj) {
                if (this.isBlank(obj)) {
                    return;
                }
                if (typeof obj == "object") {
                    try {
                        obj.style.display = "block";
                    } catch (e) {
                    }
                    ;
                } else {
                    try {
                        if (npCommon.findElement(obj)) {
                            npCommon.findElement(obj).style.display = "block";
                        } else {
                        }
                    } catch (e) {
                    }
                    ;
                }
            };

            this.hide = function (obj) {
                if (this.isBlank(obj)) {
                    return;
                }
                if (typeof obj == "object") {
                    try {
                        obj.style.display = "none";
                    } catch (e) {
                    }
                    ;
                } else {
                    try {
                        if (npCommon.findElement(obj)) {
                            npCommon.findElement(obj).style.display = "none";
                        }
                    } catch (e) {
                    }
                    ;
                }
            };

            this.val = function (element, value) {
                if (typeof (element) != "undefined" && element != null && typeof (element) == "object") {
                    if (typeof (value) == "undefined") {
                        return element.value || "";
                    } else {
                        element.value = value;
                    }
                }
            }

            this.readOnly = function (element, value) {
                if (typeof (element) != "undefined" && element != null && typeof (element) == "object") {
                    if (typeof (value) == "undefined") {
                        return element.readOnly || false;
                    } else {
                        element.readOnly = value;
                    }
                }
            }

            /************************************************************
             * 이벤트 처리
             ***********************************************************/
            this.addEvent = function (eventName, target, func, args) {
//		$(target).bind(eventName, function(){func(args);});
                try {
                    if (target.addEventListener) {
                        target.addEventListener(eventName, func, false);
                    } else if (target.attachEvent) {
                        target.attachEvent('on' + eventName, func);
                    }
                } catch (e) {
                }
            };

            this.removeEvent = function (eventName, target, func, args) {
                try {
                    if (target.removeEventListener) {
                        target.removeEventListener(eventName, func, false);
                    } else if (target.detachEvent) {
                        target.detachEvent('on' + eventName, func);
                    }
                } catch (e) {
                }
            };

            this.addLoadEvent = function (func, args) {
                //var oldonload = window.onload;
                var exec = function () {
                    if (typeof (args) != "undefined" && args != null) {
                        func(args);
                    } else {
                        func();
                    }
                };
                //if(typeof oldonload == "function") { setTimeout(oldonload, 500); window.onload=null; }
                if (typeof (jQuery) == "undefined") {
                    //console.log("add load event.....");
                    setTimeout(exec, 500);
                } else {
                    //console.log("add load event by jquery.....");
                    jQuery(document).ready(function () {
                        exec();
                    });
                }
            };

            this.u8d = function (utftext) {
                var string = "";
                var i = 0;
                var c = c1 = c2 = 0;
                while (i < utftext.length) {
                    c = utftext.charCodeAt(i);
                    if (c < 128) {
                        string += String.fromCharCode(c);
                        i++;
                    } else if ((c > 191) && (c < 224)) {
                        c2 = utftext.charCodeAt(i + 1);
                        string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                        i += 2;
                    } else {
                        c2 = utftext.charCodeAt(i + 1);
                        c3 = utftext.charCodeAt(i + 2);
                        string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                        i += 3;
                    }
                }
                return string;
            }

            this.dispatch = function (fn, args) {
                fn = (typeof fn == "function") ? fn : window[fn];
                return fn.apply(this, args || []);
            }

            /************************************************************
             * Elements의 위치얻기
             ***********************************************************/
            this.getBounds = function (obj, formname) {
                var locations = new Object();

                if (typeof obj == "undefined") {
                    return;
                }
                if (obj == "") {
                    return;
                }
                if (typeof obj != "object" && obj != "") {
                    obj = npCommon.findElement(obj, formname);
                }

                if (obj != null && obj != undefined) {
                    if (obj.getBoundingClientRect) { //IE, FF3
                        var rect = obj.getBoundingClientRect();
                        locations.left = rect.left + (document.documentElement.scrollLeft || document.body.scrollLeft);
                        locations.top = rect.top + (document.documentElement.scrollTop || document.body.scrollTop);
                        locations.width = rect.right - rect.left;
                        locations.height = rect.bottom - rect.top + 1;
                    } else if (document.getBoxObjectFor) {
                        var boxObjectFor = document.getBoxObjectFor(obj);
                        locations.left = boxObjectFor.x;
                        locations.top = boxObjectFor.y;
                        locations.width = boxObjectFor.width;
                        locations.height = boxObjectFor.height;
                    } else {
                        locations.left = obj.offsetLeft;
                        locations.top = obj.offsetTop;
                        locations.width = obj.offsetWidth;
                        locations.height = obj.offsetHeight + 3;

                        var parent = obj.offsetParent;
                        if (parent != obj) {
                            while (parent) {
                                locations.left += parent.offsetLeft;
                                locations.top += parent.offsetTop;
                                parent = parent.offsetParent;
                            }
                        }

                        var ua = navigator.userAgent.toLowerCase();
                        if (ua.indexOf('opera') != -1 || (ua.indexOf('safari') != -1 && getStyle(obj, 'position') == 'absolute')) {
                            locations.top -= document.body.offsetTop;
                        }
                    }
                    return locations;
                }
            };

        };


        var npPfsCtrl = new function () {
            this.uuid = null;
            this.Field = null,

                this.currentPort = -1;
            this.foundPort = false;

            this.Options = {};

            this.parseOptions = function (options) {
                if (npCommon.isNull(options)) options = {};
                if (npCommon.isNull(options.Firewall)) options.Firewall = true;
                if (npCommon.isNull(options.SecureKey)) options.SecureKey = true;
                if (npCommon.isNull(options.Fds)) options.Fds = true;
                if (npCommon.isNull(options.AutoStartup)) options.AutoStartup = true;
                if (npCommon.isNull(options.Form)) options.Form = null;
                if (npCommon.isNull(options.Debug)) options.Debug = false;
                if (npCommon.isNull(options.Loading)) options.Loading = {};
                if (npCommon.isNull(options.Loading.Default)) options.Loading.Default = false;
                if (npCommon.isNull(options.Loading.Before)) options.Loading.Before = function () {
                };
                if (npCommon.isNull(options.Loading.After)) options.Loading.After = function () {
                };
                if (npCommon.isNull(options.AutoScanAttrName)) options.AutoScanAttrName = "npkencrypt";
                if (npCommon.isNull(options.AutoScanAttrValue)) options.AutoScanAttrValue = "on";
                if (npCommon.isNull(options.MoveToInstall)) options.MoveToInstall = function (url) {
                    if (!npCommon.isBlank(url)) {
                        if (confirm("No module is installed or Can not find the server. Would you like to go to the installation page?")) {
                            location.replace(url);
                        }
                    } else {
                        alert("No module is installed or Can not find the server.");
                    }
                };
                if (options.Loading.Default == true) {
                    options.Loading.Before = function () {
                        npPfsCtrl.showLoading();
                    };
                    options.Loading.After = function () {
                        npPfsCtrl.hideLoading();
                    };
                }


                this.Options.FW = options.Firewall && npPfsPolicy.License.FW;
                this.Options.SK = options.SecureKey && npPfsPolicy.License.SK;
                this.Options.FD = options.Fds && npPfsPolicy.License.FD;
                this.Options.KV = options.Keypad && npPfsPolicy.License.KV;
                this.Options.AS = options.AutoStartup;
                this.Options.FM = options.Form;

                this.Options.LD = options.Loading;
                //this.Options.LD.DF = options.Loading.Default;
                this.Options.LD.BF = options.Loading.Before;
                this.Options.LD.AF = options.Loading.After;

                this.Options.AN = options.AutoScanAttrName;
                this.Options.AV = options.AutoScanAttrValue;
                this.Options.MI = options.MoveToInstall;

                //this.Options.Debug = options.Debug;
                if (options.Debug == true) {
                    npPfsPolicy.Common.RuntimeMode = npPfsConst.RuntimeMode.DEBUG;
                } else {
                    npPfsPolicy.Common.RuntimeMode = npPfsConst.RuntimeMode.RELEASE;
                }
            }


            this.init = function (options) {
                if (npTransaction.getJob("jhs") && npTransaction.getJob("jhs").getState() != npPfsConst.TaskState.STATE_READY) {
                    return;
                }
                ;

                if (npPfsDefine.ie && npCommon.compareVersion(9.0, npPfsDefine.browserVersion) && npCommon.isNull(jQuery)) {
//		if(((npPfsDefine.ie && npCommon.compareVersion(9.0, npPfsDefine.browserVersion)) || (npPfsDefine.op && npPfsDefine.lnx))
//			 && npCommon.isNull(jQuery)){
                    //alert("jQuery 객체를 찾을 수 없습니다. Microsoft IE Browser 9.0 이하 버전에서는 jQuery를 사용해야 합니다.");
                    alert("Can not find a jQuery object. In Microsoft IE Browser 9.0 or earlier, you must use jQuery.")
                    return;
                }

                npPfsCtrl.parseOptions(options);

                npPfsCtrl.Options.LD.BF();

                if (npPfsPolicy.Common.RuntimeMode == npPfsConst.RuntimeMode.DEBUG) {
                    npCommon.createDivision(document, "byid", "nppfs-console-log");
                }

                // 개발자 도구 막기
                if (npPfsPolicy.Common.BlockDevTools == true) {
                    document.onkeydown = function (event) {
                        event = (event || window.event);
                        //npConsole.log("ctrl : " + event.ctrlKey + ", shift : " + event.shiftKey + ", alt : " + event.altKey + ", meta : " + event.metaKey + ", keycode : " + event.keyCode);
                        var blockEvent = false;
                        if (npPfsDefine.win || npPfsDefine.lnx) {
                            blockEvent = (event.keyCode == 123) || (event.ctrlKey && event.shiftKey && event.keyCode == 73);/* F12, Ctrl+Shift+i */
                            if (npPfsDefine.ff) {
                                blockEvent = blockEvent || (event.ctrlKey && event.shiftKey && (event.keyCode == 75 || event.keyCode == 81 || event.keyCode == 83));/* Ctrl+Shift+k, Ctrl+Shift+q, Ctrl+Shift+s */
                                blockEvent = blockEvent || (event.shiftKey && (event.keyCode == 113 || event.keyCode == 116 || event.keyCode == 118))/* Shift+F2, Shift+F5, Shift+F7 */
                            }
                        } else if (npPfsDefine.mac) {
                            blockEvent = (event.altKey && event.metaKey && (event.keyCode == 73));/* Command+Alt+i */
                            if (npPfsDefine.ff) {
                                blockEvent = blockEvent || (event.altKey && event.metaKey && (event.keyCode == 75 || event.keyCode == 81 || event.keyCode == 83));/* Command+Shift+k, Command+Shift+q, Command+Shift+s */
                                blockEvent = blockEvent || (event.shiftKey && (event.keyCode == 113 || event.keyCode == 116 || event.keyCode == 118));/* Shift+F2, Shift+F5, Shift+F7 */
                            }
                        }

                        if (blockEvent == true) {
                            npConsole.log("Developer Tools shortcuts are not available.");
                            event.preventDefault();
                            return false;
                        }
                    }

                    document.onmousedown = function (event) {
                        event = (event || window.event);
                        if ((event.button == 2) || (event.button == 3)) {
                            npConsole.log("Right mouse button can not be used.");
                            return false;
                        }
                    }
                }

                var job = new npTransaction.Job("jhs");
                npTransaction.addJob(job);

                if (npCommon.isBlank(this.currentPort) || this.currentPort <= 0) {
                    npPfsCtrl.findPort();
                }


                // Auto Start...
                if (this.Options.AS == true) {
                    this.startup(this.Options.FM);
                } else {
                    npTransaction.waitRun("jhs", function () {
                        job.setState(npPfsConst.TaskState.STATE_DONE);
                        npPfsCtrl.Options.LD.AF();
                    }, npPfsPolicy.Common.WaitTimeout);
                }
            };

            this.startup = function (theform) {
                npTransaction.waitRun("jhs", function () {
                    var job = new npTransaction.Job("jsu");

                    if (npPfsCtrl.foundPort == false) {
                        job.setState(npPfsConst.TaskState.STATE_DONE);

                        npPfsCtrl.Options.LD.AF();

                        npConsole.log("Can not find a valid [nProtect Online Security]. Check the connection path, or contact your administrator.");
//				npConsole.log("서버를 찾을 수 없거나 모듈이 설치되어있지 않습니다. 설치페이지로 이동합니다.");

                        if (typeof (npPfsCtrl.Options.MI) == "function") {
                            npPfsCtrl.Options.MI(npPfsPolicy.Common.InstallUrl);
                        }
                        job.setState(npPfsConst.TaskState.STATE_DONE);
                        return;
                    }

                    if (npCommon.isBlank(npPfsCtrl.uuid)) {
                        npPfsCtrl.uuid = npCommon.makeUuid();
                        npConsole.log("Page UUID : " + npPfsCtrl.uuid);
                    }


                    // IE7의 경우 키보드보안 또는 단말정보수집을 하는 경우 적어도 1개의 nppfs-elements 의 항목이 존재해야 한다.
                    if (npPfsDefine.ie && npCommon.compareVersion(7.0, npPfsDefine.browserVersion)) {
                        var div4ie7 = null;
                        var found = false;
                        for (var nIndex = 0; nIndex < document.forms.length; nIndex++) {
                            div4ie7 = npCommon.findDivision(document.forms[nIndex], "byclass", "nppfs-elements");
                            if (!npCommon.isNull(div4ie7)) {
                                found = true;
                                break;
                            }
                        }

                        if (found == false) {
                            alert("To use the Security keyboard in IE7 or older, need at least one div[class=\"nppfs-elements\"] in form.");
                            job.setState(npPfsConst.TaskState.STATE_DONE);
                            npPfsCtrl.hideLoading();
                            return;
                        }
                    }


                    var initCompleteJobs = [];
                    //var destoryJobs = ["jhs"];
                    var destoryJobs = [];

                    //npPfsCtrl.Options.LD.BF();
                    if (npPfsCtrl.Options.FW == true) {
                        if (npPfsDefine.IsSupported(npPfsPolicy.FW.Support)) {
                            npNCtrl.init();
                        } else {
                            npConsole.log("The environment does not support a firewall.");
                        }
                    }

                    if (npPfsCtrl.Options.SK == true) {
                        if (npPfsDefine.IsSupported(npPfsPolicy.SK.Support)) {
                            npKCtrl.init();
                            initCompleteJobs.push("jkrf");
                            destoryJobs = destoryJobs.concat(["jki", "nkc", "jkb"]);
                        } else {
                            npConsole.log("The environment does not support a keyboard security.");
                        }
                    }

                    if (npPfsCtrl.Options.FD == true) {
                        if (npPfsDefine.IsSupported(npPfsPolicy.FD.Support)) {
                            npFCtrl.init(theform);
                            //initCompleteJobs.push("jfc");
                            //destoryJobs = destoryJobs.concat(["nfa", "jfi", "jfg", "jfp", "jfb", "jfc"]);

                            initCompleteJobs.push("jfi");
                            //destoryJobs = destoryJobs.concat(["nfa", "jfi"]);
                            destoryJobs = destoryJobs.concat(["nfa"]);
                        } else {
                            npConsole.log("The environment does not support a FDS(Fraud Detection System).");
                        }
                    }


                    if (npPfsCtrl.Options.KV == true) {
                        //if(npPfsDefine.IsSupported(npPfsPolicy.SK.Support)){
                        npVCtrl.init();
                        //initCompleteJobs.push("jfc");
                        //destoryJobs = destoryJobs.concat(["nfa", "jfi", "jfg", "jfp", "jfb", "jfc"]);

                        initCompleteJobs.push("jvi");

                        //destoryJobs = destoryJobs.concat(["nfa", "jfi"]);
                        destoryJobs = destoryJobs.concat(["jvi"]);
                        //} else {
                        //	npConsole.log("The environment does not support a Keypad.");
                        //}
                    }


                    // 종료시 이벤트 추가
                    npPfsCtrl.bindEventListener();

                    job.setState(npPfsConst.TaskState.STATE_DONE);

                    // 모든 작업 대기
                    npTransaction.waitRun(initCompleteJobs, function () {
                        //npTransaction.print();
                        npPfsCtrl.Options.LD.AF();
                        for (var idx = 0; idx < destoryJobs.length; idx++) {
                            npTransaction.destory(destoryJobs[idx]);
                        }
                        //npTransaction.print();
                    }, npPfsPolicy.Common.WaitTimeout);

                }, npPfsPolicy.Common.WaitTimeout);
            }

            this.waitSubmit = function (callback) {
                if (npPfsCtrl.Options.FD == true && npPfsDefine.IsSupported(npPfsPolicy.FD.Support)) {
                    npTransaction.waitRun("jfc", function () {
                        callback();
                    }, npPfsPolicy.Common.WaitTimeout);
                } else {
                    callback();
                }
            };

            //this.oldOverflow = "";

            this.showLoading = function () {
                var layer = npCommon.findDivision(document, "byid", "nppfs-loading-modal");
                try {
                    layer.style.display = "none";
                    layer.style.position = "fixed";
                    layer.style.zIndex = "1000";
                    layer.style.top = "0";
                    layer.style.left = "0";
                    layer.style.height = "100%";
                    layer.style.width = "100%";

                    // clock
                    layer.style.background = "rgba( 255, 255, 255, .7) url(data:image/gif;base64,R0lGODlhIAAgAPMAAP///wAAAMbGxoSEhLa2tpqamjY2NlZWVtjY2OTk5Ly8vB4eHgQEBAAAAAAAAAAAACH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAIAAgAAAE5xDISWlhperN52JLhSSdRgwVo1ICQZRUsiwHpTJT4iowNS8vyW2icCF6k8HMMBkCEDskxTBDAZwuAkkqIfxIQyhBQBFvAQSDITM5VDW6XNE4KagNh6Bgwe60smQUB3d4Rz1ZBApnFASDd0hihh12BkE9kjAJVlycXIg7CQIFA6SlnJ87paqbSKiKoqusnbMdmDC2tXQlkUhziYtyWTxIfy6BE8WJt5YJvpJivxNaGmLHT0VnOgSYf0dZXS7APdpB309RnHOG5gDqXGLDaC457D1zZ/V/nmOM82XiHRLYKhKP1oZmADdEAAAh+QQJCgAAACwAAAAAIAAgAAAE6hDISWlZpOrNp1lGNRSdRpDUolIGw5RUYhhHukqFu8DsrEyqnWThGvAmhVlteBvojpTDDBUEIFwMFBRAmBkSgOrBFZogCASwBDEY/CZSg7GSE0gSCjQBMVG023xWBhklAnoEdhQEfyNqMIcKjhRsjEdnezB+A4k8gTwJhFuiW4dokXiloUepBAp5qaKpp6+Ho7aWW54wl7obvEe0kRuoplCGepwSx2jJvqHEmGt6whJpGpfJCHmOoNHKaHx61WiSR92E4lbFoq+B6QDtuetcaBPnW6+O7wDHpIiK9SaVK5GgV543tzjgGcghAgAh+QQJCgAAACwAAAAAIAAgAAAE7hDISSkxpOrN5zFHNWRdhSiVoVLHspRUMoyUakyEe8PTPCATW9A14E0UvuAKMNAZKYUZCiBMuBakSQKG8G2FzUWox2AUtAQFcBKlVQoLgQReZhQlCIJesQXI5B0CBnUMOxMCenoCfTCEWBsJColTMANldx15BGs8B5wlCZ9Po6OJkwmRpnqkqnuSrayqfKmqpLajoiW5HJq7FL1Gr2mMMcKUMIiJgIemy7xZtJsTmsM4xHiKv5KMCXqfyUCJEonXPN2rAOIAmsfB3uPoAK++G+w48edZPK+M6hLJpQg484enXIdQFSS1u6UhksENEQAAIfkECQoAAAAsAAAAACAAIAAABOcQyEmpGKLqzWcZRVUQnZYg1aBSh2GUVEIQ2aQOE+G+cD4ntpWkZQj1JIiZIogDFFyHI0UxQwFugMSOFIPJftfVAEoZLBbcLEFhlQiqGp1Vd140AUklUN3eCA51C1EWMzMCezCBBmkxVIVHBWd3HHl9JQOIJSdSnJ0TDKChCwUJjoWMPaGqDKannasMo6WnM562R5YluZRwur0wpgqZE7NKUm+FNRPIhjBJxKZteWuIBMN4zRMIVIhffcgojwCF117i4nlLnY5ztRLsnOk+aV+oJY7V7m76PdkS4trKcdg0Zc0tTcKkRAAAIfkECQoAAAAsAAAAACAAIAAABO4QyEkpKqjqzScpRaVkXZWQEximw1BSCUEIlDohrft6cpKCk5xid5MNJTaAIkekKGQkWyKHkvhKsR7ARmitkAYDYRIbUQRQjWBwJRzChi9CRlBcY1UN4g0/VNB0AlcvcAYHRyZPdEQFYV8ccwR5HWxEJ02YmRMLnJ1xCYp0Y5idpQuhopmmC2KgojKasUQDk5BNAwwMOh2RtRq5uQuPZKGIJQIGwAwGf6I0JXMpC8C7kXWDBINFMxS4DKMAWVWAGYsAdNqW5uaRxkSKJOZKaU3tPOBZ4DuK2LATgJhkPJMgTwKCdFjyPHEnKxFCDhEAACH5BAkKAAAALAAAAAAgACAAAATzEMhJaVKp6s2nIkolIJ2WkBShpkVRWqqQrhLSEu9MZJKK9y1ZrqYK9WiClmvoUaF8gIQSNeF1Er4MNFn4SRSDARWroAIETg1iVwuHjYB1kYc1mwruwXKC9gmsJXliGxc+XiUCby9ydh1sOSdMkpMTBpaXBzsfhoc5l58Gm5yToAaZhaOUqjkDgCWNHAULCwOLaTmzswadEqggQwgHuQsHIoZCHQMMQgQGubVEcxOPFAcMDAYUA85eWARmfSRQCdcMe0zeP1AAygwLlJtPNAAL19DARdPzBOWSm1brJBi45soRAWQAAkrQIykShQ9wVhHCwCQCACH5BAkKAAAALAAAAAAgACAAAATrEMhJaVKp6s2nIkqFZF2VIBWhUsJaTokqUCoBq+E71SRQeyqUToLA7VxF0JDyIQh/MVVPMt1ECZlfcjZJ9mIKoaTl1MRIl5o4CUKXOwmyrCInCKqcWtvadL2SYhyASyNDJ0uIiRMDjI0Fd30/iI2UA5GSS5UDj2l6NoqgOgN4gksEBgYFf0FDqKgHnyZ9OX8HrgYHdHpcHQULXAS2qKpENRg7eAMLC7kTBaixUYFkKAzWAAnLC7FLVxLWDBLKCwaKTULgEwbLA4hJtOkSBNqITT3xEgfLpBtzE/jiuL04RGEBgwWhShRgQExHBAAh+QQJCgAAACwAAAAAIAAgAAAE7xDISWlSqerNpyJKhWRdlSAVoVLCWk6JKlAqAavhO9UkUHsqlE6CwO1cRdCQ8iEIfzFVTzLdRAmZX3I2SfZiCqGk5dTESJeaOAlClzsJsqwiJwiqnFrb2nS9kmIcgEsjQydLiIlHehhpejaIjzh9eomSjZR+ipslWIRLAgMDOR2DOqKogTB9pCUJBagDBXR6XB0EBkIIsaRsGGMMAxoDBgYHTKJiUYEGDAzHC9EACcUGkIgFzgwZ0QsSBcXHiQvOwgDdEwfFs0sDzt4S6BK4xYjkDOzn0unFeBzOBijIm1Dgmg5YFQwsCMjp1oJ8LyIAACH5BAkKAAAALAAAAAAgACAAAATwEMhJaVKp6s2nIkqFZF2VIBWhUsJaTokqUCoBq+E71SRQeyqUToLA7VxF0JDyIQh/MVVPMt1ECZlfcjZJ9mIKoaTl1MRIl5o4CUKXOwmyrCInCKqcWtvadL2SYhyASyNDJ0uIiUd6GGl6NoiPOH16iZKNlH6KmyWFOggHhEEvAwwMA0N9GBsEC6amhnVcEwavDAazGwIDaH1ipaYLBUTCGgQDA8NdHz0FpqgTBwsLqAbWAAnIA4FWKdMLGdYGEgraigbT0OITBcg5QwPT4xLrROZL6AuQAPUS7bxLpoWidY0JtxLHKhwwMJBTHgPKdEQAACH5BAkKAAAALAAAAAAgACAAAATrEMhJaVKp6s2nIkqFZF2VIBWhUsJaTokqUCoBq+E71SRQeyqUToLA7VxF0JDyIQh/MVVPMt1ECZlfcjZJ9mIKoaTl1MRIl5o4CUKXOwmyrCInCKqcWtvadL2SYhyASyNDJ0uIiUd6GAULDJCRiXo1CpGXDJOUjY+Yip9DhToJA4RBLwMLCwVDfRgbBAaqqoZ1XBMHswsHtxtFaH1iqaoGNgAIxRpbFAgfPQSqpbgGBqUD1wBXeCYp1AYZ19JJOYgH1KwA4UBvQwXUBxPqVD9L3sbp2BNk2xvvFPJd+MFCN6HAAIKgNggY0KtEBAAh+QQJCgAAACwAAAAAIAAgAAAE6BDISWlSqerNpyJKhWRdlSAVoVLCWk6JKlAqAavhO9UkUHsqlE6CwO1cRdCQ8iEIfzFVTzLdRAmZX3I2SfYIDMaAFdTESJeaEDAIMxYFqrOUaNW4E4ObYcCXaiBVEgULe0NJaxxtYksjh2NLkZISgDgJhHthkpU4mW6blRiYmZOlh4JWkDqILwUGBnE6TYEbCgevr0N1gH4At7gHiRpFaLNrrq8HNgAJA70AWxQIH1+vsYMDAzZQPC9VCNkDWUhGkuE5PxJNwiUK4UfLzOlD4WvzAHaoG9nxPi5d+jYUqfAhhykOFwJWiAAAIfkECQoAAAAsAAAAACAAIAAABPAQyElpUqnqzaciSoVkXVUMFaFSwlpOCcMYlErAavhOMnNLNo8KsZsMZItJEIDIFSkLGQoQTNhIsFehRww2CQLKF0tYGKYSg+ygsZIuNqJksKgbfgIGepNo2cIUB3V1B3IvNiBYNQaDSTtfhhx0CwVPI0UJe0+bm4g5VgcGoqOcnjmjqDSdnhgEoamcsZuXO1aWQy8KAwOAuTYYGwi7w5h+Kr0SJ8MFihpNbx+4Erq7BYBuzsdiH1jCAzoSfl0rVirNbRXlBBlLX+BP0XJLAPGzTkAuAOqb0WT5AH7OcdCm5B8TgRwSRKIHQtaLCwg1RAAAOwAAAAAAAAAAAA==) 50% 50% no-repeat";
                    // for IE7, IE8
                    if (npPfsDefine.ie && npCommon.compareVersion(8.0, npPfsDefine.browserVersion)) {
                        layer.style.backgroundColor = "#ffffff";
                        layer.style.filter = "alpha(opacity=70)";
                    }
                } catch (e) {
                }

                layer.style.display = "block";

                //this.oldOverflow = document.body.style.overflow;
                //npConsole.log("old overflow : " + this.oldOverflow);

                //document.body.style.overflow = "hidden";
            };

            this.hideLoading = function () {
                var layer = npCommon.findDivision(document, "byid", "nppfs-loading-modal");
                layer.style.display = "none";
                layer.style.height = "0px";
                layer.style.width = "0px";

                //document.body.style.overflow = "auto";
                //document.body.style.overflow = this.oldOverflow;
            };

            this.bindEventListener = function () {
                var finalize = function () {
                    if (npPfsCtrl.Options.FW == true && npPfsDefine.IsSupported(npPfsPolicy.FW.Support)) {
                        npNCtrl.finalize();
                    }
                    if (npPfsCtrl.Options.SK == true && npPfsDefine.IsSupported(npPfsPolicy.SK.Support)) {
                        npVCtrl.finalize();
                    }
                    if (npPfsCtrl.Options.FD == true && npPfsDefine.IsSupported(npPfsPolicy.FD.Support)) {
                        npFCtrl.finalize();
                    }
                }

                try {
                    if (window.addEventListener) {
                        window.addEventListener("beforeunload", finalize, false);
                        window.addEventListener("unload", finalize, false);
                    } else {
                        window.attachEvent("onbeforeunload", finalize);
                        window.attachEvent("onunload", finalize);
                    }
                } catch (e) {
                    npConsole.log(e);
                }
            }


            this.makeUrl = function (port) {
                if (npCommon.isBlank(port)) {
                    port = (this.currentPort > 0) ? this.currentPort : npPfsPolicy.Common.Port;
                }

                var ret = [];
                ret.push(npPfsPolicy.Common.Protocol);
                ret.push("://");
                ret.push(npPfsPolicy.Common.Host);
                ret.push(((npPfsPolicy.Common.Protocol == "http" && port == 80) || (npPfsPolicy.Common.Protocol == "https" && port == 443)) ? "" : ":" + port);
                ret.push(npPfsPolicy.Common.ContextPath);
                return ret.join("");
            };

            this.makeHeader = function (pcode, sync, headers) {
                if (npCommon.isNull(headers) || typeof (headers) != "array") {
                    headers = new Array(2);
                    headers[0] = document.domain;
                    headers[1] = npPacket.CustomerId;
                }

                var headerCount = headers.length;

                var command = [];
                command.push(pcode);
                if (npCommon.isBlank(sync)) {
                    command.push("1");
                } else {
                    command.push(sync);
                }
                command.push(headerCount);
                for (var idx = 0; idx < headerCount; idx++) {
                    command.push(npCommon.makeLength(headers[idx]));
                    command.push(headers[idx]);
                }

                return command;
            };

            this.makeCommand = function (pcode, sync, headers, ccode, parameter) {
                var command = npPfsCtrl.makeHeader(pcode, sync, headers);

                if (!npCommon.isBlank(ccode)) {
                    command.push(ccode);
                }

                if (!npCommon.isBlank(parameter)) {
                    command.push(npCommon.makeLength(parameter));
                    command.push(parameter);
                }

                return command.join("");
            };


            //this.isCompleteFindTask = npPfsConst.TaskState.STATE_READY;
            //this.taskStatus = {};
            this.handshake = function (url, port, options) {
                var task = npTransaction.getJob("jhs").getTask("ths_" + port + "");

                var result = "";
                if (npCommon.isNull(options)) {
                    options = {};
                }
                if (npCommon.isNull(options.callback)) {
                    options.callback = function (result, port) {
                        //npConsole.log("Handshake(Port : " + port + ") result : => " + result);
                        if (npPfsCtrl.foundPort == false && result == npPacket.Daemon.Result.TRUE) {
                            npPfsCtrl.currentPort = port;
                            npPfsCtrl.foundPort = true;

                            npCommon.setCookie("nosServerPort", npPfsCtrl.currentPort, 30);

                            npConsole.log("Found a possible connection port (" + port + ").")

                            task.setState(npPfsConst.TaskState.STATE_DONE);
                            task.setData({port: port});
                            //npTransaction.print();
                        }
                    }
                }

                //npConsole.log("Handshake(URL : " + url + ") port : [" + port + "]");
                //alert("Handshake(URL : " + url + ") port : [" + port + "]");

                task.setState(npPfsConst.TaskState.STATE_DOING);

                var certParam = this.makeCommand(npPacket.Product.DM, npPacket.Sync.Sync, [document.domain, npPacket.CustomerId], npPacket.Daemon.Command.Handshake, null);
                //npConsole.log("Find Port : " + certParam);

                /*
		IE8이하에서는 크로스사이트 스크립트 방지로 인하여 우회
		*/
                if (npPfsDefine.ie && npCommon.compareVersion(9.0, npPfsDefine.browserVersion)) {
//		if((npPfsDefine.ie && npCommon.compareVersion(9.0, npPfsDefine.browserVersion)) || (npPfsDefine.op && npPfsDefine.lnx)){
                    options.port = port;
                    npCommon.sendAsyncCommand(certParam, function (xhr) {
                        if (xhr.readyState == 4) {
                            if (xhr.status == 200) {
                                npConsole.log("RES : " + xhr.responseText);
                                options.callback(xhr.responseText, port);
                            } else {
                                options.callback("", port);
                            }

                            task.setState(npPfsConst.TaskState.STATE_DONE);
                            //npTransaction.print();
                        }
                    }, options);
                } else {
                    npCommon.send(url, "code=" + certParam, {
                        async: true, callback: function (xhr) {
                            if (xhr.readyState == 4) {
                                if (xhr.status == 200) {
                                    npConsole.log("RES : " + xhr.responseText);
                                    options.callback(xhr.responseText, port);
                                } else {
                                    options.callback("", port);
                                }

                                task.setState(npPfsConst.TaskState.STATE_DONE);
                                //npTransaction.print();
                            }
                        }
                    });
                }
            };

            this.findPort = function () {
                // 접속가능여부 확인
                var cookiePort = npCommon.getCookie("nosServerPort");
                if (!npCommon.isBlank(cookiePort) && cookiePort > 0) {
                    var task = new npTransaction.Job("ths_" + cookiePort + "");
                    npTransaction.getJob("jhs").addTask(task);


                    npConsole.log("There is a port(" + cookiePort + ") is stored in a cookie. Verify the port.");

                    var url = this.makeUrl(cookiePort);
                    this.handshake(url, cookiePort, {
                        callback: function (result) {
                            if (result == npPacket.Daemon.Result.TRUE) {
                                npPfsCtrl.currentPort = cookiePort;
                                npPfsCtrl.foundPort = true;
                                npConsole.log("The port(" + cookiePort + ") is stored in a cookie is a valid value. Use this port.");
                                task.setState(npPfsConst.TaskState.STATE_DONE);
                                //npTransaction.print();
                            } else {
                                //npTransaction.print();
                                npCommon.setCookie("nosServerPort", null);
                                npTransaction.getJob("jhs").destoryAll();
                                npPfsCtrl.findPort();
                            }
                        }
                    });
                    return;
                }

                for (var index = 0; index < npPfsPolicy.Common.Range && this.foundPort == false; index++) {
                    var tempPort = npPfsPolicy.Common.Port + index;

                    var task = new npTransaction.Job("ths_" + tempPort + "");
                    task.setData({port: tempPort});
                    npTransaction.getJob("jhs").addTask(task);

                    var url = this.makeUrl(tempPort);

                    //npConsole.log(url);
                    this.handshake(url, tempPort);
                }
            };


            this.isInstall = function (callbacks) {
                var job = new npTransaction.Job("jhs");
                npTransaction.addJob(job);

                if (npCommon.isNull(callbacks)) {
                    callbacks = {};
                }
                if (npCommon.isNull(callbacks.success)) {
                    callbacks.success = function () {
                    }
                }
                if (npCommon.isNull(callbacks.fail)) {
                    callbacks.fail = function () {
                    }
                }

                if (npCommon.isBlank(this.currentPort) || this.currentPort <= 0) {
                    npPfsCtrl.findPort();
                }

                npTransaction.waitRun("jhs", function () {
                    if (npPfsCtrl.foundPort == false) {
                        callbacks.fail();
                    } else {
                        callbacks.success();
                    }
                }, npPfsPolicy.Common.WaitTimeout);
            }

            this.checkInstall = function (callbacks) {
                npTransaction.waitRun("jhs", function () {

                    if (npCommon.isNull(callbacks)) {
                        callbacks = {};
                    }

                    if (npCommon.isNull(callbacks.before)) {
                        callbacks.before = function () {
                            //alert("정상적인 설치가 되었는지 확인합니다. 설치 후 초기화 완료시까지 수 초(대략 5~10초)가 소요됩니다. 설치가 완료되면 자동으로 첫 페이지로 이동합니다.");
                        }
                    }

                    if (npCommon.isNull(callbacks.after)) {
                        callbacks.after = function () {
                            //alert("설치가 완료되었습니다.");
                        }
                    }

                    var job = npTransaction.getJob("jsu");
                    if (npCommon.isNull(job)) {
                        job = new npTransaction.Job("jsu");
                        npTransaction.addJob(job);

                        job.setState(npPfsConst.TaskState.STATE_DONE);

                        // 설치전 메시지
                        callbacks.before();
                    }

                    if (npPfsCtrl.foundPort == false) {
                        job.setState(npPfsConst.TaskState.STATE_DONE);

                        npTransaction.destory("jhs");
                        var job = new npTransaction.Job("jhs");
                        npTransaction.addJob(job);

                        if (npCommon.isBlank(this.currentPort) || this.currentPort <= 0) {
                            npPfsCtrl.findPort();
                        }

                        npPfsCtrl.checkInstall(callbacks);

                        return;
                    } else {
                        // 설치 완료 후 작업
                        callbacks.after();
                    }
                }, 300);
            }
        };


        var npVCtrl = new function () {
            this.init = function () {
                var job = new npTransaction.Job("jvi");
                npTransaction.addJob(job);

//		try {
                if (document.forms.length == 0) {
                    job.setState(npPfsConst.TaskState.STATE_DONE);
                    return;
                }

                var keypadElements = [];
                for (var nIndex = 0; nIndex < document.forms.length; nIndex++) {
                    var hiddenFields = [];
                    for (var nElement = 0; nElement < document.forms[nIndex].elements.length; nElement++) {
                        var layer1 = npCommon.findDivision(document.forms[nIndex], "byclass", "nppfs-elements");
                        var layer2 = npCommon.findDivision(document.forms[nIndex], "byclass", "nppfs-keypad-div");

                        var element = document.forms[nIndex].elements[nElement];
                        if (element.tagName != "INPUT" || element.style.display == "none" || (element.type != "text" && element.type != "password")) {
                            continue;
                        }

                        if (element.getAttribute(npPfsCtrl.Options.AN) == npPfsCtrl.Options.AV) {
                            keypadElements.push({form: document.forms[nIndex], element: element});
                        }
                    }

                    if (hiddenFields.length > 0) {
                        npConsole.log("[" + element.tagName + ", " + element.type + ", " + element.name + ", " + element.getAttribute(npPfsCtrl.Options.AN) + ", " + npPfsCtrl.Options.AV + "]");
                        npCommon.createElement(document.forms[nIndex], hiddenFields);
                    }
                }

                for (var nIndex = 0; nIndex < keypadElements.length; nIndex++) {
                    var element = keypadElements[nIndex];
                    this.registKeypad(element.form, element.element);
                }

//		} catch (e) {
//			npConsole.log(e);
//		} finally {
                job.setState(npPfsConst.TaskState.STATE_DONE);
//		}
            };

            /************************************************************
             * hidden input 생성
             ***********************************************************/
            this.createElement = function (elements) {
                var inputs = [];
                if (!npCommon.isNull(elements) && elements.length > 0) {
                    for (var i = 0, t = elements.length; i < t; i++) {
                        var forms = npCommon.findElement(elements[i].parent);
                        var layer = npCommon.findDivision(forms, "byclass", "nppfs-elements");
                        var html = "<input type=\"hidden\" style=\"display:;\" id=\"" + elements[i].name + "\" name=\"" + elements[i].name + "\"";
                        if (!npCommon.isBlank(elements[i].value)) {
                            html += " value=\"" + elements[i].value + "\"";
                        }
                        html += " />";
                        inputs.push(html);
                    }
                }
                //npConsole.log(inputs.join("\n"));
                layer.innerHTML = layer.innerHTML + inputs.join("\n");
            };

            this.removeHiddenInput = function (form, id) {
                var forms = document.getElementsByName(form);
                if (forms.length == 0 || forms.length > 1) {
                    alert(form + " is not exist or more than one");
                }
                var elem = document.getElementById(id);
                if (typeof form != "object") {
                    return;
                }
                if (typeof elem != "object") {
                    return;
                }

                forms.item(0).removeChild(elem);
            };

            this.registKeypad = function (form, element) {
                var layer1 = npCommon.findDivision(form, "byclass", "nppfs-elements");
                var layer2 = npCommon.findDivision(form, "byclass", "nppfs-keypad-div");
                if (npCommon.isNull(layer1)) {
                    npConsole.log("In IE7, div[class=\"nppfs-elements\"] in form(" + form.name + ") is required.");
                    alert("In IE7, div[class=\"nppfs-elements\"] in form(" + form.name + ") is required.");
                    return;
                }
                if (npCommon.isNull(layer2)) {
                    npConsole.log("In IE7, div[class=\"nppfs-keypad-div\"] in form(" + form.name + ") is required.");
                    alert("In IE7, div[class=\"nppfs-keypad-div\"] in form(" + form.name + ") is required.");
                    return;
                }

                var job = npTransaction.getJob("jvi");
                var task = new npTransaction.Job("tvrf_" + form.name + "_" + element.name);
                job.addTask(task);

                var options = {
                    type: npPfsConst.Keypad.types.KEYBOARD
                    , actiontype: npPfsConst.Keypad.actionMode.PASSWORD
                    , showtype: npPfsConst.Keypad.showMode.LAYER
                    , usetype: npPfsConst.Keypad.activate.FOCUS
                    , inputs: element.name
                    , form: form.name
                    , display: "hide"
                    , imagepath: "/pluginfree/jsp"
                    , checkfunc: "decode('result1', npKpdConst.types.KEYBOARD, 'form1');"
                };
                //npKpd.create("nppfs-keypad-div", options);

                if (typeof options != "object") {
                    alert("JSON param not found in create method!");
                    return;
                }
                options.div = "nppfs-keypad-div";
                options.width = document.body.offsetWidth;

                var param = [];
                param.push("m=e");
                var keys = npCommon.GetKeys(options);
                for (var idx = 0, length = keys.length; idx < length; idx++) {
                    var key = keys[idx];
                    param.push(key + "=" + encodeURIComponent(options[key]));
                }


                var params = param.join("&");
                var result = npCommon.send(npPfsPolicy.Common.KeyPadUrl, params, {
                    async: false,
                    callback: function (xhr) {
                        if (xhr.readyState == 4) {
                            if (xhr.status == 200) {
                                //npConsole.log("RES : " + xhr.responseText);
                                result = xhr.responseText;

                                //var div = document.getElementById("nppfs-keypad-div");
                                layer2.innerHTML = layer2.innerHTML + result;
                                // search <script> and eval
                                if (match = result.match(/<script[^>]*>(([^<]|\n|\r|<[^\/])+)<\/script>/g)) {
                                    //alert(match.length);
                                    for (var idx = 0; idx < match.length; idx++) {
                                        var match2 = match[idx].match(/<script[^>]*>(([^<]|\n|\r|<[^\/])+)<\/script>/);
                                        //alert(match2);
                                        if (match2.length < 2) continue;
                                        var jscript = match2[1].replace( /<!--\s*([\s\S]*?)\s*-->/, '$1');

                                        //alert(jscript);
                                        try {
                                            if (window.execScript) { // FOR IE
                                                window.execScript(jscript);
                                            } else {
                                                window["eval"].call(window, jscript);
                                            }
                                        } catch (e) {
                                            alert(e);
                                        }
                                    }
                                }
                            } else {
                                alert("result not exist!");
                            }

                            //npConsole.log("aaaaaaaaaaaaaaaaaa complete.... tvrf_" + form.name + "_" + element.name)
                            task.setState(npPfsConst.TaskState.STATE_DONE);
                            //npTransaction.print();
                        }
                        return result;
                    }
                });
            };

            this.decode = function (type, form, jsonParams) {
                if (typeof jsonParams != "object") {
                    alert("JSON param not found in create method!");
                    return;
                }
                var param = this.serialize(type, form);

                var param1 = [];
                param1.push("m=d");
                var keys = npCommon.GetKeys(jsonParams);
                for (var idx = 0, length = keys.length; idx < length; idx++) {
                    var key = keys[idx];
                    param1.push(key + "=" + encodeURIComponent(jsonParams[key]));
                }

                var params = param + "&" + param1.join("&");
                //console.log(params);

                var result = this.sendAjaxCall(npPfsPolicy.Common.KeyPadUrl, params);

                return result;
            };

            this.finalize = function () {
                var params = "m=f";
                var result = npCommon.sendAjaxCall(npPfsPolicy.Common.KeyPadUrl, params);
                return result;
            };

            this.setBackColor = function (obj, formname, color) {
                if (npCommon.isBlank(obj)) {
                    return;
                }
                try {
                    if (typeof obj == "object") {
                        obj.style.backgroundColor = color;
                    } else {
                        var element = npCommon.findElement(obj, formname);
                        if (element) {
                            element.style.backgroundColor = color;
                        }
                    }
                } catch (e) {
                }
                ;
            };

            this.moveFocus = function (num, fromform, toform) {
                try {
                    var len = fromform.value.length;
                    if (len == num) {
                        this.doFocus(toform);
                    }
                } catch (e) {
                }
            };

            this.doFocus = function (toform) {
                if (!npCommon.isNull(toform)) {
                    window.setTimeout(function () {
                        try {
                            toform.focus();
                        } catch (e) {
                        }
                    }, 0);
                }
            };

            this.doBlur = function (toform) {
                if (!npCommon.isNull(toform)) {
                    window.setTimeout(function () {
                        try {
                            toform.blur();
                        } catch (e) {
                        }
                    }, 0);
                }
            };

            this.isUseYn = function (elementname, formname) {
                try {
                    var element = npCommon.findElement(elementname, formname);
                    if (element.type.toString().toLowerCase() == 'radio') {
                        var elements = document.getElementsByName(elementname);
                        for (var i = 0; i < elements.length; i++) {
                            if (elements[i].checked) {
                                if (typeof (formname) != "undefined") {
                                    if (elements[i].form.name == formname && elements[i].value == 'Y') return true;
                                    else return false;
                                } else {
                                    if (elements[i].value == 'Y') return true;
                                    else return false;
                                }
                            }
                        }
                    } else if (element.type.toString().toLowerCase() == 'checkbox') {
                        if (!element.disabled) return element.checked;
                        else return false;
                    } else if (element.type.toString().toLowerCase() == 'hidden') {
                        if (element.value == 'Y') return true;
                        else return false;
                    } else {
                        return false;
                    }
                } catch (e) {
                    return false;
                }
            };

            this.setUnuse = function (elementname, formname) {
                try {
                    var element = npCommon.findElement(elementname, formname);
                    if (element.type.toString().toLowerCase() == 'radio') {
                        var elements = document.getElementsByName(elementname);
                        for (var i = 0; i < elements.length; i++) {
                            if (elements[i].checked) {
                                if (typeof (formname) != "undefined") {
                                    if (elements[i].form.name == formname && elements[i].value == 'Y') elements[i].checked = false;
                                } else {
                                    if (elements[i].value == 'Y') elements[i].checked = false;
                                    else elements[i].checked = true;
                                }
                            } else {
                                elements[i].checked = true;
                            }
                        }
                    } else if (element.type.toString().toLowerCase() == 'checkbox') {
                        element.checked = false;
                    } else if (element.type.toString().toLowerCase() == 'hidden') {
                        if (element.value == 'Y') {
                            element.value = 'N';
                        }
                    }
                } catch (e) {
                    return false;
                }
            };

            /************************************************************
             * 모든 키패드의 해당 입력양식의 배경식 초기화
             ***********************************************************/
            this.clearBackColor = function () {
            };

            // 입력양식에서 값을 입력하였을 경우 특정키(백스페이스 8, 엔터  13) 막기
            this.onKeydown = function (evt, isuse) {
                evt = (evt) ? evt : ((typeof (event) != 'undefined') ? event : null);
                var stopEvent = false;
                if (evt) {
                    //var charCode = (evt.charCode||evt.charCode==0)?evt.charCode:((evt.keyCode)?evt.keyCode:evt.which);
                    var charCode = (evt.charCode) ? evt.charCode : ((evt.keyCode) ? evt.keyCode : evt.which);
                    2011 - 12 - 14
                    if (isuse) {
                        stopEvent = true;
                    }

                    if (stopEvent) {
                        if (evt.returnValue) {
                            evt.returnValue = false;
                        } else if (evt.preventDefault) {
                            evt.preventDefault();
                        } else {
                            return false;
                        }
                    }
                }
            };


            /************************************************************
             * 가상키패드 강제사용정책 적용
             ***********************************************************/
            this.isRunSecureKey = function () {
                var isrun = true;
//		var NPKFXX = document.getElementById("NPKFXX");
//		if(typeof(NPKFXX) == "undefined" || NPKFXX == null || NPKFXX.object == null){
//			isrun = false;
//		} else if(typeof(NPKFXX.IsInsideVm) == "function"){
//			isrun = !NPKFXX.IsInsideVm();
//		}

                return isrun;
            };

            /************************************************************
             * 가상키패드 강제사용정책 적용
             ***********************************************************/
            this.isAbsoluteUse = function (useynelename, formname) {
                var isRunSecureKey = this.isRunSecureKey();
                return !isRunSecureKey;
            };
            this.setUse = function (elementname, formname) {
                this.setUseYn(elementname, formname, true);
            };
            this.setUnuse = function (elementname, formname) {
                this.setUseYn(elementname, formname, false);
            };
            this.clearBackColor = function () {
            };

            this.setUseYn = function (elementname, formname, isUse) {
                try {
                    var element = npCommon.findElement(elementname, formname);
                    if (element.type.toString().toLowerCase() == 'radio') {
                        var elements = document.getElementsByName(elementname);
                        for (var i = 0; i < elements.length; i++) {
                            if (elements[i].checked) {
                                if (typeof (formname) != "undefined") {
                                    if (elements[i].form.name == formname) {
                                        if (elements[i].value == 'Y') elements[i].checked = isUse;
                                        else elements[i].checked = !isUse;
                                    }
                                } else {
                                    if (elements[i].value == 'Y') elements[i].checked = isUse;
                                    else elements[i].checked = !isUse;
                                }
                            } else {
                                if (elements[i].value == 'Y') elements[i].checked = isUse;
                                else elements[i].checked = !isUse;
                            }
                        }
                    } else if (element.type.toString().toLowerCase() == 'checkbox') {
                        element.checked = isUse;
                    } else if (element.type.toString().toLowerCase() == 'hidden') {
                        if (isUse) element.value = 'Y';
                        else element.value = 'N';
                    }
                } catch (e) {
                    return false;
                }
            };


            /************************************************************
             * 모든 키패드의 기능중지
             ***********************************************************/
            this.hideAll = function () {
            };

            /************************************************************
             * 입력된 해쉬값얻기
             ***********************************************************/
            this.getKeypadHash = function (elementid) {
            };

            /************************************************************
             * 키패드 사용여부 확인
             ***********************************************************/
            this.isKeypadUse = function (elementid) {
            };

            /************************************************************
             * 계좌번호 키패드의 기능중지
             ***********************************************************/
            this.hideAccountKeypad = function () {
            };

            /************************************************************
             * 키패드 디비전 관리
             ***********************************************************/
            this.toogleDivision = function () {
            };


            this.clearFocusKeypad = function (initbg) {
                this.hideAll();
                if (typeof (initbg) == 'undefined' || initbg == true) {
                    this.clearBackColor();
                }
            };

            this.Hangul = {
                initial: [12593, 12594, 12596, 12599, 12600, 12601, 12609, 12610, 12611, 12613, 12614, 12615, 12616, 12617, 12618, 12619, 12620, 12621, 12622],
                finale: [0, 12593, 12594, 12595, 12596, 12597, 12598, 12599, 12601, 12602, 12603, 12604, 12605, 12606, 12607, 12608, 12609, 12610, 12612, 12613, 12614, 12615, 12616, 12618, 12619, 12620, 12621, 12622],
                dMedial: [0, 0, 0, 0, 0, 0, 0, 0, 0, 800, 801, 820, 0, 0, 1304, 1305, 1320, 0, 0, 1820],
                dFinale: [0, 0, 0, 119, 0, 422, 427, 0, 0, 801, 816, 817, 819, 825, 826, 827, 0, 0, 1719, 0, 1919],
                SBase: 44032, VCount: 21, LCount: 19, TCount: 28, NCount: 588, VBase: 12623, SCount: 11172,
                composeHangul: function (word) {		//한글 조합
                    var wordLength = word.length;
                    var firstKeyCode = word.charCodeAt(0);
                    var returnValue = word; 						//1자일 경우
                    for (var i = 1; i < wordLength; i++) {
                        var prevKeyCode = word.charCodeAt(i - 1);
                        var curKeyCode = word.charCodeAt(i);
                        var prevWord = String.fromCharCode(prevKeyCode);
                        var idxLastKey = "";
                        var subCurKey = "";
                        var subPrevKey = "";
                        var idxFirstKey = this.findCode(this.initial, prevKeyCode);
                        if (idxFirstKey != -1) {//이전 단어가 초성일 때
                            subCurKey = curKeyCode - this.VBase;
                            if (0 <= subCurKey && subCurKey < this.VCount) {		//현재 단어가 중성일 때
                                combineKeyCode = this.SBase + (idxFirstKey * this.VCount + subCurKey) * this.TCount;
                                returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                                continue;
                            }
                        }
                        subPrevKey = prevKeyCode - this.SBase;
                        if (0 <= subPrevKey && subPrevKey < 11145 && (subPrevKey % this.TCount) == 0) {		//이전 단어가 '히' 전이면  11145 = D789 = '힉';
                            idxLastKey = this.findCode(this.finale, curKeyCode);
                            if (idxLastKey != -1) {
                                combineKeyCode = prevKeyCode + idxLastKey;
                                returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                                continue;
                            }
                            subCurKey = (subPrevKey % this.NCount) / this.TCount;
                            var tmp = this.findCode(this.dMedial, (subCurKey * 100) + (curKeyCode - this.VBase));
                            if (tmp > 0) {
                                combineKeyCode = prevKeyCode + (tmp - subCurKey) * this.TCount;
                                returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                            }
                        }
                        if (0 <= subPrevKey && subPrevKey < 11172 && (subPrevKey % this.TCount) != 0) {		//이전 단어가 한글의 끝 이면 11172 = D7A4 = ' 힤'
                            idxLastKey = subPrevKey % this.TCount;
                            subCurKey = curKeyCode - this.VBase;
                            if (0 <= subCurKey && subCurKey < this.VCount) {		//현재 단어가 중성일 때
                                curKeyCode = this.findCode(this.initial, this.finale[idxLastKey]);
                                if (0 <= curKeyCode && curKeyCode < this.LCount) {
                                    var tmpPrevWord = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(firstKeyCode - idxLastKey);
                                    var tmpFirstKeyCode = this.SBase + (curKeyCode * this.VCount + subCurKey) * this.TCount;
                                    returnValue = tmpPrevWord + String.fromCharCode(tmpFirstKeyCode);
                                    continue;
                                }
                                if (idxLastKey < this.dFinale.length && this.dFinale[idxLastKey] != 0) {
                                    var tmpPrevWord = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(firstKeyCode - idxLastKey + Math.floor(this.dFinale[idxLastKey] / 100)); 	//받침빼기
                                    var tmp = this.findCode(this.initial, this.finale[(this.dFinale[idxLastKey] % 100)])
                                    var tmpFirstKeyCode = this.SBase + (tmp * this.VCount + subCurKey) * this.TCount;
                                    returnValue = tmpPrevWord + String.fromCharCode(tmpFirstKeyCode);
                                }
                            }
                            var tmpIdx = this.findCode(this.finale, curKeyCode);
                            var c = this.findCode(this.dFinale, (idxLastKey * 100) + tmpIdx);
                            if (c > 0) {
                                combineKeyCode = prevKeyCode + c - idxLastKey;
                                returnValue = prevWord.slice(0, prevWord.length - 1) + String.fromCharCode(combineKeyCode);
                                continue;
                            }
                        }
                    }//for end
                    return returnValue;
                }, //end combineHangul
                findCode: function (codeList, key) {
                    for (var i = 0; i < codeList.length; i++) {
                        if (codeList[i] == key) {
                            return i;
                        }
                    }
                    return -1
                },// end findCode
                backSpace: function (word) {
                    var wordLength = word.length;
                    var returnValue = "";
                    var idxFirstKey, idxMiddleKey, idxLastKey;
                    for (var i = 0; i < wordLength; i++) {
                        var curKeyCode = word.charCodeAt(i);
                        var idxCurKey = curKeyCode - this.SBase;
                        if (idxCurKey < 0 || idxCurKey >= this.SCount) {
                            returnValue = String.fromCharCode(curKeyCode);
                            continue;
                        }
                        idxFirstKey = this.initial[Math.floor(idxCurKey / this.NCount)];
                        idxMiddleKey = this.VBase + (idxCurKey % this.NCount) / this.TCount;
                        idxLastKey = this.finale[idxCurKey % this.TCount];
                        returnValue = String.fromCharCode(idxFirstKey, idxMiddleKey);
                        if (idxLastKey != 0) {
                            returnValue = returnValue + String.fromCharCode(idxLastKey);
                        }
                    }
                    return returnValue;
                },
                splitWord: function (word, idxSplit) {
                    var staticWord = word.substring(0, word.length - idxSplit);
                    var tmpWord = word.substring(word.length - idxSplit, word.length);
                    var returnArr = new Array(staticWord, tmpWord);
                    return returnArr;
                }
            };


            this.getHiddenID = function (inputName, formName) {
                var forms = document.getElementsByName(formName);
                if (forms.length == 0 || forms.length > 1) {
                    alert(form + " is not exist or more than one");
                }
                var form = forms.item(0);
                for (var i = 0; i < form.elements.length; i++) {
                    var element = form.elements.item(i);
                    if (element.type == "hidden" && element.value == inputName) {
                        return element.id;
                    }
                }
                return null;
            };

            this.destroy = function (inputName, formName) {
                var forms = document.getElementsByName(formName);
                if (forms.length == 0 || forms.length > 1) {
                    alert(form + " is not exist or more than one");
                }
                var hiddenID = this.getHiddenID(inputName, formName);
                if (hiddenID == null || hiddenID == "" | hiddenID == undefined) return;
                var h1 = hiddenID.substring(hiddenID.lastIndexOf("_") + 1, hiddenID.length);
                var keypaduseyn = npCommon.findElement("_KEYPAD_USEYN_" + h1, formName).value;
                var h = keypaduseyn.substring(keypaduseyn.lastIndexOf("_") + 1, keypaduseyn.length);
                var kp = eval("vKpd" + h);
                kp.destroy();
            };

            this.cls = function (inputName, formName) {
                var forms = document.getElementsByName(formName);
                if (forms.length == 0 || forms.length > 1) {
                    alert(form + " is not exist or more than one");
                }
                var hiddenID = this.getHiddenID(inputName, formName);
                if (hiddenID == null || hiddenID == "" | hiddenID == undefined) return;
                var h1 = hiddenID.substring(hiddenID.lastIndexOf("_") + 1, hiddenID.length);
                var keypaduseyn = npCommon.findElement("_KEYPAD_USEYN_" + h1, formName).value;
                var h = keypaduseyn.substring(keypaduseyn.lastIndexOf("_") + 1, keypaduseyn.length);
                var kp = eval("vKpd" + h);
                kp.cls();
            };

        };


        function npPfsStartup(form, firewall, securekey, fds, e2eattr, e2eval) {
            var options = {
                Firewall: n2b(firewall, false),
                SecureKey: n2b(securekey, false),
                Fds: n2b(fds, false),
                Keypad: true,
                AutoStartup: true,
                Form: (isNull(form)) ? null : form,
                AutoScanAttrName: n2b(e2eattr, "npkencrypt"),
//		AutoScanAttrName : n2b(e2eattr, "enc"),
                AutoScanAttrValue: n2b(e2eval, "on"),
                Debug: true,
                MoveToInstall: function (url) {
                    if (url != null && url != "") {
                        if (confirm("모듈이 설치되어 있지 않습니다. 설치페이지로 이동하시겠습니까?")) {
                            location.replace(url);
                        }
                    } else {
                        alert("모듈이 설치되어 있지 않습니다.");
                    }
                },
                Loading: {
                    Default: false,
                    Before: function () {
                        npPfsCtrl.showLoading();
                    },
                    After: function () {
                        npPfsCtrl.hideLoading();
                    }
                }
            };
            npPfsCtrl.init(options);
        }

        function isNull(val) {
            if (typeof (val) == "undefined" || val == null) return true;
            return false;
        }

        function isBlank(val) {
            if (typeof (val) == "undefined" || val == null || val == "") return true;
            return false;
        }

        function n2b(val, def) {
            def = (isBlank(def)) ? "" : def;
            return (isBlank(val)) ? def : val;
        }

        var npPrinter = window.console || {
            log: function (text) {
//		var p = document.createElement("P");
//		var t = document.createTextNode(text);
//		p.appendChild(t);
//		document.body.appendChild(p);
            }
        };

        var npEfdsUtil = {};
        npEfdsUtil.checkInstallUpdate = function () {
            var message = [];
            message.push("Warning! NOS 설치상태를 SLM 설치확인 함수(npEfdsUtil.checkInstallUpdate)로 확인할 수 없습니다.");
            message.push("         이 함수는 [설치됨]상태를 반환하며 npPfsCtrl.isInstall 함수를 사용하여 아래와 같이 구현하십시오.");
            message.push("         npPfsCtrl.isInstall({");
            message.push("             success : function(){");
            message.push("                 // 설치되어 있을 경우의 처리로직");
            message.push("             },");
            message.push("             fail : function(){");
            message.push("                 // 설치안되어 있을 경우의 처리로직");
            message.push("             }");
            message.push("         });");
            npPrinter.log(message.join("\n"));
            return 0;
        };

        var npEfdsCtrl = {};
        npEfdsCtrl.InitSecuLog = function () {
        }
        npEfdsCtrl.GetSecuLog = function (theform) {
            var message = [];
            message.push("Warning! 본 함수(npEfdsCtrl.GetSecuLog)는 더 이상 지원하지 않습니다. npPfsStartup()를 사용하십시오.");
            npPrinter.log(message.join("\n"));
            //npPfsCtrl.startup(theform);
        }
        npEfdsCtrl.GetBorun = function () {
        }
        npEfdsCtrl.CpSecuLog = function (fromForm, toForm) {
            var message = [];
            message.push("Warning! 본 함수(npEfdsCtrl.CpSecuLog)는 더 이상 지원하지 않습니다. npPfsStartup()를 사용하십시오.");
            npPrinter.log(message.join("\n"));
        }

    </script>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            jQuery("#userAgent").text(navigator.userAgent);
            /*
	npPfsStartup(document.form1, false, true, true, "enc", "on");
	1. form 양식 : 기본값 DOM Document 상의 첫번째 form
	2. 개인방화벽 사용여부 : 기본값 false
	3. 키보드보안 사용여부 : 기본값 false
	4. 단말정보수집 사용여부 : 기본값 false
	5. 키보드보안 E2E 필드 설정 속성명 : 기본값 "enc"
	6. 키보드보안 E2E 필드 설정 속성값: 기본값 "on"
	부가적인 설정은(예, 설치확인 등) /pluginfree/js/nppfs-1.0.0.js를 수정하여 설정하십시오.
*/
            /*
	npPfsStartup(
		document.form2
		, true
		, true
		, true
		, "npkencrypt"
		, "on"
	);
*/
            npPfsStartup(document.form2, true, true, true, "npkencrypt", "on");
        });


        function decryptKeyCryptData() {
            npPfsCtrl.waitSubmit(function () {
                document.form1.submit();
            });
        }

        function doDecrypt() {
            npPfsCtrl.waitSubmit(function () {
                document.form2.submit();
            });
        }

    </script>

</head>
<!-- <body oncontextmenu="return false" onselectstart="return false" ondragstart="return false"> -->
<body>
<table>
    <tr>
        <th style="text-align:left;font-size:14pt;">접속정보</th>
    </tr>
    <tr>
        <td>
            <span id="userAgent"></span>
        </td>
    </tr>
</table>

<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">

    <table>
        <tr>
            <th style="text-align:left;font-size:14pt;">개인방화벽 테스트</th>
        </tr>
        <tr>
            <td>
                <input type="button" name="startNos" id="startNos" value="방화벽 시작" onclick="npNCtrl.start();">
                <input type="button" name="stopNos" id="stopNos" value="방화벽 종료" onclick="npNCtrl.stop();">

            </td>
        </tr>
    </table>
</div>

<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">

    <form name="form1" action="decrypt.jsp" method="post" target="resultTarget">
        <!-- <div id="nppfs-loading-modal" style="display:none;"></div>
<div class="nppfs-elements" style="display:none;"></div> -->

        <input type="hidden" name="mode" value="KEYCRYPT"/>
        <table width="100%">
            <colgroup>
                <col width="10%"></col>
                <col width="90%"></col>
            </colgroup>
            <tr>
                <th colspan="2" style="text-align:left;font-size:14pt;">키보드보안 테스트</th>
            </tr>
            <tr>
                <td> FormOut ID</td>
                <td><input type="text" name="NONE_TEXT_4" id="t4" value=""></td>
            </tr>
            <tr>
                <td> FormOut PW</td>
                <td><input type="password" name="NONE_PASS_4" id="p4" value=""></td>
            </tr>
            <tr>
                <td>E2E Id(Inca):</td>
                <td><input type="text" name="E2E_TEXT_1" id="t1" style="ime-mode:disabled;" npkencrypt="on" value=""
                           maxlength="4"/> : 4글자
                </td>
            </tr>
            <tr>
                <td>E2E PW(Inca):</td>
                <td><input type="password" name="E2E_PASS_1" id="p1" style="ime-mode:disabled;" npkencrypt="on" value=""
                           maxlength="6"/> : 6글자
                </td>
            </tr>
            <tr>
                <td>E2E Card(Inca):</td>
                <td>
                    <input type="password" name="cardNo1" id="cardNo1" style="ime-mode:disabled;" npkencrypt="on"
                           value="" maxlength="4" size="4" style="width:20px;"/>
                    <input type="password" name="cardNo2" id="cardNo2" style="ime-mode:disabled;" npkencrypt="on"
                           value="" maxlength="4" size="4" style="width:20px;"/>
                    <input type="password" name="cardNo3" id="cardNo3" style="ime-mode:disabled;" npkencrypt="on"
                           value="" maxlength="4" size="4" style="width:20px;"/>
                    <input type="password" name="cardNo4" id="cardNo4" style="ime-mode:disabled;" npkencrypt="on"
                           value="" maxlength="4" size="4" style="width:20px;"/>
                </td>
            </tr>
            <tr>
                <td>E2E Id(Raon):</td>
                <td><input type="text" name="E2E_RAON_TEXT_1" id="rt2" style="ime-mode:disabled;" enc="on" value=""
                           maxlength="4"/> : 4글자
                </td>
            </tr>
            <tr>
                <td>E2E PW(Raon):</td>
                <td><input type="password" name="E2E_RAON_PASS_1" id="rp2" style="ime-mode:disabled;" enc="on" value=""
                           maxlength="6"/> : 6글자
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="button" name="getClientKey" id="getClientKey" value="복호화"
                           onclick="decryptKeyCryptData();">
                </td>
            </tr>
        </table>
    </form>
</div>


<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">
    <form name="form2" action="decrypt.jsp" method="post" target="resultTarget">
        <!-- <div class="nppfs-elements" style="display:none;"></div> -->
        <input type="hidden" name="mode" value=""/>
        <table>
            <tr>
                <th style="text-align:left;font-size:14pt;"> 단말정보수집 테스트</th>
            </tr>
            <tr>
                <td><input type="button" name="doDec" onclick="doDecrypt();" value="복호화"/></td>
            </tr>
        </table>
    </form>
</div>

<div style="margin-bottom:20px; padding:10px; border:1px solid #000;">
    <table width="100%">
        <tr>
            <th style="text-align:left;font-size:14pt;"> 복호화 테스트</th>
        </tr>
        <tr>
            <td>

                <iframe id="resultTarget" name="resultTarget" src="about:blank"
                        style="border:0px solid #000;width:100%;height:500px;"></iframe>


            </td>
        </tr>
    </table>
</div>


</body>
</html>
