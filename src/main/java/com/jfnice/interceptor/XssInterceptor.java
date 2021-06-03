package com.jfnice.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfnice.core.JFniceBaseController;
import com.jfnice.handler.HttpServletRequestExtend;
import com.jfnice.handler.MultipartRequestExtend;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import java.util.Map;

public class XssInterceptor implements Interceptor {

    private static final Whitelist whitelist = createWhitelist();
    private static final OutputSettings outputSettings = new OutputSettings().prettyPrint(false);

    private static Whitelist createWhitelist() {
        return Whitelist.relaxed()
                /**
                 * 必须要删除应用在 a 与 img 上的 protocols，否则就只有使用了这些 protocol 的才不被过滤，比较蛋疼
                 * 在 remove 的时候，后面的 protocols 要完全一个不露的对应上 jsoup 默认已经添加的，否则仍然会被过滤掉
                 * 在升级 jsoup 后需要测试这 a 与 img 的过滤是否正常
                 */
                .removeProtocols("a", "href", "ftp", "http", "https", "mailto")
                .removeProtocols("img", "src", "http", "https")

                .addAttributes("a", "href", "title", "target")  // 官方默认会将 target 给过滤掉

                /**
                 * 在 Whitelist.relaxed() 之外添加额外的白名单规则
                 */
                .addTags("div", "span", "embed", "object", "param")
                .addAttributes(":all", "style", "class", "id", "name")
                .addAttributes("object", "width", "height", "classid", "codebase")
                .addAttributes("param", "name", "value")
                .addAttributes("embed", "src", "quality", "width", "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type", "pluginspage");
    }

    private static String[] filter(String[] values) {
        if (values != null) {
            for (int i = 0, len = values.length; i < len; i++) {
                if (values[i] != null && !"".equals(values[i])) {
                    values[i] = Jsoup.clean(values[i], "", whitelist, outputSettings).trim();
                }
            }
        }
        return values;
    }

    private static void filterRequest(Invocation inv) {
        JFniceBaseController c = (JFniceBaseController) inv.getController();
        if (c.isMultipart()) {
            MultipartRequestExtend request = (MultipartRequestExtend) c.getRequest();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                request.setParameter(entry.getKey(), filter(entry.getValue()));
            }
        } else {
            HttpServletRequestExtend request = (HttpServletRequestExtend) c.getRequest();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                request.setParameter(entry.getKey(), filter(entry.getValue()));
            }
        }
    }

    public void intercept(Invocation inv) {
        filterRequest(inv);
        inv.invoke();
    }

}
