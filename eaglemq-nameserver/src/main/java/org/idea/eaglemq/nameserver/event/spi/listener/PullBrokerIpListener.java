package org.idea.eaglemq.nameserver.event.spi.listener;

import com.alibaba.fastjson.JSON;
import com.sun.security.ntlm.Server;
import org.idea.eaglemq.common.coder.TcpMsg;
import org.idea.eaglemq.common.dto.PullBrokerIpRespDTO;
import org.idea.eaglemq.common.enums.NameServerResponseCode;
import org.idea.eaglemq.common.enums.RegistryTypeEnum;
import org.idea.eaglemq.common.event.Listener;
import org.idea.eaglemq.nameserver.common.CommonCache;
import org.idea.eaglemq.nameserver.enums.PullBrokerIpRoleEnum;
import org.idea.eaglemq.nameserver.event.model.PullBrokerIpEvent;
import org.idea.eaglemq.nameserver.store.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author idea
 * @Date: Created in 16:48 2024/6/11
 * @Description
 */
public class PullBrokerIpListener implements Listener<PullBrokerIpEvent> {

    @Override
    public void onReceive(PullBrokerIpEvent event) throws Exception {
        String pullRole = event.getRole();
        PullBrokerIpRespDTO pullBrokerIpRespDTO = new PullBrokerIpRespDTO();
        List<String> addressList = new ArrayList<>();
        List<String> masterAddressList = new ArrayList<>();
        List<String> slaveAddressList = new ArrayList<>();
        Map<String, ServiceInstance> serviceInstanceMap = CommonCache.getServiceInstanceManager().getServiceInstanceMap();
        for (String reqId : serviceInstanceMap.keySet()) {
            ServiceInstance serviceInstance = serviceInstanceMap.get(reqId);
            if(RegistryTypeEnum.BROKER.getCode().equals(serviceInstance.getRegistryType())){
                Map<String,Object> brokerAttrs = serviceInstance.getAttrs();
                String group = (String) brokerAttrs.getOrDefault("group","");
                //先命中集群组，再根据角色进行判断
                if(group.equals(event.getBrokerClusterGroup())) {
                    String role = (String) brokerAttrs.get("role");
                    if (PullBrokerIpRoleEnum.MASTER.getCode().equals(pullRole)
                            && PullBrokerIpRoleEnum.MASTER.getCode().equals(role)) {
                        masterAddressList.add(serviceInstance.getIp()+":"+serviceInstance.getPort());
                    } else if (PullBrokerIpRoleEnum.SLAVE.getCode().equals(pullRole)
                            && PullBrokerIpRoleEnum.SLAVE.getCode().equals(role)) {
                        slaveAddressList.add(serviceInstance.getIp()+":"+serviceInstance.getPort());
                    } else if (PullBrokerIpRoleEnum.SINGLE.getCode().equals(pullRole)
                            && PullBrokerIpRoleEnum.SINGLE.getCode().equals(role)) {
                        addressList.add(serviceInstance.getIp()+":"+serviceInstance.getPort());
                    }
                }
            }
        }
        pullBrokerIpRespDTO.setMsgId(event.getMsgId());
        pullBrokerIpRespDTO.setMasterAddressList(masterAddressList);
        pullBrokerIpRespDTO.setSlaveAddressList(slaveAddressList);
        //防止ip重复
        pullBrokerIpRespDTO.setAddressList(addressList.stream().distinct().collect(Collectors.toList()));
        event.getChannelHandlerContext().writeAndFlush(new TcpMsg(NameServerResponseCode.PULL_BROKER_ADDRESS_SUCCESS.getCode(),
                JSON.toJSONBytes(pullBrokerIpRespDTO)));
    }
}
