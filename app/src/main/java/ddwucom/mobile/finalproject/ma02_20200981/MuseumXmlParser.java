package ddwucom.mobile.finalproject.ma02_20200981;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class MuseumXmlParser {
    private enum TagType {NONE, NAME ,TYPE, ADRESS, LAT, LNG, NUM, OPEN, CLOSE, REST};

    private final static String ITEM_TAG = "item";
    private final static String MUSEUM_TITLE = "fcltyNm";
    private final static String MUSEUM_TYPE = "fcltyType";
    private final static String MUSEUM_ADR = "rdnmadr";
    private final static String MUSEUM_LAT = "latitude";
    private final static String MUSEUM_LNG = "longitude";
    private final static String MUSEUM_TEL = "operPhoneNumber";
    private final static String MUSEUM_OPEN = "weekdayOperOpenHhmm";
    private final static String MUSEUM_CLOSE = "weekdayOperColseHhmm";
    private final static String MUSEUM_REST = "rstdeInfo";

    private XmlPullParser parser;

    public MuseumXmlParser() {
        //XmlPullParserFactory factory = null;
        try {
            //factory = XmlPullParserFactory.newInstance();
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MuseumDTO> parse(String xml) {
        ArrayList<MuseumDTO> resultList = new ArrayList<>();
        MuseumDTO dbo = null;
        TagType tagType = TagType.NONE;

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals(ITEM_TAG)) {
                            dbo = new MuseumDTO();
                        } else if (tag.equals(MUSEUM_TITLE) && dbo != null) {
                            tagType = TagType.NAME;
                        } else if (tag.equals(MUSEUM_TYPE)) {
                            tagType = TagType.TYPE;
                        } else if (tag.equals(MUSEUM_TEL)) {
                            tagType = TagType.NUM;
                        } else if (tag.equals(MUSEUM_ADR)) {
                            tagType = TagType.ADRESS;
                        } else if (tag.equals(MUSEUM_LAT) && dbo != null) {
                            tagType = TagType.LAT;
                        } else if (tag.equals(MUSEUM_LNG) && dbo != null) {
                            tagType = TagType.LNG;
                        } else if (tag.equals(MUSEUM_REST)) {
                            tagType = TagType.REST;
                        } else if (tag.equals(MUSEUM_OPEN)) {
                            tagType = TagType.OPEN;
                        } else if (tag.equals(MUSEUM_CLOSE)) {
                            tagType = TagType.CLOSE;
                        } else {
                            tagType = TagType.NONE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dbo);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case NAME:
                                dbo.setName(parser.getText());
                                break;
                            case TYPE:
                                dbo.setType(parser.getText());
                                break;
                            case ADRESS:
                                dbo.setAddress(parser.getText());
                                break;
                            case NUM:
                                dbo.setNum(parser.getText());
                                break;
                            case REST:
                                dbo.setRestDay(parser.getText());
                                break;
                            case OPEN:
                                dbo.setOpen(parser.getText());
                                break;
                            case CLOSE:
                                dbo.setClose(parser.getText());
                                break;
                            case LAT:
                                dbo.setLat(parser.getText());
                                break;
                            case LNG:
                                dbo.setLng(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
