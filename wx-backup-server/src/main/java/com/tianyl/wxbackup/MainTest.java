package com.tianyl.wxbackup;

import com.tianyl.wxbackup.mapper.ChatRoomMapper;
import com.tianyl.wxbackup.mapper.ContactMapper;
import com.tianyl.wxbackup.service.ImportService;

public class MainTest {

    public static void main(String[] args) {
        init();
//        ContactMapper mapper = new ContactMapper();
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
        ImportService importService = new ImportService();
        importService.importAll("D:\\wechat\\phone_files");
    }

    private static void init() {
        new ContactMapper().autoCreateTable();
        new ChatRoomMapper().autoCreateTable();
    }

}
