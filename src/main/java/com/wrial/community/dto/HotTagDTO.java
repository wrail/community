package com.wrial.community.dto;
/*
 * @Author  Wrial
 * @Date Created in 21:34 2019/8/4
 * @Description  准备放入优先队列的对象
 */

import lombok.Data;

@Data
public class HotTagDTO implements Comparable {
    private String tagName;
    private Integer priority;

    @Override
    public int compareTo(Object o) {
        return this.priority - ((HotTagDTO) o).getPriority();
    }
}
