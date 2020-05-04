package com.cosin.servicecomb.provider.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {

  public final String QUOTA_RESERVE_LOCK = "/lock/identity-quota-reserve-lock";
  
  public final String QUOTA_COMFIRM_LOCK = "/lock/identity-quota-confirm-lock";

  // 项目到期
  public final String PROJECT_EXPIRE_LOCK = "/lock/identity-project-expire-lock";

  // 项目过期
  public final String PROJECT_EXCEED_LOCK = "/lock/identity-project-exceed-lock";

  // 项目将要过期
  public final String PROJECT_WILL_EXPIRE_LOCK = "/lock/identity-project-will-expire-lock";
  
  @Value("${spring.etcd.endpoints:http://localhost:2379}")
  public String endpoints;
}
