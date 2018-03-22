# Follow-Up T.Plan - Scala

**WIP:** RELEASE 0.1-SNAPSHOT

## Table of Contents

* [Configuration](#configuration)
* [Google Drive Backup](#google-drive-backup)
* [Screenshot](#screenshot)
* [Troubleshooting](#troubleshooting)

## Configuration

For the configuration and all messages of the application see [application.conf](src/main/resources/application.conf).

The only important key to update before starting is **`configuration.root-data-directory`** which defines the path of the data folder for the application.

## Google Drive Backup

Is possible to do backups to your Google Drive account (if you have already one).

1. You need to save the application for [Google Drive API](https://console.developers.google.com/flows/enableapi?apiid=drive).
2. And to configure the keys in the section **`configuration.drive`**.
```
drive {
  folder-id = "{google drive id main folder}"
  client-secret = "{the path of your client_secret.json (generated by the API)}"
  app-name = "{the app name configured by the API}"
  year-to-drive-id.map = {
    "2018" : "{google drive id 2018 folder (in the main folder for archiving)}",
    "2019" : "{google drive id 2019 folder}"
  }
}
```

## Screenshot

* Home

![Home](/resources/screenshots/f-up.home.png)

* Compute Average Pace

![Compute Average Pace](/resources/screenshots/f-up.home.option-1.png)

* Compute Run Times

![Compute Run Times](/resources/screenshots/f-up.home.option-2.png)

* Add New Training Plan

![New Training Plan](/resources/screenshots/f-up.home.option-3.new.png)

* T-Plan Home

![T-Plan Home](/resources/screenshots/f-up.home.t-plan.home.png)

## Troubleshooting

Soon...
