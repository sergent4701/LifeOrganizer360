package com.lifeorganizer360;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class awardPenaltyNotification extends Notification {

	@Property
	private double awardPenalty;

	protected awardPenaltyNotification() {

	}

	protected awardPenaltyNotification(double d) {
		super((d > 0 ? "You have been awarded $" + d + "!" : "You have been penalized $" + (-1 * d) + " :("),
				"Click on this notification to acknowledge it.");
		awardPenalty = d;
		save();
	}
}
