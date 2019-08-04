package com.wrial.community.cahce;
/*
 * @Author  Wrial
 * @Date Created in 20:24 2019/8/4
 * @Description 拿到热门标签
 */

import com.wrial.community.dto.HotTagDTO;
import com.wrial.community.dto.TagDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
@Slf4j
public class HotTagCache {


    /*
    将Tag更新到优先队列
     */
    public List<HotTagDTO> updateTags(Map<String, Integer> priorityMap) {
        //避免浪费 因为我们的热门标签只需要五个
        int max = 4;
        Queue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);

        //遍历拿来的Map里面有我们要的所有内容
        priorityMap.forEach((tagName, priority) -> {

            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setTagName(tagName);
            hotTagDTO.setPriority(priority);
            if (priorityMap.size() < 5) {
                priorityQueue.add(hotTagDTO);
            } else {
                //和element语义一样，获取对头但不删除（权值最小的），只是为空的时候返回null，而不是抛异常
                HotTagDTO minPriority = priorityQueue.peek();
                //如果队头都比这个大，那就不要的，否则替换掉队头
                if (minPriority.compareTo(hotTagDTO) < 0) {
                    //和remove语义相同（移除队首），只是为空的时候返回null
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }

        });

        //测试打印
        priorityQueue.forEach((t) -> {
            log.info("测试打印优先队列{}",t.toString());
        });

        //按顺序出
        List<HotTagDTO> sortedHotList = new ArrayList<>();
        HotTagDTO poll = priorityQueue.poll();
        while (poll != null) {
            //因为队列后面优先最高
            sortedHotList.add(0, poll);
            poll = priorityQueue.poll();
        }
        log.info("测试打印排序好的List{}",sortedHotList.toString());

        return sortedHotList;


    }
}
