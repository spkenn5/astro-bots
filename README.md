# astro-bots
Android App that responds to Basic REST API

## How to use
` build & install android project `

# Action available:
- List of Channel
  - Add channel to favorited list when clicked
- Drawer
  - Channel List 
    - List of Channel
      - Add channel to favorited list when clicked
  - TV Guide
    - List of Programs ( periodStart: currentDeviceHour - 1 hour , periodEnd: currentDeviceHour + 2 hour )
      - Add program to favorited list when clicked
  - Favorited List

    - List of Channel
      - Signed in
        - Displays favorited channel 
      - Default
        - Displays favorited channel
     - List of Program
        - Signed in
          - Displays favorited program 
        - Default
          - Displays favorited program 
    - Drawer
      - Logged In
        - Submit data to server
          - Add data (user_id, user_email, user_photo, user_channel_fav, user_program_fav ) to DB server
          - Update data (if data existed) (user_id, user_email, user_photo, user_channel_fav, user_program_fav ) to DB server
        - Get data from server
          - Get data from server based on google user_id
          - Populate local preference based on server data          
        - Delete data from server
          - Delete data from server based on google user_id
          - Delete all user saved preferences
      - Default
        - Back to main 
          - Navigates to Home        
        - Delete local data
          - Deletes local saved preferences
- Setting
  - Default Sorting ( Default sorting is based on STB Number )
    - ID - All data will be sorted by ID
    - NAME - All data will be sorted by NAME
    - SAVE
      - Saves to local sorting preferences
  - Sign in / Sign out
    - Sign In
      - Initiate user preferences
    - Sign Out
      - Clear all preferences
