package com.nace.miniapp.service;

import java.io.IOException;
import java.util.List;

import com.nace.miniapp.model.Nace;
import com.nace.miniapp.utils.UploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface NaceService {

	List<Nace> getNaces();

	Nace getNaceByOrder(Long order);

	UploadResult uploadFile(MultipartFile file) throws IOException;

}
