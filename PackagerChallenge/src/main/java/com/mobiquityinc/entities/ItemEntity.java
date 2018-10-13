package com.mobiquityinc.entities;


// entity implements comparable by weight to allow sorting
// in order the results include the highest price with lowest weight
public class ItemEntity implements Comparable<ItemEntity>{

	private Integer id;
	private Integer   weight;
	private Integer   cost;

	public ItemEntity(Integer id, Integer weight, Integer cost) {
		super();
		this.id = id;
		this.weight = weight;
		this.cost = cost;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getWeight() {
		return weight;
	}
	
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	public Integer getCost() {
		return cost;
	}
	
	public void setCost(Integer cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", weight=" + weight + ", cost=" + cost + "]";
	}

	@Override
	public int compareTo(ItemEntity other) {
		return Integer.compare(this.getWeight(), other.getWeight());
	}

}
