package com.tianyl.wxbackup;

import com.tianyl.wxbackup.entity.Contact;
import com.tianyl.wxbackup.mapper.ContactMapper;
import com.tianyl.wxbackup.service.ExportService;
import com.tianyl.wxbackup.wechat.entity.Rcontact;
import com.tianyl.wxbackup.wechat.mapper.RcontactMapper;

import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        ContactMapper mapper = new ContactMapper();
//        mapper.createTable();
//        Contact contact = mapper.get("zhangsan");
//        System.out.println(contact);
//        mapper.autoCreateTable();
//        List<Contact> contacts = mapper.getAll();
//        for (Contact contact : contacts) {
////            System.out.println(contact);
//            contact.setAlias("new alias ");
//            contact.setRemark("asdfsadf");
//            contact.setType(null);
//            mapper.update(contact);
//        }

//        RcontactMapper rcontactMapper = new RcontactMapper("D:\\wechat\\phone_files\\EnMicroMsg_plain.db");
//        List<Rcontact> rcontacts = rcontactMapper.getAll();
//        System.out.println();
        ExportService exportService = new ExportService();
        exportService.export("D:\\wechat\\phone_files");
    }

}
