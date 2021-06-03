package com.school.library.userinfo;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.model.BookStorage;
import com.jfnice.model.UserInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserInfoService extends JFniceBaseService<UserInfo> {

	public boolean save(UserInfo userinfo) {
		boolean flag = super.save(userinfo);
		UserInfoIdMap.me.clear();
		return flag;
	}

	public boolean update(UserInfo userinfo) {
		boolean flag = super.update(userinfo);
		UserInfoIdMap.me.clear();
		return flag;
	}

	public boolean deleteById(long userinfoId, boolean isRealDelete) {
		boolean flag = super.deleteById(userinfoId, isRealDelete);
		UserInfoIdMap.me.clear();
		return flag;
	}

	public void batchSave(List<UserInfo> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchSave(list, Math.min(list.size(), maxBatchSize));
			UserInfoIdMap.me.clear();
		}
	}

	public void batchUpdate(List<UserInfo> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			Db.batchUpdate(list, Math.min(list.size(), maxBatchSize));
			UserInfoIdMap.me.clear();
		}
	}

	/**
	 * 通过班级查询学生用户信息
	 * @param schoolCode
	 * @param clsCodes
	 * @return
	 */
	public List<UserInfo> queryByClsCodes(String schoolCode, String clsCodes){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("cls_codes", clsCodes);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryByClsCodes", condPara);
		return UserInfo.dao.find(sqlPara);
	}

	/**
	 * 删除不在班级里面的学生用户
	 * @param schoolCode
	 * @param updateUserCode
	 * @param updateTime
	 * @param clsCodes
	 * @return
	 */
	public int deleteNotClsCodes(String schoolCode, String updateUserCode, Date updateTime, String clsCodes){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("update_user_code", updateUserCode);
		condPara.put("update_time", updateTime);
		condPara.put("cls_codes", clsCodes);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.deleteNotClsCodes", condPara);
		return Db.update(sqlPara);
	}

	/**
	 * 通过用户类型查询用户信息
	 * @param schoolCode
	 * @return
	 */
	public List<UserInfo> queryByType(String schoolCode, String userType){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("user_type", userType);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryByType", condPara);
		return UserInfo.dao.find(sqlPara);
	}

	/**
	 * 查询某个类型的用户是否存在
	 * @param schoolCode
	 * @return
	 */
	public boolean existsByType(String schoolCode, String userType){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("user_type", userType);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryByType", condPara);
		UserInfo userInfo = UserInfo.dao.findFirst(sqlPara);
		return null != userInfo;
	}

	/**
	 * 分页查询首页学生用户记录
	 * @return
	 */
	public Page<UserInfo> queryStuIndexList(CondPara condPara){
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryStuIndexList", condPara);
		return UserInfo.dao.paginate(condPara.getInt("page_number"), condPara.getInt("page_size"), sqlPara);
	}

	/**
	 * 分页查询首页教师用户记录
	 * @return
	 */
	public Page<UserInfo> queryTeacherIndexList(CondPara condPara){
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryTeacherIndexList", condPara);
		return UserInfo.dao.paginate(condPara.getInt("page_number"), condPara.getInt("page_size"), sqlPara);
	}

	/**
	 *查询用户信息
	 * @param schoolCode
	 * @param grdCode
	 * @param clsCode
	 * @param stuCode
	 * @param dptCode
	 * @param userCode
	 * @return
	 */
	public List<UserInfo> queryUser(String schoolCode, String grdCode, String clsCode, String stuCode,
									String dptCode, String userCode){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("grd_code", grdCode);
		condPara.put("cls_code", clsCode);
		condPara.put("stu_code", stuCode);
		condPara.put("dpt_code", dptCode);
		condPara.put("user_code", userCode);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryUser", condPara);
		return UserInfo.dao.find(sqlPara);
	}

	/**
	 * 通过id查询用户
	 * @param schoolCode
	 * @param id
	 * @return
	 */
	public UserInfo queryById(String schoolCode, Long id){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.queryById", condPara);
		return UserInfo.dao.findFirst(sqlPara);
	}

	/**
	 * 更新押金
	 * @param schoolCode
	 * @param id
	 * @param version
	 * @param deposit
	 * @param updateUserCode
	 * @param updateTime
	 * @return
	 */
	public int updateDeposit(String schoolCode, Long id, Long version, int deposit, String updateUserCode, Date updateTime){
		CondPara condPara = new CondPara();
		condPara.put("school_code", schoolCode);
		condPara.put("id", id);
		condPara.put("version", version);
		condPara.put("deposit", deposit);
		condPara.put("update_user_code", updateUserCode);
		condPara.put("update_time", updateTime);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.updateDeposit", condPara);
		return Db.update(sqlPara);
	}

}