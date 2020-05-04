package com.cosin.servicecomb.consumer.service;

import com.cosin.servicecomb.provider.model.*;
import com.cosin.servicecomb.provider.service.api.QuotaService;
import com.cosin.servicecomb.provider.service.api.RpcService;
import com.cosin.servicecomb.provider.service.api.UserService;
import org.apache.servicecomb.provider.pojo.RpcReference;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RpcServiceImpl{
//    从注册中心获取
//    从APPLICATION_ID获取id；从service_description.name获取name
//    schemaId来自于服务提供者
    @RpcReference(microserviceName = "start.servicecomb.huayun:ServiceCombDemo", schemaId = "hello")
    private RpcService rpcService;

    public void helloServicecomb() {
        System.out.println(rpcService.helloServicecomb());
    }

    @RpcReference(microserviceName = "start.servicecomb.huayun:ServiceCombDemo", schemaId = "quota")
    private QuotaService quotaService;

    public void print() {
        System.out.println(quotaService.print());
    }

    public void updateQuota(UpdateQuotaListRequestDto updateQuotaListRequestDto) {
        quotaService.updateQuota(updateQuotaListRequestDto);
    }

    public ReserveQuotaResp reserveQuota(ReserveQuotaRequestDto reserveQuotaReqDto) {
        return  quotaService.reserveQuota(reserveQuotaReqDto);
    }

    public void createQuota(CreateQuotaListRequestDto createQuotaReqDto) {
        quotaService.createQuota(createQuotaReqDto);
    }

    public List<ListQuotaResp> listQuota(ListQuotaRequestDto listQuotaRequestDto) {
        return quotaService.listQuota(listQuotaRequestDto);
    }

    public List<Parameter> listParameter() {
        return quotaService.listParameter();
    }


    @RpcReference(microserviceName = "start.servicecomb.huayun:ServiceCombDemo", schemaId = "user")
    private UserService userService;

    public List<User> listUser(ListUserRequestDto dto) {
        return userService.listUser(dto);
    }
    
}
