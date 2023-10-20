package com.test.hibernate.model.onetomany;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Store.class)
public abstract class Store_ {

	public static volatile SingularAttribute<Store, String> name;
	public static volatile SingularAttribute<Store, Long> id;
	public static volatile CollectionAttribute<Store, Item> items;

	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String ITEMS = "items";

}

