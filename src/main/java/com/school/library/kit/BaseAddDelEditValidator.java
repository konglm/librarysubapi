package com.school.library.kit;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.core.JFniceBaseService;
import com.jfnice.core.JFniceBaseValidator;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;

/**
 * 编辑、删除、保存校验器
 * @author jinshiye
 *
 * @param <T>
 */
public abstract class BaseAddDelEditValidator<T extends JFniceBaseService<?>> extends JFniceBaseValidator {
	
	private T service;
	
	public BaseAddDelEditValidator(T t){
		this.service = t;
	}
	
	@Override
	protected void validate(Controller c) {
		boolean flag = false;
		boolean addFlag = false;
		String primaryKey = this.service.getPrimaryKey();
		JsyAddDelEdit ade = getActionMethod().getAnnotation(JsyAddDelEdit.class);
        if (ade != null && ade.requirePrimary()) {//需要主键-删、改
        	if(StrKit.notBlank(ade.primaryParam())){//传过来的主键字段名非空，则取传过来的主键名
        		primaryKey = ade.primaryParam();
        	}
        	if (StrKit.notBlank(c.getPara(primaryKey))) {
				flag = true;
			}else{
				validateEqualString(null, null, "ERROR_ID_NULL", "id传参错误");//errorKey 必须以ERROR开头
			}
        }else if(ade != null && !ade.requirePrimary()){//不需要主键-新增
        	addFlag = true;//新增
        }
		
		if(addFlag){//新增
			JsyPermissions p = getActionMethod().getAnnotation(JsyPermissions.class);
			if (p != null) {
	            boolean access = PermissionValidateKit.validate(p, c);
	            if(access){//具有新增权限
	            	return;
	            }else{
	            	throw new ErrorMsg(ResultEnum.AUTHORITY_ERROR);
	            }
			}
		}
		if(flag){//编辑或者删除
			Model<?> m = this.service.queryById(c.getPara(primaryKey));
			//判断是否具有系统赋予的编辑或者删除权限
			JsyPermissions p = getActionMethod().getAnnotation(JsyPermissions.class);
			if (p != null) {
				
		        if(StrKit.notBlank(p.grdCodeParaName())){//需要年级参数
		        	//但是请求没有把这个参数传过来，则从model里面取值加入请求参数
		        	if(!c.isParaExists(p.grdCodeParaName())){
		        		String grdCode = m.getStr(p.grdCodeParaName());
		        		c.setAttr(p.grdCodeParaName(), grdCode);
		        		//c.setUrlPara(urlPara);
		        	}
		        }
		        if(StrKit.notBlank(p.clsCodeParaName())){//需要班级参数
		        	//但是请求没有把这个参数传过来，则从model里面取值加入请求参数
		        	if(!c.isParaExists(p.clsCodeParaName())){
						String clsCode = m.getStr(p.clsCodeParaName());
		        		c.setAttr(p.clsCodeParaName(), clsCode);
		        		//c.setUrlPara(urlPara);
		        	}
		        }
		        
		        if(StrKit.notBlank(p.subCodeParaName())){//需要科目参数
		        	//但是请求没有把这个参数传过来，则从model里面取值加入请求参数
		        	if(!c.isParaExists(p.subCodeParaName())){
		        		String subCode = m.getStr(p.subCodeParaName());
		        		c.setAttr(p.subCodeParaName(), subCode);
		        		//c.setUrlPara(urlPara);
		        	}
		        }
		        if(StrKit.notBlank(p.stuCodeParaName())){//需要学生参数
		        	//但是请求没有把这个参数传过来，则从model里面取值加入请求参数
		        	if(!c.isParaExists(p.stuCodeParaName())){
		        		String stuCode = m.getStr(p.stuCodeParaName());
		        		c.setAttr(p.stuCodeParaName(), stuCode);
		        		//c.setUrlPara(urlPara);
		        	}
		        }
				
	            boolean access = PermissionValidateKit.validate(p, c);
	            if(access){//具有编辑、删除权限
	            	return;
	            }
			}
			
			if(null!= m){
				String createUserCode = StrKit.notBlank(m.getStr("create_user_code"))?m.getStr("create_user_code"):m.getStr("recorder_code");
				if(StrKit.notBlank(createUserCode)){//判断是否为本人创建
					//validateEqualInteger(createUserCode.intValue(), CurrentUser.getUserCode().intValue(), "ERROR_NOT_CREATE_SELF", "没有对此条数据操作的权限");
					if(!createUserCode.equals(CurrentUser.getUserCode())){
						throw new ErrorMsg(ResultEnum.AUTHORITY_ERROR);
					}
				}
			}else{
				//validateEqualString(null, null, "ERROR_MODEL_NULL", "没有此操作的权限");//errorKey 必须以ERROR开头
				throw new ErrorMsg(ResultEnum.AUTHORITY_ERROR);
			}
		}

	}

}
