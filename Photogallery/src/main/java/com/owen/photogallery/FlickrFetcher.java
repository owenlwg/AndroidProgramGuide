package com.owen.photogallery;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.util.Log;

import com.owen.photogallery.model.GalleryItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Owen on 2016/1/11.
 */
public class FlickrFetcher {
    private static final String API_KEY = "842002cc129b7cfa9e27cb8659970f52";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_TEXT = "text";

    public static final String PREF_SEARCH_QUERY = "searchQuery";

    private static final String XML_PHOTO = "photo";


    public ArrayList<GalleryItem> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                             .appendQueryParameter("method", METHOD_GET_RECENT)
                             .appendQueryParameter("api_key", API_KEY)
                             .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                             .build().toString();
        return downloadGalleryItems(url);
    }

    public ArrayList<GalleryItem> search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                             .appendQueryParameter("method", METHOD_SEARCH)
                             .appendQueryParameter("api_key", API_KEY)
                             .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                             .appendQueryParameter(PARAM_TEXT, query)
                             .build().toString();
        return downloadGalleryItems(url);
    }

    public ArrayList<GalleryItem> downloadGalleryItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {
            Log.e("url", url);
            String xmlString = getUrlResult(url);

            Log.e("xmlString", xmlString);
            items = parseXml(xmlString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    private String getUrlResult(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * XmlPullParser
     **/
    private ArrayList<GalleryItem> parseXml(String xmlString) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.getText();
//            parser.setInput(new StringReader(xmlString));
            parser.setInput(new StringReader(xmlString));
            int eventType = parser.next();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                    String id = parser.getAttributeValue(null, "id");
                    String caption = parser.getAttributeValue(null, "title");
                    String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                    GalleryItem item = new GalleryItem(id, caption, smallUrl);

                    items.add(item);
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * 从本地获取flickr.xml
     */
    public ArrayList<GalleryItem> fetchItems(Context context) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.flickr);
//            parser.setInput(new StringReader(xmlString));
            int eventType = parser.next();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                    String id = parser.getAttributeValue(null, "id");
                    String caption = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                    String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                    GalleryItem item = new GalleryItem(id, caption, smallUrl);

                    items.add(item);
                }

                eventType = parser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * 从本地获取search.xml
     */
    public ArrayList<GalleryItem> search(Context context, String query) {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.search);
//            parser.setInput(new StringReader(xmlString));
            int eventType = parser.next();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                    String id = parser.getAttributeValue(null, "id");
                    String caption = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                    String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                    GalleryItem item = new GalleryItem(id, caption, smallUrl);

                    items.add(item);
                }

                eventType = parser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return items;
    }
}
