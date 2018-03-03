/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.storage.HTTPUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.logging.Logger;

/**
 *
 * @author vasil
 */
public class Util {

    private static final Logger log = Logger.getLogger(Util.class);

     public static /*JSONObject*/ String doGet(String url, Map<String, String> headerList) //throws ParseException
    {
        System.out.println("doGet => " + url + "\n\n");
        //JSONObject res = new JSONObject();
        String res = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response;

        if (headerList != null) {
            headerList.entrySet().stream().forEach((t) -> {
                Header header = new BasicHeader(t.getKey(), t.getValue());
                request.setHeader(header);
            });
        }

        try {
            response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuilder json = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                json.append(line);
            }
            //org.json.simple.parser.JSONParser parser = new JSONParser();
            //Object obj = parser.parse(json.toString());
            //JSONObject jsonObj = (JSONObject) obj;
            res = json.toString();
                    //jsonObj;
        } catch (IOException ex) {
            log.info(ex.getMessage());
        }
        return res;
    }
}
