package com.rick.ecatalogs.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.rick.ecatalogs.lint.designsystem.DesignSystemDetector

/**
 * Registers the E-Catalogs lint checks.
 */
class EcsIssueRegistry : IssueRegistry() {

    override val issues = listOf(
        DesignSystemDetector.ISSUE,
        TestMethodNameDetector.FORMAT,
        TestMethodNameDetector.PREFIX,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "E-Catalogs",
        feedbackUrl = "https://github.com/rColeJnr/E-Catalog/issues",
        contact = "https://github.com/rColeJnr/E-Catalog",
    )
}
