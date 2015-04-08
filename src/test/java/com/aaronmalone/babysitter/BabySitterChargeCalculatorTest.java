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

	/**
	 * A really short way to get a {@link LocalTime}.
	 * Based on a 24-hour day (e.g. 16 hours is 4pm)
	 */
	private static LocalTime time(int hour, int minute) {
		return LocalTime.of(hour, minute);
	}
}
