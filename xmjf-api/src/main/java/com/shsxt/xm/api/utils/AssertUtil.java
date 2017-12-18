package com.shsxt.xm.api.utils;


import com.shsxt.xm.api.exceptions.ParamsExcetion;

/**
 * 非空判断  返回提示信息
 */
public class AssertUtil {
	
	public static void isTrue(Boolean flag,String errorMsg) {
		if(flag){
			throw new ParamsExcetion(errorMsg);
		}
	}
	
	
	public static void isTrue(Boolean flag,String errorMsg,Integer errorCode) {
		if(flag){
			throw new ParamsExcetion(errorMsg,errorCode);
		}
	}

	
}
