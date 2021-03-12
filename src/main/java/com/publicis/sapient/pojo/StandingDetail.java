package com.publicis.sapient.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"overall_league_payed","overall_league_W","overall_league_D","overall_league_L","overall_league_GF","overall_league_GA","overall_league_PTS","home_league_position","home_league_payed","overall_promotion","home_promotion","league_round","team_badge",
"home_league_W","home_league_D","home_league_L","home_league_GF","home_league_GA","home_league_PTS","away_league_position","away_league_payed","away_league_W","away_league_D","away_league_L","away_league_GF","away_league_GA","away_league_PTS","away_promotion"})
public class StandingDetail {
	
	@JsonProperty("country_id")
	private String countryId;
	
	@JsonProperty("country_name")
	private String countryName;
	
	@JsonProperty("league_id")
	private String leagueId;
	
	@JsonProperty("league_name")
	private String leagueName;
	
	@JsonProperty("team_id")
	private String teamId;
	
	@JsonProperty("team_name")
	private String teamName;
	
	@JsonProperty("overall_league_position")
	private String leaguePosition;

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getLeaguePosition() {
		return leaguePosition;
	}

	public void setLeaguePosition(String leaguePosition) {
		this.leaguePosition = leaguePosition;
	}
	
}
