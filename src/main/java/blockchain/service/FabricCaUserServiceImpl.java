package blockchain.service;

import blockchain.dao.mapper.FabricCaUserMapper;
import blockchain.dao.model.FabricCaUser;
import blockchain.model.HyperUser;
import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Service("fabricCaUserService")
public class FabricCaUserServiceImpl {

    private Logger logger = LoggerFactory.getLogger(FabricCaUserServiceImpl.class);

    @Resource
    private FabricCaUserMapper fabricCaUserMapper;


    public void insertFabricCaUser(HyperUser user,String peerWithOrg){
        try {
            FabricCaUser fabricCaUser = new FabricCaUser();
            fabricCaUser.setAccount(user.getAccount());
            fabricCaUser.setAffiliation(user.getAffiliation());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(user.getEnrollment());
            oos.flush();

            fabricCaUser.setEnrollment(Hex.toHexString(bos.toByteArray()));
            fabricCaUser.setEnrollmentsecret(user.getEnrollmentSecret());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);//增加一年
            fabricCaUser.setExpDate(cal.getTime());
            fabricCaUser.setMspId(user.getMspId());
            fabricCaUser.setName(user.getName());
            fabricCaUser.setOrganization(user.getOrganization());
            fabricCaUser.setRoles(JSONObject.toJSONString(user.getRoles()));
            fabricCaUser.setCreateDate(new Date());
            fabricCaUserMapper.insertSelective(fabricCaUser);


        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public FabricCaUser selFabricCaUserByNameAndEnrollmentSecret(String name,String peerWithOrg,String enrollmentSecret){
        FabricCaUser fabricCaUser = null;
        try {
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("name",name);
            parameters.put("enrollmentSecret",enrollmentSecret);
            parameters.put("organization",peerWithOrg);
            fabricCaUser = fabricCaUserMapper.selFabricCaUserByNameAndEnrollmentSecret(parameters);
        }catch (Exception e){
            logger.error(e.getMessage());

        }
        return fabricCaUser;
    }

    /**
     *
     * @param fabricCaUser
     * @param enrollment
     */
    public void updateFabricCaUserEnrollment(FabricCaUser fabricCaUser,Enrollment enrollment){
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(enrollment);
            oos.flush();

            fabricCaUser.setEnrollment(Hex.toHexString(bos.toByteArray()));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);//增加一年
            fabricCaUser.setExpDate(cal.getTime());
            fabricCaUser.setUpdateDate(new Date());
            fabricCaUserMapper.updateByPrimaryKeySelective(fabricCaUser);


        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    /**
     * 对象转换
     * @param fabricCaUser
     * @return
     */
    public HyperUser transferFabricCaUserToHyperUser(FabricCaUser fabricCaUser){
        HyperUser user = null;
        try {
            if(fabricCaUser != null){
                user = new HyperUser(fabricCaUser.getName(),fabricCaUser.getOrganization());

                byte[] serialized = Hex.decode(fabricCaUser.getEnrollment());
                ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
                ObjectInputStream ois = new ObjectInputStream(bis);
                Enrollment enrollment = (Enrollment) ois.readObject();
                user.setEnrollment(enrollment);
                user.setAccount(fabricCaUser.getAccount());
                user.setAffiliation(fabricCaUser.getAffiliation());
                user.setEnrollmentSecret(fabricCaUser.getEnrollmentsecret());
                user.setMspId(fabricCaUser.getMspId());
                user.setRoles(JSONObject.parseObject(fabricCaUser.getRoles(), Set.class));
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return user;
    }

    /**
     * 判断是否证书到期
     * @param fabricCaUser
     * @return
     */
    public boolean isExpDate(FabricCaUser fabricCaUser){
        Calendar cal = Calendar.getInstance();

        Calendar expDate = Calendar.getInstance();
        expDate.setTime(fabricCaUser.getExpDate());
        if(cal.compareTo(expDate) == -1){
            return true;
        }else{
            return false;
        }
    }
}
