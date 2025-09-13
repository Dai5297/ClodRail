package util;

import enums.RespCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class RespResult<T> implements Serializable {

    private T data;

    private RespCode code;

    private String message;

    public RespResult(T data, RespCode code) {
        this.data = data;
        this.code = code;
        this.message = code.getMessage();
    }

    public RespResult(RespCode code) {
        this.code = code;
        this.message = code.getMessage();
    }

    public RespResult() {
    }

    public static <T> RespResult<T> ok() {
        return new RespResult<>(null, RespCode.SUCCESS);
    }

    public static <T> RespResult<T> ok(T data) {
        return new RespResult<>(data, RespCode.SUCCESS);
    }

    public static <T> RespResult<T> error() {
        return new RespResult<>(null, RespCode.ERROR);
    }

    public static <T> RespResult<T> error(String message) {
        return secByError(message);
    }

    public static <T> RespResult<T> error(RespCode respCode, String message) {
        RespResult<T> result = secByError(message);
        result.setCode(respCode);
        return result;
    }

    public static <T> RespResult<T> secByError(String message) {
        RespResult<T> respResult = new RespResult<>();
        respResult.setCode(RespCode.ERROR);
        respResult.setMessage(message);
        return respResult;
    }

    public static <T> RespResult<T> error(RespCode respCode) {
        return new RespResult<>(respCode);
    }
}
