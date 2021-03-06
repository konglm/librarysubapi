package com.jfnice.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserInfoHis<M extends BaseUserInfoHis<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setDel(java.lang.Boolean del) {
		set("del", del);
	}
	
	public java.lang.Boolean getDel() {
		return get("del");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}
	
	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setCreateUserCode(java.lang.String createUserCode) {
		set("create_user_code", createUserCode);
	}
	
	public java.lang.String getCreateUserCode() {
		return getStr("create_user_code");
	}

	public void setCreateUserName(java.lang.String createUserName) {
		set("create_user_name", createUserName);
	}
	
	public java.lang.String getCreateUserName() {
		return getStr("create_user_name");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}
	
	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	public void setUpdateUserCode(java.lang.String updateUserCode) {
		set("update_user_code", updateUserCode);
	}
	
	public java.lang.String getUpdateUserCode() {
		return getStr("update_user_code");
	}

	public void setSchoolCode(java.lang.String schoolCode) {
		set("school_code", schoolCode);
	}
	
	public java.lang.String getSchoolCode() {
		return getStr("school_code");
	}

	public void setGrdCode(java.lang.String grdCode) {
		set("grd_code", grdCode);
	}
	
	public java.lang.String getGrdCode() {
		return getStr("grd_code");
	}

	public void setGrdName(java.lang.String grdName) {
		set("grd_name", grdName);
	}
	
	public java.lang.String getGrdName() {
		return getStr("grd_name");
	}

	public void setClsCode(java.lang.String clsCode) {
		set("cls_code", clsCode);
	}
	
	public java.lang.String getClsCode() {
		return getStr("cls_code");
	}

	public void setClsName(java.lang.String clsName) {
		set("cls_name", clsName);
	}
	
	public java.lang.String getClsName() {
		return getStr("cls_name");
	}

	public void setStuCode(java.lang.String stuCode) {
		set("stu_code", stuCode);
	}
	
	public java.lang.String getStuCode() {
		return getStr("stu_code");
	}

	public void setSno(java.lang.String sno) {
		set("sno", sno);
	}
	
	public java.lang.String getSno() {
		return getStr("sno");
	}

	public void setStuName(java.lang.String stuName) {
		set("stu_name", stuName);
	}
	
	public java.lang.String getStuName() {
		return getStr("stu_name");
	}

	public void setDptCode(java.lang.String dptCode) {
		set("dpt_code", dptCode);
	}
	
	public java.lang.String getDptCode() {
		return getStr("dpt_code");
	}

	public void setDptName(java.lang.String dptName) {
		set("dpt_name", dptName);
	}
	
	public java.lang.String getDptName() {
		return getStr("dpt_name");
	}

	public void setUserCode(java.lang.String userCode) {
		set("user_code", userCode);
	}
	
	public java.lang.String getUserCode() {
		return getStr("user_code");
	}

	public void setUserName(java.lang.String userName) {
		set("user_name", userName);
	}
	
	public java.lang.String getUserName() {
		return getStr("user_name");
	}

	public void setSex(java.lang.Short sex) {
		set("sex", sex);
	}
	
	public java.lang.Short getSex() {
		return getShort("sex");
	}

	public void setCardNo(java.lang.String cardNo) {
		set("card_no", cardNo);
	}
	
	public java.lang.String getCardNo() {
		return getStr("card_no");
	}

	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}
	
	public java.lang.String getMobile() {
		return getStr("mobile");
	}

	public void setDeposit(java.lang.Integer deposit) {
		set("deposit", deposit);
	}
	
	public java.lang.Integer getDeposit() {
		return getInt("deposit");
	}

	public void setImgUrl(java.lang.String imgUrl) {
		set("img_url", imgUrl);
	}
	
	public java.lang.String getImgUrl() {
		return getStr("img_url");
	}

	public void setVersion(java.lang.Long version) {
		set("version", version);
	}
	
	public java.lang.Long getVersion() {
		return getLong("version");
	}

	public void setUserType(java.lang.String userType) {
		set("user_type", userType);
	}
	
	public java.lang.String getUserType() {
		return getStr("user_type");
	}

	public void setCanBorrow(java.lang.Short canBorrow) {
		set("can_borrow", canBorrow);
	}
	
	public java.lang.Short getCanBorrow() {
		return getShort("can_borrow");
	}

}
