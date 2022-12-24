package com.chenyq.controller;

import com.chenyq.domain.Dep;
import com.chenyq.domain.ResultList;
import com.chenyq.domain.Terms;
import com.chenyq.service.DepService;
import com.chenyq.service.TokenService;
import com.chenyq.utils.CurrentAcc;
import com.chenyq.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/deps")
public class DepController {
    @Autowired
    private DepService depService;
    @Autowired
    private TokenService tokenService;
    private SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");

    /**
     * 根据id查询部门
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Dep> selectById(@PathVariable Long id) {
        Dep dep = depService.getById(id);
        if (dep == null) {
            return Result.error("您查询的部门不存在!");
        }
        return Result.success(dep);
    }

    /**
     * 获取部门信息列表
     * @return
     */
    @PostMapping("/list")
    public Result<ResultList> selectAll(@RequestBody Terms terms) {
        return depService.selectPageByDomain(terms);
    }

    /**
     * 创建部门接口
     * @param request
     * @param dep
     * @return
     */
    @PostMapping()
    public Result<String> save(HttpServletRequest request, @RequestBody Dep dep) {
        if (dep.getName() == null || dep.getLeader() == null) {
            return Result.error("请输入完整的表单信息!");
        }

        // 设置创建修改时间
        dep.setCreateTime(formatdate.format(new Date()));
        dep.setUpdateTime(formatdate.format(new Date()));
        // 设置创建人修改人
        Long id = CurrentAcc.getCurrentId(request, tokenService);
        dep.setCreateUser(id);
        dep.setUpdateUser(id);

        depService.save(dep);
        return Result.success("创建成功!");
    }

    /**
     * 更新/修改部门功能
     * @param request
     * @param dep
     * @return
     */
    @PutMapping()
    public Result<String> update(HttpServletRequest request, @RequestBody Dep dep) {
        // 设置更新时间/更新人
        dep.setUpdateTime(formatdate.format(new Date()));
        Long id = CurrentAcc.getCurrentId(request, tokenService);
        dep.setUpdateUser(id);

        boolean res = depService.updateById(dep);
        if (res) {
            return Result.success("修改成功!");
        }
        return Result.error("修改失败!");
    }

    /**
     * 删除部门功能
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (id < 6) return Result.error("id小于6的部门不允许删除");

        boolean res = depService.removeById(id);

        if (res) {
            return Result.success("删除成功!");
        }
        return Result.error("删除失败!");
    }
}
