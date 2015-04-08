package com.aaronmalone.babysitter;

import com.google.common.base.Preconditions;

import java.time.LocalTime;

/**
 * Class for calculating the amount charged by a babysitter according to the
 * following rules:
 * <ul>
 * <li>The babysitter starts no earlier than 5:00PM.</li>
 * <li>The babysitter leaves no later than 4:00AM.</li>
 * <li>The babysitter gets paid $12/hour from start-time to bedtime.</li>
 * <li>The babysitter gets paid $8/hour from bedtime to midnight.</li>
 * <li>The babysitter gets paid $16/hour from midnight to end of job.</li>
 * <li>The babysitter gets paid for full hours (no fractional hours).</li>
 * </ul>
 * <p/>
 * Additionally, I'll add this clarification based on my understanding of the
 * kata:
 * <ul>
 * <li>Bedtime never occurs after midnight, but may happen any time between the
 * start time and midnight (inclusive). Every night has a bedtime.</li>
 * </ul>
 * <p/>
 * The babysitter gets paid for full hours, and there are no fractional hours
 * in the calculation of the nightly charge. This does not specify how
 * fractions of an hour should be figured. This is how I will interpret it:
 * <ul>
 * <li>For each hour, if the babysitter works any part of that hour,
 * then the babysitter is paid for the whole hour (e.g. if the babysitter starts
 * at 7:55pm and finishes at 8:05pm, the babysitter is paid for the full
 * 7-o'clock hour and the full 8-o'clock hour).</li>
 * <li>If the babysitter works at the pre-bedtime rate for any part of an hour,
 * then the babysitter charges the pre-bedtime rate for the entire hour (e.g.
 * if bedtime is at 10:05pm, then the babysitter charges the pre-bedtime rate
 * for the entire 10-o'clock hour).</li>
 * </ul>
 */
@SuppressWarnings("OctalInteger") //so we can use "00" for minutes
public class BabySitterChargeCalculator {

	private static final LocalTime FIVE_PM = LocalTime.of(17, 00);
	private static final LocalTime FOUR_AM = LocalTime.of(4, 00);

	/**
	 * Calculate the charge for a night of babysitting.
	 * The specific rules for calculating the charge are in the Javadoc for
	 * {@link BabySitterChargeCalculator}.
	 *
	 * @param startTime the time at which babysitting begins.
	 * @param bedTime   the time when the children go to bed.
	 * @param endTime   the the when babysitting ends.
	 * @return the total charge, in dollars, for the night of babysitting
	 */
	public static int calculateNightlyCharge(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		Preconditions.checkArgument(!startTime.isBefore(FIVE_PM), "Start time is before 5pm: %s", startTime);
		Preconditions.checkArgument(!bedTime.isBefore(startTime), "Bed time is before start time.");
		Preconditions.checkArgument(!endTime.isBefore(bedTime), "End time is before bed time.");
		Preconditions.checkArgument(endTimeNotAfter4am(endTime), "End time is after 4am: %s", endTime);
		return Integer.MIN_VALUE;
	}

	private static boolean endTimeNotAfter4am(LocalTime endTime) {
		return endTime.isBefore(LocalTime.MAX) || !endTime.isAfter(FOUR_AM);
	}
}
