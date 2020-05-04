package com.cosin.servicecomb.provider.service;

import com.cosin.servicecomb.provider.application.mapper.*;
import com.cosin.servicecomb.provider.utils.EtcdConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseService {
    @Autowired
    protected ProjectQuotaMapper projectQuotaMapper;
    
    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected VdcQuotaMapper vdcQuotaMapper;

    @Autowired
    protected ProjectMapper projectMapper;
    
    @Autowired
    protected ParamMapper paramMapper;

    @Autowired	
    protected EtcdConfig etcdConfig;	

}
