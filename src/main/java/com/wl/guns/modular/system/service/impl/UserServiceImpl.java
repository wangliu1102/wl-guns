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

import cn.hutool.core.date.DateUtil;
import cn.stylefeng.roses.core.datascope.DataScope;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.wl.guns.core.common.constant.state.ManagerStatus;
import com.wl.guns.core.common.exception.BizExceptionEnum;
import com.wl.guns.core.util.annotationexcel.StringUtils;
import com.wl.guns.core.util.poiexcel.PoiUtil;
import com.wl.guns.core.util.poiexcel.WriteExcelDataDelegated;
import com.wl.guns.modular.system.dao.UserMapper;
import com.wl.guns.modular.system.model.User;
import com.wl.guns.modular.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

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
        String filaName = "用户信息_" + DateUtil.formatDateTime(new Date());

        // 标题
        String[] titles = {"ID", "账号", "姓名", "性别", "生日", "部门", "邮箱", "电话", "创建时间", "状态"};
        // 查询的sql对应的名称
        String[] datas = {"id", "account", "name", "sexName", "birthday", "deptName", "email", "phone", "createtime", "statusName"};

        // 开始导出
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
                user.setBirthday(DateUtil.parseDateTime(row.get(3)));
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

    @Override
    public List<User> selectUserList(User user) {
        return baseMapper.selectUserList(user);
    }

    @Override
    public ResponseData importUser(List<User> userList, boolean updateSupport) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException(BizExceptionEnum.IMPORT_EXCEL_NULL);
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        List<User> newUserList = new ArrayList<>();
        List<User> updateUserList = new ArrayList<>();
        for (User user : userList) {
            try {
                // 验证是否存在这个用户
                User u = baseMapper.getByAccount(user.getAccount());
                if (StringUtils.isNull(u)) {
                    user.setCreatetime(new Date());
                    newUserList.add(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getAccount() + " 导入成功");
                } else if (updateSupport) {
                    // 已存在的用户,要做更新
                    user.setId(u.getId());
                    user.setCreatetime(new Date());
                    updateUserList.add(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getAccount() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getAccount() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getAccount() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            return ResponseData.error(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        Map map = new HashMap();
        map.put("new", newUserList);
        map.put("old", updateUserList);
        return ResponseData.success(200, successMsg.toString(), map);
    }
}
