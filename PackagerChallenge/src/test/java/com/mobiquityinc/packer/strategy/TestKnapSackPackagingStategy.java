package com.mobiquityinc.packer.strategy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.mobiquityinc.entities.ItemEntity;
import com.mobiquityinc.packer.stratetgy.KnapSackPackagingStrategy;

public class TestKnapSackPackagingStategy {

	// since a the KanpSack engine is stateless it can be initiate once
	private final KnapSackPackagingStrategy packer = new KnapSackPackagingStrategy();
	
	@Test
	public void testEmptyList(){
		// sending no items --> empty list should return
		List<ItemEntity> items = new ArrayList<>();
		List<ItemEntity> selectedItems = packer.selectItems(items, 50);
		assertEquals(0, selectedItems.size());
	}
	
	@Test
	public void testInvalidCapacity(){
		// sending no items --> empty list should return
		List<ItemEntity> items = new ArrayList<>();
		items.add(new ItemEntity(1, 10, 10));
		List<ItemEntity> selectedItems = packer.selectItems(items, 0);
		assertEquals(0, selectedItems.size());
		selectedItems = packer.selectItems(items, -1);
		assertEquals(0, selectedItems.size());
	}
	
	@Test
	public void testOneItem(){
		// sending no items --> empty list should return
		List<ItemEntity> items = new ArrayList<>();
		items.add(new ItemEntity(1, 10, 10));
		List<ItemEntity> selectedItems = packer.selectItems(items, 10);
		assertEquals(1, selectedItems.size());
		ItemEntity item = selectedItems.remove(0);
		assertEquals(Integer.valueOf(1), item.getId());
		assertEquals(Integer.valueOf(10), item.getWeight());
		assertEquals(Integer.valueOf(10), item.getCost());
	}
	
	@Test
	public void testOneTooBigItem(){
		// sending no items --> empty list should return
		List<ItemEntity> items = new ArrayList<>();
		items.add(new ItemEntity(1, 1000, 10));
		List<ItemEntity> selectedItems = packer.selectItems(items, 900);
		assertEquals(0, selectedItems.size());
	}
	
	// 56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)
	@Test
	public void testCapacityThreshold(){
		List<ItemEntity> items = new ArrayList<>();
		items.add(new ItemEntity(1, 9072, 13));
		items.add(new ItemEntity(2, 3380, 40));
		items.add(new ItemEntity(3, 4315, 10));
		items.add(new ItemEntity(4, 3797, 16));
		items.add(new ItemEntity(5, 4681, 36));
		items.add(new ItemEntity(6, 4877, 79));
		items.add(new ItemEntity(7, 8180, 45));
		items.add(new ItemEntity(8, 1936, 79));
		items.add(new ItemEntity(9, 676, 64));
		Collections.sort(items);
		
		List<ItemEntity> selectedItems = packer.selectItems(items, 5600);

		assertEquals(2, selectedItems.size());
		assertEquals(Integer.valueOf(8), selectedItems.get(0).getId());
		assertEquals(Integer.valueOf(1936), selectedItems.get(0).getWeight());
		assertEquals(Integer.valueOf(79), selectedItems.get(0).getCost());
		assertEquals(Integer.valueOf(9), selectedItems.get(1).getId());
		assertEquals(Integer.valueOf(676), selectedItems.get(1).getWeight());
		assertEquals(Integer.valueOf(64), selectedItems.get(1).getCost());
	}
	
}
