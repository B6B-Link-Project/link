package com.link.back.entity;

import java.time.LocalTime;

public enum ScheduleTime {
	HOUR_00, HOUR_01, HOUR_02, HOUR_03, HOUR_04, HOUR_05,
	HOUR_06, HOUR_07, HOUR_08, HOUR_09, HOUR_10, HOUR_11,
	HOUR_12, HOUR_13, HOUR_14, HOUR_15, HOUR_16, HOUR_17,
	HOUR_18, HOUR_19, HOUR_20, HOUR_21, HOUR_22, HOUR_23;

	public LocalTime toLocalTime() {
		return LocalTime.of(this.ordinal(), 0);
	}
}