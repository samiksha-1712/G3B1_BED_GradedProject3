package com.gl.tickettracker.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gl.tickettracker.dto.TicketDto;
import com.gl.tickettracker.service.TicketService;

@Controller
public class TicketController {

	private TicketService ticketService;

	// constructor based DI
	public TicketController(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	@GetMapping("/admin/Tickets")
	// create handler method, GET request and return model and view
	public String tickets(Model model) {
		List<TicketDto> tickets = ticketService.findAllTickets();
		model.addAttribute("tickets", tickets); // key value pair
		return "/admin/Tickets";
	}

	// handler method to handle new Ticket request
	@GetMapping("admin/Tickets/newTicket")
	public String newTicketForm(Model model) {
		TicketDto ticketDto = new TicketDto();
		model.addAttribute("ticket", ticketDto);
		return "admin/CreateTicket";
	}

	// handler method to handle form submit request
	@PostMapping("/admin/Tickets")
	public String createTicket(@Valid @ModelAttribute("ticket") TicketDto ticketDto, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("ticket", ticketDto);
			return "admin/CreateTicket";
		}
		ticketDto.setUrl(getUrl(ticketDto.getTitle()));
		ticketService.createTicket(ticketDto);
		return "redirect:/admin/Tickets";
	}

	private static String getUrl(String ticketTitle) {

		String title = ticketTitle.trim().toLowerCase();
		String url = title.replaceAll("\\s+", "-");
		url = url.replaceAll("[A-Za-z0-9]", "=");
		return url;
	}

	// handler method to handle edit ticket request
	@GetMapping("/admin/Tickets/{ticketId}/edit")
	public String editTicketForm(@PathVariable("ticketId") Long ticketId, Model model) {

		TicketDto ticketDto = ticketService.findTickerById(ticketId);
		model.addAttribute("ticket", ticketDto);
		return "admin/EditTicket";

	}

	// handler method to handle edit ticket form submit request
	@PostMapping("/admin/Tickets/{ticketId}")
	public String updateTicket(@PathVariable("ticketId") long ticketId,
			@Valid @ModelAttribute("ticket") TicketDto ticket, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("ticket", ticket);
			return "admin/EditTicket";
		}
		ticket.setId(ticketId);
		ticketService.updateTicket(ticket);
		return "redirect:/admin/Tickets";
	}

	// handler method to delete the ticket request
	@GetMapping("/admin/Tickets/{ticketId}/delete")

	public String deleteTicket(@PathVariable("ticketId") Long ticketId) {
		ticketService.deleteTicket(ticketId);
		return "redirect:/admin/Tickets";
	}

	//handler method to view Ticket request
	@GetMapping("/admin/Tickets/{ticketUrl}/view")
	public String viewPost(@PathVariable("ticketUrl") String ticketUrl, Model model) {
		TicketDto ticketDto = ticketService.findTicketByUrl(ticketUrl);
		model.addAttribute("ticket",ticketDto);
		return "admin/ViewTicket";
	}

	//handler method to handle search tickets
	//localhost:8080/admin/tickets/search?query=tomcat
	@GetMapping("/admin/Tickets/search")
	public String searchTickets(@RequestParam(value="query") String query, Model model) {

		List<TicketDto> tickets = ticketService.searchTickets(query);
		model.addAttribute("tickets", tickets);
		return "admin/Tickets";

	}

}