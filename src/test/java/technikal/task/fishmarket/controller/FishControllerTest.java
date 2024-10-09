package technikal.task.fishmarket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import technikal.task.fishmarket.controllers.FishController;
import technikal.task.fishmarket.dtos.FishDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.services.FishService;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(FishController.class)
class FishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FishService fishService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FishController(fishService)).build();
    }

    @Test
    void showFishList_ShouldNotShowAddFishButton_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/fish"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("//button[contains(text(),'Створити')]").exists());
    }

    @Test
    void showFishList_ShouldReturnFishListView() throws Exception {
        when(fishService.getAllFish()).thenReturn(Arrays.asList(new Fish(), new Fish()));

        mockMvc.perform(MockMvcRequestBuilders.get("/fish"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("fishlist"));

        verify(fishService).getAllFish();
    }

    @Test
    void showCreatePage_ShouldReturnCreateFishView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/fish/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createFish"))
                .andExpect(model().attributeExists("fishDto"));
    }

    @Test
    void addFish_ShouldSaveFishAndRedirectToFishList_WhenValid() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageFiles", "fish.png", "image/png", "test image".getBytes());

        mockMvc.perform(multipart("/fish/create")
                        .file(imageFile)
                        .param("name", "Salmon")
                        .param("price", "250.50")
                        .param("imageFiles", imageFile.getOriginalFilename())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fish"));

        verify(fishService).saveFish(Mockito.any(FishDto.class));
    }

    @Test
    void addFish_ShouldReturnCreateFishView_WhenValidationFails() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile("imageFiles", "fish.pdf", "application/pdf", "test pdf content".getBytes());

        mockMvc.perform(multipart("/fish/create")
                        .file(pdfFile)
                        .param("name", "")
                        .param("price", "-1")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("createFish"))
                .andExpect(model().attributeHasFieldErrors("fishDto", "name", "price", "imageFiles"));
    }

    @Test
    void deleteFish_ShouldRedirectToFishList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/fish/delete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fish"));

        verify(fishService).deleteFish(1);
    }
}
