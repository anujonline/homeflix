package com.homeflix.app.views;

import java.util.Date;


public record FeedbackData(Long id, Date currentDate, String timeZoneId, boolean touchDevice, String windowName) {
}
