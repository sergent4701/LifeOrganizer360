package com.lifeorganizer360;

import java.text.DecimalFormat;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class AwardPenaltyNotification extends Notification {

	@Property
	private double awardPenalty;

	protected AwardPenaltyNotification() {

	}

	protected AwardPenaltyNotification(double d) {
		super((d >= 0 ? "You have been awarded $" + new DecimalFormat("#0.00").format(d) + "!"
				: "You have been penalized $" + new DecimalFormat("#0.00").format(-1 * d) + " :("),
				"Click on this notification to acknowledge it.");
		awardPenalty = d;
		save();
	}
}
