---
name: babell-review-test
description: Review BABELL API tests for ownership, Cucumber isolation, reuse, and maintainability. Use for requested test or architecture reviews; report findings without editing files.
---

# Review a BABELL API test

Perform a read-only review of the requested test(s). Read [the review checklist](references/review-checklist.md) and the current [BABELL Maven architecture](../../docs/BABELL_CUCUMBER_MAVEN_ARCHITECTURE.md), then inspect the feature, matching Java Test Definition, reusable Steps, and related API/helper code as needed.

Report actionable findings first, ordered by severity, with file and line references, the violated rule, its impact, and a concise correction. Check for test ownership, isolation, duplicated behavior, and accidental low-level HTTP implementation in upper layers.

Do not modify files, auto-fix findings, or broaden the review beyond the requested tests. If no issues are found, state that explicitly and summarize the checks performed.
