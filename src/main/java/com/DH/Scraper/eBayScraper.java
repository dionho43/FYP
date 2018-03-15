package com.DH.Scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.DH.Entity.Item;
import com.DH.Entity.Purchase;
import com.DH.Entity.Sale;
import com.DH.Entity.Search;
import com.DH.Entity.Statistics;
import com.DH.Entity.User;

public class eBayScraper {
	
	public Purchase getPurchase(String url) throws IOException {
		try
		{
			Document d = Jsoup.connect(url).timeout(40000).get();
			String title = salePurchaseTitle(d.getElementsByClass("it-ttl").text());
			String condition = d.getElementsByClass("u-flL condText  ").text();
			String sellerName = d.getElementsByClass("mbg-nw").text();
			
			String p;
			if (d.getElementById("prcIsum") != null)
			{
				p = d.getElementById("prcIsum").text();
			}
			else if (d.getElementById("prcIsum_bidPrice") != null)
			{
				p = d.getElementById("prcIsum_bidPrice").text();
			}
			else
			{
				p = d.getElementsByClass("notranslate vi-VR-cvipPrice").first().text();
			}
			//String price = removeCurrency(p);
			String price = p;
			//String currency = getCurrency(p);
			
			String postage;
			if (d.getElementById("fshippingCost") != null)
			{
				postage = d.getElementById("fshippingCost").getElementsByTag("span").first().text();
			}
			else
			{
				postage = "Postage not available";
			}
			//String postage = getPostagePrice(removeCurrency(post));
			String link = url;
			String img = d.getElementById("ebay-scShare-div").attr("data-imageUrl");
			String loc = d.getElementsByClass("iti-eu-bld-gry vi-shp-pdg-rt").first().text();
			String location = extractLocation(loc);
			String date = d.getElementById("bb_tlft").text();
			
			Purchase purch = new Purchase(title, price, img, condition, postage, location, sellerName, link, date);
			return purch;
			
		}
			catch(Exception e)
			{
				return null;
			}
	}
	
	public Sale getSale(String url) throws IOException {
		//try
		//{
		Document d = Jsoup.connect(url).timeout(40000).get();
		String title = salePurchaseTitle(d.getElementsByClass("it-ttl").text());
		String condition = d.getElementsByClass("u-flL condText  ").text();
		String p;
		if (d.getElementById("prcIsum") != null)
		{
			p = d.getElementById("prcIsum").text();
		}
		else if (d.getElementById("prcIsum_bidPrice") != null)
		{
			p = d.getElementById("prcIsum_bidPrice").text();
		}
		else
		{
			p = d.getElementsByClass("notranslate vi-VR-cvipPrice").first().text();
		}
		String price = p;
		String postage;
		if (d.getElementById("fshippingCost") != null)
		{
			postage = d.getElementById("fshippingCost").getElementsByTag("span").first().text();
		}
		else
		{
			postage = "Postage not available";
		}
		String link = url;
		String img = d.getElementById("ebay-scShare-div").attr("data-imageUrl");
		String loc = d.getElementsByClass("iti-eu-bld-gry vi-shp-pdg-rt").first().text();
		String location = extractLocation(loc);
		String fee = String.valueOf(Double.valueOf(getPriceAsDouble(price))/10);;
		String date = d.getElementById("bb_tlft").text();
		
		Sale sale = new Sale(title, price, img, condition, postage, location, link, fee, date);
		return sale;
		}
		//catch(Exception e)
		//{
		//	return null;
		//}
	//}
	
	public List<Item> getResults(Search search) throws IOException {
		ArrayList<Item> items = new ArrayList<Item>();
		String searchableKeywords = formatSearch(search.getKeyword());
		String ebayUrl = "https://" + search.getWebsite() + "/sch/" + selectCategory(search.getCategory()) + "i.html?LH_Complete=1" + searchSold(search) + freePostage(search) + listingType(search) + numberOfListings(search) + "&_nkw=" + searchableKeywords + "";
		System.out.println(ebayUrl);
		Document d = Jsoup.connect(ebayUrl).timeout(40000).get();
		Elements el = d.getElementsByAttribute("listingId");
		String itemCondition;
		//System.out.println(el.size());
		for (Element a : el) {
			String title = a.getElementsByClass("lvtitle").first().getElementsByTag("a").first().text();//.first().getElementsByTag("img").first().attr("alt").toString();
			String price = a.getElementsByClass("lvprice prc").first().text();
			if (a.getElementsByClass("lvsubtitle").first() != null)
			{
				itemCondition = a.getElementsByClass("lvsubtitle").first().text();
			}
			else 
			{
				itemCondition = "None";
			}
			//String price = getPrice(p);
			//String price = p;
			String post = a.getElementsByClass("lvshipping").first().getElementsByClass("ship").first().text();
			/*if (post.equalsIgnoreCase("") && a.getElementsByClass("lvshipping").first().getElementsByClass("ship").first().getElementsByClass("fee").size() > 0)
			{
				post = a.getElementsByClass("lvshipping").first().getElementsByClass("ship").first().getElementsByClass("fee").first().text();
			}
			else
			{
				post = a.getElementsByClass("lvshipping").first().getElementsByClass("ship").first().getElementsByClass("bfsp").first().text();
			}*/
			
			//String postage = getPostagePrice(removeCurrency(post));
			String img = a.getElementsByClass("lvpicinner full-width picW").first().getElementsByTag("img").attr("src");
			
			if (img.equals("https://ir.ebaystatic.com/pictures/aw/pics/s_1x2.gif"))
			{
			 img = a.getElementsByClass("lvpicinner full-width picW").first().getElementsByTag("img").attr("imgurl");
			}
			String location;
			if (a.getElementsByClass("lvdetails left space-zero full-width").first().getElementsByTag("li ").size()>1)
			{
				location = a.getElementsByClass("lvdetails left space-zero full-width").first().getElementsByTag("li ").get(1).text();
				location = formatLocation(location);
			}
			else
			{
				location = defaultLocation(search.getWebsite());
			}
			
			if (location.equalsIgnoreCase(""))
			{
				location = defaultLocation(search.getWebsite());
			}
			
			String soldString = a.getElementsByClass("lvprice prc").first().getElementsByTag("span").first().className();
			boolean sold = false;
			if (soldString.equals("bold bidsold"))
			{
				sold = true;
			}
			Item newItem = new Item(title, itemCondition, price, post, location, img, sold);
			items.add(newItem);
		}
		return items;
	}
	
	public List<Sale> getUserSales(User user) throws IOException {
		
		//String ebayUrl = "https://" + search.getWebsite() + "/sch/" + user.getEbayUsername() + "/i.html?"; luch6006
		String ebayUrl = "https://ebay.ie" + "/sch/" + user.getEbayUsername() + "/m.html?LH_Complete=1";
		System.out.println(ebayUrl);
		
		ArrayList<Sale> userSales = new ArrayList<Sale>();
		String itemCondition;
		
		Document d = Jsoup.connect(ebayUrl).timeout(40000).get();
		Elements el = d.getElementsByAttribute("listingId");
		
		for (Element a : el) {
			String title = a.getElementsByClass("lvtitle").first().getElementsByTag("a").first().text();
			String price = a.getElementsByClass("lvprice prc").first().text();
			if (a.getElementsByClass("lvsubtitle").first() != null)
			{
				itemCondition = a.getElementsByClass("lvsubtitle").first().text();
			}
			else 
			{
				itemCondition = "None";
			}
			String post = a.getElementsByClass("lvshipping").first().getElementsByClass("ship").first().text();
			
		String link = d.getElementsByClass("lvpicinner full-width picW").first().getElementsByTag("a").attr("href");
		String img = a.getElementsByClass("lvpicinner full-width picW").first().getElementsByTag("img").attr("src");
		
		if (img.equals("https://ir.ebaystatic.com/pictures/aw/pics/s_1x2.gif"))
		{
		 img = a.getElementsByClass("lvpicinner full-width picW").first().getElementsByTag("img").attr("imgurl");
		}
		String location = "";
		if (a.getElementsByClass("lvdetails left space-zero full-width").first().getElementsByTag("li ").size()>1)
		{
			location = a.getElementsByClass("lvdetails left space-zero full-width").first().getElementsByTag("li ").get(1).text();
			location = formatLocation(location);
		}
		String fee = String.valueOf(Double.valueOf(getPriceAsDouble(price))/10);;
		String date = d.getElementsByClass("timeleft").first().getElementsByClass("tme").first().getElementsByTag("span").first().text();
		
		Sale sale = new Sale(title, price, img, itemCondition, post, location, link, fee, date);
		userSales.add(sale);
		}
		return userSales;
	}
	
	public Statistics getStatistics(Search search) throws IOException {
		Statistics stats = new Statistics();
		ArrayList<String> conditions = new ArrayList<String>();
		ArrayList<Double> prices = new ArrayList<Double>();
		String searchableKeywords = formatSearch(search.getKeyword());
		String ebayUrl = "https://" + search.getWebsite() + "/sch/" + selectCategory(search.getCategory()) + "i.html?LH_Complete=1" + searchSold(search) + freePostage(search) + listingType(search) + numberOfListings(search) + "&_nkw=" + searchableKeywords + "";
		System.out.println(ebayUrl);
		Document d = Jsoup.connect(ebayUrl).timeout(20000).get();
		Elements el = d.getElementsByAttribute("listingId");
		int numberSold = 0;
		int numberNotSold = 0;
		double priceTotal = 0;
		double postageTotal = 0;
		int numberAuction = 0;
		int numberBin = 0;
		double highestPrice = 0;
		double lowestPrice = 0;
		double currentPrice = 0;
		double currentPostage = 0;
		String priceRange = "";
		int numberListingsPictures = 0;
		
		String auc = "";
		for (Element a : el) {
			
			String price = a.getElementsByClass("lvprice prc").first().text();
			prices.add(getPriceAsDouble(price));
			
			auc = a.getElementsByClass("lvformat").first().getElementsByTag("span").first().text();
			if (auc.isEmpty())
			{
				numberBin++;
			}
			else
			{
				numberAuction++;
			}
			
			if (a.getElementsByClass("lvprice prc").first().getElementsByClass("bold binsold").first() != null)
			{
				
				numberNotSold++;
			}
			else if (a.getElementsByClass("lvprice prc").first().getElementsByClass("bold bidsold").first() != null)
			{
				numberSold++;
			}
			
			currentPrice = getPriceAsDouble(a.getElementsByClass("lvprice prc").first().text());
			priceTotal = priceTotal + currentPrice;
			
			currentPostage = getPriceAsDouble(a.getElementsByClass("lvshipping").first().getElementsByClass("ship").first().text());
			postageTotal = postageTotal + currentPostage;
			
			if (currentPrice > highestPrice)
			{
				highestPrice = currentPrice;
			}
			
			if (lowestPrice <= 0)
			{
				lowestPrice = currentPrice;
			}
			else if (currentPrice < lowestPrice)
			{
				lowestPrice = currentPrice;
			}
			
			if (a.getElementsByClass("lvpicinner full-width picW").first().getElementsByClass("stockImg") == null)
			{
				
			}
			else
			{
				numberListingsPictures++;
			}
			
			String soldString = a.getElementsByClass("lvprice prc").first().getElementsByTag("span").first().className();
			boolean sold = false;
			if (soldString.equals("bold bidsold"))
			{
				sold = true;
			}
			
			String itemCondition;
			//String itemCondition = a.getElementsByClass("lvsubtitle").first().text();
			if (a.getElementsByClass("lvsubtitle").first() != null)
			{
				itemCondition = a.getElementsByClass("lvsubtitle").first().text();
			}
			else 
			{
				itemCondition = "None";
			}
			conditions.add(itemCondition);
		}
		Collections.sort(prices);
		stats.setNumberListings(el.size());
		stats.setNumberSold(numberSold);
		stats.setAveragePrice(priceTotal/el.size());
		stats.setAveragePostage(postageTotal/el.size());
		stats.setNumberAuctions(numberAuction);
		stats.setNumberPictures(numberListingsPictures);
		stats.setHighestPrice(highestPrice);
		stats.setLowestPrice(lowestPrice);
		stats.setRange(String.valueOf(lowestPrice) + " to " + String.valueOf(highestPrice));
		stats.setConditions(conditions);
		stats.setPrices(prices);
		return stats;
	}
	
	public String salePurchaseTitle(String s)
	{
		String parts[] = s.split(" ");
		String title = "";
		for (int i = 2; i < parts.length; i++)
		{
			title = title + parts[i] + " ";
		}

		return title;
	}
	
	public String freePostage(Search s)
	{
		if (s.isFreePostage())
		{
			return "&LH_FS=1";
		}
		else return "";
	}
	
	public String listingType(Search s)
	{
		if (s.getListingType().equalsIgnoreCase("Buy it Now"))
		{
			return "&LH_BIN=1";
		}
		else if (s.getListingType().equalsIgnoreCase("Auction"))
		{
			return "&LH_Auction=1";
		}
		else return "";
	}
	
	public String numberOfListings(Search s)
	{
		if (s.getNumberOfListings().equalsIgnoreCase("50"))
		{
			return "&_ipg=50";
		}
		else if (s.getNumberOfListings().equalsIgnoreCase("100"))
		{
			return "&_ipg=100";
		}
		else if (s.getNumberOfListings().equalsIgnoreCase("200"))
		{
			return "&_ipg=200";
		}
		else return "";
	}
	
	public String searchSold(Search s)
	{
		if (s.isSold())
		{
			return "&LH_Sold=1";
		}
		else return "";
	}
	
	public String defaultLocation(String website)
	{
		String loc = "";
		switch(website)
		{
				case "eBay.ie" : loc = "Ireland"; break;
				case "eBay.co.uk" : loc = "United Kingdom"; break;
				case "eBay.com" : loc = "United States"; break;
				case "eBay.ca" : loc = "Canada"; break;
				case "eBay.com.au" : loc = "Australia"; break;
				case "eBay.es" : loc = "Spain"; break;
				case "eBay.fr" : loc = "France"; break;
				case "eBay.it" : loc = "Italy"; break;
				case "eBay.de" : loc = "Germany"; break;
				case "eBay.ch" : loc = "Switzerland"; break;
				case "eBay.at" : loc = "Austria"; break;
				case "eBay.nl" : loc = "Netherlands"; break;
				case "eBay.in" : loc = "India"; break;
		}
		return loc;
	}
	
	public String extractLocation(String locationRaw)
	{
		String[] locations = {"Ireland", "United Kingdom", "United States", "Canada", "Australia", "Spain", "France", "Italy", "Germany", "Switzerland", "Austria", "Netherlands", "India"};
		for (String a : locations)
		{
			if (locationRaw.contains(a))
			{
				return a;
			}
		}
		return locationRaw;
		
	}
	
	public String selectCategory(String p)
	{
		String categoryId = "";
		
		switch (p) {
		case "Search by Category": categoryId = ""; break;
	    case "Antiques": categoryId = "20081/"; break; 
	    case "Art": categoryId = "550/"; break;
	    case "Baby": categoryId = "2984/"; break;
	    case "Books": categoryId = "267/"; break;
	    case "Business & Industrial": categoryId = "12576/"; break;
	    case "Cameras & Photo": categoryId = "625/"; break;
	    case "Cell Phones & Accessories": categoryId = "15032/"; break;
	    case "Clothing, Shoes & Accessories": categoryId = "11450/"; break;
	    case "Coins & Paper Money": categoryId = "11116/"; break;
	    case "Collectibles": categoryId = "1/"; break;
	    case "Computers/Tablets & Networking": categoryId = "58058/"; break;
	    case "Consumer Electronics": categoryId = "293/"; break;
	    case "Crafts": categoryId = "14339/"; break;
	    case "Dolls & Bears": categoryId = "237/"; break;
	    case "DVDs & Movies": categoryId = "11232/"; break;
	    case "Entertainment Memorabilia": categoryId = "45100/"; break;
	    case "Everything Else": categoryId = "99/"; break;
	    case "Gift Cards & Coupons": categoryId = "172008/"; break;
	    case "Health & Beauty": categoryId = "26395/"; break;
	    case "Home & Garden": categoryId = "11700/"; break;
	    case "Jewelry & Watches": categoryId = "281/"; break;
	    case "Music": categoryId = "11233/"; break;
	    case "Musical Instruments & Gear": categoryId = "619/"; break;
	    case "Pet Supplies": categoryId = "1281/"; break;
	    case "Pottery & Glass": categoryId = "870/"; break;
	    case "Real Estate": categoryId = "10542/"; break;
	    case "Specialty Services": categoryId = "316/"; break;
	    case "Sporting Goods": categoryId = "888/"; break;
	    case "Sports Mem, Cards & Fan Shop": categoryId = "64482/"; break;
	    case "Stamps": categoryId = "260/"; break;
	    case "Tickets & Experiences": categoryId = "1305/"; break;
	    case "Toys & Hobbies": categoryId = "220/"; break;
	    case "Travel": categoryId = "3252/"; break;
	    case "Video Games & Consoles": categoryId = "1249/"; break;
	}
		System.out.println(categoryId);
		return categoryId;
	}
	
	/*public String selectWebsite(String p)
	{
		String websiteUrl = "";
		System.out.println(websiteUrl);
		return websiteUrl;
	}*/
	
	public String getPrice(String p)
	{
		String parts[] = p.split(" ");
		if (parts.length > 2)
		{
			if (parts[1].equalsIgnoreCase("Trending"))
			{
				return parts[0];
			}
			return parts[0] + parts[1];
		}
		else return p;
	}
	
	public double getPriceAsDouble(String p)
	{
		System.out.println(p);
		if (!p.matches(".*\\d+.*"))
		{
			return 0;
		}
		    String parts[] = p.split(" ");
	    if (parts.length == 2)
	    {
	    	if (parts[0].equals("C") || parts[0].equalsIgnoreCase("AU"))
	    	{
	    		double spacedPrice = Double.parseDouble(parts[1].substring(1).replaceAll(",", ""));
			    return Double.valueOf(formatDecimal(spacedPrice));
	    	}
	    	else if (Character.isDigit(parts[0].charAt(0)))
	    	{
	    		double spacedPrice = Double.parseDouble(parts[0].replaceAll(",", "."));
			    return Double.valueOf(formatDecimal(spacedPrice));
	    	}
	    	else if (parts[0].startsWith("+$"))
	    	{
	    		double spacedPrice = Double.parseDouble(parts[0].substring(2).replaceAll(",", "."));
			    return Double.valueOf(formatDecimal(spacedPrice));
	    	}
	    	else
	    	{
	    		double spacedPrice = Double.parseDouble(parts[1].replaceAll(",", ""));
			    return Double.valueOf(formatDecimal(spacedPrice));
	    	}
	    }
	    else if ( parts.length >= 3)
	    {
	    	if (parts[0].contains("EUR") || parts[0].equalsIgnoreCase("Rs."))
	    	{
	    		double firstPrice = Double.parseDouble(parts[1].replaceAll(",", ""));
				return Double.valueOf(formatDecimal(firstPrice));
	    	}
	    	else if (parts[0].equalsIgnoreCase("C") || parts[0].contains("AU") || parts[0].equalsIgnoreCase("+C"))
	    	{
	    		double firstPrice = Double.parseDouble(parts[1].substring(1).replaceAll(",", ""));
				return Double.valueOf(formatDecimal(firstPrice));
	    	}
	    	else if (parts[0].equalsIgnoreCase("+") && parts[1].charAt(0)=='£')
	    	{
	    		double firstPrice = Double.parseDouble(parts[1].substring(1).replaceAll(",", ""));
				return Double.valueOf(formatDecimal(firstPrice));
	    	}
	    	else if (Character.isDigit(parts[0].charAt(0)))
	    	{
	    		double spacedPrice = Double.parseDouble(parts[0].replaceAll(",", "."));
			    return Double.valueOf(formatDecimal(spacedPrice));
	    	}
	    	else if (parts[0].equalsIgnoreCase("+"))
	    	{
	    		double firstPrice = Double.parseDouble(parts[2].replaceAll(",", ""));
				return Double.valueOf(formatDecimal(firstPrice));
	    	}
	    	else
	    	{
	    		double firstPrice = Double.parseDouble(parts[0].substring(1).replaceAll(",", ""));
	    		return Double.valueOf(formatDecimal(firstPrice));
	    	}
	    }
	    else
	    {
	    	double symbolPrice = Double.parseDouble(p.substring(1).replaceAll(",", ""));
	    	return Double.valueOf(formatDecimal(symbolPrice));
	    }
	}
	
	public String searchURL(Search search)
	{
		String searchableKeywords = formatSearch(search.getKeyword());
		String ebayUrl = "https://" + search.getWebsite() + "/sch/" + selectCategory(search.getCategory()) + "i.html?LH_Complete=1" + searchSold(search) + freePostage(search) + "&_nkw=\"" + searchableKeywords + "\"";
		return ebayUrl;
	}
	
	public String formatLocation(String p)
	{
		String parts[] = p.split(" ");
		String correctedLocation = "";
		for (int i=1; i < parts.length; i++)
		{
			correctedLocation = correctedLocation + parts[i] + " ";
		}
		return correctedLocation;
	}
	
	public String formatDecimal(double number) {
		  float epsilon = 0.004f; // 4 tenths of a cent
		  String ret= "";
		  if (Math.abs(Math.round(number) - number) < epsilon) {
		     ret = String.format("%10.0f", number); // sdb
		     return ret.concat(".00");
		  } else {
		     return String.format("%10.2f", number); // dj_segfault
		  }
		}
	
	public String getPostagePrice(String p)
	{
		String parts[] = p.split(" ");
	    double price = Double.parseDouble(parts[0]);
	    return formatDecimal(price);
	}
	
	public String removeCurrency(String p)
	{
		String pr = p.substring(1);
		return pr;
	}
	
	public String getCurrency(String p)
	{
		String curr = String.valueOf(p.charAt(0));
		return curr;
	}
	
	public String formatSearch(String s) {
		String resultString = "";
		String parts[] = s.split(" ");
		resultString = parts[0];
		for (int i=1; i<parts.length; i++)
		{
			resultString = resultString.concat("+").concat(parts[i]);
		}
		return resultString;
	}

}
