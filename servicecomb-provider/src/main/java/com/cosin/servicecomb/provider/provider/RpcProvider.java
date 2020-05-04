package com.cosin.servicecomb.provider.provider;

import com.cosin.servicecomb.provider.service.api.RpcService;
import org.apache.servicecomb.provider.pojo.RpcSchema;

/**
 * <p>ClassName: servicecomb-demo</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 13:16 2019/11/1</p>
 */
@RpcSchema(schemaId = "hello")
public class RpcProvider implements RpcService {

    @Override
    public String helloServicecomb() {
        return "helloServicecomb";
    }
}
