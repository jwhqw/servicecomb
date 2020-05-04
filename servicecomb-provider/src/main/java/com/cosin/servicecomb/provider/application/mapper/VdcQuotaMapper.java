package com.cosin.servicecomb.provider.application.mapper;


import com.cosin.servicecomb.provider.model.VdcQuota;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VdcQuotaMapper {
    int deleteByPrimaryKey(String id);

    int insert(VdcQuota record);

    int insertSelective(VdcQuota record);

    VdcQuota selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(VdcQuota record);

    int updateByPrimaryKey(VdcQuota record);

    List<VdcQuota> selectByVdcId(String vdcId);
    
    void addBatch(@Param("quotaList") List<VdcQuota> quotaList);

    void deleteQuotaByVdcIds(@Param("vdcIds") List<String> vdcIds);

    void updateQuotas(@Param("quotas") List<VdcQuota> quotas);

    Integer selectQuotaBySourceType(@Param("vdcIds") List<String> vdcIds, @Param("type") String type);
    
    List<String> selectNoBareMachineTypeIds(@Param("type") String type);

    List<String> selectNoLoadBalancerTypeIds(@Param("type") String type);

    List<String> selectNoElasticIpTypeIds(@Param("type") String type);

    void addBatchInsert(@Param("vdcIds") List<String> vdcIds, @Param("type") String type);
}