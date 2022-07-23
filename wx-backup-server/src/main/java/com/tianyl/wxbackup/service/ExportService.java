package com.tianyl.wxbackup.service;

import com.tianyl.wxbackup.entity.Contact;
import com.tianyl.wxbackup.enums.ContactType;
import com.tianyl.wxbackup.mapper.ContactMapper;
import com.tianyl.wxbackup.mapper.core.BaseMapper;
import com.tianyl.wxbackup.wechat.entity.Rcontact;
import com.tianyl.wxbackup.wechat.mapper.RcontactMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    private static final Set<String> IGNORE_USERNAME = new HashSet<>();
    static {
        IGNORE_USERNAME.add("filehelper");
    }

    public void export(String path) {
        ContactMapper contactMapper = new ContactMapper();
        String msgDbPath = path + "\\EnMicroMsg_plain.db";
        RcontactMapper rcontactMapper = new RcontactMapper(msgDbPath);
        List<Rcontact> rcontacts = rcontactMapper.getAll();
        // 过滤无意义数据
        rcontacts = rcontacts.stream()
                .filter(e -> !e.getUsername().startsWith("fake_"))
                .filter(e -> !IGNORE_USERNAME.contains(e.getUsername()))
                .collect(Collectors.toList());
        Map<String, Contact> contactMap = getExistContact(contactMapper);
        List<Contact> toUpdate = new ArrayList<>();
        List<Contact> toAdd = new ArrayList<>();
        for (Rcontact wxCt : rcontacts) {
            String username = wxCt.getUsername();
            Contact contact = contactMap.get(username);
            Contact contactFromWx = convert(wxCt);
            if (contact != null) {
                if (!contact.equals(contactFromWx)) {
                    toUpdate.add(contactFromWx);
                }
            } else {
                toAdd.add(contactFromWx);
            }
        }
        contactMapper.saveBatch(toAdd);
        contactMapper.updateBatch(toUpdate);
        logger.info("导入完成,新增:{},更新:{}", toAdd.size(), toUpdate.size());
    }

    private Contact convert(Rcontact wxCt) {
        Contact contact = new Contact();
        contact.setUsername(wxCt.getUsername());
        contact.setAlias(wxCt.getAlias());
        contact.setRemark(wxCt.getRemark());
        contact.setNickname(wxCt.getNickname());
        if (wxCt.getUsername().startsWith("gh_")) {
            contact.setType(ContactType.OFFICIAL_ACCOUNTS.getType());
        } else {
            contact.setType(ContactType.FRIEND.getType());
        }
        contact.setWxType(wxCt.getType());
        return contact;
    }

    private Map<String, Contact> getExistContact(ContactMapper contactMapper) {
        List<Contact> contacts = contactMapper.getAll();
        return contacts.stream().collect(Collectors.toMap(Contact::getUsername, e -> e));
    }
}
