package com.ideal.blockchain.dao.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "`fabric_ca_user`")
public class FabricCaUser {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`roles`")
    private String roles;

    @Column(name = "`account`")
    private String account;

    @Column(name = "`affiliation`")
    private String affiliation;

    @Column(name = "`organization`")
    private String organization;

    @Column(name = "`enrollmentSecret`")
    private String enrollmentsecret;

    @Column(name = "`enrollment`")
    private String enrollment;

    @Column(name = "`msp_Id`")
    private String mspId;

    @Column(name = "`exp_date`")
    private Date expDate;

    @Column(name = "`create_by`")
    private String createBy;

    @Column(name = "`create_date`")
    private Date createDate;

    @Column(name = "`update_by`")
    private String updateBy;

    @Column(name = "`update_date`")
    private Date updateDate;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return roles
     */
    public String getRoles() {
        return roles;
    }

    /**
     * @param roles
     */
    public void setRoles(String roles) {
        this.roles = roles == null ? null : roles.trim();
    }

    /**
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     */
    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    /**
     * @return affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * @param affiliation
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation == null ? null : affiliation.trim();
    }

    /**
     * @return organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization
     */
    public void setOrganization(String organization) {
        this.organization = organization == null ? null : organization.trim();
    }

    /**
     * @return enrollmentSecret
     */
    public String getEnrollmentsecret() {
        return enrollmentsecret;
    }

    /**
     * @param enrollmentsecret
     */
    public void setEnrollmentsecret(String enrollmentsecret) {
        this.enrollmentsecret = enrollmentsecret == null ? null : enrollmentsecret.trim();
    }

    /**
     * @return enrollment
     */
    public String getEnrollment() {
        return enrollment;
    }

    /**
     * @param enrollment
     */
    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment == null ? null : enrollment.trim();
    }

    /**
     * @return msp_Id
     */
    public String getMspId() {
        return mspId;
    }

    /**
     * @param mspId
     */
    public void setMspId(String mspId) {
        this.mspId = mspId == null ? null : mspId.trim();
    }

    /**
     * @return exp_date
     */
    public Date getExpDate() {
        return expDate;
    }

    /**
     * @param expDate
     */
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    /**
     * @return create_by
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    /**
     * @return create_date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return update_by
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    /**
     * @return update_date
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}