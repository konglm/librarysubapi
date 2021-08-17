package com.school.library.userinfo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.*;
import com.jfinal.aop.Inject;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfnice.admin.dict.DictKit;
import com.jfnice.commons.CacheName;
import com.jfnice.enums.FinishEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.jfnice.cache.JsyCacheKit;
import com.jfnice.model.*;
import com.school.api.gx.RsApi;
import com.school.api.model.Cls;
import com.school.api.model.Dpt;
import com.school.api.model.JsyUser;
import com.school.api.model.Stu;
import com.school.library.bookbarcode.BookBarCodeStatusEnum;
import com.school.library.bookdamaged.JudgeStatusEnum;
import com.school.library.borrowbook.BorrowBookLogic;
import com.school.library.borrowbook.ReturnStatusEnum;
import com.school.library.borrowsetting.BorrowSettingLogic;
import com.school.library.constants.DictConstants;
import com.school.library.constants.RedisConstants;
import com.school.library.depositreturn.DepositReturnService;
import com.school.library.kit.CommonKit;

import java.util.*;
import java.util.stream.Collectors;

public class UserInfoLogic {

	@Inject
	private UserInfoService service;
	@Inject
	private BorrowBookLogic borrowBookLogic;
	@Inject
	private BorrowSettingLogic settingLogic;
	@Inject
	private DepositReturnService returnService;

	/**
	 * 分页查询首页学生记录
	 * @return
	 */
	public Page<UserInfo> queryIndexPageStu(CondPara condPara){
		condPara.put("school_code", CurrentUser.getSchoolCode());
		condPara.put("user_type", UserTypeEnum.STUDENT.getUserType());
		condPara.put("return_status", 0);
		Page<UserInfo> page = this.service.queryStuIndexList(condPara);
		return page;
	}

	/**
	 * 分页查询首页教师记录
	 * @return
	 */
	public Page<UserInfo> queryIndexPageTeacher(CondPara condPara){
		condPara.put("school_code", CurrentUser.getSchoolCode());
		condPara.put("user_type", UserTypeEnum.TEACHER.getUserType());
		condPara.put("return_status", 0);
		Page<UserInfo> page = this.service.queryTeacherIndexList(condPara);
		return page;
	}

	/**
	 * 同步学生信息
	 */
	public void synchronizeStuInfo(){
		//从redis取值，判断是否有人同时在同步
		String redisKey = RedisConstants.SYNCHRONIZE_STU_KEY_PREFIX + CurrentUser.getSchoolCode();
		Integer syncStuFlag = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, redisKey);
		if(null!= syncStuFlag && syncStuFlag.intValue() >= 1){
			throw new ErrorMsg("其他账号正在进行同步操作，不能同时进行同步");
		}
		JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, 1, RedisConstants.TIME_TO_LIVE_SECONDS);
		try{
			//查询人事学生数据
			//1.查出所有班级
			List<Cls> clsList = RsApi.getClsList(FinishEnum.ALL.getK());
			int clsLen = clsList.size();
			List<String> clsCodeList = clsList.stream().map(c -> c.getClsCode()).collect(Collectors.toList());
			String allClsCodes = clsList.stream().map(c -> c.getClsCode()).collect(Collectors.joining(","));
			//分两次去取班级学生数据
			int size = 2;
			Integer [][] count = new Integer[2][];
			count[0] = new Integer[]{0, clsLen/size};
			count[1] = new Integer[]{clsLen/size, clsLen};
			//对人事和数据库查询出来的数据进行比对，分为增加列表、修改列表、删除列表
			List<UserInfo> addList = new ArrayList<>();
			List<UserInfo> updateList = new ArrayList<>();
			List<UserInfo> deleteList = new ArrayList<>();
			Db.tx(() -> {
				for(int i = 0; i < count.length; i++){
					String clsCodes = clsCodeList.subList(count[i][0], count[i][1]).stream().map(c -> c).collect(Collectors.joining(","));
					//人事的学生数据
					List<Stu> stuList = RsApi.getStuList(clsCodes);
					//人事数据组装成Map
					Multimap<String, Stu> stuMap = Multimaps.index(stuList, new Function<Stu, String>() {
						@Override
						public String apply(Stu stu) {
							return stu.getStuCode();
						}
					});
					//从数据库里查询班级学生数据
					List<UserInfo> userInfoList = this.service.queryByClsCodes(CurrentUser.getSchoolCode(), clsCodes);
					//数据库数据组装成Map
					Multimap<String, UserInfo> userMap= Multimaps.index(userInfoList, new Function<UserInfo, String>() {
						@Override
						public String apply(UserInfo userInfo) {
							return userInfo.getStuCode();
						}
					});
					//对人事和数据库查询出来的数据进行比对，分为增加列表、修改列表、删除列表
					//1.差集-增加列表Key
					Set<String> addKeySet = new HashSet<>(stuMap.keySet());

					addKeySet.removeAll(userMap.keySet());
					addKeySet.forEach(k -> {
						if(!stuMap.get(k).isEmpty()){
							Stu stu = stuMap.get(k).stream().findFirst().get();
							UserInfo user = new UserInfo();
							user.setCardNo(null);
							user.setClsCode(stu.getClsCode());
							user.setClsName(stu.getClsName());
							user.setDeposit(0);
							user.setGrdCode(stu.getGrdCode());
							user.setGrdName(stu.getGrdName());
							user.setImgUrl(stu.getImgUrl());
							user.setMobile(null);
							user.setSchoolCode(CurrentUser.getSchoolCode());
							user.setSex(stu.getSex());
							user.setSno(stu.getSno());
							user.setStuCode(stu.getStuCode());
							user.setStuName(stu.getStuName());
							user.setVersion(1L);
							user.setUserType(UserTypeEnum.STUDENT.getUserType());
							user.setCreateTime(new Date());
							user.setCreateUserCode(CurrentUser.getUserCode());
							user.setCreateUserName(CurrentUser.getUserName());
							user.setDel(false);
							user.setUpdateTime(new Date());
							user.setUpdateUserCode(CurrentUser.getUserCode());
							addList.add(user);
						}
					});
					//2.交集-可能修改列表key（还要对比value是否一致）
					Set<String> updateKeySet = new HashSet<>(stuMap.keys());
					updateKeySet.retainAll(userMap.keySet());
					updateKeySet.forEach(k -> {
						if(!stuMap.get(k).isEmpty()){
							Stu stu = stuMap.get(k).stream().findFirst().get();
							userMap.get(k).stream().forEach(u -> {
								//比较信息，如果有改变则更新
								if(!Objects.equals(u.getGrdCode(), stu.getGrdCode()) ||!Objects.equals(u.getGrdName(),stu.getGrdName()) ||
										!Objects.equals(u.getClsCode(),stu.getClsCode()) || !Objects.equals(u.getClsName(),stu.getClsName()) ||
										!Objects.equals(u.getStuName(),stu.getStuName()) || !Objects.equals(u.getSno(),stu.getSno()) ||
										!Objects.equals(u.getImgUrl(),stu.getImgUrl()) || !Objects.equals(u.getSex(), stu.getSex()) ){ // 卡号、手机号也需要比较
									u.setCardNo(null);
									u.setClsCode(stu.getClsCode());
									u.setClsName(stu.getClsName());
									u.setGrdCode(stu.getGrdCode());
									u.setGrdName(stu.getGrdName());
									u.setImgUrl(stu.getImgUrl());
									u.setMobile(null);
									u.setSex(stu.getSex());
									u.setSno(stu.getSno());
									u.setStuCode(stu.getStuCode());
									u.setStuName(stu.getStuName());
									updateList.add(u);
								}
							});
						}
					});
					//3.userInfo对应的差集-删除列表key
					Set<String> deleteKeySet = new HashSet<>(userMap.keys());
					deleteKeySet.removeAll(stuMap.keySet());
					deleteKeySet.forEach(k -> {
						userMap.get(k).stream().forEach(u -> {
							u.setUpdateUserCode(CurrentUser.getUserCode());
							u.setUpdateTime(new Date());
							u.setDel(false);
							deleteList.add(u);
						});
					});
				}
				if(!addList.isEmpty()){
					this.service.batchSave(addList);
				}
				if(!updateList.isEmpty()){
					this.service.batchUpdate(updateList);
				}
				if(!deleteList.isEmpty()){
					this.service.batchUpdate(deleteList);
				}
				if(StrKit.notBlank(allClsCodes)){
					this.service.deleteNotClsCodes(CurrentUser.getSchoolCode(), CurrentUser.getUserCode(), new Date(), allClsCodes);
				}

				return true;
			});
		}catch(Exception e){
			e.printStackTrace();
			throw new ErrorMsg("同步出错，请稍后重试");
		}finally {
			JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, 0, RedisConstants.TIME_TO_LIVE_SECONDS);
		}
	}

	/**
	 * 同步教师信息
	 */
	public void synchronizeTeacherInfo(){
		//从redis取值，判断是否有人同时在同步
		String redisKey = RedisConstants.SYNCHRONIZE_TEACHER_KEY_PREFIX + CurrentUser.getSchoolCode();
		Integer syncStuFlag = JsyCacheKit.get(CacheName.DEFAULT_SUB_NAME, redisKey);
		if(null!= syncStuFlag && syncStuFlag.intValue() >= 1){
			throw new ErrorMsg("其他账号正在进行同步操作，不能同时进行同步");
		}
		JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, 1, RedisConstants.TIME_TO_LIVE_SECONDS);
		try{
			//1.查出所有部门
			List<Dpt> dptList = RsApi.getDptList();
			String allDptCodes = dptList.stream().map(d -> d.getDptCode()).collect(Collectors.joining(","));
			//查询人事教师数据
			//对人事和数据库查询出来的数据进行比对，分为增加列表、修改列表、删除列表
			List<UserInfo> addList = new ArrayList<>();
			List<UserInfo> updateList = new ArrayList<>();
			List<UserInfo> deleteList = new ArrayList<>();
			Db.tx(() -> {
				//人事的教师数据
				List<JsyUser> teacherList = RsApi.getJsyUserList("", -1);
				//人事数据组装成Map,一个人可以对应多个部门，key使用userCode来去重
				Multimap<String, JsyUser> jsyUserMap = Multimaps.index(teacherList, new Function<JsyUser, String>() {
					@Override
					public String apply(JsyUser user) {
						return user.getUserCode();
					}
				});
				//从数据库里查询教师数据
				List<UserInfo> userInfoList = this.service.queryByType(CurrentUser.getSchoolCode(), UserTypeEnum.TEACHER.getUserType());
				//数据库数据组装成Map
				Multimap<String, UserInfo> userMap= Multimaps.index(userInfoList, new Function<UserInfo, String>() {
					@Override
					public String apply(UserInfo userInfo) {
						return userInfo.getUserCode();
					}
				});
				//对人事和数据库查询出来的数据进行比对，分为增加列表、修改列表、删除列表
				//1.差集-增加列表Key
				Set<String> addKeySet = new HashSet<>(jsyUserMap.keySet());
				Set<String> userKeySet = new HashSet<>(userMap.keySet());

				addKeySet.removeAll(userMap.keySet());
				addKeySet.forEach(k -> {
					if(!jsyUserMap.get(k).isEmpty()){
						JsyUser teacher = jsyUserMap.get(k).stream().findFirst().get();
						UserInfo user = new UserInfo();
						user.setCardNo(null);
						user.setDeposit(0);
						user.setImgUrl(teacher.getImgUrl());
						user.setMobile(teacher.getPhone());
						user.setSex(teacher.getSex());
						user.setVersion(1L);
						user.setDptCode(teacher.getDptCode());
						user.setDptName(teacher.getDptName());
						user.setUserCode(teacher.getUserCode());
						user.setUserName(teacher.getUserName());
						user.setUserType(UserTypeEnum.TEACHER.getUserType());
						user.setSchoolCode(CurrentUser.getSchoolCode());
						user.setCreateTime(new Date());
						user.setCreateUserCode(CurrentUser.getUserCode());
						user.setCreateUserName(CurrentUser.getUserName());
						user.setDel(false);
						user.setUpdateTime(new Date());
						user.setUpdateUserCode(CurrentUser.getUserCode());
						addList.add(user);
					}
				});
				//2.交集-可能修改列表key（还要对比value是否一致）
				Set<String> updateKeySet = new HashSet<>(jsyUserMap.keys());
				updateKeySet.retainAll(userMap.keySet());
				updateKeySet.forEach(k -> {
					if(!jsyUserMap.get(k).isEmpty()){
						JsyUser teacher = jsyUserMap.get(k).stream().findFirst().get();
						userMap.get(k).stream().forEach(u -> {
							//比较信息，如果有改变则更新
							if(!Objects.equals(u.getDptCode(), teacher.getDptCode()) ||!Objects.equals(u.getDptName(),teacher.getDptName()) ||
									!Objects.equals(u.getUserCode(),teacher.getUserCode()) || !Objects.equals(u.getUserName(),teacher.getUserName()) ||
									!Objects.equals(u.getMobile(),teacher.getPhone()) || !Objects.equals(u.getSex(),teacher.getSex()) ||
									!Objects.equals(u.getImgUrl(),teacher.getImgUrl())){ // 卡号也需要比较
								u.setCardNo(null);
								u.setImgUrl(teacher.getImgUrl());
								u.setMobile(teacher.getPhone());
								u.setSex(teacher.getSex());
								u.setDptCode(teacher.getDptCode());
								u.setDptName(teacher.getDptName());
								u.setUserCode(teacher.getUserCode());
								u.setUserName(teacher.getUserName());
								updateList.add(u);
							}
						});
					}
				});
				//3.userInfo对应的差集-删除列表key
				Set<String> deleteKeySet = new HashSet<>(userMap.keys());
				deleteKeySet.removeAll(jsyUserMap.keySet());
				deleteKeySet.forEach(k -> {
					userMap.get(k).stream().forEach(u -> {
						u.setUpdateUserCode(CurrentUser.getUserCode());
						u.setUpdateTime(new Date());
						u.setDel(true);
						deleteList.add(u);
					});
				});
				if(!addList.isEmpty()){
					this.service.batchSave(addList);
				}
				if(!updateList.isEmpty()){
					this.service.batchUpdate(updateList);
				}
				if(!deleteList.isEmpty()){
					this.service.batchUpdate(deleteList);
				}

				return true;
			});
		}catch(Exception e){
			e.printStackTrace();
			throw new ErrorMsg("同步出错，请稍后重试");
		}finally {
			JsyCacheKit.put(CacheName.DEFAULT_SUB_NAME, redisKey, 0, RedisConstants.TIME_TO_LIVE_SECONDS);
		}
	}

	/**
	 * 获取用户详细信息（包括借出列表）
	 * @param id
	 * @param pageNumber
	 * @param pageSize
	 */
	public JSONObject getDetailById(Long id, int pageNumber, int pageSize){

		//1.查询用户信息
		UserInfo user = this.service.queryById(CurrentUser.getSchoolCode(), id);
		if(null == user){
			throw new ErrorMsg("用户不存在");
		}
		user.set("deposit", CommonKit.formatMoney(user.getDeposit()));
		//查询借出设置
		BorrowSetting borrowSetting = this.settingLogic.queryIfNotNewBySchool(CurrentUser.getSchoolCode());
		long sysBorrowDays = borrowSetting.getBorrowDays();
		//2.查询借阅书籍
		List<BorrowBook> borrowList = this.borrowBookLogic.queryUnReturnByUser(user.getUserCode(), user.getStuCode());
		borrowList.stream().forEach(b -> {
			long borrowDays = CommonKit.differDays(b.getBorrowTime(), new Date());
			b.put("borrow_days", borrowDays);
			long beyondDays = 0;
			if(borrowDays > sysBorrowDays){
				beyondDays = borrowDays - sysBorrowDays;
			}
			b.put("over_days", beyondDays);
			b.put("borrow_time", DateKit.toStr(b.getBorrowTime(), "yyyy-MM-dd"));
		});
		//3.查询借阅历史
		Page<BorrowBook> page = this.borrowBookLogic.pageReturnByUser(user.getUserCode(), user.getStuCode(), pageNumber, pageSize);
		page.getList().stream().forEach(b -> {
			long borrowDays = CommonKit.differDays(b.getBorrowTime(), new Date());
			b.put("borrow_days", borrowDays);
			String return_status_text = "";
			//已归还，且不是馆藏，则用书本状态来表示归还状态
			if(null != b.getBookStatus() && ReturnStatusEnum.RETURN.getStatus() == b.getReturnStatus() &&
					BookBarCodeStatusEnum.STORAGE.getStatus() != b.getBookStatus()){
				return_status_text = DictKit.text(DictConstants.BOOK_STATUS_TAG, b.getBookStatus());
			}else{
				return_status_text = DictKit.text(DictConstants.RETURN_STATUS_TAG, b.getReturnStatus());
			}
			b.put("return_status_text", return_status_text);
			//审核状态
			Integer judge = b.getInt("judge");
			String deduction = CommonKit.formatMoney(b.getDeductions());
			//需要审核，但是未审核
			if(null != judge && JudgeStatusEnum.UNAUDIT.getStatus() == judge.intValue()){
				deduction = "";

			}
			b.put("deductions", deduction);
			b.put("borrow_time", DateKit.toStr(b.getBorrowTime(), "yyyy-MM-dd"));
		});

		JSONObject data = new JSONObject();
		data.put("user", user);
		data.put("borrow_list", borrowList);
		data.put("history", page);

		return data;
	}

	/**
	 * 退还押金
	 * @param id
	 * @return
	 */
	public boolean returnDeposit(Long id){
		//1.查询用户信息
		UserInfo user = this.service.queryById(CurrentUser.getSchoolCode(), id);
		if(null == user){
			throw new ErrorMsg("用户不存在");
		}
		if(null == user.getDeposit() || user.getDeposit().intValue() <= 0){
			throw new ErrorMsg("用户无押金可退");
		}
		DepositReturn dr = new DepositReturn();
		dr.setCardNo(null);
		dr.setClsCode(user.getClsCode());
		dr.setClsName(user.getClsName());
		dr.setCreateTime(new Date());
		dr.setCreateUserCode(CurrentUser.getUserCode());
		dr.setCreateUserName(CurrentUser.getUserName());
		dr.setDel(false);
		dr.setDptName(user.getDptName());
		dr.setDptCode(user.getDptCode());
		dr.setGrdCode(user.getGrdCode());
		dr.setGrdName(user.getGrdName());
		dr.setReturnAmount(user.getDeposit());
		dr.setReturnTime(new Date());
		dr.setSchoolCode(CurrentUser.getSchoolCode());
		dr.setSno(user.getSno());
		dr.setStuCode(user.getStuCode());
		if(Objects.equals(UserTypeEnum.STUDENT.getUserType(), user.getUserType())){
			dr.setUserName(user.getStuName());
		}else{
			dr.setUserName(user.getUserName());
			dr.setUserCode(user.getUserCode());
		}
		boolean result = Db.tx(() -> {
			int count = this.service.updateDeposit(CurrentUser.getSchoolCode(), user.getId(), user.getVersion(),
					0, CurrentUser.getUserCode(), new Date());
			if(count == 1){
				return this.returnService.save(dr);
			}
			return count == 1;
		});
		return result;
	}

	/**
	 * 根据user_code或stu_code查询
	 */
	public UserInfo findByUserCode(String untiCode,String userCode,String userType){
		Kv kv = Kv.by("school_code", untiCode)
				.set("user_type",userType)
				.set("user_code",userCode);
		SqlPara sqlPara = Db.getSqlPara("UserInfoLogic.findByUserCode", kv);
		return UserInfo.dao.findFirst(sqlPara);
	}

	/**
	 * 查询某个类型的用户是否存在
	 * @param schoolCode
	 * @return
	 */
	public boolean existsByType(String schoolCode, String userType){
		return this.service.existsByType(schoolCode, userType);
	}

	public boolean stopOrRecoverBorrow(String schoolCode, String userType, String userCode, int canBorrow){
		Kv kv = Kv.by("user_type", userType).set("user_code", userCode).set("can_borrow", canBorrow).set("school_code", schoolCode);
		SqlPara barSql = Db.getSqlPara("UserInfoLogic.stopOrRecoverBorrow", kv);
		Db.update(barSql);
		return true;
	}

}