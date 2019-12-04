package co.uk.postoffice.apps.parcelshop



import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import co.uk.postoffice.apps.parcelshop.fragments.FragmentInit
import co.uk.postoffice.apps.parcelshop.fragments.FragmentWeb
import co.uk.postoffice.apps.parcelshop.javascriptbridge.AndroidInject
import co.uk.postoffice.apps.parcelshop.javascriptbridge.AndroidInjectSettings
import co.uk.postoffice.apps.parcelshop.services.*
import co.uk.postoffice.apps.parcelshop.singleton.Constants
import co.uk.postoffice.apps.parcelshop.singleton.Constants.JAVASCRIPT_FUNCTION_PRINTRESULT
import co.uk.postoffice.apps.parcelshop.singleton.Constants.PRINT_JOB_COMPLETE
import co.uk.postoffice.apps.parcelshop.singleton.Constants.PRINT_JOB_SUCCESS
import com.payzone.libs.E200Printer
import kotlinx.android.synthetic.main.activity_main.*
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.StringBuilder
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import kotlin.concurrent.thread


class MainActivity:AppCompatActivity(){

    companion object {
        val LOGGER = LoggerFactory.getLogger(MainActivity::class.java.name)
    }


    lateinit var context:Context
    lateinit var printer: E200Printer
    lateinit var fragmentWeb: FragmentWeb
    lateinit var bluetoothService: IBluetoothService
    private lateinit var androidInject:AndroidInject
    private lateinit var androidInjectSettings: AndroidInjectSettings
    private lateinit var batteryService: IBatteryService
    private lateinit var wifiService: IWifiService
    private val mainActivity = WeakReference<MainActivity>(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOGGER.info(resources.getString(R.string.entering))
        setContentView(R.layout.activity_main)

        /* initialize context */
        context = this

        val permissionLists = ArrayList<String>()

        if(!isFineLocationPermitted()) {
            permissionLists.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // enabling location permission to allow wifi functionality works
        if (!iCoarseLocationPermitted()) {
            permissionLists.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if(!isPhonePermitted()) {
            permissionLists.add(Manifest.permission.READ_PHONE_STATE)
        }

        if(!isReadExternalPermitted()){
            permissionLists.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(fragment_container != null) {

            if (savedInstanceState != null) {
                return
            }

            /* create and add fragment here*/
            var  fragmentInit = FragmentInit.getInstance(LoggerFactory.getLogger(FragmentInit::class.java))
            var fm: FragmentManager = supportFragmentManager
            var ft: FragmentTransaction = fm.beginTransaction()
            ft.add(R.id.fragment_container, fragmentInit,"fragment-init")
            ft.commit()
        }

        if(isSuAvailable() || isEmulator()){
            finish()
        }

        batteryService = BatteryService.getInstance(context)
        wifiService = WifiService.getInstance(context)
        bluetoothService = BluetoothService.getInstance(context)

        if(isNetAwailable()){
            showToast("Network is connected")
        }

        if(permissionLists.isNotEmpty())
        {
            ActivityCompat.requestPermissions(
                this,
                permissionLists.toTypedArray(),
                Constants.REQUEST_ALL_PERMISSIONS
            )
        }else{
            initializeWebApp()
            displayNextFragment1()
            deviceCertAndKey()
            dirsinfo()
        }

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onStart() {
        super.onStart()
        LOGGER.info(resources.getString(R.string.entering))

        val handler = PrinterMessageHandler(mainActivity)

        printer = E200Printer.getInstance()
        printer.setPrintMessageHandler(handler)

        androidInject = AndroidInject(context, this.printer)
        androidInjectSettings = AndroidInjectSettings.getInstance(this,context,LoggerFactory.getLogger(AndroidInjectSettings::class.java), batteryService, wifiService, bluetoothService)

        setBrightness()

        /*getWriteSettingsPer()*/
        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onPause(){
        super.onPause()
        LOGGER.info(resources.getString(R.string.entering))



        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onResume() {
        super.onResume()
        LOGGER.info(resources.getString(R.string.entering))



        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onStop() {
        LOGGER.info(resources.getString(R.string.entering))

        printer.close()

        LOGGER.info(resources.getString(R.string.leaving))
        super.onStop()
    }

    override fun onDestroy() {
        LOGGER.info(resources.getString(R.string.entering))



        LOGGER.info(resources.getString(R.string.leaving))
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info(resources.getString(R.string.leaving))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info("requestCode -> {}", requestCode)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LOGGER.info("read external permission granted")
                    initializeWebApp()
                    displayNextFragment1()
                    deviceCertAndKey()
                    dirsinfo()
                } else {

                    LOGGER.info("read external permission not granted")
                }
            }

            Constants.REQUEST_PERMISSION_CAMERA -> {
                LOGGER.info("request constant : {}", requestCode)
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LOGGER.info("Camera permission granted")

                } else {
                    LOGGER.info("Camera permission not granted")
                }
            }

            Constants.REQUEST_PERMISSION_READ_PHONE_STATE -> {
                LOGGER.info("request constant : {}", requestCode)
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LOGGER.info("Read phone state permission granted")

                } else {
                    LOGGER.info("Read phone state permission not granted")
                }
            }

            Constants.REQUEST_PERMISSION_ACCESS_FINE_LOCATION -> {
                LOGGER.info("request constant : {}", requestCode)
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LOGGER.info("Access location permission granted")

                } else {
                    LOGGER.info("Access location permission not granted")
                }
            }

            Constants.REQUEST_PERMISSION_ACCESS_COARSE_LOCATION -> {
                LOGGER.info("request constant : {}", requestCode)
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LOGGER.info("Access Coarse location permission granted")

                } else {
                    LOGGER.info("Access Coarse location permission not granted")
                }
            }

            Constants.REQUEST_ALL_PERMISSIONS -> {
                LOGGER.info("request constant : {}", requestCode)
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    LOGGER.info("All permissions are granted")
                    initializeWebApp()
                    displayNextFragment1()
                    deviceCertAndKey()
                    dirsinfo()
                } else {
                    LOGGER.info("All permissions are not granted")
                }
            }

            else -> {
                /* do nothing */
            }
        }


        LOGGER.info(resources.getString(R.string.leaving))
    }

    fun isNetAwailable():Boolean{

        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo:NetworkInfo? = cm.activeNetworkInfo
        LOGGER.info("***************************************************")
        LOGGER.info("Network Information are")
        LOGGER.info(networkInfo.toString())
        LOGGER.info("IsAvailable :   {}", networkInfo?.isAvailable)
        LOGGER.info("IsConnected :   {}", networkInfo?.isConnected)
        val dState: NetworkInfo.DetailedState? = networkInfo?.detailedState
        LOGGER.info("DestailedState: {}",dState.toString())
        LOGGER.info("***************************************************")
        return networkInfo?.isConnectedOrConnecting?:false


    }

    /**
     *  Function to detect if device is an emulator
     */
    private fun isEmulator():Boolean{
        return (Build.BRAND.startsWith(resources.getString(R.string.generic)) && Build.DEVICE.startsWith(resources.getString(R.string.generic)))
                || Build.FINGERPRINT.startsWith(resources.getString(R.string.generic))
                || Build.FINGERPRINT.startsWith(resources.getString(R.string.unknown))
                || Build.HARDWARE.contains(resources.getString(R.string.goldfish))
                || Build.HARDWARE.contains(resources.getString(R.string.ranchu))
                || Build.MODEL.contains(resources.getString(R.string.google_sdk))
                || Build.MODEL.contains(resources.getString(R.string.emulator1))
                || Build.MODEL.contains(resources.getString(R.string.android_sdk_built_for_x86))
                || Build.MANUFACTURER.contains(resources.getString(R.string.genymotion))
                || Build.PRODUCT.contains(resources.getString(R.string.sdk_google))
                || Build.PRODUCT.contains(resources.getString(R.string.google_sdk))
                || Build.PRODUCT.contains(resources.getString(R.string.sdk))
                || Build.PRODUCT.contains(resources.getString(R.string.sdk_x86))
                || Build.PRODUCT.contains(resources.getString(R.string.vbox86p))
                || Build.PRODUCT.contains(resources.getString(R.string.emulator2))
                || Build.PRODUCT.contains(resources.getString(R.string.simulator));
    }


    /**
     * Function to detect su is available on the device or
     * if device is rooted.
     */
    private fun isSuAvailable():Boolean{

        val dirPaths = System.getenv(resources.getString(R.string.path))?.split(resources.getString(R.string.colon))

        for(dir in dirPaths!!){
            if(File(dir,resources.getString(R.string.su)).exists()){
                return true
            }
        }

        return false

    }


    /**
     * A function to show information Toast on device
     */
    private fun showToast( message:String){

        val taskShowToast = Thread(Runnable{
                runOnUiThread {Toast.makeText(context , message,Toast.LENGTH_SHORT).show() }
        })

        taskShowToast.start()
    }



    private fun displayNextFragment1(){

        val changeFragment1 = Thread(Runnable {

           // Thread.sleep(3000)
            runOnUiThread {
                LOGGER.info(resources.getString(R.string.entering))

                /* initialize webview fragment */
                fragmentWeb = FragmentWeb.getInstance(printer,androidInject,androidInjectSettings ,LoggerFactory.getLogger(FragmentWeb::class.java.name))

                val fm: FragmentManager = supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()

                ft.replace(R.id.fragment_container, fragmentWeb,"fragment-web")
                ft.commit()


                LOGGER.info(resources.getString(R.string.leaving))

            }
        })

        changeFragment1.start()
    }





  /*  private fun displayNextFragment2(){

        var changeFragment2 = Thread(Runnable {

            Thread.sleep(60000)
            runOnUiThread(Runnable {
                LOGGER.info(resources.getString(R.string.entering))

                 *//*initialize weview fragment *//*
                var fragmentWeb = FragmentWeb.getInstance()


                var fm: FragmentManager = supportFragmentManager
                var ft: FragmentTransaction = fm.beginTransaction()

                ft.replace(R.id.fragment_container, fragmentWeb,"fragment-web")
                ft.commit()

                LOGGER.info(resources.getString(R.string.leaving))

            })
        })

        changeFragment2.start()
    }*/

    /*private fun displaySetFragment(){

        var changeFragment3 = Thread(Runnable {

            Thread.sleep(30000)
            runOnUiThread(Runnable {
                LOGGER.info(resources.getString(R.string.entering))

                *//* initialize weview fragment *//*
                var fragmentSet = FragmentSet()


                var fm: FragmentManager = supportFragmentManager
                var ft: FragmentTransaction = fm.beginTransaction()

                ft.replace(R.id.fragment_container, fragmentSet,"fragment-set")
                ft.commit()

                LOGGER.info(resources.getString(R.string.leaving))

            })
        })

        changeFragment3.start()
    }*/

    private fun dirsinfo(){
        LOGGER.info(resources.getString(R.string.entering))

        val get_files_dir = context.filesDir.absolutePath
        val get_ext_files_dir = context.getExternalFilesDir(null)?.absolutePath
        val get_cache_dir = context.cacheDir?.absolutePath

        val get_root_dir = Environment.getRootDirectory().absolutePath
        val get_data_dir = Environment.getDataDirectory().absolutePath


        val get_ext_storage_dir = Environment.getExternalStorageDirectory().absolutePath



        LOGGER.info("*** Directories Information ***")
        val sb = StringBuilder()
        sb.append(
            "Files Dir:  " + get_files_dir
                    + "Files_External_Dir:" + get_ext_files_dir
                    + "\n Cache Dir:  " + get_cache_dir
                    + "\n RootDir:  " + get_root_dir
                    + "\n DataDir:  " + get_data_dir
                    + "\n External Storage Dir:  " + get_ext_storage_dir
                    + "\n\n"
        )
        LOGGER.info("{}",sb.toString())
        LOGGER.info(resources.getString(R.string.leaving))

    }


    /**
     *  A function to check if  a file or directory exists
     *
     * */
    private fun checkFileExits(file:File):Boolean {

        LOGGER.info(resources.getString(R.string.entering))
        LOGGER.info("File: {}, Exists: {} ", file.absolutePath,file.exists())
        LOGGER.info(resources.getString(R.string.leaving))
        for (i in 1..2) {
            LOGGER.info("retry file $i")
            if (file.exists()) {
                return true
            }else{
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    LOGGER.debug("InterruptException $e")
                }
            }

        }
        return false
    }


    private fun copyWebApp(zipfile:ZipFile) {
        LOGGER.info(resources.getString(R.string.entering))

        /* live folder path */
        val liveFolder = context.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.APP_LIVE_FOLDER
        val file = File(liveFolder)
        if (!file.exists()) {
            /* create directory */
            file.mkdir()
            LOGGER.info("{} is created now ", file.absolutePath)
        }

        try {
                LOGGER.info("starting to unzipping file")
                zipfile.extractAll(liveFolder)
                LOGGER.info("unzipping completed now")
        } catch (e: ZipException) {
                LOGGER.error("Exception: {}", e)
        }





        LOGGER.info(resources.getString(R.string.leaving))
    }



    private fun deleteFolder(file:File) {
        LOGGER.info(resources.getString(R.string.entering))

        file.deleteRecursively()

        LOGGER.info(resources.getString(R.string.leaving))
    }


    private fun deleteFile(file:File){
        LOGGER.info(resources.getString(R.string.entering))

        /* delete a given file */
        var flag = file.delete()
        LOGGER.info(" deleted file , {}",flag)

        LOGGER.info(resources.getString(R.string.leaving))
    }

    private fun setBrightness(){
        window.attributes.screenBrightness = 1F
        window.addFlags(WindowManager.LayoutParams.FLAGS_CHANGED)
    }

    private fun initializeWebApp(){
        LOGGER.info("Initialise web app ...")
        val zipFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Constants.ZIPPED_WEB_APP)

        val zip_file_exist = checkFileExits(zipFile)

        val liveFolder = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.APP_LIVE_FOLDER)
        val live_folder_exist = checkFileExits(liveFolder)

        /* check if webapp.zip exists */
        if(zip_file_exist){

            /*if exist*/
            if(live_folder_exist){
                LOGGER.info("/*1. delete live folder if exits*/")

                /*1. delete live folder if exits*/
                deleteFolder(liveFolder)

                /* 2. create a live folder */
                /* 3. copy the webapp in live folder*/
                copyWebApp(ZipFile(zipFile))

                /* 4. delete the webapp.zip file */
                deleteFile(zipFile)

            } else {
                LOGGER.info("if it doesn't exists ( this will the case for the first time)")

                /* if it doesn't exists ( this will the case for the first time) */
                /* copy the webapp after creating a live folder */
                copyWebApp(ZipFile(zipFile))

                /* 4. delete the webapp.zip file */
                deleteFile(zipFile)

            }


        } else {

            /* only go into this if statement if live folder doesn't exists and this will happen only
             * when the application run for very first time after install */
            if(!live_folder_exist){

                LOGGER.info("live folder doesn't exist so copying app from assets folder")
                /* file path to be unzipped */
                val filePath = context.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.ZIPPED_WEB_APP

                copyFileFromAsset(File(filePath))
                copyWebApp(ZipFile(filePath))
                deleteFile(File(filePath))



            }

        }


    }

    /**
     * This function copies "webapp.zip"  file from assets folder to applications
     * external "files" folder .
     */
    private fun copyFileFromAsset(destFile:File){
        LOGGER.info(context.resources.getString(R.string.entering))

        val am = context.assets
        val inputStream = am.open(Constants.ZIPPED_WEB_APP)
        val outputStream = FileOutputStream(destFile)

        val numOfBytes = inputStream.copyTo(outputStream, DEFAULT_BUFFER_SIZE)

        LOGGER.info("numOfBytes (webapp.zip) = {}", numOfBytes)
        LOGGER.info(context.resources.getString(R.string.leaving))
    }



    private fun isReadExternalPermitted():Boolean{

        val value = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(value != PackageManager.PERMISSION_GRANTED){
            return false
        }

        return true
    }

    private fun isFineLocationPermitted():Boolean{

        val value = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(value != PackageManager.PERMISSION_GRANTED){
            return false
        }

        return true
    }

    private fun iCoarseLocationPermitted():Boolean{

        val value = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if(value != PackageManager.PERMISSION_GRANTED){
            return false
        }

        return true
    }

    private fun isPhonePermitted():Boolean{

        val value = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        if(value != PackageManager.PERMISSION_GRANTED){
            return false
        }

        return true
    }

    /**
     * This function only request read external storage
     */
    private fun requestReadPermission(){
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
    }



    class PrinterMessageHandler(private val mainActivity: WeakReference<MainActivity>): Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                PRINT_JOB_COMPLETE ->{
                    if(msg.arg1 == PRINT_JOB_SUCCESS){
                        LOGGER.info(mainActivity.get()?.resources?.getString(R.string.print_job_completed))
                        mainActivity.get()?.printResult(true,"\""+ mainActivity.get()?.resources?.getString(R.string.print_job_completed) +"\"")

                    } else {
                        LOGGER.info(mainActivity.get()?.resources?.getString(R.string.print_job_failed))
                        LOGGER.info(JSONObject(msg.obj.toString()).toString())
                        mainActivity.get()?.printResult(false,JSONObject(msg.obj.toString()).toString())
                        mainActivity.get()?.printer!!.cancel()
                    }
                }

                /* This is default case */
                else -> {
                    LOGGER.info("Message received ${msg?.what}")
                    mainActivity.get()?.printResult(false,JSONObject(msg?.obj.toString()).toString())
                    super.handleMessage(msg)
                }
            }
        }
    }


    /**
     * This function will report printer print job result to function in javascript layer
     */
    private fun printResult(status:Boolean,msg:String){

        LOGGER.info(resources.getString(R.string.entering))
        var webview = fragmentWeb.webView

        val result = "'{ \"status\": \"$status\", \"msg\": $msg }'"

        webview.post {
            LOGGER.info("javascript:$JAVASCRIPT_FUNCTION_PRINTRESULT($result)")
            webview.loadUrl("javascript:$JAVASCRIPT_FUNCTION_PRINTRESULT($result)")
        }

        LOGGER.info(resources.getString(R.string.leaving))

    }


    /**
     * This function checks and copy device certificate and key to its private storage.
     * Initially these files are pushed at /sdcard/download/ folder on the device. This
     * function run in a separate thread.
     */
    private fun deviceCertAndKey(){

        thread {

            LOGGER.info(resources.getString(R.string.entering))
            val inCertFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),Constants.DEVICE_CERTIFICATE)
            val inKeyFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.CERTIFICATE_KEY)


            var  inCertFlag = checkFileExits(inCertFile)
            var  inKeyFlag = checkFileExits(inKeyFile)


            /* only copy files if both file exists */
            if(inCertFlag && inKeyFlag){

                val outCertFile = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.DEVICE_CERTIFICATE)
                val outKeyFile = File(context.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.CERTIFICATE_KEY)

                /* copy files now */
                inCertFile.copyTo(outCertFile,true, DEFAULT_BUFFER_SIZE)
                inKeyFile.copyTo(outKeyFile,true, DEFAULT_BUFFER_SIZE)

                /* delete files from public folder now */
                deleteFile(inCertFile)
                deleteFile(inKeyFile)

            }

            LOGGER.info(resources.getString(R.string.leaving))
        }

    }


    fun getWriteSettingsPer(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (Settings.System.canWrite(context)){
                    // Do stuff here
                }
            else {
                intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.setData(Uri.parse("package:"+ context.packageName ))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }


}