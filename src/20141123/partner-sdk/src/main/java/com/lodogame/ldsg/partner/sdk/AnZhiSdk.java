package com.lodogame.ldsg.partner.sdk;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.lodogame.game.utils.UrlRequestUtils;
import com.lodogame.game.utils.UrlRequestUtils.Mode;
import com.lodogame.game.utils.anzhi.Base64;
import com.lodogame.game.utils.anzhi.Des3Util;
import com.lodogame.game.utils.json.Json;
import com.lodogame.ldsg.partner.model.anzhi.AnZhiPaymentObj;

public class AnZhiSdk extends BaseSdk {
	
	private static final Logger logger = Logger.getLogger(AnZhiSdk.class);
	
	private static AnZhiSdk ins;

	private static Properties prop;

	private final static String PROTOCOL_HEAD = "http://";
	private String partnerId;
	private String apiKey;
	private String appSecret;
	private String host;
	private String payHost;

	public static AnZhiSdk instance() {
		synchronized (AnZhiSdk.class) {
			if (ins == null) {
				ins = new AnZhiSdk();
			}
		}

		return ins;
	}

	private AnZhiSdk() {
		loadSdkProperties();
	}

	public void reload(){
		loadSdkProperties();
	}
	
	private void loadSdkProperties() {
		try {
			prop = PropertiesLoaderUtils.loadProperties(new ClassPathResource("sdk.properties"));
			apiKey = prop.getProperty("AnZhiSdk.apiKey");
			partnerId = prop.getProperty("AnZhiSdk.partnerId");
			host = prop.getProperty("AnZhiSdk.host");
			appSecret = prop.getProperty("AnZhiSdk.appSecret");
			payHost = prop.getProperty("AnZhiSdk.payHost");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public boolean verifySession(String session, String uid,long timestamp){
		String url = PROTOCOL_HEAD + host;
		Map<String, String> params = new HashMap<String, String>();
		params.put("time",timestamp+"");
		params.put("appkey", apiKey);
		params.put("account", uid);
		params.put("sid", session);
		params.put("sign", Base64.encodeToString(apiKey+uid+session+appSecret));
		String jsonStr = UrlRequestUtils.execute(url, params, Mode.POST);
		jsonStr = StringUtils.replace(jsonStr,"'","\"");
		Map<String,Object> jsonMap = Json.toObject(jsonStr,Map.class);
		if(jsonMap.containsKey("sc")
				&& jsonMap.get("sc") != null
				&& Integer.parseInt(jsonMap.get("sc").toString()) == 1){
			return true;
		}
		return false;
	}
	

	public boolean checkPayCallbackSign(AnZhiPaymentObj data) {
		try {
			String jsonStr = Des3Util.decrypt(data.getData(),AnZhiSdk.instance().getAppSecret());
			Map<String,Object> ret = Json.toObject(jsonStr, Map.class);
			data.setAppInfo(ret.get("cpInfo").toString());
			data.setCode(ret.get("code").toString());
			data.setNotifyTime(ret.get("notifyTime").toString());
			if(ret.get("orderAccount") != null){
				data.setOrderAccount(ret.get("orderAccount").toString());
			}
			data.setOrderAmount(ret.get("orderAmount").toString());
			data.setOrderId(ret.get("orderId").toString());
			data.setOrderTime(ret.get("orderTime").toString());
			data.setPayAmount(ret.get("payAmount").toString());
			data.setUid(ret.get("uid").toString());
			if(ret.get("memo") != null){
				data.setMemo(ret.get("memo").toString());
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getPayHost() {
		return payHost;
	}

	public void setPayHost(String payHost) {
		this.payHost = payHost;
	}
}
