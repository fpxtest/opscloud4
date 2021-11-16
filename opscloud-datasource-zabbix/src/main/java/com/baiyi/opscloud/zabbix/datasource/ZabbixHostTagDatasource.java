package com.baiyi.opscloud.zabbix.datasource;

import com.baiyi.opscloud.common.config.CachingConfiguration;
import com.baiyi.opscloud.common.datasource.ZabbixConfig;
import com.baiyi.opscloud.zabbix.entry.ZabbixHost;
import com.baiyi.opscloud.zabbix.entry.ZabbixHostTag;
import com.baiyi.opscloud.zabbix.datasource.base.BaseZabbixDatasource;
import com.baiyi.opscloud.zabbix.http.SimpleZabbixRequest;
import com.baiyi.opscloud.zabbix.http.SimpleZabbixRequestBuilder;
import com.baiyi.opscloud.zabbix.param.ZabbixHostParam;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.baiyi.opscloud.zabbix.datasource.base.ZabbixServer.ApiConstant.HOST_IDS;
import static com.baiyi.opscloud.zabbix.datasource.base.ZabbixServer.ApiConstant.RESULT;

/**
 * @Author baiyi
 * @Date 2021/8/23 4:16 下午
 * @Version 1.0
 */
@Slf4j
@Component
public class ZabbixHostTagDatasource extends BaseZabbixDatasource<ZabbixHostTag> {

    @CacheEvict(cacheNames = CachingConfiguration.Repositories.ZABBIX, key = "#zabbix.url + '_host_tag_hostid' + #zabbixHost.hostid")
    public void evictHostTag(ZabbixConfig.Zabbix zabbix, ZabbixHost zabbixHost) {
        log.info("清除ZabbixHostTag缓存 : hostid = {}", zabbixHost.getHostid());
    }

    @Cacheable(cacheNames = CachingConfiguration.Repositories.ZABBIX, key = "#zabbix.url + '_host_tag_hostid' + #zabbixHost.hostid", unless = "#result == null")
    public ZabbixHostTag getHostTag(ZabbixConfig.Zabbix zabbix, ZabbixHost zabbixHost) {
        SimpleZabbixRequest request = SimpleZabbixRequestBuilder.builder()
                .method(ZabbixHostDatasource.HostAPIMethod.GET)
                .paramEntry("output", new String[]{"name"})
                .paramEntry("hostids", zabbixHost.getHostid())
                .paramEntry("selectTags", new String[]{"tag", "value"})
                .build();
        JsonNode data = call(zabbix, request);
        return mapperListGetOne(data.get(RESULT), ZabbixHostTag.class);
    }

    public void updateHostTags(ZabbixConfig.Zabbix zabbix, ZabbixHost zabbixHost, List<ZabbixHostParam.Tag> tags) {
        SimpleZabbixRequest request = SimpleZabbixRequestBuilder.builder()
                .method(ZabbixHostDatasource.HostAPIMethod.UPDATE)
                .paramEntry("hostid", zabbixHost.getHostid())
                .paramEntry("tags", tags)
                .build();
        JsonNode data = call(zabbix, request);
        if (data.get(RESULT).get(HOST_IDS).isEmpty()) {
            log.error("更新ZabbixHost主机标签失败: hostid = {}", zabbixHost.getHostid());
        } else {
            log.info("更新ZabbixHost主机标签: hostid = {}", zabbixHost.getHostid());
        }
    }
}