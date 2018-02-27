package com.DH.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import com.DH.Entity.Sale;
import com.DH.Entity.Search;
import com.DH.Entity.Statistics;
import com.DH.Entity.User;
import com.DH.Scraper.eBayScraper;
import com.DH.Service.PurchaseService;
import com.DH.Service.SearchService;
import com.DH.Service.UserService;


@Controller
public class WebController extends WebMvcConfigurerAdapter {
	
	@Autowired
	UserService userService;
	
	@Autowired
	PurchaseService purchaseService;
	
	@Autowired
	SearchService searchService;
	
	public User loggedInUser;
	
	public Search lastSearch;
	private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }
    
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

    @GetMapping("/register")
    public String showForm(User user) {
        return "form";
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

    @PostMapping("/registerProcess")
   public void register(@Valid User user, HttpServletRequest request, HttpServletResponse response)throws IOException {
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
        if (registrationCheck(user)) {
        	userService.save(user);
        	htmlHeader( out);
		    out.println(" <div class=\"container\">");
		    out.println("<div class=\"jumbotron\">");
		    out.println("<p>Successfully registered user:" + user.getUsername() + "</p>");
		    out.println("<form action=\"http://localhost:8080/login\">");
		    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Login\" /></form>");
		    htmlFooter(out);
        }
        else
        {
        	htmlHeader( out);
		    out.println(" <div class=\"container\">");
		    out.println("<div class=\"jumbotron\">");
		    out.println("<p>Invalid registration details</p>");
		    out.println("");
		    out.println("<form action=\"http://localhost:8080/register\">");
		    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Back to Register\" /></form>");
		    htmlFooter(out);
        }
    }
    
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
		    out.println(" <div class=\"container\">");
		    out.println("<div class=\"jumbotron\">");
		    out.println("<p>You are logged in as :" + loggedInUser.getUsername() + "</p>");
		    out.println("<form action=\"http://localhost:8080/logout\">");
		    out.println("<input type=\"submit\" class=\"btn btn-default\" value=\"Logout\" /></form>");
		    htmlFooter(out);
	    	}
	    	else
	    	{
	    		htmlHeader( out);
			    out.println(" <div class=\"container\">");
			    out.println("<div class=\"jumbotron\">");
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
    
    public boolean registrationCheck(User user)
    {
    	boolean result = false;
    	if (userService.findByUsername(user.getUsername()).isEmpty() && !user.getUsername().isEmpty() && !user.getPassword().isEmpty())
    	{
    		result = true;
    	}
    	return result;
    }
    
    /*@GetMapping("/searchStatistics")
    public String searchStats(Model model) {
        model.addAttribute("searchStatistics", new Search());
        return "searchStatistics";
    }

    @PostMapping("/searchStatistics")
    public void resultsStats(@ModelAttribute Search s, HttpServletRequest request, HttpServletResponse response)throws IOException {
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	htmlHeader( out);
     	toolBarStart(out);
     	toolBarEnd(out);
		    out.println(" <div class=\"container\">");
		    out.println("<div class=\"jumbotron\">");
		    out.println("<p>Search :" + s.getKeyword() + "</p>");
		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    }*/
    
    @GetMapping("/searchStatistics")
    public void searchStatistics (HttpServletRequest request, HttpServletResponse response)throws IOException {
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	eBayScraper eBay = new eBayScraper();
    	Statistics stats = eBay.getStatistics(lastSearch);
    	htmlHeader( out);
     	toolBarStart(out);
     	toolBarEnd(out);
		    out.println(" <div class=\"container\">");
		    out.println("<div class=\"jumbotron\">");
		    out.println("<p>Search :" + lastSearch.getKeyword() + "</p>");
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
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + stats.getHighestPrice() +"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + stats.getLowestPrice() +"</td>");
 		    	out.println("<td>"+ correctCurrency(lastSearch) + " " + stats.getRange() +"</td>");
 		    	out.println("</tr>");
 		    out.println("</tbody>");
 		    out.println("</table>");
 		    htmlFooter(out);
    }
    
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
 		    out.println(" <div class=\"container\">");
 		    out.println("<div class=\"jumbotron\">");
 		   out.println("<p>Search :" + search.getKeyword() + " (" + items.size() +" Listings Found)</p>");
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
    
    @PostMapping("/saveSearch")
    public void saveSearch(@Valid Search search, HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	search.setSearchURL(eBay.searchURL(search));
     	loggedInUser.addSearch(search);
	    userService.save(loggedInUser);
	    loginCheck(loggedInUser);
	    lastSearch = search;
     	List<Item> items = eBay.getResults(search);
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    out.println("<center><h2>Search saved to account</h2></center>");
 		    out.println("<div class=\"jumbotron\">");
 		    out.println("<p>Search :" + search.getKeyword() + "(" + items.size() +")</p>");
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
    
    @GetMapping("/myPurchases")
    public void myPurchases( HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	List<Purchase> purchases = loggedInUser.getPurchases();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    out.println("<div class=\"jumbotron\">");
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
    
    @GetMapping("/mySales")
    public void mySales( HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	List<Sale> sales = loggedInUser.getSales();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    out.println("<div class=\"jumbotron\">");
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
    
    @GetMapping("/mySearches")
    public void mySearches( HttpServletRequest request, HttpServletResponse response)throws IOException {
     	response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	
     	List<Search> searches = loggedInUser.getSearches();
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    out.println("<div class=\"jumbotron\">");
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
    
   /* @RequestMapping(value="searches/view", method = RequestMethod.POST)
    public void viewSavedSearch (@RequestParam int id, HttpServletRequest request, HttpServletResponse response)throws IOException {
        Search current = searchService.findById(id).get(0);
        response.setContentType("text/html");
     	PrintWriter out = response.getWriter();
     	eBayScraper eBay = new eBayScraper();
     	lastSearch = current;
     	
     	List<Item> items = eBay.getResults(current);
         	htmlHeader( out);
         	toolBarStart(out);
         	toolBarEnd(out);
 		    out.println(" <div class=\"container\">");
 		    out.println("<div class=\"jumbotron\">");
 		   out.println("<p>Search :" + current.getKeyword() + " (" + items.size() +" Listings Found)</p>");
 		    out.println("");
 		    out.println("<form action=\"/searchStatistics\">");
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
    }*/
    
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
 		    out.println(" <div class=\"container\">");
 		    out.println("<div class=\"jumbotron\">");
 		   out.println("<p>Search :" + current.getKeyword() + " (" + items.size() +" Listings Found)</p>");
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
    
    @GetMapping("/incorrectUrl")
    public String incorrectUrl(Search search) {

            return "incorrectUrl";
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
 		    	//purchaseService.save(p);
 		    	loggedInUser.addPurchase(p);
 		    	userService.save(loggedInUser);
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
 		    	loggedInUser.addSale(s);
 		    	userService.save(loggedInUser);
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
    	 out.println("<li><a href=\"http://localhost:8080/myItems\">My Items</a></li>");
     }
     
     public void toolBarEnd(PrintWriter out)
     {
    	 out.println("</ul>");
    	 out.println("</div>");
    	 out.println("</nav>");
     }
     
     public void htmlHeader(PrintWriter out)
     {
	    	out.println("<!DOCTYPE html>");
		    out.println("<link href=\"http://cdn.jsdelivr.net/webjars/bootstrap/3.3.4/css/bootstrap.min.css\" th:href=\"@{/webjars/bootstrap/3.3.4/css/bootstrap.min.css}\"\r\n" + 
		    		"      rel=\"stylesheet\" media=\"screen\" />");
		    out.println("<script src=\"http://cdn.jsdelivr.net/webjars/jquery/2.1.4/jquery.min.js\"\r\n" + 
		    		"            th:src=\"@{/webjars/jquery/2.1.4/jquery.min.js}\"></script>");
		    out.println("<link href=\"../static/css/style.css\" th:href=\"@{css/style.css}\" rel=\"stylesheet\" media=\"screen\"/>");
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
}