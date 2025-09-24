package com.dai.service;

import com.dai.model.PageResult;
import com.dai.model.dto.request.ContactAddReqDTO;
import com.dai.model.dto.response.ContactAddResDTO;
import com.dai.model.dto.response.ContactDetailResDTO;
import com.dai.model.dto.response.ContactPageResDTO;

public interface ContactService {

    /**
     * 联系人分页查询
     *
     * @param page     页码
     * @param size     页大小
     * @param name     姓名
     * @param passengerType 乘客类型
     * @param status   状态
     * @return 联系人分页查询结果
     */
    PageResult<ContactPageResDTO> page(Integer page, Integer size, String name, Integer passengerType, Integer status);

    /**
     * 联系人详情查询
     *
     * @param id 联系人id
     * @return 联系人详情
     */
    ContactDetailResDTO detail(Long id);

    /**
     * 添加联系人
     *
     * @param contactAddReqDTO 联系人信息
     * @return 添加结果
     */
    ContactAddResDTO add(ContactAddReqDTO contactAddReqDTO);
}
