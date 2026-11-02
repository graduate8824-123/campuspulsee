package com.campuspulse.service;

import com.campuspulse.model.Category;

/**
 * Transparent Priority Calculation Engine.
 * Does NOT use AI.
 * 
 * Rules:
 * Reports < 3       -> LOW
 * Reports 3 - 9     -> MEDIUM
 * Reports >= 10     -> HIGH
 * 
 * If Category is Critical (Electricity, Water, Security), priority is elevated by one tier.
 */
public class PriorityService {

    public String calculatePriority(int reportCount, Category category, int ageHours) {
        String basePriority;

        if (reportCount >= 10) {
            basePriority = "HIGH";
        } else if (reportCount >= 3) {
            basePriority = "MEDIUM";
        } else {
            basePriority = "LOW";
        }

        // Critical category escalation
        if (category != null && category.isCritical()) {
            if ("LOW".equals(basePriority)) {
                basePriority = "MEDIUM";
            } else if ("MEDIUM".equals(basePriority)) {
                basePriority = "HIGH";
            }
        }

        // Aging escalation (older than 48 hours without progress)
        if (ageHours > 48 && "LOW".equals(basePriority)) {
            basePriority = "MEDIUM";
        }

        return basePriority;
    }
}
