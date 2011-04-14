package com.softwareprojects.androidtasks.domain;

import java.util.Calendar;
import java.util.Date;

public abstract class TaskDateCalculation {

	private long unitaryShiftInMillis;

	protected TaskDateCalculation(long unitaryShiftInMillis) {
		this.unitaryShiftInMillis = unitaryShiftInMillis;
	}

	protected long calculateShiftToApply(int requestedShift, long tresholdShift) {
		long appliedShift = requestedShift;

		if (tresholdShift >= requestedShift) {
			appliedShift = ((tresholdShift / requestedShift) + 1) * requestedShift;
		}

		return appliedShift;
	}

	protected int calculateMinimumShift(Calendar firstDate, Calendar secondDate) {

		long diffInMillis = Math.abs(secondDate.getTimeInMillis() - firstDate.getTimeInMillis());
		long numberOfShiftUnits = diffInMillis / unitaryShiftInMillis;

		return (int) numberOfShiftUnits;
	}

	protected abstract void applyShiftTo(final Calendar calendar, final int shift);

	public Date getNext(Date offset, TaskDateProvider dateProvider, int shift) {

		if (offset == null) {
			return null;
		}

		Calendar offsetCalendar = Calendar.getInstance();
		offsetCalendar.setTime(offset);

		Calendar now = dateProvider.getNow();
		
		if(now.before(offsetCalendar)){
			
			applyShiftTo(offsetCalendar, shift);
			return offsetCalendar.getTime();
		}

		long appliedShift = shift;

		if (offsetCalendar.before(now)) {
			
			int tresholdShift = calculateMinimumShift(offsetCalendar, now);
			appliedShift = calculateShiftToApply(shift, tresholdShift);
		}

		applyShiftTo(offsetCalendar, (int) appliedShift);
		return offsetCalendar.getTime();
	}
}
