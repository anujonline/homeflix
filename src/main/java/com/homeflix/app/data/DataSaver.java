package com.homeflix.app.data;

import com.homeflix.app.data.controllers.AdminController;
import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.data.service.VideoFile;
import com.homeflix.app.views.common.MaintainHistory;
import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSaver {
    private final AdminController adminController;
    private final MaintainHistory maintainHistory;

    public void saveData(UI ui, VideoData videoFile) {
        try {
            maintainHistory.addToList(videoFile);
            var pendingJavaScriptResult = ui.getPage().executeJs("""
                    return $.getJSON('https://json.geoiplookup.io/?callback=?', function(data) {
                        JSON.stringify(data, null, 2);
                    });
                    """, "");
            pendingJavaScriptResult.then(jsonValue -> adminController.addHistory(videoFile.title() + " " + ui.getSession().getBrowser().getBrowserApplication(), jsonValue.toJson()));
        } catch (Exception e) {
            log.error("Exception in running js ", e);
        }
    }

    public void saveData(UI ui, VideoFile videoFile) {
        try {
            var pendingJavaScriptResult = ui.getPage().executeJs("""
                    return $.getJSON('https://json.geoiplookup.io/?callback=?', function(data) {
                        JSON.stringify(data, null, 2);
                    });
                    """, "");
            pendingJavaScriptResult.then(jsonValue -> adminController.addHistory("browse " + videoFile.getName() + " " + ui.getSession().getBrowser().getBrowserApplication(), jsonValue.toJson()));
        } catch (Exception e) {
            log.error("Exception in running js ", e);
        }
    }
}
