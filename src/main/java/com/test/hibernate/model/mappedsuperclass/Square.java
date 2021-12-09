package com.test.hibernate.model.mappedsuperclass;

import javax.persistence.Entity;

@Entity
public class Square extends Shape {

	public Square() {
		super();
		this.sides = 4;
	}

}
