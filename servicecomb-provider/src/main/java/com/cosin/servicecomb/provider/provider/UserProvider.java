package com.cosin.servicecomb.provider.provider;

import com.cosin.servicecomb.provider.application.mapper.UserMapper;
import com.cosin.servicecomb.provider.model.ListUserRequestDto;
import com.cosin.servicecomb.provider.model.User;
import com.cosin.servicecomb.provider.service.api.UserService;
import com.cosin.servicecomb.provider.utils.IdentityUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>ClassName: servicecomb-demo</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 13:18 2019/11/3</p>
 */
@RpcSchema(schemaId = "user")
public class UserProvider implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<User> listUser(ListUserRequestDto request) {

        String like = "";
        if(request != null && org.apache.commons.lang3.StringUtils.isNotBlank(request.getLike())){
            like = request.getLike().replaceAll("%","/%").replaceAll("_","/_");
            request.setLike(like);
        }
        if (StringUtils.isNotBlank(request.getLoginNameLike())) {
            request.setLoginNameLike(request.getLoginNameLike().replaceAll("%","/%").replaceAll("_","/_"));
        }
        String userIds = request.getUserIds();
        Integer pageSize = request.getPageSize();
        Integer pageNumber = request.getPageNumber();
        String[] userIdsArray = null;
        if (userIds != null && !StringUtils.isEmpty(userIds)) {
            userIdsArray = userIds.split(",");
        }

        List<String> emailList = Lists.newArrayList();
        if (request.getEmails() != null && !StringUtils.isEmpty(request.getEmails().toString())) {
            emailList = Arrays.asList(request.getEmails());
        }

        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageSize == null || pageSize < 1) {
        }

        Map<String, Object> params = IdentityUtils.entityToMap(request);
        params.put("userIdsArray", userIdsArray);
        params.put("emailList", emailList);
        params.put("visitorId", request.getVisitorId());
        List<User> list = userMapper.list(params);

        return list;
    }
}
