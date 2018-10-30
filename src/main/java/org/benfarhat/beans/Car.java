package org.benfarhat.beans;

public class Car {
    private Long Id;
    private String name;
    private int price;
    
    public Car() {
    	
    }
	public Car(Long id, String name, int price) {
		super();
		Id = id;
		this.name = name;
		this.price = price;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
