package com.tianyl.wxbackup.service;

import com.tianyl.wxbackup.entity.ChatRoom;
import com.tianyl.wxbackup.entity.Contact;
import com.tianyl.wxbackup.enums.ContactType;
import com.tianyl.wxbackup.mapper.ChatRoomMapper;
import com.tianyl.wxbackup.mapper.ContactMapper;
import com.tianyl.wxbackup.wechat.entity.Rcontact;
import com.tianyl.wxbackup.wechat.entity.WxChatroom;
import com.tianyl.wxbackup.wechat.mapper.RcontactMapper;
import com.tianyl.wxbackup.wechat.mapper.WxChatroomMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);

    private static final Set<String> IGNORE_USERNAME = new HashSet<>();

    static {
        IGNORE_USERNAME.add("filehelper");
    }

    public void importAll(String path) {
        importContact(path);
        importChatRoom(path);
    }

    private void importChatRoom(String path) {
        ChatRoomMapper chatRoomMapper = new ChatRoomMapper();
        String msgDbPath = path + "\\EnMicroMsg_plain.db";
        WxChatroomMapper wxChatroomMapper = new WxChatroomMapper(msgDbPath);
        List<WxChatroom> wxChatrooms = wxChatroomMapper.getAll();
        Map<String, ChatRoom> chatRoomMap = getExistChatRoom(chatRoomMapper);
        List<ChatRoom> addChatRooms = new ArrayList<>();
        List<ChatRoom> updateChatRooms = new ArrayList<>();
        for (WxChatroom wxChatroom : wxChatrooms) {
            ChatRoom chatRoomFromWx = convert(wxChatroom);
            ChatRoom exist = chatRoomMap.get(wxChatroom.getChatroomname());
            if (exist != null) {
                if (!exist.equals(chatRoomFromWx)) {
                    updateChatRooms.add(chatRoomFromWx);
                }
            } else {
                addChatRooms.add(chatRoomFromWx);
            }
        }
        chatRoomMapper.saveBatch(addChatRooms);
        chatRoomMapper.updateBatch(updateChatRooms);
        logger.info("聊天室导入完成,新增:{},更新:{}", addChatRooms.size(), updateChatRooms.size());
    }

    private Map<String, ChatRoom> getExistChatRoom(ChatRoomMapper chatRoomMapper) {
        return chatRoomMapper.getAll().stream().collect(Collectors.toMap(ChatRoom::getName, e -> e));
    }

    private void importContact(String path) {
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
        logger.info("联系人导入完成,新增:{},更新:{}", toAdd.size(), toUpdate.size());
    }

    private ChatRoom convert(WxChatroom wxChatroom) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMembers(wxChatroom.getMemberlist());
        chatRoom.setName(wxChatroom.getChatroomname());
        chatRoom.setDisplayNames(wxChatroom.getDisplayname());
        chatRoom.setModifyTime(wxChatroom.getModifytime());
        chatRoom.setRoomOwner(wxChatroom.getRoomowner());
        return chatRoom;
    }

    private Contact convert(Rcontact wxCt) {
        Contact contact = new Contact();
        contact.setUsername(wxCt.getUsername());
        contact.setAlias(wxCt.getAlias());
        contact.setRemark(wxCt.getRemark());
        contact.setNickname(wxCt.getNickname());
        if (wxCt.getUsername().startsWith("gh_")) {
            contact.setType(ContactType.OFFICIAL_ACCOUNTS.getType());
        } else if (wxCt.getUsername().endsWith("@chatroom")) {
            contact.setType(ContactType.GROUP_FRIEND.getType());
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
