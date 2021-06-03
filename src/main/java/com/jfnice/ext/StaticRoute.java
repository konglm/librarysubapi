package com.jfnice.ext;

import com.jfinal.config.Routes;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticRoute {

    private static List<Routes> routesList; // JFinal的路由
    private static Map<String, Action> actionMap; // JFinal的映射: actionKey > Action
    private static Map<Class<? extends Controller>, String> controllerKeyMap = new HashMap<Class<? extends Controller>, String>(); // controllerClass -> controllerKey
    private static Map<Class<? extends Controller>, String> viewPathMap = new HashMap<Class<? extends Controller>, String>(); // controllerClass -> viewPath
    private static Map<Class<? extends Controller>, Routes> routesMap = new HashMap<Class<? extends Controller>, Routes>(); // controllerClass -> Routes

    @SuppressWarnings("unchecked")
    public static void build(List<Routes> routesList) {
        try {
            StaticRoute.routesList = routesList;
            Field actionMappingField = JFinal.me().getClass().getDeclaredField("actionMapping");
            actionMappingField.setAccessible(true);
            Object actionMapping = actionMappingField.get(JFinal.me());
            Field mappingField = actionMapping.getClass().getDeclaredField("mapping");
            mappingField.setAccessible(true);
            actionMap = (Map<String, Action>) mappingField.get(actionMapping);
            for (Map.Entry<String, Action> entry : actionMap.entrySet()) {
                controllerKeyMap.put(entry.getValue().getControllerClass(), entry.getValue().getControllerKey());
                viewPathMap.put(entry.getValue().getControllerClass(), entry.getValue().getViewPath());
                for (Routes routes : StaticRoute.routesList) {
                    for (Routes.Route route : routes.getRouteItemList()) {
                        if (route.getControllerClass() == entry.getValue().getControllerClass()) {
                            routesMap.put(route.getControllerClass(), routes);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Action getAction(String actionKey) {
        return actionMap.get(actionKey);
    }

    public static String getControllerKey(Class<? extends Controller> controllerClass) {
        return controllerKeyMap.get(controllerClass);
    }

    public static String getViewPath(Class<? extends Controller> controllerClass) {
        return viewPathMap.get(controllerClass);
    }

    public static Routes getRoutes(Class<? extends Controller> controllerClass) {
        return routesMap.get(controllerClass);
    }

}
