package com.cosin.servicecomb.provider.application.mapper;

import com.cosin.servicecomb.provider.model.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Xie Yifeng
 * @date 2018/5/7 16:03
 */
public interface ProjectMapper {

    Project getProjectById(@Param("projectId") String id, @Param("coverDel") Boolean coverDel);
}
