package com.nomadconnection.dapp.api.v2.controller.card;

import com.nomadconnection.dapp.api.abstracts.AbstractWebMvcTest;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.exception.BadRequestedException;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShinhanCardControllerV2Test extends AbstractWebMvcTest {


	@Test
	@Transactional
	void application() throws Exception {

		mockMvc.perform(
			post(ShinhanCardControllerV2.URI.BASE + ShinhanCardControllerV2.URI.APPLY)
				.param("cardIssuanceInfoIdx", "968")
				.param("signedBinaryString", "3082144b06092a864886f70d010702a082143c30821438020101310f300d0609608648016503040201050030820cab06092a864886f70d010701a0820c9c04820c987b22ec9db4ec9aa9ec95bdeab48020eb8f99ec9d98223a7b22eab8b0ec9785ec8ba0ec9aa9eca095ebb3b420ec8898eca7912c20ec9db4ec9aa92c20eca09ceab3b520ebb08f20eca1b0ed9a8cec979020eab480ed959c20ec82aced95ad2028ed9584ec889829223a22eb8f99ec9d98ed95a8222c22eab3a0ec9c84eb939c20ec8aa4ed8380ed8ab8ec978520ecb9b4eb939cebb09ceab88920ec9db4ec9aa9ec95bdeab48028ed9584ec889829223a22eb8f99ec9d98ed9598eca78020ec958aec9d8c222c22eba1afeb8db020ebb295ec9db8ecb9b4eb939c20ed9a8cec9b9020ec95bdeab48020eb8f99ec9d982028ed9584ec889829223a22eb8f99ec9d98ed95a8222c22eab09cec9db828ec8ba0ec9aa929eca095ebb3b420ec8898eca79120ebb08f20ec9db4ec9aa9eb8f99ec9d98223a22eb8f99ec9d98ed9598eca78020ec958aec9d8c222c22eab480eba6acecb185ec9e84ec9e9020ec9785ebacb420ec9c84ec9e8420eb8f99ec9d982028ed9584ec88982920223a22eb8f99ec9d98ed95a8222c22eab09cec9db828ec8ba0ec9aa929eca095ebb3b420ed9584ec8898eca08120ec8898eca791c2b7ec9db4ec9aa9ec979020eab480ed959c20ec82aced95ad2028ed9584ec8898292020223a22eb8f99ec9d98ed95a8222c22eab09cec9db828ec8ba0ec9aa929eca095ebb3b420eca1b0ed9a8cec979020eab480ed959c20ec82aced95ad2028ed9584ec8898292020223a22eb8f99ec9d98ed95a8222c22eab09cec9db828ec8ba0ec9aa929eca095ebb3b420ed9584ec8898eca08120ec8898eca791c2b7ec9db4ec9aa9ec979020eab480ed959c20ec82aced95ad2028ed9584ec889829223a22eb8f99ec9d98ed95a8222c22ebb295ec9db8ecb9b4eb939c20ec8ba0ecb2adec9d8420ec9c84ed959c20eab09cec9db828ec8ba0ec9aa929eca095ebb3b420ed9584ec8898eca08120eca09ceab3b5ec979020eab480ed959c20eb8f99ec9d982028ed9584ec889829223a22eb8f99ec9d98ed95a8222c22ec9e90eb8f99ec9db4ecb2b420ec95bdeab4802028ed9584ec889829223a22eb8f99ec9d98ed95a8222c22ec97aced9689ec9e90ebb3b4ed979820ec9db4ec9aa920eb8f99ec9d982028ec84a0ed839d292020223a22eb8f99ec9d98ed95a8222c22eca09ced9cb4ec95bdeca095ed959ceb8f8420eab480eba6aceba5bc20ec9c84ed959c20eab09cec9db828ec8ba0ec9aa929eca095ebb3b420ed9584ec8898eca08120eca09ceab3b5ec979020eab480ed959c20eb8f99ec9d982028ed9584ec889829223a22eb8f99ec9d98ed95a8227d2c22ebb295ec9db820eca095ebb3b4223a7b22ebb295ec9db8ebaa8528eab5adebacb829223a2228eca3bc29eba088ec9db4ed8bb0ec8aa4ed8ab8ecbc80ec9db4222c22ebb295ec9db8ebaa8528ec9881ebacb829223a224c4154455354204b222c22ec82acec9785ec9e90eb93b1eba19debb288ed98b8223a223534392d38372d3030373030222c22ebb295ec9db8eb93b1eba19debb288ed98b8223a223131303131312d36343935353632222c22ec9785eca285223a22eb8db0ec9db4ed84b0ebb2a0ec9db4ec8aa420ebb08f20ec98a8eb9dbcec9db820eca095ebb3b420eca09ceab3b5ec9785222c22ec82acec9785ec9ea520eca084ed9994ebb288ed98b8223a2230322d363030362d37353937222c22ebb3b8eca09020eca3bcec868c223a22ec849cec9ab820ec86a1ed8c8ceab5ac20ec86a1ed8c8ceb8c80eba19c2032303120ed858ceb9dbced8380ec9b8c3220413331362028303538353429227d2c22ebb2a4ecb298eab8b0ec978520eca095ebb3b4223a7b22ebb2a4ecb298eab8b0ec9785ed9995ec9db8ec849c20ebb3b4ec9ca0223a22ec9584eb8b88ec9a942c20ebb2a4ecb298eab8b0ec9785ed9995ec9db8ec849ceba5bc20ebb3b4ec9ca0ed9598eab3a020ec9e88eca78020ec958aec8ab5eb8b88eb8ba42e222c223130ec96b520ec9b9020ec9db4ec8381ec9d9820564320ed88acec9e90223a22ec9584eb8b88ec9a942c20eb8884eca081ed88acec9e90eab888ec95a1ec9db4203130ec96b5ec9b9020ebafb8eba78cec9e85eb8b88eb8ba42e227d2c22eca3bceca3bcebaa85ebb680223a7b22eca3bceca3bcebaa85ebb68031223a22eca09cecb69c222c22eca3bceca3bcebaa85ebb68032223a22eca09cecb69c227d2c22eca3bceca3bceca095ebb3b4223a7b2232352520ec9db4ec8381ec9d9820eca780ebb684ec9d8420ebb3b4ec9ca0ed959c20eab09cec9db8ec9db420ec9e88ec9cbcec8ba0eab080ec9a943f223a22ec98882c20eca084ecb2b420eca780ebb684ec9d9820323525ec9db4ec8381ec9d8420ebb3b4ec9ca0ed959c20eab09cec9db8ec9db420ec9e88ec8ab5eb8b88eb8ba42e222c2231eb8c8020eca3bceca3bceab08020eab09cec9db8ec9db8eab080ec9a943f20ebb295ec9db8ec9db8eab080ec9a943f223a22ed95b4eb8bb920ec9786ec9d8c222c2231eb8c8020eca3bceca3bc20ebb295ec9db8ec9d9820eca3bceca3bcebaa85ebb680eba5bc20ebb3b4ec9ca0ed9598eab3a020eab384ec8ba0eab080ec9a943f223a22ed95b4eb8bb920ec9786ec9d8c227d2c22ebb295ec9db820ec8ba4eca09cec868cec9ca0ec9e9020eca095ebb3b4223a7b22eca3bceca3bc20ec9db4eba68428ed959ceab88029223a22eab980eab2bded9b88222c22ec839deb8584ec9b94ec9dbc2036ec9e90eba6ac223a22383731303237222c22eca3bceca3bc20ec9db4eba68428ec9881ebacb829223a224b494d204b59554e4720484f4f4e222c22eca780ebb684ec9ca8223a22393025222c22eab5adeca081223a224b52227d2c2231eb8c8020eca3bceca3bc20ebb295ec9db8ec9d9820eca3bceca3bcebaa85ebb680223a7b7d2c22ecb9b4eb939c20ebb09ceab88920eca095ebb3b4223a7b22ec8381ed9288ebaa85223a22eab3a0ec9c84eb939c20ec8aa4ed8380ed8ab8ec978520ecb9b4eb939c222c22ebaa85ec9d9820eab5acebb684223a22ebb295ec9db8ebaa85ec9d9820ecb9b4eb939c222c22eab7b8eba6b020ebb984eab590ed86b5ecb9b4eb939c20ec8ba0ecb2ad20ec8898eb9f89223a223020eba7a4222c22eab7b8eba6b020eab590ed86b5ecb9b4eb939c20ec8ba0ecb2ad20ec8898eb9f89223a223020eba7a4222c22ebb894eb9e9920ebb984eab590ed86b5ecb9b4eb939c20ec8ba0ecb2ad20ec8898eb9f89223a223220eba7a4222c22ebb894eb9e9920eab590ed86b5ecb9b4eb939c20ec8ba0ecb2ad20ec8898eb9f89223a223020eba7a4222c22ed9598ec9db4ed8ca8ec8aa420eca084ec9aa920ecb9b4eb939c20ec8ba0ecb2ad20ec8898eb9f89223a223020eba7a4222c22ed95b4ec99b8eab2b0eca09c20ebb88ceb9e9ceb939c223a22eba788ec8aa4ed84b0ecb9b4eb939c222c22ebaa85ec84b8ec849c20ec8898eba0b9ebb0a9ebb295223a22ec9ab0ed8eb820ebb08f20ec9db4eba994ec9dbc222c22ec8898eba0b9eca3bcec868c223a22ec849cec9ab820ec86a1ed8c8ceab5ac20ec86a1ed8c8ceb8c80eba19c2032303120ed858ceb9dbced8380ec9b8c3220413331362028303538353429227d2c22ed9daceba79ded959ceb8f84223a7b22ed9daceba79ded959ceb8f84eb8a9420ec96bceba788ec9db8eab080ec9a943f223a222020ec9b90222c22ec9db4ec9aa9eab080eb8aa5ed959ceb8f84223a222031333030eba78c20ec9b90227d2c22eab2b0eca09c20eab384eca28c223a7b22eab2b0eca09c20eab384eca28c223a22ec8ba0ed959cec9d80ed96892031303030332a2a2a2a2a3432222c22eab2b0eca09cec9dbc223a22eba7a4ec9b94203135ec9dbc2028ec82acec9aa9eab8b0eab08420eca084ec9b942031ec9dbc207e20eca084ec9b9420eba790ec9dbc29227d2c22eb8c80ed919cec9e9020eca095ebb3b4223a7b22eab5adeca081223a224b52222c22eb8c80ed919cec9e902028ed959ceab88029223a22eab980eab2bded9b88222c22eb8c80ed919cec9e902028ec9881ebacb829223a224b494d204b59554e4720484f4f4e222c22eb8c80ed919cec9e9020eca3bcebafbceb93b1eba19debb288ed98b8223a223837313032372d312a2a2a2a2a2a222c22eb8c80ed919cec9e9020ed9cb4eb8c80ed8fb020ebb288ed98b8223a223031302d383632302d37393339222c22eb8c80ed919cec9e9020ec9db8eca69d223a22ec9db8eca69dec9984eba38c227d2c22eab3b5eb8f99eb8c80ed919c31223a7b7d2c22eab3b5eb8f99eb8c80ed919c32223a7b7d2c22ebb295ec9db820ecb694eab080eca095ebb3b4223a7b22eab080ec8381ed86b5ed9994ecb7a8eab889ec9785ec868cec9db8eab080ec9a943f223a22ec9584eb8b88ec9a942e20ecb7a8eab889ed9598eca78020ec958aec8ab5eb8b88eb8ba42e227d7da08205eb308205e7308204cfa00302010202040159f313300d06092a864886f70d01010b0500304f310b3009060355040613024b5231123010060355040a0c0943726f73734365727431153013060355040b0c0c4163637265646974656443413115301306035504030c0c43726f737343657274434133301e170d3230303830333032313130305a170d3231303930313134353935395a30818b310b3009060355040613024b5231123010060355040a0c0943726f73734365727431153013060355040b0c0c416363726564697465644341311b3019060355040b0c12ed959ceab5adeca084ec9e90ec9db8eca69d310f300d060355040b0c06ebb295ec9db83123302106035504030c1a28eca3bc29eba088ec9db4ed8bb0ec8aa4ed8ab8ecbc80ec9db430820122300d06092a864886f70d01010105000382010f003082010a0282010100e630e5bb9b655bc6a637c00ed87371b4b22403167837b10bccfdc6b4672f562bd49ca61126dda0d8dac6fbf00c953a71152df1f1fdaeab545f68fb7fd9e5463929dee03e05d48b18525a0f9628dc100240b401a698a7be1c05dca9793a9953e2409702a2a034e3cda98464f28d02c5e7e6718738f6cdea551aa0a19990178c9f4a82d6a8083987c421a72c3e4c3ac6bf9d792feb8d7d8ec44e6cd7a0b80ef2e19658bc4541f7c5ddb7ffcd2bbd635f9d46444fe10c05e0b082b438cb8abc9789048ca0a1af35780f83b200302cc62a1686ca10bb1d96a7308d579091c0a05073cff6788fc95cf9ad08e3a82350e2487a01eb4b17e634cce0b3b8aba6084544310203010001a382028c3082028830818f0603551d23048187308184801443d6f3657f659dcd6bc1ce730abf3210a051e711a168a4663064310b3009060355040613024b52310d300b060355040a0c044b495341312e302c060355040b0c254b6f7265612043657274696669636174696f6e20417574686f726974792043656e7472616c3116301406035504030c0d4b49534120526f6f74434120348202101e301d0603551d0e041604141e8b8015455b630183b3bb23c9340aa888ec5ccd300e0603551d0f0101ff0404030206c0307f0603551d200101ff047530733071060a2a831a8c9a44050401023063302d06082b060105050702011621687474703a2f2f6763612e63726f7373636572742e636f6d2f6370732e68746d6c303206082b0601050507020230261e24c7740020c778c99dc11cb2940020acf5c7780020c778c99dc11c0020c785b2c8b2e4002e30790603551d1104723070a06e06092a831a8c9a440a0101a061305f0c1a28eca3bc29eba088ec9db4ed8bb0ec8aa4ed8ab8ecbc80ec9db43041303f060a2a831a8c9a440a0101013031300b0609608648016503040201a022042060b170fb9f439268b6e4a12430b7b225f5d1bb0451058b842c9b0503f343ddc13081800603551d1f047930773075a073a071866f6c6461703a2f2f6469722e63726f7373636572742e636f6d3a3338392f636e3d73316470313070313234352c6f753d63726c64702c6f753d4163637265646974656443412c6f3d43726f7373436572742c633d4b523f63657274696669636174655265766f636174696f6e4c697374304606082b06010505070101043a3038303606082b06010505073001862a687474703a2f2f6f6373702e63726f7373636572742e636f6d3a31343230332f4f435350536572766572300d06092a864886f70d01010b05000382010100033d53f3ecfb2ecd6074278a714dc30b737eb7776a50830ae3911d2a71175bfc0e193e38a046b58c9ea0ed4b1376904189f2edd6aed93e0a60ae684f382341c912fbdc43241a96cc3e74df3bdac6ec08b8e725f3cd8fe9ac4e56037dcc783be4cf3325a1d5628c620adc7e61533a1f51e40feebb4e5a09c0fddbffed893c0b26193e539ea8bf29ef85659bfe29cb1cb4d9dc0f2c848c70cf403762c2c2cd230e07a54e1258bc25fae27b60d7df5afc69e01a706f54f889354aca04a4a3fcccf1b0bd4d1dad0ab138838e87ad44c261791b1c9f884e50eb75335c6ee94eb7c6a72fa1acc7ce011294ab51e5d99dffa9eaf274b7d8a84157e543b767f379d9b3f8318201823082017e0201013057304f310b3009060355040613024b5231123010060355040a0c0943726f73734365727431153013060355040b0c0c4163637265646974656443413115301306035504030c0c43726f73734365727443413302040159f313300d06096086480165030402010500300d06092a864886f70d0101010500048201003819526739ca535b92fd5db521b7d13bf232ff8d68848e39eda01f3cc51d1cea9fa32f3ad5a0f71858dea93c1d01aa3ea563bc5f7c2b948f0a7a83b2d0dbdebdf6e1bfb9277b63cd4182fa69582033c0e2bafd3878f6f90e7a5fb9f6ab7624e734787ce2c30d967ee061b9a16a916aa9cb76254895fe270c33713b9e3df2be43d249b46edc865d110886c3584e1eab89f49991b805fc9241f923216dcee7a91c81316b1429450f634fac34e7a2a6cc6daa333613910ebb8024abb23ea9e01b7a191385a9bbf153ce0713201264533213eb4d8df06fd951b5c37aecce62617948c3495c26c22c6f3fcbf8be27359649bc85b83170b4d636c96966ee08cc4d4dc3")
				.param("payAccount", "")
				.param("userIdx", "487")
				.param("cardType", CardType.KISED.toString())
				.header("Authorization", "Bearer " + getToken())
				.characterEncoding("UTF-8")
		)
			.andDo(print())
			.andExpect(status().isOk());

	}


	@Test
	@Transactional
	@DisplayName("창진원 과제번호 검증 API")
	void shouldReturnKisedResponseDtoWhenRequestProjectId() throws Exception {
		String token = getToken();
		String projectId = "10375033";
		String licenseNo = "2618125793";

		mockMvc.perform(
			get(ShinhanCardControllerV2.URI.BASE + ShinhanCardControllerV2.URI.PROJECT_ID)
				.param("licenseNo", licenseNo)
				.param("projectId", projectId)
				.param("cardIssuanceInfoIdx", "1007")
				.header("Authorization", "Bearer " + token)
				.characterEncoding("UTF-8")
		)
			.andDo(print())
			.andExpect(jsonPath("$.projectId").value(projectId))
			.andExpect(jsonPath("$.licenseNo").value(licenseNo))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("창진원 최종선정 확인서 업로드")
	@Transactional
	void shouldGetStatusOKWhenUploadConfirmation() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "multipart/form-data", "some data".getBytes());

		mockMvc.perform(
			multipart(ShinhanCardControllerV2.URI.BASE + ShinhanCardControllerV2.URI.CONFIRMATION)
				.file(multipartFile)
				.param("projectId", "12345678")
				.header("Authorization", "Bearer " + getToken())
				.characterEncoding("UTF-8")
		)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("창진원 최종선정 확인서 허용되지 않은 확장자 업로드 시 예외 발생")
	@Transactional
	void shouldGet400ErrorWhenNotAllowedExtensionUploadConfirmation() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "multipart/form-data", "some data".getBytes());

		mockMvc.perform(
			multipart(ShinhanCardControllerV2.URI.BASE + ShinhanCardControllerV2.URI.CONFIRMATION)
				.file(multipartFile)
				.param("projectId", "12345678")
				.header("Authorization", "Bearer " + getToken())
				.characterEncoding("UTF-8")
		)
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.error")
				.value(BadRequestedException.Category.NOT_ALLOWED_EXTENSION.toString()));
	}

}
