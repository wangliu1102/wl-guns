/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl.guns.modular.system.service.impl;

import cn.stylefeng.roses.core.datascope.DataScope;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wl.guns.core.common.constant.state.ManagerStatus;
import com.wl.guns.core.util.DateUtil;
import com.wl.guns.core.util.poi_excel.PoiUtil;
import com.wl.guns.core.util.poi_excel.WriteExcelDataDelegated;
import com.wl.guns.modular.system.dao.UserMapper;
import com.wl.guns.modular.system.model.User;
import com.wl.guns.modular.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author 王柳123
 * @since 2018-02-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public int setStatus(Integer userId, int status) {
        return this.baseMapper.setStatus(userId, status);
    }

    @Override
    public int changePwd(Integer userId, String pwd) {
        return this.baseMapper.changePwd(userId, pwd);
    }

    @Override
    public List<Map<String, Object>> selectUsers(DataScope dataScope, String name, String beginTime, String endTime, Integer deptid) {
        return this.baseMapper.selectUsers(dataScope, name, beginTime, endTime, deptid);
    }

    @Override
    public int setRoles(Integer userId, String roleIds) {
        return this.baseMapper.setRoles(userId, roleIds);
    }

    @Override
    public User getByAccount(String account) {
        return this.baseMapper.getByAccount(account);
    }

    @Override
    public void exportPoi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = request.getParameter("name");
        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");

        // 总记录数
        Integer totalRowCount = baseMapper.selectListCount(name, beginTime, endTime);
        log.info("totalRowCount:  " + totalRowCount);
        Integer start = PoiUtil.getStart(totalRowCount);

        // 导出EXCEL文件名称
        String filaName = "用户信息_" + DateUtil.getTime(new Date());

        // 标题
        String[] titles = {"ID", "账号", "姓名", "性别", "生日", "部门", "邮箱", "电话", "创建时间", "状态"};

        String[] datas = {"id", "account", "name", "sexName", "birthday", "deptName", "email", "phone", "createtime", "statusName"};


        // 开始导入
        PoiUtil.exportExcelToWebsite(start, request, response, totalRowCount, filaName, titles, new WriteExcelDataDelegated() {
            @Override
            public Boolean writeExcelData(SXSSFSheet eachSheet, Integer startRowCount, Integer endRowCount, Integer currentPage, Integer pageSize) throws Exception {
                Pagination page = new Pagination(currentPage, pageSize);
                log.info("currentPage:" + currentPage + ",pageSize:" + pageSize);
                List<Map<String, Object>> result = baseMapper.selectPoiExport(page, name, beginTime, endTime);
                log.info("writeExcelData------------result.size: " + result.size());

                Boolean isDownload = PoiUtil.writeWorkbook(datas, result, eachSheet, startRowCount, endRowCount);
                return isDownload;
            }
        });
    }

    @Override
    public List<User> writeExelData(InputStream is) {
        List<List<String>> list = PoiUtil.readExcelContent(is);

        // 要根据账号去重
        List<User> userList = new ArrayList<>();
        List<String> row;
        User user;
        String account;
        for (int i = 0, j = list.size(); i < j; i++) {
            row = list.get(i);
            account = row.get(0);
            EntityWrapper<User> wrapper = new EntityWrapper<>();
            // 账号在数据库中重复的数量
            Integer num = baseMapper.selectCount(wrapper.eq("account", account).ne("status", ManagerStatus.DELETED));

            for (int k = 0, m = userList.size(); k < m; k++) {
                if (account.equals(userList.get(k).getAccount())) {
                    // 文件中的账号重复
                    num = 1;
                    break;
                }
            }
            // 账号在文件和数据库中都不重复
            if (num == 0) {
                user = new User();
                user.setAccount(account);
                user.setName(row.get(1));
                user.setSex(Integer.valueOf(row.get(2)));
                user.setBirthday(DateUtil.parseTime(row.get(3)));
                user.setDeptid(Integer.valueOf(row.get(4)));
                user.setEmail(row.get(5));
                user.setPhone(row.get(6));
                user.setStatus(Integer.valueOf(row.get(7)));
                user.setCreatetime(new Date());

                userList.add(user);

            }
        }

        return userList;
    }
}
