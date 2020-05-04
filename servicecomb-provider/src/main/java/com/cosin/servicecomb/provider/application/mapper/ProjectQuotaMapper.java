package com.cosin.servicecomb.provider.application.mapper;

import com.cosin.servicecomb.provider.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProjectQuotaMapper {

	List<ProjectQuotaModel> list(ListQuotaRequestDto listQuotaReqDto);

	int batchAdd(List<ProjectQuotaModel> quotas);
	
	int add(CreateQuotaRequestDto createQuotaRequestDto);

	ProjectQuotaModel query(ListQuotaRequestDto listQuotaReqDto);

	int modify(ProjectQuotaModel quotas);

	int deleteByOwnerId(@Param("ownerId") String ownerId);
	
	int addReserve(ProjectQuotaReserveModel projectQuotaReserveModel);
	
	int updateReserve(ProjectQuotaReserveModel projectQuotaReserveModel);
	
	List<ProjectQuotaReserveModel> listReserveQuota(ProjectQuotaReserveModel projectQuotaReserveModel);
	
	 List<QuotaStatistic> countStatistic(@Param("clazz") QuotaEnum clazz, @Param("limit") Integer limit, @Param("sortType") String sortType, @Param("isUsed") Boolean isUsed, @Param("ownerIds") List<String> ownerIds);

    List<String> selectNoBareMachineTypeIds(@Param("type") String type);

    List<String> selectNoLoadBalancerTypeIds(@Param("type") String type);

    List<String> selectNoElasticIpTypeIds(@Param("type") String type);

    void addBatchInsert(@Param("ownerIds") List<String> ownerIds, @Param("type") String type);
}
