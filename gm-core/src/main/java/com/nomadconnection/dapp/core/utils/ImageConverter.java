package com.nomadconnection.dapp.core.utils;

import com.nomadconnection.dapp.core.config.CrownixConfig;
import com.nomadconnection.dapp.core.dto.ImageConvertDto;
import lombok.RequiredArgsConstructor;
import m2soft.ers.invoker.InvokerException;
import m2soft.ers.invoker.http.ReportingServerInvoker;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class ImageConverter {

	private final CrownixConfig crownixConfig;
	private final EnvUtil envUtil;

	private String uri;
	private ReportingServerInvoker invoker;
	private String response;

	private class ImageConvertParam {
		// parameter for image convert
		public static final String OPCODE = "opcode";
		public static final String MRD_NAME = "mrd_path";
		public static final String MRD_PARAM = "mrd_param";
		public static final String EXPORT_TYPE = "export_type";
		public static final String EXPORT_NAME = "export_name";
		public static final String PROTOCOL = "protocol";

		// mrd file (1510:사업자등록증, 1520:재무제표, 1530:법인등기부등본)
		public static final String CORP_REGISTRATION_MRD = "1510.mrd";
		public static final String FINANCIAL_STATEMENTS_MRD = "1520.mrd";
		public static final String COPY_REGISTER_MRD = "1530.mrd";
	}

	@PostConstruct
	private void init() throws URISyntaxException {
		setURI();
		setConnectionInfo();
	}

	private void setURI() throws URISyntaxException {
		URI uri;
		if(envUtil.isProd()){
			uri = new URIBuilder(crownixConfig.getProdUrl()).setScheme(crownixConfig.getProtocol()).setPort(crownixConfig.getPort()).setPath(crownixConfig.getEndPoint()).build();
		} else if(envUtil.isStg()){
			uri = new URIBuilder(crownixConfig.getStgUrl()).setScheme(crownixConfig.getProtocol()).setPort(crownixConfig.getPort()).setPath(crownixConfig.getEndPoint()).build();
		} else {
			return;
		}
		this.uri = uri.toString();
	}

	private void setConnectionInfo(){
		invoker = new ReportingServerInvoker(uri);
		invoker.setCharacterEncoding("utf-8");
		invoker.setReconnectionCount(5);
		invoker.setConnectTimeout(180);
		invoker.setReadTimeout(180);
	}

	public String convertJsonToImage(ImageConvertDto params) throws Exception {

		setParameters(params);
		response = invoker.invoke();	// convert
		isSuccess();
		return response;
	}

	private void setParameters(ImageConvertDto params) {
		String targetData = params.getData().toString();

		isNotNullData(targetData);
		String mrdParam = getMrdParam(targetData);

		invoker.addParameter(ImageConvertParam.OPCODE, params.getOpCode());
		invoker.addParameter(ImageConvertParam.MRD_NAME, getMrdPath(params.getMrdType()));
		invoker.addParameter(ImageConvertParam.MRD_PARAM, mrdParam);
		invoker.addParameter(ImageConvertParam.EXPORT_TYPE, params.getExportType());
		invoker.addParameter(ImageConvertParam.EXPORT_NAME, params.getFileName().concat("."+params.getExportType() ));
		invoker.addParameter(ImageConvertParam.PROTOCOL, params.getProtocol());
	}

	private String getMrdPath(int mrdType) {
		String mrdName;
		switch (mrdType) {
			case 1510:
				mrdName = ImageConvertParam.CORP_REGISTRATION_MRD;
				break;
			case 1520:
				mrdName = ImageConvertParam.FINANCIAL_STATEMENTS_MRD;
				break;
			case 1530:
				mrdName = ImageConvertParam.COPY_REGISTER_MRD;
				break;
			default:
				throw new IllegalArgumentException("Not Found MrdType.");
		}
		return mrdName;
	}

	private String getMrdParam(String targetData){
		return  "/rdata [" + targetData + "]";
	}

	public void isSuccess() throws Exception {
		if(!response.startsWith("1")){
			throw new InvokerException(response);
		}
	}

	private void isNotNullData(String targetData) {
		if(StringUtils.isEmpty(targetData)){
			throw new NullPointerException(targetData);
		}
	}

}
