# SRS for Booktrack

Version 1.5

Prepared by: Valerio Gianella

Date: 01.08.2025

### Table of Contents

1.  Introduction
    1.1. Purpose
    1.2. Document Conventions
2.  Overall Description
    2.1. Product Perspective and Context
    2.2. Product Functions
    2.3. User Classes and Characteristics
3.  System Features
    3.1. System Feature 1: Book Management
    3.2. System Feature 2: Reading Session Timer
    3.3. System Feature 3: Data Export
    3.4. System Feature 4: Statistics View
    3.5. System Feature 5: Settings
4.  External Interface Requirements
    4.1. User Interfaces
    4.2. Software Interfaces
5.  Non-Functional Requirements
    5.1. Performance Requirements
    5.2. Software Quality Attributes
    5.3. Business Rules

### 1. Introduction

#### 1.1. Purpose

Booktrack is an application that helps the user to manage his library and keep track of his reading time, by logging the time that was spent reading.

#### 1.2. Document Conventions

We refer to Booktrack as the application, the app, the software, or the program, interchangeably.

### 2. Overall Description

#### 2.1. Product Perspective and Context

This product is a new, standalone android mobile application.

#### 2.2. Product Functions

* Registration of a new book.
* Management of the registered books (modify, delete, change status of an existing book).
* Ability to start and stop a timer connected to a specific book that has been previously registered by the user. The recorded time is saved, together with the information of the book to which it was connected.
* Visualization of Historical Data (reading history)
* User Profile Settings Management

#### 2.3. User Classes and Characteristics

* General User: This is the only profile using the app. The user doesn't need to login.

### 3. System Features

#### 3.1 Feature: Book Management

* Description: Users must be able to add, view, modify, and delete books in their personal library. This library is the central repository for all their reading material.
* Functional Requirements:
    * REQ-1.1: The system shall provide a form for the user to manually add a new book. The form must capture:
        * Title (Text, Mandatory)
        * Author (Text, Mandatory)
        * Total Pages (Integer, Optional)
        * Cover Image (If no image is provided, generate a simple cover with the book's title and author's initials).
    * REQ-1.2: Upon creation, a book's status shall default to "Active".
    * REQ-1.3: The system shall allow the user to open a dedicated edit screen for any existing book by tapping a "Modify" icon on the book's tile.
    * REQ-1.4: On the book's edit screen, the user can modify all book fields (Title, Author, Total Pages, Cover Image, Status) and its associated note (TEXT field). The user shall be able to change a book's status to one of the following: "Active", "Read", "Paused", "Abandoned".
    * REQ-1.5: The book edit screen shall contain a "Delete" button. Tapping this button must prompt the user for confirmation before permanently deleting the book and all its associated reading logs.
    * REQ-1.6: The book edit screen will have a "Save" button. After the user taps "Save" or "Delete", the system saves the changes (if any) and navigates the user back to the previous screen.

#### 3.2 Feature: Reading Session Timer

* Description: Users can time their reading sessions for any "Active" book in their library.
* Functional Requirements:
    * REQ-2.1: The main library view must display a "Start Reading" button (stop watch icon) next to each "Active" book.
    * REQ-2.2: Clicking "Start Reading" will navigate the user to a dedicated timer page for that book. This page will display the book's title, its cover image, and a running stopwatch.
    * REQ-2.3: The timer page must have controls, represented by icons, for "Cancel", "Stop & Save", and "Pause/Resume". The book's notes area shall be visible and directly editable at the bottom of this screen.
    * REQ-2.4: Any modifications to the notes on the timer screen are saved automatically when the user navigates away from the page (e.g., by stopping the session, canceling, or closing the app).
    * REQ-2.5: The user shall be able to pause the running timer. While paused, the elapsed time is preserved. The user must be able to resume the timer, at which point the clock continues from where it was paused.
    * REQ-2.6: The user shall be able to cancel the timer session. Cancelling will discard the elapsed time and return the user to the previous screen without saving a log.
    * REQ-2.7: Upon clicking "Stop & Save", the system shall present a form to log:
        * The starting time of the session (prefilled with the actual starting time).
        * The ending time of the session (prefilled with the current time).
        * The elapsed time (pre-filled from the stopwatch).
        * The number of pages read in that session (Integer, Optional).
    * REQ-2.8: Saving the session log shall store the record and associate it with the correct book.

#### 3.3 Feature: Data Export

* Description: Users can export the data of the library and of the reading sessions.
* Functional Requirements:
    * REQ-3.1: The user can export all the data present in the library and of the reading sessions pressing a button.
    * REQ-3.2: The exported data is saved in a single json file. The file has the following form (time saves the seconds):
        ```json
        {
          "export_date": "2025-07-31T10:00:00Z",
          "books": [
            {
              "title": "Dune",
              "author": "Frank Herbert",
              "status": "Read",
              "totalPages": 896,
              "notes": "The spice must flow.",
              "reading_logs": [
                {
                  "start_time": "2025-07-30T20:00:00Z",
                  "end_time": "2025-07-30T21:30:00Z",
                  "time": 1312,
                  "pages_read": 50
                }
              ]
            }
          ]
        }
        ```

#### 3.4 Feature: Statistics View

* Description: Users can view visualizations of their reading history. Data is aggregated from all books.
* Functional Requirements:
    * REQ-4.1: The Statistics view shall display the total time spent reading across all books.
    * REQ-4.2: The system shall display a bar chart showing the total reading time for each of the last 7 days. If reading time was recorded for a given day, its corresponding bar in the chart must not be zero.
    * REQ-4.3: The system shall provide options to view total reading time by week and by month. The visualizations shall only display time periods (weeks/months) starting from the date of the first recorded log entry until the current date. Periods with no reading time before the first log entry shall not be displayed.

#### 3.5 Feature: Settings

* Description: Users can manage application-level settings.
* Functional Requirements:
    * REQ-5.1: The user shall be able to delete all application data (books, logs, notes) from the Settings view. The system must prompt the user for confirmation before executing this action.

### 4. External Interface Requirements

#### 4.1. User Interfaces

The main screen shows a list of 'Active' books. A navigation bar on the bottom of the screen allows the user to switch to an 'All Books' view, a 'Statistics' view, and a 'Settings' view. Tapping an 'Add Book' button opens a form modal. The 'All Books' view displays all the books in the library.

* Book Grid: The main screen is organized in tiles in a 3xN grid. The tiles display the cover image of each book.
* Book Tile Icons: Each tile will display only two icons for actions:
    * A stop watch icon to start the timer, which navigates to the Reading Session Timer screen.
    * A modify icon (e.g., a pencil) to open the book's details screen where all data, including notes, can be edited.
* Timer Screen UI: The timer screen will use icons for its main actions instead of text. The notes for the book will be displayed in an open, editable field at the bottom of this screen.
* Iconography: Buttons should use intuitive icons instead of text labels.
    * 'Active' (Main View): A house icon.
    * 'All Books': A library/bookshelf icon.
    * 'Statistics': A bar chart icon.
    * 'Settings': A gear icon.
    * Start Timer (on tile): A stop watch icon.
    * Modify Book (on tile): A pencil icon (✎).
    * Timer Pause: A pause icon (❚❚).
    * Timer Resume: A play icon (▶).
    * Timer Cancel: An 'X' icon (❌).
    * Timer Stop & Save: A checkmark icon (✔).

#### 4.2. Software Interfaces

The system will interface with the database.

### 5. Non-Functional Requirements

#### 5.1. Performance Requirements

* PERF-1: The application's main screen shall be visible and interactive within 2 seconds of the user tapping the app icon.
* PERF-2: Database queries for fetching the user's library should complete in under 500 milliseconds.

#### 5.2. Software Quality Attributes

* USABILITY-1: A user must be able to start a reading session for a book from the main library view in no more than two clicks.
* RELIABILITY-1: Reading session data must be saved transactionally. A failure during saving should not result in a partially saved or corrupt log entry.
* RELIABILITY-2: In the event of an unexpected application crash or shutdown, the currently running timer's elapsed time shall be saved so it can be recovered on the next app launch.

#### 5.3. Business Rules

* BR-1: A timer cannot be started for a book whose status is "Read" or "Abandoned".
* BR-2: A timer cannot be started if there is another active timer (active means either running or paused).
