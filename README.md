# MSxpo Create-Event Automation (Katalon)

Automation of the **MSxpo Event Host super-admin create-event flow** on `https://app.msxpo.sa`,
built from the QA reports in the parent folder
(`create-event-flow-report.md`, `create-event-test-case-report.md`, `login-test-report.md`).

Test case under automation: create event **`test 1.0`** — Trade Show, 2026-11-01 (single day,
Asia/Kolkata), media + 37-line description, Featured + all 4 public stats ON, Technology industry,
admin *test admin* (from directory), required questionnaire with 1 Text + 1 Media question,
1 hall (243×129 m, Perimeter) with 30 booths (10 basic / 10 standard / 10 premium, all priced 50),
5 banners @ 50 + 5 decorations — saved as **Draft**.

## Project layout

```
katalon-create-event/
├── Keywords/msxpo/
│   ├── Selectors.groovy      # every UI selector, captured from live DOM 2026-10-26 — one place to fix on UI change
│   ├── AppKeywords.groovy    # reusable steps + all F# workarounds
│   └── Config.groovy         # settings resolution (see below)
├── Test Data/
│   ├── EventData.csv         # Step 1 — Event Basics row
│   ├── Admins.csv            # Step 2 — Event Admin row
│   ├── Questionnaire.csv     # Step 3 — questionnaire + 2 questions
│   ├── FloorPlan.csv         # Step 4 — hall/tiers/banner/decoration counts
│   └── description.txt       # 37-line event description (fidelity check F8)
├── Test Cases/
│   ├── TC_01_Login.glue
│   ├── TC_02_EventBasics.glue
│   ├── TC_03_EventAdmin.glue
│   ├── TC_04_Questionnaire.glue
│   ├── TC_05_FloorPlan.glue
│   ├── TC_06_Review_SaveDraft.glue
│   └── TC_07_ApproveParticipant.glue  # standalone — own login, not part of the flow suite
└── Test Suites/
    ├── TS_CreateEvent_Flow.ts    # TC_01 → TC_06 in order, stop on first failure
    └── TS_ApproveParticipant.ts  # TC_07 on its own — independent of the create-event flow
```

## TC_07 — Approve Participant (standalone)

`Test Cases/TC_07_ApproveParticipant` handles **Manage Participant → Approve Participant**:
it reads the participant name from `Test Data/Participants.csv` (column `Name`) and clicks the
**Approve button of that name's row only** — the XPath is anchored on the exact name text and
walks up to the enclosing row/card, so other pending rows are never touched. An optional
confirmation dialog is handled (scoped to `role=dialog`), and the test asserts afterwards that
this row stopped offering Approve while the total Approve-button count dropped by exactly one.

Run it through its own suite `Test Suites/TS_ApproveParticipant` (or open the test case and press
Run — it logs in itself); it is not in `TS_CreateEvent_Flow` because that suite drives the
create-event wizard in one shared session. The named participant must be **pending**
on the list, and the correct event must be selected in the page's event filter if the list is
event-scoped. Unlike the rest of the suite, its selectors are **text-anchored, not DOM-captured**
— if the app renames "Manage Participant" / "Approve Participant" / "Approve", adjust them in
`Selectors.groovy` (one place).

## Import & run

1. **Import**: Katalon Studio 8+ → *File ▸ Import ▸ Existing Katalon Project* and select this folder
   (or copy its four folders into an empty Katalon project). No profile (`.prfs`) setup is required —
   the suite runs on defaults.
2. **Open** `Test Suites/TS_CreateEvent_Flow` and press **Run** (Chrome by default).
   The suite executes TC_01→TC_06 in **one browser session**; each test case continues the wizard
   the previous one left open, so don't re-order or run test cases individually except TC_01.
3. Optional overrides (highest priority first — see `Config.groovy`):

   | Setting        | GlobalVariable | JVM property                | Default                                      |
   |----------------|----------------|-----------------------------|----------------------------------------------|
   | Base URL       | `BASE_URL`     | `-Dmsxpo.baseUrl=`          | `https://app.msxpo.sa`                       |
   | Admin email    | `ADMIN_EMAIL`  | `-Dmsxpo.adminEmail=`       | `aartioza2026+fgate@gmail.com`               |
   | Admin password | `ADMIN_PASSWORD` | `-Dmsxpo.adminPassword=`  | `Admin@123`                                  |
   | Media file     | `MEDIA_FILE`   | `-Dmsxpo.mediaFile=`        | local `growtech/media/*.jpg` — **override this on any other machine** |

   Katalon CLI example:
   ```
   katalon -noSplash -runSuite:"Test Suites/TS_CreateEvent_Flow.ts" ^
     -browserType="Chrome" -Dmsxpo.mediaFile="C:/path/to/image.jpg"
   ```
4. **Aftermath**: each green run leaves one draft event `test 1.0` in *Events* — delete it manually
   (the app has no API exposed to the test for cleanup).

## Workarounds for known app findings

The suite is designed to pass **despite** the verified app defects; each workaround is tagged `[F#]`
in the code. Mapping (flow-report findings unless noted):

| # | Severity | App defect | Automation workaround |
|---|----------|------------|----------------------|
| F1 | High | Featured/stats switches silently drop on persist; only visible at Review | `verifyVisibilityFlagsIntact()` — TC_06 checks the Review summary; if lost, uses the Edit shortcut to re-toggle and walks Continue ×4 back to Review |
| F2 | High | Banners/decorations spawn stacked at (2,2) with empty price | `addBannerAt()`/`addDecorationAt()` set X/Y (and price) immediately after each spawn; all coords are multiples of 3, banners placed before decorations so the missing price field identifies a decoration selection |
| F3 | Medium | Validation counter/toasts are stale | `validateAndSaveConfiguration()` trusts only the post-check persisted state, never the live counter |
| F4 | Medium | Undo can wipe the whole hall | Undo is never used |
| F6 | Low | Canvas silently snaps coords to a 3 m grid | every coordinate used is a multiple of 3 |
| F5 | — | Only Text and Media question types exist; toggle OFF hides the builder | questionnaire built with exactly those two types; requiredness asserted per question |
| F7 (login) | — | Admin-directory search ignores paste | `addAdminFromDirectory()` types keystroke-by-keystroke, clicks the suggestion with a trusted click, and asserts the committed email |
| F8 (create-test) | — | Description line count / flags / stats fidelity | TC_02 asserts 37 lines, Featured + all 4 stats ON, media attached; public-stats sanity re-checked at Review |

Also handled: the event-type/industry `<select>` elements are hidden shims (we drive the visible
Radix dropdowns and click `[role=option]`), and the check button is matched via a
`ConfigurationCheckSave` normalization because its label is dynamic.

## Known limitations

- **Step 3 (questionnaire) is not shown on Review** (flow-report F3) — by design of the app, so TC_04
  verifies it on its own step; the Review check only covers event, admin and floor plan.
- The floor-plan editor is a **Konva canvas** — booth/banner positions are verified through the
  properties panel (X/Y/Price fields) and the validator result, not through canvas pixels.
- TC_05's asset coordinates are hard-coded valid positions (grid-snapped, non-overlapping); counts
  and prices come from `FloorPlan.csv`, positions do not.
- One run = one draft event; there is no automated cleanup (no delete API path exercised).
