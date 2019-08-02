package com.wrial.community.service;
/*
 * @Author  Wrial
 * @Date Created in 14:57 2019/8/2
 * @Description
 */

import com.wrial.community.Enums.NotificationStatusEnum;
import com.wrial.community.Enums.NotificationTypeEnum;
import com.wrial.community.dto.NotificationDTO;
import com.wrial.community.dto.PaginationDTO;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import com.wrial.community.mapper.NotificationMapper;
import com.wrial.community.mapper.UserMapper;
import com.wrial.community.model.Notification;
import com.wrial.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {


        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        Integer offset = size * (page - 1);

        if (offset < 0) {
            offset = 0;
        }
        //limit分页
        List<Notification> notifications = notificationMapper.selectPage(offset, size);

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        //将分页查询出的数据进一步封装到dto里
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            //使用BeanUtils将notification的属性拷贝到NotificationDTO
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        Integer totalCount =notificationMapper.count(userId);
        paginationDTO.setPagination(totalCount, page, size);
        return paginationDTO;
    }

    public int unRead(Long receiverId){
        Example notificationExample = new Example(Notification.class);
        notificationExample.createCriteria()
                .andEqualTo("receiver",receiverId)
                .andEqualTo("status", NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.selectCountByExample(notificationExample);


    }

    //id是在页面获取到的notification的id而不是question的id
    public NotificationDTO read(Long id, User user) {

        Example example = new Example(Notification.class);
        example.createCriteria().andEqualTo("id", id);
        Notification notification = notificationMapper.selectOneByExample(example);
        if (notification==null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (notification.getReceiver()!=user.getId()){
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FIAL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        //更新状态
        notificationMapper.updateStatus(notification.getId(),notification.getStatus());

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
