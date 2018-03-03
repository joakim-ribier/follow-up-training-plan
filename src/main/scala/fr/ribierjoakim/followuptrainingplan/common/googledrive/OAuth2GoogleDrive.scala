package fr.ribierjoakim.followuptrainingplan.common.googledrive

import java.io.{File, FileInputStream, InputStream, InputStreamReader}

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.{Drive, DriveScopes}
import com.typesafe.config.{Config, ConfigFactory}
import fr.ribierjoakim.followuptrainingplan.common.MyConfig.configToMyConfig

object OAuth2GoogleDrive {

  private val config: Config = ConfigFactory.load("application")

  private val applicationName = config.getSettingValue("drive.app-name")
  private val scopes: java.util.List[String] = {
    val list = new java.util.ArrayList[String]()
    list.add(DriveScopes.DRIVE)
    list.add(DriveScopes.DRIVE_FILE)
    list.add(DriveScopes.DRIVE_APPDATA)
    list
  }
  private val dataStoreDir = new File(System.getProperty("user.home"), ".credentials/drive-java-quickstart");
  private val inputStream: InputStream = new FileInputStream(new File(config.getSettingValue("drive.client-secret")))

  private val jacksonFactory: JacksonFactory = JacksonFactory.getDefaultInstance();
  private val dataStoreFactory: FileDataStoreFactory = new FileDataStoreFactory(dataStoreDir)
  private val httpTransportService: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

  private val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(jacksonFactory, new InputStreamReader(inputStream));
  private val flow: GoogleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransportService, jacksonFactory, clientSecrets, scopes)
    .setDataStoreFactory(dataStoreFactory)
    .setAccessType("offline")
    .build()

  private val credential: Credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")

  val service: GoogleDriveApiService = {
    /**
      * Build and return an authorized Drive client service.
      * @return an authorized Drive client service
      */
    val drive = new Drive.Builder(httpTransportService, jacksonFactory, credential).setApplicationName(applicationName).build();
    new GoogleDriveApiService(drive)
  }
}