package com.mobiquityinc.com.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mobiquityinc.entities.ItemEntity;

public class DefaultPackagingViewer implements IPackagingViewer {

	@Override
	public void showItems(List<ItemEntity> list){
		System.out.println(convertToString(list));
	}
	
	public String convertToString(List<ItemEntity> list){
		  if (list.isEmpty()){
			  return ("-");
		  } else {
			  List<Integer> itemsIndexes = new ArrayList<>();
			  for (ItemEntity item : list){
				 itemsIndexes.add(item.getId());
			  }
			  Collections.sort(itemsIndexes);
			  StringBuilder builder = new StringBuilder();
			  builder.append(itemsIndexes.remove(0));
			  while (!itemsIndexes.isEmpty()) {
				  builder.append(",");
				  builder.append(itemsIndexes.remove(0));				  
			  } 
			  return (builder.toString());
		  }
	}
	
}
