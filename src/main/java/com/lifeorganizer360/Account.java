package com.lifeorganizer360;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Account extends Saveable {
	@Property
	private String firstName, lastName;

	@Property
	double currentBalance, totalAwards, totalPenalties;

	protected Account() {

	}

	protected Account(String f, String l) {
		firstName = f;
		lastName = l;
		save();
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public double getBalance() {
		return currentBalance;
	}

	public void award(double a) {
		new awardPenaltyNotification(a);
		currentBalance += a;
		totalAwards += a;
		Main.updateBalance();
		save();
	}

	public void penalize(double p) {
		new awardPenaltyNotification(-1 * p);
		currentBalance -= p;
		totalPenalties += p;
		Main.updateBalance();
		save();
	}

}
