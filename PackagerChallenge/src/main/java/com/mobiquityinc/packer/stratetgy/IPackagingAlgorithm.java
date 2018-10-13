package com.mobiquityinc.packer.stratetgy;

import java.util.List;

import com.mobiquityinc.entities.ItemEntity;

public interface IPackagingAlgorithm {

	public List<ItemEntity> selectItems(List<ItemEntity> itemsList, int capacity);
	
}
