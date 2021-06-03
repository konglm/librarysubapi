package com.jfnice.interceptor;

import com.jfinal.aop.Invocation;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfnice.core.JFniceBaseController;

public class TxPost extends Tx {

    public void intercept(Invocation inv) {
        JFniceBaseController c = (JFniceBaseController) inv.getController();
        if (c.isPost()) {
            super.intercept(inv);
        } else {
            inv.invoke();
        }
    }

}
