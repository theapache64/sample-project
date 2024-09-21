#!/usr/bin/env kotlin

main()
fun main() {
    val prBody = getPRBodyOrCrash()
    val errorList = mutableListOf<String>()

    // Validating PR type section
    validatePRType(prBody, errorList)
    // Validating PR description
    validateDescription(prBody, errorList)
    // Validating mandatory PR checklist item
    validateMandatoryChecklistItems(prBody, errorList)

    if (errorList.isNotEmpty()) {
        println()
        error(
            errorList.joinToString(
                separator = "\n",
                prefix = "PR template check failed",
                transform = { line ->
                    "${errorList.indexOf(line) + 1}.  $line"
                }
            )
        )
    } else {
        println("✅ PR body looks good!!!")
    }
}

fun getSectionWithoutHeading(prBody: String, heading: String): String? {
    val headingPattern = "^##".toRegex(setOf(RegexOption.MULTILINE))
    return prBody.split(headingPattern)
        .map { it.trim() }
        .find { section ->
            val secLines = section.lines()
            val sectionHeading = secLines.firstOrNull()
            sectionHeading == heading
        }
        ?.lines()
        ?.drop(1) // drop heading
        ?.joinToString(separator = "\n")
        ?.trim()
}


fun validatePRType(prBody: String, errorList: MutableList<String>) {
    val prTypeValue = getSectionWithoutHeading(prBody, "PR Type")
    if (prTypeValue == null) {
        errorList.appendSectionMissing("PR Type")
    } else {
        val selectedPrTypesCount = prTypeValue
            .lines()
            .filter { it.startsWith("- ") }
            .size

        if (selectedPrTypesCount != 1) {
            errorList.add("$selectedPrTypesCount PR type found, expected only 1")
        }
    }
}

fun validateMandatoryChecklistItems(prBody: String, errorList: MutableList<String>) {
    val checkListValue = getSectionWithoutHeading(prBody, "Checklist")
    if (checkListValue == null) {
        errorList.appendSectionMissing("Checklist")
    } else {
        val mandatoryItems = checkListValue
            .lines()
            .map { line -> line.trim() }
            .filter { line -> line.endsWith("(mandatory)") }

        for (mandatoryItem in mandatoryItems) {
            if (!mandatoryItem.startsWith("- [x]")) {
                errorList.add("Checklist mandatory item not checked properly: ($mandatoryItem)")
            }
        }
    }
}

fun getPRBodyOrCrash(): String {
    val isDebug = true
    var prBody: String? = System.getenv("PR_BODY")
    prBody = if (prBody == null && isDebug) {
        println("Providing dummy PR body...")
        """
        <!-- Comment on this document to propose changes: https://my-company.atlassian.net/wiki/spaces/HP2/pages/1588396037/Github+PR+template --> 
        ## PR Type
        Please keep the type of change your PR introduces and remove others
        - FEATURE - Addition of functionality
        - FEATURE - Addition of functionality
        - FEATURE - Addition of functionality
        - FEATURE - Addition of functionality

        ## PR Checks
        Please add a comment `run test` to run maestro and UI tests. Check [here](https://my-company.slack.com/archives/C01SUJ1CDFF/p1684930174247179) for more details
        ## Why more than 500 lines of code?
        <!-- 
        Please explain why your PR is more than 500 lines of code. 
        Sample : https://github.com/my-company/my-company-android-mobile/pull/5833
        Remove this section if your PR is less than 500 lines of code. 
        -->
        ## Description
        
        
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

    return prBody
}

fun validateDescription(prBody: String, errorList: MutableList<String>) {
    val description = getSectionWithoutHeading(prBody, "Description")
    if (description.isNullOrEmpty()) {
        errorList.appendSectionMissing("Description")
    } else if (description.startsWith("<!--") && description.endsWith("-->")) {
        errorList.add("Description looks empty - ($description)")
    }
}

fun MutableList<String>.appendSectionMissing(heading: String) {
    add("'$heading' section is missing from PR body ")
}
