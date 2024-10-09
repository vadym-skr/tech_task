package technikal.task.fishmarket.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.utils.annotation.ValidFiles;

import java.util.List;

public class FishDto {

	@NotEmpty(message = "потрібна назва рибки")
	private String name;
	@Min(0)
	private double price;
	@ValidFiles(maxSize = 100, allowedTypes = {"image/jpeg", "image/png", "image/gif"})
	private List<MultipartFile> imageFiles;

	public FishDto() {
	}

	public FishDto(String name, double price) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	public List<MultipartFile> getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(List<MultipartFile> imageFiles) {
		this.imageFiles = imageFiles;
	}
}
