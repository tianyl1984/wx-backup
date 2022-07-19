package com.tianyl.wxbackup;

import com.tianyl.wxbackup.entity.Contact;
import com.tianyl.wxbackup.mapper.ContactMapper;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        ContactMapper mapper = new ContactMapper();
//        mapper.createTable();
//        Contact contact = mapper.get("zhangsan");
//        System.out.println(contact);
        List<Contact> contacts = mapper.getAll();
        for (Contact contact : contacts) {
//            System.out.println(contact);
            contact.setAlias("new alias ");
            contact.setRemark("asdfsadf");
            contact.setType(null);
            mapper.update(contact);
        }

    }

}
