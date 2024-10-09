package technikal.task.fishmarket.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import technikal.task.fishmarket.dtos.FishDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.services.FishService;

import java.util.List;

@Controller
@RequestMapping("/fish")
public class FishController {

	private FishService fishService;

	@Autowired
	public FishController(FishService fishService) {
		this.fishService = fishService;
	}

	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishlist = fishService.getAllFish();
		model.addAttribute("fishlist", fishlist);
		return "index";
	}

	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}

	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {
		fishService.deleteFish(id);
		return "redirect:/fish";
	}

	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
		if (result.hasErrors()) {
			return "createFish";
		}

		fishService.saveFish(fishDto);
		return "redirect:/fish";
	}
}
