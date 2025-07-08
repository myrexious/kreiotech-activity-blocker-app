package com.example.kreiotech.activityblocker

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ActivityBlockerAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "ActivityBlockerService"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        // Will need to log to get the unique identifier for Reels button
        private const val REELS_CONTENT_DESCRIPTION = "Reels"
        private const val EXPLORE_CONTENT_DESCRIPTION = "Search and explore"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Check if blocker is enabled in SharedPreferences
        val prefs = getSharedPreferences("blocker_prefs", MODE_PRIVATE)
        val isInstagramPackage = event.packageName?.toString() == INSTAGRAM_PACKAGE
        val node = rootInActiveWindow

        // If Explore blocker is enabled, block the Explore page
        val isInstagramExploreBlockerEnabled = prefs.getBoolean("block_instagram_explore_enabled", false)
        Log.d(TAG, "isInstagramExploreBlockerEnabled: $isInstagramExploreBlockerEnabled")
        if (isInstagramPackage && isInstagramExploreBlockerEnabled) {
            if (blockPage(EXPLORE_CONTENT_DESCRIPTION, isInstagramExploreBlockerEnabled, node)) return
        }

        // If Reels blocker is enabled, block the Reels page
        val isInstagramReelsBlockerEnabled = prefs.getBoolean("block_instagram_reels_enabled", false)
        Log.d(TAG, "isInstagramReelsBlockerEnabled: $isInstagramReelsBlockerEnabled")
        if (isInstagramPackage && isInstagramReelsBlockerEnabled) {
            blockPage(REELS_CONTENT_DESCRIPTION, isInstagramReelsBlockerEnabled, node)
            return
        }
    }

    private fun blockPage(searchedContentDesc: String, config: Boolean, node: AccessibilityNodeInfo?): Boolean {
        if (node == null || !config) return false

        // Check if the node is the Reels button
        if (existNodeByContentDescriptionAndSelected(searchedContentDesc, node)) {
            // Perform the blocking action, e.g., click the back button
            performGlobalAction(GLOBAL_ACTION_BACK)
            return true
        }
        return false
    }

    private fun existNodeByContentDescriptionAndSelected(searchedContentDesc: String, node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        if (searchedContentDesc == node.contentDescription && node.isSelected) return true
        for (i in node.childCount - 1 downTo 0) {
            if (existNodeByContentDescriptionAndSelected(searchedContentDesc, node.getChild(i))) return true
        }
        return false
    }

    override fun onInterrupt() {
        // Required method - leave empty
    }
}