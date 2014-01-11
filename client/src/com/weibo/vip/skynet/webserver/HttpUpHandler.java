package com.weibo.vip.skynet.webserver;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.httpserv.HttpServFileUpload;
import org.apache.commons.fileupload.httpserv.HttpServRequestContext;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.weibo.vip.skynet.Constants;

/**
 * @brief 上传请求处理
 * @waring
 *   1) `Unsafe JavaScript attempt to access frame...` maybe occur in chrome, which caused by iframe way of `ajaxfileupload.js`. 
 *   more: `http://stackoverflow.com/questions/5660116/unsafe-javascript-attempt-to-access-frame-in-google-chrome`
 * @author join
 */
public class HttpUpHandler implements HttpRequestHandler {

    static final String TAG = "HttpUpHandler";
    static final boolean DEBUG = false || Constants.DEV_MODE;

    private String webRoot;

    public HttpUpHandler() {
        this.webRoot = Constants.APP_UPLOAD_DIR;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
    	response.addHeader("Access-Control-Allow-Origin", "*");
    	
        Map<String, String> params = parseGetParamters(request);
        String dir = params.get("dir");
        if (dir == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        dir = URLDecoder.decode(dir, Constants.ENCODING);

        final File uploadDir; // upload directory
        uploadDir = new File(this.webRoot, dir);
        if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

        // TODO Decide if file exists and if there are enough free spaces.
        if (uploadDir.isDirectory()) {
            response.setStatusCode(HttpStatus.SC_OK);
            try {
                processFileUpload(request, uploadDir);
                response.setEntity(new StringEntity("ok", Constants.ENCODING));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            }
        } else {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
    
    protected Map<String, String> parseGetParamters(HttpRequest request) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        return parser.parse(getContent(request), '&');
    }
    
    private String getContent(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf('?');
        return index == -1 || index + 1 >= uri.length() ? null : uri.substring(index + 1);
    }

    /** Process file upload */
    private void processFileUpload(HttpRequest request, File uploadDir) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(Constants.THRESHOLD_UPLOAD, uploadDir);
        HttpServFileUpload fileUpload = new HttpServFileUpload(factory);
        
        List<FileItem> fileItems = fileUpload.parseRequest(new HttpServRequestContext(request));
        Iterator<FileItem> iter = fileItems.iterator();
        while (iter.hasNext()) {
            FileItem item = iter.next();

            if (item.isFormField()) {
                processFormField(item);
            } else {
                processUploadedFile(item, uploadDir);
            }
        }
    }

    /** Process a regular form field */
    private void processFormField(FileItem item) {
    }

    /** Process a file upload */
    private void processUploadedFile(FileItem item, File uploadDir) throws Exception {
        String fileName = item.getName();
        File uploadedFile = new File(uploadDir, fileName);
        item.write(uploadedFile);
    }

}
