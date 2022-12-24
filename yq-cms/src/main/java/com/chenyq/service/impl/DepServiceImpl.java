package com.chenyq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenyq.domain.*;
import com.chenyq.mapper.DepMapper;
import com.chenyq.service.DepService;
import com.chenyq.utils.FormatDate;
import com.chenyq.utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepServiceImpl extends ServiceImpl<DepMapper, Dep> implements DepService {
    @Override
    public Result<ResultList> selectPageByDomain(Terms terms) {
        // 定义查询条件
        LambdaQueryWrapper<Dep> lqw = new LambdaQueryWrapper<>();
        if (terms.getName() != null) {
            lqw.like(Dep::getName, terms.getName());
        }
        if (terms.getLeader() != null) {
            lqw.like(Dep::getLeader, terms.getLeader());
        }
        if (terms.getCreateTime() != null && terms.getCreateTime().size() != 0) {
            String statTime = FormatDate.formatDate(terms.getCreateTime().get(0));
            String endTime = FormatDate.formatDate(terms.getCreateTime().get(1));
            log.error(statTime);
            log.error(endTime);
            lqw.between(Dep::getCreateTime, statTime, endTime);
        }
        lqw.orderByDesc(Dep::getCreateTime);

        if (terms.getPage() != null && terms.getSize() != null) {
            IPage page = new Page(terms.getPage(), terms.getSize());
            this.page(page, lqw);
            List<Dep> depList = page.getRecords();
            ResultList resultList = new ResultList<Dep>();
            resultList.setList(depList);
            resultList.setCurrentPage(page.getCurrent());
            resultList.setSize(page.getSize());
            resultList.setTotalPages(page.getPages());
            resultList.setTotalCount(page.getTotal());

            return Result.success(resultList);
        } else {
            // 按条件查询全部
            List<Dep> deps = this.list(lqw);
            ResultList resultList = new ResultList();
            resultList.setList(deps);

            return Result.success(resultList);
        }
    }
}
