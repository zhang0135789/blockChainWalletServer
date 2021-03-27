package com.feel.modules.wallet.utils;


import com.feel.common.constant.HttpStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回数据
 *
 * @author chenshun
 * @email sunlightcs@gmail.c
 * om
 * @date 2016年10月27日 下午9:59:27
 */
@ApiModel("返回结果")
@Data
@AllArgsConstructor
public class R<T>  implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("参照 HttpStatus\n" +
			"200:请求成功\n" +
			"400:参数或语法不对\n" +
			"401:未认证\n" +
			"403:禁止访问\n" +
			"404-408:资源不存在\n" +
			"500:服务器内部错误，异常未捕获")
	private int code;

	@ApiModelProperty("返回消息")
	private String msg;

	@ApiModelProperty("返回数据")
	private T data;


	public R() {
		code = HttpStatus.SUCCESS;
		msg = "success";
	}
	public R(T data) {
		if (data != null) {
			this.data = data;
		}
		code = HttpStatus.SUCCESS;
		msg = "success";
	}

	public static R error() {
		return error(HttpStatus.ERROR, "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(HttpStatus.ERROR, msg);
	}

	public static R error(int code, String msg) {
		R r = new R();
		r.setCode(code);
		r.setMsg(msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.setMsg(msg);
		return r;
	}


	public static R ok() {
		return new R();
	}


	public static R ok(Object data){
		R r = new R(data);
		return r;
	}

}
