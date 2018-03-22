package sample.http;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

abstract public class Downloader {


    private static HashMap<String, String> mimeMap = new HashMap<String, String>();
    private static ArrayList<String> mimeTypes = new ArrayList<String>();
    private static boolean mimeMapCreated = false;



    /**
     * Downloading the file with UI updating during the downloading.
     * Method supports pausing and resuming.
     * @param url
     * URL of file to download
     * @param savePath
     * the directory in which the file will be downloaded(with last symbol "/")
     * @param buffSize
     * the maximum number of bytes that can be taken at a time
     */
    public static void downloadFile(String url, String savePath, int buffSize) {
        try {
			/* Get connection */
            URL connection = new URL(url);
            HttpURLConnection urlconn;
            long fileSize, downloadedBytes = 0;

            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            fileSize = urlconn.getContentLengthLong();
            urlconn.connect();

			/* Set input stream */
            InputStream in = null;

            boolean gettedException = false;

            try {
                in = urlconn.getInputStream();
            }
            catch (IOException e) { // Fix url(replace spaces with %20)
                gettedException = true;
                if (e.getMessage().split(" for URL: ")[0]
                        .equals("Server returned HTTP response code: 400")) {
                    url = e.getMessage().split(" for URL: ")[1].replaceAll(" ", "%20"); // Replace spaces
                    connection = new URL(url);
                    urlconn.disconnect();
                    urlconn = (HttpURLConnection) connection.openConnection();
                    urlconn.setRequestMethod("GET");
                    fileSize = urlconn.getContentLengthLong();
                    urlconn.connect();
                }
            }

            if (gettedException) { // Try again with url fix
                try {
                    in = urlconn.getInputStream();
                }
                catch (IOException e) {

                    return;
                }
            }

			/* Find file name and create full path */
            String fileName = null;
            String fullPath = null;

            String contentDispos = urlconn.getHeaderField("Content-Disposition"); // 1. Try to extract done name

            if (contentDispos != null) { // 1. If name is in header
                fileName = contentDispos.split("\"")[1];
                // TODO improve Regex (filename=\"The.Walking.Dead.S06E01.1080p.HDTV.FOX HD.ts\")
            }
            else { // 2. Try to construct name from url
                if (!mimeMapCreated) {
                    System.out.println("Pull MIME types from Apache svn...");
                    createMimeMap();
                }

                String[] tArr = url.replaceAll("%20", " ").split("/"); // Separate name + extension(if is) from url
                String possibleName = tArr[tArr.length - 1];

                tArr = possibleName.split("[.]"); // Separate extension

                if (mimeTypes.contains(tArr[tArr.length - 1])) { // Check extension
                    fileName = possibleName;
                }
                else { // 3. Try to find extension by content-type
                    String contentType = urlconn.getContentType();

                    tArr = url.split("/");

                    if (contentType != null) { // If content-type is in header
                        String extension = mimeMap.get(contentType);

                        if (extension != null) // Extension registered in Apache MIME types
                            fileName = tArr[tArr.length - 1] + "." + extension;
                        else // same as 4.
                            fileName = tArr[tArr.length - 1] + "." + "file";
                    }
                    else // 4. Huh, I did everything I could(
                        fileName = tArr[tArr.length - 1] + "." + "file";
                }
            }

            fullPath = savePath + fileName;

			/* Set Labels */

            Double dTemp = new BigDecimal(fileSize / Math.pow(10, 6))
                    .setScale(3, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            File file = new File(fullPath);
            if(file.exists())return;
            System.out.println("fullPath:" + fullPath);
            if(!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
			/* Set write stream */
            OutputStream writer = new FileOutputStream(fullPath);


            byte buffer[] = new byte[buffSize]; // Max bytes per one reception

			/* Download */
            int i = 0;
            long delta_t = System.nanoTime();
            double second_waiter = 0.0;

            while ((i = in.read(buffer)) > 0) {
                writer.write(buffer, 0, i);
                second_waiter += i;
                downloadedBytes += i;


                if ((System.nanoTime() - delta_t) >= 1E9) { // If the second was over
                    Double speed = new BigDecimal((second_waiter / Math.pow(10, 6)))
                            .setScale(3, BigDecimal.ROUND_HALF_UP)
                            .doubleValue();


                    delta_t = System.nanoTime(); // Set to zero
                    second_waiter = 0.0;
                }
                if (downloadedBytes == fileSize) { // If download is complete
                    System.out.println("Complete downloader");
                }

            }
            writer.flush();
            writer.close();
            in.close();
            urlconn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pull actual MIME list from Apache svn and save in HashMap
     * @author
     * heroys6
     */
    public static void createMimeMap() {
        try {
            String url = "https://svn.apache.org/repos/asf/httpd/httpd/trunk/docs/conf/mime.types";
            URL connection = new URL(url);
            HttpURLConnection urlconn;

            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();

            InputStream in = urlconn.getInputStream();
            OutputStream writer = new FileOutputStream("MIME.txt");


            int i = 0;
            byte[] buffer = new byte[10000];

            while ((i = in.read(buffer)) > 0)
                writer.write(buffer, 0, i);

            writer.flush();
            writer.close();

            in.close();
            urlconn.disconnect();

            File f = new File("MIME.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String temp;
            String[] parts;

            while ((temp = br.readLine()) != null) {
                parts = temp.split("	+");
                if (!parts[0].contains("#")) { // Add content-type if not commented
                    mimeMap.put(parts[0], parts[1].split(" ")[0]);
                    for (String s : parts[1].split(" ")) // Consider all possible extensions
                        mimeTypes.add(s);
                }
            }

            mimeMapCreated = true;

            br.close();
            f.delete();
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }
}