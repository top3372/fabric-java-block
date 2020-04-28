package org.xialing.fabric.model;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.util.Set;

public class FabricUser implements User {

    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;

    public FabricUser(String name, Enrollment enrollment, String mspId) {
        this.name = name;
        this.enrollment = enrollment;
        this.mspId = mspId;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public Set<String> getRoles() {
        return roles;
    }
    @Override
    public String getAccount() {
        return account;
    }
    @Override
    public String getAffiliation() {
        return affiliation;
    }
    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }
    @Override
    public String getMspId() {
        return mspId;
    }
}
