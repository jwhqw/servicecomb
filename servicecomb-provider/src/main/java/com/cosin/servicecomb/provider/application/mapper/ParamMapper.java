package com.cosin.servicecomb.provider.application.mapper;

import com.cosin.servicecomb.provider.model.Parameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Xie Yifeng
 * @date 2018/5/7 16:03
 */
public interface ParamMapper {

    public List<Parameter> list(@Param("names") String[] name);

    public int updateByName(@Param("list") List<Parameter> list);

    public Parameter getByName(@Param("name") String name);
}
