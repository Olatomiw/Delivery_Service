package com.deliverywindow.deliveryservice.web.service;

import java.util.List;
import java.util.Map;

public interface ServiceClass {

    Map<String, List<String>> deliveryInterSection(String venueId, String citySlug);
}
