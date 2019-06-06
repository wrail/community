package com.wrial.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//利用面向对象思想
@Data
public class PaginationDTO {
    //展现所有问题
    private List<QuestionDTO> questions;
    //是否有前一个页面
    private boolean showPrevious;
    //是否是第一个页面
    private boolean showFirstPage;
    //是否有下一个
    private boolean showNext;
    //是否是最后一页
    private boolean showEndPage;
    //当前页
    private Integer page;
    //保存当前需要展示的页面
    private List<Integer> pages = new ArrayList<>();
    //总页数
    private Integer totalPage;


    public void setPagination(Integer totalCount, Integer page, Integer size) {


        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = (totalCount / size) + 1;
        }

        //增加容错处理,不然在页面上输入-1都会显示
        if (page < 1) {
            page = 1;
            this.page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
            this.page = totalPage;
        }
        //又一个粗心的点，没有给page赋值,并且不能放在最开始,因为后边的容错对这个值进行了改变
        this.page = page;
        //如果page=1，那就没有前一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }

        //如果page=totalCount，那就没有下一页
        if (page.equals(totalPage)) {
            showNext = false;
        } else {
            showNext = true;
        }


        //如果包含第一页就不用有跳转到第一页的箭头了
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        if (pages.contains(totalCount)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }


        //此处粗心大意没有加当前页，所以在前台没有当前页码
        pages.add(page);
        //实现页面页码
        for (int i = 1; i <= 3; i++) {

            if (page - i > 0) {
                //每次都加到第一个位置，并且移动后边元素
                pages.add(0, page - i);
            }
            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }


    }
}
