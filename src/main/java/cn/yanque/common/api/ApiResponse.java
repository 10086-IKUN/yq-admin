package cn.yanque.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应包装类
 * @param <T> 响应数据类型
 */
/**
 * 统一API响应封装
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @return 成功的API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 成功响应（无数据）
     * @return 成功的API响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 成功响应（带消息，无数据）
     * @param message 提示消息
     * @return 成功的API响应
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }

    /**
     * 成功响应（带消息和数据）
     * @param message 提示消息
     * @param data 响应数据
     * @return 成功的API响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @return 失败的API响应
     */
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
