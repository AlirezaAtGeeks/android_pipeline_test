package co.uk.postoffice.apps.parcelshop.singleton

object Constants {

    val ZIPPED_WEB_APP = "webapp.zip"
    val APP_LIVE_FOLDER = "live"
    val FILE_INDEX_HTML = "index.html"
    /* constants for permission request */
    val REQUEST_PERMISSION_CAMERA = 10
    val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 20
    val REQUEST_PERMISSION_WRITE_SETTINGS = 30
    val REQUEST_PERMISSION_READ_PHONE_STATE = 40
    val REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 50
    val REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 60
    val REQUEST_ALL_PERMISSIONS = 70


    /* printer messages */
    val PRINT_JOB_COMPLETE:Int = 5000
    val PRINT_JOB_SUCCESS:Int = 1

    /* javascript function to be called by native code */
    val JAVASCRIPT_FUNCTION_PRINTRESULT = "window.printResult"

    /* device certificates and key */
    val DEVICE_CERTIFICATE = "certificate.7z"
    val CERTIFICATE_KEY = "key"

    /* names allocated to trigger other applications */
    val ANDROID_SETTINGS = "android_settings"
    val ANDROID_SETTINGS_PACKAGE_NAME = "com.android.settings"
    val XAC_XAIOUTILITY = "xac_xaioutility"
    val XAIOUTILITY_PACKAGE_NAME = "com.xac.util.saioutility"
    val XAC_PRODUCTIONTEST = "xac_productiontest"
    val PRODUCTIONTEST_PACKAGE_NAME = "com.xac.productiontest"



}