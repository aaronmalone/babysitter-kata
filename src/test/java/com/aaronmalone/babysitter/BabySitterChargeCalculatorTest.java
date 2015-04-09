package com.aaronmalone.babysitter;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;

import static com.aaronmalone.babysitter.BabySitterChargeCalculator.*;

@SuppressWarnings("OctalInteger") //so we can use "00" for minutes
public class BabySitterChargeCalculatorTest {

	/**
	 * Test that an {@link java.lang.IllegalArgumentException} is thrown if an
	 * invalid start time is used.
	 */
	@Test
	public void testStartTimeNotBefore5PM() {
		LocalTime[] invalidStartTimes = {time(3, 00), time(4, 00), time(5, 00), time(12, 00), time(16, 59)};
		for (LocalTime startTime : invalidStartTimes) {
			LocalTime bedTime = startTime.plusHours(1);
			LocalTime endTime = startTime.plusHours(2);
			try {
				BabySitterChargeCalculator.checkArguments(startTime, bedTime, endTime);
				Assert.fail();
			} catch (IllegalArgumentException e) {
				// we expected this
			}
		}
	}

	/**
	 * Test that an {@link java.lang.IllegalArgumentException} is thrown if
	 * the bed time is before the start time.
	 */
	@Test
	public void testThatBedTimeNotBeforeStartTime() {
		LocalTime startTime = time(18, 00);
		LocalTime invalidBedTime = time(17, 15);
		LocalTime endTime = time(23, 00);
		try {
			BabySitterChargeCalculator.checkArguments(startTime, invalidBedTime, endTime);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage().contains("Bed time is before start time."));
			// we expected this
		}
		LocalTime earlierStartTime = time(17, 10);
		BabySitterChargeCalculator.checkArguments(earlierStartTime, invalidBedTime, endTime);
	}

	@Test
	public void testThatEndTimeNotBeforeBedTime() {
		LocalTime startTime = time(18, 00);
		LocalTime bedTime = time(20, 00);
		LocalTime invalidEndTime = time(19, 00);
		try {
			BabySitterChargeCalculator.checkArguments(startTime, bedTime, invalidEndTime);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("End time is before bed time.", e.getMessage());
			// we expected this
		}
	}

	@Test
	public void testThatEndTimeBefore4AM() {
		LocalTime startTime = time(17, 00);
		LocalTime bedTime = time(21, 00);
		LocalTime[] invalidEndTimes = {time(4, 10), time(5, 00), time(11, 00), time(16, 00)};
		for (LocalTime endTime : invalidEndTimes) {
			try {
				BabySitterChargeCalculator.checkArguments(startTime, bedTime, endTime);
				Assert.fail();
			} catch (IllegalArgumentException e) {
				// we expected this
				Assert.assertTrue(e.getMessage().contains("End time is after 4am: "));
			}
		}
	}

	@Test
	public void testThatBedTimeNotAfterMidnight() {
		LocalTime startTime = time(17, 00);
		LocalTime endTime = time(4, 00);
		LocalTime[] invalidBedTimes = {time(0, 15), time(1, 00), time(3, 59)};
		for (LocalTime bedTime : invalidBedTimes) {
			try {
				BabySitterChargeCalculator.checkArguments(startTime, bedTime, endTime);
				Assert.fail();
			} catch (IllegalArgumentException e) {
				// we expected this
				Assert.assertTrue(e.getMessage().contains("Bed time is after midnight: "));
			}
		}
	}

	@Test
	public void testBetweenInclusive() {
		//test between midnight and 4am
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), LocalTime.MIDNIGHT);
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), time(4, 00));
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), time(0, 30));
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), time(3, 30));
		expectBetweenInclusiveReturnsFalse(LocalTime.MIDNIGHT, time(4, 00), time(4, 10));
		expectBetweenInclusiveReturnsFalse(LocalTime.MIDNIGHT, time(4, 00), time(5, 30));
		expectBetweenInclusiveReturnsFalse(LocalTime.MIDNIGHT, time(4, 00), time(23, 00));

		//test between 5pm and LocalTime.MAX
		expectBetweenInclusiveReturnsTrue(time(17, 00), LocalTime.MAX, time(17, 00));
		expectBetweenInclusiveReturnsTrue(time(17, 00), LocalTime.MAX, LocalTime.MAX);
		expectBetweenInclusiveReturnsTrue(time(17, 00), LocalTime.MAX, time(22, 30));
		expectBetweenInclusiveReturnsFalse(time(17, 00), LocalTime.MAX, LocalTime.MIDNIGHT);
		expectBetweenInclusiveReturnsFalse(time(17, 00), LocalTime.MAX, LocalTime.NOON);
		expectBetweenInclusiveReturnsFalse(time(17, 00), LocalTime.MAX, time(0, 30));
		expectBetweenInclusiveReturnsFalse(time(17, 00), LocalTime.MAX, time(4, 00));
		expectBetweenInclusiveReturnsFalse(time(17, 00), LocalTime.MAX, time(16, 00));
	}

	private void expectBetweenInclusiveReturnsTrue(LocalTime begin, LocalTime end, LocalTime timeToTest) {
		boolean returnValue = BabySitterChargeCalculator.betweenInclusive(begin, end, timeToTest);
		Assert.assertTrue(returnValue);
	}

	private void expectBetweenInclusiveReturnsFalse(LocalTime begin, LocalTime end, LocalTime timeToTest) {
		boolean returnValue = BabySitterChargeCalculator.betweenInclusive(begin, end, timeToTest);
		Assert.assertTrue(!returnValue);
	}

	@Test
	public void testHoursAfterMidnightMethodWithTimesNotAfterMidnight() {
		//note: static import of method to save space
		LocalTime[] timesBeforeMidnight = {LocalTime.MAX, time(23, 00), time(17, 00)};
		for (LocalTime endTime : timesBeforeMidnight) {
			Assert.assertEquals(0, hoursAfterMidnight(endTime));
		}
		Assert.assertEquals(0, hoursAfterMidnight(LocalTime.MIDNIGHT));
	}

	@Test
	public void testHoursBeforeMidnight() {
		//note: static import of method to save space
		Assert.assertEquals(0, hoursBeforeMidnight(LocalTime.MIDNIGHT));
		Assert.assertEquals(1, hoursBeforeMidnight(time(23, 59)));
		Assert.assertEquals(1, hoursBeforeMidnight(LocalTime.MAX));
		Assert.assertEquals(4, hoursBeforeMidnight(time(20, 00)));
		Assert.assertEquals(5, hoursBeforeMidnight(time(19, 59)));
		Assert.assertEquals(12, hoursBeforeMidnight(LocalTime.NOON));
	}

	@Test
	public void testHoursAfterMidnight() {
		//note: static import of method to save space
		Assert.assertEquals(0, hoursAfterMidnight(LocalTime.MIDNIGHT));
		Assert.assertEquals(1, hoursAfterMidnight(LocalTime.MIDNIGHT.plusNanos(1)));
		Assert.assertEquals(1, hoursAfterMidnight(time(1, 00).minusNanos(1)));
		Assert.assertEquals(1, hoursAfterMidnight(time(1, 00)));
		Assert.assertEquals(2, hoursAfterMidnight(time(1, 00).plusNanos(1)));
		Assert.assertEquals(2, hoursAfterMidnight(time(2, 00).minusNanos(1)));
		Assert.assertEquals(3, hoursAfterMidnight(time(2, 00).plusNanos(1)));
		Assert.assertEquals(3, hoursAfterMidnight(time(3, 00)));
		Assert.assertEquals(4, hoursAfterMidnight(time(3, 00).plusNanos(1)));
		Assert.assertEquals(4, hoursAfterMidnight(time(4, 00)));
	}

	@Test
	public void testHoursPreBedTime() {
		//note: static import of method to save space
		Assert.assertEquals(0, hoursPreBedTime(time(17, 00), time(17, 00)));
		Assert.assertEquals(1, hoursPreBedTime(time(17, 00), time(17, 10)));
		Assert.assertEquals(1, hoursPreBedTime(time(17, 00), time(18, 00)));
		Assert.assertEquals(2, hoursPreBedTime(time(17, 00), time(18, 10)));
		Assert.assertEquals(2, hoursPreBedTime(time(17, 59), time(18, 10)));

		//midnight cases
		Assert.assertEquals(0, hoursPreBedTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		Assert.assertEquals(1, hoursPreBedTime(LocalTime.MAX, LocalTime.MIDNIGHT));
		Assert.assertEquals(4, hoursPreBedTime(time(20, 00), LocalTime.MIDNIGHT));
	}

	@Test
	public void testHourPostBedTime() {
		//pre-midnight end time cases
		Assert.assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), LocalTime.MAX));
		Assert.assertEquals(4, hoursPostBedTime(time(17, 00), time(20, 00), LocalTime.MAX));
		Assert.assertEquals(3, hoursPostBedTime(time(17, 00), time(20, 01), LocalTime.MAX));

		//post-midnight end time cases
		Assert.assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), LocalTime.MIDNIGHT));
		Assert.assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), time(2, 15)));
		Assert.assertEquals(0, hoursPostBedTime(time(17, 00), time(23, 10), time(4, 00)));
		Assert.assertEquals(1, hoursPostBedTime(time(17, 00), time(22, 59), time(4, 00)));
		Assert.assertEquals(2, hoursPostBedTime(time(17, 00), time(22, 00), time(4, 00)));
	}

	@Test
	public void testChargeForAfterMidnight() {
		LocalTime[] preMidnightEndTimes = {time(17, 00), time(22, 00), LocalTime.MAX};
		for (LocalTime endTime : preMidnightEndTimes) {
			Assert.assertEquals(0, getChargeForAfterMidnight(endTime));
		}
		Assert.assertEquals(0, getChargeForAfterMidnight(LocalTime.MIDNIGHT));
		Assert.assertEquals(AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(LocalTime.MIDNIGHT.plusNanos(1)));
		Assert.assertEquals(AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(0, 59)));
		Assert.assertEquals(AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(1, 00)));
		Assert.assertEquals(2 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(1, 15)));
		Assert.assertEquals(2 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(2, 00)));
		Assert.assertEquals(4 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(4, 00).minusNanos(1)));
		Assert.assertEquals(4 * AFTER_MIDNIGHT_RATE, getChargeForAfterMidnight(time(4, 00)));
	}

	@Test
	public void testChargeForBeforeBedTime() {
		Assert.assertEquals(0, getChargeForBeforeBedTime(time(17, 00), time(17, 00)));
		Assert.assertEquals(0, getChargeForBeforeBedTime(time(21, 00), time(21, 00)));
		Assert.assertEquals(0, getChargeForBeforeBedTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		Assert.assertEquals(BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(17, 00).plusNanos(1)));
		Assert.assertEquals(BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(18, 00)));
		Assert.assertEquals(2 * BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(18, 01)));
		Assert.assertEquals(3 * BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(17, 00), time(19, 01)));
		Assert.assertEquals(4 * BEFORE_BEDTIME_RATE, getChargeForBeforeBedTime(time(20, 30), LocalTime.MIDNIGHT));
	}

	@Test
	public void testGetChargeForAfterBedTime() {
		LocalTime start = time(18, 00);
		Assert.assertEquals(0, getChargeForAfterBedTime(start, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT));
		Assert.assertEquals(0, getChargeForAfterBedTime(start, time(22, 30), time(22, 30)));
		Assert.assertEquals(0, getChargeForAfterBedTime(start, time(22, 01), time(22, 45)));
	}

	/**
	 * A really short way to get a {@link LocalTime}.
	 * Based on a 24-hour day (e.g. 16 hours is 4pm)
	 */
	private static LocalTime time(int hour, int minute) {
		return LocalTime.of(hour, minute);
	}
}
