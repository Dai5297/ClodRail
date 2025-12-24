package com.rs.mapper;

import com.rs.dto.response.user.PassengerResDTO;
import com.rs.model.customer.Contact;
import com.rs.model.dto.response.user.ContactAddResDTO;
import com.rs.model.dto.response.user.ContactDetailResDTO;
import com.rs.model.dto.response.user.ContactPageResDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContactMapper {

    List<ContactPageResDTO> queryContacts(Long userId, String name, Integer passengerType , Integer status);

    ContactDetailResDTO queryContactDetail(Long id);

    boolean addContact(ContactAddResDTO contact);

    Integer getDefaultCount(Long userId);

    boolean updateContact(Contact contact);

    void deleteById(Long id);

    PassengerResDTO queryPassenger(Long passengerId);
}
