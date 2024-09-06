# Java Appointment Scheduler Application #

## Overview

This Java application is designed to manage customer records, appointments, and scheduling for a business. It provides a graphical user interface (GUI) for users to interact with the system efficiently.

# Features #

1. Customer Record Management

- *Add Customer*: Add new customer details such as name, address, postal code, and phone number.

- *Update Customer*: Modify existing customer information.

- *Delete Customer*: Remove a customer from the system, including associated appointments.

2. Appointment Scheduling

- *Schedule Appointment*: Add, update, and delete appointments with information like title, description, location, contact, type, start date and time, end date and time, customer ID, and user ID.

- *View Schedule*: Display appointment schedules by month and week in a TableView.

- *Adjust Appointment Times*: Automatically update appointment times according to the user's local time zone while storing in Coordinated Universal Time (UTC).

3. Appointment Validation and Error Checks

- *Business Hours Check*: Prevent scheduling appointments outside of business hours (8:00 a.m. to 10:00 p.m. ET), including weekends.

- **Overlapping Appointments*: Avoid scheduling overlapping appointments for customers.

- *Username and Password Check*: Validate and prevent incorrect username and password entries.

4. User Activity Tracking

- *User Log-in Tracking*: Record user log-in attempts, dates, time stamps, and success status in a login_activity.txt file.

5. Appointment Alerts

- *Upcoming Appointments*: Provide an alert when there is an appointment within 15 minutes of the user's log-in.

6. Reports

- *Customer Appointments Report*: Display the total number of customer appointments by type and month.

- *Contact Schedule Report*: Show a schedule for each contact with appointment details.

- *Additional Custom Report*: Generate an additional report of your choice.

7. Lambda Expressions

- Utilize at least two different lambda expressions to improve code readability and conciseness.

#How to Run#

1. Ensure you have Java installed on your machine.

2. Clone the repository.

3. Open the project in your preferred Java IDE.

4. Compile and run the `CustomerRecordUI` class.

5. Connect with the suitable Database and related tables

6. Ensure the to have proper JDBC connection
