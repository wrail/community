package com.wrial.community.cahce;

import com.wrial.community.dto.TagDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//功能：统一管理所有标签
public class TagCache {

    public static List<TagDTO> getTags() {
        ArrayList<TagDTO> language = new ArrayList<>();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setCategoryName("编程语言");
        tagDTO.setTagList(Arrays.asList("JS", "Java", "PHP", "ASP", "CSS", "HTML", "GO", "PYTHON", "C", "C++"));
        language.add(tagDTO);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTagList(Arrays.asList("Django", "Spring", "Spring MVC", "Spring Security", "Struts", "Mybatis", "Hibernate", "Koa"));
        language.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTagList(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存 tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        language.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTagList(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosql memcached", "sqlserver", "postgresql", "sqlite"));
        language.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategoryName("开发工具");
        tool.setTagList(Arrays.asList("git", "github", "visual-studio-code", "vim", "sublime-text", "xcode intellij-idea", "eclipse", "maven", "ide", "svn", "visual-studio", "atom emacs", "textmate", "hg"));
        language.add(tool);

        return language;
    }


    //将所有不合法的收集起来
    public static String filterValidTags(String tag) {
        String[] split = tag.split("，");
        List<TagDTO> tags = getTags();

        List<String> allTags = tags.stream().flatMap(t -> t.getTagList().stream()).collect(Collectors.toList());

        String collect = Arrays.asList(split).stream().filter(t -> !allTags.contains(t)).collect(Collectors.joining("，"));
        return collect;

    }


}
