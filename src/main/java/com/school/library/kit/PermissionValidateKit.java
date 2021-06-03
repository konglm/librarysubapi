package com.school.library.kit;


import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.enums.Logical;
import com.jfnice.enums.OpCodeEnum;
import com.school.api.gx.PtApi;

/**
 * 权限校验辅助类
 * @author jinshiye
 *
 */
public class PermissionValidateKit {
	
	/**
	 * 校验权限是否通过
	 * @param m
	 * @param c
	 * @return
	 */
	public static boolean validate(JsyPermissions m , Controller c){
        OpCodeEnum[] permissions = m.value();

        String grdCode = c.getPara(m.grdCodeParaName(), c.getAttr(m.grdCodeParaName()));
        
        if(StrKit.isBlank(m.grdCodeParaName())){//不需要年级参数，传0
        	grdCode = "0";
        }
        String clsCode = c.getPara(m.clsCodeParaName(), c.getAttr(m.clsCodeParaName()));
        if(StrKit.isBlank(m.clsCodeParaName())){//不需要班级参数，传0
        	clsCode = "0";
        }
        String subCode = c.getPara(m.subCodeParaName(), c.getAttr(m.subCodeParaName())); 
        if(StrKit.isBlank(m.subCodeParaName())){//不需要科目参数，传"0"
        	subCode = "0";
        }
        String stuCode = c.getPara(m.stuCodeParaName(), c.getAttr(m.stuCodeParaName()));
        if(StrKit.isBlank(m.stuCodeParaName())){//不需要学生参数，传0
        	stuCode = "0";
        }
        String res = PtApi.getPermissionByPositionList(permissions, grdCode, clsCode, subCode, stuCode);// 1,1,0,1,0
        boolean isPermitted = StrKit.notBlank(res) && (m.logical().equals(Logical.AND) ? !res.contains("0") : res.contains("1"));
        LogKit.info("permissions[" + permissions + "]grdCode[" + grdCode + "]clsCode[" + clsCode + "]subCode[" + subCode + "]res[" + res + "]isPermitted[" + isPermitted + "]");
        return isPermitted;
	}
	
	
	
	
	
	
	
	
}
