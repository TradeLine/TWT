package org.tlsys.twt.rt.twt.util;

import org.tlsys.twt.JDictionary;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.util.TTimeZone;

import java.util.Optional;

@JSClass
public class TZoneInfo extends TTimeZone {

    private static JDictionary<TTimeZone> zones;

    static {
        zones = new JDictionary<>();
        zones.set(-12 * 60, new TZoneInfo("BIT", -12 * 60 * 60 * 1000));
        zones.set(-11 * 60, new TZoneInfo("SST", -11 * 60 * 60 * 1000));
        zones.set(-10 * 60, new TZoneInfo("HST", -10 * 60 * 60 * 1000));
        zones.set(-9 * 60 - 30, new TZoneInfo("MIT", -(9 * 60 + 30) * 60 * 1000));
        zones.set(-9 * 60, new TZoneInfo("AKST", -9 * 60 * 60 * 1000));
        zones.set(-8 * 60, new TZoneInfo("PST", -8 * 60 * 60 * 1000));
        zones.set(-7 * 60, new TZoneInfo("MST", -7 * 60 * 60 * 1000));
        zones.set(-6 * 60, new TZoneInfo("CST", -6 * 60 * 60 * 1000));
        zones.set(-5 * 60, new TZoneInfo("EST", -5 * 60 * 60 * 1000));

        zones.set(-4 * 60 - 30, new TZoneInfo("VET", -(4 * 60 + 30) * 60 * 1000));
        zones.set(-4 * 60, new TZoneInfo("CLT", -4 * 60 * 60 * 1000));
        zones.set(-3 * 60 - 30, new TZoneInfo("NST", -(3 * 60 + 30) * 60 * 1000));
        zones.set(-3 * 60, new TZoneInfo("BRT", -3 * 60 * 60 * 1000));
        zones.set(-2 * 60, new TZoneInfo("GST", -2 * 60 * 60 * 1000));
        zones.set(-1 * 60, new TZoneInfo("AZOST", -1 * 60 * 60 * 1000));
        zones.set(0, new TZoneInfo("GMT", 0));
        zones.set(1 * 60, new TZoneInfo("CET", 1 * 60 * 60 * 1000));
        zones.set(2 * 60, new TZoneInfo("EET", 2 * 60 * 60 * 1000));
        zones.set(3 * 60, new TZoneInfo("EEDT", 3 * 60 * 60 * 1000));
        zones.set(3 * 60 + 30, new TZoneInfo("IRST", (3 * 60 + 30) * 60 * 1000));
        zones.set(4 * 60, new TZoneInfo("MSD", 4 * 60 * 60 * 1000));
        zones.set(4 * 60 + 30, new TZoneInfo("AFT", (4 * 60 + 30) * 60 * 1000));
        zones.set(5 * 60, new TZoneInfo("PKT", 5 * 60 * 60 * 1000));
        zones.set(5 * 60 + 30, new TZoneInfo("IST", (5 * 60 + 30) * 60 * 1000));
        zones.set(6 * 60, new TZoneInfo("BST", 6 * 60 * 60 * 1000));
        zones.set(6 * 60 + 30, new TZoneInfo("MST", (6 * 60 + 30) * 60 * 1000));
        zones.set(7 * 60, new TZoneInfo("THA", 7 * 60 * 60 * 1000));
        zones.set(8 * 60, new TZoneInfo("AWST", 8 * 60 * 60 * 1000));
        zones.set(9 * 60, new TZoneInfo("AWDT", 9 * 60 * 60 * 1000));
        zones.set(9 * 60 + 30, new TZoneInfo("ACST", (9 * 60 + 30) * 60 * 1000));
        zones.set(10 * 60, new TZoneInfo("ACDT", 10 * 60 * 60 * 1000));
        zones.set(10 * 60 + 30, new TZoneInfo("ACDT", (10 * 60 + 30) * 60 * 1000));
        zones.set(11 * 60, new TZoneInfo("AEDT", 11 * 60 * 60 * 1000));
        zones.set(11 * 60 + 30, new TZoneInfo("NFT", (11 * 60 + 30) * 60 * 1000));
        zones.set(12 * 60, new TZoneInfo("NZST", 12 * 60 * 60 * 1000));
    }

    private int offset;

    public TZoneInfo(String id, int offset) {
        setID(id);
        this.offset = offset;
    }

    //var timezonenames = {

    // "-12":"BIT"};
    // "-11":"SST",
    // "-10":"HST",
    // "-9.5":"MIT",
    // "-9":"AKST",
    // "-8":"PST",
    // "-7":"MST",
    // "-6":"CST",
    // "-5":"EST",
    // "-4.5":"VET",
    // "-4":"CLT",
    // "-3.5":"NST",
    // "-3":"BRT",
    // "-2":"GST",
    // "-1":"AZOST",


    // "+0":"GMT",
    // "+1":"CET",
    // "+2":"EET",
    // "+3":"EEDT",
    // "+3.5":"IRST",
    // "+4":"MSD",
    // "+4.5":"AFT",
    // "+5":"PKT",
    // "+5.5":"IST",
    // "+6":"BST",
    // "+6.5":"MST",
    // "+7":"THA",
    // "+8":"AWST",
    // "+9":"AWDT",
    // "+9.5":"ACST",
    // "+10":"AEST",
    // "+10.5":"ACDT",
    // "+11":"AEDT",
    // "+11.5":"NFT",
    // "+12":"NZST",

    public static Optional<TTimeZone> getZoneByOffset(int offset) {
        return Optional.ofNullable(zones.get(offset));
    }

    @Override
    public int getRawOffset() {
        return offset;
    }
}
