package com.mobiquityinc.entities;

import java.util.ArrayList;
import java.util.List;

public class ParsedLineEntity {
	
	private Integer maxWeight;
	private List<ItemEntity> itemsList = new ArrayList<>();
	
	public Integer getMaxWeight() {
		return maxWeight;
	}
	
	public void setMaxWeight(Integer maxWeight) {
		this.maxWeight = maxWeight;
	}
	
	public List<ItemEntity> getItemsList() {
		return itemsList;
	}
	
	public void setItemsList(List<ItemEntity> itemsList) {
		this.itemsList = itemsList;
	}
	
	@Override
	public String toString() {
		return "ParsedLineEntity [maxWeight=" + maxWeight + ", itemsList=" + itemsList + "]";
	}
	
}
