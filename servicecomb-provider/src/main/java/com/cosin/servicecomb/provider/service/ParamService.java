package com.cosin.servicecomb.provider.service;

import com.cosin.servicecomb.provider.model.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>ClassName: servicecomb-demo</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 15:44 2019/11/4</p>
 */
@Slf4j
@Service
public class ParamService extends BaseService{

    public List<Parameter> listParameter(String[] name){
        return  paramMapper.list(name);
    }
}
