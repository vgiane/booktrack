# **SRS for Booktrack**

**Version 1.4**

**Prepared by:** Valerio Gianella

**Date:** 31.07.2025

### **Table of Contents**

1. Introduction  
      1.1. Purpose  
      1.2. Document Conventions  
2. Overall Description  
      2.1. Product Perspective and Context  
      2.2. Product Functions  
      2.3. User Classes and Characteristics  
      2.4. Design and Implementation Constraints  
3. System Features  
      3.1. System Feature 1: Book Management  
      3.2. System Feature 2: Reading Session Timer  
      3.3. System Feature 3: Data Export
      3.4. System Feature 4: Statistics View
      3.5. System Feature 5: Settings
4. External Interface Requirements  
      4.1. User Interfaces  
      4.2. Software Interfaces  
5. Non-Functional Requirements  
      5.1. Performance Requirements  
      5.2. Software Quality Attributes  
      5.3. Business Rules  

### **1. Introduction**

#### **1.1. Purpose**

Booktrack is an application that helps the user to manage his library and keep track of his reading time, by logging the time that was spent reading.

#### **1.2. Document Conventions**

We refer to Booktrack as the application, the app, the software, the program, interchangeably.

### **2. Overall Description**

#### **2.1. Product Perspective and Context**

This product is a new, standalone android mobile application.

#### **2.2. Product Functions**

* Registration of a new book.
* Management of the registered books (modify, delete, change status of an existing book).
* Ability to start and stop a timer connected to a specific book that has been previously registered by the user. The recorded time is saved, together with the information of the book to which it was connected.
* Visualization of Historical Data (reading history)
* User Profile Settings Management

#### **2.3. User Classes and Characteristics**

* **General User:** This is the only profile using the app. The user doesn't need to login.

#### **2.4. Design and Implementation Constraints**

The application shall be implemented in Python using the BeeWare framework. All application data (books, logs, etc.) shall be persisted locally on the device in a single-file SQLite database.

### **3. System Features**

#### **3.1 Feature: Book Management**

* **Description:** Users must be able to add, view, modify, and delete books in their personal library. This library is the central repository for all their reading material.
* **Functional Requirements:**
    * REQ-1.1: The system shall provide a form for the user to manually add a new book. The form must capture:
        * Title (Text, Mandatory)
        * Author (Text, Mandatory)
        * Total Pages (Integer, Optional)
        * Cover Image (If no image is provided, generate a simple cover with the book's title and author's initials).
    * REQ-1.2: Upon creation, a book's status shall default to "Active".
    * REQ-1.3: The user shall be able to change a book's status to one of the following: "Active", "Read", "Paused", "Abandoned".
    * REQ-1.4: The system shall allow the user to edit all fields of an existing book.
    * REQ-1.5: The system shall allow the user to delete a book. Deleting a book must also delete all associated reading logs and notes. The system must ask for confirmation before deletion.
    * REQ-1.6: The user can add a note to a book. The note shall be stored as a TEXT field in the book's database record.
    * REQ-1.7: The user can modify the note associated to a book.

#### **3.2 Feature: Reading Session Timer**

* **Description:** Users can time their reading sessions for any "Active" book in their library.
* **Functional Requirements:**
    * REQ-2.1: The main library view must display a "Start Reading" button next to each "Active" book.
    * REQ-2.2: Clicking "Start Reading" will navigate the user to a dedicated timer page for that book. This page will display the book's title and a running stopwatch.
    * REQ-2.3: The timer page must have a "Cancel" button, a "Stop & Save" button, a "Pause" Button and a button that allows the user to create a note (if not present in the book) or modify the note of the book.
    * REQ-2.4: The user shall be able to pause the running timer. While paused, the elapsed time is preserved. The user must be able to resume the timer, at which point the clock continues from where it was paused.
    * REQ-2.5: The user shall be able to cancel the timer session. Cancelling will discard the elapsed time and return the user to the previous screen without saving a log.
    * REQ-2.6: Upon clicking "Stop & Save", the system shall present a form to log:
        * The starting time of the session (prefilled with the actual starting time).
        * The ending time of the session (prefilled with the current time).
        * The elapsed time (pre-filled from the stopwatch).
        * The number of pages read in that session (Integer, Optional).
    * REQ-2.7: Saving the session log shall store the record and associate it with the correct book.

#### **3.3 Feature: Data Export**

* **Description:** Users can export the data of the library and of the reading sessions.
* **Functional Requirements:**
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

#### **3.4 Feature: Statistics View**

* **Description:** Users can view visualizations of their reading history.
* **Functional Requirements:**
    * REQ-4.1: The Statistics view shall display the total time spent reading.
    * REQ-4.2: The system shall display a bar chart showing the total reading time for each of the last 7 days.
    * REQ-4.3: The system shall provide options to view total reading time by week and by month displaying the weeks / months since the first recorded data until now.

#### **3.5 Feature: Settings**

* **Description:** Users can manage application-level settings.
* **Functional Requirements:**
    * REQ-5.1: The user shall be able to delete all application data (books, logs, notes) from the Settings view. The system must prompt the user for confirmation before executing this action.

### **4. External Interface Requirements**

#### **4.1. User Interfaces**

The main screen shows a list of 'Active' books. A navigation bar on the bottom of the screen allows the user to switch to an 'All Books' view, a 'Statistics' view, and a 'Settings' view. Tapping an 'Add Book' button opens a form modal.
The main screen is organized in tiles in a 2xN matrix. The tiles display the cover image of each book. At the bottom of these tiles there are the commands relative to the book management, displayed as icons.
The 'All Books' view displays all the books in the library.

The buttons don't display text, but icons:
* 'Active': a hause
* 'All Books': a library
* 'Statistics': some Vertical sticks / bars
* 'Settings': a gear wheel
* The timer at each book: a stopwatch
* Add / Modify notes: a note

#### **4.2. Software Interfaces**

The system will interface with the Python `sqlite3` library to manage the local database file stored within the application's private data directory on the device.

### **5. Non-Functional Requirements**

#### **5.1. Performance Requirements**

* PERF-1: The application's main screen shall be visible and interactive within 2 seconds of the user tapping the app icon.
* PERF-2: Database queries for fetching the user's library should complete in under 500 milliseconds.

#### **5.2. Software Quality Attributes**

* USABILITY-1: A user must be able to start a reading session for a book from the main library view in no more than two clicks.
* RELIABILITY-1: Reading session data must be saved transactionally. A failure during saving should not result in a partially saved or corrupt log entry.
* RELIABILITY-2: In the event of an unexpected application crash or shutdown, the currently running timer's elapsed time shall be saved so it can be recovered on the next app launch.

#### **5.3. Business Rules**

* BR-1: A timer cannot be started for a book whose status is "Read" or "Abandoned".
* BR-2: A timer cannot be started if there is another active timer (active means either running or paused).
