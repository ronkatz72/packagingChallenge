package com.mobiquityinc.com.viewer;

import java.util.List;

import com.mobiquityinc.entities.ItemEntity;

public interface IPackagingViewer {
	public void showItems(List<ItemEntity> list);
	public String convertToString(List<ItemEntity> list);
}
