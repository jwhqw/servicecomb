package com.cosin.servicecomb.provider.service;

import com.cosin.servicecomb.provider.model.ListQuotaRequestDto;
import com.cosin.servicecomb.provider.model.ProjectQuotaModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>ClassName: servicecomb-demo</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 15:45 2019/11/4</p>
 */
@Service
public class UserService extends BaseService {

    public List<ProjectQuotaModel> list(ListQuotaRequestDto dto){
        return  projectQuotaMapper.list(dto);
    }
}
