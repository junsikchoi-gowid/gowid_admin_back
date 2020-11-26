package com.nomadconnection.dapp.core.utils;

import com.nomadconnection.dapp.core.config.CrownixConfig;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.dto.ImageConvertDto;
import com.nomadconnection.dapp.core.dto.ImageConvertRespDto;
import com.nomadconnection.dapp.core.exception.ImageConvertException;
import com.nomadconnection.dapp.core.exception.error.ImageConvertErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2soft.ers.invoker.InvokerException;
import m2soft.ers.invoker.http.ReportingServerInvoker;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
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
		public static final String FAX_RESERVED2 = "fax_reserved2";

		// mrd file (1510:사업자등록증, 1520:재무제표, 1530:법인등기부등본, guarantee: 지급보증)
		public static final String CORP_REGISTRATION_MRD = "1510.mrd";
		public static final String FINANCIAL_STATEMENTS_MRD = "1520.mrd";
		public static final String COPY_REGISTER_MRD = "1530.mrd";
		public static final String GUARANTEE_MRD = "guarantee.mrd";
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

	public ImageConvertRespDto convertJsonToImage(ImageConvertDto params) throws Exception {
		if(!crownixConfig.isEnabled()) {
			log.debug("Crownix Image Server is disabled.");
			return ImageConvertRespDto.builder()
				.isSuccess(true)
				.build();
		}
		setParameters(params);
        try {
            response = invoker.invoke();    // convert
        } catch (InvokerException e){
			throw new ImageConvertException(ImageConvertErrorMessage.INTERNAL_ERROR, e);
        }

		return ImageConvertRespDto.builder()
				.isSuccess(isSuccess(response))
				.totalPageCount(invoker.getTotalPage())
				.build();
    }

	private void setParameters(ImageConvertDto params) {
        String targetData = params.getData();
        isNotNullData(targetData);
        String mrdParam = getMrdParam(targetData);
		setLotteImageExtension(params);
        invoker.addParameter(ImageConvertParam.OPCODE, params.getOpCode());
        invoker.addParameter(ImageConvertParam.MRD_NAME, getMrdPath(params.getMrdType()));
        invoker.addParameter(ImageConvertParam.MRD_PARAM, mrdParam);
        invoker.addParameter(ImageConvertParam.EXPORT_TYPE, params.getExportType());
        invoker.addParameter(ImageConvertParam.EXPORT_NAME, params.getFileName().concat("." + params.getExportType()));
        invoker.addParameter(ImageConvertParam.PROTOCOL, params.getProtocol());
        invoker.addParameter(ImageConvertParam.FAX_RESERVED2, "1"); // 이미지 해상도 조정
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
			case 9991:
				mrdName = ImageConvertParam.GUARANTEE_MRD;
				break;
            default:
                throw new IllegalArgumentException("Not Found MrdType.");
        }
        return mrdName;
    }

    private String getMrdParam(String targetData) {
        return "/rdata [" + targetData + "]";
    }

    private boolean isSuccess(String response) throws Exception {
        if (!response.startsWith("1")) {
            throw new InvokerException(response);
        }
        return true;
    }

    private void isNotNullData(String targetData) {
        if (StringUtils.isEmpty(targetData)) {
            throw new NullPointerException(targetData);
        }
    }

    private void setLotteImageExtension(ImageConvertDto params){
	    if(CardCompany.isLotte(params.getCardCompany())){
		    params.setExportType("jpg");
	    }
    }

}
