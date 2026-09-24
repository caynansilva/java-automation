package com.caynan.qa.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonResponse(

        Integer id,

        String name,

        @JsonProperty("base_experience")
        Integer baseExperience,

        Integer height,

        @JsonProperty("is_default")
        Boolean isDefault,

        Integer order,

        Integer weight,

        List<PokemonAbility> abilities,

        @JsonProperty("past_abilities")
        List<PokemonAbilityPast> pastAbilities,

        List<NamedApiResource> forms,

        @JsonProperty("game_indices")
        List<VersionGameIndex> gameIndices,

        @JsonProperty("held_items")
        List<PokemonHeldItem> heldItems,

        @JsonProperty("location_area_encounters")
        String locationAreaEncounters,

        List<PokemonMove> moves,

        NamedApiResource species,

        PokemonSprites sprites,

        PokemonCries cries,

        List<PokemonStat> stats,

        @JsonProperty("past_stats")
        List<PokemonStatPast> pastStats,

        List<PokemonType> types,

        @JsonProperty("past_types")
        List<PokemonTypePast> pastTypes
){

    // =========================================================
    // GENERIC POKEAPI RESOURCE
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NamedApiResource(
            String name,
            String url
    ) {
    }

    // =========================================================
    // ABILITIES
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonAbility(

            @JsonProperty("is_hidden")
            Boolean isHidden,

            Integer slot,

            NamedApiResource ability
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonAbilityPast(

            NamedApiResource generation,

            List<PokemonAbility> abilities
    ) {
    }

    // =========================================================
    // GAME INDEX
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VersionGameIndex(

            @JsonProperty("game_index")
            Integer gameIndex,

            NamedApiResource version
    ) {
    }

    // =========================================================
    // HELD ITEMS
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonHeldItem(

            NamedApiResource item,

            @JsonProperty("version_details")
            List<HeldItemVersion> versionDetails
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HeldItemVersion(

            Integer rarity,

            NamedApiResource version
    ) {
    }

    // =========================================================
    // MOVES
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonMove(

            NamedApiResource move,

            @JsonProperty("version_group_details")
            List<MoveVersionDetail> versionGroupDetails
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MoveVersionDetail(

            @JsonProperty("level_learned_at")
            Integer levelLearnedAt,

            @JsonProperty("move_learn_method")
            NamedApiResource moveLearnMethod,

            @JsonProperty("version_group")
            NamedApiResource versionGroup,

            Integer order
    ) {
    }

    // =========================================================
    // SPRITES
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonSprites(

            @JsonProperty("back_default")
            String backDefault,

            @JsonProperty("back_female")
            String backFemale,

            @JsonProperty("back_shiny")
            String backShiny,

            @JsonProperty("back_shiny_female")
            String backShinyFemale,

            @JsonProperty("front_default")
            String frontDefault,

            @JsonProperty("front_female")
            String frontFemale,

            @JsonProperty("front_shiny")
            String frontShiny,

            @JsonProperty("front_shiny_female")
            String frontShinyFemale,

            PokemonOtherSprites other,

            Map<String, Map<String, PokemonGameSprites>> versions
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonOtherSprites(

            @JsonProperty("dream_world")
            PokemonDreamWorldSprites dreamWorld,

            PokemonHomeSprites home,

            @JsonProperty("official-artwork")
            PokemonOfficialArtworkSprites officialArtwork,

            PokemonGameSprites showdown
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonDreamWorldSprites(

            @JsonProperty("front_default")
            String frontDefault,

            @JsonProperty("front_female")
            String frontFemale
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonHomeSprites(

            @JsonProperty("front_default")
            String frontDefault,

            @JsonProperty("front_female")
            String frontFemale,

            @JsonProperty("front_shiny")
            String frontShiny,

            @JsonProperty("front_shiny_female")
            String frontShinyFemale
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonOfficialArtworkSprites(

            @JsonProperty("front_default")
            String frontDefault,

            @JsonProperty("front_shiny")
            String frontShiny
    ) {
    }

    /**
     * Sprite URLs for a game or game animation. Generation and game names
     * are map keys because the API adds entries as new games are supported.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonGameSprites(

            @JsonProperty("back_default")
            String backDefault,

            @JsonProperty("back_female")
            String backFemale,

            @JsonProperty("back_shiny")
            String backShiny,

            @JsonProperty("back_shiny_female")
            String backShinyFemale,

            @JsonProperty("back_gray")
            String backGray,

            @JsonProperty("front_default")
            String frontDefault,

            @JsonProperty("front_female")
            String frontFemale,

            @JsonProperty("front_shiny")
            String frontShiny,

            @JsonProperty("front_shiny_female")
            String frontShinyFemale,

            @JsonProperty("front_gray")
            String frontGray,

            PokemonGameSprites animated
    ) {
    }

    // =========================================================
    // CRIES
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonCries(
            String latest,
            String legacy
    ) {
    }

    // =========================================================
    // STATS
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonStat(

            @JsonProperty("base_stat")
            Integer baseStat,

            Integer effort,

            NamedApiResource stat
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonStatPast(

            NamedApiResource generation,

            List<PokemonStat> stats
    ) {
    }

    // =========================================================
    // TYPES
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonType(

            Integer slot,

            NamedApiResource type
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonTypePast(

            NamedApiResource generation,

            List<PokemonType> types
    ) {
    }
}
