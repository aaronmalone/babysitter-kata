package com.aaronmalone.babysitter;

import com.google.common.annotations.VisibleForTesting;
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
 * <p>
 * Additionally, I'll add this clarification based on my understanding of the
 * kata:
 * <ul>
 * <li>Bedtime never occurs after midnight, but may happen any time between the
 * start time and midnight (inclusive). Every night has a bedtime.</li>
 * </ul>
 * <p>
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
		checkArguments(startTime, bedTime, endTime);
		return Integer.MIN_VALUE;
	}

	@VisibleForTesting
	static void checkArguments(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		Preconditions.checkArgument(!startTime.isBefore(FIVE_PM), "Start time is before 5pm: %s", startTime);
		Preconditions.checkArgument(endTimeNotAfter4am(endTime), "End time is after 4am: %s", endTime);
		Preconditions.checkArgument(bedTimeNotAfterMidnight(bedTime), "Bed time is after midnight: %s", bedTime);
		Preconditions.checkArgument(!bedTime.isBefore(startTime), "Bed time is before start time.");
		Preconditions.checkArgument(endTimeNotBeforeBedTime(bedTime, endTime), "End time is before bed time.");
	}

	private static boolean endTimeNotAfter4am(LocalTime endTime) {
		return betweenInclusive(LocalTime.MIDNIGHT, FOUR_AM, endTime)
				|| betweenInclusive(FIVE_PM, LocalTime.MAX, endTime);
	}

	private static boolean bedTimeNotAfterMidnight(LocalTime bedTime) {
		return bedTime.equals(LocalTime.MIDNIGHT)
				|| betweenInclusive(FIVE_PM, LocalTime.MAX, bedTime);
	}

	private static boolean endTimeNotBeforeBedTime(LocalTime bedTime, LocalTime endTime) {
		//note: bedtime should always be at, or before, midnight
		return betweenInclusive(LocalTime.MIDNIGHT, FOUR_AM, endTime)
				|| !endTime.isBefore(bedTime);
	}

	/**
	 * Checks that a {@link LocalTime} is between two times.
	 *
	 * @param begin       the beginning of the time period, inclusive
	 * @param end         the end of the time period, inclusive
	 * @param timeToCheck the time to check
	 * @return true if the time is between the two times or equal to one of them
	 * @throws java.lang.IllegalArgumentException if the endTime is before the
	 *                                            start time using {@link LocalTime#isBefore(LocalTime)}
	 */
	@VisibleForTesting
	static boolean betweenInclusive(LocalTime begin, LocalTime end, LocalTime timeToCheck) {
		Preconditions.checkArgument(!end.isBefore(begin), "End time (%s) is before begin time (%s).", end, begin);
		boolean between = timeToCheck.isAfter(begin) && timeToCheck.isBefore(end);
		return between || timeToCheck.equals(begin) || timeToCheck.equals(end);
	}

	@VisibleForTesting
	static int hoursBeforeMidnight(LocalTime startTime) {
		return Integer.MIN_VALUE;
	}

	/**
	 * Returns the number of hours worked after midnight, based on the end time.
	 * Fractional hours are rounded up to a whole hour.
	 */
	@VisibleForTesting
	static int hoursAfterMidnight(LocalTime endTime) {
		int hour = endTime.getHour();
		if (hour > FOUR_AM.getHour()) {
			return 0;
		} else if (endTime.isAfter(LocalTime.of(hour, 00))) {
			return hour + 1; //round up number of hours
		} else {
			return hour;
		}
	}
}
