package com.cosin.servicecomb.consumer.controller;

import com.cosin.servicecomb.consumer.service.RpcServiceImpl;
import com.cosin.servicecomb.provider.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RpcConsumerController{

    @Autowired
    private RpcServiceImpl rpcService;

    @GetMapping("rpc")
    public void helloServicecomb(){
        rpcService.helloServicecomb();
    }


    @GetMapping("quota")
    public void print(){
        rpcService.print();
    }

    @PostMapping("/updateQuota")
    public void updateQuota(@RequestBody UpdateQuotaListRequestDto updateQuotaListRequestDto) {
        rpcService.updateQuota(updateQuotaListRequestDto);
    }

    @PostMapping("/reserveQuota")
    public ReserveQuotaResp reserveQuota(@RequestBody ReserveQuotaRequestDto reserveQuotaReqDto) {
        return  rpcService.reserveQuota(reserveQuotaReqDto);
    }

    @PostMapping("/createQuota")
    public void createQuota(@RequestBody List<CreateQuotaRequestDto> createQuotaReqDto) {
        CreateQuotaListRequestDto dto = new CreateQuotaListRequestDto();
        dto.setCreateQuotaReqDto(createQuotaReqDto);
        rpcService.createQuota(dto);
    }

    @PostMapping(value = "/listQuota")
    public List<ListQuotaResp> listQuota(@RequestBody ListQuotaRequestDto listQuotaRequestDto) {
        return rpcService.listQuota(listQuotaRequestDto);
    }

    @PostMapping("/listParameter")
    public List<Parameter> listParameter() {
        return rpcService.listParameter();
    }


    @PostMapping("/listUser")
    public List<User> listUser(@RequestBody ListUserRequestDto requestDto) {
        return rpcService.listUser(requestDto);
    }

}
