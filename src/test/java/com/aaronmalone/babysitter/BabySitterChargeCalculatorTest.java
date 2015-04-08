package com.aaronmalone.babysitter;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;

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
				BabySitterChargeCalculator.calculateNightlyCharge(startTime, bedTime, endTime);
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
			BabySitterChargeCalculator.calculateNightlyCharge(startTime, invalidBedTime, endTime);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage().contains("Bed time is before start time."));
			// we expected this
		}
		LocalTime earlierStartTime = time(17, 10);
		BabySitterChargeCalculator.calculateNightlyCharge(earlierStartTime, invalidBedTime, endTime);
	}

	@Test
	public void testThatEndTimeNotBeforeBedTime() {
		LocalTime startTime = time(18, 00);
		LocalTime bedTime = time(20, 00);
		LocalTime invalidEndTime = time(19, 00);
		try {
			BabySitterChargeCalculator.calculateNightlyCharge(startTime, bedTime, invalidEndTime);
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
				BabySitterChargeCalculator.calculateNightlyCharge(startTime, bedTime, endTime);
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
				BabySitterChargeCalculator.calculateNightlyCharge(startTime, bedTime, endTime);
				Assert.fail();
			} catch (IllegalArgumentException e) {
				// we expected this
				Assert.assertTrue(e.getMessage().contains("Bed time is after midnight: "));
			}
		}
	}

	@Test
	public void testBetweenInclusive() {
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), LocalTime.MIDNIGHT);
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), time(4, 00));
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), time(0, 30));
		expectBetweenInclusiveReturnsTrue(LocalTime.MIDNIGHT, time(4, 00), time(3, 30));
		expectBetweenInclusiveReturnsFalse(LocalTime.MIDNIGHT, time(4, 00), time(4, 10));
		expectBetweenInclusiveReturnsFalse(LocalTime.MIDNIGHT, time(4, 00), time(5, 30));
		expectBetweenInclusiveReturnsFalse(LocalTime.MIDNIGHT, time(4, 00), time(23, 00));
	}

	private void expectBetweenInclusiveReturnsTrue(LocalTime begin, LocalTime end, LocalTime timeToTest) {
		boolean returnValue = BabySitterChargeCalculator.betweenInclusive(begin, end, timeToTest);
		Assert.assertTrue(returnValue);
	}

	private void expectBetweenInclusiveReturnsFalse(LocalTime begin, LocalTime end, LocalTime timeToTest) {
		boolean returnValue = BabySitterChargeCalculator.betweenInclusive(begin, end, timeToTest);
		Assert.assertTrue(!returnValue);
	}

	/**
	 * A really short way to get a {@link LocalTime}.
	 * Based on a 24-hour day (e.g. 16 hours is 4pm)
	 */
	private static LocalTime time(int hour, int minute) {
		return LocalTime.of(hour, minute);
	}
}
