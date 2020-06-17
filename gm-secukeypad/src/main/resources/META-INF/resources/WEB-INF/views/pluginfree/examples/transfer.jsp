<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"
%><%@page import="java.util.Enumeration"
%><%@page import="java.util.List"
%><%@page import="java.util.Collections"
%><%@page import="java.security.KeyPair"
%><%@page import="java.security.PrivateKey"
%><%@page import="java.security.PublicKey"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%@page import="com.nprotect.pluginfree.util.PKIUtil"
%><%@page import="com.nprotect.pluginfree.util.StringUtil"
%><%@page import="com.nprotect.pluginfree.PluginFreeTransfer"
%><%@page import="com.nprotect.pluginfree.PluginFreeException"
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeDecrypt"
%><%@page import="com.nprotect.pluginfree.modules.PluginFreeRequest"
%><%@page import="com.nprotect.pluginfree.transfer.Transfer"
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


//HttpServletRequest를 인자값으로 하는 PluginFreeRequest객체를 생성합니다.
ServletRequest pluginfreeRequest = new PluginFreeRequest(request);

String cardNo1 = pluginfreeRequest.getParameter("cardNo1");
String cardNo2 = pluginfreeRequest.getParameter("cardNo2");
String cardNo3 = pluginfreeRequest.getParameter("cardNo3");
String cardNo4 = pluginfreeRequest.getParameter("cardNo4");

out.println("수동복호화(cardNo1) : " + cardNo1 + "<br />");
out.println("수동복호화(cardNo2) : " + cardNo2 + "<br />");
out.println("수동복호화(cardNo3) : " + cardNo3 + "<br />");
out.println("수동복호화(cardNo4) : " + cardNo4 + "<br />");



/*
Enumeration<String> enumeration = request.getParameterNames();
List<String> list = Collections.list(enumeration);
if(list.size()>0){
	Collections.sort(list);
	for(String key : list) {
		try{
			//String key = (String)e.nextElement();
			String value = (String)request.getParameter(key);
			//out.println("nProtect Plugin Free Service (Mobile), Request [" + key + "][" + value + "]");
			System.out.println("nProtect Plugin Free Service (Transfer), Request [" + key + "][" + value + "]");
			//out.println("// " + key + "  => " + value + "");
		} catch(Exception ex){}
	}
	//out.println();
	//out.println();
	//out.println();
	System.out.println("nProtect Plugin Free Service (Transfer), -----------------------");
}
*/

PublicKey publicKey = null;
PrivateKey privateKey = null;

//	// 고정된 공개키쌍 사용
//	String pubstr = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100a06e8363a9da248d179ba43bbabf4d73ef95718c0f5c67fabad6bf3b11b3ea1158e103b2f227afffa00ec8a5c7593e0d99e274b8671eec44ed066def2c37b256bc7b1e2fa7fae685d17e13c8bd810770ed6b8567395215b492cd1f2541d85dd098adab1cf048f4326c380cd4b06d5ab4a7c01834da597974f56cdca68bb56276091fc14a5f8d649ce78849de3995f136c07d289fec850cdb41c45944b131bd7a6c347b1c60612dc44027a9c910b171d9559f492e9d3cb14becedb5b448fed6b78b41de290e535a718f4f0c7f817eff8cab032c6284db3d64ce3e978e479d6289784294c20daec5eef271b9124569e6744857fcfe8968843b83607764a96a2c9f0203010001";
//	X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(StringUtil.hexDecode(pubstr));
//	KeyFactory keyFact1 = KeyFactory.getInstance("RSA");
//	publicKey = keyFact1.generatePublic(pubKeySpec);
//
//	String pristr = "308204bd020100300d06092a864886f70d0101010500048204a7308204a30201000282010100a06e8363a9da248d179ba43bbabf4d73ef95718c0f5c67fabad6bf3b11b3ea1158e103b2f227afffa00ec8a5c7593e0d99e274b8671eec44ed066def2c37b256bc7b1e2fa7fae685d17e13c8bd810770ed6b8567395215b492cd1f2541d85dd098adab1cf048f4326c380cd4b06d5ab4a7c01834da597974f56cdca68bb56276091fc14a5f8d649ce78849de3995f136c07d289fec850cdb41c45944b131bd7a6c347b1c60612dc44027a9c910b171d9559f492e9d3cb14becedb5b448fed6b78b41de290e535a718f4f0c7f817eff8cab032c6284db3d64ce3e978e479d6289784294c20daec5eef271b9124569e6744857fcfe8968843b83607764a96a2c9f020301000102820100322579123cf43fba8e678af55491195fa4c2bca43fe4ed6774e14d12e49cad0c5110bc7c41aee01771eb4d126c765bac1aaeab373c9c70d3b696ece3f6994e38485fdf769bf613fa3e1a3f8ade99273f4826f4a2e84add17fd4efa6e45dfa0ab641ddcbf85e7f7d48ef91221a527f95340a00db0ef934a20a1da2e3a2caf3ca013d49db77e7494fed13eba2e44d56554c309483fe50993078d994779cac44f599d7bb4b310a535d0d620bc6422892f10b4c72610334ff0b709e30e8f7649f15699011e94d719da2a901dd40b76b0e6e4e04e9805de1275e036dffa16c0818fc5d89ddaa015348f1bacc45c550fae0dc66a0f82e3068280ad3f75e2aebcb21f1902818100ccfc4f0be49917ffc4f9a660a3252413dadae0527c5615e6df1e5030cfc91991685459649e25e2db970c64caa690b147fa7e542798e0522afbe03596e9193af8ea25c1e401dcd24c582e880a21544daf16b2c6abe25566835b20456017fc2602997aabdedf9f1610a1f60db73da0e7caebd7919ec2b09606d5d6d7f45d18db0d02818100c85ba9c7750ad35d08fe7e565272ec7a089cc1554ec5f3b42e403f33784215f220a3b4e8e475129f3aad23d0e6ce56feb4cbd141bc4a06968af9f980ed5b78c57c6282cb91df2d8cbfc0538de5e12afd1e7fdb4a9c2b9649ba32ddcfed3795700229db9769bd1a6d4e587d3c900ce8f6050952fc620fbb5241a656c93f25cb5b0281805ac97cb105c4106f056c9495c46c14b87e7be6526223367c1461b69e87c8c77c313afa84a7ce9bd529e72154e7c4b9dfe93fbe41f36196c2d6df8c9c940ccaa3a800a5093911f64a3ddc0e007e9679f98c120e0fdea4784cc1355fc4999ae1b2d10b15c8163ebd650c768fc892910b5842702d5ca559d4789e891308759b269902818031f05ef5ff1f4ea57ecb6813fe02f51c49af40a511b857510ec226be9e77e25e72723b725d172d281108fcc761f006510021592c08516f28f0c4f3c285e6e9c857837a54612c7e7ef980679313bc36e9d6434a1663ac9d8e0ce206d57fabfe0c680da4d52d9edbca68dfb77f73ec33d8b652a7a38e919b401a6aea70c8d393c70281810097379ff587be5d37267d516f350f68c2180d836e24428fb6da49bdfa3a170a65977fd77c457105a3d9709051cfadbb9944506824a030f31475d4e344137cd69c910e4fa71156a0d01e50766589e6a12f78d86058ffe7b1628a0adb099421669b09533463e6b6ebab5457f6bf59b6f085fb81a91c28dee21d613bb1fad3b786f6";
//	PKCS8EncodedKeySpec privKeySpec1 = new PKCS8EncodedKeySpec(StringUtil.hexDecode(pristr));
//	KeyFactory keyFact2 = KeyFactory.getInstance("RSA");
//	privateKey = keyFact2.generatePrivate(privKeySpec1);

// 실행시마다 임의의 공개키쌍 생성
KeyPair keyPair = PKIUtil.generateKeyPair();	// default : 2048
publicKey = keyPair.getPublic();
privateKey = keyPair.getPrivate();

byte[] pubkbytes = publicKey.getEncoded();
byte[] prikbytes = privateKey.getEncoded();

String pubstr = StringUtil.hexEncode(pubkbytes);
String pristr = StringUtil.hexEncode(prikbytes);

System.out.println("nProtect Plugin Free Service (Transfer), RSA-2048(Public Key) : [" + pubstr + "]");
System.out.println("nProtect Plugin Free Service (Transfer), RSA-2048(Private Key): [" + pristr + "]");

Transfer transfer = PluginFreeTransfer.getInstance(Transfer.KCP_QUICK_PAY, publicKey, request);
System.out.println("nProtect Plugin Free Service (Transfer),   Key [" + transfer.getKey() + "]");
System.out.println("nProtect Plugin Free Service (Transfer), Card1 [" + transfer.get("cardNo1") + "]");
System.out.println("nProtect Plugin Free Service (Transfer), Card2 [" + transfer.get("cardNo2") + "]");
System.out.println("nProtect Plugin Free Service (Transfer), Card3 [" + transfer.get("cardNo3") + "]");
System.out.println("nProtect Plugin Free Service (Transfer), Card4 [" + transfer.get("cardNo4") + "]");
%>