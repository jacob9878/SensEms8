package com.imoxion.sensems.server.repository.mapper;

import com.imoxion.sensems.server.domain.SmtpAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmtpAccountMapper {
    public List<SmtpAccount> getSmtpAccountList();
    public SmtpAccount getSmtpAccount(@Param("userid") String userid);
}
