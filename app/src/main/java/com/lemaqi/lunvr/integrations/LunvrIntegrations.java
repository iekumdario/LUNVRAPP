package com.lemaqi.lunvr.integrations;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.media.MediaPlayer;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Melly on 04/23/2016.
 */
public class LunvrIntegrations {
    Context lunvrContext;
    Location location;
    float north;
    MediaPlayer mediaPlayer;

    public LunvrIntegrations(Context lunvrContext) {
        this.lunvrContext = lunvrContext;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @JavascriptInterface
    public String getGeoposition() {
        JSONObject locationObject = new JSONObject();
        try {
            locationObject.put("lat", location.getLatitude());
            locationObject.put("lon", location.getLongitude());
            //geoIntegration.setLocation(locationObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return locationObject.toString();
        }
    }

    public void setNorth(float northDegree) {
        this.north = (float)(Math.toRadians(northDegree));
    }

    @JavascriptInterface
    public float getNorth() {
        return this.north;
    }

    // FROM: http://raingod.com/raingod/resources/Programming/Java/Software/Moon/javafiles/MoonCalculation.java
    // day_year - gives the day of the year for the first day of each
    // month -1. i.e. 1st January is the 0th day of the year, 1st
    // February is the 31st etc. Used by 'moonPhase'.

    private static final int    day_year[] = { -1, -1, 30, 58, 89, 119,
            150, 180, 211, 241, 272,
            303, 333 };

    // moon_phase_name - the English name for the different phases.
    // Change this if you need to localise the software.

    private static final String moon_phase_name[] = { "New",
            "Waxing crescent",
            "First quarter",
            "Waxing gibbous",
            "Full",
            "Waning gibbous",
            "Third quarter",
            "Waning crescent" };

    @JavascriptInterface
    public String  getMoonPhase() {
        Calendar calander = Calendar.getInstance();
        int day = calander.get(Calendar.DAY_OF_MONTH);
        int month = calander.get(Calendar.MONTH) + 1;
        int year = calander.get(Calendar.YEAR);

        int             phase;          // Moon phase
        int             cent;           // Century number (1979 = 20)
        int             epact;          // Age of the moon on Jan. 1
        int             diy;            // Day in the year
        int             golden;         // Moon's golden number

        if (month < 0 || month > 12) month = 0;     // Just in case
        diy = day + day_year[month];                // Day in the year
        if ((month > 2) && this.isLeapYearP(year))
            diy++;                                  // Leapyear fixup
        cent = (year / 100) + 1;                    // Century number
        golden = (year % 19) + 1;                   // Golden number
        epact = ((11 * golden) + 20                 // Golden number
                + (((8 * cent) + 5) / 25) - 5       // 400 year cycle
                - (((3 * cent) / 4) - 12)) % 30;    //Leap year correction
        if (epact <= 0)
            epact += 30;                        // Age range is 1 .. 30
        if ((epact == 25 && golden > 11) ||
                epact == 24)
            epact++;

        // Calculate the phase, using the magic numbers defined above.
        // Note that (phase and 7) is equivalent to (phase mod 8) and
        // is needed on two days per year (when the algorithm yields 8).

        phase = (((((diy + epact) * 6) + 11) % 177) / 22) & 7;

        JSONObject phaseObject = new JSONObject();
        try {
            phaseObject.put("phase", phase);
            phaseObject.put("phaseName", moon_phase_name[phase]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return(phaseObject.toString());
        }

    }

    // isLeapYearP
    //
    // Return true if the year is a leapyear

    public  boolean isLeapYearP(int year) {
        return ((year % 4 == 0) &&
                ((year % 400 == 0) || (year % 100 != 0)));
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @JavascriptInterface
    public void printTest(String test) {
        Log.i("spaceapp", "TEST: " + test);
    }

    @JavascriptInterface
    public void playSound(String audioName) {
        try {
            AssetFileDescriptor afd = lunvrContext.getAssets().openFd("lunvrjs/" + audioName);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
