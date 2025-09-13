package enums;

public enum RespCode {

    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "未授权"),
    ERROR(500, "操作失败"),
    SYSTEM_ERROR(501, "系统错误");

    private Integer code;
    private String message;

    RespCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    RespCode() {}

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
