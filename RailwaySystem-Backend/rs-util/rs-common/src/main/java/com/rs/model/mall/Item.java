package com.rs.model.mall; // 假设的包名，请根据您的项目结构修改

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商品表 (item) 实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends BaseModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    private Long id;

    /**
     * SKU名称
     */
    private String name;

    /**
     * 价格（分）
     * 对应数据库的 price int
     */
    private Integer price;

    /**
     * 库存数量
     * 对应数据库的 stock int unsigned
     */
    private Integer stock;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 类目名称
     */
    private String category;

    /**
     * 品牌名称
     */
    private String brand;

    /**
     * 规格
     */
    private String spec;

    /**
     * 销量
     */
    private Integer sold;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否是推广广告，true/false
     * 对应数据库的 is_ad tinyint(1)
     */
    private Boolean isAd;

    /**
     * 商品状态 1-正常，2-下架，3-删除
     */
    private Integer status;
}