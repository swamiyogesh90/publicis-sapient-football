package com.publicis.sapient.controller;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.sapient.constants.URLConstants;
import com.publicis.sapient.pojo.CountryDetail;
import com.publicis.sapient.pojo.LeagueDetail;
import com.publicis.sapient.pojo.StandingDetail;

@RestController
public class FootballLeagueController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${football_api_base_url}")
	private String baseUrl;
	
	@Value("${api_key}")
	private String api_key;

	@RequestMapping(value = "/getCountryCode", method = RequestMethod.GET)
	public ResponseEntity<String> getAllCountryCodeByName(@RequestParam final String countryName) throws JsonMappingException, JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
		        .queryParam(URLConstants.ACTION, URLConstants.GET_COUNTRIES)
		        .queryParam(URLConstants.API_KEY, api_key);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
		        builder.toUriString(),
		        HttpMethod.GET,
		        entity,
		        String.class);
		
		ObjectMapper mapper = new ObjectMapper();
		List<CountryDetail> countryList = Arrays.asList(mapper.readValue(response.getBody().toString(), CountryDetail[].class));
		
		Optional<CountryDetail> country = countryList.stream().filter(countryDetails -> countryDetails.getCountryName().equalsIgnoreCase(countryName)).findFirst();
		
		return country.isPresent() ? ResponseEntity.status(HttpStatus.OK).body(country.get().getCountryId()) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Country associated with country name \"" + countryName + "\" Not Found");
	}
	
	@RequestMapping(value = "/getLeagueId", method = RequestMethod.GET)
	public ResponseEntity<String> getLeaguesByCountryId(@RequestParam final String countryId, @RequestParam final String leagueName) throws JsonMappingException, JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
		        .queryParam(URLConstants.ACTION, URLConstants.GET_LEAGUES)
		        .queryParam(URLConstants.COUNTRY_ID, countryId)
		        .queryParam(URLConstants.API_KEY, api_key);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
		        builder.toUriString(),
		        HttpMethod.GET,
		        entity,
		        String.class);
		
		ObjectMapper mapper = new ObjectMapper();
		List<LeagueDetail> leagueList = Arrays.asList(mapper.readValue(response.getBody().toString(), LeagueDetail[].class));
		
		List<LeagueDetail> matchingLeagueResults = leagueList.stream().filter(leagueDetails -> leagueDetails.getCountryId().equalsIgnoreCase(countryId)).collect(Collectors.toList());
		
		Optional<LeagueDetail> leagueResult = matchingLeagueResults.stream().filter(league -> league.getLeagueName().equalsIgnoreCase(leagueName)).findFirst();
		
		return leagueResult.isPresent() ? ResponseEntity.status(HttpStatus.OK).body(leagueResult.get().getLeagueId()) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("League associated with country id \"" + countryId + "\" and league name " + "\"" + leagueName + "\" not found");
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getLeaguePosition", method = RequestMethod.GET)
	public ResponseEntity getLeagePositionOfTeamByLeagueId(@RequestParam final String leagueId, @RequestParam final String teamName) throws JsonMappingException, JsonProcessingException {
		

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
		        .queryParam(URLConstants.ACTION, URLConstants.GET_STANDINGS)
		        .queryParam(URLConstants.LEAGUE_ID, leagueId)
		        .queryParam(URLConstants.API_KEY, api_key);
		
		ResponseEntity<String> response = restClient(builder);
		
		ObjectMapper mapper = new ObjectMapper();
		List<StandingDetail> leagueList = Arrays.asList(mapper.readValue(response.getBody().toString(), StandingDetail[].class));
		
		Optional<StandingDetail> standingResult = leagueList.stream().filter(standingDetails -> standingDetails.getTeamName().equalsIgnoreCase(teamName)).findFirst();
		
		return standingResult.isPresent() ? ResponseEntity.status(HttpStatus.OK).body(standingResult.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("League position associated with lead id \"" + leagueId + "\" and team name " + "\"" + teamName + "\" not found");
	}

	private ResponseEntity<String> restClient(UriComponentsBuilder builder) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
		        builder.toUriString(),
		        HttpMethod.GET,
		        entity,
		        String.class);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getTeamStanding", method = RequestMethod.GET)
	public ResponseEntity getTeamStanding(@RequestParam(required=true) final String countryName, @RequestParam(required=true) final String leagueName, @RequestParam(required=true) final String teamName) throws JsonMappingException, JsonProcessingException {
		
		StandingDetail leaguePosition = null;
		ResponseEntity<String> countryCodeResponse = getAllCountryCodeByName(countryName);
		if(countryCodeResponse.getStatusCode().equals(HttpStatus.OK)) {
			String countryId = countryCodeResponse.getBody();
			ResponseEntity<String> leagueCodeResponse = getLeaguesByCountryId(countryId, leagueName);
			if(leagueCodeResponse.getStatusCode().equals(HttpStatus.OK)) {
				String leagueId = leagueCodeResponse.getBody();
				ResponseEntity<StandingDetail> leaguePositionResponse = getLeagePositionOfTeamByLeagueId(leagueId, teamName);
				if(leaguePositionResponse.getStatusCode().equals(HttpStatus.OK)) {
					leaguePosition = leaguePositionResponse.getBody();
					leaguePosition.setCountryId(countryId);
				}
			}
		}
		
		return leaguePosition != null ? ResponseEntity.status(HttpStatus.OK).body(leaguePosition) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Respective record not found.");
	}

}
