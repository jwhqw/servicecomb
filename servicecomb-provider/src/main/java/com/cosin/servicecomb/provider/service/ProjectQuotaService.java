package com.cosin.servicecomb.provider.service;

import com.cosin.servicecomb.provider.model.*;
import com.cosin.servicecomb.provider.utils.IdentityUtils;
import com.cosin.servicecomb.provider.utils.QuotaReserveStatus;
import com.cosin.servicecomb.provider.utils.ResourceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectQuotaService extends BaseService{

	/**
	 * 
	 * @Title: updateQuota
	 * @Description: TODO( )
	 * @param @param
	 *            updateQuotaReqDto 参数
	 * @return void 返回类型
	 * @throws @author
	 *             sunhao
	 */
	public void updateQuota(List<UpdateQuotaReqDto> updateQuotaReqDto) {
		if (CollectionUtils.isEmpty(updateQuotaReqDto)) {
			return;
		}

		validate(updateQuotaReqDto);
		processUpdateQuota(updateQuotaReqDto);
	}

	/**
	 * 创建配额
	 * 
	 * @Title: createQuota
	 * @Description: TODO( )
	 * @param @param
	 *            createQuotaReqDto 参数
	 * @return void 返回类型
	 * @throws @author
	 *             caizhiyang
	 */
	public void createQuota(List<CreateQuotaRequestDto> createQuotaReqDto) {
		if (CollectionUtils.isEmpty(createQuotaReqDto)) {
			return;
		}
		List<String> ownerIds = createQuotaReqDto.stream().map(quota -> quota.getOwnerId())
				.collect(Collectors.toList());
		ListQuotaRequestDto listQuotaReqDto = new ListQuotaRequestDto();
		listQuotaReqDto.setOwnerIds(ownerIds);
		List<ProjectQuotaModel> projectQuotaModels = projectQuotaMapper.list(listQuotaReqDto);

		List<CreateQuotaRequestDto> createProjectQuotas = new ArrayList<CreateQuotaRequestDto>();
		List<UpdateQuotaReqDto> updateProjectQuotas = new ArrayList<UpdateQuotaReqDto>();

		createQuotaReqDto.stream().forEach(addQuota -> {
			List<ProjectQuotaModel> quotaItem = projectQuotaModels.stream()
					.filter(currentQuota -> currentQuota.getType().equalsIgnoreCase(addQuota.getType()))
					.collect(Collectors.toList());
			if (CollectionUtils.isEmpty(quotaItem)) {
				createProjectQuotas.add(addQuota);
			} else {
				UpdateQuotaReqDto updateQuotaReq = new UpdateQuotaReqDto();
				BeanUtils.copyProperties(addQuota, updateQuotaReq);
				updateQuotaReq.setType(QuotaEnum.valueOf(addQuota.getType()));
				updateProjectQuotas.add(updateQuotaReq);
			}

		});
		// 创建新配额类型
		if (!CollectionUtils.isEmpty(createProjectQuotas)) {
			List<String> createOwnerIds = createProjectQuotas.stream().map(quota -> quota.getOwnerId())
					.collect(Collectors.toList());
			List<ProjectQuotaModel> createQuotaItem = projectQuotaModels.stream()
					.filter(currentQuota -> createOwnerIds.contains(currentQuota.getOwnerId()))
					.collect(Collectors.toList());
			processCreateQuota(createProjectQuotas, createQuotaItem);
		}
		// 更新现有配额
		if (!CollectionUtils.isEmpty(updateProjectQuotas)) {
			processUpdateQuota(updateProjectQuotas);
		}
	}

	private void processCreateQuota(List<CreateQuotaRequestDto> createProjectQuotas,
			List<ProjectQuotaModel> projectQuotaModels) {
		List<String> createOwnerIds = createProjectQuotas.stream().map(quota -> quota.getOwnerId())
				.collect(Collectors.toList());
		List<Project> projects = new ArrayList<>();
		Map<String, List<VdcQuota>> vdcQuotas = new HashMap<String, List<VdcQuota>>();
		createOwnerIds.stream().forEach(ownerId -> {
			Project owner = projectMapper.getProjectById(ownerId, null);
			projects.add(owner);

			List<VdcQuota> vdcQuotaList = vdcQuotaMapper.selectByVdcId(owner.getVdcId());
			vdcQuotas.put(owner.getId(), vdcQuotaList);
		});

		List<ProjectQuotaModel> quotas = new ArrayList<>();
		createProjectQuotas.stream().forEach(addQuota -> {
			ProjectQuotaModel newQuotaModel = new ProjectQuotaModel();
			BeanUtils.copyProperties(addQuota, newQuotaModel);
			newQuotaModel.setCreateTime(new Date());
			newQuotaModel.setUpdateTime(newQuotaModel.getCreateTime());
			quotas.add(newQuotaModel);

			// 同步 vdc配额
			Project owner = projects.stream().filter(item -> item.getId().equals(addQuota.getOwnerId()))
					.collect(Collectors.toList()).get(0);
			List<VdcQuota> vdcQuotaFilterOwner = vdcQuotas.get(owner.getId());
			if (!CollectionUtils.isEmpty(vdcQuotaFilterOwner)) {
				vdcQuotaFilterOwner.stream()
						.filter(vdcQuotaFilter -> vdcQuotaFilter.getType().equalsIgnoreCase(addQuota.getType()))
						.forEach(quota -> {
							quota.setRemain(quota.getRemain() - addQuota.getLimit().intValue());
							vdcQuotaMapper.updateByPrimaryKey(quota);
						});
			}
		});
		if (!CollectionUtils.isEmpty(quotas)) {
			projectQuotaMapper.batchAdd(quotas);
		}

	}

	/**
	 * 创建配额項目創建專用
	 * 
	 * @Title: createQuota
	 * @Description: TODO( )
	 * @param @param
	 *            createObject 参数
	 * @return void 返回类型
	 * @throws @author
	 *             caizhiyang
	 */
	public void createQuotaByProject(CreateProjectObject createObject) {
		List<ProjectQuotaModel> quotas = new ArrayList<>();
		if (createObject != null && createObject.getQuota() != null) {
			ProjectQuotaModel newCpuQuota = new ProjectQuotaModel();
			newCpuQuota.setCreateTime(new Date());
			newCpuQuota.setUpdateTime(new Date());
			newCpuQuota.setLimit(createObject.getQuota().getCpu());
			newCpuQuota.setType(QuotaEnum.CPU.toString());
			newCpuQuota.setOwnerId(createObject.getId());
			newCpuQuota.setReserved(0L);
			newCpuQuota.setUsed(0L);
			quotas.add(newCpuQuota);

			ProjectQuotaModel newDiskQuota = new ProjectQuotaModel();
			newDiskQuota.setCreateTime(new Date());
			newDiskQuota.setUpdateTime(new Date());
			newDiskQuota.setLimit(createObject.getQuota().getDisk());
			newDiskQuota.setType(QuotaEnum.DISK.toString());
			newDiskQuota.setOwnerId(createObject.getId());
			newDiskQuota.setReserved(0L);
			newDiskQuota.setUsed(0L);
			quotas.add(newDiskQuota);

			ProjectQuotaModel newElasticIpQuota = new ProjectQuotaModel();
			newElasticIpQuota.setCreateTime(new Date());
			newElasticIpQuota.setUpdateTime(new Date());
			newElasticIpQuota.setLimit(createObject.getQuota().getElasticIp());
			newElasticIpQuota.setType(QuotaEnum.ELASTIC_IP.toString());
			newElasticIpQuota.setOwnerId(createObject.getId());
			newElasticIpQuota.setReserved(0L);
			newElasticIpQuota.setUsed(0L);
			quotas.add(newElasticIpQuota);

			ProjectQuotaModel newMemoryQuota = new ProjectQuotaModel();
			newMemoryQuota.setCreateTime(new Date());
			newMemoryQuota.setUpdateTime(new Date());
			newMemoryQuota.setLimit(createObject.getQuota().getMemory());
			newMemoryQuota.setType(QuotaEnum.MEMORY.toString());
			newMemoryQuota.setOwnerId(createObject.getId());
			newMemoryQuota.setReserved(0L);
			newMemoryQuota.setUsed(0L);
			quotas.add(newMemoryQuota);

			ProjectQuotaModel newNetworkIpQuota = new ProjectQuotaModel();
			newNetworkIpQuota.setCreateTime(new Date());
			newNetworkIpQuota.setUpdateTime(new Date());
			newNetworkIpQuota.setLimit(createObject.getQuota().getNetworkIp());
			newNetworkIpQuota.setType(QuotaEnum.NETWORK_IP.toString());
			newNetworkIpQuota.setOwnerId(createObject.getId());
			newNetworkIpQuota.setReserved(0L);
			newNetworkIpQuota.setUsed(0L);
			quotas.add(newNetworkIpQuota);

			ProjectQuotaModel newBareQuota = new ProjectQuotaModel();
			newBareQuota.setCreateTime(new Date());
			newBareQuota.setUpdateTime(new Date());
			newBareQuota.setLimit(createObject.getQuota().getBareMachine());
			newBareQuota.setType(QuotaEnum.BARE_MACHINE.toString());
			newBareQuota.setOwnerId(createObject.getId());
			newBareQuota.setReserved(0L);
			newBareQuota.setUsed(0L);
			quotas.add(newBareQuota);

			ProjectQuotaModel newLbaasQuota = new ProjectQuotaModel();
			newLbaasQuota.setCreateTime(new Date());
			newLbaasQuota.setUpdateTime(new Date());
			newLbaasQuota.setLimit(createObject.getQuota().getLoadbalancer());
			newLbaasQuota.setType(QuotaEnum.LOADBALANCER.toString());
			newLbaasQuota.setOwnerId(createObject.getId());
			newLbaasQuota.setReserved(0L);
			newLbaasQuota.setUsed(0L);
			quotas.add(newLbaasQuota);

			// ProjectQuotaModel newVGPUQuota = new ProjectQuotaModel();
			// newVGPUQuota.setCreateTime(new Date());
			// newVGPUQuota.setUpdateTime(new Date());
			// newVGPUQuota.setLimit(createObject.getQuota().getVGPU());
			// newVGPUQuota.setType(QuotaEnum.vGPU.toString());
			// newVGPUQuota.setOwnerId(createObject.getId());
			// quotas.add(newVGPUQuota);
		}
		if (!CollectionUtils.isEmpty(quotas)) {
			projectQuotaMapper.batchAdd(quotas);
		}

	}

	private void validate(List<UpdateQuotaReqDto> updateQuotaReqDto) {
		updateQuotaReqDto.forEach(cq -> {
			if (StringUtils.isEmpty(cq.getOwnerId())) {
//				new Builder().addParam("param", "ownerId").build()
//						.throwExceptionWithParams(HttpStatus.BAD_REQUEST, IdentityExceptionCode.INVALID_PARAM);
			} else {
				Project project = projectMapper.getProjectById(cq.getOwnerId(), null);
				if (project == null) {
					new CloudCommonExceptionHelper.Builder().addParam("ownerId", cq.getOwnerId()).build()
							.throwExceptionWithParams(HttpStatus.BAD_REQUEST, IdentityExceptionCode.OWNER_NOT_FOUND);
				}
			}
			if (cq.getType() == null) {
				new CloudCommonExceptionHelper.Builder().addParam("param", "type").build()
						.throwExceptionWithParams(HttpStatus.BAD_REQUEST, IdentityExceptionCode.INVALID_PARAM);
			}
			if (cq.getLimit() == null) {
				new CloudCommonExceptionHelper.Builder().addParam("param", "limit").build()
						.throwExceptionWithParams(HttpStatus.BAD_REQUEST, IdentityExceptionCode.INVALID_PARAM);
			}
		});
	}

	private void processUpdateQuota(List<UpdateQuotaReqDto> resourceReq) {
		if (CollectionUtils.isEmpty(resourceReq)) {
			return;
		}
		List<ProjectQuotaModel> update = new ArrayList<>();
		// 项目配额变更量
		Map<String, Long> updateQutatoMap = new HashMap<>();
		String ownerId = resourceReq.get(0).getOwnerId();
		Project owner = projectMapper.getProjectById(ownerId, null);
		if (owner == null) {
			new CloudCommonExceptionHelper.Builder().addParam("ownerId", ownerId).build()
					.throwExceptionWithParams(HttpStatus.BAD_REQUEST, IdentityExceptionCode.OWNER_NOT_FOUND);
		}
		try {
			resourceReq.forEach(ud -> {
				// 根据resource和owner查找出对应的配额信息
				ProjectQuotaModel quota = projectQuotaMapper.query(ListQuotaRequestDto.builder()
						.ownerIds(Arrays.asList(ud.getOwnerId())).type(ud.getType().name()).build());

				if (quota == null) {
					new CloudCommonExceptionHelper.Builder().build().throwExceptionWithParams(HttpStatus.BAD_REQUEST,
							IdentityExceptionCode.QUOTA_NOT_EXIST);
				}
				// 记录变量配额
				if (ud.getLimit() - quota.getLimit() != 0) {
					updateQutatoMap.put(quota.getOwnerId() + quota.getType(), quota.getLimit() - ud.getLimit());
				}

				quota.setLimit(ud.getLimit());
				quota.setUpdateTime(new Date());
				update.add(quota);
			});
		} catch (Exception e) {
			throw e;
		}
		// 更新 项目总配额
		if (!CollectionUtils.isEmpty(update)) {
			update.forEach(projectQuotaMapper::modify);
		}
		// 同步VDC 配额
		ListQuotaRequestDto listQuotaReqDto = new ListQuotaRequestDto();
		List<String> owners = new ArrayList<String>();
		owners.add(ownerId);
		listQuotaReqDto.setOwnerIds(owners);
		List<ProjectQuotaModel> projectQuotaModels = projectQuotaMapper.list(listQuotaReqDto);
		List<VdcQuota> vdcQuotas = vdcQuotaMapper.selectByVdcId(owner.getVdcId());
		if (!CollectionUtils.isEmpty(vdcQuotas) && !CollectionUtils.isEmpty(projectQuotaModels)) {
			vdcQuotas.stream().forEach(quota -> {
				if (updateQutatoMap.get(ownerId + quota.getType()) != null) {
					quota.setRemain(quota.getRemain() + updateQutatoMap.get(ownerId + quota.getType()).intValue());
					vdcQuotaMapper.updateByPrimaryKey(quota);
				}
			});
		}

	}

	/**
	 * 预扣配额
	 * 
	 * @Title: reserveQuota
	 * @Description: TODO( )
	 * @param @param
	 *            reserveQuotaReqDto 参数
	 * @return reserveId 返回类型
	 * @throws @author
	 *             caizhiyang
	 */
	public String reserveQuota(ReserveQuotaRequestDto reserveQuotaReqDto) {
		// 检查配额数量是否足够
		ListQuotaRequestDto listQuotaReqDto = new ListQuotaRequestDto();
		List<String> ownerIds = new ArrayList<>();
		ownerIds.add(reserveQuotaReqDto.getOwnerId());
		listQuotaReqDto.setOwnerIds(ownerIds);
		List<ProjectQuotaModel> quotas = projectQuotaMapper.list(listQuotaReqDto);
		if (CollectionUtils.isEmpty(quotas)) {
			new CloudCommonExceptionHelper.Builder().addParam("projectQuotas", null).build()
					.throwExceptionWithParams(HttpStatus.BAD_REQUEST, IdentityExceptionCode.QUOTA_NOT_EXIST);
		}
		String reserveId = null;
		List<ProjectQuotaModel> updateQuotaList = new ArrayList<ProjectQuotaModel>();
		if (!CollectionUtils.isEmpty(reserveQuotaReqDto.getQuotas())) {
			reserveQuotaReqDto.getQuotas().forEach(reserveQuota -> {
				List<ProjectQuotaModel> projQuota = quotas.stream()
						.filter(quota -> reserveQuota.getType().name().equalsIgnoreCase(quota.getType().toString()))
						.collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(projQuota)) {
					ProjectQuotaModel updateQuotaModel = projQuota.get(0);
					// 默认项目可强制更新项目资源配额limit
					if (reserveQuota.getIsForced() != null && reserveQuota.getIsForced()) {
						Long tempLimit = (updateQuotaModel.getUsed() != null ? updateQuotaModel.getUsed() : 0) + (updateQuotaModel.getReserved() != null ? updateQuotaModel.getReserved() : 0);
						updateQuotaModel.setLimit(updateQuotaModel.getLimit() < tempLimit ? tempLimit : updateQuotaModel.getLimit());
					} else {
						// 判断项目配额是否足够，默认项目不判断
						Long quotaRemain = (updateQuotaModel.getLimit() != null ? updateQuotaModel.getLimit() : 0L)
								- (updateQuotaModel.getUsed() != null ? updateQuotaModel.getUsed() : 0)
								- (updateQuotaModel.getReserved() != null && updateQuotaModel.getReserved() > 0
										? updateQuotaModel.getReserved()
										: 0);
                        log.info("预扣项目配额，ownerId：【" + ownerIds + "】类型为【" +  reserveQuota.getType().name() + "】," +
                                "used为：" + updateQuotaModel.getUsed() + ",reserved为：" + updateQuotaModel.getReserved());
						if (reserveQuota.getValue() > quotaRemain || (updateQuotaModel.getUsed() != null ? updateQuotaModel.getUsed() : 0)+reserveQuota.getValue()<0) {
							new CloudCommonExceptionHelper.Builder().addParam("type", ResourceTypeEnum.getResourceTypeEnum(updateQuotaModel.getType()))
									.build().throwExceptionWithParams(HttpStatus.BAD_REQUEST,
											IdentityExceptionCode.PROJECT_QUOTA_NOTENOUGH);
						}
					}
					// 更新预扣
					updateQuotaModel.setReserved(reserveQuota.getValue()
							+ (updateQuotaModel.getReserved() != null ? updateQuotaModel.getReserved() : 0));
					updateQuotaList.add(updateQuotaModel);
				}
			});

			// 新增预扣
			reserveId = IdentityUtils.generateUuid();
			ProjectQuotaReserveModel projectQuotaReserveModel = new ProjectQuotaReserveModel();
			projectQuotaReserveModel.setCreateTime(new Date());
			projectQuotaReserveModel.setUpdateTime(projectQuotaReserveModel.getCreateTime());
			projectQuotaReserveModel.setReserveId(reserveId);
			JSONObject playload = new JSONObject();
			playload.put("ownerId", reserveQuotaReqDto.getOwnerId());
			playload.put("quotas", reserveQuotaReqDto.getQuotas());
			projectQuotaReserveModel.setQuotas(playload.toString());
			projectQuotaReserveModel.setStatus(QuotaReserveStatus.Uncommitted.name());

			if (!CollectionUtils.isEmpty(updateQuotaList)) {
				// db 操作 更新项目配额
				updateQuotaList.forEach(projectQuotaMapper::modify);
			}
			// db 操作新增配额预扣信息
			projectQuotaMapper.addReserve(projectQuotaReserveModel);
			// db 批量操作成功
		}
		return reserveId;
	}
	
	
    public List<ProjectQuotaModel> list(ListQuotaRequestDto dto){
	    return  projectQuotaMapper.list(dto);
    }
}
