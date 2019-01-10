package com.ideal.blockchain.service.block;

import com.ideal.blockchain.config.ConnectionUtil;
import com.ideal.blockchain.config.HyperledgerConfiguration;
import com.ideal.blockchain.dao.model.FabricCaUser;
import com.ideal.blockchain.model.HyperUser;
import com.ideal.blockchain.model.Org;
import com.ideal.blockchain.model.SampleStoreEnrollement;
import com.ideal.blockchain.model.Utils;
import com.ideal.blockchain.service.FabricCaUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.PrivateKey;

import static java.lang.String.format;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:00
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private FabricCaUserServiceImpl fabricCaUserService;

    @Autowired
    private HyperledgerConfiguration hyperledgerConfiguration;


    public synchronized String register(String name, String password, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        HFCAClient ca = sampleOrg.getCAClient();

        String orgName = sampleOrg.getName();
        String msPid = sampleOrg.getMSPID();
        String sampleOrgDomainName = sampleOrg.getDomainName();
        ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        //just check if we connect at all.
        HFCAInfo info = ca.info();

        String infoName = info.getCAName();
        log.info("CAName: " + infoName);
        if (infoName != null && !infoName.isEmpty()) {
            //返回错误信息

        }
        FabricCaUser adminFabricCaUser = fabricCaUserService.selFabricCaUserByNameAndEnrollmentSecret(hyperledgerConfiguration.getAdminName(),orgName,hyperledgerConfiguration.getAdminPwd());
        HyperUser admin = null;
        if(adminFabricCaUser == null) {


            admin = new HyperUser(hyperledgerConfiguration.getAdminName(), orgName);

            admin.setEnrollmentSecret(hyperledgerConfiguration.getAdminPwd());
            admin.setEnrollment(ca.enroll(admin.getName(), hyperledgerConfiguration.getAdminPwd()));
            admin.setMspId(msPid);

            fabricCaUserService.insertFabricCaUser(admin,peerWithOrg);

        }else{
            admin = fabricCaUserService.transferFabricCaUserToHyperUser(adminFabricCaUser);
        }

        FabricCaUser peerOrgAdminFabricCaUser = fabricCaUserService.selFabricCaUserByNameAndEnrollmentSecret(orgName + "Admin",orgName,"");
        if( peerOrgAdminFabricCaUser == null) {
            HyperUser peerOrgAdmin = new HyperUser(orgName + "Admin", orgName);
            peerOrgAdmin.setMspId(msPid);

            File certificateFile = Paths.get(HyperledgerConfiguration.config.getChannelPath(), "crypto-config/peerOrganizations/",
                    sampleOrgDomainName, format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
                            sampleOrgDomainName, sampleOrgDomainName))
                    .toFile();
            File privateKeyFile = ConnectionUtil.findFileSk(Paths.get(HyperledgerConfiguration.config.getChannelPath(),
                    "crypto-config/peerOrganizations/", sampleOrgDomainName,
                    format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile());

            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");

            PrivateKey privateKey = Utils.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
            peerOrgAdmin.setEnrollment(new SampleStoreEnrollement(privateKey, certificate));

            fabricCaUserService.insertFabricCaUser(peerOrgAdmin,peerWithOrg);
        }

        FabricCaUser userFabricCaUser = fabricCaUserService.selFabricCaUserByNameAndEnrollmentSecret(name,orgName,password);
        if(userFabricCaUser == null) {
            HyperUser user = new HyperUser(name, orgName);

            RegistrationRequest rr = new RegistrationRequest(user.getName());
            rr.setMaxEnrollments(1);

            /**自定义的用户密码 rr.setSecret(password);**/

            try {
                String secret = ca.register(rr, admin);
                log.info("****userName: " + user.getName() + ",password: " + password + ",secret: " + secret);
                user.setEnrollmentSecret(secret);

                Enrollment enrollment = ca.enroll(user.getName(), secret);
                user.setEnrollment(enrollment);
                user.setMspId(msPid);


                fabricCaUserService.insertFabricCaUser(user, peerWithOrg);


            } catch (RegistrationException re) {
                re.printStackTrace();
                log.error(re.getMessage());
                throw re;
            }
        }
        return "User " + name + " enroll Successfully";
    }


    public String loadUserFromPersistence(String name, String password, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);


        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);

        //如果出现高并发情况,CA服务会出现网络积压,造成服务调用失败...所以目前暂时 不reenroll用户的新证书
//        HFCAClient ca = sampleOrg.getCAClient();
        String orgName = sampleOrg.getName();
        String msPid = sampleOrg.getMSPID();
//        ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

//        HFCAInfo info = ca.info(); //just check if we connect at all.
//        String infoName = info.getCAName();
//        log.info("CAName: " + infoName);
//        if (infoName != null && !infoName.isEmpty()) {
//            //返回错误信息
//
//        }
        FabricCaUser adminFabricCaUser = fabricCaUserService.selFabricCaUserByNameAndEnrollmentSecret(hyperledgerConfiguration.getAdminName(),orgName,hyperledgerConfiguration.getAdminPwd());

        if(adminFabricCaUser != null) {

            HyperUser admin = fabricCaUserService.transferFabricCaUserToHyperUser(adminFabricCaUser);
//            if(!fabricCaUserService.isExpDate(adminFabricCaUser)) {
//
//                Enrollment enrollment = ca.reenroll(admin);
//                admin.setEnrollment(enrollment);
//
//                fabricCaUserService.updateFabricCaUserEnrollment(adminFabricCaUser,enrollment);
//            }
            sampleOrg.setAdmin(admin); // The admin of this org.
        }else{
            throw new Exception(hyperledgerConfiguration.getAdminName() + " User is Not exist");
        }
        FabricCaUser peerOrgAdminFabricCaUser = fabricCaUserService.selFabricCaUserByNameAndEnrollmentSecret(orgName + "Admin",orgName,"");
        if( peerOrgAdminFabricCaUser != null) {

            HyperUser peerOrgAdmin = fabricCaUserService.transferFabricCaUserToHyperUser(peerOrgAdminFabricCaUser);
//            if(!fabricCaUserService.isExpDate(peerOrgAdminFabricCaUser)) {
//
//                Enrollment enrollment = ca.reenroll(peerOrgAdmin);
//                peerOrgAdmin.setEnrollment(enrollment);
//
//                fabricCaUserService.updateFabricCaUserEnrollment(peerOrgAdminFabricCaUser,enrollment);
//            }
            sampleOrg.setPeerAdmin(peerOrgAdmin);
        }else{
            throw new Exception( orgName + "Admin" + " User is Not exist");
        }
        FabricCaUser userFabricCaUser = fabricCaUserService.selFabricCaUserByNameAndEnrollmentSecret(name,orgName,password);
        if(userFabricCaUser != null) {
            HyperUser user = fabricCaUserService.transferFabricCaUserToHyperUser(userFabricCaUser);
//            if(!fabricCaUserService.isExpDate(userFabricCaUser)) {
//
//                Enrollment enrollment = ca.reenroll(user);
//                user.setEnrollment(enrollment);
//
//                fabricCaUserService.updateFabricCaUserEnrollment(userFabricCaUser,enrollment);
//            }
            sampleOrg.addUser(user); // Remember user belongs to this Org
        }else{
            throw new Exception( name + " User is Not exist");
        }

        return "Successfully loaded member from persistence";
    }
}
