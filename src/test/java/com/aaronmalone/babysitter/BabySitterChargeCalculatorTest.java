package com.aaronmalone.babysitter;

import org.junit.Test;

import java.time.LocalTime;

import static com.aaronmalone.babysitter.BabySitterChargeCalculator.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("OctalInteger") //so we can use "00" for minutes
public class BabySitterChargeCalculatorTest {

	@Test(expected = IllegalArgumentException.class)
	public void testStartTimeBeforeFiveThrowsException() {
		LocalTime invalidStartTime = time(16, 59);
		LocalTime bedTime = time(18, 00);
		LocalTime endTime = time(20, 30);
		BabySitterChargeCalculator.checkArguments(invalidStartTime, bedTime, endTime);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStartTimeAfterMidnightThrowsException() {
		LocalTime afterMidnightStartTime = LocalTime.MIDNIGHT.plusMinutes(30);
		LocalTime bedTime = time(18, 00);
		LocalTime endTime = time(20, 30);
		BabySitterChargeCalculator.checkArguments(afterMidnightStartTime, bedTime, endTime);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatExceptionThrowIfBedTimeBeforeStartTime() {
		LocalTime startTime = time(18, 00);
		LocalTime invalidBedTimeBeforeStartTime = time(17, 15);
		LocalTime endTime = time(23, 00);
		BabySitterChargeCalculator.checkArguments(startTime, invalidBedTimeBeforeStartTime, endTime);
	}

	@Test
	public void testThatMidnightBedtimeIsAllowed() {
		/*
		 * LocalTime.MIDNIGHT is technically "before" every other valid LocalTime value,
		 * but if the bed time is midnight, we don't want to throw the same exception that
		 * we would throw if the bed time were actually before the start time.
		 */
		LocalTime startTime = time(18, 00);
		LocalTime midnightBedtime = LocalTime.MIDNIGHT;
		LocalTime endTime = LocalTime.MIDNIGHT.plusHours(2);
		BabySitterChargeCalculator.checkArguments(startTime, midnightBedtime, endTime);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatExceptionThrownIfEndTimeBeforeBedTime() {
		LocalTime startTime = time(18, 00);
		LocalTime bedTime = time(20, 00);
		LocalTime invalidEndTime = time(19, 00);
		BabySitterChargeCalculator.checkArguments(startTime, bedTime, invalidEndTime);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatEndTimeAfterFourAmThrowsException() {
		LocalTime startTime = time(17, 00);
		LocalTime bedTime = time(21, 00);
		LocalTime invalidEndTime = time(4, 30);
		BabySitterChargeCalculator.checkArguments(startTime, bedTime, invalidEndTime);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatBedTimeAfterMidnightThrowsException() {
		LocalTime startTime = time(17, 00);
		LocalTime endTime = time(4, 00);
		LocalTime invalidAfterMidnightBedTime = LocalTime.MIDNIGHT.plusMinutes(1);
		BabySitterChargeCalculator.checkArguments(startTime, invalidAfterMidnightBedTime, endTime);
	}

	@Test
	public void testHoursAfterMidnightMethodWithTimesNotAfterMidnight() {
		//note: there are zero hours after midnight if endTime is at or before midnight
		assertEquals(0, hoursAfterMidnight(LocalTime.MAX /* 23:59:59.99... */));
		assertEquals(0, hoursAfterMidnight(LocalTime.MIDNIGHT));
	}

	@Test
	public void testHoursBeforeMidnight() {
		//note: static import of method to save space
		assertEquals(0, hoursBeforeMidnight(LocalTime.MIDNIGHT));
		assertEquals(1, hoursBeforeMidnight(time(23, 59)));
		assertEquals(1, hoursBeforeMidnight(LocalTime.MAX));
		assertEquals(4, hoursBeforeMidnight(time(20, 00)));
		assertEquals(5, hoursBeforeMidnight(time(19, 59)));
		assertEquals(12, hoursBeforeMidnight(LocalTime.NOON));
	}

	@Test
	public void testHoursAfterMidnight() {
		//note: static import of method to save space
		assertEquals(0, hoursAfterMidnight(LocalTime.MIDNIGHT));
		assertEquals(1, hoursAfterMidnight(LocalTime.MIDNIGHT.plusNanos(1)));
		assertEquals(1, hoursAfterMidnight(time(1, 00).minusNanos(1)));
		assertEquals(1, hoursAfterMidnight(time(1, 00)));
		assertEquals(2, hoursAfterMidnight(time(1, 00).plusNanos(1)));
		assertEquals(2, hoursAfterMidnight(time(2, 00).minusNanos(1)));
		assertEquals(3, hoursAfterMidnight(time(2, 00).plusNanos(1)));
		assertEquals(3, hoursAfterMidnight(time(3, 00)));
		assertEquals(4, hoursAfterMidnight(time(3, 00).plusNanos(1)));
		assertEquals(4, hoursAfterMidnight(time(4, 00)));
	}

	@Test
	public void testHoursPreBedTime() {
		//note: static import of method to save space
		assertEquals(0, hoursPreBedTime(time(17, 00), time(17, 00)));
		assertEquals(1, hoursPreBedTime(time(17, 00), time(17, 10)));
		assertEquals(1, hoursPreBedTime(time(17, 00), time(18, 00)));
		assertEquals(2, hoursPreBedTime(time(17, 00), time(18, 10)));
		assertEquals(2, hoursPreBedTime(time(17, 59), time(18, 10)));

		//midnight cases
		assertEquals(0, hoursPreBedTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		assertEquals(1, hoursPreBedTime(LocalTime.MAX, LocalTime.MIDNIGHT));
		assertEquals(4, hoursPreBedTime(time(20, 00), LocalTime.MIDNIGHT));
	}

	@Test
	public void testHourPostBedTime() {
		//pre-midnight end time cases
		assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), LocalTime.MAX));
		assertEquals(4, hoursPostBedTime(time(17, 00), time(20, 00), LocalTime.MAX));
		assertEquals(3, hoursPostBedTime(time(17, 00), time(20, 01), LocalTime.MAX));

		//post-midnight end time cases
		assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), LocalTime.MIDNIGHT));
		assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), time(2, 15)));
		assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), time(4, 00)));
		assertEquals(1, hoursPostBedTime(time(17, 00), time(22, 59), time(4, 00)));
		assertEquals(2, hoursPostBedTime(time(17, 00), time(22, 00), time(4, 00)));
	}

	@Test
	public void testChargeForAfterMidnight() {
		LocalTime[] preMidnightEndTimes = {time(17, 00), time(22, 00), LocalTime.MAX};
		for (LocalTime endTime : preMidnightEndTimes) {
			assertEquals(0, getChargeForAfterMidnight(endTime));
		}
		assertEquals(0, getChargeForAfterMidnight(LocalTime.MIDNIGHT));
		assertEquals(AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(LocalTime.MIDNIGHT.plusNanos(1)));
		assertEquals(AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(0, 59)));
		assertEquals(AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(1, 00)));
		assertEquals(2 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(1, 15)));
		assertEquals(2 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(2, 00)));
		assertEquals(4 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(4, 00).minusNanos(1)));
		assertEquals(4 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(4, 00)));
	}

	@Test
	public void testChargeForBeforeBedTime() {
		assertEquals(0, getChargeForBeforeBedTime(time(17, 00), time(17, 00)));
		assertEquals(0, getChargeForBeforeBedTime(time(21, 00), time(21, 00)));
		assertEquals(0, getChargeForBeforeBedTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		assertEquals(BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(17, 00).plusNanos(1)));
		assertEquals(BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(18, 00)));
		assertEquals(2 * BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(18, 01)));
		assertEquals(3 * BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(19, 01)));
		assertEquals(4 * BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(20, 30), LocalTime.MIDNIGHT));
	}

	@Test
	public void testGetChargeForAfterBedTime() {
		LocalTime start = time(18, 00);
		assertEquals(0, getChargeForAfterBedTime(start, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		assertEquals(0, getChargeForAfterBedTime(start, time(22, 30), time(22, 30)));
		assertEquals(0, getChargeForAfterBedTime(start, time(22, 01), time(22, 45)));
		assertEquals(AFTER_BEDTIME_RATE, getChargeForAfterBedTime(start, time(22, 01), time(23, 45)));
		assertEquals(3 * AFTER_BEDTIME_RATE, getChargeForAfterBedTime(start, time(20, 30), LocalTime.MIDNIGHT));
		assertEquals(3 * AFTER_BEDTIME_RATE, getChargeForAfterBedTime(start, time(20, 30), time(1, 45)));
	}

	@Test
	public void testTotalChargeWithNoPostBedtimeCharge() {
		//all cases have no post-bedtime charge (nothing earned at the $8/rate)
		assertEquals(148, calculateTotalCharge(time(17, 00), LocalTime.MIDNIGHT, time(4, 00)));
		assertEquals(148, calculateTotalCharge(time(17, 59), LocalTime.MIDNIGHT, time(3, 01)));
		assertEquals(84, calculateTotalCharge(time(17, 59), LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		assertEquals(148, calculateTotalCharge(time(17, 59), time(23, 01), time(3, 01)));
		assertEquals(48, calculateTotalCharge(time(20, 00), time(23, 15), time(23, 45)));
	}

	@Test
	public void testTotalChargeWithNoPreBedtimeCharge() {
		//all cases have no pre-bedtime charge (nothing earned at the $12/rate)
		assertEquals(56, calculateTotalCharge(time(17, 00), time(17, 00), LocalTime.MIDNIGHT));
		assertEquals(72, calculateTotalCharge(time(17, 00), time(17, 00), LocalTime.MIDNIGHT.plusNanos(1)));
		assertEquals(72, calculateTotalCharge(time(17, 00), time(17, 00), time(1, 00)));
	}

	@Test
	public void testTotalChargeWithAllThreeChargeCategories() {
		assertEquals(76, calculateTotalCharge(time(17, 00), time(17, 01), time(1, 00)));
		assertEquals(100, calculateTotalCharge(time(19, 15), time(21, 30), time(2, 10)));
		assertEquals(32, calculateTotalCharge(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, time(1, 15)));
		assertEquals(44, calculateTotalCharge(time(20, 00), time(22, 15), time(23, 45)));
	}

	@Test
	public void testTotalChargesWithZeroOverallCharge() {
		assertEquals(0, calculateTotalCharge(time(17, 00), time(17, 00), time(17, 00)));
		assertEquals(0, calculateTotalCharge(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
	}

	/**
	 * A convenient method for creating a {@link LocalTime}.
	 * Based on a 24-hour day (e.g. 16 hours is 4pm)
	 */
	private static LocalTime time(int hour, int minute) {
		return LocalTime.of(hour, minute);
	}
}
