package com.lodogame.ldsg.web.controller;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lodogame.game.utils.json.Json;
import com.lodogame.ldsg.partner.model.guguo.GuGuoPaymentObj;
import com.lodogame.ldsg.partner.sdk.GuGuoSdk;
import com.lodogame.ldsg.partner.service.PartnerService;
import com.lodogame.ldsg.web.factory.PartnerServiceFactory;

@Controller
public class GuGuoController {
	private static Logger LOG = Logger.getLogger(GuGuoController.class);
	
	@Autowired
	private PartnerServiceFactory serviceFactory;

	@RequestMapping(value = "/webApi/cooGuoPayment.do")
	public ModelAndView payCallback(HttpServletRequest req, HttpServletResponse res) {
		PartnerService ps = serviceFactory.getBean(GuGuoSdk.instance().getPartnerId());
		try {
			GuGuoPaymentObj data = new GuGuoPaymentObj();
			data.setAmount(req.getParameter("amount"));
			data.setCallBackInfo(req.getParameter("callBackInfo"));
			data.setOpenId(req.getParameter("openId"));
			data.setOrderId(req.getParameter("orderId"));
			data.setOrderStatus(req.getParameter("orderStatus"));
			data.setPayType(URLDecoder.decode(req.getParameter("payType"),"UTF-8"));
			data.setRemark(URLDecoder.decode(req.getParameter("remark"),"UTF-8"));
			data.setRoleId(req.getParameter("roleId"));
			data.setRoleName(URLDecoder.decode(req.getParameter("roleName"),"UTF-8"));
			data.setServerId(URLDecoder.decode(req.getParameter("serverId"),"UTF-8"));
			data.setServerName(URLDecoder.decode(req.getParameter("serverName"),"UTF-8"));
			data.setPayTime(req.getParameter("payTime"));
			data.setPaySUTime(req.getParameter("paySUTime"));
			data.setSign(req.getParameter("sign"));
			LOG.info("Guguo payment info:"+Json.toJson(data));
			if(StringUtils.isNotBlank(data.getSign())
					&& StringUtils.isNotBlank(data.getAmount())
					&& StringUtils.isNotBlank(data.getCallBackInfo())){
				if (ps.doPayment(data)) {
					res.getWriter().print("success");
				}else{
					res.getWriter().print("fail");
				}
			}else{
				res.getWriter().print("fail");
			}
		} catch (Exception e) {
			LOG.error("anzhi payment error!",e);
		}
		return null;
	}
}
