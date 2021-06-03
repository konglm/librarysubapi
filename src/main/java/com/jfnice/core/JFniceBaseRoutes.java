package com.jfnice.core;

import com.jfinal.config.Routes;

public abstract class JFniceBaseRoutes extends Routes implements JFniceIBaseRoutes {

    private String staticPath;
    private String projectName;
    private String projectUrl;
    private String errorUrl;
    private String entryUrl;

    public String getBaseViewPath() {
        return super.getBaseViewPath();
    }

    public String getStaticPath() {
        return staticPath;
    }

    public void setStaticPath(String staticPath) {
        this.staticPath = staticPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorPageActionUrl) {
        this.errorUrl = errorPageActionUrl;
    }

    public String getEntryUrl() {
        return entryUrl;
    }

    public void setEntryUrl(String entryUrl) {
        this.entryUrl = entryUrl;
    }

}
