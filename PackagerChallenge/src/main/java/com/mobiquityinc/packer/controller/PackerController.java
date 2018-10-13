package com.mobiquityinc.packer.controller;

import java.util.List;

import com.mobiquityinc.com.viewer.IPackagingViewer;
import com.mobiquityinc.entities.ItemEntity;
import com.mobiquityinc.packer.stratetgy.IPackagingAlgorithm;

public class PackerController {
	
	IPackagingAlgorithm selector;
	IPackagingViewer viewer;

	public PackerController(IPackagingAlgorithm selector, IPackagingViewer viewer){
		this.selector = selector;
		this.viewer   = viewer;
	}
	
	public String selectItems(List<ItemEntity> itemsList, int capacity){
		List<ItemEntity> selectedItemsList =  selector.selectItems(itemsList, capacity);
		return viewer.convertToString(selectedItemsList);
	}
}
