package com.wrial.community.schedule;
/*
 * @Author  Wrial
 * @Date Created in 20:23 2019/8/4
 * @Description 使用定时器来实现热门标签
 */

import com.wrial.community.cahce.HotTagCache;
import com.wrial.community.dto.HotTagDTO;
import com.wrial.community.mapper.QuestionMapper;
import com.wrial.community.model.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class HotTagTasks {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private HotTagCache hotTagCache;


    //    @Scheduled(fixedDelay = 5000)
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 3)
    public List<HotTagDTO> hotTagSchedule() {

        //根据offset和limit循环统计，每次统计20条，直至结束
        int offset = 0;
        int limit = 20;

        List<Question> questions = new ArrayList<>();
        //每次定时任务都是一个新的开始
        Map<String, Integer> priorityMap = new HashMap<>();

        log.info("start schedule");
        //拿出所有标签并计算权重
        while (offset == 0 || limit == questions.size()) {
            questions = questionMapper.selectByExampleAndRowBounds(new Example(Question.class), new RowBounds(offset, limit));
            for (Question question : questions) {
                String[] tags = question.getTag().split("，");
                for (String tag : tags) {
                    //每一个问题使用该标签   +10   每一条评论  +2   每一个阅读 +1
                    Integer priority = priorityMap.get(tag);
                    if (priority != null) {
                        priorityMap.put(tag, priority + 10 + question.getCommentCount() * 2 + question.getViewCount());
                    } else {
                        priorityMap.put(tag, 10 + question.getCommentCount() * 2 + question.getViewCount());
                    }
                }
            }
            //准备下一轮的循环
            offset += limit;
        }
        priorityMap.forEach((k, v) -> {
            log.info(k + "的权重" + v);
        });
        log.info("end schedule");

        List<HotTagDTO> hotTagDTOS = hotTagCache.updateTags(priorityMap);
        return hotTagDTOS;

    }


}
