package day.kotlinlib


import day.kotlinhelper.printExceptionHeader
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.nio.file.Paths
import java.util
import java.util.*
import java.util.regex.Pattern

/**
 * parses provided file to extract urls and file name
 */
class TaskFileParser (val filePath : String,val fileOutputPath : String) {
    val parsedValues = hashMapOf<String,String>()
    val createdTaskParams = linkedListOf<Pair<File,URL>>()
    val failedLines = linkedListOf<String>()
    private val matchPattern = Pattern.compile("^[ ]*(http://[^ ]+)[ ]+([^ ^/][^/]+?)[ ]*$")
    private  fun parse() {
        var currentLine = 0
        var reader : BufferedReader? = null;
        var readedStr : String? = null;
        try {
            reader =  BufferedReader( FileReader(filePath))
            fun tryRead() : Boolean {
                readedStr = reader?.readLine()
                return  readedStr !=null
            }
            while ( tryRead()) {
                currentLine++
                val matcher = matchPattern.matcher(readedStr)
                if (matcher.matches()) {
                    val url = matcher.group(1)
                    val fileName = matcher.group(2)
                    if (parsedValues.containsKey(fileName))
                        failedLines.add("duplicate file name at line ${currentLine}")
                    else
                        parsedValues.put(fileName,url)
                } else {
                    failedLines.add("ivalid string at line ${currentLine}")
                }
            }
        } finally{
            if (reader!= null)
                    reader.close()
        }
    }
    fun checkFilesAndUrls() {
        for((fileName,urlName) in parsedValues) {
            val file = Paths.get(fileOutputPath, fileName).toFile() //File(fileName)
            val url = URL(urlName)
            try {
                if ( !file.exists() ) {
                    val con : HttpURLConnection? = url.openConnection() as? HttpURLConnection
                    if (con != null){
                        con.connect()
                        val responseCode = con.getResponseCode()
                        con.disconnect()
                        if (responseCode == 200)
                            createdTaskParams.add(Pair(file,url))
                        else
                            failedLines.add("error response code ${createdTaskParams} at url ${responseCode}")
                    } else {
                        failedLines.add("unable to open url as HttpURLConnection : ${urlName}")
                    }
                } else {
                    failedLines.add("file or directory already exists : ${fileName}")
                }

            }
            catch ( e : UnknownHostException) {
                failedLines.add("unknown hostname: ${e.getMessage()}")
            }
            catch ( e : Exception) {
                failedLines.add(printExceptionHeader("error during file or url anlysis: ", e))
                e.printStackTrace()
            }

        }
    }
    init {
        parse()
        checkFilesAndUrls()
    }

}