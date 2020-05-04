package com.cosin.servicecomb.provider.provider;

import com.cosin.servicecomb.provider.model.*;
import com.cosin.servicecomb.provider.service.ParamService;
import com.cosin.servicecomb.provider.service.ProjectQuotaService;
import com.cosin.servicecomb.provider.service.api.QuotaService;
import com.cosin.servicecomb.provider.utils.EtcdConfig;
import com.cosin.servicecomb.provider.utils.EtcdLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>ClassName: servicecomb-demo</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 13:56 2019/10/30</p>
 */
@RpcSchema(schemaId = "quota")
@Slf4j
public class QuotaProvider implements QuotaService {
    
    @Autowired
    private EtcdConfig etcdConfig;
    
    @Autowired
    private ProjectQuotaService projectQuotaService;

    @Autowired
    private ParamService paramService;
    
    @Override
    public String print() {
        return "你好";
    }

    @Override
    public ReserveQuotaResp reserveQuota(ReserveQuotaRequestDto request) {
        log.info("reserve quota start execute，owner:"+request.getOwnerId());
        // 获取分布式锁
        // 1. 加锁
//        EtcdLock.LockResult lockResult = EtcdLock.getInstance(etcdConfig.endpoints).lock(etcdConfig.QUOTA_RESERVE_LOCK+request.getOwnerId(), 10);
        // 2. 执行业务
//        if (lockResult.getIsLockSuccess()) {
            try {
                String reserveId = projectQuotaService.reserveQuota(request);
                ReserveQuotaResp resp = new ReserveQuotaResp();
                resp.setReserveId(reserveId);
                return resp;
            } catch (Exception e) {
            } 
//            finally {
//                // 3. 解锁
//                if (lockResult != null) {
//                    EtcdLock.getInstance(etcdConfig.endpoints).unLock(etcdConfig.QUOTA_RESERVE_LOCK+request.getOwnerId(), lockResult);
//                }
//            }
//        } else {
//            log.error("failure to acquire reserveQuota distributed locks, request:" + request.toString());
//            new CloudCommonExceptionHelper.Builder()
//                    .addParam("ownerId", request.getOwnerId())
//                    .addParam("msg", "failure to acquire reserveQuota distributed locks")
//                    .build().throwExceptionWithParams(HttpStatus.BAD_REQUEST,
//                    IdentityExceptionCode.PROJECT_QUOTA_RESERVEERROR);
//        }
        log.info("reserve quota end success，owner:"+request.getOwnerId());
        
        return null;
    }

    @Override
    public void createQuota(CreateQuotaListRequestDto request) {
        projectQuotaService.createQuota(request.getCreateQuotaReqDto());
    }

    @Override
    public void updateQuota(UpdateQuotaListRequestDto request) {
        projectQuotaService.updateQuota(request.getUpdateQuotaReqDto());
    }

    @Override
    public List<ListQuotaResp> listQuota(ListQuotaRequestDto request) {

        ListQuotaRequestDto listQuotaReqDto = new ListQuotaRequestDto();
        listQuotaReqDto.setOwnerIds(request.getOwnerIds());
        listQuotaReqDto.setType(request.getType());
        if(!StringUtils.isBlank(request.getClazz())) {
            listQuotaReqDto.setType(request.getClazz());
        }
        List<ListQuotaResp> quotaResps = new ArrayList<ListQuotaResp>();
        List<ProjectQuotaModel> projectQuotaModels =  projectQuotaService.list(listQuotaReqDto);
        if(!CollectionUtils.isEmpty(projectQuotaModels)) {
            projectQuotaModels.stream().forEach(quotaModel->{
                ListQuotaResp quotaResp = new ListQuotaResp();
                BeanUtils.copyProperties(quotaModel, quotaResp);
                quotaResp.setReserved(quotaResp.getReserved()!=null&quotaResp.getReserved()<0? 0 : quotaResp.getReserved());
                quotaResp.setUsed(quotaResp.getUsed()!=null&quotaResp.getUsed()<0 ? 0 : quotaResp.getUsed());
                quotaResps.add(quotaResp);
            });
        }
        return quotaResps;
    }


    public List<Parameter> listParameter() {
        return paramService.listParameter(null);
    }
}
