# PawPlan

PawPlan is a volunteer-coordination platform for animal shelters.

It is designed to help shelters coordinate dog-walking appointments while giving volunteers a simple way to book walks and interact with the shelter during an appointment.

This repository contains the **native Android application**, developed with **Kotlin** as part of a professional web and mobile development training program and continued as an ongoing personal project.

> **Project status:** PawPlan is currently under active development and is not yet a production release.

## What PawPlan does

Volunteers can use the Android application to:

- Browse and book available dog-walking appointments
- View upcoming and previous appointments
- Track the route of a walk using device location services
- Report incidents that occur during a walk
- Receive appointment-related notifications
- Interact with data shared across the broader PawPlan platform

The Android application focuses primarily on the **volunteer experience while preparing for and completing a walk**.

PawPlan also includes:

- A **React/TypeScript web application** focused on managing users, roles, dogs and appointments
- An initial **iOS prototype built with Swift and SwiftUI**

## Motivation

PawPlan originated from my own experience as an animal-shelter volunteer.

Finding time to volunteer can already be difficult, and coordinating available volunteers, dogs and suitable walking times adds additional friction.

PawPlan explores how software can simplify that coordination and make it easier for volunteers to turn their intention to help into action.

The project has since grown into an opportunity to explore modern web and mobile development across multiple platforms while working on a product based on a real-world need.

## Android Features

### Appointment Booking

Volunteers can browse available walking appointments and book appointments with individual dogs.

Appointment data is synchronized through Firebase/Firestore so that relevant changes can be reflected across the PawPlan applications.

### Walk Tracking

During an appointment, the Android application can track the volunteer's walking route using device location services.

This extends PawPlan beyond appointment management and connects the digital workflow with the activity taking place during the actual walk.

### Incident Reporting

Volunteers can report incidents encountered while walking a dog.

The feature is intended to provide shelters with useful information about events that may require follow-up after an appointment.

### Notifications

The application uses Android notification functionality to communicate relevant appointment information and support time-sensitive interactions.

## Technologies

The Android application currently uses technologies and concepts including:

- **Kotlin**
- **Android**
- **Firebase / Firestore**
- **Kotlin Flow**
- **ViewModels**
- Reactive application state
- Android location services
- Background processing
- Notifications
- Git

The project also explores lifecycle-aware Android architecture and separation between UI, application logic and data access.

## Architecture

PawPlan is structured around a separation of responsibilities between the UI, application state and data layer.

The application makes use of:

- ViewModels for UI-facing state and application logic
- Kotlin Flow for reactive data streams
- Repository abstractions for data access
- Firebase/Firestore for shared application data
- Android lifecycle-aware components
- Platform services for functionality such as location tracking and notifications

The architecture is still evolving as the project grows.

## Broader PawPlan Platform

PawPlan is being developed as a multi-platform product rather than as three identical applications.

Each platform currently has a slightly different focus.

### Android

Volunteer-facing functionality, including:

- Appointment booking
- Walk execution
- Route tracking
- Incident reporting
- Notifications

### Web

Shelter-management functionality, including:

- User management
- Role management
- Dog management
- Appointment management

The web application is built with **React and TypeScript** and also includes ongoing work around semantic HTML, keyboard navigation and screen-reader accessibility.

### iOS

The Swift/SwiftUI application was the first mobile implementation and currently serves primarily as an initial prototype of the PawPlan concept and core appointment workflows.
