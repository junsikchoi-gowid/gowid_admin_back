package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.api.dto.CorpDto;
import com.nomadconnection.dapp.api.dto.RiskDto;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.repository.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.AdminCustomRepository;
import com.nomadconnection.dapp.core.domain.repository.querydsl.CorpCustomRepository;
import com.nomadconnection.dapp.core.dto.response.BusinessResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.convert.ConversionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import javax.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.security.auth.callback.*;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AdminService {

	private final EmailConfig config;
	private final ITemplateEngine templateEngine;

	private final JwtService jwt;
	private final JavaMailSenderImpl sender;

	private final UserService serviceUser;
	private final RiskRepository repoRisk;
	private final CorpRepository repoCorp;

	private final ResAccountHistoryRepository resAccountHistoryRepository;
	private final RiskConfigRepository repoRiskConfig;

	final static String BEGIN = "-----BEGIN CERTIFICATE-----\n";
	final static String PEMS = "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";
	final static String END = "\n-----END CERTIFICATE-----";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public boolean getVid(){

		BASE64Encoder encoder = new BASE64Encoder();

		try {
			// String leafPublicCertificatePem = BEGIN + PEMS + END;

			String pem = BEGIN+ "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"+END;
			byte[] encoded = Base64.decodeBase64(pem);
			PKCS8EncodedKeySpec spec = new  PKCS8EncodedKeySpec(encoded);
			ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
			Reader reader = new InputStreamReader(bis);

			try {
				final StringReader stringReader = new StringReader(pem);
				final PemReader pemReader = new PemReader(stringReader);
				final byte[] x509Data = pemReader.readPemObject().getContent();
				final CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
				final Certificate certificate = certificateFactory.generateCertificate(
						new ByteArrayInputStream(x509Data));
				log.debug(Base64.encodeBase64String(certificate.getEncoded()));
			} catch (final Exception e) {
				e.printStackTrace();
			}

			KeyStore ks=KeyStore.getInstance("PKCS12");
			ks.load(bis,"ymp29088mp!".toCharArray());
			Certificate cert=ks.getCertificate("alias");

			log.debug(cert.getPublicKey().getFormat());

			X509Certificate x509Cert = X509Certificate.getInstance(encoded);

			KeyFactory kf = KeyFactory.getInstance("RSA");
			String caPassword = "ymp29088mp!";
			System.out.println(x509Cert.getIssuerDN());
			System.out.println(x509Cert.getNotAfter());
			System.out.println(x509Cert.getSubjectDN());


			log.debug(spec.getFormat());

			log.debug(kf.getAlgorithm());
			RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));



//			KeyStore ks=KeyStore.getInstance("PKCS12");
			ks.load(new ByteArrayInputStream(pem.getBytes()),"password".toCharArray());
			//Certificate cert=ks.getCertificate("alias");




		} catch(Exception e) {
			e.printStackTrace();
		}
		//String certVID = getVitualIdFromCert(cert);
		String genVID = null;
		//String idRandumNum = getIdRandomNumber(privateKeySpec);
		//byte[] result = CertificateUtil.generateVID(idNum, ByteUtil.hexToByteArray(idRandumNum), hashAlg);

		//genVID = ByteUtil.byteArrayToHex(result);

//		if(!certVID.equals(genVID)){
//			return false;
//		}

		return true;
	}



	public ResponseEntity riskList(AdminCustomRepository.SearchRiskDto riskDto, Long idxUser, Pageable pageable) {

		//	todo: idxUser Auth check

		Page<AdminCustomRepository.SearchRiskResultDto> resAccountPage = repoRisk.riskList(riskDto, idxUser, pageable);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(resAccountPage).build());
	}


	public ResponseEntity corpList(CorpCustomRepository.SearchCorpDto corpDto, Long idxUser, Pageable pageable) {

		//	todo: idxUser Auth check

		Page<CorpCustomRepository.SearchCorpResultDto> page = repoCorp.corpList(corpDto, idxUser, pageable);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(page).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity riskIdLevelChange(Long idxUser, RiskDto.RiskConfigDto dto) {

		//	todo: idxUser Auth check

		Optional<RiskConfig> riskConfig = repoRiskConfig.findByUserAndEnabled(User.builder().idx(idxUser).build(), true);

		Boolean cardIssuance = false;

		if(riskConfig.isPresent()){
			riskConfig.ifPresent( x -> repoRiskConfig.save(
					RiskConfig.builder()
							.idx(x.idx())
							.enabled(false)
							.build()
			));

			cardIssuance = riskConfig.get().cardIssuance();
		}

		return ResponseEntity.ok().body(BusinessResponse.builder().data(
				repoRiskConfig.save(RiskConfig.builder()
						.user(User.builder().idx(idxUser).build())
						.enabled(true)
						.ceoGuarantee(dto.isCeoGuarantee())
						.cardIssuance(cardIssuance)
						.ventureCertification(dto.isVentureCertification())
						.vcInvestment(dto.isVcInvestment())
						.depositPayment(dto.isDepositPayment())
						.corp(User.builder().idx(idxUser).build().corp())
						.depositGuarantee(dto.getDepositGuarantee())
						.build())
		).build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity saveEmergencyStop(Long idxUser, Long idxCorp, String booleanValue) {

		//	todo: idxUser Auth check

		String calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		Risk risk = repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), calcDate).orElseThrow(
				() -> new RuntimeException("Empty Data")
		);

		if(booleanValue != null){
			if(booleanValue.toLowerCase().equals("true")){
				risk.emergencyStop(true);
			}else{
				risk.emergencyStop(false);
			}
		}

		repoRisk.save(risk);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity savePause(Long idxUser, Long idxCorp, String booleanValue) {

		//	todo: idxUser Auth check

		String calcDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

		Risk risk = repoRisk.findByCorpAndDate(Corp.builder().idx(idxCorp).build(), calcDate).orElseThrow(
				() -> new RuntimeException("Empty Data")
		);

		if(booleanValue != null){
			if(booleanValue.toLowerCase().equals("true")){
				risk.pause(true);
			}else{
				risk.pause(false);
			}
		}

		repoRisk.save(risk);

		return ResponseEntity.ok().body(BusinessResponse.builder().build());
	}

	public ResponseEntity riskListSelected(AdminCustomRepository.SearchRiskDto riskDto, Long idx, Long idxCorp, Pageable pageable) {

		Corp corp = repoCorp.findById(idxCorp).orElseThrow(
				() -> new RuntimeException("Bad idxCorp request.")
		);

		Page<RiskDto> result = repoRisk.findByCorp(corp, pageable).map(RiskDto::from);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(result).build());
	}

	public ResponseEntity corpId(Long idx, Long idxCorp) {

		Optional<CorpDto> corp = repoCorp.findById(idxCorp).map(CorpDto::from);

		return ResponseEntity.ok().body(BusinessResponse.builder().data(corp.get()).build());
	}
}