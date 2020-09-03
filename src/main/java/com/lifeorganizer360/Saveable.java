package com.lifeorganizer360;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Saveable {
	@Id
	@GeneratedValue
	private Long id;

	protected Saveable() {

	}

	public void save() {
		Main.getSession().save(this);
	}

	public void delete() {
		Main.getSession().delete(Main.getSession().load(Saveable.class, getId()));
	}

	public long getId() {
		return id;
	}
}
