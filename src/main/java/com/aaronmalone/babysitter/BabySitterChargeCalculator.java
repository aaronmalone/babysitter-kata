package com.aaronmalone.babysitter;

import java.time.LocalTime;

/**
 * Class for calculating the amount charged by a babysitter according to the
 * following rules:
 * <ul>
 *     <li>The babysitter starts no earlier than 5:00PM.</li>
 *     <li>The babysitter leaves no later than 4:00AM.</li>
 *     <li>The babysitter gets paid $12/hour from start-time to bedtime.</li>
 *     <li>The babysitter gets paid $8/hour from bedtime to midnight.</li>
 *     <li>The babysitter gets paid $16/hour from midnight to end of job.</li>
 *     <li>The babysitter gets paid for full hours (no fractional hours).</li>
 * </ul>
 * <p>
 * Additionally, I'll add this clarification based on my understanding of the
 * kata:
 * <ul>
 *     <li>Bedtime never occurs after midnight, but may happen any time between the
 *     start time and midnight (inclusive). Every night have a bedtime.</li>
 * </ul>
 * <p>
 * The babysitter gets paid for full hours, and there are no fractional hours
 * in the calculation of the nightly charge. This does not specify how
 * fractions of an hour should be figured. I will assume the following are true:
 * <ul>
 *     <li>Each rate will be multiplied by a whole number of hours to calculate
 *     the overall charge.</li>
 *     <li>The number of hours for each rate, added together, should equal the
 *     total number of hours for the night (rounded to the nearest whole).</li>
 * </ul>
 *
 */
@SuppressWarnings("OctalInteger") //so we can use "00" for minutes
public class BabySitterChargeCalculator {

	private static final LocalTime FIVE_PM = LocalTime.of(17, 00);

	/**
	 * Calculate the charge for a night of babysitting.
	 *
	 * @param startTime the time at which babysitting begins.
	 * @param bedTime   the time when the children go to bed.
	 * @param endTime   the the when babysitting ends.
	 * @return the total charge, in dollars, for the night of babysitting
	 */
	public static int calculateNightlyCharge(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		if (startTime.isBefore(FIVE_PM)) {
			throw new IllegalArgumentException("Start time is before 5pm: " + startTime);
		}
		return Integer.MIN_VALUE;
	}
}
