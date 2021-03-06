package com.coupon.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.coupon.service.CouponService;
import com.coupon.util.TotalPage;;

@RestController
@RequestMapping("/api")
public class CouponController {

    @Autowired
    private CouponService objCouponService;
    
    @Autowired
    private TotalPage objTotalPage;
    
//    //1. 쿠폰 리스트 조회
//    @GetMapping(value = "/coupon")
//    public String selectCouponList() {
//        return "couponList";
//    }

    //2. 쿠폰 리스트 조회 API
    @RequestMapping(value = "/coupon/list", method=RequestMethod.GET)
    @ResponseBody
    public ModelAndView selectCouponList(HttpServletRequest  objRequest
                                        ,HttpServletResponse objResponse
                                        ,@RequestParam Map<String, Object> paramMap) {
    	
        int    intPageNo   = 1;
        int    intPageSize = 10;
        int    intTotalCnt = 0;
        int    intResult   = 0;
        String strErrMsg   = "";
        
        String strPageNo   = paramMap.get("pageNo").toString();
        String strPageSize = paramMap.get("pageSize").toString();

        //JSON 응답 객체
        ModelAndView  objMV = new ModelAndView(new MappingJackson2JsonView());

        
        try 
        {
            if(strPageNo != null && !strPageNo.isEmpty()){
                intPageNo = Integer.parseInt(paramMap.get("pageNo").toString());
            }
            if(strPageSize != null && !strPageSize.isEmpty()){
                intPageSize = Integer.parseInt(paramMap.get("pageSize").toString());
            }

            intTotalCnt = objCouponService.getCouponCnt();

            //현재 페이지
            objMV.addObject("currentPage", intPageNo);
            //페이지 사이즈
            objMV.addObject("pageSize", intPageSize); 
            //전체 페이지
            objMV.addObject("totalPage", objTotalPage.getTotalPage(intTotalCnt, intPageSize));
            //쿠폰 리스트 호출
            objMV.addObject("couponList", objCouponService.selectCouponList(intPageNo, intPageSize));
            
        }
        catch (Exception objEx) 
        { 
            intResult = 9999;
            strErrMsg = objEx.getMessage();
        }
        finally
        {
            objMV.addObject("code",    intResult);
            objMV.addObject("message", strErrMsg);
        }

        return objMV;
        
    }
    
    //3. 쿠폰 생성 API
    @RequestMapping(value="/coupon/create", method=RequestMethod.POST)
    @ResponseBody
    public ModelAndView insertCoupon(HttpServletRequest  objRequest
                                    ,HttpServletResponse objResponse
                                    ,@RequestBody Map<String, Object> paramMap) {
        
        int    intResult    = 0;
        String strErrMsg    = "";
        String strEmailAddr = "";        
        
        //JSON 응답 객체
        ModelAndView  objMV = new ModelAndView(new MappingJackson2JsonView());

        try 
        {         
            //이메일 정보 설정
            strEmailAddr = paramMap.get("strEmailAddr").toString();
            
            //이메일 공백 체크
            if(strEmailAddr == null || strEmailAddr.isEmpty()) {
            	intResult = 1001;
            	strErrMsg = "이메일 값이 존재하지 않습니다.";
            }
            else {
            	strErrMsg = "쿠폰생성에 성공하였습니다.";
            }
            
            //입력받은 이메일 추가
            paramMap.put("strEmailAddr", strEmailAddr);
            
            objCouponService.insertCoupon(paramMap);
        } 
        catch (Exception objEx) 
        { 
            intResult = 9999;
            strErrMsg = objEx.getMessage();
        }
        finally
        {
            objMV.addObject("code",    intResult);
            objMV.addObject("message", strErrMsg);
        }
        
        return objMV;
        
    }
}