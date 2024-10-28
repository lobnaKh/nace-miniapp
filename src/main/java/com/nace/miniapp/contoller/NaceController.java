package com.nace.miniapp.contoller;

import com.nace.miniapp.model.Nace;
import com.nace.miniapp.service.NaceService;
import com.nace.miniapp.exception.InvalidFileTypeException;
import com.nace.miniapp.exception.ResourceNotFoundException;
import com.nace.miniapp.utils.UploadResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class NaceController {

	private final NaceService naceService;


	@PostMapping(value = "/csv/upload", consumes = {"multipart/form-data"})
	public ResponseEntity<String> putNaceDetails(@RequestPart("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CSV file is empty.");
		}
		try {
			UploadResult result = naceService.uploadFile(file);
			return ResponseEntity.status(HttpStatus.OK).body("File uploaded and data persisted successfully : { duplicated / uploaded } "+ result.getDuplicatedNb() + "/" + result.getUploadedNb());
		} catch (InvalidFileTypeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}


	@GetMapping("/nace/{order}")
	public ResponseEntity<?> getNace(@PathVariable("order") Long order) {
		try {
			Nace nace = this.naceService.getNaceByOrder(order);
			return new ResponseEntity(nace, HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/naces")
	public ResponseEntity<Nace> getNaces() {
		List<Nace> naces = this.naceService.getNaces();
		return new ResponseEntity(naces, HttpStatus.OK);
	}

}
