package com.bytecamp.web.controller;

import com.bytecamp.biz.service.ResetService;
import com.bytecamp.web.enums.ResetStatusEnum;
import com.bytecamp.web.query.PayQuery;
import com.bytecamp.web.query.ResetQuery;
import com.bytecamp.web.util.JsonRequestUtil;
import com.bytecamp.web.vo.ResetResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wangke
 * @description: ResetController
 * @date 2019-08-10 20:52
 */
@Controller
@Slf4j
public class ResetController {

    @Resource
    ResetService resetService;

    @ResponseBody
    @PostMapping("/reset")
    public ResetResultVO reset(HttpServletRequest request) {
        ResetQuery resetQuery = JsonRequestUtil.getPostJson(request, ResetQuery.class);
        ResetResultVO vo = new ResetResultVO();
        if (resetQuery == null || resetQuery.getToken() == null) {
            log.error("reset 请求参数为空");
            vo.setCode(ResetStatusEnum.FAILURE.getValue());
            return vo;
        }

        try {
            if (resetService.reset(resetQuery.getToken())) {
                vo.setCode(ResetStatusEnum.SUCCESS.getValue());
            }else{
                vo.setCode(ResetStatusEnum.FAILURE.getValue());
            }
        } catch (Exception e) {
            log.error("reset 异常", e);
            vo.setCode(ResetStatusEnum.FAILURE.getValue());
        }

        return vo;
    }
}
