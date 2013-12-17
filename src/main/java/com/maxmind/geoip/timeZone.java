package com.maxmind.geoip;

// generated automatically from admin/generate_timeZone.pl
public class timeZone {
	static public String timeZoneByCountryAndRegion(String country,
			String region) {
		String timezone = null;
		if (country == null) {
			return null;
		}
		if (region == null) {
			region = "";
		}
		if (country.equals("US")) {
			if (region.equals("AL")) {
				timezone = "America/Chicago";
			} else if (region.equals("AK")) {
				timezone = "America/Anchorage";
			} else if (region.equals("AZ")) {
				timezone = "America/Phoenix";
			} else if (region.equals("AR")) {
				timezone = "America/Chicago";
			} else if (region.equals("CA")) {
				timezone = "America/Los_Angeles";
			} else if (region.equals("CO")) {
				timezone = "America/Denver";
			} else if (region.equals("CT")) {
				timezone = "America/New_York";
			} else if (region.equals("DE")) {
				timezone = "America/New_York";
			} else if (region.equals("DC")) {
				timezone = "America/New_York";
			} else if (region.equals("FL")) {
				timezone = "America/New_York";
			} else if (region.equals("GA")) {
				timezone = "America/New_York";
			} else if (region.equals("HI")) {
				timezone = "Pacific/Honolulu";
			} else if (region.equals("ID")) {
				timezone = "America/Denver";
			} else if (region.equals("IL")) {
				timezone = "America/Chicago";
			} else if (region.equals("IN")) {
				timezone = "America/Indiana/Indianapolis";
			} else if (region.equals("IA")) {
				timezone = "America/Chicago";
			} else if (region.equals("KS")) {
				timezone = "America/Chicago";
			} else if (region.equals("KY")) {
				timezone = "America/New_York";
			} else if (region.equals("LA")) {
				timezone = "America/Chicago";
			} else if (region.equals("ME")) {
				timezone = "America/New_York";
			} else if (region.equals("MD")) {
				timezone = "America/New_York";
			} else if (region.equals("MA")) {
				timezone = "America/New_York";
			} else if (region.equals("MI")) {
				timezone = "America/New_York";
			} else if (region.equals("MN")) {
				timezone = "America/Chicago";
			} else if (region.equals("MS")) {
				timezone = "America/Chicago";
			} else if (region.equals("MO")) {
				timezone = "America/Chicago";
			} else if (region.equals("MT")) {
				timezone = "America/Denver";
			} else if (region.equals("NE")) {
				timezone = "America/Chicago";
			} else if (region.equals("NV")) {
				timezone = "America/Los_Angeles";
			} else if (region.equals("NH")) {
				timezone = "America/New_York";
			} else if (region.equals("NJ")) {
				timezone = "America/New_York";
			} else if (region.equals("NM")) {
				timezone = "America/Denver";
			} else if (region.equals("NY")) {
				timezone = "America/New_York";
			} else if (region.equals("NC")) {
				timezone = "America/New_York";
			} else if (region.equals("ND")) {
				timezone = "America/Chicago";
			} else if (region.equals("OH")) {
				timezone = "America/New_York";
			} else if (region.equals("OK")) {
				timezone = "America/Chicago";
			} else if (region.equals("OR")) {
				timezone = "America/Los_Angeles";
			} else if (region.equals("PA")) {
				timezone = "America/New_York";
			} else if (region.equals("RI")) {
				timezone = "America/New_York";
			} else if (region.equals("SC")) {
				timezone = "America/New_York";
			} else if (region.equals("SD")) {
				timezone = "America/Chicago";
			} else if (region.equals("TN")) {
				timezone = "America/Chicago";
			} else if (region.equals("TX")) {
				timezone = "America/Chicago";
			} else if (region.equals("UT")) {
				timezone = "America/Denver";
			} else if (region.equals("VT")) {
				timezone = "America/New_York";
			} else if (region.equals("VA")) {
				timezone = "America/New_York";
			} else if (region.equals("WA")) {
				timezone = "America/Los_Angeles";
			} else if (region.equals("WV")) {
				timezone = "America/New_York";
			} else if (region.equals("WI")) {
				timezone = "America/Chicago";
			} else if (region.equals("WY")) {
				timezone = "America/Denver";
			}
		} else if (country.equals("CA")) {
			if (region.equals("AB")) {
				timezone = "America/Edmonton";
			} else if (region.equals("BC")) {
				timezone = "America/Vancouver";
			} else if (region.equals("MB")) {
				timezone = "America/Winnipeg";
			} else if (region.equals("NB")) {
				timezone = "America/Halifax";
			} else if (region.equals("NL")) {
				timezone = "America/St_Johns";
			} else if (region.equals("NT")) {
				timezone = "America/Yellowknife";
			} else if (region.equals("NS")) {
				timezone = "America/Halifax";
			} else if (region.equals("NU")) {
				timezone = "America/Rankin_Inlet";
			} else if (region.equals("ON")) {
				timezone = "America/Toronto";
			} else if (region.equals("PE")) {
				timezone = "America/Halifax";
			} else if (region.equals("QC")) {
				timezone = "America/Montreal";
			} else if (region.equals("SK")) {
				timezone = "America/Regina";
			} else if (region.equals("YT")) {
				timezone = "America/Whitehorse";
			}
		} else if (country.equals("AU")) {
			if (region.equals("01")) {
				timezone = "Australia/Sydney";
			} else if (region.equals("02")) {
				timezone = "Australia/Sydney";
			} else if (region.equals("03")) {
				timezone = "Australia/Darwin";
			} else if (region.equals("04")) {
				timezone = "Australia/Brisbane";
			} else if (region.equals("05")) {
				timezone = "Australia/Adelaide";
			} else if (region.equals("06")) {
				timezone = "Australia/Hobart";
			} else if (region.equals("07")) {
				timezone = "Australia/Melbourne";
			} else if (region.equals("08")) {
				timezone = "Australia/Perth";
			}
		} else if (country.equals("AS")) {
			timezone = "Pacific/Pago_Pago";
		} else if (country.equals("CI")) {
			timezone = "Africa/Abidjan";
		} else if (country.equals("GH")) {
			timezone = "Africa/Accra";
		} else if (country.equals("DZ")) {
			timezone = "Africa/Algiers";
		} else if (country.equals("ER")) {
			timezone = "Africa/Asmara";
		} else if (country.equals("ML")) {
			timezone = "Africa/Bamako";
		} else if (country.equals("CF")) {
			timezone = "Africa/Bangui";
		} else if (country.equals("GM")) {
			timezone = "Africa/Banjul";
		} else if (country.equals("GW")) {
			timezone = "Africa/Bissau";
		} else if (country.equals("CG")) {
			timezone = "Africa/Brazzaville";
		} else if (country.equals("BI")) {
			timezone = "Africa/Bujumbura";
		} else if (country.equals("EG")) {
			timezone = "Africa/Cairo";
		} else if (country.equals("MA")) {
			timezone = "Africa/Casablanca";
		} else if (country.equals("GN")) {
			timezone = "Africa/Conakry";
		} else if (country.equals("SN")) {
			timezone = "Africa/Dakar";
		} else if (country.equals("DJ")) {
			timezone = "Africa/Djibouti";
		} else if (country.equals("SL")) {
			timezone = "Africa/Freetown";
		} else if (country.equals("BW")) {
			timezone = "Africa/Gaborone";
		} else if (country.equals("ZW")) {
			timezone = "Africa/Harare";
		} else if (country.equals("ZA")) {
			timezone = "Africa/Johannesburg";
		} else if (country.equals("UG")) {
			timezone = "Africa/Kampala";
		} else if (country.equals("SD")) {
			timezone = "Africa/Khartoum";
		} else if (country.equals("SS")) {
			timezone = "Africa/Juba";
		} else if (country.equals("RW")) {
			timezone = "Africa/Kigali";
		} else if (country.equals("NG")) {
			timezone = "Africa/Lagos";
		} else if (country.equals("GA")) {
			timezone = "Africa/Libreville";
		} else if (country.equals("TG")) {
			timezone = "Africa/Lome";
		} else if (country.equals("AO")) {
			timezone = "Africa/Luanda";
		} else if (country.equals("ZM")) {
			timezone = "Africa/Lusaka";
		} else if (country.equals("GQ")) {
			timezone = "Africa/Malabo";
		} else if (country.equals("MZ")) {
			timezone = "Africa/Maputo";
		} else if (country.equals("LS")) {
			timezone = "Africa/Maseru";
		} else if (country.equals("SZ")) {
			timezone = "Africa/Mbabane";
		} else if (country.equals("SO")) {
			timezone = "Africa/Mogadishu";
		} else if (country.equals("LR")) {
			timezone = "Africa/Monrovia";
		} else if (country.equals("KE")) {
			timezone = "Africa/Nairobi";
		} else if (country.equals("TD")) {
			timezone = "Africa/Ndjamena";
		} else if (country.equals("NE")) {
			timezone = "Africa/Niamey";
		} else if (country.equals("MR")) {
			timezone = "Africa/Nouakchott";
		} else if (country.equals("BF")) {
			timezone = "Africa/Ouagadougou";
		} else if (country.equals("ST")) {
			timezone = "Africa/Sao_Tome";
		} else if (country.equals("LY")) {
			timezone = "Africa/Tripoli";
		} else if (country.equals("TN")) {
			timezone = "Africa/Tunis";
		} else if (country.equals("AI")) {
			timezone = "America/Anguilla";
		} else if (country.equals("AG")) {
			timezone = "America/Antigua";
		} else if (country.equals("AW")) {
			timezone = "America/Aruba";
		} else if (country.equals("BB")) {
			timezone = "America/Barbados";
		} else if (country.equals("BZ")) {
			timezone = "America/Belize";
		} else if (country.equals("CO")) {
			timezone = "America/Bogota";
		} else if (country.equals("VE")) {
			timezone = "America/Caracas";
		} else if (country.equals("KY")) {
			timezone = "America/Cayman";
		} else if (country.equals("CR")) {
			timezone = "America/Costa_Rica";
		} else if (country.equals("DM")) {
			timezone = "America/Dominica";
		} else if (country.equals("SV")) {
			timezone = "America/El_Salvador";
		} else if (country.equals("GD")) {
			timezone = "America/Grenada";
		} else if (country.equals("FR")) {
			timezone = "Europe/Paris";
		} else if (country.equals("GP")) {
			timezone = "America/Guadeloupe";
		} else if (country.equals("GT")) {
			timezone = "America/Guatemala";
		} else if (country.equals("GY")) {
			timezone = "America/Guyana";
		} else if (country.equals("CU")) {
			timezone = "America/Havana";
		} else if (country.equals("JM")) {
			timezone = "America/Jamaica";
		} else if (country.equals("BO")) {
			timezone = "America/La_Paz";
		} else if (country.equals("PE")) {
			timezone = "America/Lima";
		} else if (country.equals("NI")) {
			timezone = "America/Managua";
		} else if (country.equals("MQ")) {
			timezone = "America/Martinique";
		} else if (country.equals("UY")) {
			timezone = "America/Montevideo";
		} else if (country.equals("MS")) {
			timezone = "America/Montserrat";
		} else if (country.equals("BS")) {
			timezone = "America/Nassau";
		} else if (country.equals("PA")) {
			timezone = "America/Panama";
		} else if (country.equals("SR")) {
			timezone = "America/Paramaribo";
		} else if (country.equals("PR")) {
			timezone = "America/Puerto_Rico";
		} else if (country.equals("KN")) {
			timezone = "America/St_Kitts";
		} else if (country.equals("LC")) {
			timezone = "America/St_Lucia";
		} else if (country.equals("VC")) {
			timezone = "America/St_Vincent";
		} else if (country.equals("HN")) {
			timezone = "America/Tegucigalpa";
		} else if (country.equals("YE")) {
			timezone = "Asia/Aden";
		} else if (country.equals("JO")) {
			timezone = "Asia/Amman";
		} else if (country.equals("TM")) {
			timezone = "Asia/Ashgabat";
		} else if (country.equals("IQ")) {
			timezone = "Asia/Baghdad";
		} else if (country.equals("BH")) {
			timezone = "Asia/Bahrain";
		} else if (country.equals("AZ")) {
			timezone = "Asia/Baku";
		} else if (country.equals("TH")) {
			timezone = "Asia/Bangkok";
		} else if (country.equals("LB")) {
			timezone = "Asia/Beirut";
		} else if (country.equals("KG")) {
			timezone = "Asia/Bishkek";
		} else if (country.equals("BN")) {
			timezone = "Asia/Brunei";
		} else if (country.equals("IN")) {
			timezone = "Asia/Kolkata";
		} else if (country.equals("LK")) {
			timezone = "Asia/Colombo";
		} else if (country.equals("BD")) {
			timezone = "Asia/Dhaka";
		} else if (country.equals("AE")) {
			timezone = "Asia/Dubai";
		} else if (country.equals("TJ")) {
			timezone = "Asia/Dushanbe";
		} else if (country.equals("HK")) {
			timezone = "Asia/Hong_Kong";
		} else if (country.equals("TR")) {
			timezone = "Asia/Istanbul";
		} else if (country.equals("IL")) {
			timezone = "Asia/Jerusalem";
		} else if (country.equals("AF")) {
			timezone = "Asia/Kabul";
		} else if (country.equals("PK")) {
			timezone = "Asia/Karachi";
		} else if (country.equals("NP")) {
			timezone = "Asia/Kathmandu";
		} else if (country.equals("KW")) {
			timezone = "Asia/Kuwait";
		} else if (country.equals("MO")) {
			timezone = "Asia/Macau";
		} else if (country.equals("PH")) {
			timezone = "Asia/Manila";
		} else if (country.equals("OM")) {
			timezone = "Asia/Muscat";
		} else if (country.equals("CY")) {
			timezone = "Asia/Nicosia";
		} else if (country.equals("KP")) {
			timezone = "Asia/Pyongyang";
		} else if (country.equals("QA")) {
			timezone = "Asia/Qatar";
		} else if (country.equals("MM")) {
			timezone = "Asia/Rangoon";
		} else if (country.equals("SA")) {
			timezone = "Asia/Riyadh";
		} else if (country.equals("KR")) {
			timezone = "Asia/Seoul";
		} else if (country.equals("SG")) {
			timezone = "Asia/Singapore";
		} else if (country.equals("TW")) {
			timezone = "Asia/Taipei";
		} else if (country.equals("GE")) {
			timezone = "Asia/Tbilisi";
		} else if (country.equals("BT")) {
			timezone = "Asia/Thimphu";
		} else if (country.equals("JP")) {
			timezone = "Asia/Tokyo";
		} else if (country.equals("LA")) {
			timezone = "Asia/Vientiane";
		} else if (country.equals("AM")) {
			timezone = "Asia/Yerevan";
		} else if (country.equals("BM")) {
			timezone = "Atlantic/Bermuda";
		} else if (country.equals("CV")) {
			timezone = "Atlantic/Cape_Verde";
		} else if (country.equals("FO")) {
			timezone = "Atlantic/Faroe";
		} else if (country.equals("IS")) {
			timezone = "Atlantic/Reykjavik";
		} else if (country.equals("GS")) {
			timezone = "Atlantic/South_Georgia";
		} else if (country.equals("SH")) {
			timezone = "Atlantic/St_Helena";
		} else if (country.equals("CL")) {
			timezone = "America/Santiago";
		} else if (country.equals("NL")) {
			timezone = "Europe/Amsterdam";
		} else if (country.equals("AD")) {
			timezone = "Europe/Andorra";
		} else if (country.equals("GR")) {
			timezone = "Europe/Athens";
		} else if (country.equals("YU")) {
			timezone = "Europe/Belgrade";
		} else if (country.equals("DE")) {
			timezone = "Europe/Berlin";
		} else if (country.equals("SK")) {
			timezone = "Europe/Bratislava";
		} else if (country.equals("BE")) {
			timezone = "Europe/Brussels";
		} else if (country.equals("RO")) {
			timezone = "Europe/Bucharest";
		} else if (country.equals("HU")) {
			timezone = "Europe/Budapest";
		} else if (country.equals("DK")) {
			timezone = "Europe/Copenhagen";
		} else if (country.equals("IE")) {
			timezone = "Europe/Dublin";
		} else if (country.equals("GI")) {
			timezone = "Europe/Gibraltar";
		} else if (country.equals("FI")) {
			timezone = "Europe/Helsinki";
		} else if (country.equals("SI")) {
			timezone = "Europe/Ljubljana";
		} else if (country.equals("GB")) {
			timezone = "Europe/London";
		} else if (country.equals("LU")) {
			timezone = "Europe/Luxembourg";
		} else if (country.equals("MT")) {
			timezone = "Europe/Malta";
		} else if (country.equals("BY")) {
			timezone = "Europe/Minsk";
		} else if (country.equals("MC")) {
			timezone = "Europe/Monaco";
		} else if (country.equals("NO")) {
			timezone = "Europe/Oslo";
		} else if (country.equals("CZ")) {
			timezone = "Europe/Prague";
		} else if (country.equals("LV")) {
			timezone = "Europe/Riga";
		} else if (country.equals("IT")) {
			timezone = "Europe/Rome";
		} else if (country.equals("SM")) {
			timezone = "Europe/San_Marino";
		} else if (country.equals("BA")) {
			timezone = "Europe/Sarajevo";
		} else if (country.equals("MK")) {
			timezone = "Europe/Skopje";
		} else if (country.equals("BG")) {
			timezone = "Europe/Sofia";
		} else if (country.equals("SE")) {
			timezone = "Europe/Stockholm";
		} else if (country.equals("EE")) {
			timezone = "Europe/Tallinn";
		} else if (country.equals("AL")) {
			timezone = "Europe/Tirane";
		} else if (country.equals("LI")) {
			timezone = "Europe/Vaduz";
		} else if (country.equals("VA")) {
			timezone = "Europe/Vatican";
		} else if (country.equals("AT")) {
			timezone = "Europe/Vienna";
		} else if (country.equals("LT")) {
			timezone = "Europe/Vilnius";
		} else if (country.equals("PL")) {
			timezone = "Europe/Warsaw";
		} else if (country.equals("HR")) {
			timezone = "Europe/Zagreb";
		} else if (country.equals("IR")) {
			timezone = "Asia/Tehran";
		} else if (country.equals("MG")) {
			timezone = "Indian/Antananarivo";
		} else if (country.equals("CX")) {
			timezone = "Indian/Christmas";
		} else if (country.equals("CC")) {
			timezone = "Indian/Cocos";
		} else if (country.equals("KM")) {
			timezone = "Indian/Comoro";
		} else if (country.equals("MV")) {
			timezone = "Indian/Maldives";
		} else if (country.equals("MU")) {
			timezone = "Indian/Mauritius";
		} else if (country.equals("YT")) {
			timezone = "Indian/Mayotte";
		} else if (country.equals("RE")) {
			timezone = "Indian/Reunion";
		} else if (country.equals("FJ")) {
			timezone = "Pacific/Fiji";
		} else if (country.equals("TV")) {
			timezone = "Pacific/Funafuti";
		} else if (country.equals("GU")) {
			timezone = "Pacific/Guam";
		} else if (country.equals("NR")) {
			timezone = "Pacific/Nauru";
		} else if (country.equals("NU")) {
			timezone = "Pacific/Niue";
		} else if (country.equals("NF")) {
			timezone = "Pacific/Norfolk";
		} else if (country.equals("PW")) {
			timezone = "Pacific/Palau";
		} else if (country.equals("PN")) {
			timezone = "Pacific/Pitcairn";
		} else if (country.equals("CK")) {
			timezone = "Pacific/Rarotonga";
		} else if (country.equals("WS")) {
			timezone = "Pacific/Pago_Pago";
		} else if (country.equals("KI")) {
			timezone = "Pacific/Tarawa";
		} else if (country.equals("TO")) {
			timezone = "Pacific/Tongatapu";
		} else if (country.equals("WF")) {
			timezone = "Pacific/Wallis";
		} else if (country.equals("TZ")) {
			timezone = "Africa/Dar_es_Salaam";
		} else if (country.equals("VN")) {
			timezone = "Asia/Phnom_Penh";
		} else if (country.equals("KH")) {
			timezone = "Asia/Phnom_Penh";
		} else if (country.equals("CM")) {
			timezone = "Africa/Lagos";
		} else if (country.equals("DO")) {
			timezone = "America/Santo_Domingo";
		} else if (country.equals("ET")) {
			timezone = "Africa/Addis_Ababa";
		} else if (country.equals("FX")) {
			timezone = "Europe/Paris";
		} else if (country.equals("HT")) {
			timezone = "America/Port-au-Prince";
		} else if (country.equals("CH")) {
			timezone = "Europe/Zurich";
		} else if (country.equals("AN")) {
			timezone = "America/Curacao";
		} else if (country.equals("BJ")) {
			timezone = "Africa/Porto-Novo";
		} else if (country.equals("EH")) {
			timezone = "Africa/El_Aaiun";
		} else if (country.equals("FK")) {
			timezone = "Atlantic/Stanley";
		} else if (country.equals("GF")) {
			timezone = "America/Cayenne";
		} else if (country.equals("IO")) {
			timezone = "Indian/Chagos";
		} else if (country.equals("MD")) {
			timezone = "Europe/Chisinau";
		} else if (country.equals("MP")) {
			timezone = "Pacific/Saipan";
		} else if (country.equals("MW")) {
			timezone = "Africa/Blantyre";
		} else if (country.equals("NA")) {
			timezone = "Africa/Windhoek";
		} else if (country.equals("NC")) {
			timezone = "Pacific/Noumea";
		} else if (country.equals("PG")) {
			timezone = "Pacific/Port_Moresby";
		} else if (country.equals("PM")) {
			timezone = "America/Miquelon";
		} else if (country.equals("PS")) {
			timezone = "Asia/Gaza";
		} else if (country.equals("PY")) {
			timezone = "America/Asuncion";
		} else if (country.equals("SB")) {
			timezone = "Pacific/Guadalcanal";
		} else if (country.equals("SC")) {
			timezone = "Indian/Mahe";
		} else if (country.equals("SJ")) {
			timezone = "Arctic/Longyearbyen";
		} else if (country.equals("SY")) {
			timezone = "Asia/Damascus";
		} else if (country.equals("TC")) {
			timezone = "America/Grand_Turk";
		} else if (country.equals("TF")) {
			timezone = "Indian/Kerguelen";
		} else if (country.equals("TK")) {
			timezone = "Pacific/Fakaofo";
		} else if (country.equals("TT")) {
			timezone = "America/Port_of_Spain";
		} else if (country.equals("VG")) {
			timezone = "America/Tortola";
		} else if (country.equals("VI")) {
			timezone = "America/St_Thomas";
		} else if (country.equals("VU")) {
			timezone = "Pacific/Efate";
		} else if (country.equals("RS")) {
			timezone = "Europe/Belgrade";
		} else if (country.equals("ME")) {
			timezone = "Europe/Podgorica";
		} else if (country.equals("AX")) {
			timezone = "Europe/Mariehamn";
		} else if (country.equals("GG")) {
			timezone = "Europe/Guernsey";
		} else if (country.equals("IM")) {
			timezone = "Europe/Isle_of_Man";
		} else if (country.equals("JE")) {
			timezone = "Europe/Jersey";
		} else if (country.equals("BL")) {
			timezone = "America/St_Barthelemy";
		} else if (country.equals("MF")) {
			timezone = "America/Marigot";
		} else if (country.equals("MH")) {
			timezone = "Pacific/Kwajalein";
		} else if (country.equals("UM")) {
			timezone = "Pacific/Wake";
		} else if (country.equals("AR")) {
			if (region.equals("01")) {
				timezone = "America/Argentina/Buenos_Aires";
			} else if (region.equals("02")) {
				timezone = "America/Argentina/Catamarca";
			} else if (region.equals("03")) {
				timezone = "America/Argentina/Tucuman";
			} else if (region.equals("04")) {
				timezone = "America/Argentina/Rio_Gallegos";
			} else if (region.equals("05")) {
				timezone = "America/Argentina/Cordoba";
			} else if (region.equals("06")) {
				timezone = "America/Argentina/Tucuman";
			} else if (region.equals("07")) {
				timezone = "America/Argentina/Buenos_Aires";
			} else if (region.equals("08")) {
				timezone = "America/Argentina/Buenos_Aires";
			} else if (region.equals("09")) {
				timezone = "America/Argentina/Tucuman";
			} else if (region.equals("10")) {
				timezone = "America/Argentina/Jujuy";
			} else if (region.equals("11")) {
				timezone = "America/Argentina/San_Luis";
			} else if (region.equals("12")) {
				timezone = "America/Argentina/La_Rioja";
			} else if (region.equals("13")) {
				timezone = "America/Argentina/Mendoza";
			} else if (region.equals("14")) {
				timezone = "America/Argentina/Buenos_Aires";
			} else if (region.equals("15")) {
				timezone = "America/Argentina/San_Luis";
			} else if (region.equals("16")) {
				timezone = "America/Argentina/Buenos_Aires";
			} else if (region.equals("17")) {
				timezone = "America/Argentina/Salta";
			} else if (region.equals("18")) {
				timezone = "America/Argentina/San_Juan";
			} else if (region.equals("19")) {
				timezone = "America/Argentina/San_Luis";
			} else if (region.equals("20")) {
				timezone = "America/Argentina/Rio_Gallegos";
			} else if (region.equals("21")) {
				timezone = "America/Argentina/Buenos_Aires";
			} else if (region.equals("22")) {
				timezone = "America/Argentina/Catamarca";
			} else if (region.equals("23")) {
				timezone = "America/Argentina/Ushuaia";
			} else if (region.equals("24")) {
				timezone = "America/Argentina/Tucuman";
			}
		} else if (country.equals("BR")) {
			if (region.equals("01")) {
				timezone = "America/Rio_Branco";
			} else if (region.equals("02")) {
				timezone = "America/Maceio";
			} else if (region.equals("03")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("04")) {
				timezone = "America/Manaus";
			} else if (region.equals("05")) {
				timezone = "America/Bahia";
			} else if (region.equals("06")) {
				timezone = "America/Fortaleza";
			} else if (region.equals("07")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("08")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("11")) {
				timezone = "America/Campo_Grande";
			} else if (region.equals("13")) {
				timezone = "America/Belem";
			} else if (region.equals("14")) {
				timezone = "America/Cuiaba";
			} else if (region.equals("15")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("16")) {
				timezone = "America/Belem";
			} else if (region.equals("17")) {
				timezone = "America/Recife";
			} else if (region.equals("18")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("20")) {
				timezone = "America/Fortaleza";
			} else if (region.equals("21")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("22")) {
				timezone = "America/Recife";
			} else if (region.equals("23")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("24")) {
				timezone = "America/Porto_Velho";
			} else if (region.equals("25")) {
				timezone = "America/Boa_Vista";
			} else if (region.equals("26")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("27")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("28")) {
				timezone = "America/Maceio";
			} else if (region.equals("29")) {
				timezone = "America/Sao_Paulo";
			} else if (region.equals("30")) {
				timezone = "America/Recife";
			} else if (region.equals("31")) {
				timezone = "America/Araguaina";
			}
		} else if (country.equals("CD")) {
			if (region.equals("01")) {
				timezone = "Africa/Kinshasa";
			} else if (region.equals("02")) {
				timezone = "Africa/Kinshasa";
			} else if (region.equals("03")) {
				timezone = "Africa/Kinshasa";
			} else if (region.equals("04")) {
				timezone = "Africa/Lubumbashi";
			} else if (region.equals("05")) {
				timezone = "Africa/Lubumbashi";
			} else if (region.equals("06")) {
				timezone = "Africa/Kinshasa";
			} else if (region.equals("07")) {
				timezone = "Africa/Lubumbashi";
			} else if (region.equals("08")) {
				timezone = "Africa/Kinshasa";
			} else if (region.equals("09")) {
				timezone = "Africa/Lubumbashi";
			} else if (region.equals("10")) {
				timezone = "Africa/Lubumbashi";
			} else if (region.equals("11")) {
				timezone = "Africa/Lubumbashi";
			} else if (region.equals("12")) {
				timezone = "Africa/Lubumbashi";
			}
		} else if (country.equals("CN")) {
			if (region.equals("01")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("02")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("03")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("04")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("05")) {
				timezone = "Asia/Harbin";
			} else if (region.equals("06")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("07")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("08")) {
				timezone = "Asia/Harbin";
			} else if (region.equals("09")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("10")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("11")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("12")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("13")) {
				timezone = "Asia/Urumqi";
			} else if (region.equals("14")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("15")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("16")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("18")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("19")) {
				timezone = "Asia/Harbin";
			} else if (region.equals("20")) {
				timezone = "Asia/Harbin";
			} else if (region.equals("21")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("22")) {
				timezone = "Asia/Harbin";
			} else if (region.equals("23")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("24")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("25")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("26")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("28")) {
				timezone = "Asia/Shanghai";
			} else if (region.equals("29")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("30")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("31")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("32")) {
				timezone = "Asia/Chongqing";
			} else if (region.equals("33")) {
				timezone = "Asia/Chongqing";
			}
		} else if (country.equals("EC")) {
			if (region.equals("01")) {
				timezone = "Pacific/Galapagos";
			} else if (region.equals("02")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("03")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("04")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("05")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("06")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("07")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("08")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("09")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("10")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("11")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("12")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("13")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("14")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("15")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("17")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("18")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("19")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("20")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("22")) {
				timezone = "America/Guayaquil";
			} else if (region.equals("24")) {
				timezone = "America/Guayaquil";
			}
		} else if (country.equals("ES")) {
			if (region.equals("07")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("27")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("29")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("31")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("32")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("34")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("39")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("51")) {
				timezone = "Africa/Ceuta";
			} else if (region.equals("52")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("53")) {
				timezone = "Atlantic/Canary";
			} else if (region.equals("54")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("55")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("56")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("57")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("58")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("59")) {
				timezone = "Europe/Madrid";
			} else if (region.equals("60")) {
				timezone = "Europe/Madrid";
			}
		} else if (country.equals("GL")) {
			if (region.equals("01")) {
				timezone = "America/Thule";
			} else if (region.equals("02")) {
				timezone = "America/Godthab";
			} else if (region.equals("03")) {
				timezone = "America/Godthab";
			}
		} else if (country.equals("ID")) {
			if (region.equals("01")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("02")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("03")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("04")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("05")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("06")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("07")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("08")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("09")) {
				timezone = "Asia/Jayapura";
			} else if (region.equals("10")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("11")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("12")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("13")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("14")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("15")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("16")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("17")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("18")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("19")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("20")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("21")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("22")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("23")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("24")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("25")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("26")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("28")) {
				timezone = "Asia/Jayapura";
			} else if (region.equals("29")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("30")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("31")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("32")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("33")) {
				timezone = "Asia/Jakarta";
			} else if (region.equals("34")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("35")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("36")) {
				timezone = "Asia/Jayapura";
			} else if (region.equals("37")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("38")) {
				timezone = "Asia/Makassar";
			} else if (region.equals("39")) {
				timezone = "Asia/Jayapura";
			} else if (region.equals("40")) {
				timezone = "Asia/Pontianak";
			} else if (region.equals("41")) {
				timezone = "Asia/Makassar";
			}
		} else if (country.equals("KZ")) {
			if (region.equals("01")) {
				timezone = "Asia/Almaty";
			} else if (region.equals("02")) {
				timezone = "Asia/Almaty";
			} else if (region.equals("03")) {
				timezone = "Asia/Qyzylorda";
			} else if (region.equals("04")) {
				timezone = "Asia/Aqtobe";
			} else if (region.equals("05")) {
				timezone = "Asia/Qyzylorda";
			} else if (region.equals("06")) {
				timezone = "Asia/Aqtau";
			} else if (region.equals("07")) {
				timezone = "Asia/Oral";
			} else if (region.equals("08")) {
				timezone = "Asia/Qyzylorda";
			} else if (region.equals("09")) {
				timezone = "Asia/Aqtau";
			} else if (region.equals("10")) {
				timezone = "Asia/Qyzylorda";
			} else if (region.equals("11")) {
				timezone = "Asia/Almaty";
			} else if (region.equals("12")) {
				timezone = "Asia/Qyzylorda";
			} else if (region.equals("13")) {
				timezone = "Asia/Aqtobe";
			} else if (region.equals("14")) {
				timezone = "Asia/Qyzylorda";
			} else if (region.equals("15")) {
				timezone = "Asia/Almaty";
			} else if (region.equals("16")) {
				timezone = "Asia/Aqtobe";
			} else if (region.equals("17")) {
				timezone = "Asia/Almaty";
			}
		} else if (country.equals("MN")) {
			if (region.equals("06")) {
				timezone = "Asia/Choibalsan";
			} else if (region.equals("11")) {
				timezone = "Asia/Ulaanbaatar";
			} else if (region.equals("17")) {
				timezone = "Asia/Choibalsan";
			} else if (region.equals("19")) {
				timezone = "Asia/Hovd";
			} else if (region.equals("20")) {
				timezone = "Asia/Ulaanbaatar";
			} else if (region.equals("21")) {
				timezone = "Asia/Ulaanbaatar";
			} else if (region.equals("25")) {
				timezone = "Asia/Ulaanbaatar";
			}
		} else if (country.equals("MX")) {
			if (region.equals("01")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("02")) {
				timezone = "America/Tijuana";
			} else if (region.equals("03")) {
				timezone = "America/Hermosillo";
			} else if (region.equals("04")) {
				timezone = "America/Merida";
			} else if (region.equals("05")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("06")) {
				timezone = "America/Chihuahua";
			} else if (region.equals("07")) {
				timezone = "America/Monterrey";
			} else if (region.equals("08")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("09")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("10")) {
				timezone = "America/Mazatlan";
			} else if (region.equals("11")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("12")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("13")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("14")) {
				timezone = "America/Mazatlan";
			} else if (region.equals("15")) {
				timezone = "America/Chihuahua";
			} else if (region.equals("16")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("17")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("18")) {
				timezone = "America/Mazatlan";
			} else if (region.equals("19")) {
				timezone = "America/Monterrey";
			} else if (region.equals("20")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("21")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("22")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("23")) {
				timezone = "America/Cancun";
			} else if (region.equals("24")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("25")) {
				timezone = "America/Mazatlan";
			} else if (region.equals("26")) {
				timezone = "America/Hermosillo";
			} else if (region.equals("27")) {
				timezone = "America/Merida";
			} else if (region.equals("28")) {
				timezone = "America/Monterrey";
			} else if (region.equals("29")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("30")) {
				timezone = "America/Mexico_City";
			} else if (region.equals("31")) {
				timezone = "America/Merida";
			} else if (region.equals("32")) {
				timezone = "America/Monterrey";
			}
		} else if (country.equals("MY")) {
			if (region.equals("01")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("02")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("03")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("04")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("05")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("06")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("07")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("08")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("09")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("11")) {
				timezone = "Asia/Kuching";
			} else if (region.equals("12")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("13")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("14")) {
				timezone = "Asia/Kuala_Lumpur";
			} else if (region.equals("15")) {
				timezone = "Asia/Kuching";
			} else if (region.equals("16")) {
				timezone = "Asia/Kuching";
			}
		} else if (country.equals("NZ")) {
			if (region.equals("85")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("E7")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("E8")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("E9")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F1")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F2")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F3")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F4")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F5")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F6")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F7")) {
				timezone = "Pacific/Chatham";
			} else if (region.equals("F8")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("F9")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("G1")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("G2")) {
				timezone = "Pacific/Auckland";
			} else if (region.equals("G3")) {
				timezone = "Pacific/Auckland";
			}
		} else if (country.equals("PT")) {
			if (region.equals("02")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("03")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("04")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("05")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("06")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("07")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("08")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("09")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("10")) {
				timezone = "Atlantic/Madeira";
			} else if (region.equals("11")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("13")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("14")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("16")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("17")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("18")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("19")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("20")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("21")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("22")) {
				timezone = "Europe/Lisbon";
			} else if (region.equals("23")) {
				timezone = "Atlantic/Azores";
			}
		} else if (country.equals("RU")) {
			if (region.equals("01")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("02")) {
				timezone = "Asia/Irkutsk";
			} else if (region.equals("03")) {
				timezone = "Asia/Novokuznetsk";
			} else if (region.equals("04")) {
				timezone = "Asia/Novosibirsk";
			} else if (region.equals("05")) {
				timezone = "Asia/Vladivostok";
			} else if (region.equals("06")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("07")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("08")) {
				timezone = "Europe/Samara";
			} else if (region.equals("09")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("10")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("11")) {
				timezone = "Asia/Irkutsk";
			} else if (region.equals("12")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("13")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("14")) {
				timezone = "Asia/Irkutsk";
			} else if (region.equals("15")) {
				timezone = "Asia/Anadyr";
			} else if (region.equals("16")) {
				timezone = "Europe/Samara";
			} else if (region.equals("17")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("18")) {
				timezone = "Asia/Krasnoyarsk";
			} else if (region.equals("20")) {
				timezone = "Asia/Irkutsk";
			} else if (region.equals("21")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("22")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("23")) {
				timezone = "Europe/Kaliningrad";
			} else if (region.equals("24")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("25")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("26")) {
				timezone = "Asia/Kamchatka";
			} else if (region.equals("27")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("28")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("29")) {
				timezone = "Asia/Novokuznetsk";
			} else if (region.equals("30")) {
				timezone = "Asia/Vladivostok";
			} else if (region.equals("31")) {
				timezone = "Asia/Krasnoyarsk";
			} else if (region.equals("32")) {
				timezone = "Asia/Omsk";
			} else if (region.equals("33")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("34")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("35")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("36")) {
				timezone = "Asia/Anadyr";
			} else if (region.equals("37")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("38")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("39")) {
				timezone = "Asia/Krasnoyarsk";
			} else if (region.equals("40")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("41")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("42")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("43")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("44")) {
				timezone = "Asia/Magadan";
			} else if (region.equals("45")) {
				timezone = "Europe/Samara";
			} else if (region.equals("46")) {
				timezone = "Europe/Samara";
			} else if (region.equals("47")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("48")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("49")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("50")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("51")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("52")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("53")) {
				timezone = "Asia/Novosibirsk";
			} else if (region.equals("54")) {
				timezone = "Asia/Omsk";
			} else if (region.equals("55")) {
				timezone = "Europe/Samara";
			} else if (region.equals("56")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("57")) {
				timezone = "Europe/Samara";
			} else if (region.equals("58")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("59")) {
				timezone = "Asia/Vladivostok";
			} else if (region.equals("60")) {
				timezone = "Europe/Kaliningrad";
			} else if (region.equals("61")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("62")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("63")) {
				timezone = "Asia/Yakutsk";
			} else if (region.equals("64")) {
				timezone = "Asia/Sakhalin";
			} else if (region.equals("65")) {
				timezone = "Europe/Samara";
			} else if (region.equals("66")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("67")) {
				timezone = "Europe/Samara";
			} else if (region.equals("68")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("69")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("70")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("71")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("72")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("73")) {
				timezone = "Europe/Samara";
			} else if (region.equals("74")) {
				timezone = "Asia/Krasnoyarsk";
			} else if (region.equals("75")) {
				timezone = "Asia/Novosibirsk";
			} else if (region.equals("76")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("77")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("78")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("79")) {
				timezone = "Asia/Irkutsk";
			} else if (region.equals("80")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("81")) {
				timezone = "Europe/Samara";
			} else if (region.equals("82")) {
				timezone = "Asia/Irkutsk";
			} else if (region.equals("83")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("84")) {
				timezone = "Europe/Volgograd";
			} else if (region.equals("85")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("86")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("87")) {
				timezone = "Asia/Novosibirsk";
			} else if (region.equals("88")) {
				timezone = "Europe/Moscow";
			} else if (region.equals("89")) {
				timezone = "Asia/Vladivostok";
			} else if (region.equals("90")) {
				timezone = "Asia/Yekaterinburg";
			} else if (region.equals("91")) {
				timezone = "Asia/Krasnoyarsk";
			} else if (region.equals("92")) {
				timezone = "Asia/Anadyr";
			} else if (region.equals("93")) {
				timezone = "Asia/Irkutsk";
			}
		} else if (country.equals("UA")) {
			if (region.equals("01")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("02")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("03")) {
				timezone = "Europe/Uzhgorod";
			} else if (region.equals("04")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("05")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("06")) {
				timezone = "Europe/Uzhgorod";
			} else if (region.equals("07")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("08")) {
				timezone = "Europe/Simferopol";
			} else if (region.equals("09")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("10")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("11")) {
				timezone = "Europe/Simferopol";
			} else if (region.equals("12")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("13")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("14")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("15")) {
				timezone = "Europe/Uzhgorod";
			} else if (region.equals("16")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("17")) {
				timezone = "Europe/Simferopol";
			} else if (region.equals("18")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("19")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("20")) {
				timezone = "Europe/Simferopol";
			} else if (region.equals("21")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("22")) {
				timezone = "Europe/Uzhgorod";
			} else if (region.equals("23")) {
				timezone = "Europe/Kiev";
			} else if (region.equals("24")) {
				timezone = "Europe/Uzhgorod";
			} else if (region.equals("25")) {
				timezone = "Europe/Uzhgorod";
			} else if (region.equals("26")) {
				timezone = "Europe/Zaporozhye";
			} else if (region.equals("27")) {
				timezone = "Europe/Kiev";
			}
		} else if (country.equals("UZ")) {
			if (region.equals("01")) {
				timezone = "Asia/Tashkent";
			} else if (region.equals("02")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("03")) {
				timezone = "Asia/Tashkent";
			} else if (region.equals("05")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("06")) {
				timezone = "Asia/Tashkent";
			} else if (region.equals("07")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("08")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("09")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("10")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("12")) {
				timezone = "Asia/Samarkand";
			} else if (region.equals("13")) {
				timezone = "Asia/Tashkent";
			} else if (region.equals("14")) {
				timezone = "Asia/Tashkent";
			}
		} else if (country.equals("TL")) {
			timezone = "Asia/Dili";
		} else if (country.equals("PF")) {
			timezone = "Pacific/Marquesas";
		} else if (country.equals("SX")) {
			timezone = "America/Curacao";
		} else if (country.equals("BQ")) {
			timezone = "America/Curacao";
		} else if (country.equals("CW")) {
			timezone = "America/Curacao";
		} else if (country.equals("AQ")) {
			timezone = "Antarctica/South_Pole";
		} else if (country.equals("BV")) {
			timezone = "Antarctica/Syowa";
		} else if (country.equals("FM")) {
			timezone = "Pacific/Pohnpei";
		}
		return timezone;
	}
}
