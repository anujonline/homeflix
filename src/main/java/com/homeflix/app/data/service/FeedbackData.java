package com.homeflix.app.data.service;

import java.util.Date;


public record FeedbackData(Long id, Date currentDate, String timeZoneId, boolean touchDevice, String windowName) {
}
