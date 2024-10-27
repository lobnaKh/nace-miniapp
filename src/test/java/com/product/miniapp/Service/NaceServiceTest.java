package com.product.miniapp.Service;

import com.nace.miniapp.MiniAppApplication;
import com.nace.miniapp.model.Nace;
import com.nace.miniapp.repository.NaceRepository;
import com.nace.miniapp.service.NaceServiceImpl;
import com.nace.miniapp.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NaceServiceTest {

    @InjectMocks
    private NaceServiceImpl naceService;

    @Mock
    private NaceRepository naceRepository;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetNaceByOrder_OK() {
        Long order = 1L;
        Nace nace = new Nace();
        nace.setOrder(order);
        when(naceRepository.findByOrder(1)).thenReturn(Optional.of(nace));

        //call service
        Nace foundNace = naceService.getNaceByOrder(order);

        //Assertions
        assertNotNull(foundNace);
        assertEquals(1L, foundNace.getOrder());
        verify(naceRepository, times(1)).findByOrder(order);
    }

    @Test
    public void testGetNaceByOrder_NotFound_KO() {
        Long order = 1L;
        when(naceRepository.findByOrder(order)).thenReturn(Optional.empty());

        //call service
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            naceService.getNaceByOrder(order);
        });

        //assertEquals("Data not found with id: " + order, exception.getMessage());
        verify(naceRepository, times(1)).findByOrder(order);
    }


    @Test
    public void testGetNaces_OK() {
        Nace nace1 = new Nace();
        nace1.setOrder(1L);
        nace1.setCode("A");

        Nace nace2 = new Nace();
        nace2.setOrder(2L);
        nace2.setCode("B");

        List<Nace> mockList = Arrays.asList(nace1, nace2);
        when(naceRepository.findAll()).thenReturn(mockList);

        //call service method
        List<Nace> result = naceService.getNaces();

        //Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getOrder());
        assertEquals("A", result.get(0).getCode());

        assertEquals(2L, result.get(1).getOrder());
        assertEquals("B", result.get(1).getCode());

        verify(naceRepository, times(1)).findAll();
    }

    @Test
    public void testUploadCsv() throws IOException {
        String csvContent = "Order;Level;Code;Parent;Description;This item includes;This item also includes;Rulings;This item excludes;Reference to ISIC Rev. 4" +
                "\n1;1;test_col3;test_col4;test_col5;test_col6;test_col7;test_col8;test_col9;test_col10" +
                "\n2;2;test_col3;test_col4;test_col5;test_col6;test_col7;test_col8;test_col9;test_col10";
        MockMultipartFile file = new MockMultipartFile("file", "input.csv", "text/csv",
                csvContent.getBytes());

        //call service
        naceService.uploadFile(file);

        verify(naceRepository, times(1)).saveAll(anyList());
    }
}