package com.jfnice.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDepositReturn<M extends BaseDepositReturn<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return getLong("id");
	}

	public void setDel(Boolean del) {
		set("del", del);
	}

	public Boolean getDel() {
		return get("del");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setCreateUserCode(String createUserCode) {
		set("create_user_code", createUserCode);
	}

	public String getCreateUserCode() {
		return getStr("create_user_code");
	}

	public void setCreateUserName(String createUserName) {
		set("create_user_name", createUserName);
	}

	public String getCreateUserName() {
		return getStr("create_user_name");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	public void setUpdateUserCode(String updateUserCode) {
		set("update_user_code", updateUserCode);
	}

	public String getUpdateUserCode() {
		return getStr("update_user_code");
	}

	public void setSchoolCode(String schoolCode) {
		set("school_code", schoolCode);
	}

	public String getSchoolCode() {
		return getStr("school_code");
	}

	public void setGrdCode(String grdCode) {
		set("grd_code", grdCode);
	}

	public String getGrdCode() {
		return getStr("grd_code");
	}

	public void setGrdName(String grdName) {
		set("grd_name", grdName);
	}

	public String getGrdName() {
		return getStr("grd_name");
	}

	public void setClsCode(String clsCode) {
		set("cls_code", clsCode);
	}

	public String getClsCode() {
		return getStr("cls_code");
	}

	public void setClsName(String clsName) {
		set("cls_name", clsName);
	}

	public String getClsName() {
		return getStr("cls_name");
	}

	public void setStuCode(String stuCode) {
		set("stu_code", stuCode);
	}

	public String getStuCode() {
		return getStr("stu_code");
	}

	public void setSno(String sno) {
		set("sno", sno);
	}

	public String getSno() {
		return getStr("sno");
	}

	public void setDptName(String dptName) {
		set("dpt_name", dptName);
	}

	public String getDptName() {
		return getStr("dpt_name");
	}

	public void setUserName(String userName) {
		set("user_name", userName);
	}

	public String getUserName() {
		return getStr("user_name");
	}

	public void setCardNo(String cardNo) {
		set("card_no", cardNo);
	}

	public String getCardNo() {
		return getStr("card_no");
	}

	public void setReturnAmount(Integer returnAmount) {
		set("return_amount", returnAmount);
	}

	public Integer getReturnAmount() {
		return getInt("return_amount");
	}

	public void setReturnTime(java.util.Date returnTime) {
		set("return_time", returnTime);
	}

	public java.util.Date getReturnTime() {
		return get("return_time");
	}

	public void setUserCode(String userCode) {
		set("user_code", userCode);
	}

	public String getUserCode() {
		return getStr("user_code");
	}

	public void setDptCode(String dptCode) {
		set("dpt_code", dptCode);
	}

	public String getDptCode() {
		return getStr("dpt_code");
	}

}
