package com.jfnice.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDict<M extends BaseDict<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return getLong("id");
	}

	public void setTag(String tag) {
		set("tag", tag);
	}

	public String getTag() {
		return getStr("tag");
	}

	public void setK(String k) {
		set("k", k);
	}

	public String getK() {
		return getStr("k");
	}

	public void setV(String v) {
		set("v", v);
	}

	public String getV() {
		return getStr("v");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	public String getLabel() {
		return getStr("label");
	}

	public void setStyle(String style) {
		set("style", style);
	}

	public String getStyle() {
		return getStr("style");
	}

	public void setDisplay(Boolean display) {
		set("display", display);
	}

	public Boolean getDisplay() {
		return get("display");
	}

	public void setSort(Long sort) {
		set("sort", sort);
	}

	public Long getSort() {
		return getLong("sort");
	}

	public void setStatus(Short status) {
		set("status", status);
	}

	public Short getStatus() {
		return getShort("status");
	}

	public void setDel(Boolean del) {
		set("del", del);
	}

	public Boolean getDel() {
		return get("del");
	}

}
