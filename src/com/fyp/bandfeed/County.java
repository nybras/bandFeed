/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

public class County {

	private static final String[] counties = new String[] { " Select..", "-- England --",
			"Avon", "Bedfordshire", "Berkshire", "Buckinghamshire",
			"Cambridgeshire", "Cheshire", "Cleveland", "Cornwall", "Cumbria",
			"Derbyshire", "Devon", "Dorset", "Durham", "East Sussex", "Essex",
			"Gloucestershire", "Hampshire", "Herefordshire", "Hertfordshire",
			"Isle of Wight", "Kent", "Lancashire", "Leicestershire",
			"Lincolnshire", "London", "Merseyside", "Middlesex", "Norfolk",
			"Northamptonshire", "Northumberland", "North Humberside",
			"North Yorkshire", "Nottinghamshire", "Oxfordshire", "Rutland",
			"Shropshire", "Somerset", "South Humberside", "South Yorkshire",
			"Staffordshire", "Suffolk", "Surrey", "Tyne and Wear",
			"Warwickshire", "West Midlands", "West Sussex", "West Yorkshire",
			"Wiltshire", "Worcestershire", "-- Wales --", "Clwyd", "Dyfed",
			"Gwent", "Gwynedd", "Mid Glamorgan", "Powys", "South Glamorgan",
			"West Glamorgan", "-- Scotland --", "Aberdeenshire", "Angus",
			"Argyll", "Ayrshire", "Banffshire", "Berwickshire", "Bute",
			"Caithness", "Clackmannanshire", "Dumfriesshire", "Dunbartonshire",
			"East Lothian", "Fife", "Inverness-shire", "Kincardineshire",
			"Kinross-shire", "Kirkcudbrightshire", "Lanarkshire", "Midlothian",
			"Moray", "Nairnshire", "Orkney", "Peeblesshire", "Perthshire",
			"Renfrewshire", "Ross-shire", "Roxburghshire", "Selkirkshire",
			"Shetland", "Stirlingshire", "Sutherland", "West Lothian",
			"Wigtownshire", "-- Northern Ireland --", "Antrim", "Armagh",
			"Down", "Fermanagh", "Londonderry", "Tyrone" };

	public String[] getCounties() {
		return counties;
	}
}
