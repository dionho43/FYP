package com.DH.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.DH.Entity.Item;
import com.DH.Entity.Purchase;
import com.DH.Entity.Report;
import com.DH.Entity.Sale;
import com.DH.Entity.Search;
import com.DH.Entity.Statistics;
import com.DH.Entity.User;
import com.DH.Scraper.eBayScraper;
import com.DH.Service.ReportService;
import com.DH.Service.SearchService;
import com.DH.Service.UserService;


@Controller
public class WebController extends WebMvcConfigurerAdapter {
	
	@Autowired
	UserService userService;
	
	@Autowired
	SearchService searchService;
	
	@Autowired
	ReportService reportService;
	
	public User loggedInUser;
	
	public Search lastSearch;
	
	public Report currentReport;
	private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }
    
    //Home pages
    @GetMapping("/")
    public String homepage() {
    	 if (loggedInUser == null)
    	 {
    		 return "home";
    	 }
    	 else
    	 {
    		 return "welcome";
    	 }
    }
    
    @GetMapping("/home")
    public String home() {
   	 if (loggedInUser == null)
   	 {
   		 return "home";
   	 }
   	 else
   	 {
   		 return "welcome";
   	 }
    }
    
    
    //Login Pages
    @GetMapping("/login")
    public String showLogin(User user) {
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout() {
    	loggedInUser = null;
        return "loginRegister";
    }
	  
	    @PostMapping("/loginProcess")
	    public void loginProcess(@Valid User user, HttpServletRequest request, HttpServletResponse response)throws IOException {
	    	response.setContentType("text/html");
	    	PrintWriter out = response.getWriter();
	    	if (loginCheck(user))
	    	{
	    	htmlHeader( out);
 		    toolBarStart(out);
		    toolBarEnd(out);
		    container(out);
		    out.println("<p>You are logged in as :" + loggedInUser.getUsername() + "</p>");
		    out.println("<form action=\"http://localhost:8080/logout\">");
		    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Logout\" /></form>");
		    htmlFooter(out);
	    	}
	    	else
	    	{
	    		htmlHeader( out);
	    		container(out);
			    out.println("<p>Incorrect username or password</p>");
			    out.println("");
			    out.println("<form action=\"http://localhost:8080/login\">");
			    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Back to Login\" /></form>");
			    htmlFooter(out);
	    	}
	    }
    
    public boolean loginCheck(User user)
    {
    	boolean result = false;
    	User temp;
    	if (userService.findByUsername(user.getUsername()).isEmpty())
    	{
    		return result;
    	}
    	else
    	{
    		temp = userService.findByUsername(user.getUsername()).get(0);
    		if (temp.getUsername().equalsIgnoreCase(user.getUsername()) && temp.getPassword().equalsIgnoreCase(user.getPassword()))
    		{
    			loggedInUser = temp;
    			return true;
    		}
    	}
    	return result;
    }
    
    //Registrations
    @GetMapping("/register")
    public String showForm(User user) {
        return "form";
    }
    

    @PostMapping("/registerProcess")
   public void register(@Valid User user, HttpServletRequest request, HttpServletResponse response)throws IOException {
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
        if (registrationCheck(user)) {
        	userService.save(user);
        	htmlHeader( out);
        	container(out);
		    out.println("<p>Successfully registered user:" + user.getUsername() + "</p>");
		    out.println("<form action=\"http://localhost:8080/login\">");
		    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Login\" /></form>");
		    htmlFooter(out);
        }
        else
        {
        	htmlHeader( out);
        	container(out);
		    out.println("<p>Invalid registration details</p>");
		    out.println("");
		    out.println("<form action=\"http://localhost:8080/register\">");
		    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Back to Register\" /></form>");
		    htmlFooter(out);
        }
    }
    
   
    
    public boolean registrationCheck(User user)
    {
    	boolean result = false;
    	if (userService.findByUsername(user.getUsername()).isEmpty() && !user.getUsername().isEmpty() && !user.getPassword().isEmpty())
    	{
    		result = true;
    	}
    	return result;
    }
    
    //Searches
    
    @GetMapping("/search")
    public String search(Search search) {
    	if (loggedInUser != null)
    	{
            return "search";
    	}
    	else
    	{
    		return "loginRegister";
    	}
    }
    
    @PostMapping("/searchResults")
    public void search(@Valid Search search, HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	lastSearch = search;
     	
     	List<Item> items = eBay.getResults(search);
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		   out.println("<p>Search : " + search.getKeyword() + " (" + items.size() +" Listings Found)</p>");
 		    out.println("");
 		    out.println("<form action=\"http://localhost:8080/searchStatistics\">");
		    out.println("<center><input type=\"submit\" class=\"btn btn-default\" value=\"View Statistics\" /></center></form>");

		    out.println("</tr></table></form>");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Title</th>");
 		    //out.println("<th>Condition</th>");
 		    out.println("<th>Price</th>");
 		    out.println("<th>Postage</th>");
 		    out.println("<th>Location</th>");
 		    out.println("<th>Sold</th>");
 		    out.println("<th>Image</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<items.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	out.println("<td>" + items.get(i).getTitle() + "</td>");
 		    	//out.println("<td>" + items.get(i).getItemCondition() + "</td>");
 		    	out.println("<td>"+ items.get(i).getPrice()+"</td>");
 		    	out.println("<td>"+ items.get(i).getPostage()+"</td>");
 		    	out.println("<td>"+ items.get(i).getLocation()+"</td>");
 		    	if (items.get(i).getSold())
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-ok\"></span></td>");
 		    	}
 		    	else
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-remove\"></span></td>");
 		    	}
 		    	out.println("<td><img src=\"" + items.get(i).getImage()+ "\"></td>");
 		    	out.println("</tr>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
     }
    
    @PostMapping("/saveSearch")
    public void saveSearch(@Valid Search search, HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	search.setSearchURL(eBay.searchURL(search));
     	//loggedInUser.addSearch(search);
	    //userService.save(loggedInUser);
     	User temp = userService.findByUsername(loggedInUser.getUsername()).get(0);
	    temp.addSearch(search);
	    userService.save(temp);
	    loginCheck(loggedInUser);
	    lastSearch = search;
     	List<Item> items = eBay.getResults(search);
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    out.println("<center><h2>Search saved to account</h2></center>");
 		    out.println("<div class=\"jumbotron\">");
 		    out.println("<p>Search : " + search.getKeyword() + "(" + items.size() +")</p>");
 		    out.println("");
 		    out.println("<form action=\"http://localhost:8080/searchStatistics\">");
		    out.println("<center><input type=\"submit\" class=\"btn btn-default\" value=\"View Statistics\" /></center></form>");
		    
 		    out.println("</tr></table></form>");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Title</th>");
 		    out.println("<th>Price</th>");
 		    out.println("<th>Postage</th>");
		    out.println("<th>Location</th>");
 		    out.println("<th>Sold</th>");
 		    out.println("<th>Image</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<items.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	out.println("<td>" + items.get(i).getTitle() + "</td>");
 		    	out.println("<td>"+ items.get(i).getPrice()+"</td>");
 		    	out.println("<td>"+ items.get(i).getPostage()+"</td>");
 		    	out.println("<td>"+ items.get(i).getLocation()+"</td>");
 		    	if (items.get(i).getSold())
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-ok\"></span></td>");
 		    	}
 		    	else
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-remove\"></span></td>");
 		    	}
 		    	out.println("<td><img src=\"" + items.get(i).getImage()+ "\"></td>");
 		    	out.println("</tr>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
     }
    
    @GetMapping("/searchStatistics")
    public void searchStatistics (HttpServletRequest request, HttpServletResponse response)throws IOException {
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	eBayScraper eBay = new eBayScraper();
    	Statistics stats = eBay.getStatistics(lastSearch);
    	/*int brandNew = 0;
    	int newOther = 0;
    	int preOwned = 0;
    	int noCondition = 0;
    	int popularConditionNumber = 0;
    	String popularCondition = "";
    	for (String a : stats.getConditions())
    	{
    		if (a.equalsIgnoreCase("Brand New"))
    		{
    			brandNew++;
    		}
    		else if (a.equalsIgnoreCase("New (other)"))
    		{
    			newOther++;
    		}
    		else if (a.equalsIgnoreCase("Pre-owned"))
    		{
    			preOwned++;
    		}
    		else
    		{
    			noCondition++;
    		}
    	}
    	
    	if (preOwned > 0)
    	{
    		popularConditionNumber = preOwned;
    		popularCondition = "Pre-owned";
    		if (newOther > popularConditionNumber)
    		{
    			popularConditionNumber = newOther;
    			popularCondition = "New (other)";
    			if (brandNew > popularConditionNumber)
    			{
    				popularConditionNumber = brandNew;
    				popularCondition = "Brand New";
    				if (noCondition > popularConditionNumber)
        			{
        				popularConditionNumber = noCondition;
        				popularCondition = "None";
        			}
    			}
    		}
    	}*/
    	htmlHeader( out);
     	toolBarStart(out);
     	toolBarEnd(out);
     	container(out);
		    out.println("<p>Search : " + lastSearch.getKeyword() + "</p>");
		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Number of Items</th>");
 		    out.println("<th>Sold</th>");
 		    out.println("<th>Not Sold</th>");
 		    out.println("<th>Average Price</th>");
 		    out.println("<th>Average Postage</th>");
 		    out.println("<th>BIN's</th>");
 		    out.println("<th>Auctions</th>");
 		    out.println("<th>With Pictures</th>");
 		   // out.println("<th>Most Common Condition</th>");
 		    out.println("<th>Highest Price</th>");
 		    out.println("<th>Lowest Price</th>");
 		    out.println("<th>Range</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    	out.println("<tr>");
 		    	out.println("<td>" + stats.getNumberListings() + "</td>");
 		    	out.println("<td>"+ stats.getNumberSold()+"</td>");
 		    	out.println("<td>"+ (stats.getNumberListings() - stats.getNumberSold())+"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + df2.format(stats.getAveragePrice())+"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + df2.format(stats.getAveragePostage())+"</td>");
 		    	out.println("<td>"+ (stats.getNumberListings() - stats.getNumberAuctions())+"</td>");
 		    	out.println("<td>"+ stats.getNumberAuctions() +"</td>");
 		    	out.println("<td>"+ stats.getNumberPictures() +"</td>");
 		    	//out.println("<td>"+ popularCondition +"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + stats.getHighestPrice() +"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + stats.getLowestPrice() +"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + stats.getRange() +"</td>");
 		    	out.println("</tr>");
 		    	out.println("</tbody>");
 		    out.println("</table>");
 		    scriptStart(out);
 		    pieChart(1, out,  stats.getNumberSold(), (stats.getNumberListings() - stats.getNumberSold()), "Sold", "Not Sold", "Sold vs. Not Sold (" + stats.getNumberListings() + " Listings)", "chart1");
 		    pieChart(2, out, (stats.getNumberListings() - stats.getNumberAuctions()), stats.getNumberAuctions(), "Buy it Now", "Auction", "Listing Types", "chart2");
 		    //pieChart(3, out, brandNew, newOther, preOwned,  "Brand New", "Like New", "Pre-Owned", "Item Condition", "chart3");
 		    areaChart(4, out, stats.getPrices(), "Prices", "chart4");
 		    scriptEnd(out);
 		   out.println(" <center><div id=\"chart1\" style=\"height: 450px; width: 75%;\"></div></center>");
 		   out.println("<br>");
 		  out.println(" <center><div id=\"chart2\" style=\"height: 450px; width: 75%;\"></div></center>");
 		  //out.println("<br>");
		  //out.println(" <center><div id=\"chart3\" style=\"height: 450px; width: 75%;\"></div></center>");
		  out.println("<br>");
		  out.println(" <center><div id=\"chart4\" style=\"height: 450px; width: 75%;\"></div></center>");
 		    htmlFooter(out);
    }
    
    @GetMapping("/mySearches")
    public void mySearches( HttpServletRequest request, HttpServletResponse response)throws IOException {
    	if (loggedInUser != null)
    	{
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	List<Search> searches = loggedInUser.getSearches();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		    out.println("");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Search Link</th>");
 		    out.println("<th>Category</th>");
 		    out.println("<th>Website</th>");
 		    out.println("<th>Listing Type</th>");
 		    out.println("<th>Free Postage Only</th>");
 		    out.println("<th>Sold Only</th>");
 		    out.println("<th>Number of Listings</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<searches.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	//out.println("<td><form th:action=\"@{/search/}\" th:object=\"${search}\" method=\"post\"><input type=\"hidden\" th:field=\"${id}\" /><button type=\"submit\">" + searches.get(i).getKeyword() + "</button></form></td>");
 		    	out.println("<td> <a href=\"http://localhost:8080/search/" + searches.get(i).getId() + "\">" + searches.get(i).getKeyword() + "</a></td>");
 		    	out.println("<td>" + searches.get(i).getCategory() + "</td>");
 		    	out.println("<td>" + searches.get(i).getWebsite() + "</td>");
 		    	out.println("<td>" + searches.get(i).getListingType() + "</td>");

 		    	if (searches.get(i).isFreePostage())
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-ok\"></span></td>");
 		    	}
 		    	else
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-remove\"></span></td>");
 		    	}
 		    	
 		    	if (searches.get(i).isSold())
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-ok\"></span></td>");
 		    	}
 		    	else
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-remove\"></span></td>");
 		    	}
 		    	out.println("<td>" + searches.get(i).getNumberOfListings() + "</td>");
 		    	out.println("</tr>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    	}
    	else
    	{
    		response.sendRedirect("http://localhost:8080/login");
    	}
     }

    @GetMapping("/search/{id}")
    @ResponseBody
    public void handler(@PathVariable(value = "id") int id, HttpServletRequest request, HttpServletResponse response)throws IOException {
    	Search current = searchService.findById(id).get(0);
        response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	lastSearch = current;
     	
     	System.out.println(current.getKeyword());
     	
     	List<Item> items = eBay.getResults(current);
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		   out.println("<p>Search : " + current.getKeyword() + " (" + items.size() +" Listings Found)</p>");
 		    out.println("");
 		    out.println("<form action=\"http://localhost:8080/searchStatistics\">");
		    out.println("<center><input type=\"submit\" class=\"btn btn-default\" value=\"View Statistics\" /></center></form>");

		    out.println("</tr></table></form>");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Title</th>");
 		    out.println("<th>Condition</th>");
 		    out.println("<th>Price</th>");
 		    out.println("<th>Postage</th>");
 		    out.println("<th>Location</th>");
 		    out.println("<th>Sold</th>");
 		    out.println("<th>Image</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<items.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	out.println("<td>" + items.get(i).getTitle() + "</td>");
 		    	out.println("<td>" + items.get(i).getItemCondition() + "</td>");
 		    	out.println("<td>"+ items.get(i).getPrice()+"</td>");
 		    	out.println("<td>"+ items.get(i).getPostage()+"</td>");
 		    	out.println("<td>"+ items.get(i).getLocation()+"</td>");
 		    	if (items.get(i).getSold())
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-ok\"></span></td>");
 		    	}
 		    	else
 		    	{
 		    		out.println("<td><span class=\"glyphicon glyphicon-remove\"></span></td>");
 		    	}
 		    	out.println("<td><img src=\"" + items.get(i).getImage()+ "\"></td>");
 		    	out.println("</tr>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    }
    
    //Purchases
    @GetMapping("/myPurchases")
    public void myPurchases( HttpServletRequest request, HttpServletResponse response)throws IOException {
    	if (loggedInUser != null)
    	{
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	List<Purchase> purchases = loggedInUser.getPurchases();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		    out.println("");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Title</th>");
 		    out.println("<th>Price</th>");
 		    out.println("<th>Postage</th>");
 		    out.println("<th>Location</th>");
 		    out.println("<th>Date</th>");
 		    out.println("<th>Condition</th>");
 		    out.println("<th>Seller</th>");
 		    out.println("<th>Link</th>");
 		    out.println("<th>Image</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<purchases.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	out.println("<td>" + purchases.get(i).getTitle() + "</td>");
 		    	out.println("<td>"+ purchases.get(i).getPrice()+"</td>");
 		    	out.println("<td>"+ purchases.get(i).getPostage()+"</td>");
 		    	out.println("<td>"+ purchases.get(i).getLocation()+"</td>");
 		    	out.println("<td>"+ purchases.get(i).getDate()+"</td>");
 		    	out.println("<td>"+ purchases.get(i).getItemCondition()+"</td>");
 		    	out.println("<td>"+ purchases.get(i).getSellerName()+"</td>");
 		    	out.println("<td> <a href=\"" + purchases.get(i).getLink() + "\">Link to eBay</a></td>");
 		    	out.println("<td><img src=\"" + purchases.get(i).getImage()+ "\"></td>");
 		    	out.println("</tr>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    	}
    	else
    	{
    		response.sendRedirect("http://localhost:8080/login");
    	}
     }
    
    @PostMapping("/recordPurchase")
    public void recordPurchase(@Valid Search search, HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	Purchase p = eBay.getPurchase(search.getKeyword());
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    if (p != null)
 		    {
 		    	//loggedInUser.addPurchase(p);
 		    	User temp = userService.findByUsername(loggedInUser.getUsername()).get(0);
 		    	temp.addPurchase(p);
 		    	userService.save(temp);
 		    	loginCheck(loggedInUser);
 		    	//userService.save(loggedInUser);
 		    out.println("<center><h2>Purchase recorded successfully</h2></center>");
 		    out.println("<div class=\"jumbotron\">");
		    htmlFooter(out);
 		    }
 		    else
 		    {
 		    	out.println("<center><h2>Invalid URL, please enter the URL of a completed eBay listing</h2></center>");
 	 		    out.println("<div class=\"jumbotron\">");
 	 		    out.println("<form action=\"http://localhost:8080/record\">");
			    out.println("<center><input type=\"submit\" class=\"btn btn-default\" value=\"Back\" /></center></form>");
 	 		    htmlFooter(out);
 		    }
     }

    //Sales
    @GetMapping("/mySales")
    public void mySales( HttpServletRequest request, HttpServletResponse response)throws IOException {
    	if (loggedInUser != null)
    	{
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	List<Sale> sales = loggedInUser.getSales();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		    out.println("");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Title</th>");
 		    out.println("<th>Price</th>");
 		    out.println("<th>Postage</th>");
 		    out.println("<th>Location</th>");
 		    out.println("<th>Date</th>");
 		    out.println("<th>Fee</th>");
 		    out.println("<th>Condition</th>");
 		    out.println("<th>Link</th>");
 		    out.println("<th>Image</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<sales.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	out.println("<td>" + sales.get(i).getTitle() + "</td>");
 		    	out.println("<td>"+ sales.get(i).getPrice()+"</td>");
 		    	out.println("<td>"+ sales.get(i).getPostage()+"</td>");
 		    	out.println("<td>"+ sales.get(i).getLocation()+"</td>");
 		    	out.println("<td>"+ sales.get(i).getDate()+"</td>");
 		    	out.println("<td>"+ sales.get(i).getFee()+"</td>");
 		    	out.println("<td>"+ sales.get(i).getItemCondition()+"</td>");
 		    	out.println("<td> <a href=\"" + sales.get(i).getLink() + "\">Link to eBay</a></td>");
 		    	out.println("<td><img src=\"" + sales.get(i).getImage()+ "\"></td>");
 		    	out.println("</tr>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    	}
    	else
    	{
    		response.sendRedirect("http://localhost:8080/login");
    	}
     }
    
    @PostMapping("/recordSale")
    public void recordSale(@Valid Search search, HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	Sale s = eBay.getSale(search.getKeyword());
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    if (s != null)
 		    {
 		    	//loggedInUser.addSale(s);
 		    	//userService.save(loggedInUser);
 		    	User temp = userService.findByUsername(loggedInUser.getUsername()).get(0);
 		    	temp.addSale(s);
 		    	userService.save(temp);
 		    	loginCheck(loggedInUser);
 		    out.println("<center><h2>Sale recorded successfully</h2></center>");
 		    out.println("<div class=\"jumbotron\">");
		    htmlFooter(out);
 		    }
 		    else
 		    {
 		    	out.println("<center><h2>Invalid URL, please enter the URL of a completed eBay listing</h2></center>");
 	 		    out.println("<div class=\"jumbotron\">");
 	 		    out.println("<form action=\"http://localhost:8080/record\">");
			    out.println("<center><input type=\"submit\" class=\"btn btn-default\" value=\"Back\" /></center></form>");
 	 		    htmlFooter(out);
 		    }
     }
    
    public void updateSalesMethod() throws IOException
    {
    	//search ebay for user
    	//parse items
    	//for each item,
    	//check item title, date
    	//if doesnt exist,
    	//add to user's list of items
    	
    	eBayScraper eBay = new eBayScraper();
     	List<Sale> userSales = eBay.getUserSales(loggedInUser);
     	List<Sale> prevSales = loggedInUser.getSales();
     	System.out.println(userSales.toString());
     	System.out.println(userSales.size());
     	System.out.println(prevSales.toString());
     	System.out.println(prevSales.size());
     	
     	for (Sale s : userSales)
     	{
     		boolean exists = false;
     		for (Sale c : prevSales)
     		{
     			System.out.println(s.getTitle());
     			System.out.println(c.getTitle());
     			System.out.println(s.getLink());
     			System.out.println(c.getLink());
     			if (s.getTitle().equals(c.getTitle()) && s.getLink().equals(c.getLink()))
     			{
     				
     				exists = true;
     			}
     		}
     			if (exists ==false)
     			{
     				Sale newSale = s;
     				User temp = userService.findByUsername(loggedInUser.getUsername()).get(0);
     		    	temp.addSale(newSale);
     		    	userService.save(temp);
     		    	loginCheck(loggedInUser);
     			}
     		
     	}
    }
    
    @GetMapping("/updateSales")
    public void updateSales(HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	if (loggedInUser != null)
    	{
     	updateSalesMethod();
     	List<Sale> sales = loggedInUser.getSales();
     	htmlHeader( out);
     	toolBarStart(out);
     	toolBarEnd(out);
     	container(out);
		    out.println("");
		    out.println("<table class=\"table\">");
		    out.println("<thead>");
		    out.println("<tr>");
		    out.println("<th>Title</th>");
		    out.println("<th>Price</th>");
		    out.println("<th>Postage</th>");
		    out.println("<th>Location</th>");
		    out.println("<th>Date</th>");
		    out.println("<th>Fee</th>");
		    out.println("<th>Condition</th>");
		    out.println("<th>Link</th>");
		    out.println("<th>Image</th>");
		    out.println("</tr>");
		    out.println("</thead>");
		    out.println("<tbody>");
		    for (int i=0; i<sales.size(); i++)
		    {
		    	out.println("<tr>");
		    	out.println("<td>" + sales.get(i).getTitle() + "</td>");
		    	out.println("<td>"+ sales.get(i).getPrice()+"</td>");
		    	out.println("<td>"+ sales.get(i).getPostage()+"</td>");
		    	out.println("<td>"+ sales.get(i).getLocation()+"</td>");
		    	out.println("<td>"+ sales.get(i).getDate()+"</td>");
		    	out.println("<td>"+ sales.get(i).getFee()+"</td>");
		    	out.println("<td>"+ sales.get(i).getItemCondition()+"</td>");
		    	out.println("<td> <a href=\"" + sales.get(i).getLink() + "\">Link to eBay</a></td>");
		    	out.println("<td><img src=\"" + sales.get(i).getImage()+ "\"></td>");
		    	out.println("</tr>");
		    }
		    out.println("</tbody>");
		    out.println("</table>");
		    htmlFooter(out);
	}
	else
	{
		response.sendRedirect("http://localhost:8080/login");
	}
     }
    
    //Reports
    @GetMapping("/myReports")
    public void myReports( HttpServletRequest request, HttpServletResponse response)throws IOException {
    	
    	if (loggedInUser != null)
    	{
    	createReports();
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
     	
     	Calendar cal = Calendar.getInstance();
    	String currentMonth = monthName[cal.get(Calendar.MONTH)];
    	int numberOfSales = 0;
    	double totalSales = 0;
    	double fees = 0;
    	int numberOfPurchases = 0;
    	double totalPurchases = 0;
    	double postageCosts = 0;
    	
     	if (currentReport != null)
     	{
     		numberOfSales = currentReport.getNumberOfSales();
     		totalSales = currentReport.getTotalSales();
     		fees = currentReport.getFees();
     		numberOfPurchases = currentReport.getNumberOfPurchases();
     		totalPurchases = currentReport.getTotalPurchases();
     		postageCosts = currentReport.getPostageCosts();
     	}
     	List<Report> reports = loggedInUser.getReports();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		   out.println("<center><h2>This month's Report : </h2></center>");
 		    out.println("");
		    out.println("<table class=\"table\">");
		    out.println("<thead>");
		    out.println("<tr>");
		    out.println("<th>" + currentMonth + "</th>");
		    out.println("<th></th>");
		    out.println("<th></th>");
		    out.println("<th></th>");
		    out.println("<th> Value </th>");
		    out.println("</tr>");
		    out.println("</thead>");
		    out.println("<tbody>");
		    out.println("<tr>");
		    out.println("<td> Number of Sales</td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td>" + numberOfSales + "</td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td>Fees</td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td>-" + fees + "</td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td>Gross Sales</td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td>" + (totalSales - fees) + "</td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td>Purchases</td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td>" + totalPurchases + " (" + numberOfPurchases + " Items)</td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td>Postage Costs</td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td>+" + postageCosts + "</td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td>Cost of Purchases</td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td>" + (totalPurchases + postageCosts) + "</td>");
		    out.println("</tr>");
		    out.println("<tr>");
		    out.println("<td><b>Profit</b></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td></td>");
		    out.println("<td><b>" +((totalSales - fees) - (totalPurchases + postageCosts)) + "</b></td>");
		    out.println("</tr>");
		    out.println("</tbody>");
		    out.println("</table>");
 		    out.println("");
 		    out.println("<table class=\"table\">");
 		    out.println("<thead>");
 		    out.println("<tr>");
 		    out.println("<th>Previous Reports</th>");
 		    out.println("</tr>");
 		    out.println("</thead>");
 		    out.println("<tbody>");
 		    for (int i=0; i<reports.size(); i++)
 		    {
 		    	out.println("<tr>");
 		    	//out.println("<td><form th:action=\"@{/search/}\" th:object=\"${search}\" method=\"post\"><input type=\"hidden\" th:field=\"${id}\" /><button type=\"submit\">" + searches.get(i).getKeyword() + "</button></form></td>");
 		    	out.println("<td> <a href=\"http://localhost:8080/report/" + reports.get(i).getId() + "\">" + reports.get(i).getReportName() + "</a></td>");
 		    }
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    	}
    	else
    	{
    		response.sendRedirect("http://localhost:8080/login");
    	}
     }
    
    public void createReports()
    {
    	String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
    	
    	ArrayList<String> monthList = new ArrayList<String>();
    	for (String a : monthName)
    	{
    		monthList.add(a);
    	}
    	
    	ArrayList<String> reportMonths = new ArrayList<String>();
    	
    	for (Sale s : loggedInUser.getSales())
    	{
    		String a = extractMonth(s.getDate());
    		if (!reportMonths.contains(a))
    		{
    			reportMonths.add(a);
    		}
    	}
    	
    	for (Purchase p : loggedInUser.getPurchases())
    	{
    		String b = extractMonth(p.getDate());
    		if (!reportMonths.contains(b))
    		{
    			reportMonths.add(b);
    		}
    	}
    	
    	Calendar cal = Calendar.getInstance();
    	String currentMonth = monthName[cal.get(Calendar.MONTH)];
    	
    	for (String month : reportMonths)
    	{
    		String reportName = "";
        	int numberOfSales = 0;
        	int numberOfPurchases = 0;
        	double totalSales = 0;
        	double fees = 0;
        	double totalPurchases = 0;
        	double totalPostage = 0;
    			for (Sale s : loggedInUser.getSales())
    	    	{
    	    		if (month.contains(extractMonth(s.getDate())))
    	    		{
    	    			numberOfSales++;
    	    			totalSales = totalSales + getPriceAsDouble(s.getPrice());
    	    			reportName =  loggedInUser.getUsername() + month + extractYear(s.getDate());
    	    			fees = fees + Double.valueOf(s.getFee());
    	    		}
    	    	}
    	    	
    	    	for (Purchase p : loggedInUser.getPurchases())
    	    	{
    	    		numberOfPurchases++;
    	    		totalPurchases = totalPurchases + getPriceAsDouble(p.getPrice());
    	    		totalPostage = totalPostage + getPriceAsDouble(p.getPostage());
    	    		reportName = loggedInUser.getUsername() + month + extractYear(p.getDate());
    	    	}
    	    	Report r = new Report (reportName, numberOfSales, totalSales, fees, numberOfPurchases, totalPurchases, totalPostage);
    	    	
    	    	List<Report> existingReport = reportService.findByReportName(reportName);
    	    	if (existingReport.isEmpty())
    	    	{
    	    	
    	    	//loggedInUser.addReport(r);
    	    	//userService.save(loggedInUser);
    	    		if (month.equals(currentMonth))
    	    		{
    	    			currentReport = r;
    	    			break;
    	    		}
    	    		User temp = userService.findByUsername(loggedInUser.getUsername()).get(0);
     		    	temp.addReport(r);
     		    	userService.save(temp);
     		    	loginCheck(loggedInUser);
    	    	
    	    	}
    	    	else
    	    	{
    	    		existingReport.get(0).setNumberOfSales(numberOfSales);
    	    		existingReport.get(0).setTotalSales(totalSales);
    	    		existingReport.get(0).setFees(fees);
    	    		existingReport.get(0).setNumberOfPurchases(numberOfPurchases);
    	    		existingReport.get(0).setTotalPurchases(totalPurchases);
    	    		existingReport.get(0).setPostageCosts(totalPostage);
    	    		
    	    		reportService.save(existingReport.get(0));
    	    		loginCheck(loggedInUser);
    	    	}
    	}
    	
    	

    }
    
    @GetMapping("/report/{id}")
    @ResponseBody
    public void reportHandler(@PathVariable(value = "id") int id, HttpServletRequest request, HttpServletResponse response)throws IOException {
    	Report current = reportService.findById(id).get(0);
        response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	currentReport = current;
     	
     	htmlHeader( out);
     	toolBarStart(out);
     	toolBarEnd(out);
     	container(out);
		    out.println("");
	    out.println("<table class=\"table\">");
	    out.println("<thead>");
	    out.println("<tr>");
	    out.println("<th>" + current.getReportName() + "</th>");
	    out.println("<th></th>");
	    out.println("<th></th>");
	    out.println("<th></th>");
	    out.println("<th> Value </th>");
	    out.println("</tr>");
	    out.println("</thead>");
	    out.println("<tbody>");
	    out.println("<tr>");
	    out.println("<td>Sales</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>" + current.getTotalSales() + " (" + current.getNumberOfSales() + " Items)</td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>Fees</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>-" + current.getFees() + "</td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>Gross Sales</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>" + (current.getTotalSales() - current.getFees()) + "</td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>Purchases</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>" + current.getTotalPurchases() + " (" + current.getNumberOfPurchases() + " Items)</td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>Postage Costs</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>+" + current.getPostageCosts() + "</td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>Cost of Purchases</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>" + (current.getTotalPurchases() + current.getPostageCosts()) + "</td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>Profit</td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td></td>");
	    out.println("<td>" +((current.getTotalSales() - current.getFees()) - (current.getTotalPurchases() + current.getPostageCosts())) + "</td>");
	    out.println("</tr>");
	    
	    out.println("</tbody>");
	    out.println("</table>");


		    htmlFooter(out);
    }
    
    //General Items
    @GetMapping("/myItems")
    public String myItems() {
    	if (loggedInUser != null)
    	{
            return "myItems";
    	}
    	else
    	{
    		return "loginRegister";
    	}
    }
    
    @GetMapping("/record")
    public String record(Search search) {
    	if (loggedInUser != null)
    	{
            return "logItem";
    	}
    	else
    	{
    		return "loginRegister";
    	}
    }
    
    @GetMapping("/myTest")
    public void myTest( HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
         	container(out);
 		    out.println("");
 		    out.println("<script>");
 		    out.println("window.onload = function () {");
 		    out.println("var options = {");
 		    out.println("title: {");
 		    out.println("text: \"Column Chart in jQuery CanvasJS\"},");
 		    out.println("data: [ { type: \"column\",");
 		    out.println("dataPoints: [");
 		    out.println("{ label: \"apple\",  y: 10  },");
 		    out.println("{ label: \"orange\", y: 20.49  },");
 		    out.println("{ label: \"banana\", y: 25  },");
 		    out.println("{ label: \"mango\",  y: 30  },");
 		    out.println("{ label: \"grape\",  y: 211  }]}]};");
 		    out.println("$(\"#chartContainer\").CanvasJSChart(options);");
 		    pieChart(1,  out, 56, 56, "test", "test", "test", "testChart");
 		    out.println("</script>");
 		    out.print(" <div id=\"chartContainer\" style=\"height: 250px; width: 33%;\"></div>");
 		    out.println(" <div id=\"chartContainer1\" style=\"height: 250px; width: 33%;\"></div>");
 		    out.println("<script type=\"text/javascript\" src=\"https://canvasjs.com/assets/script/jquery-1.11.1.min.js\"></script>");
 		    out.println("<script type=\"text/javascript\" src=\"https://canvasjs.com/assets/script/jquery.canvasjs.min.js\"></script>");
 		    
 		    out.println(" <p id=\"demo\"></p>");
 		
 		
 		    htmlFooter(out);
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
    
    //Error Page
    @GetMapping("/incorrectUrl")
    public String incorrectUrl(Search search) {

            return "incorrectUrl";
    }
    
    //Location / Site variables
    public String correctCurrency(Search s)
    {
    	switch(s.getWebsite())
    	{
		case "eBay.ie" : return "EUR";
		case "eBay.co.uk" : return "£";
		case "eBay.com" : return "$";
		case "eBay.ca" : return "C$";
		case "eBay.com.au" : return "AU";
		case "eBay.es" : return "EUR";
		case "eBay.fr" : return "EUR";
		case "eBay.it" : return "EUR";
		case "eBay.de" : return "EUR";
		case "eBay.ch" : return "EUR";
		case "eBay.at" : return "EUR";
		case "eBay.nl" : return "EUR";
		case "eBay.in" : return "Rs.";
    	}
    	
    	return "";
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
     
	public String extractMonth(String date)
	{
		String parts[] = date.split(" ");
		String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};
		
		for (String a : monthName)
		{
			if (a.contains(parts[1].replaceAll(",", "")))
			{
				return a;
			}
		}
		return "error";
	}
	
	public String extractYear(String date)
	{
		String parts[] = date.split(" ");

				return parts[2];

	}
	
	//HTML writer methods
     public void toolBarStart(PrintWriter out)
     {
    	 out.println("<nav class=\"navbar navbar-inverse\">");
    	 out.println("<div class=\"container-fluid\">");
    	 out.println("<ul class=\"nav navbar-nav\">");
    	 if (loggedInUser != null)
    	 {
    	 out.println("<li><a href=\"http://localhost:8080/logout\">Sign Out</a></li>");
    	 }
    	 else
    	 {
    		 out.println("<li><a href=\"http://localhost:8080/register\">Register</a></li>");
    		 out.println("<li><a href=\"http://localhost:8080/login\">Sign In</a></li>");
    	 }
    	 out.println("<li><a href=\"/\">Home</a></li>");
    	 out.println("<li><a href=\"http://localhost:8080/search\">Search</a></li>");
    	 out.println("<li><a href=\"http://localhost:8080/record\">Record an Item</a></li>");
    	 out.println("<li class=\"dropdown\">");
    	 out.println("<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">My Items <span class=\"caret\"></span></a>");
    	 out.println("<ul class=\"dropdown-menu\">");
    	 out.println("<li><a href=\"http://localhost:8080/myPurchases/\">My Purchases</a></li>");
    	 out.println("<li><a href=\"http://localhost:8080/mySales/\">My Sales</a></li>");
    	 out.println("<li><a href=\"http://localhost:8080/mySearches/\">My Searches</a></li>");
    	 out.println("<li><a href=\"http://localhost:8080/myReports/\">My Reports</a></li>");
    	 out.println("</ul>");
    	 out.println("</li>");
    	 
     }
     
     public void toolBarEnd(PrintWriter out)
     {
    	 out.println("</ul>");
    	 out.println("</div>");
    	 out.println("</nav>");
     }
     
     public void container(PrintWriter out)
     {
    	 out.println(" <div class=\"container\">");
		 out.println("<div class=\"jumbotron\">");
     }
     
     public void htmlHeader(PrintWriter out)
     {
	    	out.println("<!DOCTYPE html>");
		    out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"/>");
		    out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>");
		    out.println("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>");
		    out.println("<html xmlns:th=\"http://www.thymeleaf.org\">");
		    out.println("<body>");
     }
     
     public void htmlFooter(PrintWriter out)
     {
    	 out.println("<div>");
		 out.println("<div>");
		 out.println("</body>");
		 out.println("</html>");
     }
     
     //Charts
 	public void pieChart(int chartId, PrintWriter out, int point1, int point2, String name1, String name2, String title, String chartName)
 	{
 		out.println("var options" + chartId + " = {");
 		out.println("exportEnabled: true,");
 		out.println("animationEnabled: true,");
 		out.println("title:{");
 		out.println("text: \"" + title + "\"");
 		out.println("},");
 		out.println("legend:{");
 		out.println("horizontalAlign: \"right\",");
 		out.println("verticalAlign: \"center\"");
 		out.println("},");
 		out.println("data: [{");
 		out.println("type: \"pie\",");
 		out.println("showInLegend: true,");
 		out.println("toolTipContent: \"<b>{name}</b>: {y} (#percent%)\",");
 		out.println("indexLabel: \"{name}\",");
 		out.println("legendText: \"{name} (#percent%)\",");
 		out.println("indexLabelPlacement: \"inside\",");
 		out.println("dataPoints: [");
 		out.println("{ y: " + point1 + ", name: \"" + name1 + "\" },");
 		out.println("{ y: " + point2 + ", name: \"" + name2 + "\" },");
 		out.println("]");
 		out.println("}]");
 		out.println("};");
 		out.println("$(\"#" + chartName + "\").CanvasJSChart(options" + chartId + ");");
 	}
 	
 	public void pieChart(int chartId, PrintWriter out, int point1, int point2, int point3, String name1, String name2, String name3, String title, String chartName)
 	{
 		out.println("var options" + chartId + " = {");
 		out.println("exportEnabled: true,");
 		out.println("animationEnabled: true,");
 		out.println("title:{");
 		out.println("text: \"" + title + "\"");
 		out.println("},");
 		out.println("legend:{");
 		out.println("horizontalAlign: \"right\",");
 		out.println("verticalAlign: \"center\"");
 		out.println("},");
 		out.println("data: [{");
 		out.println("type: \"pie\",");
 		out.println("showInLegend: true,");
 		out.println("toolTipContent: \"<b>{name}</b>: {y} (#percent%)\",");
 		out.println("indexLabel: \"{name}\",");
 		out.println("legendText: \"{name} (#percent%)\",");
 		out.println("indexLabelPlacement: \"inside\",");
 		out.println("dataPoints: [");
 		out.println("{ y: " + point1 + ", name: \"" + name1 + "\" },");
 		out.println("{ y: " + point2 + ", name: \"" + name2 + "\" },");
 		out.println("{ y: " + point3 + ", name: \"" + name3 + "\" },");
 		out.println("]");
 		out.println("}]");
 		out.println("};");
 		out.println("$(\"#" + chartName + "\").CanvasJSChart(options" + chartId + ");");
 	}
 	
 	public void areaChart(int chartId, PrintWriter out, ArrayList<Double> points, String title, String chartName)
 	{
 		out.println("var options" + chartId + " = {");
 		out.println("exportEnabled: true,");
 		out.println("animationEnabled: true,");
 		out.println("title:{");
 		out.println("text: \"" + title + "\"");
 		out.println("},");
 		out.println("axisY: {");
 		out.println("title: \"Price\",");
 		out.println("},");
 		out.println("axisX: {");
 		out.println("title: \"Item\"");
 		out.println("},");
 		out.println("data: [{");
 		out.println("type: \"spline\",");
 		out.println("dataPoints: [");
 		for (Double a : points)
 		{
 			out.println("{ y: " + a + "},");
 		}
 		out.println("]");
 		out.println("}]");
 		out.println("};");
 		out.println("$(\"#" + chartName + "\").CanvasJSChart(options" + chartId + ");");
 	}
 	
 	//Javascript Methods
 	public void scriptStart(PrintWriter out)
 	{
 		out.println("<script>");
	    out.println("window.onload = function () {");
 	}
 	
 	public void scriptEnd(PrintWriter out)
 	{
 		out.println("}");
 		out.println("</script>");
	    out.println("<script type=\"text/javascript\" src=\"https://canvasjs.com/assets/script/jquery-1.11.1.min.js\"></script>");
	    out.println("<script type=\"text/javascript\" src=\"https://canvasjs.com/assets/script/jquery.canvasjs.min.js\"></script>");
 	}
}