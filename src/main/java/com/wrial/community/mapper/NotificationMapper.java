package com.wrial.community.mapper;
/*
 * @Author  Wrial
 * @Date Created in 11:13 2019/8/2
 * @Description 通知 通知一定是和评论和点赞有关的
 */

import com.wrial.community.model.Notification;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface NotificationMapper extends Mapper<Notification> {

    @Select("select * from notification limit #{offset},#{size}")
    List<Notification> selectPage(Integer offset, Integer size);

    @Select("select count(1) from notification where receiver = #{userId}")
    Integer count(Long userId);

    @Update("update notification set status = #{status} where id = #{id}")
    void updateStatus(Long id, Integer status);
}
