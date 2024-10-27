package com.nace.miniapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.nace.miniapp.exception.InvalidFileTypeException;
import com.nace.miniapp.utils.NaceCsvRepresentation;
import com.nace.miniapp.exception.ResourceNotFoundException;
import com.nace.miniapp.utils.UploadResult;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.nace.miniapp.model.Nace;
import com.nace.miniapp.repository.NaceRepository;

import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class NaceServiceImpl implements NaceService {

	private final NaceRepository naceRepository;

	@Override
	public Nace getNaceByOrder(Long order) {
		return naceRepository.findByOrder(order).orElseThrow(() -> new ResourceNotFoundException("Nace not found"));
	}

	@Override
	public List<Nace> getNaces() {
		return naceRepository.findAll();
	}

	@Override
	public UploadResult uploadFile(MultipartFile file) throws IOException {
		validateCsvFile(file);
		List<Nace> naces = this.parseCsvToNaceObjects(file);
		List<Nace> duplicatedNaces = new ArrayList<>();
		List<Nace> uniqueNaces = new ArrayList<>();

		for (Nace nace : naces) {
			//check if nace's order already exists
			if (naceRepository.findByOrder(nace.getOrder()).isPresent()) {
				duplicatedNaces.add(nace);
			} else {
				uniqueNaces.add(nace);
			}
		}

		//save unique naces at once
		naceRepository.saveAll(uniqueNaces);

		return new UploadResult(uniqueNaces.size(), duplicatedNaces.size());
	}

	private void validateCsvFile(MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (filename == null || !(filename.endsWith(".csv") || filename.endsWith(".CSV")) ) { //|| !"text/csv".equals(file.getContentType())
			throw new InvalidFileTypeException("The file must be in csv format!");
		}
	}

	private List<Nace> parseCsvToNaceObjects(MultipartFile file) throws IOException {
		List<Nace> naces = new ArrayList<>();
		char csvSeperator = detectSeparator(file);

		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			//define how to read the csv file (columns titles)
			HeaderColumnNameMappingStrategy<NaceCsvRepresentation> strategy =
					new HeaderColumnNameMappingStrategy<>();
			strategy.setType(NaceCsvRepresentation.class);
			//transform csv file to a bean
			CsvToBean<NaceCsvRepresentation> csvToBean =
					new CsvToBeanBuilder<NaceCsvRepresentation>(reader)
							.withSeparator(csvSeperator)
							.withMappingStrategy(strategy)
							.withIgnoreEmptyLine(true)
							.withIgnoreLeadingWhiteSpace(true)
							.build();

			//loop over the list to collect the list of naces
			List<NaceCsvRepresentation> csvDataList = csvToBean.parse();

			//transform csvdata to nace objects
			naces = csvDataList	.stream()
					.map(csvLine -> Nace.builder()
							.order(csvLine.getOrder())
							.level(csvLine.getLevel())
							.code(csvLine.getCode())
							.parent(csvLine.getParent())
							.description(csvLine.getDescription())
							.including(csvLine.getIncluding())
							.includingMore(csvLine.getIncludingMore())
							.excluding(csvLine.getExcluding())
							.rulings(csvLine.getRulings())
							.reference(csvLine.getReference()).build()
					).toList();
		} catch (IOException e) {
			throw new RuntimeException("Failed to process CSV file", e);
		}

		//remove duplicates based on column Order
        return naces.stream().filter(distinctByKey(Nace::getOrder)).collect(Collectors.toList());
	}


	private char detectSeparator(MultipartFile file) {
		// Read the first line to detect the separator
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String firstLine = reader.readLine();
			if (firstLine != null) {
				// Check for both semicolon and comma
				if (firstLine.contains(";")) {
					return ';';
				} else if (firstLine.contains(",")) {
					return ',';
				}
			}
		} catch (Exception e) {
			throw new InvalidFileTypeException("Errow while checking CSV seperator");
		}
		// Default to semicolon if no separator is found
		return ';';
	}

	/**
	 * Helper method to filter duplicates based on a key extractor
	 * @param keyExtractor
	 * @return
	 * @param <T>
	 */
	private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new HashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
