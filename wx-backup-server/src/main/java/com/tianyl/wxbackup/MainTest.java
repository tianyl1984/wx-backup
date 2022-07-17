package com.tianyl.wxbackup;

import com.tianyl.wxbackup.entity.Contact;
import com.tianyl.wxbackup.mapper.ContactMapper;

import java.io.File;

public class MainTest {

    public static void main(String[] args) {
        ContactMapper mapper = new ContactMapper();
        Contact contact = mapper.get("zhangsan");
        System.out.println(contact);
    }

}
