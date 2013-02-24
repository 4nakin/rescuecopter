package com.badlogic.gdx.sionengine;

import com.badlogic.gdx.utils.TimeUtils;

public class Chronometer {
	
	public enum Format {
		Hours,
		Minutes,
		Seconds,
		Milliseconds,
	};
	
	public static class Time {
		int m_hours;
		int m_minutes;
		int m_seconds;
		int m_milliseconds;
		
		public Time(int hours, int minutes, int seconds, int milliseconds) {
			m_hours = hours;
			m_minutes = minutes;
			m_seconds = seconds;
			m_milliseconds = milliseconds;
		}
		
		public int getHours() {
			return m_hours;
		}
		
		public int getMinutes() {
			return m_minutes;
		}
		
		public int getSeconds() {
			return m_seconds;
		}
		
		public int getMilliseconds() {
			return m_milliseconds;
		}
		
		@Override
		public String toString() {
			return "time (" + m_hours + " : " + m_minutes + " : " + m_seconds + " : " + m_milliseconds + ")";
		}
	}
	
	private long m_startTime = 0;
	
	public void start() {
		m_startTime = TimeUtils.millis();
	}
	
	public void reset() {
		start();
	}
	
	public float getTime() {
		return getTimeInMilliseconds() / 1000.0f;
	}
	
	public float getTimeInMilliseconds() {
		long currentTime = TimeUtils.millis();
		long difference = currentTime - m_startTime;
		return (float)difference;
	}
	
	public void getTime(Time time, Format format) {
		float milliseconds = getTimeInMilliseconds();
		
		if (format == Format.Milliseconds) {
			time.m_milliseconds = (int)milliseconds;
			time.m_seconds = time.m_minutes = time.m_hours = 0;
			return;
		}
		
		float seconds = milliseconds / 1000.0f;
		milliseconds = milliseconds % 1000.0f;
		
		if (format == Format.Seconds) {
			time.m_milliseconds = (int)milliseconds;
			time.m_seconds = (int)seconds;
			time.m_minutes = time.m_hours = 0;
			return;
		}
		
		float minutes = seconds / 60.0f;
		seconds = seconds % 60.0f;
		
		if (format == Format.Minutes) {
			time.m_milliseconds = (int)milliseconds;
			time.m_seconds = (int)seconds;
			time.m_minutes = (int)minutes;
			time.m_hours = 0;
			return;
		}
		
		float hours = minutes / 60.0f;
		minutes = minutes % 60.0f;
		
		time.m_milliseconds = (int)milliseconds;
		time.m_seconds = (int)seconds;
		time.m_minutes = (int)minutes;
		time.m_hours = (int)hours;
	}
	
	@Override
	public String toString() {
		return "chronometer (" + getTime() + "s)";
	}
}
