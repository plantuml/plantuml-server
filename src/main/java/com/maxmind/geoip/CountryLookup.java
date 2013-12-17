package com.maxmind.geoip;
/* Only works with GeoIP Country Edition */
/* For Geoip City Edition, use CityLookup.java */

import java.io.IOException;

class CountryLookup {
    public static void main(String[] args) {
	try {
	    String sep = System.getProperty("file.separator");

	    // Uncomment for windows
	    // String dir = System.getProperty("user.dir");

	    // Uncomment for Linux
	    String dir = "C:/eclipse";

	    String dbfile = dir + sep + "GeoIP.dat";
	    // You should only call LookupService once, especially if you use
	    // GEOIP_MEMORY_CACHE mode, since the LookupService constructor takes up
	    // resources to load the GeoIP.dat file into memory
	    //LookupService cl = new LookupService(dbfile,LookupService.GEOIP_STANDARD);
	    LookupService cl = new LookupService(dbfile,LookupService.GEOIP_MEMORY_CACHE);

	    System.out.println(cl.getCountry("151.38.39.114").getCode());
	    System.out.println(cl.getCountry("151.38.39.114").getName());
	    System.out.println(cl.getCountry("12.25.205.51").getName());
	    System.out.println(cl.getCountry("64.81.104.131").getName());
	    System.out.println(cl.getCountry("200.21.225.82").getName());

	    cl.close();
	}
	catch (IOException e) {
	    System.out.println("IO Exception");
	}
    }
}
