package com.dai.mapper;

import com.dai.model.dto.response.ContactAddResDTO;
import com.dai.model.dto.response.ContactDetailResDTO;
import com.dai.model.dto.response.ContactPageResDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContactMapper {

    List<ContactPageResDTO> queryContacts(Long userId, String name, Integer passengerType , Integer status);

    ContactDetailResDTO queryContactDetail(Long id);

    boolean addContact(ContactAddResDTO contact);

    Integer getDefaultCount(Long userId);
}
