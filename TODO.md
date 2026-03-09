# TODO: Fix Data Storage and Input Validation

## Tasks
- [x] Fix DBConnection.java error handling (propagate exceptions instead of returning null)
- [x] Update Member.java to validate memberId (numbers only)
- [x] Update web/app.js to validate memberId (numbers only) and email format
- [x] Compile and test the application

## Notes
- DBConnection was silently catching exceptions and returning null
- Member ID should only accept numeric values
- Email should be validated for proper format
- Duplicate prevention already exists in DAO layer

