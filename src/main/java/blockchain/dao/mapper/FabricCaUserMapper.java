package blockchain.dao.mapper;


import blockchain.dao.base.BaseMapper;
import blockchain.dao.model.FabricCaUser;

import java.util.Map;

public interface FabricCaUserMapper extends BaseMapper<FabricCaUser> {

    FabricCaUser selFabricCaUserByNameAndEnrollmentSecret(Map<String,Object> parameters);
}