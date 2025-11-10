package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.rs.dto.response.customer.PassengerResDTO;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.ContactMapper;
import com.rs.model.PageResult;
import com.rs.model.customer.Contact;
import com.rs.model.dto.request.ContactReqDTO;
import com.rs.model.dto.response.ContactAddResDTO;
import com.rs.model.dto.response.ContactDetailResDTO;
import com.rs.model.dto.response.ContactPageResDTO;
import com.rs.service.ContactService;
import com.rs.util.PageUtil;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMapper contactMapper;

    /**
     * 联系人分页查询
     *
     * @param page      页码
     * @param size      页大小
     * @param name      联系人姓名
     * @param passengerType 联系人类型
     * @param status    联系人状态
     * @return 联系人分页结果
     */
    @Override
    public PageResult<ContactPageResDTO> page(Integer page, Integer size, String name, Integer passengerType, Integer status) {
        PageUtil.startPage(page, size);
        Long userId = UserContext.get();
        List<ContactPageResDTO> contactPageResDTOS = contactMapper.queryContacts(userId, name, passengerType, status);
        return PageUtil.buildPageResult(contactPageResDTOS);
    }

    /**
     * 联系人详情查询
     *
     * @param id 联系人id
     * @return 联系人详情
     */
    @Override
    public ContactDetailResDTO detail(Long id) {
        return contactMapper.queryContactDetail(id);
    }

    /**
     * 添加联系人
     *
     * @param contactAddReqDTO 联系人信息
     * @return 添加结果
     */
    @Override
    public ContactAddResDTO add(ContactReqDTO contactAddReqDTO) {
        ContactAddResDTO contact = BeanUtil.copyProperties(contactAddReqDTO, ContactAddResDTO.class);
        contact.setUserId(UserContext.get());
        if (contact.getIsDefault() == 1) {
            if (contactMapper.getDefaultCount(contact.getUserId()) > 0) {
                throw new CommonException(RespCode.ERROR, "已存在默认联系人");
            }
        }
        if (contactMapper.addContact(contact)) {
            return contact;
        }
        return null;
    }

    /**
     * 修改联系人
     *
     * @param contactUpdateReqDTO 联系人信息
     * @return 修改结果
     */
    @Override
    public ContactDetailResDTO update(ContactReqDTO contactUpdateReqDTO) {
        Contact contact = BeanUtil.copyProperties(contactUpdateReqDTO, Contact.class);
        if (contact.getId() ==  null) {
            throw new CommonException(RespCode.ERROR, "用户id不能为空");
        }
        if (contact.getIsDefault() == 1) {
            if (contactMapper.getDefaultCount(contact.getUserId()) > 1) {
                throw new CommonException(RespCode.ERROR, "已存在默认联系人");
            }
        }
        if (contactMapper.updateContact(contact)) {
            return contactMapper.queryContactDetail(contact.getId());
        }
        return null;
    }

    /**
     * 删除联系人
     *
     * @param id 联系人id
     */
    @Override
    public void delete(Long id) {
        contactMapper.deleteById(id);
    }

    /**
     * 查询联系人
     *
     * @param passengerIds 联系人id
     * @return 联系人信息
     */
    @Override
    public List<PassengerResDTO> queryPassenger(List<Long> passengerIds) {
        List<PassengerResDTO> result = new ArrayList<>();
        for (Long passengerId : passengerIds) {
            result.add(contactMapper.queryPassenger(passengerId));
        }
        return result;
    }
}
