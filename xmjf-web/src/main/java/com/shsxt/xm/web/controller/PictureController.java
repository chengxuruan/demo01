package com.shsxt.xm.web.controller;

import com.google.code.kaptcha.Producer;

import com.shsxt.xm.api.constant.P2PConstant;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * Created by Administrator on 2017/12/8.
 */
@Controller
@RequestMapping("img")
public class PictureController {

    @Resource
    private Producer captchaProducer;

    @RequestMapping("getPictureVerifyImage")
    public void getKaptchaImage(HttpServletRequest request ,HttpServletResponse response ) throws Exception{

        //设置响应流信息
        response.setDateHeader("Expires",0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");


        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");

     String capText = captchaProducer.createText();
        System.out.println("验证码:"+capText);
        // 将验证码存session
        request.getSession().setAttribute(P2PConstant.PICTURE_VERIFY_CODE,capText);
        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);

        ServletOutputStream out = response.getOutputStream();
        // write the data out
        ImageIO.write(bi, "jpg", out);

        try {
            out.flush();
        } finally {

            out.close();
        }
    }
}
