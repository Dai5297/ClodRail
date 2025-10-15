package com.rs.properties;

import com.rs.model.Group;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {

    /**
     * 全局 OpenAPI 标题
     */
    private String title = "API Documentation";

    /**
     * 全局 OpenAPI 描述
     */
    private String description = "API 描述";

    /**
     * 全局 OpenAPI 版本
     */
    private String version = "1.0.0";

    /**
     * 全局 OpenAPI 许可证名称
     */
    private String licenseName = "Apache 2.0";

    /**
     * 全局 OpenAPI 许可证 URL
     */
    private String licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.html";

    /**
     * 全局 OpenAPI 联系人名称
     */
    private String name;

    /**
     * 全局 OpenAPI 联系人邮箱
     */
    private String email;

    /**
     * 全局 OpenAPI 联系人网址
     */
    private String url;

    /**
     * 全局 OpenAPI 联系人
     */
    private Contact contact = new Contact().name(name).email(email).url(url);

    /**
     * 全局 OpenAPI 许可证
     */
    private License license = new License().name(licenseName).url(licenseUrl);

    /**
     * 全局 OpenAPI 分组名称
     */
    private List<Group> groups;
}
