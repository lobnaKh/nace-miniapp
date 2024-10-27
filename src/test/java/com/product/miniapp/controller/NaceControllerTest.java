package com.product.miniapp.controller;

import com.nace.miniapp.MiniAppApplication;
import com.nace.miniapp.model.Nace;
import com.nace.miniapp.repository.NaceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MiniAppApplication.class)
@Transactional
@AutoConfigureMockMvc
public class NaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NaceRepository naceRepository;

    @BeforeEach
    public void setUp() {
        naceRepository.deleteAll();
    }

    @Test
    public void test_uploadCsv_PersistData_SemiColonDelimiter_OK() throws Exception {
        String csvContent = "Order;Level;Code;Parent;Description;This item includes;This item also includes;Rulings;This item excludes;Reference to ISIC Rev. 4" +
                "\n1;1;test_col3;test_col4;test_col5;test_col6;test_col7;test_col8;test_col9;test_col10" +
                "\n2;2;test_col3;test_col4;test_col5;test_col6;test_col7;test_col8;test_col9;test_col10";
        MockMultipartFile file = new MockMultipartFile("file", "input.csv", MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());
        mockMvc.perform(multipart("/api/csv/upload")
                        .file(file))
                .andExpect(status().isOk());

        // Verify data was persisted
        List<Nace> persitedNaces = naceRepository.findAll();
        assertEquals(2, persitedNaces.size());

        // Verify first entry
        assertEquals(1, persitedNaces.get(0).getOrder());
        assertEquals("test_col3", persitedNaces.get(0).getCode());

        //Verify second entry
        assertEquals(2, persitedNaces.get(1).getOrder());
        assertEquals("test_col4", persitedNaces.get(1).getParent());
    }

    @Test
    public void test_uploadCsv_PersistData_CommaDelimiter_OK() throws Exception {
        String csvContent = "Order,Level,Code,Parent,Description,This item includes,This item also includes,Rulings,This item excludes,Reference to ISIC Rev. 4"+
                "\n1,1,test_col3,test_col4,test_col5,test_col6,test_col7,test_col8,test_col9,est_col10";

        MockMultipartFile file = new MockMultipartFile("file", "input.csv", MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());
        mockMvc.perform(multipart("/api/csv/upload").file(file))
                .andExpect(status().isOk());

        // Verify data was persisted
        List<Nace> persitedNaces = naceRepository.findAll();
        assertEquals(1, persitedNaces.size());
    }

    @Test
    public void test_uploadCsv_EmptyFile_KO() throws Exception {
        String csvContent = "";
        MockMultipartFile file = new MockMultipartFile("file", "input.csv", MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());        mockMvc.perform(multipart("/api/csv/upload").file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("CSV file is empty."));

        // Mock the repository to prevent actual saving
        NaceRepository mockRepository = Mockito.mock(NaceRepository.class);
        verify(mockRepository, never()).save(any());
    }

    @Test
    public void test_uploadCsv_with_duplicates() throws Exception {
        String csvContent = "Order,Level,Code,Parent,Description,This item includes,This item also includes,Rulings,This item excludes,Reference to ISIC Rev. 4"+
                "\n1,1,test_col3,test_col4,test_col5,test_col6,test_col7,test_col8,test_col9,est_col10"+
                "\n1,2,test_col3,test_col4,test_col5,test_col6,test_col7,test_col8,test_col9,est_col10";

        MockMultipartFile file = new MockMultipartFile("file", "input.csv", MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());
        mockMvc.perform(multipart("/api/csv/upload").file(file))
                .andExpect(status().isOk());

        // Verify data was persisted
        List<Nace> persitedNaces = naceRepository.findAll();
        assertEquals(1, persitedNaces.size());    }


    @Test
    void testGeNaceByOrderId_OK() throws Exception {
        String csvContent = "Order;Level;Code;Parent;Description;This item includes;This item also includes;Rulings;This item excludes;Reference to ISIC Rev. 4\n398481;1;A;;AGRICULTURE, FORESTRY AND FISHING;This section includes the exploitation of vegetal and animal natural resources, comprising the activities of growing of crops, raising and breeding of animals, harvesting of timber and other plants, animals or animal products from a farm or their natural habitats.;;;;A 3\n398483;3;1,1;1;Growing of non-perennial crops;This group includes the growing of non-perennial crops, i.e. plants that do not last for more than two growing seasons. Included is the growing of these plants for the purpose of seed production.;;;;11";
        MockMultipartFile file = new MockMultipartFile("file", "input.csv", MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes());
        mockMvc.perform(multipart("/api/csv/upload")
                        .file(file))
                .andExpect(status().isOk());

        // Now, test the GET endpoint for the nace by ORDER
        Optional<Nace> savedNace = naceRepository.findByOrder(398481);

        mockMvc.perform(get("/api/nace/" + savedNace.get().getOrder()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("398481");
                    assertThat(responseBody).contains("AGRICULTURE");
                });
    }


    @Test
    void testGeNaceByOrderId_KO() throws Exception {
        // Attempt to retrieve a nace that doesn't exist
        mockMvc.perform(get("/api/naces/999")) // Assuming 999 does not exist
                .andExpect(status().isNotFound());
    }
}
