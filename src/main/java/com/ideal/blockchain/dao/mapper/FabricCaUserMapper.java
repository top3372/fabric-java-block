package com.ideal.blockchain.dao.mapper;


import com.ideal.blockchain.dao.base.BaseMapper;
import com.ideal.blockchain.dao.model.FabricCaUser;

import java.util.Map;

public interface FabricCaUserMapper extends BaseMapper<FabricCaUser> {

    FabricCaUser selFabricCaUserByNameAndEnrollmentSecret(Map<String,Object> parameters);
}