# Project Plan for Booktrack

Version 1.0

Prepared by: Valerio Gianella

Date: 08.08.2025

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
    3.3. System Feature 3: Statistics & Goals
    3.4. System Feature 4: Data Management
    3.5. System Feature 5: Application Settings
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

### 3. Features

#### 3.1 Feature: Book Management 📚

* Description: This feature covers all aspects of managing books in the user's personal library, including adding, viewing, editing, and deleting them.

* a. Sub-Feature: Add Book
    * Description: Users can add books to their personal library either manually or by importing data.
    * Functional Requirements:
        * REQ-1: The system shall allow a user to add a book manually or by importing its details from the Google Books API.
        * REQ-2: For a manual entry, the system shall provide a form with the following fields:
            * Title (Text, Mandatory)
            * Author (Text, Mandatory)
            * Total Pages (Integer, Optional)
            * Cover Image (Image, Optional). If no image is provided, the system shall generate a default cover using the book's title and the author's initials.
        * REQ-3: When importing from the Google Books API, the user shall be able to search by title and select from a list of results. The system will then automatically populate the book's details.
        * REQ-4: Upon creation, a book's status shall default to "Active".

* b. Sub-Feature: View Book Details
    * Description: Users can view the comprehensive details of any book in their library.
    * Functional Requirements:
        * REQ-1: The system shall provide a dedicated "Book View" screen, accessible when a user taps on a book in the main library view.
        * REQ-2: The Book View shall display the following information:
            * Cover Image
            * Title
            * Author
            * Total Pages
            * Status ("Active", "Read", "Paused", "Abandoned")
            * Notes (in a viewable format)
            * A chronological list of all reading sessions for the book, including the date and duration of each session.
        * REQ-3: The user shall be able to delete individual reading logs from the list within the Book View.

* c. Manage Book Details
    * Description: From a single screen, users can modify all aspects of a book, including its metadata, personal notes, and reading sessions, or delete the book entirely.
    * Functional Requirements:
        * REQ-1: The Book View shall contain a single "details" icon (`✎`) that navigates the user to a dedicated "Manage Book" screen.
        * REQ-2: On this screen, the user shall be able to modify the book's core fields: Title, Author, Total Pages, Cover Image, and Status ("Active", "Read", "Paused", "Abandoned").
        * REQ-3: This screen shall contain an editable text area where the user can view and modify their personal notes for the book.
        * REQ-4: This screen shall display the list of registered reading sessions. The user shall be able to add a new session manually.
            * *When adding, a dialog shall prompt for: Date/Time (defaulting to now), Duration in minutes, and optional Pages Read.*
        * REQ-5: The user shall be able to delete individual reading sessions from the list on this screen.
        * REQ-6: The screen shall contain a "Delete Book" button. Tapping it must prompt the user for confirmation before permanently deleting the book and all its associated data.
        * REQ-7: The screen shall have a "Save" button to commit all changes (to details, notes, etc.). After saving, the user shall be navigated back to the previous screen.

#### 3.2 Feature: Reading Session Timer ⏱️

* Description: Users can time their reading sessions for any "Active" book in their library to track their progress.
* Functional Requirements:
    * REQ-1: The main library view must display a "Start Reading" button (e.g., a stopwatch icon) next to each "Active" book.
    * REQ-2: Clicking "Start Reading" shall navigate the user to a dedicated timer page displaying the book's title, cover, and a running stopwatch.
    * REQ-3: The timer page must provide "Cancel", "Stop & Save", and "Pause/Resume" controls. The book's notes area shall be visible and editable on this screen.
    * REQ-4: Modifications to the notes on the timer screen shall be saved automatically when the user navigates away from the page.
    * REQ-5: The user shall be able to pause the timer, preserving the elapsed time. The timer must be resumable.
    * REQ-6: Cancelling the timer shall discard the elapsed time and return the user to the previous screen without saving a log.
    * REQ-7: Upon clicking "Stop & Save", the system shall present a form to log the session, pre-filled with:
        * Start Time (actual start time)
        * End Time (current time)
        * Elapsed Time (from the stopwatch)
        * Pages Read (Integer, Optional, user-input)
    * REQ-8: Saving the session log shall store the record and associate it with the correct book.

#### 3.3 Feature: Statistics & Goals 📊

* Description: Users can view visualizations of their reading history and set personal reading goals.

* a. Sub-Feature: Statistics View
    * Description: Users can view aggregated data and visualizations of their reading habits.
    * Functional Requirements:
        * REQ-1: The Statistics view shall display the total time spent reading across all books.
        * REQ-2: The system shall display a bar chart of total reading time for each of the last 7 days. Only days with recorded reading time will have a non-zero bar.
        * REQ-3: The system shall provide options to view total reading time aggregated by week and by month. Visualizations will only display time periods (weeks/months) from the first recorded log entry to the current date.
        * REQ-4: The system shall display the daily reading progress in the format of 'Time Read Today / Daily Goal'.

* b. Sub-Feature: Daily Goal
    * Description: Users can define a daily reading goal to stay motivated.
    * Functional Requirements:
        * REQ-1: From the settings or statistics area, the user shall be able to define a daily reading goal, specified in minutes.

#### 3.4 Feature: Data Management 💾

* Description: Users can import and export their complete library data for backup or migration purposes.

* a. Sub-Feature: Export Data
    * Description: Users can export all their library and reading session data into a single file.
    * Functional Requirements:
        * REQ-1: A button in the application settings shall allow the user to export all data.
        * REQ-2: The exported data must be saved in a single JSON file. The `time` field represents the total duration in seconds.
            ```json
            {
              "export_date": "2025-08-08T23:55:00Z",
              "books": [
                {
                  "title": "Dune",
                  "author": "Frank Herbert",
                  "status": "Read",
                  "totalPages": 896,
                  "notes": "The spice must flow.",
                  "reading_logs": [
                    {
                      "start_time": "2025-08-07T20:00:00Z",
                      "end_time": "2025-08-07T21:30:00Z",
                      "time": 5400,
                      "pages_read": 50
                    }
                  ]
                }
              ]
            }
            ```

* b. Sub-Feature: Import Data
    * Description: Users can import data from a previously exported JSON file, replacing their current library.
    * Functional Requirements:
        * REQ-1: The system shall allow a user to select a JSON file for import. The file must conform to the structure defined in REQ-2 of the Export Data sub-feature.
        * REQ-2: Upon import, all existing data in the application (books, logs, notes) shall be deleted and replaced by the data from the imported file. The system must prompt the user for confirmation before executing this destructive action.

#### 3.5 Feature: Application Settings ⚙️

* Description: Users can manage global application settings and data.
* Functional Requirements:
    * REQ-1: The user shall be able to delete all application data (books, logs, notes, settings) from the Settings view. The system must prompt the user for confirmation before executing this action.

### 4. External Interface Requirements

#### 4.1. User Interfaces

The main screen shows a list of 'Active' books. A navigation bar on the bottom of the screen allows the user to switch to an 'All Books' view, a 'Statistics' view, and a 'Settings' view. Tapping an 'Add Book' button opens a form modal. The 'All Books' view displays all the books in the library.

* Book Grid: The main screen is organized in tiles in a 3xN grid. The tiles display the cover image of each book.
* Book Tile Icons: Each tile will display only two icons for actions:
    * A stop watch icon to start the timer, which navigates to the Reading Session Timer screen.
    * A details icon to open the book's Manage Book screen, where all of its data can be edited.
* Timer Screen UI: The timer screen will use icons for its main actions instead of text. The notes for the book will be displayed in an open, editable field at the bottom of this screen.
* Iconography: Buttons should use intuitive icons instead of text labels.
    * 'Active' (Main View): A house icon.
    * 'All Books': A library/bookshelf icon.
    * 'Statistics': A bar chart icon.
    * 'Settings': A gear icon.
    * Start Timer (on tile): A stop watch icon.
    * Manage Book (on tile): A pencil icon (✎).
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
