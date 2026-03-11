# TODO - Fix Email Validation Issue

## Task
Fix "Load failed: Invalid email format" error and ensure data is properly stored.

## Steps to Completed:
- [x] 1. Update Member.java - Fix email validation regex + add constructor without validation for DB retrieval
- [x] 2. Update MemberDAO.java - Use constructor without validation for database retrieval
- [x] 3. Update app.js - Match the same strict regex on client side
- [x] 4. Update WebServer.java - Improve error handling to properly propagate error messages

## Status: Completed

