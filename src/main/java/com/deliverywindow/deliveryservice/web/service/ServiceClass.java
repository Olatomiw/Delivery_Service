package com.deliverywindow.deliveryservice.web.service;

import com.deliverywindow.deliveryservice.web.domain.Window;
import com.deliverywindow.deliveryservice.web.domain.WindowResponse;

import java.util.List;
import java.util.Map;

public interface ServiceClass {

    Map<String, String> deliveryInterSection(String venueId, String citySlug);
}
