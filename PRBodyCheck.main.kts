#!/usr/bin/env kotlin

import java.io.File

val isDebug = true
var prBody: String? = System.getenv("PR_BODY")
println("QuickTag: :hello: ")
prBody = if (prBody == null && isDebug) {
    println("Providing dummy PR body...")
    """
        <!-- Comment on this document to propose changes: https://my-company.atlassian.net/wiki/spaces/HP2/pages/1588396037/Github+PR+template --> 
        ## PR Type
        Please keep the type of change your PR introduces and remove others
        - FEATURE - Addition of functionality
        - BUG FIX - Fixes existing functionality
        - CODE REFACTOR & CLEANUP - Removing unused configurations or code cleaning
        - PERF - Performance improvement
        - TESTS - Only contains changes and additions to tests
        - RELEASE - App version upgrade etc.
        - OTHER - README update, PR template update, etc

        ## PR Checks
        Please add a comment `run test` to run maestro and UI tests. Check [here](https://my-company.slack.com/archives/C01SUJ1CDFF/p1684930174247179) for more details

        ## Why more than 500 lines of code?
        <!-- 
        Please explain why your PR is more than 500 lines of code. 
        Sample : https://github.com/my-company/my-company-android-mobile/pull/5833
        Remove this section if your PR is less than 500 lines of code. 
        -->

        ## Description
        <!-- Please explain your changes -->

        ## Documents and other resources
        <!-- Link to Engineering Doc, Zeplin links, Product Doc and others -->

        ## Other Impacted Features (side effects)
        <!-- For Example:
             Continue Watching 
             Subscription Paywall
             Billboard Ads -->

        ## Checklist
        - [ ] My PR only includes one change. There are no additional changes mixed in this single PR (mandatory)
        - [ ] I have made corresponding changes to the documentation
        - [ ] I have tested on a release build
        - [ ] I have tested this change with leak canary enabled for any leaks
        - [ ] I have got the design reviewed from the design team
        - [ ] This change is config controlled

        ##  For SDETs Only
        - [ ] E2E Test cases are covered for the PR

        ## Testing
        Slack Link to the build in #android-my-company-x-build channel from GoCD pipeline:
        <!-- For example https://my-company.slack.com/archives/CD7Q1L7J7/p1589185464304800 -->

        <!-- Guide to using pipeline: https://my-company.atlassian.net/wiki/spaces/HP2/pages/1676641409/Android+Pipelines+Overview -->
        <!-- Note: The link to the build should have the changes enabled by default. -->

        ## Before / After Change Screenshots
        <!-- For UI/UX changes. Can be Gif / screenshot. -->

        ## Is additional manual testing needed?
        - [ ] Yes
        - [ ] No
    """.trimIndent()
} else {
    prBody
}?.trim()

if (prBody.isNullOrEmpty()) {
    error("prBody is emptyOrNull -> '$prBody'")
}

val prTemplate = File(".github/PULL_REQUEST_TEMPLATE.md").readText().trim()
val errorBuilder = StringBuilder()

if (prTemplate == prBody){
    error("PR template is unfilled. Please fill it properly before asking for a review.")
}