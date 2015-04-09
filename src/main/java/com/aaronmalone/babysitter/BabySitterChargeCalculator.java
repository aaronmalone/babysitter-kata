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

	private static final LocalTime FOUR_AM = LocalTime.of(4, 00);

	@VisibleForTesting
	static final int AFTER_MIDNIGHT_RATE = 16;

	@VisibleForTesting
	static final int BEFORE_BEDTIME_RATE = 12;

	@VisibleForTesting
	static final int AFTER_BEDTIME_RATE = 8;

	private static final int HOUR_OF_MIDNIGHT = 0;
	private static final int HOUR_OF_4_AM = 4;
	private static final int HOUR_OF_5_PM = 17;

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
	public static int calculateTotalCharge(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		checkArguments(startTime, bedTime, endTime);
		int beforeBedTimeCharge = getChargeForBeforeBedTime(startTime, bedTime);
		int afterBedTimeCharge = getChargeForAfterBedTime(startTime, bedTime, endTime);
		int afterMidnightCharge = getChargeForAfterMidnight(endTime);
		return beforeBedTimeCharge + afterBedTimeCharge + afterMidnightCharge;
	}

	/**
	 * Check that all times fit within the rules outlined for this kata
	 * and fall in the right order.
	 */
	@VisibleForTesting
	static void checkArguments(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		Preconditions.checkArgument(isStartTimeNotBefore5pm(startTime), "Start time is before 5pm: %s", startTime);
		Preconditions.checkArgument(isEndTimeNotAfter4am(endTime), "End time is after 4am: %s", endTime);
		Preconditions.checkArgument(isBedTimeNotAfterMidnight(bedTime), "Bed time is after midnight: %s", bedTime);
		Preconditions.checkArgument(isBedTimeNotBeforeStartTime(startTime, bedTime), "Bed time is before start time.");
		Preconditions.checkArgument(isEndTimeNotBeforeBedTime(bedTime, endTime), "End time is before bed time.");
	}

	private static boolean isStartTimeNotBefore5pm(LocalTime startTime) {
		int hour = startTime.getHour();
		return hour >= HOUR_OF_5_PM || hour == HOUR_OF_MIDNIGHT;
	}

	private static boolean isEndTimeNotAfter4am(LocalTime endTime) {
		return !endTime.isAfter(FOUR_AM)
				|| endTime.getHour() >= HOUR_OF_5_PM;

	}

	private static boolean isBedTimeNotAfterMidnight(LocalTime bedTime) {
		return bedTime.equals(LocalTime.MIDNIGHT)
				|| bedTime.getHour() >= HOUR_OF_5_PM;
	}

	private static boolean isBedTimeNotBeforeStartTime(LocalTime startTime, LocalTime bedTime) {
		return bedTime.equals(LocalTime.MIDNIGHT) || !startTime.isAfter(bedTime);
	}

	private static boolean isEndTimeNotBeforeBedTime(LocalTime bedTime, LocalTime endTime) {
		//note: bedtime should always be at, or before, midnight
		return endTime.getHour() <= HOUR_OF_4_AM
				|| !bedTime.isAfter(endTime);
	}

	/**
	 * Returns the total number of hours worked before midnight, both pre- and post-bedtime,
	 * based on the start time.
	 * Fractional hours are rounded up to a whole hour.
	 */
	@VisibleForTesting
	static int hoursBeforeMidnight(LocalTime startTime) {
		int hour = startTime.getHour();
		return hour == HOUR_OF_MIDNIGHT ? 0 : 24 - hour;
	}

	/**
	 * Returns the number of hours worked after midnight, based on the end time.
	 * Fractional hours are rounded up to a whole hour.
	 */
	@VisibleForTesting
	static int hoursAfterMidnight(LocalTime endTime) {
		int hour = endTime.getHour();
		if (hour > FOUR_AM.getHour()) {
			//actually, in this scenario, before midnight
			return 0;
		} else {
			return getHourRoundUp(endTime);
		}
	}

	/**
	 * Returns the number of hours worked between start time and bed time.
	 * Fractional hours are rounded up to a whole hour.
	 */
	@VisibleForTesting
	static int hoursPreBedTime(LocalTime startTime, LocalTime bedTime) {
		if (bedTime.equals(startTime)) {
			return 0;
		} else if (bedTime.equals(LocalTime.MIDNIGHT)) {
			return 24 - startTime.getHour();
		} else {
			int bedHour = getHourRoundUp(bedTime);
			int startHour = startTime.getHour();
			return bedHour - startHour;
		}
	}

	/**
	 * Returns the number of hours that will be used to factor the
	 * charge between bed time and either midnight or the end time,
	 * whichever is earlier.
	 * <p>
	 * Note that if both pre- and post-bedtime babysitting occurs in
	 * the same hour, the hour will be counted toward pre-bedtime.
	 */
	@VisibleForTesting
	static int hoursPostBedTime(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		if (endTime.getHour() <= HOUR_OF_4_AM) {
			return hoursBeforeMidnight(startTime) - hoursPreBedTime(startTime, bedTime);
		} else {
			int endHour = getHourRoundUp(endTime);
			int startHour = startTime.getHour();
			int totalHours = endHour - startHour;
			return totalHours - hoursPreBedTime(startTime, bedTime);
		}
	}

	/**
	 * Returns the hour of the {@link LocalTime}, rounded up if the time has
	 * passed the exactly n-o'clock.
	 * <p>
	 * For example, <code>getHourRoundUp(LocalTime.of(4, 15)) == 5</code>
	 */
	private static int getHourRoundUp(LocalTime t) {
		int hour = t.getHour();
		if (t.isAfter(LocalTime.of(hour, 00))) {
			return hour + 1;
		} else {
			return hour;
		}
	}

	/**
	 * Returns the charge, in dollars, for hours worked after midnight.
	 * Fractional hours are rounded up.
	 */
	@VisibleForTesting
	static int getChargeForAfterMidnight(LocalTime endTime) {
		int hours = hoursAfterMidnight(endTime);
		return hours * AFTER_MIDNIGHT_RATE;
	}

	/**
	 * Returns the charge, in dollars, for hours worked before bedtime.
	 * Fractional hours are rounded up. If a both pre- and post-bedtime
	 * babysitting occur in the same hour, the hour is counted as
	 * before bedtime.
	 */
	@VisibleForTesting
	static int getChargeForBeforeBedTime(LocalTime startTime, LocalTime bedTime) {
		int hours = hoursPreBedTime(startTime, bedTime);
		return hours * BEFORE_BEDTIME_RATE;
	}

	/**
	 * Returns the charge, in dollars, for hours worked after bedtime
	 * and before midnight. If a both pre- and post-bedtime
	 * babysitting occur in the same hour, the hour is counted as
	 * before bedtime.
	 */
	@VisibleForTesting
	static int getChargeForAfterBedTime(LocalTime startTime, LocalTime bedTime, LocalTime endTime) {
		int hours = hoursPostBedTime(startTime, bedTime, endTime);
		return hours * AFTER_BEDTIME_RATE;
	}
}
