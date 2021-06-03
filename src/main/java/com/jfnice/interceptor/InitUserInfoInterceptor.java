package com.jfnice.interceptor;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfnice.annotation.JsyPermissions;
import com.jfnice.annotation.ShiroClear;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.enums.Logical;
import com.jfnice.enums.OpCodeEnum;
import com.jfnice.enums.ResultEnum;
import com.jfnice.ext.CondPara;
import com.jfnice.ext.CurrentUser;
import com.jfnice.ext.ErrorMsg;
import com.school.api.gx.PtApi;
import com.school.library.userinfo.UserInfoLogic;
import com.school.library.userinfo.UserTypeEnum;

/**
 * 初始化用户信息拦截器，每次进入系统，判断用户信息是否为空，为空则去同步信息
 */
public class InitUserInfoInterceptor implements Interceptor {

    private static final UserInfoLogic userLogic = Aop.get(UserInfoLogic.class);

    @Override
    public void intercept(Invocation inv) {
        try {
            //判断学生信息是否为空，为空则进行同步操作
            boolean existsStu = userLogic.existsByType(CurrentUser.getSchoolCode(), UserTypeEnum.STUDENT.getUserType());
            if(!existsStu){
                userLogic.synchronizeStuInfo();
            }

            //判断教师信息是否为空，为空则进行同步操作
            boolean existsTeacher = userLogic.existsByType(CurrentUser.getSchoolCode(), UserTypeEnum.TEACHER.getUserType());
            if(!existsTeacher){
                userLogic.synchronizeTeacherInfo();
            }
        } catch (ErrorMsg e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        inv.invoke();
    }


}
