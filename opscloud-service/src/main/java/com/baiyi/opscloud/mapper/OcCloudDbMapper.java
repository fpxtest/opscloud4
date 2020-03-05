package com.baiyi.opscloud.mapper;

import com.baiyi.opscloud.domain.generator.OcCloudDb;
import com.baiyi.opscloud.domain.param.cloud.CloudDBParam;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OcCloudDbMapper extends Mapper<OcCloudDb> {

    List<OcCloudDb> fuzzyQueryOcCloudDbByParam(CloudDBParam.PageQuery pageQuery);
}