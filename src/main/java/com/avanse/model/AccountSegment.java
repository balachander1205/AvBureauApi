package com.avanse.model;

import java.util.List;

public class AccountSegment {
	
	private List<OwnAccountBean> ownAccountBeanList;
	private List<OthersAccountBean> othersAccountBeanList;
	public List<OwnAccountBean> getOwnAccountBeanList() {
		return ownAccountBeanList;
	}
	public void setOwnAccountBeanList(List<OwnAccountBean> ownAccountBeanList) {
		this.ownAccountBeanList = ownAccountBeanList;
	}
	public List<OthersAccountBean> getOthersAccountBeanList() {
		return othersAccountBeanList;
	}
	public void setOthersAccountBeanList(
			List<OthersAccountBean> othersAccountBeanList) {
		this.othersAccountBeanList = othersAccountBeanList;
	}
	
}
