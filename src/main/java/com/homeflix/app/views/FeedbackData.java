package com.homeflix.app.views;

import java.util.Date;


record FeedbackData(Long id, Date currentDate, String timeZoneId, boolean touchDevice, String windowName) {
}
