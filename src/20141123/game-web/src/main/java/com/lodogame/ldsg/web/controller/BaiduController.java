package com.lodogame.ldsg.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lodogame.ldsg.partner.model.baidu.BaiduPaymentObj;
import com.lodogame.ldsg.partner.sdk.BaiduSdk;
import com.lodogame.ldsg.partner.service.PartnerService;
import com.lodogame.ldsg.web.factory.PartnerServiceFactory;

@Controller
public class BaiduController {
	private static Logger LOG = Logger.getLogger(BaiduController.class);

	private static final String SUCCESS = "<!--recv=ok-->";
	private static final String FAILURE = "<!--recv=fail-->";
	@Autowired
	private PartnerServiceFactory serviceFactory;

	@RequestMapping(value = "/webApi/baiduPayment.do", method = RequestMethod.POST)
	public ModelAndView payCallback(HttpServletRequest req, HttpServletResponse res) {
		PartnerService ps = serviceFactory.getBean(BaiduSdk.instance().getPartnerId());
		BaiduPaymentObj data = new BaiduPaymentObj();
		String retVal = FAILURE;
		data.setApiKey(req.getParameter("api_key"));
		data.setUserId(req.getParameter("user_id"));
		data.setServerId(req.getParameter("server_id"));
		data.setTimestamp(req.getParameter("timestamp"));
		data.setOrderId(req.getParameter("order_id"));
		data.setWanbaOid(req.getParameter("wanba_oid"));
		data.setAmount(req.getParameter("amount"));
		data.setCurrency(req.getParameter("currency"));
		data.setResult(req.getParameter("result"));
		data.setBackSend(req.getParameter("back_send"));
		data.setCustomInfo(req.getParameter("customInfo"));
		data.setSign(req.getParameter("sign"));

		if (ps.doPayment(data)) {
			retVal = SUCCESS;
		}

		ModelAndView modelView = new ModelAndView("empty", "output", retVal);

		return modelView;
	}
}
