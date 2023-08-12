package com.homeflix.app.views;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class TMDBService {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NzQzYjM2YmU2NWNmZWUzZWE5NzlkZmM5ZTIwZDY4YSIsInN1YiI6IjY0ZDcyNThkZjQ5NWVlMDI5NDJmYWRhMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.x4EMfvuP8Qd8ab9CGx_UmihVOJIfSQyOuDL3kuFB2w8";
    private static final String URL = "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=%s";

    private static final String DATA = """
            {
              "dates": {
                "maximum": "2023-08-17",
                "minimum": "2023-06-30"
              },
              "page": 1,
              "results": [
                {
                  "adult": false,
                  "backdrop_path": "/2vFuG6bWGyQUzYS9d69E5l85nIz.jpg",
                  "genre_ids": [
                    28,
                    12,
                    878
                  ],
                  "id": 667538,
                  "original_language": "en",
                  "original_title": "Transformers: Rise of the Beasts",
                  "overview": "When a new threat capable of destroying the entire planet emerges, Optimus Prime and the Autobots must team up with a powerful faction known as the Maximals. With the fate of humanity hanging in the balance, humans Noah and Elena will do whatever it takes to help the Transformers as they engage in the ultimate battle to save Earth.",
                  "popularity": 2359.007,
                  "poster_path": "/gPbM0MK8CP8A174rmUwGsADNYKD.jpg",
                  "release_date": "2023-06-06",
                  "title": "Transformers: Rise of the Beasts",
                  "video": false,
                  "vote_average": 7.5,
                  "vote_count": 2582
                },
                {
                  "adult": false,
                  "backdrop_path": "/tTfnd2VrlaZJSBD9HUbtSF3CqPJ.jpg",
                  "genre_ids": [
                    35,
                    12,
                    14
                  ],
                  "id": 346698,
                  "original_language": "en",
                  "original_title": "Barbie",
                  "overview": "Barbie and Ken are having the time of their lives in the colorful and seemingly perfect world of Barbie Land. However, when they get a chance to go to the real world, they soon discover the joys and perils of living among humans.",
                  "popularity": 2258.781,
                  "poster_path": "/iuFNMS8U5cb6xfzi51Dbkovj7vM.jpg",
                  "release_date": "2023-07-19",
                  "title": "Barbie",
                  "video": false,
                  "vote_average": 7.5,
                  "vote_count": 2789
                },
                {
                  "adult": false,
                  "backdrop_path": "/yF1eOkaYvwiORauRCPWznV9xVvi.jpg",
                  "genre_ids": [
                    28,
                    12,
                    878
                  ],
                  "id": 298618,
                  "original_language": "en",
                  "original_title": "The Flash",
                  "overview": "When his attempt to save his family inadvertently alters the future, Barry Allen becomes trapped in a reality in which General Zod has returned and there are no Super Heroes to turn to. In order to save the world that he is in and return to the future that he knows, Barry's only hope is to race for his life. But will making the ultimate sacrifice be enough to reset the universe?",
                  "popularity": 1948.386,
                  "poster_path": "/rktDFPbfHfUbArZ6OOOKsXcv0Bm.jpg",
                  "release_date": "2023-06-13",
                  "title": "The Flash",
                  "video": false,
                  "vote_average": 7,
                  "vote_count": 2189
                },
                {
                  "adult": false,
                  "backdrop_path": "/bz66a19bR6BKsbY8gSZCM4etJiK.jpg",
                  "genre_ids": [
                    28,
                    27,
                    53
                  ],
                  "id": 1006462,
                  "original_language": "en",
                  "original_title": "The Flood",
                  "overview": "A horde of giant hungry alligators is unleashed on a group of in-transit prisoners and their guards after a massive hurricane floods Louisiana.",
                  "popularity": 1935.851,
                  "poster_path": "/mvjqqklMpHwOxc40rn7dMhGT0Fc.jpg",
                  "release_date": "2023-07-14",
                  "title": "The Flood",
                  "video": false,
                  "vote_average": 6.8,
                  "vote_count": 73
                },
                {
                  "adult": false,
                  "backdrop_path": "/zN41DPmPhwmgJjHwezALdrdvD0h.jpg",
                  "genre_ids": [
                    28,
                    878,
                    27
                  ],
                  "id": 615656,
                  "original_language": "en",
                  "original_title": "Meg 2: The Trench",
                  "overview": "An exploratory dive into the deepest depths of the ocean of a daring research team spirals into chaos when a malevolent mining operation threatens their mission and forces them into a high-stakes battle for survival.",
                  "popularity": 1863.504,
                  "poster_path": "/4m1Au3YkjqsxF8iwQy0fPYSxE0h.jpg",
                  "release_date": "2023-08-02",
                  "title": "Meg 2: The Trench",
                  "video": false,
                  "vote_average": 7,
                  "vote_count": 351
                },
                {
                  "adult": false,
                  "backdrop_path": "/hPcP1kv6vrkRmQO3YgV1H97FE5Q.jpg",
                  "genre_ids": [
                    27,
                    9648,
                    53
                  ],
                  "id": 614479,
                  "original_language": "en",
                  "original_title": "Insidious: The Red Door",
                  "overview": "To put their demons to rest once and for all, Josh Lambert and a college-aged Dalton Lambert must go deeper into The Further than ever before, facing their family's dark past and a host of new and more horrifying terrors that lurk behind the red door.",
                  "popularity": 1772.231,
                  "poster_path": "/uS1AIL7I1Ycgs8PTfqUeN6jYNsQ.jpg",
                  "release_date": "2023-07-05",
                  "title": "Insidious: The Red Door",
                  "video": false,
                  "vote_average": 6.9,
                  "vote_count": 740
                },
                {
                  "adult": false,
                  "backdrop_path": "/dWvDlTkt9VEGCDww6IzNRgm8fRQ.jpg",
                  "genre_ids": [
                    28,
                    12,
                    53,
                    35
                  ],
                  "id": 457332,
                  "original_language": "en",
                  "original_title": "Hidden Strike",
                  "overview": "Two elite soldiers must escort civilians through a gauntlet of gunfire and explosions.",
                  "popularity": 1428.582,
                  "poster_path": "/zsbolOkw8RhTU4DKOrpf4M7KCmi.jpg",
                  "release_date": "2023-07-06",
                  "title": "Hidden Strike",
                  "video": false,
                  "vote_average": 7.1,
                  "vote_count": 483
                },
                {
                  "adult": false,
                  "backdrop_path": "/7drO1kYgQ0PnnU87sAnBEphYrSM.jpg",
                  "genre_ids": [
                    16,
                    28,
                    27
                  ],
                  "id": 1083862,
                  "original_language": "ja",
                  "original_title": "バイオハザード：デスアイランド",
                  "overview": "In San Francisco, Jill Valentine is dealing with a zombie outbreak and a new T-Virus, Leon Kennedy is on the trail of a kidnapped DARPA scientist, and Claire Redfield is investigating a monstrous fish that is killing whales in the bay. Joined by Chris Redfield and Rebecca Chambers, they discover the trail of clues from their separate cases all converge on the same location, Alcatraz Island, where a new evil has taken residence and awaits their arrival.",
                  "popularity": 1170.775,
                  "poster_path": "/xzAQ28moSPEZxOHJ7WL1mX6hb5H.jpg",
                  "release_date": "2023-06-22",
                  "title": "Resident Evil: Death Island",
                  "video": false,
                  "vote_average": 7.8,
                  "vote_count": 490
                },
                {
                  "adult": false,
                  "backdrop_path": "/iEFuHjqrE059SmflBva1JzDJutE.jpg",
                  "genre_ids": [
                    16,
                    10751,
                    28,
                    14,
                    10749
                  ],
                  "id": 496450,
                  "original_language": "fr",
                  "original_title": "Miraculous - le film",
                  "overview": "A life of an ordinary Parisian teenager Marinette goes superhuman when she becomes Ladybug. Bestowed with magical powers of creation, Ladybug must unite with her opposite, Cat Noir, to save Paris as a new villain unleashes chaos unto the city.",
                  "popularity": 957.065,
                  "poster_path": "/bBON9XO9Ek0DjRwMBnJNCwC96Cd.jpg",
                  "release_date": "2023-07-05",
                  "title": "Miraculous: Ladybug & Cat Noir, The Movie",
                  "video": false,
                  "vote_average": 7.9,
                  "vote_count": 402
                },
                {
                  "adult": false,
                  "backdrop_path": "/aLpQ3G2LRgXYNrQgUlo6AQRo9R6.jpg",
                  "genre_ids": [
                    28,
                    53
                  ],
                  "id": 1143190,
                  "original_language": "en",
                  "original_title": "Fear the Night",
                  "overview": "During a bachelorette party in a secluded California farmhouse, masked intruders launch a brutal attack, forcing eight women to fight for survival. Led by Tess, a troubled military veteran, they unite to defend themselves throughout a harrowing night.",
                  "popularity": 911.817,
                  "poster_path": "/4XLZS2xvdv5rxizzTUVREtRyw95.jpg",
                  "release_date": "2023-07-21",
                  "title": "Fear the Night",
                  "video": false,
                  "vote_average": 6.6,
                  "vote_count": 76
                },
                {
                  "adult": false,
                  "backdrop_path": "/iJ0UZaC7XW7BUpRQ7OLPZSms8Ou.jpg",
                  "genre_ids": [
                    28,
                    12,
                    18,
                    14,
                    878
                  ],
                  "id": 813477,
                  "original_language": "ja",
                  "original_title": "シン・仮面ライダー",
                  "overview": "A man forced to bear power and stripped of humanity. A woman skeptical of happiness. Takeshi Hongo, an Augmentation made by SHOCKER, and Ruriko Midorikawa, a rebel of the organization, escape while fighting off assassins. What’s justice? What’s evil? Will this violence end? Despite his power, Hongo tries to remain human. Along with freedom, Ruriko has regained a heart. What paths will they choose?",
                  "popularity": 873.604,
                  "poster_path": "/9dTO2RygcDT0cQkawABw4QkDegN.jpg",
                  "release_date": "2023-03-17",
                  "title": "Shin Kamen Rider",
                  "video": false,
                  "vote_average": 7.5,
                  "vote_count": 130
                },
                {
                  "adult": false,
                  "backdrop_path": "/f7UI3dYpr7ZUHGo0iIr1Qvy1VPe.jpg",
                  "genre_ids": [
                    16,
                    10751,
                    14,
                    35
                  ],
                  "id": 1040148,
                  "original_language": "en",
                  "original_title": "Ruby Gillman, Teenage Kraken",
                  "overview": "Ruby Gillman, a sweet and awkward high school student, discovers she's a direct descendant of the warrior kraken queens. The kraken are sworn to protect the oceans of the world against the vain, power-hungry mermaids. Destined to inherit the throne from her commanding grandmother, Ruby must use her newfound powers to protect those she loves most.",
                  "popularity": 881.566,
                  "poster_path": "/kgrLpJcLBbyhWIkK7fx1fM4iSvf.jpg",
                  "release_date": "2023-06-28",
                  "title": "Ruby Gillman, Teenage Kraken",
                  "video": false,
                  "vote_average": 7.6,
                  "vote_count": 488
                },
                {
                  "adult": false,
                  "backdrop_path": "/14GEZCzCGhV7FMFaWi4Ec22Kcai.jpg",
                  "genre_ids": [
                    16,
                    12,
                    10751,
                    14
                  ],
                  "id": 459003,
                  "original_language": "uk",
                  "original_title": "Мавка: Лісова пісня",
                  "overview": "Mavka — a Soul of the Forest and its Warden — faces an impossible choice between love and her duty as guardian to the Heart of the Forest, when she falls in love with a human — the talented young musician Lukas.",
                  "popularity": 708.751,
                  "poster_path": "/eeJjd9JU2Mdj9d7nWRFLWlrcExi.jpg",
                  "release_date": "2023-03-02",
                  "title": "Mavka: The Forest Song",
                  "video": false,
                  "vote_average": 7.4,
                  "vote_count": 288
                },
                {
                  "adult": false,
                  "backdrop_path": "/J0XkW5toJLGEucm1pLDvTHXaKC.jpg",
                  "genre_ids": [
                    28,
                    18,
                    10752
                  ],
                  "id": 1076487,
                  "original_language": "en",
                  "original_title": "Warhorse One",
                  "overview": "A gunned down Navy SEAL Master Chief must guide a child to safety through a gauntlet of hostile Taliban insurgents and survive the brutal Afghanistan wilderness.",
                  "popularity": 732.933,
                  "poster_path": "/jP2ik17jvKiV5sGEknMFbZv7WAe.jpg",
                  "release_date": "2023-06-30",
                  "title": "Warhorse One",
                  "video": false,
                  "vote_average": 7.3,
                  "vote_count": 151
                },
                {
                  "adult": false,
                  "backdrop_path": "/fm6KqXpk3M2HVveHwCrBSSBaO0V.jpg",
                  "genre_ids": [
                    18,
                    36
                  ],
                  "id": 872585,
                  "original_language": "en",
                  "original_title": "Oppenheimer",
                  "overview": "The story of J. Robert Oppenheimer’s role in the development of the atomic bomb during World War II.",
                  "popularity": 708.996,
                  "poster_path": "/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
                  "release_date": "2023-07-19",
                  "title": "Oppenheimer",
                  "video": false,
                  "vote_average": 8.3,
                  "vote_count": 1772
                },
                {
                  "adult": false,
                  "backdrop_path": "/r54HQwvisSXMfip7oJNhPSWyCK5.jpg",
                  "genre_ids": [
                    28,
                    12,
                    10752
                  ],
                  "id": 1061181,
                  "original_language": "ja",
                  "original_title": "キングダム 運命の炎",
                  "overview": "It follows Li Xin and Wang Qi as they stand on the battlefield for the first time to fight off an invasion by Zhao, and it also follows Ying Zheng's unknown past.",
                  "popularity": 673.081,
                  "poster_path": "/50WLieQSV6WSPoNjhf0GabbOeey.jpg",
                  "release_date": "2023-07-28",
                  "title": "Kingdom 3: The Flame of Fate",
                  "video": false,
                  "vote_average": 8.4,
                  "vote_count": 30
                },
                {
                  "adult": false,
                  "backdrop_path": "/cSYLX73WskxCgvpN3MtRkYUSj1T.jpg",
                  "genre_ids": [
                    16,
                    35,
                    10751,
                    14,
                    10749
                  ],
                  "id": 976573,
                  "original_language": "en",
                  "original_title": "Elemental",
                  "overview": "In a city where fire, water, land and air residents live together, a fiery young woman and a go-with-the-flow guy will discover something elemental: how much they have in common.",
                  "popularity": 632.033,
                  "poster_path": "/8riWcADI1ekEiBguVB9vkilhiQm.jpg",
                  "release_date": "2023-06-14",
                  "title": "Elemental",
                  "video": false,
                  "vote_average": 7.6,
                  "vote_count": 762
                },
                {
                  "adult": false,
                  "backdrop_path": "/oqP1qEZccq5AD9TVTIaO6IGUj7o.jpg",
                  "genre_ids": [
                    14,
                    28,
                    12
                  ],
                  "id": 455476,
                  "original_language": "en",
                  "original_title": "Knights of the Zodiac",
                  "overview": "When a headstrong street orphan, Seiya, in search of his abducted sister unwittingly taps into hidden powers, he discovers he might be the only person alive who can protect a reincarnated goddess, sent to watch over humanity. Can he let his past go and embrace his destiny to become a Knight of the Zodiac?",
                  "popularity": 595.749,
                  "poster_path": "/qW4crfED8mpNDadSmMdi7ZDzhXF.jpg",
                  "release_date": "2023-04-27",
                  "title": "Knights of the Zodiac",
                  "video": false,
                  "vote_average": 6.7,
                  "vote_count": 719
                },
                {
                  "adult": false,
                  "backdrop_path": "/rRcNmiH55Tz0ugUsDUGmj8Bsa4V.jpg",
                  "genre_ids": [
                    35,
                    10749
                  ],
                  "id": 884605,
                  "original_language": "en",
                  "original_title": "No Hard Feelings",
                  "overview": "On the brink of losing her childhood home, Maddie discovers an intriguing job listing: wealthy helicopter parents looking for someone to “date” their introverted 19-year-old son, Percy, before he leaves for college. To her surprise, Maddie soon discovers the awkward Percy is no sure thing.",
                  "popularity": 554.043,
                  "poster_path": "/4K7gQjD19CDEPd7A9KZwr2D9Nco.jpg",
                  "release_date": "2023-06-15",
                  "title": "No Hard Feelings",
                  "video": false,
                  "vote_average": 6.9,
                  "vote_count": 319
                },
                {
                  "adult": false,
                  "backdrop_path": "/waBWlJlMpyFb7STkFHfFvJKgwww.jpg",
                  "genre_ids": [
                    28,
                    18
                  ],
                  "id": 678512,
                  "original_language": "en",
                  "original_title": "Sound of Freedom",
                  "overview": "The story of Tim Ballard, a former US government agent, who quits his job in order to devote his life to rescuing children from global sex traffickers.",
                  "popularity": 460.32,
                  "poster_path": "/kSf9svfL2WrKeuK8W08xeR5lTn8.jpg",
                  "release_date": "2023-07-03",
                  "title": "Sound of Freedom",
                  "video": false,
                  "vote_average": 8.2,
                  "vote_count": 339
                }
              ],
              "total_pages": 74,
              "total_results": 1461
            }
            """;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Cacheable("movies")
    @SneakyThrows
    public List<Result1> getMovies() {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
                .registerModule(new Jdk8Module());
        return OBJECT_MAPPER
                .readValue(DATA, Response1.class).results();
    }


    public List<Result1> getMoviesByName(String value, String type) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", AUTH_TOKEN);
        var responseEntity = REST_TEMPLATE.exchange("https://api.themoviedb.org/3/search/%s?query=%s&include_adult=false&language=en-US&page=1".formatted(type, value), HttpMethod.GET, new HttpEntity<>(httpHeaders), Response1.class);
        return Objects.requireNonNull(responseEntity.getBody()).results();
    }
}
