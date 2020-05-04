package com.cosin.servicecomb.provider.application.mapper;

import com.cosin.servicecomb.provider.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserMapper {

    /**
     * 检索用户信息
     */
    public User getUserById(@Param("id") String id);

    List<User> list(Map<String, Object> params);
    
    int insert(User u);

    int update(User u);

    int delete(@Param("ids") String[] ids);

    int updatePwd(@Param("id") String id, @Param("pwd") String encryptedPwd);

    User getUserByLoginName(@Param("loginName") String loginName);

    int getLoginNameCount(@Param("name") String value);

    public void updateUserStatus(@Param("ids") String[] ids, @Param("status") String status, @Param("updateTime") Date now);

    int updateAdminInfo(@Param("id") String id, @Param("isAdministratorReseted") Short isAdministratorReseted);
}
