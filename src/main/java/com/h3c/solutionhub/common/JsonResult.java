package com.h3c.solutionhub.common;

/**
 * 描述: json格式数据返回对象，使用CustomJsonResultSerializer 来序列化
 * @author : h14049
 */
public class JsonResult<T> {
  
    private String code;
    private String msg;
    private T data;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "code=" + code + " message=" + msg + " data=" + data;
    }

    public static <T> com.h3c.solutionhub.common.JsonResult<T> fail() {
    	com.h3c.solutionhub.common.JsonResult<T> ret = new com.h3c.solutionhub.common.JsonResult<T>();
    	ret.setCode("201");
    	ret.setMsg("失败");
        return ret;
    }
    
    public static <T> com.h3c.solutionhub.common.JsonResult<T> fail(T data) {
	    	com.h3c.solutionhub.common.JsonResult<T> ret = com.h3c.solutionhub.common.JsonResult.fail();
	    	ret.setData(data);
        return ret;
    }
    
    public static <T> com.h3c.solutionhub.common.JsonResult<T> failMessage(String msg) {
	    	com.h3c.solutionhub.common.JsonResult<T> ret = com.h3c.solutionhub.common.JsonResult.fail();
	    	ret.setMsg(msg);
        return ret;
    }
    public static <T> com.h3c.solutionhub.common.JsonResult<T> successMessage(String msg) {
	    	com.h3c.solutionhub.common.JsonResult<T> ret = com.h3c.solutionhub.common.JsonResult.success();
	    	ret.setMsg(msg);
	    return ret;
    }

    public static <T> com.h3c.solutionhub.common.JsonResult<T> success() {
    	com.h3c.solutionhub.common.JsonResult<T> ret = new com.h3c.solutionhub.common.JsonResult<T>();
    	ret.setCode("200");
    	ret.setMsg("");
        return ret;
    }

    public static <T> com.h3c.solutionhub.common.JsonResult<T> success(T data) {
	    	com.h3c.solutionhub.common.JsonResult<T> ret = com.h3c.solutionhub.common.JsonResult.success();
	    	ret.setData(data);
        return ret;
    }
    
    public static <T> com.h3c.solutionhub.common.JsonResult<T> http404(T data) {
	    	com.h3c.solutionhub.common.JsonResult<T> ret = new com.h3c.solutionhub.common.JsonResult<T>();
	    	ret.setCode("404");
	    	ret.setMsg("");
	    	ret.setData(data);
        return ret;
    }
    
    public static <T> com.h3c.solutionhub.common.JsonResult<T> http403(T data) {
	    	com.h3c.solutionhub.common.JsonResult<T> ret = new com.h3c.solutionhub.common.JsonResult<T>();
	    	ret.setCode("403");
	    	ret.setMsg("");
	    	ret.setData(data);
        return ret;
    }

}
