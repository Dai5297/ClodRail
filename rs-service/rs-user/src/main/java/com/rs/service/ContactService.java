package com.rs.service;

import com.rs.dto.response.user.PassengerResDTO;
import com.rs.model.PageResult;
import com.rs.model.dto.request.user.ContactReqDTO;
import com.rs.model.dto.response.user.ContactAddResDTO;
import com.rs.model.dto.response.user.ContactDetailResDTO;
import com.rs.model.dto.response.user.ContactPageResDTO;

import java.util.List;

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
    ContactAddResDTO add(ContactReqDTO contactAddReqDTO);

    /**
     * 修改联系人
     *
     * @param contactUpdateReqDTO 联系人信息
     * @return 修改结果
     */
    ContactDetailResDTO update(ContactReqDTO contactUpdateReqDTO);

    /**
     * 删除联系人
     *
     * @param id 联系人id
     */
    void delete(Long id);

    /**
     * 查询联系人
     *
     * @param passengerIds 联系人id
     * @return 联系人信息
     */
    List<PassengerResDTO> queryPassenger(List<Long> passengerIds);
}
